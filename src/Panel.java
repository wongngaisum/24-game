import javax.swing.*;

public abstract class Panel extends JPanel {
    private String name;
    private boolean status;
    private RemoteInterface remote;
    private Client client;

    public Panel(String name, boolean status, RemoteInterface remote, Client client) {
        this.name = name;
        this.status = status;
        this.remote = remote;
        this.client = client;
        initializeGUI();
    }

    public String getName() {
        return name;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public RemoteInterface getRemote() {
        return remote;
    }

    public Client getClient() {
        return client;
    }

    public abstract void initializeGUI();
}
