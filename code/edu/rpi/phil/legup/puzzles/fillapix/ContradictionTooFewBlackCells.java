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
        int whiteCells = 0;
        int unknownCells = 0;
        for(int x = 0; x < width; ++x) {
            for(int y = 0; y < height; ++y) {
                cellvalue = state.getCellContents(x,y);
                // cell with a clue
                if(Fillapix.hasClue(cellvalue)) {
                    whiteCells = Fillapix.getNumWhiteCells(x,y,state);
                    unknownCells = Fillapix.getNumUnknownCells(x,y,state);
                    if ((cellvalue%10) > ((9 - whiteCells))) { //- unknownCells)) {
                        System.out.println("Too few!!");
                        return null;
                    }
                }
            }
        } // 9 - white - unknown = black     ...     9 - white - unknown > black
        // number of black cells should be equal to the clue, so 9 - white - unknown = clue

        error = "No block with too few black cells exists.";
        return error;
    }
}