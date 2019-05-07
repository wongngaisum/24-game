import javax.swing.*;
import java.awt.event.*;

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
        btnLogin.addActionListener(event -> {
            // Empty field
            if (txtUser.getText().equals("") || txtPassword.getText().equals("")) {
                JOptionPane.showMessageDialog(null, "Please enter username and password", "", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            try {
                RMIMessage loginResult = getRemote().login(txtUser.getText(), txtPassword.getText());
                JOptionPane.showMessageDialog(null, loginResult.getMessage(), "", JOptionPane.INFORMATION_MESSAGE);
                if (loginResult.getStatus()) {  // Success
                    getClient().setLogin(true);
                    getClient().setUsernamePassword(txtUser.getText(), txtPassword.getText());
                    // Clear text boxes
                    txtUser.setText("");
                    txtPassword.setText("");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error: " + e, "", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        this.add(btnLogin);

        JButton registerButton = new JButton("Register");
        registerButton.setBounds(340, 130, 80, 25);
        registerButton.addActionListener(event -> {
            // Empty field
            if (txtUser.getText().equals("") || txtPassword.getText().equals("")) {
                JOptionPane.showMessageDialog(null, "Please enter username and password", "", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            try {
                RMIMessage registerResult = getRemote().register(txtUser.getText(), txtPassword.getText());
                JOptionPane.showMessageDialog(null, registerResult.getMessage(), "", JOptionPane.INFORMATION_MESSAGE);
                if (registerResult.getStatus()) {  // Success
                    getClient().setLogin(true);
                    getClient().setUsernamePassword(txtUser.getText(), txtPassword.getText());
                    // Clear text boxes
                    txtUser.setText("");
                    txtPassword.setText("");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error: " + e, "", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        this.add(registerButton);
    }
/*
    public boolean getLoginStatus() {
        return loginSuccess;
    }
*/
}