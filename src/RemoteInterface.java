import java.rmi.*;

public interface RemoteInterface extends Remote {
    RMIMessage login(String username, String password) throws RemoteException;
    RMIMessage register(String username, String password) throws RemoteException;
    RMIMessage logout(String username, String password) throws RemoteException;
}
