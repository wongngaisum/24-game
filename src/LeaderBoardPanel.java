import javax.swing.*;

public class LeaderBoardPanel extends Panel {
    private JTable table;
    private JScrollPane scrollPane;

    public LeaderBoardPanel(String name, boolean status, RemoteInterface remote, Client client) {
        super(name, status, remote, client);
    }

    public void initializeGUI() {
        String[] columnNames = {"Rank", "Player", "Games won", "Games played", "Avg. winning time"};
        // Arbitrary data
        String[][] data = {
                {"1", "Player 4", "20", "35", "10.4s"},
                {"2", "Player 2", "18", "25", "13.2s"},
                {"3", "Player 6", "18", "31", "15.1s"},
                {"4", "Player 8", "16", "30", "12.8s"},
        };

        table = new JTable(data, columnNames);
        table.setBounds(0, 0, this.getWidth(), this.getHeight());
        scrollPane = new JScrollPane(table);
        this.add(scrollPane);
    }

}
