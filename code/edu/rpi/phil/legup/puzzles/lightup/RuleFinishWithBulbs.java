package edu.rpi.phil.legup.puzzles.lightup;

import java.awt.Point;
import java.util.Vector;

import javax.swing.ImageIcon;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.PuzzleModule;
import edu.rpi.phil.legup.PuzzleRule;
import edu.rpi.phil.legup.RuleApplication;

public class RuleFinishWithBulbs extends PuzzleRule
{
	static final long serialVersionUID = 5613497586353427743L;
	public String getImageName() {return "images/lightup/rules/SurroundBulbs.png";}
	 RuleFinishWithBulbs()
	 {
		 setName("Finish with Bulbs");
		 description = "The remaining unknowns around a block must be bulbs to satisfy the number.";
		 //image = new ImageIcon("images/lightup/rules/SurroundBulbs.png");
	 }
	
	 /**
     * Checks if the contradiction was applied correctly to this board state
     *
     * @param state The board state
     * @return null if the contradiction was applied correctly, the error String otherwise
     *
	 protected String checkRuleRaw(BoardState destBoardState)
    {
    	String error = null;
    	boolean changed = false;
    	BoardState origBoardState = destBoardState.getSingleParentState();
    	int width = destBoardState.getWidth();
    	int height = destBoardState.getHeight();
    	boolean foundvalidblock;
    	boolean[][] litup = new boolean[origBoardState.getHeight()][origBoardState.getWidth()];
    	LightUp.determineLight(origBoardState, litup);

    	// Check for only one branch
		if (destBoardState.getParents().size() != 1)
		{
			error = "This rule only involves having a single branch!";
		}
		else
		{
			for (int y = 0; y < origBoardState.getHeight() && error == null; ++y)
			{
				for (int x = 0; x < origBoardState.getWidth(); ++x)
				{
					int origState = origBoardState.getCellContents(x,y);
					int newState = destBoardState.getCellContents(x,y);

					if (origState != newState)
					{
						changed = true;

						if (newState != LightUp.CELL_LIGHT || origState != LightUp.CELL_UNKNOWN)
						{
							error = "This rule only involves adding light bulbs!";
							break;
						}
						foundvalidblock = false;
						if(x > 0)
							if(destBoardState.getCellContents(x-1,y)>= 10 && destBoardState.getCellContents(x-1,y)< 15)
								foundvalidblock = foundvalidblock | checkBlockCompleted(destBoardState,x-1,y,width,height,litup);
						if(x < width - 1)
							if(destBoardState.getCellContents(x+1,y)>= 10 && destBoardState.getCellContents(x+1,y)< 15)
								foundvalidblock = foundvalidblock | checkBlockCompleted(destBoardState,x+1,y,width,height,litup);
						if(y > 0)
							if(destBoardState.getCellContents(x,y-1)>= 10 && destBoardState.getCellContents(x,y-1)< 15)
								foundvalidblock = foundvalidblock | checkBlockCompleted(destBoardState,x,y-1,width,height,litup);
						if(y < height - 1)
							if(destBoardState.getCellContents(x,y+1)>= 10 && destBoardState.getCellContents(x,y+1)< 15)
								foundvalidblock = foundvalidblock | checkBlockCompleted(destBoardState,x,y+1,width,height,litup);

						if(!foundvalidblock)
						{
							error = "A bulb cell must be placed to complete a block's number.";
							break;
						}



					}
				}
			}

			if (error == null && !changed)
			{
				error = "You must add a bulb to use this rule!";
			}
		}

		return error;
    }

	 protected boolean doDefaultApplicationRaw(BoardState destBoardState, PuzzleModule pm)
	  {
		 BoardState origBoardState = destBoardState.getSingleParentState();
	    	boolean changed = false;
	    	int width = destBoardState.getWidth();
	    	int height = destBoardState.getHeight();
	    	int cellvalue = 0;
	    	boolean[][] litup = new boolean[origBoardState.getHeight()][origBoardState.getWidth()];
	    	LightUp.determineLight(origBoardState, litup);

	    	if (origBoardState != null && destBoardState.getParents().size() == 1)
	    	{
	    		for (int y = 0; y < origBoardState.getHeight(); ++y)
				{
					for (int x = 0; x < origBoardState.getWidth(); ++x)
					{
						cellvalue = destBoardState.getCellContents(x,y);
						if(cellvalue >= 10 && cellvalue < 15)
						{
							if(checkBlockCompletable(destBoardState,x,y,width,height,litup))
							{
								completeBlock(destBoardState,x,y,width,height, litup);
							}
						}
					}
				}

		    	String error = checkRuleRaw(destBoardState);

				if (error == null)
				{
					changed = true;
					// valid change
				}
	    	}

	    	if(!changed)
	    	{
	    		destBoardState = origBoardState.copy();
	    	}

	    	LightUp.fillLight(destBoardState);

		    return changed;
	  }*/

	 protected RuleApplication canApplyAt(BoardState state, Point at)
	 {
		 boolean[][] litup = new boolean[state.getHeight()][state.getWidth()];
	    LightUp.determineLight(state, litup);

		 if(state.getCellContents(at.x, at.y) != PuzzleModule.CELL_UNKNOWN || litup[at.y][at.x])
			 return new RuleApplication(at);

		 Vector<Point> numbers = LightUp.findAdjacentNumbers(state, at);

		 boolean found = false;
		 for(Point p : numbers)
		 {
			 if(checkBlockCompletable(state, p.x, p.y, state.getWidth(), state.getHeight(), litup))
			 {
				 RuleApplication ret = new RuleApplication(at);
				 ret.parentApplication = p;
				 ret.newValue = LightUp.CELL_LIGHT;
				 ret.isValid = true;
				 return ret;
			 }
		 }

		 return new RuleApplication(at);
		//TODO: Add parent semantics
	 }

	 private boolean checkBlockCompleted(BoardState destBoardState, int x, int y, int width, int height, boolean[][] litup)
	 {

			int bulbs = 0;
			int blanks = 0;
			if(x > 0)
			{
				if(destBoardState.getCellContents(x-1, y) == LightUp.CELL_LIGHT)
					++bulbs;
				if(destBoardState.getCellContents(x-1, y) == LightUp.CELL_EMPTY || (litup[y][x-1] && destBoardState.getCellContents(x-1, y) == LightUp.CELL_UNKNOWN) || destBoardState.getCellContents(x-1, y) >= 10)
					++blanks;
			}
			else
				++blanks;
			if(x < width - 1)
			{
				if(destBoardState.getCellContents(x+1, y) == LightUp.CELL_LIGHT)
					++bulbs;
				if(destBoardState.getCellContents(x+1, y) == LightUp.CELL_EMPTY|| (litup[y][x+1] && destBoardState.getCellContents(x+1, y) == LightUp.CELL_UNKNOWN)|| destBoardState.getCellContents(x+1, y) >= 10)
					++blanks;
			}
			else
				++blanks;
			if(y > 0)
			{
				if(destBoardState.getCellContents(x, y-1) == LightUp.CELL_LIGHT)
					++bulbs;
				if(destBoardState.getCellContents(x, y-1) == LightUp.CELL_EMPTY|| (litup[y-1][x] && destBoardState.getCellContents(x, y-1) == LightUp.CELL_UNKNOWN)|| destBoardState.getCellContents(x, y-1) >= 10)
					++blanks;
			}
			else
				++blanks;
			if(y < height - 1)
			{
				if(destBoardState.getCellContents(x, y+1) == LightUp.CELL_LIGHT)
					++bulbs;
				if(destBoardState.getCellContents(x, y+1) == LightUp.CELL_EMPTY|| (litup[y+1][x] && destBoardState.getCellContents(x, y+1) == LightUp.CELL_UNKNOWN)|| destBoardState.getCellContents(x, y+1) >= 10)
					++blanks;
			}
			else
				++blanks;


		 return ((bulbs + blanks == 4) && (bulbs == destBoardState.getCellContents(x, y)-10));
	 }

	 private boolean checkBlockCompletable(BoardState destBoardState, int x, int y, int width, int height, boolean[][] litup)
	 {

		 int bulbs = 0;
			int blanks = 0;
			if(x > 0)
			{
				if(destBoardState.getCellContents(x-1, y) == LightUp.CELL_LIGHT)
					++bulbs;
				if(destBoardState.getCellContents(x-1, y) == LightUp.CELL_EMPTY || (litup[y][x-1] && destBoardState.getCellContents(x-1, y) == LightUp.CELL_UNKNOWN) || destBoardState.getCellContents(x-1, y) >= 10)
					++blanks;
			}
			else
				++blanks;
			if(x < width - 1)
			{
				if(destBoardState.getCellContents(x+1, y) == LightUp.CELL_LIGHT)
					++bulbs;
				if(destBoardState.getCellContents(x+1, y) == LightUp.CELL_EMPTY|| (litup[y][x+1] && destBoardState.getCellContents(x+1, y) == LightUp.CELL_UNKNOWN)|| destBoardState.getCellContents(x+1, y) >= 10)
					++blanks;
			}
			else
				++blanks;
			if(y > 0)
			{
				if(destBoardState.getCellContents(x, y-1) == LightUp.CELL_LIGHT)
					++bulbs;
				if(destBoardState.getCellContents(x, y-1) == LightUp.CELL_EMPTY|| (litup[y-1][x] && destBoardState.getCellContents(x, y-1) == LightUp.CELL_UNKNOWN)|| destBoardState.getCellContents(x, y-1) >= 10)
					++blanks;
			}
			else
				++blanks;
			if(y < height - 1)
			{
				if(destBoardState.getCellContents(x, y+1) == LightUp.CELL_LIGHT)
					++bulbs;
				if(destBoardState.getCellContents(x, y+1) == LightUp.CELL_EMPTY|| (litup[y+1][x] && destBoardState.getCellContents(x, y+1) == LightUp.CELL_UNKNOWN)|| destBoardState.getCellContents(x, y+1) >= 10)
					++blanks;
			}
			else
				++blanks;


		 return (4 - blanks - bulbs == destBoardState.getCellContents(x, y)-10 - bulbs);
	 }

	 private void completeBlock(BoardState destBoardState, int x, int y, int width, int height, boolean[][] litup)
	 {

			if(x > 0)
			{
				if(destBoardState.getCellContents(x-1, y) == LightUp.CELL_UNKNOWN && !litup[y][x-1] )
					destBoardState.setCellContents(x-1, y, LightUp.CELL_LIGHT);
			}
			if(x < width - 1)
			{
				if(destBoardState.getCellContents(x+1, y) == LightUp.CELL_UNKNOWN && !litup[y][x+1])
					destBoardState.setCellContents(x+1, y, LightUp.CELL_LIGHT);
			}
			if(y > 0)
			{
				if(destBoardState.getCellContents(x, y-1) == LightUp.CELL_UNKNOWN && !litup[y-1][x])
					destBoardState.setCellContents(x, y-1, LightUp.CELL_LIGHT);
			}
			if(y < height - 1)
			{
				if(destBoardState.getCellContents(x, y+1) == LightUp.CELL_UNKNOWN && !litup[y+1][x])
					destBoardState.setCellContents(x, y+1, LightUp.CELL_LIGHT);
			}
	 }

}
