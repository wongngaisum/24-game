import javax.swing.*;
import java.awt.*;

public class UserProfilePanel extends Panel {
    private JLabel lblUsername;
    private JLabel lblNoOfWins;
    private JLabel lblNoOfGames;
    private JLabel lblAvgTimeToWin;
    private JLabel lblRank;

    public UserProfilePanel(String name, boolean status, RemoteInt remote, Client client) {
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
        new UpdateUserDataTimer().start();
    }

    public class UpdateUserDataTimer extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    if (getClient().getLoginStatus()) {
                        User newData = getRemote().retrieveUserData(getClient().getUser());
                        if (newData != null)
                            getClient().setUser(newData);
                        lblUsername.setText(getClient().getUser().getUsername());
                        lblNoOfWins.setText("Number of Wins: " + getClient().getUser().getGamesWon());
                        lblNoOfGames.setText("Number of Games: " + getClient().getUser().getGamesPlayed());
                        lblAvgTimeToWin.setText("Average time to win: " + getClient().getUser().getTimeToWin());
                        lblRank.setText("Rank #" + getClient().getUser().getRank());
                    }
                    Thread.sleep(2000);
                } catch (Exception e) {
                }
            }
        }
    }
}
