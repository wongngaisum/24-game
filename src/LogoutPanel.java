import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LogoutPanel extends Panel {
    private JButton btnLogout;

    public LogoutPanel(String name, boolean status, RemoteInterface remote, Client client) {
        super(name, status, remote, client);
    }

    public void initializeGUI() {
        btnLogout = new JButton("Logout");

        btnLogout.addActionListener(event -> {
            try {
                JDialog.setDefaultLookAndFeelDecorated(true);
                // Ask before logout
                int response = JOptionPane.showConfirmDialog(null, "Are you sure?", "Confirm",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (response == JOptionPane.YES_OPTION) {
                    Message logoutResult = getRemote().logout(getClient().getUsername(), getClient().getPassword());
                    JOptionPane.showMessageDialog(null, logoutResult.getMessage(), "", JOptionPane.INFORMATION_MESSAGE);
                    if (logoutResult.getStatus())
                        getClient().setLogin(false);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error: " + e, "", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        this.add(btnLogout);
    }
}
