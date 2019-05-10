import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.naming.NamingException;

public class Server extends UnicastRemoteObject implements RemoteInterface {
	private DBManager db;
	private JMSServer jmsServer;

	private ArrayList<User> waitingList;
	private ArrayList<GameRoom> gameRooms;
	private HashMap<User, WaitingRoomTimer> waitingRoomTimers;

	private boolean timeout; // Can start game with < 4 players if timeout, otherwise 4

	public Server() throws RemoteException, SQLException, InstantiationException, IllegalAccessException,
			ClassNotFoundException, NamingException, JMSException {
		db = new DBManager();
		jmsServer = new JMSServer(this);
		new MessageReceiver().start();

		waitingList = new ArrayList<>();
		gameRooms = new ArrayList<>();
		waitingRoomTimers = new HashMap<>();
		timeout = false;
	}

	public static void main(String[] args) {
		try {
			// Security policy
			// System.setProperty("java.security.policy", "file:./security.policy");
			// System.setSecurityManager(new SecurityManager());

			// RMI
			Server app = new Server();
			Naming.rebind("Server", app);

			System.out.println("Service registered");
		} catch (Exception e) {
			System.err.println("Exception thrown: " + e);
		}
	}

	@Override
	public RMIMessage login(User user) throws RemoteException {
		try {
			String result = db.checkACPW(user.getUsername(), user.getPassword());
			if (result.equals("correct")) {
				boolean isOnline = db.checkOnline(user.getUsername());
				if (isOnline) {
					return new RMIMessage("User has already logged in", false);
				} else {
					db.setOnline(user.getUsername());
					System.out.println("User " + user.getUsername()  + " logged in");
					return new RMIMessage("Logged in successfully", true);
				}
			} else if (result.equals("incorrect")) {
				return new RMIMessage("Incorrect password", false);
			} else {
				return new RMIMessage("User does not exist", false);
			}
		} catch (SQLException e) {
			System.err.println("Exception thrown: " + e);
			return new RMIMessage("SQL error, please try again later", false);
		}
	}

	@Override
	public RMIMessage register(User user) throws RemoteException {
		try {
			if (db.checkDuplicateUser(user.getUsername())) {
				return new RMIMessage("Username has been used", false);
			} else {
				db.register(user.getUsername(), user.getPassword());
				db.setOnline(user.getUsername());
				System.out.println("User " + user.getUsername()  + " registered");
				return new RMIMessage("Registered successfully", true);
			}
		} catch (SQLException e) {
			System.err.println("Exception thrown: " + e);
			return new RMIMessage("SQL error, please try again later", false);
		}
	}

	@Override
	public RMIMessage logout(User user) throws RemoteException {
		try {
			db.logout(user.getUsername());
			for (GameRoom room : gameRooms) {
				if (room.havePlayer(user)) {
					room.removePlayer(user);
					System.out.println("Removed player " + user.getUsername() + " from room");
					
					if (room.isEmpty()) {
						gameRooms.remove(room);
						System.out.println("Removed a room");
						break;
					}
				}
			}
			System.out.println("User " + user.getUsername()  + " logged out");
			return new RMIMessage("Logged out successfully", true);
		} catch (SQLException e) {
			System.err.println("Exception thrown: " + e);
			return new RMIMessage("Failed to logout", false);
		}
	}

	@Override
	public User retrieveUserData(User user) throws RemoteException {
		try {
			return db.retrieveUserData(user);
		} catch (SQLException e) {
			System.err.println("Exception thrown: " + e);
			return null;
		}
	}

	@Override
	public ArrayList<User> getLeaderBoardData() throws RemoteException {
		try {
			return db.getLeaderBoardData();
		} catch (SQLException e) {
			System.err.println("Exception thrown: " + e);
			return null;
		}
	}

	public void addUserToList(JMS_JoinGame msg) {
		waitingList.add(msg.getUser());
		WaitingRoomTimer timer = new WaitingRoomTimer();
		waitingRoomTimers.put(msg.getUser(), timer);
		timer.start();
		System.out.println("Added user " + msg.getUser().getUsername() + " to list");
	}

	public class WaitingRoomTimer extends Thread {
		@Override
		public void run() {
			try {
				if (waitingList.size() == 4) {
					startGame();
				} else if (timeout && waitingList.size() > 1) {
					startGame();
				} else {
					Thread.sleep(10000);
					if (waitingList.size() == 1) {
						timeout = true;
					} else {
						startGame();
					}
				}
			} catch (Exception e) {
				System.err.println("Exception thrown: " + e);
				Thread.currentThread().interrupt();
			}
		}
	}

	public void startGame() throws JMSException {
		int noOfPlayers = waitingList.size() > 4 ? 4 : waitingList.size();
		ArrayList<User> players = new ArrayList<>();
		
		for (int i = 0; i < noOfPlayers; i++) {
			players.add(waitingList.get(0));
			waitingRoomTimers.get(waitingList.get(0)).interrupt();
			waitingRoomTimers.remove(waitingList.get(0));
			waitingList.remove(0);
		}
		
		JMS_StartGame startGameMsg = new JMS_StartGame(players, drawCards());
		Message msg = jmsServer.getJmsHelper().createMessage(startGameMsg);
		jmsServer.broadcastMessage(jmsServer.getTopicSender(), msg);
		
		gameRooms.add(new GameRoom(players));
		
		timeout = false;
		
		System.out.println("Game started");
	}

	// room management thread

	public ArrayList<Card> drawCards() {
    	ArrayList<Card> cards = new ArrayList<>();
    	while (cards.size() < 4) {
    		int rank = (int) (Math.random() * 13 + 1);
    		int suit = (int) (Math.random() * 4 + 1);
    		Card card = new Card(rank, suit);
    		if (!cards.contains(card)) {
    			cards.add(card);
    		}
    	}
    	return cards;
    }

	public class MessageReceiver extends Thread {
		@Override
		public void run() {
			while (true) {
				try {
					jmsServer.receiveMessage();
				} catch (Exception e) {
					System.err.println("Exception thrown: " + e);
				}
			}
		}
	}

	@Override
	public void submitAnswer(User user, ArrayList<Card> cards, String answer) throws RemoteException {
		System.out.println(user.getUsername() + " submitted answer");
		if (checkAnswer(answer, cards)) {
			System.out.println("Answer is correct");
			for (int i = 0; i < gameRooms.size(); i++) {
				try {
					if (gameRooms.get(i).havePlayer(user)) {
						gameRooms.get(i).endGame();
						db.updateWinnerInfo(gameRooms.get(i), user);
	
						JMS_EndGame endGameMsg = new JMS_EndGame(user, gameRooms.get(i), answer);
						Message msg = jmsServer.getJmsHelper().createMessage(endGameMsg);
						jmsServer.broadcastMessage(jmsServer.getTopicSender(), msg);
	
						gameRooms.remove(i);
						break;
					}
				} catch (JMSException | SQLException e) {
					System.err.println("Exception thrown: " + e);
				}
			}
		}
	}

	private boolean checkAnswerSyntax(String answer, ArrayList<Card> cards) {
		String ans = answer.replace("+", "");
		ans = ans.replace("-", "");
		ans = ans.replace("*", "");
		ans = ans.replace("/", "");
		ans = ans.replace("(", "");
		ans = ans.replace(")", "");

		for (Card card : cards) {
			if (card.getRank() > 1 && card.getRank() <= 10) {
				ans = ans.replaceFirst(String.valueOf(card.getRank()), "");
			} else if (card.getRank() == 1) {
				ans = ans.replaceFirst("A", "");
			} else if (card.getRank() == 11) {
				ans = ans.replaceFirst("J", "");
			} else if (card.getRank() == 12) {
				ans = ans.replaceFirst("Q", "");
			} else if (card.getRank() == 13) {
				ans = ans.replaceFirst("K", "");
			}
		}

		if (ans.length() > 0) {
			return false;
		}
		return true;
	}

	private boolean checkAnswer(String answer, ArrayList<Card> cards) {
		try {
			if (!answer.isEmpty()) {
				if (checkAnswerSyntax(answer, cards)) {
					String txt = answer;
					txt = txt.replace("A", "1");
					txt = txt.replace("J", "11");
					txt = txt.replace("Q", "12");
					txt = txt.replace("K", "13");
					float result =  Float.parseFloat(new Calculator().calculate(txt));
					return result == 24.0;
				} else {
					return false;
				}
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}
}
