package ubc.cosc322.driverCode;

import java.util.*;

import ygraph.ai.smartfox.games.GameMessage;
import ygraph.ai.smartfox.games.BaseGameGUI;
import ygraph.ai.smartfox.games.GameClient;
import ygraph.ai.smartfox.games.GamePlayer;
import ygraph.ai.smartfox.games.amazons.AmazonsGameMessage;
import ygraph.ai.smartfox.games.amazons.HumanPlayer;

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

    private String userName = "The player";
    private String password = "playerPass";
    private String ourTeamColor = "";
    private String opponentTeamColor = "";

    private ArrayList<Integer> myCurrentPosition = new ArrayList<>(Arrays.asList(1, 4));

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
    }

    /**
     * Handles the game action start message. Determines the player's and opponent's
     * team colors based on the assignment in the message details.
     *
     * @param msgDetails Details of the game action start message.
     */
    private void handleGameActionStart(Map<String, Object> msgDetails) {
        if (msgDetails.get("player-white").equals(this.userName)) {
            this.ourTeamColor = "White";
            this.opponentTeamColor = "Black";
        } else {
            this.ourTeamColor = "Black";
            this.opponentTeamColor = "White";
        }
        System.out.println("Our team: " + this.ourTeamColor + " | Opponent team: " + this.opponentTeamColor);
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

        gameGui.updateGameState(currentPosition, nextPosition, arrowPosition);
        generateAndSendMove();
    }

    /**
     * Generates a random move for the AI and sends it to the server. This method
     * serves as a placeholder until a more sophisticated AI logic is implemented.
     */
    private void generateAndSendMove() {
        Random random = new Random();
        ArrayList<Integer> myNextPosition = generateRandomPosition(random);
        ArrayList<Integer> myNextArrowPosition = generateRandomPosition(random);

        gameClient.sendMoveMessage(myCurrentPosition, myNextPosition, myNextArrowPosition);
        gameGui.updateGameState(myCurrentPosition, myNextPosition, myNextArrowPosition);
        myCurrentPosition = myNextPosition;
    }

    /**
     * Generates a random position on the board.
     *
     * @param random Random generator to use for position generation.
     * @return A random board position.
     */
    private ArrayList<Integer> generateRandomPosition(Random random) {
        return new ArrayList<>(Arrays.asList(random.nextInt(10) + 1, random.nextInt(10) + 1));
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