package edu.rpi.phil.legup.puzzles.nurikabe;

import javax.swing.ImageIcon;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.Contradiction;
import edu.rpi.phil.legup.puzzles.nurikabe.Nurikabe;

public class ContradictionBlackArea extends Contradiction
{	 
    private static final long serialVersionUID = 450786104L;
	
    
	ContradictionBlackArea()
	{
		setName("Black Must Connect");
		description = "All black cells must be connected.";
		image = new ImageIcon("images/nurikabe/contradictions/BlackArea.png");
	}
		
	public String getImageName()
	{
		return "images/nurikabe/contradictions/BlackArea.png";
	}
	 
	 /**
	  * Recursively examines connected black squares, marking as checked as it goes along
	  * 
	  * @param checked array of checked squares, all false initially
	  * @param x current position
	  * @param y current position
	  * @param state Board state
	  * @param first True if first call of this function
	  * @return true if touching unknown, false if already checked, or surrounded by checked or white
	  */
	 
	 private boolean contradictionCheckerHelper(boolean checked[][], BoardState state, int x, int y, boolean first){
		 if(!first && !checked[x][y]){
			 return false;
		 }else{
			 checked[x][y] = true;
		 }
		 if(state.getCellContents(x, y) == Nurikabe.CELL_UNKNOWN){
			 return true;
		 }
		 boolean surrounded = false;
		 int around = 0;
		 if(x + 1 < state.getWidth() && !checked[x+1][y] && state.getCellContents(x+1, y) != Nurikabe.CELL_WHITE){
			 surrounded = surrounded || contradictionCheckerHelper(checked,state,x+1, y, first);
		 }else{ 
			 around++;
		 }
		 if(y + 1 < state.getHeight() && !checked[x][y+1] && state.getCellContents(x, y+1) != Nurikabe.CELL_WHITE){
			 surrounded = surrounded || contradictionCheckerHelper(checked,state,x, y+1, first);
		 }else{
			 around++;
		 }
		 if(x - 1 > 0 && !checked[x-1][y] && state.getCellContents(x-1, y) != Nurikabe.CELL_WHITE){
			 surrounded = surrounded || contradictionCheckerHelper(checked,state,x-1, y, first);
		 }else{
			 around++;
		 }	
		 if(y - 1 > 0 && !checked[x][y-1] && state.getCellContents(x+1, y-1) != Nurikabe.CELL_WHITE){
			 surrounded = surrounded || contradictionCheckerHelper(checked,state,x, y-1, first);
		 }else{
			 around++;
		 }
		 if(around == 4){
			 return false;
		 }else{
			 return surrounded;
		 }

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
    	boolean first = true;
    	boolean checked[][] = new boolean[height][width];
    	for(int i = 0; i < width; i++){
    		for(int j = 0; j < height; j++){
    			//recursive wrapper
    			if(state.getCellContents(i, j) == Nurikabe.CELL_BLACK){
    				if(!first && !checked[i][j]) return null;
        			contradictionCheckerHelper(checked, state, i, j, first);
        			first = false;
    			}
    		}
    	}
        return "ERROR";
    }
}