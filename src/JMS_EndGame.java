import java.io.Serializable;

public class JMS_EndGame implements Serializable {
    private User winner;
    private GameRoom room;
    private String answer;

    public JMS_EndGame(User winner, GameRoom room, String answer) {
        this.winner = winner;
        this.room = room;
        this.answer = answer;
    }

    public User getWinner() {
        return winner;
    }

    public GameRoom getRoom() {
        return room;
    }

    public String getAnswer() {
        return answer;
    }
}
