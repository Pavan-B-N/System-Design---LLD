// system design for a Tic Tac Toe game using Low-Level Design (LLD) in Java

// Step 1: Requirements Analysis
/*
    Before jumping into the code, we clarify the game’s expectations.

    Functional Requirements:
        3x3 board
        Two players (initially both human)
        Players alternate turns
        Validate moves (prevent overwriting)
        Detect win or draw conditions

    Non-Functional Requirements:
        Extensibility (support AI later)
        Clean code, separation of concerns
        Easy to test and debug
*/

// Step2: Identify Core Components
/*
    1. Board
    Represents the 3x3 grid, tracks moves and checks win/draw.

    2. Symbol (Enum)
    Represents X, O, or EMPTY. Prevents use of raw strings or chars.

    3. Player
    Encapsulates symbol and move strategy (Human or AI).

    4. PlayerStrategy (Interface)
    Enables strategy pattern. Human and AI both implement this.

    5. GameStatus (Enum)
    Helps track the game state: IN_PROGRESS, WIN, or DRAW.

    6. Position
    Encapsulates a cell (row, col) on the board.

    7. BoardGame (Interface)
    Abstraction for future games like Snake, Chess, etc.

    8. TicTacToeGame (Concrete class)
    Handles game flow, switching players, and ending game.
 */

// LLD: Tic Tac Toe Game
// Full Implementation with Strategy Pattern, Extensible Design, and Clean Structure

import java.util.*;

// Enum for Cell Symbols, in future it can be extensible to add more players and their symbol
enum Symbol {
    X, O, EMPTY
}

// Enum for Game Status, in future we can add Pause so extensible
enum GameStatus {
    IN_PROGRESS, WIN, DRAW
}

// Represents a cell position
class Position {
    public int row, col;
    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }
}

// Strategy Interface
interface PlayerStrategy {
    Position makeMove(Board board);
}

// Human Strategy Implementation
class HumanPlayerStrategy implements PlayerStrategy {
    private final Scanner scanner;
    private final String playerName;

    public HumanPlayerStrategy(String name) {
        this.playerName = name;
        scanner= new Scanner(System.in);
    }

    @Override
    public Position makeMove(Board board) {
        while (true) {
            try {
                System.out.printf("%s, enter your move (row and col): ", playerName);
                int row = scanner.nextInt();
                int col = scanner.nextInt();
                Position pos = new Position(row, col);
                if (board.isValidMove(pos)) return pos;
                System.out.println("Invalid move. Cell occupied or out of bounds.");
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter integers.");
                scanner.nextLine();
            }
        }
    }
}

// Player Class
class Player {
    private final Symbol symbol;
    private final PlayerStrategy strategy;

    public Player(Symbol symbol, PlayerStrategy strategy) {
        this.symbol = symbol;
        this.strategy = strategy;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public PlayerStrategy getStrategy() {
        return strategy;
    }
}

// Board Class
class Board {
    private final Symbol[][] grid;
    private final int size;

    public Board(int size) {
        this.size = size;
        this.grid = new Symbol[size][size];
        for (int i = 0; i < size; i++)
            Arrays.fill(grid[i], Symbol.EMPTY);
    }

    public boolean isValidMove(Position pos) {
        return pos.row >= 0 && pos.col >= 0 &&
               pos.row < size && pos.col < size &&
               grid[pos.row][pos.col] == Symbol.EMPTY;
    }

    public void placeSymbol(Position pos, Symbol symbol) {
        grid[pos.row][pos.col] = symbol;
    }

    public boolean isFull() {
        for (Symbol[] row : grid)
            for (Symbol cell : row)
                if (cell == Symbol.EMPTY)
                    return false;
        return true;
    }

    public boolean hasWinner(Symbol symbol) {
        // Check rows and cols
        for (int i = 0; i < size; i++) {
            if (checkLine(symbol, i, 0, 0, 1)) return true; // row
            if (checkLine(symbol, 0, i, 1, 0)) return true; // col
        }
        // Diagonals
        return checkLine(symbol, 0, 0, 1, 1) || checkLine(symbol, 0, size - 1, 1, -1);
    }

    private boolean checkLine(Symbol sym, int row, int col, int dRow, int dCol) {
        for (int i = 0; i < size; i++) {
            if (grid[row][col] != sym) return false;
            row += dRow;
            col += dCol;
        }
        return true;
    }

    public void printBoard() {
        for (Symbol[] row : grid) {
            for (Symbol cell : row) {
                System.out.print((cell == Symbol.EMPTY ? "." : cell) + " ");
                // System.out.print(cell+" ");
            }
            System.out.println();
        }
    }
}

// Interface for a generic board game
interface BoardGame {
    void play();
}

// Main Game Class
public class TicTacToeGame implements BoardGame {
    private final Board board;
    private final Player playerX;
    private final Player playerO;
    private Player currentPlayer;
    private GameStatus gameStatus;

    public TicTacToeGame(PlayerStrategy xStrategy, PlayerStrategy oStrategy, int size) {
        this.board = new Board(size);
        this.playerX = new Player(Symbol.X, xStrategy);
        this.playerO = new Player(Symbol.O, oStrategy);
        this.currentPlayer = playerX;
        this.gameStatus = GameStatus.IN_PROGRESS;
    }

    private void switchPlayer() {
        currentPlayer = (currentPlayer == playerX) ? playerO : playerX;
    }

    @Override
    public void play() {
        while (gameStatus == GameStatus.IN_PROGRESS) {
            board.printBoard();
            Position move = currentPlayer.getStrategy().makeMove(board);
            board.placeSymbol(move, currentPlayer.getSymbol());

            if (board.hasWinner(currentPlayer.getSymbol())) {
                gameStatus = GameStatus.WIN;
                board.printBoard();
                System.out.println("Player " + currentPlayer.getSymbol() + " wins!");
            } else if (board.isFull()) {
                gameStatus = GameStatus.DRAW;
                board.printBoard();
                System.out.println("It's a draw!");
            } else {
                switchPlayer();
            }
        }
    }

    // Entry Point
    public static void main(String[] args) {
        PlayerStrategy xStrategy = new HumanPlayerStrategy("Player X");
        PlayerStrategy oStrategy = new HumanPlayerStrategy("Player O");
        BoardGame game = new TicTacToeGame(xStrategy, oStrategy, 3);
        game.play();
    }
}