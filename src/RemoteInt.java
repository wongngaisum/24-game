import java.rmi.*;
import java.util.ArrayList;

public interface RemoteInt extends Remote {
    RMIMessage login(User user) throws RemoteException;

    RMIMessage register(User user) throws RemoteException;

    RMIMessage logout(User user) throws RemoteException;

    User retrieveUserData(User user) throws RemoteException;

    ArrayList<User> getLeaderBoardData() throws RemoteException;
    
    void submitAnswer(User user, ArrayList<Card> cards, String answer) throws RemoteException;
}
