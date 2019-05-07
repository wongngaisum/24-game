import java.io.*;
import java.rmi.*;
import java.rmi.server.*;
import java.util.*;

public class Server extends UnicastRemoteObject implements RemoteInterface {
    private BufferedReader userInfoReader;
    private PrintWriter userInfoWriter;
    private BufferedReader onlineUserReader;
    private PrintWriter onlineUserWriter;

    private File userInfoFile = new File("UserInfo.txt");
    private File onlineUserFile = new File("OnlineUser.txt");

    private ArrayList<String> usernameList = new ArrayList<>();
    private ArrayList<String> passwordList = new ArrayList<>();

    private Server() throws RemoteException {
        readUserInfo();
    }

    public static void main(String[] args) {
        try {
            System.setProperty("java.security.policy", "file:./security.policy");
            System.setSecurityManager(new SecurityManager());
            Server app = new Server();
            Naming.rebind("Server", app);
            System.out.println("Service registered");
        } catch (Exception e) {
            System.err.println("Exception thrown: " + e);
        }
    }

    private void readUserInfo() {
        try {
            userInfoReader = new BufferedReader(new FileReader(userInfoFile));

            String username, password;
            while ((username = userInfoReader.readLine()) != null) {
                password = userInfoReader.readLine();
                usernameList.add(username);
                passwordList.add(password);
            }

            userInfoReader.close();

            onlineUserWriter = new PrintWriter(new BufferedWriter(new FileWriter(onlineUserFile)));
            onlineUserWriter.close();
        } catch (Exception e) {
            System.err.println("Exception thrown: " + e);
            System.exit(0);
        }
    }

    private boolean isOnline(String username) {
        try {
            onlineUserReader = new BufferedReader(new FileReader(onlineUserFile));

            String onlineUser;
            while ((onlineUser = onlineUserReader.readLine()) != null) {
                if (onlineUser.equals(username)) {
                    onlineUserReader.close();
                    return true;
                }
            }

            onlineUserReader.close();
            return false;
        } catch (Exception e) {
            System.err.println("Exception thrown: " + e);
            return false;
        }
    }

    private void setOnline(String username) {
        try {
            onlineUserWriter = new PrintWriter(new BufferedWriter(new FileWriter(onlineUserFile, true)));
            onlineUserWriter.println(username);
            onlineUserWriter.flush();
            onlineUserWriter.close();
        } catch (Exception e) {
            System.err.println("Exception thrown: " + e);
        }
    }

    private void setOffline(String username) {
        try {
            onlineUserReader = new BufferedReader(new FileReader(onlineUserFile));

            StringBuffer sb = new StringBuffer("");
            String line;

            while ((line = onlineUserReader.readLine()) != null) {
                if (!line.equals(username))
                    sb.append(line + "\n");
            }

            onlineUserReader.close();

            onlineUserWriter = new PrintWriter(new BufferedWriter(new FileWriter(onlineUserFile)));
            onlineUserWriter.write(sb.toString());
            onlineUserWriter.flush();
            onlineUserWriter.close();
        } catch (Exception e) {
            System.err.println("Exception thrown: " + e);
        }
    }

    private void addNewUser(String username, String password) {
        try {
            userInfoWriter = new PrintWriter(new BufferedWriter(new FileWriter(userInfoFile, true)));
            userInfoWriter.println(username);
            userInfoWriter.println(password);
            userInfoWriter.flush();
            userInfoWriter.close();
        } catch (Exception e) {
            System.err.println("Exception thrown: " + e);
        }
    }

    @Override
    public RMIMessage login(String username, String password) throws RemoteException {
        int idx = usernameList.indexOf(username);
        if (idx != -1 && passwordList.get(idx).equals(password))
            if (isOnline(username))
                return new RMIMessage("User has already logged in", false);
            else {
                setOnline(username);
                return new RMIMessage("Logged in successfully", true);
            }
        else if (idx == -1)
            return new RMIMessage("User does not exist", false);
        else
            return new RMIMessage("Incorrect password", false);
    }

    @Override
    public RMIMessage register(String username, String password) throws RemoteException {
        int idx = usernameList.indexOf(username);
        if (idx == -1) {
            usernameList.add(username);
            passwordList.add(password);
            addNewUser(username, password);
            setOnline(username);
            return new RMIMessage("Registered successfully", true);
        } else
            return new RMIMessage("Username has been used", false);
    }

    @Override
    public RMIMessage logout(String username, String password) throws RemoteException {
        int idx = usernameList.indexOf(username);
        if (idx != -1 && passwordList.get(idx).equals(password)) {
            setOffline(username);
            return new RMIMessage("Logged out successfully", true);
        } else
            return new RMIMessage("Failed to logout", false);
    }
}
