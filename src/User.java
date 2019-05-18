import java.io.Serializable;

public class User implements Serializable, Comparable<User> {
	private String username;
    private String password;
    private int gamesWon;
    private int gamesPlayed;
    private double timeToWin;
    private int rank;

    public User(int rank, String username, int gamesWon, int gamesPlayed, double timeToWin) {
        this.username = username;
        this.gamesWon = gamesWon;
        this.gamesPlayed = gamesPlayed;
        this.timeToWin = timeToWin;
        this.rank = rank;
    }

    public User(String username, int gamesWon, int gamesPlayed, double timeToWin) {
        this.username = username;
        this.gamesWon = gamesWon;
        this.gamesPlayed = gamesPlayed;
        this.timeToWin = timeToWin;
    }

    public User(String username, String password, int gamesWon, int gamesPlayed, double timeToWin) {
        this.username = username;
        this.gamesWon = gamesWon;
        this.gamesPlayed = gamesPlayed;
        this.timeToWin = timeToWin;
        this.password = password;
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public int getRank() {
        return rank;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getGamesWon() {
        return gamesWon;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public double getTimeToWin() {
        return timeToWin;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    @Override
    public int compareTo(User o) {
        if (getGamesWon() > o.getGamesWon()) {
            return -1;
        } else if (getGamesWon() < o.getGamesWon()) {
            return 1;
        } else if (getTimeToWin() > o.getTimeToWin()) {
            return 1;
        } else if (getTimeToWin() < o.getTimeToWin()) {
            return -1;
        }
        return 0;
    }

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof User) {
            return username.equals(((User) obj).getUsername());
        }
        return false;
    }
    
}
