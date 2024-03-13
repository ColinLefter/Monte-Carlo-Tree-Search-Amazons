
package ubc.cosc322.driverCode;

import java.util.*;

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
public class AIPlayerTest extends GamePlayer {

    private GameClient gameClient = null;
    private BaseGameGUI gamegui = null;

    private String userName = "The player";
    private String passwd = "playerPass";
    private String ourTeam = "";
    private String theirTeam = "";

    ArrayList<Integer> myCurrentPosition = new ArrayList<>(Arrays.asList(1, 4));

    /**
     * The main method
     * @param args for name and passwd (current, any string would work)
     */
    public static void main(String[] args) {
        AIPlayerTest aiPlayer = new AIPlayerTest(args[0], args[1]);

        if (aiPlayer.getGameGUI() == null) {
            aiPlayer.Go();
        }
        else {
            BaseGameGUI.sys_setup();
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    aiPlayer.Go();
                }
            });
        }
    }

    /**
     * Any name and passwd 
     * @param userName
     * @param passwd
     */
    public AIPlayerTest(String userName, String passwd) {
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

                if(((String) msgDetails.get("player-white")).equals(this.userName())) {
                    System.out.println("Game State: " + msgDetails.get("player-white"));
                    ourTeam = "White Player: " + this.userName();
                    theirTeam = "Black Player: " + msgDetails.get("player-black");
                }
                else {
                    ourTeam = "Black Player: " + this.userName();
                    theirTeam = "White Player: " + msgDetails.get("player-white");
                }

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
                System.out.println("Below is player color");
                System.out.println(msgDetails.get(AmazonsGameMessage.PLAYER_WHITE));
                if(msgDetails.get(AmazonsGameMessage.PLAYER_WHITE).toString() == null){
                    System.out.println("The Opponent is Black");
                } else{
                    System.out.println("The Opponent is White");
                }
                System.out.println("Above is player color");

                gamegui.updateGameState(currentPosition,nextPosition,arrowPosition);

                // The server handles which player needs to make a move. If we reached this case, then it is our turn to make a move.
                Random random = new Random();

                System.out.println(nextPosition.get(0) + " " + nextPosition.get(1));

                ArrayList<Integer> myNextPosition = new ArrayList<>(Arrays.asList(random.nextInt(10) + 1, random.nextInt(10) + 1));
                ArrayList<Integer> myNextArrowPosition = new ArrayList<>(Arrays.asList(random.nextInt(10) + 1, random.nextInt(10) + 1));

                System.out.println(nextPosition.get(0) + " " + nextPosition.get(1));

                gameClient.sendMoveMessage(myCurrentPosition,myNextPosition,myNextArrowPosition);
                gamegui.updateGameState(myCurrentPosition,myNextPosition,myNextArrowPosition);
                handleGameMessage(GameMessage.GAME_STATE_BOARD, msgDetails);
                myCurrentPosition = myNextPosition; // the reason this only moves once is because once we move it once, it is no longer there
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
        gameClient = new GameClient(userName, passwd, this);
    }


}//end of class
