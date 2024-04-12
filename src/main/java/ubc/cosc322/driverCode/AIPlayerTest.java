package ubc.cosc322.driverCode;

import java.util.*;

import ubc.cosc322.algorithms.MonteCarloTreeSearch;
import ubc.cosc322.core.Board;
import ygraph.ai.smartfox.games.GameMessage;
import ygraph.ai.smartfox.games.BaseGameGUI;
import ygraph.ai.smartfox.games.GameClient;
import ygraph.ai.smartfox.games.GamePlayer;
import ygraph.ai.smartfox.games.amazons.AmazonsGameMessage;

/**
 * A heavily documented and refactored version of AIPlayerTest which aims to
 * improve clarity, structure, and efficiency. This class demonstrates a structured
 * approach to handling different types of game messages and making decisions based
 * on the game's current state.
 *
 * @author Yong Gao
 * @version Jan 5, 2021 - Refactored and documented
 */
public class AIPlayerTest extends GamePlayer {
    private GameClient gameClient = null;
    private BaseGameGUI gameGui = null;
    private String userName;
    private String password;
    private String ourTeamColor = "";
    private String opponentTeamColor = "";
    private int playerNo;
    private static boolean isAIPlayerWhite;

    private ArrayList<Integer> myCurrentPosition = new ArrayList<>();
    private ArrayList<Integer> myNextPosition = new ArrayList<>();
    private ArrayList<Integer> myNextArrowPosition = new ArrayList<>();
    MonteCarloTreeSearch mcts = new MonteCarloTreeSearch();
    private Board gameBoard = new Board();
    /**
     * The entry point for the AI player. Initializes the player and sets up the GUI
     * if necessary.
     *
     * @param args Command line arguments for username and password.
     */
    public static void main(String[] args) {
        AIPlayerTest aiPlayer = new AIPlayerTest(args[0], args[1]);

        if (aiPlayer.getGameGUI() == null) {
            aiPlayer.Go();
        }
        else {
            BaseGameGUI.sys_setup();
            java.awt.EventQueue.invokeLater(aiPlayer::Go);
        }
    }

    /**
     * Constructs an AIPlayerTest object with specified user credentials and initializes
     * the game GUI.
     *
     * @param userName The username for the player.
     * @param password The password for the player.
     */
    public AIPlayerTest(String userName, String password) {
        this.userName = userName;
        this.password = password;
        this.gameGui = new BaseGameGUI(this);
    }

    /**
     * Called upon successful login to the game server. Updates the game GUI with
     * room information.
     */
    @Override
    public void onLogin() {
        this.userName = gameClient.getUserName();
        if (gameGui != null) {
            gameGui.setRoomInformation(gameClient.getRoomList());
        }
    }

    /**
     * Handles various game messages from the server. Depending on the message type,
     * different actions are taken to update the game state or respond to game events.
     *
     * @param messageType The type of message received.
     * @param msgDetails Details of the message.
     * @return true if the message was handled successfully, false otherwise.
     */
    @Override
    public boolean handleGameMessage(String messageType, Map<String, Object> msgDetails) {
        //System.out.println("debug messagetype" + messageType);
        switch (messageType) {
            case GameMessage.GAME_STATE_BOARD:
                handleGameStateBoard(msgDetails);
                break;
            case GameMessage.GAME_ACTION_START:
                handleGameActionStart(msgDetails);
                break;
            case GameMessage.GAME_ACTION_MOVE:
                handleGameActionMove(msgDetails);
                break;
            default:
                System.out.println("Unrecognized message received from server.");
                return false;
        }
        return true;
    }

    /**
     * Handles the game state board message. Updates the game GUI with the new game
     * state.
     *
     * @param msgDetails Details of the game state board message.
     */
    private void handleGameStateBoard(Map<String, Object> msgDetails) {
        ArrayList<Integer> gameBoardState = (ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.GAME_STATE);
        gameGui.setGameState(gameBoardState);
        Board.setMainBoard(gameBoardState);
    }

    /**
     * Handles the game action start message. Determines the player's and opponent's
     * team colors based on the assignment in the message details.
     *
     * @param msgDetails Details of the game action start message.
     */
    private void handleGameActionStart(Map<String, Object> msgDetails) {
        if (msgDetails.get("player-white").equals(userName)) {
            isAIPlayerWhite = true;
            ourTeamColor = "White";
            opponentTeamColor = "Black";
        } else {
            isAIPlayerWhite = false;
            ourTeamColor = "Black";
            opponentTeamColor = "White";
        }
        System.out.println("Are we white? " + isAIPlayerWhite);
        if (ourTeamColor.equals("Black")) {
            generateAndSendMove(); // we make the first move
        }
        System.out.println("Our team: " + ourTeamColor + " | Opponent team: " + opponentTeamColor);
    }

    /**
     * Handles the game action move message. Updates the game state with the opponent's
     * last move and calculates the AI's next move.
     *
     * @param msgDetails Details of the game action move message.
     */
    private void handleGameActionMove(Map<String, Object> msgDetails) {
        ArrayList<Integer> currentPosition = (ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.QUEEN_POS_CURR);
        ArrayList<Integer> nextPosition = (ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.QUEEN_POS_NEXT);
        ArrayList<Integer> arrowPosition = (ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.ARROW_POS);

        System.out.println(String.format("Opponent's Move: Queen from [%d, %d] to [%d, %d], Arrow shot to [%d, %d]",
                currentPosition.get(0), currentPosition.get(1), nextPosition.get(0), nextPosition.get(1),
                arrowPosition.get(0), arrowPosition.get(1)));

        gameGui.updateGameState(currentPosition, nextPosition, arrowPosition);
        gameBoard.updateMainBoard(currentPosition, nextPosition, arrowPosition);
        System.out.println("Board After Opponent's Move");
        gameBoard.printMainBoard();
        generateAndSendMove();
    }

    /**
     * Generates a random move for the AI and sends it to the server. This method
     * serves as a placeholder until a more sophisticated AI logic is implemented.
     */
    private void generateAndSendMove() {
        playerNo = Board.getBoardPlayerNo(isAIPlayerWhite);
        System.out.println("PlayerNo: " + playerNo);
        Board bestMove = mcts.findNextMove(Board.getMainBoard(), playerNo);
        ArrayList<Integer> moveDetails = Board.extractMoveDetails(Board.getMainBoard(), bestMove);
        if (moveDetails.isEmpty()) {
            System.out.println("There are no moves for you to make. You lost.");
        } else {
            myCurrentPosition.clear();
            myNextPosition.clear();
            myNextArrowPosition.clear();
            System.out.printf("Our Move: Queen from [%d, %d] to [%d, %d], Arrow shot to [%d, %d]%n",
                    moveDetails.get(0), moveDetails.get(1), moveDetails.get(2),
                    moveDetails.get(3), moveDetails.get(4), moveDetails.get(5));
            myCurrentPosition.add(moveDetails.get(0)); // X coordinate
            myCurrentPosition.add(moveDetails.get(1)); // Y coordinate

            myNextPosition.add(moveDetails.get(2)); // X coordinate
            myNextPosition.add(moveDetails.get(3)); // Y coordinate

            myNextArrowPosition.add(moveDetails.get(4)); // X coordinate
            myNextArrowPosition.add(moveDetails.get(5)); // Y coordinate

            gameClient.sendMoveMessage(myCurrentPosition, myNextPosition, myNextArrowPosition);

            // we always need to update the game GUI and our internal board at the same time
            gameGui.updateGameState(myCurrentPosition, myNextPosition, myNextArrowPosition);
            Board.updateMainBoard(myCurrentPosition, myNextPosition, myNextArrowPosition);
            System.out.println("Board After Our Move");
            gameBoard.printMainBoard();
        }
    }

    @Override
    public String userName() {
        return this.userName;
    }

    @Override
    public GameClient getGameClient() {
        return this.gameClient;
    }

    @Override
    public BaseGameGUI getGameGUI() {
        return this.gameGui;
    }

    /**
     * Establishes a connection to the game server using the provided user credentials.
     */
    @Override
    public void connect() {
        this.gameClient = new GameClient(userName, password, this);
    }

    /**
     * Initializes the game client and connects to the server.
     */
    private void go() {
        connect();
    }
}

