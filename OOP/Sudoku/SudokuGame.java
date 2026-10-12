
import java.util.Scanner;

public class SudokuGame {

    private SudokuBoard board;
    private Scanner scanner;

    public SudokuGame() {
        board = new SudokuBoard();
        scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println("Welcome to Sudoku!");
        System.out.println("Enter row, column and number (1-9).");
        System.out.println("Enter 0 0 0 to exit.");

        int wrongAttempts = 0;

        while (true) {
            board.displayBoard();

            if (board.isComplete()) {
                System.out.println("Congratulations! Puzzle completed!");
                break;
            }

            System.out.print("Your move: ");

            int row = scanner.nextInt();
            int col = scanner.nextInt();
            int num = scanner.nextInt();

            if (row == 0 && col == 0 && num == 0) {
                System.out.println("Thanks for playing!");
                break;
            }

            if (row < 1 || row > 9 || col < 1 || col > 9
                    || num < 1 || num > 9) {
                wrongAttempts++;
                System.out.println("Invalid input! Wrong attempts: "
                        + wrongAttempts + "/3");
            } else {
                row--;
                col--;

                if (board.isOriginalCell(row, col)) {
                    wrongAttempts++;
                    System.out.println("You cannot change an original clue! "
                            + "Wrong attempts: " + wrongAttempts + "/3");
                } else if (!board.isValidMove(row, col, num)) {
                    wrongAttempts++;
                    System.out.println("Invalid move! Wrong attempts: "
                            + wrongAttempts + "/3");
                } else {
                    board.setCell(row, col, num);
                    System.out.println("Move accepted!");
                }
            }

            if (wrongAttempts >= 3) {
                System.out.println("Game Over! You made 3 wrong attempts.");
                break;
            }
        }

        scanner.close();
    }
}