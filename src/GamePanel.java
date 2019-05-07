import java.awt.*;

import javax.swing.*;

public class GamePanel extends Panel {
    private JLabel lblComingSoon;

    public GamePanel(String name, boolean status, RemoteInterface remote, Client client) {
        super(name, status, remote, client);
    }

    public void initializeGUI() {
        lblComingSoon = new JLabel("Coming Soon");
        this.setLayout(new GridBagLayout());
        this.add(lblComingSoon);
    }

}
