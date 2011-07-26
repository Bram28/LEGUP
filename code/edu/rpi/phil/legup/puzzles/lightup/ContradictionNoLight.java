package edu.rpi.phil.legup.puzzles.lightup;

import java.awt.Point;
import java.util.ArrayList;

import javax.swing.ImageIcon;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.Contradiction;

public class ContradictionNoLight extends Contradiction
{	 
	 ContradictionNoLight()
	 {
		setName("Can't Light A Cell");
		description = "All cells must be able to be lit.";
		image = new ImageIcon("images/lightup/contradictions/NoLight.png");
	 }
	 
	 /**
     * Checks if the contradiction was applied correctly to this board state
     *
     * @param state The board state
     * @return null if the contradiction was applied correctly, the error String otherwise
     */
    protected String checkContradictionRaw(BoardState state)
    {
    	String error = null;
    	int height = state.getHeight();
    	int width = state.getWidth();
    	int cellvalue = 0;
    	
    	boolean[][] litup = new boolean[height][width];
    	
		ArrayList<Object> extra = state.getExtraData();
		for(int cnt = 0; cnt < extra.size(); ++cnt)
		{
			litup[((Point)extra.get(cnt)).y][ ((Point)extra.get(cnt)).x] = true;
		}

    	for(int x = 0; x < width; ++x)
    	{
    		for(int y = 0; y < height; ++y)
    		{
    			cellvalue = state.getCellContents(x,y);
    			if(cellvalue == LightUp.CELL_BLANK && !litup[y][x])
    			{
    				if(!searchPath(state,x,y,width,height, litup))
    					return null;
    			}
    		}
    	}
    	
    	error = "All cells can be lit.";

		return error;
    }
    
    private boolean searchPath(BoardState state, int x, int y, int width, int height, boolean[][] litup)
    {
    	if(x > 0)
		{
			for(int tempx = x - 1; tempx >= 0; --tempx)
			{
				if(state.getCellContents(tempx,y) > 2 )
					break;
				else if(state.getCellContents(tempx,y) == LightUp.CELL_LIGHT)
					return true;
				else if(state.getCellContents(tempx,y) == LightUp.CELL_UNKNOWN && !litup[y][tempx])
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
				else if(state.getCellContents(tempx,y) == LightUp.CELL_UNKNOWN && !litup[y][tempx])
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
				else if(state.getCellContents(x,tempy) == LightUp.CELL_UNKNOWN && !litup[tempy][x])
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
				else if(state.getCellContents(x,tempy) == LightUp.CELL_UNKNOWN && !litup[tempy][x])
					return true;
			}
		}
		
		return false;
    }
}
