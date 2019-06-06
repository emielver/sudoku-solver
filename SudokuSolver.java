/******************************************************************************
 * Authors:
 * ANONYMOUS
 * Emiel Verkade
 *
 *  SudokuSolver.java
 *  ~~~~Description~~~~
 *  This .java file executes the main processes of this program. It reads
 *  sudokus from given text files, proceeds to solve them through various
 *  methods, and then prints them out, alongside the statistics for
 *  each of the given sudokus. This .java file attempts to solve
 *  the sudokus at first with simple logic rules, then through a more
 *  generalized counting method, whilst finally attempting enumeration.
 ******************************************************************************/


import java.util.*;
import java.io.*;

public class SudokuSolver {

  public static void main(String args[]) throws java.io.FileNotFoundException {
    String filename = "Sudoku";
    String extension = ".txt";
    for (int i = 1; i<7; i++) {
      String half = filename.concat(Integer.toString(i));
      String file = half.concat(extension);
      solve(file);
    }
  }

  public static void solve(String filename) throws java.io.FileNotFoundException {

    // Statistics.
    int hintCount = 0;
    int generalHintsCount = 0;
    int branchLevel = 0;
    int ennumerationCount = 0;

    Sudoku.clearList();
    ArrayList<Sudoku> board = new ArrayList<Sudoku>(); // List that stores all Sudokus from branching.
    board.add(new Sudoku());

    // Read hints from specified file.
    Scanner input = new Scanner(new File(filename));
    int numberOfLines = input.nextInt();
    for (int i = 0; i < numberOfLines; i++) {
      Sudoku.addHint(new Cell(input.nextInt(), input.nextInt(), input.nextInt()));
    }
    input.close();

    int x = 0; // latest branch in tree
 int[] enumPos;
    double startTime = System.currentTimeMillis();
    while (!board.get(x).checkSudoku()) {

      // Simple Logic Rule.
      // With a loop that applies the Simple Logic Rule to every hint in list until the list is empty.
      while (Sudoku.sizeList() > 0) {
        Cell current = Sudoku.removeHint();
        board.get(x).simpleLogicRule(current);
        hintCount++;
      }

      // Generalization.
      // Supplies new hints to the list.
      board.get(x).general();
      generalHintsCount += Sudoku.sizeList();

      // Enumeration.
      // If generalization fails to supply hints then branching is applied.
      // It supplies the list with a guess and a new board.
      // If a branch fails to lead to a solution, then that branch is backtracked.
      if (Sudoku.sizeList() == 0) {
    enumPos = board.get(x).enumerationPossible();
        if (enumPos[0] != -1) {
          board.add(board.get(x).enumeration(enumPos));
          x++;
          ennumerationCount++;
        }
        else if (!board.get(x).checkSudoku()) {
          board.remove(x);
          x--;
        }
        else break;
      }

      if (x > branchLevel) branchLevel = x;

    }
    double endTime = System.currentTimeMillis();

    System.out.println("+-------+-------+-------+");
    System.out.println(String.format("| %-21s |", filename));
    board.get(x).printSudoku();
    System.out.println(String.format("| Solved: %-13s |", board.get(x).checkSudoku()));
    System.out.println(String.format("| Time: %-15s |", (endTime - startTime) + " ms"));
    System.out.println(String.format("| Total Hints: %-8s |", hintCount));
    System.out.println(String.format("| Gen. Hints: %-9s |", generalHintsCount));
    System.out.println(String.format("| Enum. #: %-12s |", ennumerationCount));
    System.out.println(String.format("| Branches: %-11s |", branchLevel));
    System.out.println("+-------+-------+-------+");

  }

}
