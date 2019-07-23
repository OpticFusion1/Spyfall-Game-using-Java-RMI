package shared;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.RemoteRef;

public interface ConnectionInterface extends Remote
{
    public String testConnection() throws RemoteException;
    
    public void createDBConnection() throws RemoteException;
    
    public void signUp(String firstname, String lastname, String username, String password) throws RemoteException;
    
    public boolean checkIFUserExists(String username) throws RemoteException;
    
    public boolean login(String username, String password) throws RemoteException;
    
    public boolean areThereGames() throws RemoteException;
    
    public void createGame() throws RemoteException;
    
    public boolean areThereUnfinishedGames() throws RemoteException;
    
    public int getCurrentGameID() throws RemoteException;
    
    public void insertScore(int ID_G, String username, int points) throws RemoteException;
    
    public void finishCurrentGame(int gameID) throws RemoteException;
    
    public String[] generateStatistics(String nameOfUser) throws RemoteException;
}
