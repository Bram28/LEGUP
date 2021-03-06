package edu.rpi.phil.legup.puzzles.treetent;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.Contradiction;

public class ContradictionTooFewTents extends Contradiction
{
	static final long serialVersionUID = 9508L;
	public String getImageName() {return "images/treetent/too_few_tents.png";}
	public ContradictionTooFewTents()
	 {
		setName("Too Few Tents");
		description = "Rows and columns cannot have fewer tents than their clue.";
	 }


	private int countRow(int y, BoardState state, int type) {
		int width = state.getWidth();
		int count = 0;

		for (int x=0;x<width;x++) {
			if (state.getCellContents(x,y) == type)
        ++count;
	  }

		return count;
	}

	private int countCol(int x, BoardState state, int type) {
		int height = state.getHeight();
		int count = 0;

		for (int y=0;y<height;y++) {
			if (state.getCellContents(x,y) == type)
  			++count;
	  }

		return count;
	}

	private int getColNum(int x, BoardState state) {
		int label = state.getLabel(BoardState.LABEL_BOTTOM, x);
		return TreeTent.translateNumTents(label);
	}

	private int getRowNum(int y, BoardState state) {
		int label = state.getLabel(BoardState.LABEL_RIGHT, y);
		return TreeTent.translateNumTents(label);
	}

  /**
  * Checks if the contradiction was applied correctly to this board state
  *
  * @param state The board state
  * @return null if the contradiction was applied correctly, the error String otherwise
  */
  public String checkContradictionRaw(BoardState state)
  {
    int height = state.getHeight();
    int width = state.getWidth();

    // Check all rows for the correct amount of tents
    for (int y=0; y<height; y++) {
      int num = getRowNum(y,state);
      int unknowns = countRow(y,state,TreeTent.CELL_UNKNOWN);
      int tents = countRow(y,state,TreeTent.CELL_TENT);

      if (num > (tents + unknowns))
        return null;
    }

    // Check all columns for the correct amount of tents
    for (int x=0; x<width; x++) {
      int num = getColNum(x,state);
      int unknowns = countCol(x,state,TreeTent.CELL_UNKNOWN);
      int tents = countCol(x,state,TreeTent.CELL_TENT);

      if (num > (tents + unknowns))
        return null;
    }

    return "All rows and columns can still have sufficient tents.";
  }
}
