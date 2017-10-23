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
        int blanks = 0;
        int blackCells = 0;
        boolean[][] filledBlack = new boolean[state.getHeight()][state.getWidth()];
        // Fillapix.determineLight(state, litup); not sure what this line should do

        //System.out.println("Too Few Black Cells started");
        for(int x = 0; x < width; ++x) {
            for(int y = 0; y < height; ++y) {
                //System.out.println("Checking " + x + ", " +y);
                cellvalue = state.getCellContents(x,y);
                if(cellvalue >= 10  && cellvalue < 15)
                {
                    blanks = 0;
                    blackCells = 0;
                    if(x > 0) {
    					// if((Fillapix.isBlack(state.getCellContents(x-1,y))) || ((!litup[y][x-1]) && (Fillapix.isUnknown(state.getCellContents(x-1,y))))) {
                        if((Fillapix.isBlack(state.getCellContents(x-1,y))) || (Fillapix.isUnknown(state.getCellContents(x-1,y)))) {
                            blackCells++;
                        }
                    } else
                        ++blanks;

                    if(x < width - 1) {
    					// if((state.getCellContents(x+1,y) == LightUp.CELL_LIGHT) || ((!litup[y][x+1]) && (state.getCellContents(x+1,y) == LightUp.CELL_UNKNOWN))) {
                        if((Fillapix.isWhite(state.getCellContents(x+1,y))) || ((Fillapix.isUnknown(state.getCellContents(x+1,y))))) {
                            blackCells++;
                        }
                    } else
                        ++blanks;

                    if(y > 0) {
    					// if((state.getCellContents(x,y-1) == LightUp.CELL_LIGHT) || ((!litup[y-1][x]) && (state.getCellContents(x,y-1) == LightUp.CELL_UNKNOWN))) {
                        if((Fillapix.isBlack(state.getCellContents(x,y-1))) || (Fillapix.isUnknown(state.getCellContents(x,y-1)))) {
                                blackCells++;
                        }
                    } else
                        ++blanks;

                    if(y < height - 1)
                    {
    					// if((state.getCellContents(x,y+1) == LightUp.CELL_LIGHT) || ((!litup[y+1][x]) && (state.getCellContents(x,y+1) == LightUp.CELL_UNKNOWN))) {
                        if((Fillapix.isBlack(state.getCellContents(x,y+1))) || ((Fillapix.isUnknown(state.getCellContents(x,y+1))))) {
                            blackCells++;
                        }
                    } else {
                        ++blanks;
                    }
                    //if(blanks > 4 - (cellvalue - 10)) {
                    if (blackCells < (cellvalue%10)) {
    					/*
    					System.out.println("Cell " + x + ", " + y + " has too few lights!");
    					System.out.println(lights + " lights and " + (cellvalue - 10) + " needed ");
    					try {
    						java.lang.Thread.sleep(5000);
    					}
    					catch (Exception e) {
    						System.err.println("Thread sleep failed!");
    					}
    					*/
                        return null;
                    }
                }
                //System.out.println("Cell " + x + ", " + y + " is fine");
            }
        }

        error = "No block with too few black cells exists.";
        //System.out.println("Too Few Black Cells finished without error");
        return error;
    }
}