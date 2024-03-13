
package ubc.cosc322.driverCode;

import java.util.*;

import ubc.cosc322.core.actionFactory.Action;
import ubc.cosc322.core.actionFactory.ActionFactory;
import ygraph.ai.smartfox.games.GameMessage;
import ygraph.ai.smartfox.games.BaseGameGUI;
import ygraph.ai.smartfox.games.GameClient;
import ygraph.ai.smartfox.games.GamePlayer;
import ygraph.ai.smartfox.games.amazons.AmazonsGameMessage;


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
	private Boolean playerIsBlack = false;

	private ActionFactory a = new ActionFactory();
	
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
    	this.gamegui = new BaseGameGUI(this);
    }

    @Override
	public void onLogin() {
		userName = gameClient.getUserName();
		if (gamegui != null) {
			gamegui.setRoomInformation(gameClient.getRoomList());
		}
	}

    @Override
    public boolean handleGameMessage(String messageType, Map<String, Object> msgDetails) {
		System.out.println(messageType);
		System.out.println(msgDetails);

    	//This method will be called by the GameClient when it receives a game-related message
    	//from the server.
	
    	//For a detailed description of the message types and format, 
    	//see the method GamePlayer.handleGameMessage() in the game-client-api document. 

		   switch(messageType) {
			case GameMessage.GAME_STATE_BOARD:
				Object check = msgDetails.get(AmazonsGameMessage.GAME_STATE);
				ArrayList<Integer> gameBoardState = (ArrayList<Integer>) check;

				//set game state
				gamegui.setGameState(gameBoardState);
				ArrayList<Action> moves = a.getActions(gameBoardState, 2);
				break;

			case GameMessage.GAME_ACTION_START:
				//handleGameMessage(GameMessage.GAME_STATE_BOARD, msgDetails); // Just use the last case
				String playerNameBlack, playerNameWhite;
				playerNameBlack = (String) msgDetails.get(AmazonsGameMessage.PLAYER_BLACK);
				playerNameWhite = (String) msgDetails.get(AmazonsGameMessage.PLAYER_WHITE);

				//Change playerIsBlack variable here

				// TODO: Store player names as necessary

				break;

			case GameMessage.GAME_ACTION_MOVE:
				// Update game state

				gamegui.updateGameState(msgDetails);
				// Storing queen and arrow positions of previous move
				ArrayList<Integer> currentPositionResult = (ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.QUEEN_POS_CURR);
				ArrayList<Integer> nextPositionResult = (ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.QUEEN_POS_NEXT);
				ArrayList<Integer> arrowPositionResult = (ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.ARROW_POS);

				System.out.println(playerIsBlack);

				//Check if player is black, and only set currentPos to one of the queens that is ours
				if(playerIsBlack){

				}else{

				}

				Random random = new Random();

				ArrayList<Integer> currentPos = new ArrayList<>();
				ArrayList<Integer> nextPosition = new ArrayList<>();
				ArrayList<Integer> arrowPosition = new ArrayList<>();

				currentPos.add(1);
				currentPos.add(4);

				nextPosition.add(random.nextInt(10) + 1);
				nextPosition.add(random.nextInt(10) + 1);

				arrowPosition.add(random.nextInt(10) + 1);
				arrowPosition.add(random.nextInt(10) + 1);

				System.out.println(currentPos);
				System.out.println(nextPosition);
				System.out.println(arrowPosition);

				gameClient.sendMoveMessage(currentPos,nextPosition,arrowPosition);
				// After updated game state calculate your move and send your move to the server using the method GameClient.sendMoveMessage(...)
				break;

			default:
				System.out.println("Unrecognized message received from server.");
				return false;
		   }

    	return true;   	
    }

	public void sendMoveMessage() {
//		// TODO Compute the move and send a message to the server
//		int newYPos = (int) (Math.random() * 10);
//		int newXPos = (int) (Math.random() * 10);
//
//		int newXArrowPos = (int) (Math.random() * 10);
//		int newYArrowPos = (int) (Math.random() * 10);
//
//		queenPosNew.set(0, newXPos);
//		queenPosNew.set(1, newYPos);
//
//		arrowPos.set(0, newXArrowPos);
//		arrowPos.set(1, newYArrowPos);
//		// we have a 30s time limit, so we must send a move by then


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
		return  gamegui;
	}

	@Override
	public void connect() {
		// TODO Auto-generated method stub
    	this.gameClient = new GameClient(userName, passwd, this);
	}

 
}//end of class
