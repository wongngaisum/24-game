import java.io.Serializable;

public class JMS_JoinGame implements Serializable {
	private User user;
	
	public JMS_JoinGame(User user) {
		this.user = user;
	}

	public User getUser() {
		return user;
	}
}
