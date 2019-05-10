import java.io.Serializable;
import java.util.ArrayList;

public class GameRoom implements Serializable {
		private ArrayList<User> users;
		private long startTime;
		private long endTime;
		
		public GameRoom(ArrayList<User> users) {
			this.users = users;
			startTime = System.currentTimeMillis();
		}
		
		public void addUser(User user) {
			users.add(user);
		}
		
		public double getTimeToWin() {
			return (endTime - startTime) / 1000.0;
		}

		public ArrayList<User> getUsersInfo() {
			return users;
		}

		public void setEndTime(long endTime) {
			this.endTime = endTime;
		}

}
