import java.io.Serializable;

public class RMIMessage implements Serializable {
	private String message;
    private boolean success;

    public RMIMessage(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public boolean getStatus() {
        return success;
    }
}
