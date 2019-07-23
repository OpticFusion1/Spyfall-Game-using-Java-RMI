/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import shared.*;
/**
 *
 * @author Lenovo
 */
public class DBConnection implements ConnectionInterface
{
    private Connection conn;
    
    
    public void createDBConnection() throws RemoteException
    {    
        conn = null;
        try {
            String dbURL = "jdbc:sqlserver://localhost;databaseName=Tema";
            String user = "riti";
            String pass = "123";
            conn = DriverManager.getConnection(dbURL, user, pass);
            if (conn != null) {
                DatabaseMetaData dm = (DatabaseMetaData) conn.getMetaData();
                System.out.println("Driver name: " + dm.getDriverName());
                System.out.println("Driver version: " + dm.getDriverVersion());
                System.out.println("Product name: " + dm.getDatabaseProductName());
                System.out.println("Product version: " + dm.getDatabaseProductVersion());
            }
 
        } catch (SQLException ex) {
            ex.printStackTrace();
        } 
//        finally 
//        {
//            try {
//                if (conn != null && !conn.isClosed()) {
//                    conn.close();
//                }
//            } catch (SQLException ex) {
//                ex.printStackTrace();
//            }
//        }
    }
       
    public ConnectionInterface createStub() throws RemoteException
    {
        System.setProperty("java.security.policy", "src\\server\\policy.txt");
            if (System.getSecurityManager() == null) 
            {
            System.setSecurityManager(new SecurityManager());
            }
        ConnectionInterface engine = new DBConnection();
        ConnectionInterface stub = (ConnectionInterface) UnicastRemoteObject.exportObject(engine, 0);
        return stub;
    }
    
    public boolean areThereGames()
    {
        boolean thereAreGames = false;
        Statement stmt;
        try 
        {
            stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery("SELECT COUNT(*) AS rows FROM Games");
            result.next();
            int rows = result.getInt("rows");
            if(rows != 0)
            {
                thereAreGames = true;
                System.out.println("There are games");
            }
            else
            {
                System.out.println("There are no games");
            }
        } 
        catch (SQLException ex) 
        {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return thereAreGames;
    }
    
    public void createGame() throws RemoteException
    {
        Statement stmt;
        try 
        {
            stmt = conn.createStatement();
            String query = "INSERT INTO Games VALUES (current_timestamp, '0')";
            stmt.execute(query);
        } 
        catch (SQLException ex) 
        {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public boolean areThereUnfinishedGames() throws RemoteException
    {
        boolean are = false;
        Statement stmt;
        try 
        {
            stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery("SELECT COUNT(*) AS rows FROM (SELECT ID_G FROM Games WHERE Finished = '0') alias");
            result.next();
            int rowsNum = result.getInt("rows");
            System.out.println("Number of unfinished games is: " + rowsNum);
            if(rowsNum > 0)
            {
                are = true;
            }
//            return are;
        } 
        catch (SQLException ex) 
        {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return are;
    }
    
    public int getCurrentGameID() throws RemoteException
    {
        int gameID = 0;
        Statement stmt;
        try 
        {
            stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery("SELECT TOP 1 * FROM Games ORDER BY ID_G DESC");
            result.next();
            gameID = result.getInt("ID_G");
            System.out.println("Current game id is: " + gameID);
        } 
        catch (SQLException ex) 
        {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }       
        return gameID;
    }
    
    public void finishCurrentGame(int gameID) throws RemoteException
    {
        Statement stmt;
        try 
        {
            stmt = conn.createStatement();
            stmt.execute("UPDATE Games SET Finished = '1' WHERE ID_G = '" + gameID + "'");
        } 
        catch (SQLException ex) 
        {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }       
    }
    
    public void insertScore(int ID_G, String username, int points) throws RemoteException
    {
        Statement stmt;
        try 
        {
            stmt = conn.createStatement();
            String idG = ID_G + "";
            String pointsNum = points + "";
            
            //Get user ID
            ResultSet result = stmt.executeQuery("SELECT ID_U FROM Users WHERE username = '" + username + "'");
            result.next();
            String idU = result.getInt("ID_U") + "";
            
            String query = "INSERT INTO Scores VALUES ('" + idG + "','" + idU + "','" + pointsNum + "')";
            stmt.execute(query);
        } 
        catch (SQLException ex) 
        {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public String[] generateStatistics(String nameOfUser) throws RemoteException
    {
        String[] statistics = new String[4];
        Statement stmt = null;
        Statement stmt1 = null;
        Statement stmt2 = null;
        Statement stmt3 = null;
        Statement stmt4 = null;
        try 
        {
            ResultSet result;
            stmt = conn.createStatement();
            result = stmt.executeQuery("SELECT ID_U FROM Users WHERE Username = '" + nameOfUser + "'");
            result.next();
            int idU = result.getInt("ID_U");
        
            //Total number of points
            stmt1 = conn.createStatement();           
            result = stmt1.executeQuery("SELECT SUM(Points) AS total FROM Scores WHERE ID_U = '" + idU + "'"); 
            result.next();
            statistics[0] = "" + result.getInt("total");
            
            //Number of games played
            stmt2 = conn.createStatement();           
            result = stmt2.executeQuery("SELECT COUNT(*) AS count FROM Scores WHERE ID_U = '" + idU + "'"); 
            result.next(); 
            statistics[1] = "" + result.getInt("count");
            
            //Number of games won
            stmt3 = conn.createStatement();           
            result = stmt3.executeQuery("SELECT COUNT(*) AS count FROM Scores WHERE ID_U = '" + idU + "' AND Points = '1'"); 
            result.next(); 
            statistics[2] = "" + result.getInt("count");
            
            //Numer of games lost
            stmt4 = conn.createStatement();           
            result = stmt4.executeQuery("SELECT COUNT(*) AS count FROM Scores WHERE ID_U = '" + idU + "' AND Points = '0'"); 
            result.next(); 
            statistics[3] = "" + result.getInt("count");
            
        } 
        catch (SQLException ex) 
        {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return statistics;
    }
    
    public String testConnection() 
    {
        return "The connection is successful!";
    }
    
    //Methods for queries in the database
    public void signUp(String firstname, String lastname, String username, String password) throws RemoteException
    {
        try 
        {
            //The salt is generated with 10 log rounds
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            Statement stmt = conn.createStatement();
            String query = "INSERT INTO Users VALUES "
            + "('" + firstname + "','" + lastname + "','" + username + "'," +  "current_timestamp" + ",'" + hashedPassword + "')";
            stmt.execute(query);
        } 
        catch (SQLException ex) 
        {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public boolean login(String username, String password) throws RemoteException
    {
        boolean dataCorrect = false;
        try 
        {
            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery("SELECT Password_hash FROM Users WHERE Username = '" + username + "'");
            result.next();
            String hashed_password = result.getString("Password_hash");
            boolean same = BCrypt.checkpw(password, hashed_password);
            if(same)
            {
                dataCorrect = true;
            }   
        } 
        catch (SQLException ex) 
        {
            System.out.println("The result set has no current row");
//            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }       
        return dataCorrect;
    }
    
    public boolean checkIFUserExists(String username)
    {
        boolean exists = false;
        try 
        {
            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery("SELECT COUNT(*) AS rowcounts FROM Users WHERE Username = '" + username + "'");
            result.next();
            int count = result.getInt("rowcounts");
            if(count > 0)
            {
                exists = true;
            }           
            result.close();
            return exists;
        } 
        catch (SQLException ex) 
        {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return true;
    }
}
