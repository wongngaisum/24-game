import javax.swing.*;
import java.awt.*;

public class UserProfilePanel extends Panel {
    private JLabel lblUsername;
    private JLabel lblNoOfWins;
    private JLabel lblNoOfGames;
    private JLabel lblAvgTimeToWin;
    private JLabel lblRank;

    public UserProfilePanel(String name, boolean status, RemoteInterface remote, Client client) {
        super(name, status, remote, client);
    }

    public void initializeGUI() {
        this.setLayout(null);

        lblUsername = new JLabel("Username");
        lblUsername.setBounds(50, 25, 200, 25);
        lblUsername.setFont(new Font("Dialog", Font.BOLD, 20));
        this.add(lblUsername);

        lblNoOfWins = new JLabel("Number of Wins: 0");
        lblNoOfWins.setBounds(50, 65, 200, 25);
        this.add(lblNoOfWins);

        lblNoOfGames = new JLabel("Number of Games: 0");
        lblNoOfGames.setBounds(50, 95, 200, 25);
        this.add(lblNoOfGames);

        lblAvgTimeToWin = new JLabel("Average time to win: null");
        lblAvgTimeToWin.setBounds(50, 125, 200, 25);
        this.add(lblAvgTimeToWin);

        lblRank = new JLabel("Rank #0");
        lblRank.setBounds(50, 165, 200, 25);
        lblRank.setFont(new Font("Dialog", Font.PLAIN, 16));
        this.add(lblRank);

        // Update data periodically
        new Thread(() -> {
            while (true) {
                try {
                    if (getClient().getLoginStatus()) {
                        lblUsername.setText(getClient().getUsername());
                        lblNoOfWins.setText("Number of Wins: 0");
                        lblNoOfGames.setText("Number of Games: 0");
                        lblAvgTimeToWin.setText("Average time to win: null");
                        lblRank.setText("Rank #0");
                    }
                    Thread.sleep(1000);
                } catch (Exception e) {
                }
            }
        }).start();
    }
}
