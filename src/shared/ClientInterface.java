package shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientInterface extends Remote
{
        public boolean doYouAgreeWithAccusation(String message) throws RemoteException;
	
        public void messageFromServer(String message) throws RemoteException;

	public void refreshTheList(String[] currentUsers) throws RemoteException;
        
        public void disableEverything() throws RemoteException;   
        
        public void updateUI() throws RemoteException;
        
        public void updatePlayerNumber(int number) throws RemoteException;
        
        public void takeStatistics(String[] statistics) throws RemoteException;
}
