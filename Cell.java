/******************************************************************************
  * Authors: 
  * ANONYMOUS
  * Emiel Verkade
  *
  *  Cell.java
  *  ~~~~Description~~~~
  *  This .java file is the API for the object of type Cell. It outlines the
  *  methods and data contained in a Cell object. Every entry in the sudoku
  *  matrix is therefore a Cell, however when printing the sudoku we only look
  *  at the value. This is a relatively straightforward API, and all methods
  *  have additional comments clarifying their processes and purpose.
  *****************************************************************************/

import java.util.*;

public class Cell {

  private int row;
  private int column;
  private ArrayList<Integer> candidates;

  // Constructor for new unsolved Cell.
  public Cell(int row, int column) {
    this.row = row;
    this.column = column;
    this.candidates = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
  }

  // Constructor for solved Cell.
  public Cell(int row, int column, int value) {
    this.row = row;
    this.column = column;
    this.candidates = new ArrayList<Integer>(Arrays.asList(value));
  }

  // Constructor for existing Cell.
  public Cell(int row, int column, ArrayList<Integer> candidates) {
    this.row = row;
    this.column = column;
    this.candidates = new ArrayList<Integer>();
    for (int i = 0; i < candidates.size(); i++) {
      this.candidates.add(candidates.get(i));
    }
  }

  // Returns the row of a Cell.
  public int getRow() {
    return this.row;
  }

  // Return the column of a Cell.
  public int getColumn() {
    return this.column;
  }

  // Returns the value of a Cell, returns 0 if the Cell is unsolved.
  public int getValue() {
    if (this.isSolved()) return this.candidates.get(0);
    return 0;
  }

  // Prints the value of a Cell, prints "." if the Cell is unsolved.
  public void printValue() {
    int value = this.getValue();
    if (value == 0) System.out.print(".");
    else System.out.print(value);
  }

  // Set value of a solved Cell, also changes status to true.
  public void setValue(int value) {
    this.candidates = new ArrayList<Integer>(Arrays.asList(value));
  }

  // Returns the candidates list of a Cell, can also be used as hard copy.
  public ArrayList<Integer> getCandidates() {
    ArrayList<Integer> copy = new ArrayList<Integer>();
    for (int i = 0; i < this.candidates.size(); i++) {
      copy.add(this.candidates.get(i));
    }
    return copy;
  }

  // Removes a candidate from a Cell, also returns true if the Cell is solved after removing.
  public boolean removeCandidate(int c) {
    int index = this.candidates.indexOf(c);
    if (!this.isSolved() && (index >= 0)) {
      this.candidates.remove(this.candidates.indexOf(c));
    }
    return this.isSolved();
  }

  // Returns true & sets status to true if the Cell is solved, otherwise returns false.
  public boolean isSolved() {
    return this.candidates.size() == 1;
  }

  // Removes a candidate from a Cell, also returns true if the Cell is solved after removing.
  public boolean removeCandidate(Cell c) {
    int index = this.candidates.indexOf(c.getValue());
    // since index will be -1 if the object does not exist
    if (!this.isSolved() && (index != -1)) {
      this.candidates.remove(index);
    }
    return this.isSolved();
  }

}
