package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import shared.*;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.Border;

public class UI extends JFrame implements ActionListener
{
    private static final long serialVersionUID = 1L;
    private int justOnce = 0;
    private int justOnce1 = 0;
    private String username;
    private JPanel textPanel, inputPanel;
    private JTextField textField;
    private String name, message;
    private Font meiryoFont = new Font("Meiryo", Font.PLAIN, 14);
    private Border blankBorder = BorderFactory.createEmptyBorder(10, 10, 20, 10);//top,r,b,l
    private Client chatClient;
    private JLabel userLabel;
    private String location;
    private int playerNumber;
    public Container c;
    private JList<String> list;
    private JList<String> list2;
    private DefaultListModel<String> listModel;
    private DefaultListModel<String> listModel2;
    private String ipAddress = "";
    private String nameOfUser;
    protected JTextArea textArea, userArea;
    protected JFrame frame;
    protected JButton dummyButton;
    protected JButton startButton, sendButton;
    protected JButton spyButton;
    protected JButton clientButton;
    protected JButton startGameButton;
    protected JButton statisticsButton;
    protected JPanel clientPanel, userPanel;
    protected JPanel clientPanel2;
    protected JPanel locationPanel;
    protected JPanel startAndStatisticPanel;
    
	public static void main(String args[])
        {
		try
                {
			for(LookAndFeelInfo info : UIManager.getInstalledLookAndFeels())
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
//		new UI();
	}

	public UI(String nameOfUser, String ipAddress)
        {
                this.nameOfUser = nameOfUser;
                this.ipAddress = ipAddress;
		frame = new JFrame("Client UI");	
		frame.addWindowListener(new java.awt.event.WindowAdapter() 
                {
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) 
                    {
		        
		    	if(chatClient != null)
                        {
			    	try {
			        	sendMessage("I am leaving the game!");
			        	chatClient.serverIF.leaveSystem(name);
				    } catch (RemoteException e) 
                                    {
					e.printStackTrace();
				    }		        	
		        }
		        System.exit(0);  
		    }   
		});	
                c = getContentPane();
		JPanel outerPanel = new JPanel(new BorderLayout());
		
		outerPanel.add(getInputPanel(), BorderLayout.SOUTH);
		outerPanel.add(getTextPanel(), BorderLayout.NORTH);
		
		c.setLayout(new BorderLayout());
		c.add(outerPanel, BorderLayout.CENTER);
		c.add(getUsersPanel(), BorderLayout.EAST); 
		frame.add(c);
                frame.setSize(750,395);
		frame.setLocation(150, 150);
		textField.requestFocus();	
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.setVisible(true);
//                username = JOptionPane.showInputDialog(null, "Write the username you want to choose!");
	}
	
	public JPanel getTextPanel()
        {
		String welcome = "Please click the button: Get in the game\n";
		textArea = new JTextArea(welcome, 14, 34);
		textArea.setMargin(new Insets(10, 10, 10, 10));
		textArea.setFont(meiryoFont);
		
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(textArea);
		textPanel = new JPanel();
		textPanel.add(scrollPane);
	
		textPanel.setFont(new Font("Meiryo", Font.PLAIN, 14));
		return textPanel;
	}
	public JPanel getInputPanel()
        {
		inputPanel = new JPanel(new GridLayout(1, 1, 5, 5));
		inputPanel.setBorder(blankBorder);	
		textField = new JTextField();
		textField.setFont(meiryoFont);
		inputPanel.add(textField);
		return inputPanel;
	}

        public JPanel getLocationPanel() throws RemoteException
        {
            locationPanel = new JPanel(new BorderLayout());

            String locationStr = "Possible locations      ";

            JLabel locationLabel = new JLabel(locationStr, JLabel.CENTER);
            locationPanel.add(locationLabel, BorderLayout.NORTH);
            locationLabel.setFont(new Font("Meiryo", Font.PLAIN, 16));
            String[] locations = chatClient.serverIF.getLocations();
            setLocationPanel(locations);
            locationPanel.setBorder(blankBorder);

            return locationPanel;		
        }
	public JPanel getUsersPanel()
        {
		
		userPanel = new JPanel(new BorderLayout());
		String  userStr = " Current Users      ";		
	        userLabel = new JLabel(userStr, JLabel.CENTER);
		userPanel.add(userLabel, BorderLayout.NORTH);	
		userLabel.setFont(new Font("Meiryo", Font.PLAIN, 16));
		String[] noClientsYet = {"No other users"};
		setClientPanel(noClientsYet);
		clientPanel.setFont(meiryoFont);		
		userPanel.setBorder(blankBorder);
                userPanel.add(makeButtonPanel(), BorderLayout.SOUTH);	
		return userPanel;		
	}
        
        public JPanel getStartAndStatisticsPanel()
        {
            
            startGameButton = new JButton("Start Game");
            startGameButton.addActionListener(this);
            startAndStatisticPanel = new JPanel(new GridLayout(1, 4));
            startAndStatisticPanel.add(startGameButton);
            startAndStatisticPanel.add(new JLabel(""));
            startAndStatisticPanel.add(new JLabel(""));
            startAndStatisticPanel.add(new JLabel(""));
            return startAndStatisticPanel;
        }
        
        public JPanel getSpyButtonPanel()
        {
            spyButton = new JButton("Guess the location");
            spyButton.addActionListener(this);
            JPanel buttonPanel2 = new JPanel(new GridLayout(4, 1));
            buttonPanel2.add(spyButton);
            buttonPanel2.add(new JLabel(""));
            return buttonPanel2;
        }
        
        public JPanel getClientButtonPanel()
        {
            clientButton = new JButton("Guess the spy");
            clientButton.addActionListener(this);
            JPanel buttonPanel3 = new JPanel(new GridLayout(4, 1));
            buttonPanel3.add(clientButton);
            buttonPanel3.add(new JLabel(""));
            return buttonPanel3;
        }
    
    public void setLocationPanel(String[] possibleLocations) 
    {
        clientPanel2 = new JPanel(new BorderLayout());
        listModel2 = new DefaultListModel<String>();

        for (String s : possibleLocations) 
        {
            listModel2.addElement(s);
        }
        list2 = new JList<String>(listModel2);
        list2.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        list2.setVisibleRowCount(10);
        list2.setFont(meiryoFont);
        JScrollPane listScrollPane = new JScrollPane(list2);
        clientPanel2.add(listScrollPane, BorderLayout.CENTER);
        locationPanel.add(clientPanel2, BorderLayout.CENTER);
    }

    public void setClientPanel(String[] currClients) 
    {  	
    	clientPanel = new JPanel(new BorderLayout());
        listModel = new DefaultListModel<String>();
        
        for(String s : currClients)
        {
        	listModel.addElement(s);
        }
        
        list = new JList<String>(listModel);
        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        list.setVisibleRowCount(8);
        list.setFont(meiryoFont);
        JScrollPane listScrollPane = new JScrollPane(list);

        clientPanel.add(listScrollPane, BorderLayout.CENTER);
        userPanel.add(clientPanel, BorderLayout.CENTER);
    }

	public JPanel makeButtonPanel() 
        {		
		
            sendButton = new JButton("Send Message ");
            sendButton.addActionListener(this);
            sendButton.setEnabled(false);
            frame.getRootPane().setDefaultButton(sendButton);
            dummyButton = new JButton("");
		
            startGameButton = new JButton("Start Game");
            startGameButton.addActionListener(this);
            
            statisticsButton = new JButton("My statistics");
            statisticsButton.addActionListener(this);
            statisticsButton.setBackground(Color.WHITE);
            statisticsButton.setEnabled(false);
            
            startButton = new JButton("Get in the game ");
            startButton.addActionListener(this);
		
            JPanel buttonPanel = new JPanel(new GridLayout(4, 1));
            buttonPanel.add(startGameButton);
            buttonPanel.add(startButton);
            buttonPanel.add(statisticsButton);
            buttonPanel.add(sendButton);
            return buttonPanel;
	}

         private Component[] getComponents(Component container) 
       {
         ArrayList<Component> list = null;

        try {
            list = new ArrayList<Component>(Arrays.asList(
                  ((Container) container).getComponents()));
            for (int index = 0; index < list.size(); index++) {
                for (Component currentComponent : getComponents(list.get(index))) {
                    list.add(currentComponent);
                }
            }
        } catch (ClassCastException e) {
            list = new ArrayList<Component>();
        }

        return list.toArray(new Component[list.size()]);
       }
        
	public void actionPerformed(ActionEvent e)
        {

		try {

			if(e.getSource() == startButton)
                        {
//				name = textField.getText();
                                
                            for (Component component : getComponents(c)) 
                            {
                                component.setEnabled(true);
                            }
                            
                                name = nameOfUser;
                                System.out.println(name);
                                userLabel.setText("");
					frame.setTitle(name + "'s console ");
					textField.setText("");
					textArea.append("username : " + name + " connecting to chat...\n");							
					getConnected(name);
//                                        location = chatClient.serverIF.giveMeLocation();
                                        playerNumber = chatClient.serverIF.whichPlayerAmI();
                                        System.out.println("You are player: " + playerNumber);
//                                        chatClient.amISpy = chatClient.serverIF.amIASpy(playerNumber);
                            if (!chatClient.connectionProblem) 
                            {                             
                                startButton.setEnabled(false);
                                sendButton.setEnabled(true);
                                startGameButton.setEnabled(true);
                                statisticsButton.setEnabled(true);
                            }				
			}

			if(e.getSource() == sendButton)
                        {
				message = textField.getText();
				textField.setText("");
				sendMessage(message);
			}

                        if(e.getSource() == spyButton)
                        {
                            String selected = list2.getSelectedValue();
                            System.out.println("The selected value is: " + selected);
                            chatClient.serverIF.isLocationFound(selected);
                        }
                        if(e.getSource() == clientButton)
                        {
                            int index = list.getSelectedIndex();
                            System.out.println("The selected index is: " + index);
                            chatClient.serverIF.guessTheSpy(index, chatClient.name, playerNumber);
                            System.out.println(index);
                        }
                        
                        if(e.getSource() == startGameButton)
                        {
                            chatClient.serverIF.startGame(playerNumber);
                        }
                        
                        if(e.getSource() == statisticsButton)
                        {
                            chatClient.serverIF.getStatistics(playerNumber);
                        }
			
		}
		catch (RemoteException remoteExc) 
                {			
			remoteExc.printStackTrace();	
		}
		
	}
        
        public void updateUI() throws RemoteException
        {
            
            location = chatClient.serverIF.giveMeLocation();
            chatClient.amISpy = chatClient.serverIF.amIASpy(playerNumber);

            if (justOnce != 0) 
            c.remove(locationPanel);   
            c.add(getLocationPanel(), BorderLayout.WEST);
            if (chatClient.amISpy == false) {
                userLabel.setText("Location: " + location + "     ");
                locationPanel.add(getClientButtonPanel(), BorderLayout.SOUTH);
            } else {
                userLabel.setText("YOU ARE THE SPY        ");
                locationPanel.add(getSpyButtonPanel(), BorderLayout.SOUTH);
            }
            startGameButton.setEnabled(false);
            justOnce++;
            locationPanel.repaint();
            this.revalidate();
            this.repaint();
        }
        
        public void updatePlayerNumber(int playerNumber)
        {
            this.playerNumber = playerNumber;
            System.out.println("You're number has changed to player:" + this.playerNumber);
        }
        
        public void showStatistics(String[] statistics)
        {
            String message = "------------------------------------------\nTotal number of points: " + statistics[0]
             + "\nNumber of games played: " + statistics[1] + "\nNumber of games won: " + statistics[2] + 
                    "\nNumber of games lost: " + statistics[3] + "\n------------------------------------------\n";
            textArea.append(message); 
            textArea.setCaretPosition(textArea.getDocument().getLength());
//            String[] nothingIn = null;
//            new Statistics(statistics).main(nothingIn);
        }
        
	private void sendMessage(String chatMessage) throws RemoteException 
        {
		chatClient.serverIF.updateChat(name, chatMessage);
	}
	
	private void getConnected(String userName) throws RemoteException
        {
		
//            String cleanedUserName = userName.replaceAll("\\s+","_");
//            cleanedUserName = userName.replaceAll("\\W+","_");
//            String cleanedUserName = userName;
            try {		
               	chatClient = new Client(this, userName, ipAddress);
		chatClient.startClient(userName);
		} 
            catch (RemoteException e) 
            {
		e.printStackTrace();
            }
	}

}










