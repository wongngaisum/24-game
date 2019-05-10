import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;

public class DBManager {
    private Connection conn;
    private static final String DB_HOST = "localhost";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";
    private static final String DB_NAME = "c0402";
    
    private final Object userInfoLock = new Object();
    private final Object onlineUserLock = new Object();
    private ArrayList<User> users;

    public DBManager() throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
    	// DB
        Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
        conn = DriverManager.getConnection("jdbc:mysql://" + DB_HOST +
                "/" + DB_NAME +
                "?user=" + DB_USER +
                "&password=" + DB_PASS);
        initialize();
        System.out.println("Database connection successful");
    }

    private void initialize() throws SQLException {
        synchronized (userInfoLock) {
            Statement stmt = conn.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS UserInfo (username VARCHAR(32) NOT NULL, password VARCHAR(32) NOT NULL, wins INT NOT NULL, " +
                    "games_played INT NOT NULL, average_time DOUBLE NOT NULL, PRIMARY KEY (username))");
        }

        synchronized (onlineUserLock) {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS OnlineUser (username VARCHAR(32) NOT NULL, PRIMARY KEY(username))");
        }

        synchronized (onlineUserLock) {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("Delete from OnlineUser");
        }
    }

    public String checkACPW(String username, String password) throws SQLException {
        synchronized (userInfoLock) {
            PreparedStatement stmt;
            stmt = conn.prepareStatement("SELECT username, password FROM UserInfo WHERE username = ?");
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                if (rs.getString(2).equals(password)) {
                    return "correct";
                } else {
                    return "incorrect";
                }
            } else {
                return "no such user";
            }
        }
    }

    public boolean checkOnline(String username) throws SQLException {
        synchronized (onlineUserLock) {
            PreparedStatement stmt;
            stmt = conn.prepareStatement("SELECT Count(*) FROM OnlineUser WHERE username = ?");
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        }
    }

    public void setOnline(String username) throws SQLException {
        synchronized (onlineUserLock) {
            PreparedStatement stmt;
            stmt = conn.prepareStatement("INSERT INTO OnlineUser (username) VALUES (?)");
            stmt.setString(1, username);
            stmt.execute();
        }
    }

    public boolean checkDuplicateUser(String username) throws SQLException {
        synchronized (userInfoLock) {
            PreparedStatement stmt;
            stmt = conn.prepareStatement("SELECT username FROM UserInfo WHERE username = ?");
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }

    public String register(String username, String password) throws SQLException {
        synchronized (userInfoLock) {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO UserInfo (username, password, wins, games_played, average_time) VALUES (?, ?, ?, ?, ?)");
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setInt(3, 0);
            stmt.setInt(4, 0);
            stmt.setDouble(5, 0);
            stmt.execute();
            return "Registered successfully";
        }
    }

    public void logout(String username) throws SQLException {
        synchronized (onlineUserLock) {
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM OnlineUser WHERE username = ?");
            stmt.setString(1, username);
            stmt.executeUpdate();
        }
    }

    public User retrieveUserData(User user) throws SQLException {
        synchronized (userInfoLock) {
            sortUsersByRank();
            if (users.contains(user)) {
                return users.get(users.indexOf(user));
            } else {
                return null;
            }
        }
    }

    public void sortUsersByRank() throws SQLException {
        synchronized (userInfoLock) {
            users = new ArrayList<>();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT username, wins, games_played, average_time FROM UserInfo");
            while (rs.next()) {
                users.add(new User(rs.getString(1), rs.getInt(2), rs.getInt(3), rs.getDouble(4)));
            }

            Collections.sort(users);

            for (int i = 0; i < users.size(); i++) {
                users.get(i).setRank(i + 1);
            }
        }
    }

    public ArrayList<User> getLeaderBoardData() throws SQLException {
        synchronized (userInfoLock) {
            sortUsersByRank();
            return users;
        }
    }
}
