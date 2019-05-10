import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class LeaderBoardPanel extends Panel {
    private JTable table;
    private JScrollPane scrollPane;
    private DefaultTableModel model;

    public LeaderBoardPanel(String name, boolean status, RemoteInterface remote, Client client) {
        super(name, status, remote, client);
    }

    public void initializeGUI() {
        String[] columnNames = {"Rank", "Player", "Games won", "Games played", "Avg. winning time"};
        String[][] data = {
                {"NA", "NA", "NA", "NA", "NA"},
        };

        model = new DefaultTableModel(data, columnNames);
        table = new JTable(model);
        table.setPreferredScrollableViewportSize(new Dimension(450, 200));
        table.setFillsViewportHeight(true);
        scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.add(scrollPane);

        new UpdateLeaderBoardTimer().start();
    }

    public class UpdateLeaderBoardTimer extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    if (getClient().getLoginStatus()) {
                        ArrayList<User> users = getRemote().getLeaderBoardData();
                        model.setRowCount(0);
                        String[] data = new String[5];
                        for (int i = 0; i < users.size(); i++) {
                            User user = users.get(i);
                            data[0] = String.valueOf(user.getRank());
                            data[1] = user.getUsername();
                            data[2] = String.valueOf(user.getGamesWon());
                            data[3] = String.valueOf(user.getGamesPlayed());
                            data[4] = String.valueOf(user.getTimeToWin());
                            model.addRow(data);
                        }
                    }
                    Thread.sleep(2000);
                } catch (Exception e) {
                }
            }
        }
    }
}
