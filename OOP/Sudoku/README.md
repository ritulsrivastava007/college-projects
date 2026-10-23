# 🎮 Sudoku Game in Java

A console-based Sudoku game developed using Java as part of my Object-Oriented Programming (OOP) practice.

## ✨ Features

* 9×9 Sudoku board with clearly separated 3×3 sections.
* Interactive gameplay through the console.
* Validates moves against Sudoku row, column, and box rules.
* Protects the original numbers provided in the puzzle.
* Allows up to **3 wrong attempts** before the game ends.
* Option to exit the game at any time.

## 🛠️ Technologies Used

* Java
* Scanner class for user input

## 📂 Project Structure

```text
Sudoku/
├── Main.java
├── SudokuBoard.java
├── SudokuGame.java
└── README.md
```

## ▶️ How to Run

Make sure Java and the Java compiler (`javac`) are installed.

1. Open a terminal in the `Sudoku` directory.

2. Compile the source files:

   ```bash
   javac Main.java SudokuBoard.java SudokuGame.java
   ```

3. Run the game:

   ```bash
   java Main
   ```

## 🎯 How to Play

* Enter three numbers separated by spaces: `row column number`.
* Rows, columns, and numbers must be between 1 and 9.
* Invalid moves and attempts to change original clues count as wrong attempts.
* The game ends after 3 wrong attempts.
* Enter `0 0 0` to exit.

## 🧠 Concepts Practiced

* Classes and objects
* Encapsulation
* Constructors and methods
* Two-dimensional arrays
* Conditional statements and loops
* User input and validation

## 👩‍💻 Author

**Ritul Srivastava**

[GitHub Profile](https://github.com/ritulsrivastava007)

*Learning by building, one project at a time.*
