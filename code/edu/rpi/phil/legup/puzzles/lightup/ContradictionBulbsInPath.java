package edu.rpi.phil.legup.puzzles.lightup;

import javax.swing.ImageIcon;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.Contradiction;

public class ContradictionBulbsInPath extends Contradiction
{
	static final long serialVersionUID = 4189821508780095618L;
	public String getImageName() {return "images/lightup/contradictions/BulbsInPath.png";}
	 ContradictionBulbsInPath()
	 {
		setName("Bulbs Light Each Other");
		description = "A bulb cannot be placed in another's light path.";
		//image = new ImageIcon("images/lightup/contradictions/BulbsInPath.png");
	 }
	 
	 /**
     * Checks if the contradiction was applied correctly to this board state
     *
     * @param state The board state
     * @return null if the contradiction was applied correctly, the error String otherwise
     */
    public String checkContradictionRaw(BoardState state)
    {
    	String error = null;
    	int height = state.getHeight();
    	int width = state.getWidth();
    	int cellvalue = 0;

    	for(int x = 0; x < width; ++x)
    	{
    		for(int y = 0; y < height; ++y)
    		{
    			cellvalue = state.getCellContents(x,y);
    			if(cellvalue == LightUp.CELL_LIGHT)
    			{
    				if(searchPath(state,x,y,width,height))
    					return null;
    			}
    		}
    	}
    	
    	error = "No bulb in another's light.";

		return error;
    }
    
    private boolean searchPath(BoardState state, int x, int y, int width, int height)
    {
    	if(x > 0)
		{
			for(int tempx = x - 1; tempx >= 0; --tempx)
			{
				if(state.getCellContents(tempx,y) > 2 )
					break;
				else if(state.getCellContents(tempx,y) == LightUp.CELL_LIGHT)
					return true;
			}
		}
		
		if(x < width-1)
		{
			for(int tempx = x+1; tempx < width; ++tempx)
			{
				if(state.getCellContents(tempx,y) > 2 )
					break;
				else if(state.getCellContents(tempx,y) == LightUp.CELL_LIGHT)
					return true;
			}
		}
		
		if(y > 0)
		{
			for(int tempy = y-1; tempy >= 0; --tempy)
			{
				if(state.getCellContents(x,tempy) > 2 )
					break;
				else if(state.getCellContents(x,tempy) == LightUp.CELL_LIGHT)
					return true;
			}
		}
		
		if(y < height-1)
		{
			for(int tempy = y + 1; tempy < height; ++tempy)
			{
				if(state.getCellContents(x,tempy) > 2 )
					break;
				else if(state.getCellContents(x,tempy) == LightUp.CELL_LIGHT)
					return true;
			}
		}
		
		return false;
    }
}
