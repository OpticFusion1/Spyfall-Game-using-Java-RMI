package server;
import shared.*;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.RemoteRef;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;
import java.util.Random;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

public class Server extends UnicastRemoteObject implements ServerInterface 
{
	String line = "---------------------------------------------\n";
        private String location;
        private String[] locations = {"Airplane","Bank","Beach","Casino","Submarine","Theater","Hotel","Restaurant","Passenger Train", "School"};
	private Vector<Player> players;
        private int gameID;
        private int numOfPlayers = 4;
        private boolean locked = false;
        private int spyNumber;
        private String spyUsername;
        private int counter = 0;
        private int countSpyTries = 0;
        private int countingPlayers = 0;
        private boolean gameStarted = false;
        private static String hostNameStatic;
        private String hostName;
        private static ConnectionInterface dbStub;
	private static final long serialVersionUID = 1L;
	private static DBConnection conn;	
	public Server() throws RemoteException 
        {
		super();
		players = new Vector<Player>(10, 1);
//                generateLocation();
//                generateSpy();
                try
                {
			for(UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels())
                        {
				if("CDE/Motif".equals(info.getName()))
                                {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
                                else
                                {}
			}
		}
		catch(Exception e)
                {
	        }
                hostNameStatic = JOptionPane.showInputDialog(null, "Write the ip address of the host!");
                hostName = hostNameStatic;
//                conn = new DBConnection();
	}
	
	public static void main(String[] args) 
        {
		startRMIRegistry();	
//		String hostName = "192.168.0.26";
		String serviceName = "SpyFallGame";
		
//		if(args.length == 2)
//                {
//			hostName = args[0];
//			serviceName = args[1];
//		}
		
		try
                {
			ServerInterface serverIF = new Server();
			Naming.rebind("rmi://" + hostNameStatic + ":1099/" + serviceName, serverIF);
                        serverIF.createDBConnection();
			System.out.println("RMI Server is running...");
                        
                        
		}
		catch(Exception e)
                {
			System.out.println("Server had problems starting");
                        e.printStackTrace();
		}	
                
	}
            
        public void createDBConnection() throws RemoteException
        {
            DBConnection d = new DBConnection();
            dbStub = d.createStub();
            dbStub.createDBConnection();
            boolean hasGames = dbStub.areThereGames();
            if(!hasGames)
            {
               dbStub.createGame();
            }
        }
        public void generateLocation()
        {
            Random rand = new Random();         
            int  n = rand.nextInt(locations.length-1);
            location = locations[n];          
            System.out.println("The location is: " + location);
        }

	public static void startRMIRegistry() 
        {
            System.setProperty("java.security.policy", "src\\server\\policy.txt");
            if (System.getSecurityManager() == null) 
            {
            System.setSecurityManager(new SecurityManager());
            }
             try{
			java.rmi.registry.LocateRegistry.createRegistry(1099);
			System.out.println("RMI Server ready");
		}
		catch(RemoteException e) 
                {
                    e.printStackTrace();
		}
	}
        
        public ConnectionInterface giveConnection()
        {
            return dbStub;
        }
		

	public String sayHello(String ClientName) throws RemoteException 
        {
            System.out.println(ClientName + " sent a message");
            return "Hello " + ClientName + " from group chat server";
	}
	
        public int whichPlayerAmI()
        {
            int r = counter - 1;
//            r = players.size() - 1;
            return r;
//            int r = countingPlayers;
//            countingPlayers++;
//            return r;
        }
        public void generateSpy()
        {
            Random rand = new Random();         
            int  n = rand.nextInt(players.size());
            spyNumber = n;
            spyUsername = spyUsername = players.get(spyNumber).getName();
            System.out.println("The spy is player with the number: " + spyNumber);
        }
        
        public boolean amIASpy(int playerNumber) throws RemoteException
        {
            boolean is = false;
            if(playerNumber == spyNumber)
            {
                is = true;
            }
            return is;
            
//            boolean is = false;
//            if(counter-1 == spyNumber)
//            {
//                is = true;
//            }
//            return is;
        }
        
        public void startGame(int playerNumber) throws RemoteException
        {
           if(players.size() > 2)
           {
               String name = players.get(playerNumber).getName();
            System.out.println("Start game by: " + name);
           String message = "" + name + ": Do you agree to start the game?" ;
           boolean agreeAll = true;
           for(int i = 0; i < players.size(); i++)
           {
               if(i != playerNumber)
               {
                 try
                 {
                     boolean agree = players.get(i).getClient().doYouAgreeWithAccusation(message);
                     if(agree == false)
                     {
                        agreeAll = false;
                     }
                 }
                 catch(Exception e)
                 {
                     e.printStackTrace();
                 }
               }
           }
           if(agreeAll)
           {
               gameStarted = true;
               generateLocation();
               generateSpy();
               updateSituation();
           }
           }
        }
        
        public void updateSituation()
        {
            for(Player c : players)
                {
			try 
                        {
                            c.getClient().updateUI();
			} 
			catch (RemoteException e) 
                        {
                            e.printStackTrace();
			}
		}	
        }

	public void updateChat(String name, String nextPost) throws RemoteException 
        {
            String message =  name + " : " + nextPost + "\n";
            sendMessageToEveryone(message);
	}
        
        public void getStatistics(int playerNumber) throws RemoteException
        {
            String nameOfUser = players.get(playerNumber).getName();
            System.out.println("Statistics generated for: " + nameOfUser);
            String[] statistics = dbStub.generateStatistics(nameOfUser);
            players.get(playerNumber).getClient().takeStatistics(statistics);
        }
	

	public void registerClient(String[] details) throws RemoteException 
        {	
            boolean are = dbStub.areThereUnfinishedGames();
            if(!are)
            {
                dbStub.createGame();
            }
            gameID = dbStub.getCurrentGameID();
            System.out.println("game id is: " + gameID);
            System.out.println(new Date(System.currentTimeMillis()));
            System.out.println(details[0] + " has joined the game");
            System.out.println(details[0] + "'s hostname : " + details[1]);
            System.out.println(details[0] + "'sRMI service : " + details[2]);
            if(players.size() <= numOfPlayers-1)
            {
                registerPlayer(details);
                counter++;
                System.out.println("Counter is: " + counter);
            }
            else{
                 JOptionPane.showInputDialog(null, "There is no more room for other players");
                }
	}
        
        public String[] getLocations() throws RemoteException
        {
            return locations;
        }
        
        public String giveMeLocation()
        {
            return location;
        }
               
        public boolean isLocationFound(String locationGiven)
        {
            boolean found = false;
            if(countSpyTries < 2)
            {
                if (location.equals(locationGiven)) 
                {
                    found = true;
                    locationFound();
                }
                if(found == false)
                {
                    locationNotFoundThisTime();
                }
            }
            else if(countSpyTries == 2)
            {
                if (location.equals(locationGiven)) 
                {
                    found = true;
                    locationFound();
                }

                if(found == false)
                {
                    locationNotFound();
                }
            }
            else{
                cannotTryAnymore();
            }
            countSpyTries++;
            return found;
        }
        
        public void cannotTryAnymore()
        {
            try
            {
                players.get(spyNumber).getClient().messageFromServer("You cannot try anymore! \n");
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
            
        public void locationFound()
        {
            sendMessageToEveryone("The game is finished! The location was found by the spy. The spy was the player with the name: " + players.get(spyNumber).name + "\n");
            disableClients();
            String spyUsername = players.get(spyNumber).name;
            try {
                dbStub.insertScore(gameID, spyUsername, 1);
            } catch (RemoteException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
            for(Player c : players)
            {
                if(!c.getName().equals(spyUsername))
                {
                    try {
                        dbStub.insertScore(gameID, c.getName(), 0);
                    } catch (RemoteException ex) {
                        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            players.clear();
            counter = 0;
            countSpyTries = 0;
            try {
                dbStub.finishCurrentGame(gameID);              
            } catch (RemoteException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
//            generateSpy();
//            generateLocation();
//            System.out.println("Spy is number: " + spyNumber);
        }
        
        public void locationNotFound()
        {
            sendMessageToEveryone("The game is finished! The location was not found after all the possible tries. The spy was the player with the name: " + players.get(spyNumber).name + "\n");
            disableClients();
            String spyUsername = players.get(spyNumber).name;
            try {
                dbStub.insertScore(gameID, spyUsername, 0);
            } catch (RemoteException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
            for(Player c : players)
            {
                if(!c.getName().equals(spyUsername))
                {
                    try {
                        dbStub.insertScore(gameID, c.getName(), 1);
                    } catch (RemoteException ex) {
                        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            players.clear();
            counter = 0;
            countSpyTries = 0;
            try {
                dbStub.finishCurrentGame(gameID);              
            } catch (RemoteException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("Spy is number: " + spyNumber);          
        }
	
        public void locationNotFoundThisTime()
        {
            try
            {
                players.get(spyNumber).getClient().messageFromServer("Your location was not correct" + "\n");
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        
        public void guessTheSpy(int index, String person, int playerNumber)
        {
           String name = players.get(index).getName();
           String message = "" + person + ": Do you agree to accuse " + name + " as a spy?" ;
           boolean agreeAll = true;
           for(int i = 0; i < players.size(); i++)
           {
               if(i != index && i != playerNumber)
               {
                 try
                 {
                     boolean agree = players.elementAt(i).getClient().doYouAgreeWithAccusation(message);
                     if(agree == false)
                     {
                        agreeAll = false;
                     }
                 }
                 catch(Exception e)
                 {
                     e.printStackTrace();
                 }
               }
           }
           if(agreeAll)
           {
               if(spyNumber == index)
               {
                   sendMessageToEveryone("The game is finished! The spy is " + name);
//                   System.out.println("Number of players in the vector is: " + players.size());
//                   removeAllPlayers();
                   String spyUsername = players.get(spyNumber).name;
                   try {
                       dbStub.insertScore(gameID, spyUsername, 0);
                   } catch (RemoteException ex) {
                       Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                   }
                   for (Player c : players) {
                       if (!c.getName().equals(spyUsername)) {
                           try {
                               dbStub.insertScore(gameID, c.getName(), 1);
                           } catch (RemoteException ex) {
                               Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                           }
                       }
                   }
                   disableClients();
                   players.clear();
                   counter = 0;
                   countSpyTries = 0;
                   try {
                       dbStub.finishCurrentGame(gameID);
                   } catch (RemoteException ex) {
                       Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                   }
//                   generateSpy();
//                   generateLocation();
                   System.out.println("Spy is number: " + spyNumber);
//                   refreshTheListDissapearNames();
               }
               else
               {
                   sendMessageToEveryone("The game is finished! The spy was not found! The real spy was: " + players.get(spyNumber).getName());
                   disableClients();
                   String spyUsername = players.get(spyNumber).name;
                   try {
                       dbStub.insertScore(gameID, spyUsername, 1);
                   } catch (RemoteException ex) {
                       Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                   }
                   for (Player c : players) {
                       if (!c.getName().equals(spyUsername)) {
                           try {
                               dbStub.insertScore(gameID, c.getName(), 0);
                           } catch (RemoteException ex) {
                               Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                           }
                       }
                   }
                   players.clear();
                   counter = 0;
                   countSpyTries = 0;
                   try {
                       dbStub.finishCurrentGame(gameID);
                   } catch (RemoteException ex) {
                       Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                   }
//                   generateSpy();
//                   generateLocation();
                   System.out.println("Spy is number: " + spyNumber);
               }
           }
           else
           {
               
           }
        }
        
        private void disableClients()
        {
            for(int i = 0; i < players.size(); i++)
            {
                try
                {
                    players.get(i).getClient().disableEverything();
                }
                catch(RemoteException e)
                {
                    e.printStackTrace();
                }
            }
        }

	private void registerPlayer(String[] details)
        {		
		try{
			ClientInterface nextClient = ( ClientInterface )Naming.lookup("rmi://" + details[1] + "/" + details[2]);			
			players.addElement(new Player(details[0], nextClient)); 			
			nextClient.messageFromServer("[Server] : Hello " + details[0] + " you are now in the game.\n");			
			sendMessageToEveryone("[Server] : " + details[0] + " has started to play in the game.\n");			
			refreshTheList();		
		}
		catch(RemoteException | MalformedURLException | NotBoundException e){
			e.printStackTrace();
		}
	}
        
        public void proxyBind(ClientInterface client, String serviceName) throws RemoteException
        {
            try
            {
//                String hostName = "192.168.0.26";
                Naming.rebind("rmi://" + hostName + ":1099/" + serviceName, client);
            }
            catch(Exception e)
            {
                System.out.println("Error in proxyBind() method");
            }
        }
	

	private void refreshTheList() 
        {
		String[] currentUsers = getUserList();	
		for(Player c : players)
                {
			try 
                        {
				c.getClient().refreshTheList(currentUsers);
			} 
			catch (RemoteException e) 
                        {
				e.printStackTrace();
			}
		}	
	}
        
        private void refreshTheListDissapearNames()
        {
            String[] currentUsers = getUserList();	
            String[] setList = {};
		for(Player c : players)
                {
			try 
                        {
				c.getClient().refreshTheList(setList);
			} 
			catch (RemoteException e) 
                        {
				e.printStackTrace();
			}
		}	
        }
	
	private String[] getUserList()
        {
		String[] allUsers = new String[players.size()];
		for(int i = 0; i< allUsers.length; i++)
                {
                    allUsers[i] = players.elementAt(i).getName();
		}
		return allUsers;
	}
	
	public void sendMessageToEveryone(String newMessage)
        {	
		for(Player c : players)
                {
			try 
                        {
                            c.getClient().messageFromServer(newMessage);
			} 
			catch (RemoteException e) 
                        {
                            e.printStackTrace();
			}
		}	
	}
        
        public void removeAllPlayers()
        {
            for(Player c : players)
                {		
                    players.remove(c);
                    counter--;			
		}		
            System.out.println("The number of players in the vector is: " + players.size());
        }
        
	public void leaveSystem(String userName) throws RemoteException
        {
//		String spyUsername = players.get(spyNumber).getName();
		for(Player c : players)
                {
			if(c.getName().equals(userName))
                        {
                            System.out.println(line + userName + " left the game");
                            System.out.println(new Date(System.currentTimeMillis()));
                            players.remove(c);
                            counter--;
                            break;
			}
                        if(spyUsername.equals(userName))
                        {
                            sendMessageToEveryone("The spy left the game! You need to start a new game. The spy was: " + players.get(spyNumber).getName());
                            disableClients();
                            players.clear();
                            counter = 0;
                            countSpyTries = 0;
                            break;
                        }
                          
                        
		}		
                
                int countPlayer = players.size();
                for(Player c : players)
                {                   
                    c.getClient().updatePlayerNumber(players.size() - countPlayer);
                    countPlayer--;
                }
		if(!players.isEmpty())
                {
			refreshTheList();
		}			
	}
}



