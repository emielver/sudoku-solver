/******************************************************************************
  * Authors:
  * ANONYMOUS
  * Emiel Verkade
  *
  *  Sudoku.java
  *  ~~~~Description~~~~
  *  This .java file represent the object type Sudoku. Each sudoku contains
  *  a 9x9 matrix of objects of type Cell. Initially, these are all empty cells
  *  however as "hints" get read in from text files, we update the entries
  *  of the matrix accordingly. Afterwards, we proceed to solve the matrix
  *  through various methods outlined in this file, however executed in the
  *  SudokuSolver.java file. Here we outline the process for the simple logic
  *  rules, the generalization method and the enumeration method. Additionally,
  *  this .java file contains the hintList, which is a list of all of the hints
  *  which are at that point unprocessed. The methods required are also outlined.
  ******************************************************************************/

import java.util.*;

public class Sudoku {

  // List that stores unused hints. Can be accessed from outside this file.
  static ArrayList<Cell> hintList = new ArrayList<Cell>();

  // Adds a hint as a Cell to the list.
  public static void addHint(Cell hint) {
    hintList.add(hint);
  }

  // Removes and returns the first hint from the list.
  public static Cell removeHint() {
    return hintList.remove(0);
  }

  // Returns the size of the list.
  public static int sizeList() {
    return hintList.size();
  }

  // Clears all hints from the list.
  public static void clearList() {
    hintList.clear();
  }

  private Cell[][] matrix;

  // Constructor of a new unsolved Sudoku with new unsolved Cells.
  public Sudoku() {
    this.matrix = new Cell[9][9];
    for (int i = 0; i < 9; i++) {
      for (int j = 0; j < 9; j++) {
        matrix[i][j] = new Cell(i, j);
      }
    }
  }

  // Simple Logic Rule.
  // Removes candidate with hints value from Cells in the same row, column and block.
  public void simpleLogicRule(Cell hint) {
    int row = hint.getRow();
    int column = hint.getColumn();
    int value = hint.getValue();
    this.matrix[row][column] = new Cell(row, column, value);
    for (int i = 0; i < 9; i++) {
      if (!this.matrix[row][i].isSolved()) {
        if (this.matrix[row][i].removeCandidate(value)) {
          //
          hintList.add(new Cell(row, i, this.matrix[row][i].getValue()));
        }
      }
      if (!this.matrix[i][column].isSolved()) {
        if (this.matrix[i][column].removeCandidate(value)) {
          hintList.add(new Cell(i, column, this.matrix[i][column].getValue()));
        }
      }
    }
    for (int i = (row / 3) * 3; i < (row / 3) * 3 + 3; i++) {
      for (int j = (column / 3) * 3; j < (column / 3) * 3 + 3; j++) {
        if (!this.matrix[i][j].isSolved()) {
          if (this.matrix[i][j].removeCandidate(value)) {
            hintList.add(new Cell(i, j, this.matrix[i][j].getValue()));
          }
        }
      }
    }
  }

  // Generalization.
  // Finds single occurences of candidates in each row, column and block,
  // adding them to the list as hints and solving the Cell if it does.
  public void general() {
    int rowCount = 0;
    int colCount = 0;
    int blockCount = 0;

    int indexRow = 0;
    int indexColumn = 0;


    for (int number = 1; number < 10; number++) { // Loop through values from 1 to 9.
      rowCount = 0;
      colCount = 0;
      for (int i = 0; i < 9; i++) { // Loop through every row/column from 0 to 8.
        for (int j = 0; j < 9; j++) { // Loop through every element of row/column from 0 to 8.
          if (this.matrix[i][j].getCandidates().contains(number)) {
            rowCount++;
            if (rowCount == 1) indexColumn = j;
          }
          if (this.matrix[j][i].getCandidates().contains(number)) {
            colCount++;
            if (colCount == 1) indexRow = j;
          }
        }
        if (rowCount == 1) {
          if (!this.matrix[i][indexColumn].isSolved()) {
            this.matrix[i][indexColumn] = new Cell(i, indexColumn, number);
            Sudoku.addHint(this.matrix[i][indexColumn]);
          }
        }
        if (colCount == 1) {
          if (!this.matrix[indexRow][i].isSolved()) {
            this.matrix[indexRow][i] = new Cell (indexRow, i, number);
            Sudoku.addHint(this.matrix[indexRow][i]);
          }
        }
      }
      for (int r = 0; r < 7; r += 3) { // Loop through all 9 blocks.
        for (int c = 0; c < 7; c += 3) {
          blockCount = 0;
          for (int i = (r / 3) * 3; i < (r / 3) * 3 + 3; i++) { // Loop through all elements of a block.
            for (int j = (c / 3) * 3; j < (c / 3) * 3 + 3; j++) {
              if (this.matrix[i][j].getCandidates().contains(number)) {
                blockCount++;
                if (blockCount == 1) {
                  indexRow = i;
                  indexColumn = j;
                }
              }
            }
          }
          if (blockCount == 1) {
            if (!this.matrix[indexRow][indexColumn].isSolved()) {
              this.matrix[indexRow][indexColumn] = new Cell(indexRow, indexColumn, number);
              Sudoku.addHint(this.matrix[indexRow][indexColumn]);
            }
          }
        }
      }
    }
  }

  // Returns true if another branch is available.
  public int[] enumerationPossible() {
    int[] position = { -1, -1 };
    for (int i = 0; i < 9; i++) {
      for (int j = 0; j < 9; j++) {
        if (!this.matrix[i][j].isSolved()) {
          position[0] = i;
          position[1] = j;
        }
      }
    }
    return position;
  }

  // Enumeration.
  // Makes guesses and branches out in order to try and solve a Sudoku.
  // A guess is added as a hint to the list and a Sudoku with the guess is returned.
  // Also removes the guess from candidates in original Sudoku.
  public Sudoku enumeration(int[] position) {
    Sudoku sudokuCOPY = new Sudoku();
    int guessValue;
    ArrayList<Integer> guessList;
    if (!this.matrix[position[0]][position[1]].isSolved()) {
      guessList = this.matrix[position[0]][position[1]].getCandidates();
      guessValue = guessList.get(randomInt(0, guessList.size() - 1)); // Makes a random guess.
      //guessValue = guessList.get(0); // Takes as guess the first entry of the list.
      sudokuCOPY.matrixCopier(this.matrix);
      this.matrix[position[0]][position[1]].removeCandidate(guessValue);
      sudokuCOPY.matrix[position[0]][position[1]].setValue(guessValue);
      Sudoku.addHint(new Cell(position[0],
                              position[1],
                              sudokuCOPY.matrix[position[0]][position[1]].getCandidates()
                             ));
    }
    return sudokuCOPY;
  }

  public void matrixCopier(Cell[][] original) {
    for (int i = 0; i < 9; i++) {
      for (int j = 0; j < 9; j++) {
        this.matrix[i][j] = new Cell(i,
                                     j,
                                     original[i][j].getCandidates()
                                    );
      }
    }

  }

  // Returns true if all elements from every row, column and block add up to 45.
  public boolean checkSudoku() {
    for (int i = 0; i < 9; i++) {
      int countRow = 0;
      int countColumn = 0;
      for (int j = 0; j < 9; j++) {
        countRow += this.matrix[i][j].getValue();
        countColumn += this.matrix[j][i].getValue();
      }
      if ((countRow != 45) || (countColumn != 45)) return false;
    }
    for (int r = 0; r < 7; r += 3) {
      for (int c = 0; c < 7; c += 3) {
        int countBlock = 0;
        for (int i = (r / 3) * 3; i < (r / 3) * 3 + 3; i++) {
          for (int j = (c / 3) * 3; j < (c / 3) * 3 + 3; j++) {
            countBlock += this.matrix[i][j].getValue();
          }
        }
        if (countBlock != 45) return false;
      }
    }
 //
 int[] arrayRow;
 int[] arrayColumn;
 int[] arrayBlock;
 int value = 0;
 for (int i = 0; i<9; i++) {
 arrayRow = new int[9];
 arrayColumn = new int[9];
  for (int j = 0; j<9; j++) {
   value = this.matrix[i][j].getValue();
   arrayRow[value-1]++;
   if (arrayRow[value-1]!=1) {
    return false;
   }
   value = this.matrix[j][i].getValue();
   arrayColumn[value-1]++;
   if (arrayColumn[value-1]!=1) {
    return false;
   }
  }

 }
    for (int r = 0; r < 7; r += 3) {
      for (int c = 0; c < 7; c += 3) {
  arrayBlock = new int[9];
        for (int i = (r / 3) * 3; i < (r / 3) * 3 + 3; i++) {
          for (int j = (c / 3) * 3; j < (c / 3) * 3 + 3; j++) {
            value = this.matrix[i][j].getValue();
   arrayBlock[value-1]++;
   if (arrayBlock[value-1]!=1) {
    return false;
   }
          }
        }
      }
    }
    return true;
  }

  // Prints a specified Sudoku.
  public void printSudoku() {
    for (int i = 0; i < 9; i++) {
      if ((i == 0) || (i == 3) || (i == 6)) System.out.println("+-------+-------+-------+");
      for (int j = 0; j < 9; j++) {
        if ((j == 0) || (j == 3) || (j == 6)) System.out.print("| ");
        this.matrix[i][j].printValue();
        System.out.print(" ");
      }
      if ((i != 0) || (i != 3) || (i != 6)) System.out.print("|");
      System.out.println();
    }
    System.out.println("+-------+-------+-------+");
  }

  // Returns a random integer between a and b, a and b inclusive.
  public static int randomInt(int a, int b) {
    return a + (int) (Math.random() * (b - a + 1));
  }

}
