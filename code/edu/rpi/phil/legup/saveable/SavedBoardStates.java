package edu.rpi.phil.legup.saveable;

import edu.rpi.phil.legup.BoardState;

public class SavedBoardStates implements java.io.Serializable {
  public BoardState init;
  public BoardState curr;

  static final long serialVersionUID = 90019087116L;

  public SavedBoardStates(BoardState init, BoardState curr) {
    init = init;
    curr = curr;
  }
}
