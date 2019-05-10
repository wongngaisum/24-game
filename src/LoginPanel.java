import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginPanel extends Panel {
    private JLabel lblUser;
    private JTextField txtUser;
    private JLabel lblPassword;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    // private boolean loginSuccess;

    public LoginPanel(String name, boolean status, RemoteInterface remote, Client client) {
        super(name, status, remote, client);
    }

    public void initializeGUI() {
        this.setLayout(null);

        lblUser = new JLabel("Username");
        lblUser.setBounds(160, 50, 80, 25);
        this.add(lblUser);

        txtUser = new JTextField(20);
        txtUser.setBounds(280, 50, 160, 25);
        this.add(txtUser);

        lblPassword = new JLabel("Password");
        lblPassword.setBounds(160, 80, 80, 25);
        this.add(lblPassword);

        txtPassword = new JPasswordField(20);
        txtPassword.setBounds(280, 80, 160, 25);
        this.add(txtPassword);

        btnLogin = new JButton("Login");
        btnLogin.setBounds(180, 130, 80, 25);
        btnLogin.addActionListener(new LoginListener());
        this.add(btnLogin);

        JButton registerButton = new JButton("Register");
        registerButton.setBounds(340, 130, 80, 25);
        registerButton.addActionListener(new RegisterListener());

        this.add(registerButton);
    }

    public class LoginListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            // Empty field
            if (txtUser.getText().equals("") || txtPassword.getText().equals("")) {
                JOptionPane.showMessageDialog(null, "Please enter username and password", "", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            try {
                User user = new User(txtUser.getText(), txtPassword.getText());
                RMIMessage loginResult = getRemote().login(user);
                JOptionPane.showMessageDialog(null, loginResult.getMessage(), "", JOptionPane.INFORMATION_MESSAGE);
                if (loginResult.getStatus()) {  // Success
                    getClient().setLogin(true);
                    getClient().setUser(user);
                    getClient().getJMSClient().initialize(user);
                    // Clear text boxes
                    txtUser.setText("");
                    txtPassword.setText("");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error: " + e, "", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    public class RegisterListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            // Empty field
            if (txtUser.getText().equals("") || txtPassword.getText().equals("")) {
                JOptionPane.showMessageDialog(null, "Please enter username and password", "", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            try {
                User user = new User(txtUser.getText(), txtPassword.getText());
                RMIMessage registerResult = getRemote().register(user);
                JOptionPane.showMessageDialog(null, registerResult.getMessage(), "", JOptionPane.INFORMATION_MESSAGE);
                if (registerResult.getStatus()) {  // Success
                    getClient().setLogin(true);
                    getClient().setUser(user);
                    getClient().getJMSClient().initialize(user);
                    // Clear text boxes
                    txtUser.setText("");
                    txtPassword.setText("");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error: " + e, "", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
}