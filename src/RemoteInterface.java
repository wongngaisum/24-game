import java.rmi.*;

public interface RemoteInterface extends Remote {
    Message login(String username, String password) throws RemoteException;
    Message register(String username, String password) throws RemoteException;
    Message logout(String username, String password) throws RemoteException;
}
