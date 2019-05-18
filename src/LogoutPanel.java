import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LogoutPanel extends Panel {
    private JButton btnLogout;

    public LogoutPanel(String name, boolean status, RemoteInt remote, Client client) {
        super(name, status, remote, client);
    }

    public void initializeGUI() {
        btnLogout = new JButton("Logout");
        btnLogout.addActionListener(new LogoutListener());
        this.add(btnLogout);
        this.setLayout(new GridBagLayout());
    }

    public class LogoutListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            try {
                JDialog.setDefaultLookAndFeelDecorated(true);
                // Ask before logout
                int response = JOptionPane.showConfirmDialog(null, "Are you sure?", "Confirm",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (response == JOptionPane.YES_OPTION) {
                    RMIMessage logoutResult = getRemote().logout(getClient().getUser());
                    JOptionPane.showMessageDialog(null, logoutResult.getMessage(), "", JOptionPane.INFORMATION_MESSAGE);
                    if (logoutResult.getStatus())
                        getClient().setLogin(false);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error: " + e, "", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
}
