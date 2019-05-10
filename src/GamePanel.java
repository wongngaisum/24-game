import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

public class GamePanel extends Panel {
	private JButton btnStartGame;
	private JLabel lblWaiting;
	private JLabel[] lblCardImages;
	private PlayerInfo[] playerInfos;
	private JTextField txtAnswer;
	private JButton btnSubmit;
	private JLabel lblCalculatedNumber;
	private JLabel lblWinner;
	private JLabel lblWinnerAnswer;
	private JButton btnNextGame;

	private String suits[] = { "C", "D", "H", "S" };

	private ArrayList<Card> cards;

	public GamePanel(String name, boolean status, RemoteInterface remote, Client client) {
		super(name, status, remote, client);
	}

	public void initializeGUI() {
		btnStartGame = new JButton("Start Game");
		btnStartGame.addActionListener(new StartGameListener());
		this.add(btnStartGame);

		lblWaiting = new JLabel("Waiting for players...");
		lblWaiting.setVisible(false);
		this.add(lblWaiting);

		lblCardImages = new JLabel[4];
		playerInfos = new PlayerInfo[3];

		for (int i = 0; i < 4; i++) {
			lblCardImages[i] = new JLabel("");
			lblCardImages[i].setVisible(false);
			this.add(lblCardImages[i]);
		}

		for (int i = 0; i < 3; i++) {
			playerInfos[i] = new PlayerInfo();
			playerInfos[i].setVisible(false);
			this.add(playerInfos[i]);
		}

		lblCardImages[0].setBounds(50, 10, 65, 100);
		lblCardImages[1].setBounds(130, 10, 65, 100);
		lblCardImages[2].setBounds(210, 10, 65, 100);
		lblCardImages[3].setBounds(290, 10, 65, 100);

		playerInfos[0].setBounds(380, 10, 150, 60);
		playerInfos[1].setBounds(380, 80, 150, 60);
		playerInfos[2].setBounds(380, 150, 150, 60);

		txtAnswer = new JTextField(30);
		txtAnswer.getDocument().addDocumentListener(new AnswerListener());
		txtAnswer.setBounds(50, 180, 200, 20);
		txtAnswer.setVisible(false);
		this.add(txtAnswer);

		btnSubmit = new JButton("Submit");
		btnSubmit.addActionListener(new SubmitAnswerListener());
		btnSubmit.setBounds(260, 180, 100, 20);
		btnSubmit.setVisible(false);
		this.add(btnSubmit);
		
		lblCalculatedNumber = new JLabel("");
		lblCalculatedNumber.setBounds(50, 140, 300, 40);
		lblCalculatedNumber.setFont(new Font("Dialog", Font.BOLD, 20));
		lblCalculatedNumber.setForeground(Color.RED);
		lblCalculatedNumber.setVisible(false);
		this.add(lblCalculatedNumber);
		
		this.setLayout(new GridBagLayout());
	}

	public class StartGameListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent event) {
			try {
				btnStartGame.setVisible(false);
				lblWaiting.setVisible(true);
				getClient().sendJoinGameMessage();
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, "Error: " + e, "", JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}

	public void startGame(ArrayList<User> players, ArrayList<Card> cards) {
		try {
			this.cards = cards;

			this.setLayout(null);
			btnStartGame.setVisible(false);
			lblWaiting.setVisible(false);
			txtAnswer.setVisible(true);
			btnSubmit.setVisible(true);
			lblCalculatedNumber.setVisible(true);

			for (int i = 0; i < 4; i++) {
				try {
					ImageIcon imageIcon = new ImageIcon(getCardPath(cards.get(i).getRank(), cards.get(i).getSuit()));
					Image cardImage = imageIcon.getImage();
					cardImage = cardImage.getScaledInstance(65, 100, java.awt.Image.SCALE_AREA_AVERAGING);
					lblCardImages[i].setIcon(new ImageIcon(cardImage));
					lblCardImages[i].setVisible(true);
				} catch (Exception e) {
					System.err.println("Exception thrown: " + e);
				}
			}

			int j = 0;
			for (int i = 0; i < 3; i++) {
				if (i < players.size() - 1) { // Don't display current user
					if (players.get(i).equals(getClient().getUser()))
						j++;
					playerInfos[i].setPlayer(players.get(j));
					playerInfos[i].setVisible(true);
					j++;
				} else {
					playerInfos[i].setVisible(false);
				}
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Error: " + e, "", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	public void resetGame() {
		this.removeAll();
		this.revalidate();
		this.repaint();
		initializeGUI();
	}
	
	public void endGame(JMS_EndGame msg) {	
		this.removeAll();
		this.revalidate();
		this.repaint();
		
        this.setLayout(null);

		lblWinner = new JLabel("Winner: " + msg.getWinner().getUsername());
		lblWinner.setBounds(50, 50, 100, 50);
		lblWinner.setVisible(true);
		this.add(lblWinner);
		
		lblWinnerAnswer = new JLabel("Answer: " + msg.getAnswer());
		lblWinnerAnswer.setFont(new Font("Dialog", Font.BOLD, 20));
		lblWinnerAnswer.setBounds(50, 110, 300, 50);
		lblWinnerAnswer.setVisible(true);
		this.add(lblWinnerAnswer);
		
		btnNextGame = new JButton("Next Game");
		btnNextGame.setBounds(230, 200, 100, 25);
		btnNextGame.addActionListener(new NextGameListener());
		btnNextGame.setVisible(true);
		this.add(btnNextGame);
	}

	private URL getCardPath(int rank, int suit) throws URISyntaxException, MalformedURLException {
		String path = "/res/cards/" + rank + suits[suit - 1] + ".jpg";
		return getClass().getResource(path).toURI().toURL();
	}

	public class PlayerInfo extends JPanel {
		private JLabel lblUsername;
		private JLabel lblUserRecord;

		public PlayerInfo() {
			lblUsername = new JLabel("");
			lblUsername.setFont(new Font("Dialog", Font.BOLD, 20));
			lblUsername.setBounds(0, 0, 150, 20);
			this.add(lblUsername);

			lblUserRecord = new JLabel("");
			lblUserRecord.setBounds(0, 40, 150, 20);
			this.add(lblUserRecord);

			this.setBorder(BorderFactory.createLineBorder(Color.black));
		}

		public void setPlayer(User player) {
			lblUsername.setText(player.getUsername());
			lblUserRecord.setText("Win: " + player.getGamesWon() + "/" + player.getGamesPlayed() + " Avg: "
					+ player.getTimeToWin() + "s");
		}
	}

	private boolean checkAnswerSyntax(String answer) {
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

	private boolean checkAnswer() {
		try {
			if (!txtAnswer.getText().isEmpty()) {
				if (checkAnswerSyntax(txtAnswer.getText())) {
					String txt = txtAnswer.getText();
					txt = txt.replace("A", "1");
					txt = txt.replace("J", "11");
					txt = txt.replace("Q", "12");
					txt = txt.replace("K", "13");
					int result =  Integer.parseInt(new Calculator().calculate(txt));
					lblCalculatedNumber.setText("= " + result);
					return result == 24;
				} else {
					lblCalculatedNumber.setText("Used other numbers");
					return false;
				}
			} else {
				lblCalculatedNumber.setText("");
				return false;
			}
		} catch (Exception e) {
			lblCalculatedNumber.setText("Syntax Error");
			return false;
		}
	}
	
	public class AnswerListener implements DocumentListener {
		public void changedUpdate(DocumentEvent event) {
			checkAnswer();
		}

		public void removeUpdate(DocumentEvent e) {
			checkAnswer();
		}

		public void insertUpdate(DocumentEvent e) {
			checkAnswer();
		}
	}
	
	public class SubmitAnswerListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent event) {
			if (checkAnswer()) {
	            try {
	                getRemote().submitAnswer(getClient().getUser(), cards, txtAnswer.getText());
	                JOptionPane.showMessageDialog(null, "Submitted answer", "", JOptionPane.INFORMATION_MESSAGE);
	            } catch (Exception e) {
	                JOptionPane.showMessageDialog(null, "Error: " + e, "", JOptionPane.INFORMATION_MESSAGE);
	                e.printStackTrace();
	            }
			} else {
				JOptionPane.showMessageDialog(null, "Answer does not equal to 24" , "", JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}

	public class NextGameListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent event) {
			resetGame();
		}
	}
}
