import java.io.Serializable;
import java.util.ArrayList;

public class JMS_StartGame implements Serializable {
	private ArrayList<User> users;
	private ArrayList<Card> cards;
	
	public JMS_StartGame(ArrayList<User> users, ArrayList<Card> cards) {
		this.users = users;
		this.cards = cards;
	}
	
	public ArrayList<User> getUsers() {
		return users;
	}
	
	public ArrayList<Card> getCards() {
		return cards;
	}
}
