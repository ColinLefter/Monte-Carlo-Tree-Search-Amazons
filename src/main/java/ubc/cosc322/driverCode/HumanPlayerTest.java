
package ubc.cosc322.driverCode;

import java.util.*;

import ygraph.ai.smartfox.games.GameMessage;
import ygraph.ai.smartfox.games.BaseGameGUI;
import ygraph.ai.smartfox.games.GameClient;
import ygraph.ai.smartfox.games.GamePlayer;
import ygraph.ai.smartfox.games.amazons.AmazonsGameMessage;
import ygraph.ai.smartfox.games.amazons.HumanPlayer;


/**
 * An example illustrating how to implement a GamePlayer
 * @author Yong Gao (yong.gao@ubc.ca)
 * Jan 5, 2021
 *
 */
public class HumanPlayerTest extends GamePlayer {

    private GameClient gameClient = null;
    private BaseGameGUI gamegui = null;

    private String userName = "The player";
    private String passwd = "playerPass";
    private Boolean playerIsBlack = false;


    /**
     * The main method
     * @param args for name and passwd (current, any string would work)
     */
    public static void main(String[] args) {
        HumanPlayer humanPlayer = new HumanPlayer();

        if (humanPlayer.getGameGUI() == null) {
            humanPlayer.Go();
        }
        else {
            BaseGameGUI.sys_setup();
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    humanPlayer.Go();
                }
            });
        }
    }

    /**
     * Any name and passwd 
     * @param userName
     * @param passwd
     */
    public HumanPlayerTest(String userName, String passwd) {
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
        //This method will be called by the GameClient when it receives a game-related message
        //from the server.

        //For a detailed description of the message types and format,
        //see the method GamePlayer.handleGameMessage() in the game-client-api document.
        switch (messageType) {
            case GameMessage.GAME_STATE_BOARD:
                Object check = msgDetails.get(AmazonsGameMessage.GAME_STATE);
                ArrayList<Integer> gameBoardState = (ArrayList<Integer>) check;

                //set game state
                gamegui.setGameState(gameBoardState);
                break;

            case GameMessage.GAME_ACTION_START:
                //handleGameMessage(GameMessage.GAME_STATE_BOARD, msgDetails); // Just use the last case
                // TODO: Store player names as necessary
                String playerNameBlack = (String) msgDetails.get(AmazonsGameMessage.PLAYER_BLACK); // black is always the human player (or other AI)
                String playerNameWhite = (String) msgDetails.get(AmazonsGameMessage.PLAYER_WHITE); // we are white

                //Change playerIsBlack variable here
                System.out.println("Player names: ");
                System.out.printf("Black: %s \n", playerNameBlack);
                System.out.printf("White: %s \n", playerNameWhite);
                // the server automatically handles which player's turn it is, so we don't need to update the playerIsBlack variable
                break;

            case GameMessage.GAME_ACTION_MOVE:
                // Update game state

                // Storing queen and arrow positions of previous move
                ArrayList<Integer> currentPosition = (ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.QUEEN_POS_CURR);
                ArrayList<Integer> nextPosition = (ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.QUEEN_POS_NEXT);
                ArrayList<Integer> arrowPosition = (ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.ARROW_POS);

                gamegui.updateGameState(msgDetails);
                gamegui.updateGameState(currentPosition,nextPosition,arrowPosition);
                gameClient.sendMoveMessage(currentPosition,nextPosition,arrowPosition);
                // After updated game state calculate your move and send your move to the server using the method GameClient.sendMoveMessage(...)
                break;

            default:
                System.out.println("Unrecognized message received from server.");
                return false;
        }

        return true;
    }

    public void sendMoveMessage() {

    }

    @Override
    public String userName() {
        return userName;
    }

    @Override
    public GameClient getGameClient() {
        return this.gameClient;
    }

    @Override
    public BaseGameGUI getGameGUI() {
        return  gamegui;
    }

    @Override
    public void connect() {
        gameClient = new GameClient(userName, passwd, this);
    }


}//end of class