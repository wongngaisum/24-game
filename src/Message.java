import java.io.*;

public class Message implements Serializable {
    private String message;
    private boolean success;

    public Message(String message, boolean success) {
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
