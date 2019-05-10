import java.io.Serializable;
import java.util.ArrayList;

public class GameRoom implements Serializable {
    private ArrayList<User> users;
    private long startTime;
    private long endTime;

    public GameRoom(ArrayList<User> players) {
        users = players;
        startTime = System.currentTimeMillis();
    }

    public double getTimeToWin() {
        return (endTime - startTime) / 1000.0;
    }

    public ArrayList<User> getPlayers() {
        return users;
    }

    public void endGame() {
        endTime = System.currentTimeMillis();
    }

    public boolean havePlayer(User player) {
        for (User user : users) {
            if (user.equals(player)) {
                return true;
            }
        }
        return false;
    }
    
    public void removePlayer(User player) {
        for (User user : users) {
            if (user.equals(player)) {
                users.remove(user);
                return;
            }
        }
    }
    
    public boolean isEmpty() {
    	return users.size() == 0;
    }
}
