import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.rmi.registry.*;

public class Client implements Runnable {
    // GUI
    private JFrame frame;
    private JTabbedPane tabs;
    private ArrayList<Panel> panels = new ArrayList<>();
    private RemoteInterface remote;
    private LoginPanel loginPanel;
    private UserProfilePanel userProfilePanel;
    private GamePanel gamePanel;
    private LeaderBoardPanel leaderBoardPanel;
    private LogoutPanel logoutPanel;

    // Account data
    private String username;
    private String password;
    private boolean loginStatus;

    public static void main(String[] args) {
        if (args.length == 1)
            SwingUtilities.invokeLater(new Client(args[0]));
        else
            System.err.println("Please enter server IP address");
    }

    private Client(String host) {
        try {
            Registry registry = LocateRegistry.getRegistry(host);
            remote = (RemoteInterface) registry.lookup("Server");
        } catch (Exception e) {
            System.err.println("Failed accessing RMI: " + e);
            System.exit(0);
        }
        // User has not logged in
        loginStatus = false;
    }

    public void run() {
        frame = new JFrame("JPoker 24-Game");
        tabs = new JTabbedPane();

        // Add panels to tab pane
        initializePanel();

        // Send logout request when closing window
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                try {
                    remote.logout(username, password);
                } catch (Exception e) {
                    System.err.println("Failed accessing RMI: " + e);
                }
            }
        });

        frame.setContentPane(tabs);
        frame.setSize(new Dimension(600, 300));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    private void initializePanel() {
        loginPanel = new LoginPanel("Login", true, remote, this);
        panels.add(loginPanel);
        userProfilePanel = new UserProfilePanel("User Profile", false, remote, this);
        panels.add(userProfilePanel);
        gamePanel = new GamePanel("Play Game", false, remote, this);
        panels.add(gamePanel);
        leaderBoardPanel = new LeaderBoardPanel("Leader Board", false, remote, this);
        panels.add(leaderBoardPanel);
        logoutPanel = new LogoutPanel("Logout", false, remote, this);
        panels.add(logoutPanel);

        for (int i = 0; i < panels.size(); i++) {
            panels.get(i).setOpaque(false);
            tabs.addTab(panels.get(i).getName(), panels.get(i));
            tabs.setEnabledAt(i, panels.get(i).getStatus());
        }

        // new Thread(new CheckLoginStatus(tabs, loginPanel, logoutPanel, panels)).start();
    }

    public void setLogin(boolean status) {
        loginStatus = status;
        if (status) {
            revertTabs();
            tabs.setSelectedIndex(1);
        } else {
            revertTabs();
            tabs.setSelectedIndex(0);
        }
    }

    public void setUsernamePassword(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    private void revertTabs() {
        for (int i = 0; i < panels.size(); i++) {
            panels.get(i).setStatus(!panels.get(i).getStatus());
            tabs.setEnabledAt(i, panels.get(i).getStatus());
        }
    }

    public boolean getLoginStatus() {
        return loginStatus;
    }
}
/*
class CheckLoginStatus implements Runnable {
    private JTabbedPane tabs;
    private LoginPanel loginPanel;
    private LogoutPanel logoutPanel;
    private ArrayList<Panel> panels;
    private boolean login;

    public CheckLoginStatus(JTabbedPane tabs, LoginPanel loginPanel, LogoutPanel logoutPanel, ArrayList<Panel> panels) {
        this.tabs = tabs;
        this.loginPanel = loginPanel;
        this.logoutPanel = logoutPanel;
        this.panels = panels;
        login = false;
    }

    public void run() {
        while (true) {
            try {
                if (!login) {
                    if (login = loginPanel.getLoginStatus()) {
                        for (int i = 0; i < panels.size(); i++) {
                            panels.get(i).setStatus(!panels.get(i).getStatus());
                            tabs.setEnabledAt(i, panels.get(i).getStatus());
                        }
                        tabs.setSelectedIndex(1);
                    }
                }
                Thread.sleep(1000);
            } catch (Exception e) {
                System.err.println("Error: " + e);
            }
        }
    }
}
*/