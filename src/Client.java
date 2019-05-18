import javax.jms.JMSException;
import javax.jms.Message;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

public class Client implements Runnable {
	// GUI
	private JFrame frame;
	private JTabbedPane tabs;
	private ArrayList<Panel> panels = new ArrayList<>();
	private RemoteInt remote;
	private LoginPanel loginPanel;
	private UserProfilePanel userProfilePanel;
	private GamePanel gamePanel;
	private LeaderBoardPanel leaderBoardPanel;
	private LogoutPanel logoutPanel;

	// Account data
	private User user;
	private boolean loginStatus;

	// JMS
	private JMSClient jmsClient;

	public static void main(String[] args) {
		if (args.length == 1) {
			SwingUtilities.invokeLater(new Client(args[0]));
		} else {
			SwingUtilities.invokeLater(new Client("localhost"));
			System.out.println("Server IP address not set. Use localhost.");
		}
	}

	private Client(String host) {
		try {
			Registry registry = LocateRegistry.getRegistry(host);
			remote = (RemoteInt) registry.lookup("Server");
			jmsClient = new JMSClient(host, this);
		} catch (Exception e) {
			System.err.println("Failed accessing RMI: " + e);
			System.exit(0);
		}
		// User has not logged in
		loginStatus = false;
	}

	public void run() {
		frame = new JFrame("JPoker 24-Game");
		tabs = new JTabbedPane();

		// Add panels to tab pane
		initializePanel();

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				try {
					remote.logout(user);
				} catch (Exception e) {
					System.err.println("Failed accessing RMI: " + e);
					e.printStackTrace();
				}
			}
		});

		frame.setContentPane(tabs);
		frame.setSize(new Dimension(600, 300));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// frame.setResizable(false);
		frame.setVisible(true);
	}

	private void initializePanel() {
		loginPanel = new LoginPanel("Login", true, remote, this);
		panels.add(loginPanel);
		userProfilePanel = new UserProfilePanel("User Profile", false, remote, this);
		panels.add(userProfilePanel);
		gamePanel = new GamePanel("Play Game", false, remote, this);
		panels.add(gamePanel);
		leaderBoardPanel = new LeaderBoardPanel("Leader Board", false, remote, this);
		panels.add(leaderBoardPanel);
		logoutPanel = new LogoutPanel("Logout", false, remote, this);
		panels.add(logoutPanel);

		for (int i = 0; i < panels.size(); i++) {
			panels.get(i).setOpaque(false);
			tabs.addTab(panels.get(i).getName(), panels.get(i));
			tabs.setEnabledAt(i, panels.get(i).getStatus());
		}
	}

	public void setLogin(boolean status) {
		loginStatus = status;
		if (status) {
			revertTabs();
			tabs.setSelectedIndex(1);
		} else {
			revertTabs();
			tabs.setSelectedIndex(0);
			gamePanel.resetGame();
		}
	}

	public void setUser(User user) {
		this.user = user;
	}

	public User getUser() {
		return user;
	}

	private void revertTabs() {
		for (int i = 0; i < panels.size(); i++) {
			panels.get(i).setStatus(!panels.get(i).getStatus());
			tabs.setEnabledAt(i, panels.get(i).getStatus());
		}
	}

	public void endGame(JMS_EndGame msg) {
		gamePanel.endGame(msg);
	}

	public boolean getLoginStatus() {
		return loginStatus;
	}

	public JMSClient getJMSClient() {
		return jmsClient;
	}

	public void sendJoinGameMessage() throws JMSException {
		JMS_JoinGame joinGameMsg = new JMS_JoinGame(user);
		Message msg = jmsClient.getJmsHelper().createMessage(joinGameMsg);
		jmsClient.sendMessage(msg);
	}

	public void startGame(JMS_StartGame msg) {
		gamePanel.startGame(msg.getUsers(), msg.getCards());
	}
}