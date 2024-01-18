
package ubc.cosc322;

import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.ArrayList;

import sfs2x.client.entities.Room;
import ygraph.ai.smartfox.games.BaseGameGUI;
import ygraph.ai.smartfox.games.GameClient;
import ygraph.ai.smartfox.games.GamePlayer;
import ygraph.ai.smartfox.games.amazons.AmazonsGameMessage;
import ygraph.ai.smartfox.games.GameMessage;


/**
 * An example illustrating how to implement a GamePlayer
 * @author Yong Gao (yong.gao@ubc.ca)
 * Jan 5, 2021
 *
 */
public class COSC322Test extends GamePlayer{

    private GameClient gameClient = null; 
    private BaseGameGUI gamegui = null;
	
    private String userName = "testRunG4";
    private String passwd = "testG4";
 
	
    /**
     * The main method
     * @param args for name and passwd (current, any string would work)
     */
    public static void main(String[] args) {
    	COSC322Test player = new COSC322Test(args[0], args[1]);
    	
    	if(player.getGameGUI() == null) {
    		player.Go();
    	}
    	else {
    		BaseGameGUI.sys_setup();
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                	player.Go();
                }
            });
    	}
    }
	
    /**
     * Any name and passwd 
     * @param userName
      * @param passwd
     */
    public COSC322Test(String userName, String passwd) {
    	this.userName = userName;
    	this.passwd = passwd;
    	
    	//To make a GUI-based player, create an instance of BaseGameGUI
    	//and implement the method getGameGUI() accordingly
    	//this.gamegui = new BaseGameGUI(this);
    }
 


    @Override
	public void onLogin() {
		System.out.println("Congratulations!!! "
				+ "I am called because the server indicated that the login is successfully");
		System.out.println("The next step is to find a room and join it: "
				+ "the gameClient instance created in my constructor knows how!");
		// Print a list of rooms
		List<Room> roomList = gameClient.getRoomList();
		if(roomList.isEmpty()){
			System.out.println("There are no rooms to enter.");
			return;
		} else {
			for (Room room : roomList) {
				System.out.println(room.getName());
			}
		}
		boolean roomFound = false;
		while(!roomFound){
			// Prompt user for desired room
			System.out.println("Please type out the name of a room to enter.");
			Scanner scanner = new Scanner (System.in);
			String desiredRoom = scanner.nextLine();
			// Iterate through rooms to find desired room
			for(Room room : roomList){
				if(room.getName().equals(desiredRoom)){
					System.out.println("Room found.");
					gameClient.joinRoom(desiredRoom);
					System.out.println("Room joined.");
					roomFound = true;
					break;
				}
			}
			// If iterated through all rooms and not found, user is notified then
			// prompted again to enter the name of desired room
			if(!roomFound){
				System.out.println("Room not found.");
			}
		}
	}

    @Override
    public boolean handleGameMessage(String messageType, Map<String, Object> msgDetails) {
    	//This method will be called by the GameClient when it receives a game-related message
    	//from the server.
	
    	//For a detailed description of the message types and format, 
    	//see the method GamePlayer.handleGameMessage() in the game-client-api document. 

		   switch(messageType) {
			case GameMessage.GAME_STATE_BOARD:
				Object check = msgDetails.get(AmazonsGameMessage.GAME_STATE);
				ArrayList<Integer> gameBoardState;
				gameBoardState = (ArrayList<Integer>)check;

				// TODO: Use gameBoard from here

				// Raw data
				System.out.println("Raw data:");
				for (int i = 0; i < gameBoardState.size(); i++) {
					System.out.print(gameBoardState.get(i) + " ");
				}

				// Board Format
				System.out.println("\n\nBoard:");
				int count = 0;
				for (int i = 12; i < gameBoardState.size(); i++) {
					if(count == 10) {
						System.out.println();
						count = 0;
						i++;
					}
					count++;
					System.out.print(gameBoardState.get(i) + " ");
				}
				System.out.println("\n");
				break;

			case GameMessage.GAME_ACTION_START:
				handleGameMessage(GameMessage.GAME_STATE_BOARD, msgDetails); // Just use the last case
				String playerNameBlack, playerNameWhite;
				playerNameBlack = (String)msgDetails.get(AmazonsGameMessage.PLAYER_BLACK);
				playerNameWhite = (String)msgDetails.get(AmazonsGameMessage.PLAYER_WHITE);

				// TODO: Store player names as necessary
				break;

			case GameMessage.GAME_ACTION_MOVE:
					// TODO: Finish this
				break;

			default:
				System.out.println("Unrecognized message received from server.");
				return false;
		   }

    	return true;   	
    }
    
    
    @Override
    public String userName() {
    	return userName;
    }

	@Override
	public GameClient getGameClient() {
		// TODO Auto-generated method stub
		return this.gameClient;
	}

	@Override
	public BaseGameGUI getGameGUI() {
		// TODO Auto-generated method stub
		return  null;
	}

	@Override
	public void connect() {
		// TODO Auto-generated method stub
    	this.gameClient = new GameClient(userName, passwd, this);
	}

 
}//end of class
