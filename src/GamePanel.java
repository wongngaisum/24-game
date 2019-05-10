import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
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
	
	private String suits[] = {"C", "D", "H", "S"};
	
	private ArrayList<User> roomPlayers;
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
		
		lblCardImages[0].setBounds(50, 10, 65, 100); ;
		lblCardImages[1].setBounds(130, 10, 65, 100);
		lblCardImages[2].setBounds(210, 10, 65, 100);
		lblCardImages[3].setBounds(290, 10, 65, 100);
		
		playerInfos[0].setBounds(380, 10, 150, 60); ;
		playerInfos[1].setBounds(380, 80, 150, 60);
		playerInfos[2].setBounds(380, 150, 150, 60);

		txtAnswer = new JTextField();
		txtAnswer.setBounds(50, 180, 200, 20);
		txtAnswer.setVisible(false);
		this.add(txtAnswer);
		
		btnSubmit = new JButton("Submit");
		btnSubmit.setBounds(260, 180, 100, 20);
		btnSubmit.setVisible(false);
		this.add(btnSubmit);
		
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
			this.setLayout(null);
			btnStartGame.setVisible(false);
			lblWaiting.setVisible(false);
			txtAnswer.setVisible(true);
			btnSubmit.setVisible(true);

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
				if (i < players.size() - 1) {	// Don't display current user
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
		System.out.println("reset");
	}

	public URL getCardPath(int rank, int suit) throws URISyntaxException, MalformedURLException {
		String path = "/res/cards/" + rank + suits[suit - 1] + ".jpg";
		return getClass().getResource(path).toURI().toURL();
	}
	
	public class PlayerInfo extends JPanel {
		private JLabel lblUsername;
		private JLabel lblUserRecord;
		
		public PlayerInfo() {
			lblUsername = new JLabel("");
			lblUsername.setFont(new Font("Dialog", Font.BOLD, 20));
			lblUsername.setBounds(0, 0, 120, 20);
			this.add(lblUsername);
			
			lblUserRecord = new JLabel("");
			lblUserRecord.setBounds(0, 20, 120, 20);
			this.add(lblUserRecord);

			this.setBorder(BorderFactory.createLineBorder(Color.black));
		}
		
		public void setPlayer(User player) {
			lblUsername.setText(player.getUsername());
			lblUserRecord.setText("Win: " + player.getGamesWon() + "/" + player.getGamesPlayed()
					+ " Avg: " + player.getTimeToWin() + "s");
		}
	}
}
