package edu.rpi.phil.legup.puzzles.fillapix;

import javax.swing.ImageIcon;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.Contradiction;

public class ContradictionTooFewBlackCells extends Contradiction {
    private static final long serialVersionUID = 855439484L;

    ContradictionTooFewBlackCells() {
        setName("Too Few Black Cells");
        description = "There may not be fewer black cells than the number.";
        image = new ImageIcon("images/fillapix/contradictions/TooFewBlackCells.png");
    }

    public String getImageName()
    {
        return "images/fillapix/contradictions/TooFewBlackCells.png";
    }

    /**
     * Checks if the contradiction was applied correctly to this board state
     *
     * @param state The board state
     * @return null if the contradiction was applied correctly, the error String otherwise
     */
    public String checkContradictionRaw(BoardState state)
    {
        String error = "";
        int height = state.getHeight();
        int width = state.getWidth();
        int cellvalue = 0;
        int unknownCells = 0;
        int blackCells = 0;
        int whiteCells = 0;
        int blockSize = 9;
        boolean blockWithTooFewBlackCellsExists = false;

        for(int x = 0; x < width; ++x) {
            for(int y = 0; y < height; ++y) {
                cellvalue = state.getCellContents(x,y);
                // cell with a clue
                if(Fillapix.hasClue(cellvalue))
                {
                    whiteCells = 0;
                    blackCells = 0;
                    unknownCells = 0;
                    for (int i = -1; i < 2; ++i) {
                        for (int j = -1; j < 2; ++j) {
                            int xpos = x + i;
                            int ypos = y + j;
                            if (Fillapix.inBounds(width, height, xpos, ypos)) {
                                if (Fillapix.isWhite(cellvalue)) {
                                    whiteCells += 1;
                                }
                                if (Fillapix.isUnknown(cellvalue)) {
                                    unknownCells += 1;
                                }
                                if (Fillapix.isBlack(cellvalue)) {
                                    blackCells += 1;
                                }
                            }
                        }
                    }

                    // if ((9-whiteCells) < (cellvalue%10)) { // doesn't account for the corner case of border cells!
                    if ((blackCells+unknownCells) < (cellvalue%10)) {
                        System.err.println("LEFT SIDE, WHITE CELLS: "+(9-whiteCells)+"   RIGHT SIDE: "+(cellvalue%10));
                        System.err.println("LEFT SIDE, OTHER CELLS: "+(blackCells+unknownCells)+"   RIGHT SIDE: "+(cellvalue%10));
                        blockWithTooFewBlackCellsExists = true;
                    } else {
                        System.err.println("It never entered...");
                    }
                }
            }
        }

        if (blockWithTooFewBlackCellsExists) {
            return null;
        }

        error = "No block with too few black cells exists.";
        return error;
    }
}