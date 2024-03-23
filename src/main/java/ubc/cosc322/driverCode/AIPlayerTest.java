package ubc.cosc322.driverCode;

import java.util.*;

import com.beust.ah.A;
import ubc.cosc322.algorithms.MonteCarloTreeSearch;
import ubc.cosc322.algorithms.Node;
import ubc.cosc322.core.Board;
import ubc.cosc322.core.actionFactory.Action;
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
    public static int[][] mainBoardValues = new int[10][10]; // Assuming a 10x10 board
    private String userName;
    private String password;
    private String ourTeamColor = "";
    private String opponentTeamColor = "";
    private int playerNo;
    private boolean isAIPlayerWhite;

    private ArrayList<Integer> myCurrentPosition = new ArrayList<>();
    private ArrayList<Integer> myNextPosition = new ArrayList<>();
    private ArrayList<Integer> myNextArrowPosition = new ArrayList<>();
    MonteCarloTreeSearch mcts = new MonteCarloTreeSearch();
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
        setMainBoard(gameBoardState);
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
        System.out.println(ourTeamColor + " == " + "Black");
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
        updateMainBoard(currentPosition, nextPosition, arrowPosition);
        System.out.println("Board After Opponent's Move");
        printMainBoard();
        generateAndSendMove();
    }

    /**
     * Generates a random move for the AI and sends it to the server. This method
     * serves as a placeholder until a more sophisticated AI logic is implemented.
     */
    private void generateAndSendMove() {
        playerNo = Board.getBoardPlayerNo(isAIPlayerWhite);
        Board bestMove = mcts.findNextMove(getMainBoard(), playerNo);
        if(bestMove != null){
            System.out.println("success");
        } else {
            System.out.println("fail");
        }
        ArrayList<Integer> moveDetails = Board.extractMoveDetails(getMainBoard(),bestMove);

        if(moveDetails.isEmpty()){
            System.out.println("There are no moves for you to make. You lost.");
        } else{

            myCurrentPosition.clear();
            myNextPosition.clear();
            myNextArrowPosition.clear();
            System.out.println("Best Move Board moves successfully obtained");
            System.out.printf("Our Move: Queen from [%d, %d] to [%d, %d], Arrow shot to [%d, %d]%n",
                    moveDetails.get(0), moveDetails.get(1), moveDetails.get(2),
                    moveDetails.get(3), moveDetails.get(4), moveDetails.get(5));
            myCurrentPosition.add(moveDetails.get(0)); // X coordinate
            myCurrentPosition.add(moveDetails.get(1)); // Y coordinate

            myNextPosition.add(moveDetails.get(2)); // X coordinate
            myNextPosition.add(moveDetails.get(3)); // Y coordinate

            myNextArrowPosition.add(moveDetails.get(4)); // X coordinate
            myNextArrowPosition.add(moveDetails.get(5)); // Y coordinate
            System.out.println("moves added to positions");

            gameClient.sendMoveMessage(myCurrentPosition, myNextPosition, myNextArrowPosition);

            // we always need to update the game GUI and our internal board at the same time
            gameGui.updateGameState(myCurrentPosition, myNextPosition, myNextArrowPosition);
            updateMainBoard(myCurrentPosition, myNextPosition, myNextArrowPosition);
            System.out.println("moves sent to server");
            System.out.println("Board After Our Move");
            printMainBoard();
        }

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

    public static void setMainBoard(ArrayList<Integer> gameBoardState) {
        // Initialize a new array to hold the updated board state
        int[][] array = new int[10][10];
        int getVariable = 0;
        // Iterate over the adjusted board state to populate the new 2D array
        for(int i = 1; i < 11; i++){
            for(int j = 1; j < 11; j++){
                array[i-1][j-1] = gameBoardState.get(11*i + j);
            }
        }
        System.out.println(Arrays.deepToString(mainBoardValues));
        // Update mainBoardValues with the new 2D array
        mainBoardValues = array;
        System.out.println(Arrays.deepToString(mainBoardValues));
    }

    public static void updateMainBoard(ArrayList<Integer> currentPosition,
                                       ArrayList<Integer> nextPosition,
                                       ArrayList<Integer> arrowPosition) {
        int currentX = currentPosition.get(0) - 1;
        int currentY = currentPosition.get(1) - 1;
        int nextX = nextPosition.get(0) - 1;
        int nextY = nextPosition.get(1) - 1;
        int arrowX = arrowPosition.get(0) - 1;
        int arrowY = arrowPosition.get(1) - 1;
        //System.out.println("Debug: printing mainBoardValues in updateMainBoard method:");
        //System.out.println(Arrays.deepToString(mainBoardValues));
        // Move piece to new position
        //System.out.println("Debug: print mainBoardValues at currentX: "+(currentX+1)+" and currentY: "+(currentY+1));
        //System.out.println("Debug: mainBoardValues at currentX & currentY: "+mainBoardValues[currentX][currentY]);
        int player = mainBoardValues[currentX][currentY]; // Get the player number from the current position
        mainBoardValues[currentX][currentY] = 0; // Set current position to empty
        //System.out.println("Debug: print mainBoardValues at nextX: "+(nextX+1)+" and nextY: "+(nextY+1));
        mainBoardValues[nextX][nextY] = player; // Move the player piece to the next position
        //System.out.println("Debug: print mainBoardValues at arrowX: "+(arrowX+1)+" and currentY: "+(arrowY+1));
        // Place the arrow
        mainBoardValues[arrowX][arrowY] = 3;
    }

    public static Board getMainBoard(){
        Board board = new Board();
        board.setBoard(mainBoardValues);
        return board;
    }

    public void printMainBoard() {
        for (int i = 9; i > -1; i--) { // Iterate through each row
            for (int j = 0; j < 10; j++) { // Iterate through each column in the row
                System.out.print(mainBoardValues[i][j] + " "); // Print the value at the current position
            }
            System.out.println(); // Move to the next line after printing each row
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

