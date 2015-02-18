package edu.rpi.phil.legup.puzzles.lightup;

import javax.swing.ImageIcon;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.PuzzleRule;

public class RuleFinishWithWhite extends PuzzleRule
{
	static final long serialVersionUID = 2828176895339413023L;
	public String getImageName() {return "images/lightup/rules/SurroundWhite.png";}
	 RuleFinishWithWhite()
	 {
		setName("Finish with White");
		description = "The remaining unknowns around a block must be white if the number is satisfied.";
		//image = new ImageIcon("images/lightup/rules/SurroundWhite.png");
	 }

	 /**
     * Checks if the contradiction was applied correctly to this board state
     *
     * @param state The board state
     * @return null if the contradiction was applied correctly, the error String otherwise
     */
	 protected String checkRuleRaw(BoardState destBoardState)
    {
    	String error = null;
    	boolean changed = false;
    	BoardState origBoardState = destBoardState.getSingleParentState();
    	int width = destBoardState.getWidth();
    	int height = destBoardState.getHeight();
    	boolean foundvalidblock;

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

						if (newState != LightUp.CELL_EMPTY || origState != LightUp.CELL_UNKNOWN)
						{
							error = "This rule only involves adding white cells!";
							break;
						}

						foundvalidblock = false;
						if(x > 0)
							if(destBoardState.getCellContents(x-1,y)>= 10 && destBoardState.getCellContents(x-1,y)< 15)
								foundvalidblock = foundvalidblock | checkBlockCompleted(destBoardState,x-1,y,width,height);
						if(x < width - 1)
							if(destBoardState.getCellContents(x+1,y)>= 10 && destBoardState.getCellContents(x+1,y)< 15)
								foundvalidblock = foundvalidblock | checkBlockCompleted(destBoardState,x+1,y,width,height);
						if(y > 0)
							if(destBoardState.getCellContents(x,y-1)>= 10 && destBoardState.getCellContents(x,y-1)< 15)
								foundvalidblock = foundvalidblock | checkBlockCompleted(destBoardState,x,y-1,width,height);
						if(y < height - 1)
							if(destBoardState.getCellContents(x,y+1)>= 10 && destBoardState.getCellContents(x,y+1)< 15)
								foundvalidblock = foundvalidblock | checkBlockCompleted(destBoardState,x,y+1,width,height);

						if(!foundvalidblock)
						{
							error = "A white cell must be placed to complete a block's number.";
							break;
						}



					}
				}
			}

			if (error == null && !changed)
			{
				error = "You must add a white cell to use this rule!";
			}
		}

		return error;
    }

	 protected boolean doDefaultApplicationRaw(BoardState destBoardState)
	  {
		 BoardState origBoardState = destBoardState.getSingleParentState();
	    	boolean changed = false;
	    	int width = destBoardState.getWidth();
	    	int height = destBoardState.getHeight();
	    	int cellvalue = 0;

	    	if (origBoardState != null && destBoardState.getParents().size() == 1)
	    	{
	    		for (int y = 0; y < origBoardState.getHeight(); ++y)
				{
					for (int x = 0; x < origBoardState.getWidth(); ++x)
					{
						cellvalue = destBoardState.getCellContents(x,y);
						if(cellvalue >= 10 && cellvalue < 15)
						{
							if(checkBlockCompletable(destBoardState,x,y,width,height))
							{
								completeBlock(destBoardState,x,y,width,height);
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

		    return changed;
	  }

	 private boolean checkBlockCompleted(BoardState destBoardState, int x, int y, int width, int height)
	 {

			int bulbs = 0;
			int blanks = 0;
			if(x > 0)
			{
				if(destBoardState.getCellContents(x-1, y) == LightUp.CELL_LIGHT)
					++bulbs;
				if(destBoardState.getCellContents(x-1, y) == LightUp.CELL_EMPTY|| destBoardState.getCellContents(x-1, y) >= 10)
					++blanks;
			}
			else
				++blanks;
			if(x < width - 1)
			{
				if(destBoardState.getCellContents(x+1, y) == LightUp.CELL_LIGHT)
					++bulbs;
				if(destBoardState.getCellContents(x+1, y) == LightUp.CELL_EMPTY|| destBoardState.getCellContents(x+1, y) >= 10)
					++blanks;
			}
			else
				++blanks;
			if(y > 0)
			{
				if(destBoardState.getCellContents(x, y-1) == LightUp.CELL_LIGHT)
					++bulbs;
				if(destBoardState.getCellContents(x, y-1) == LightUp.CELL_EMPTY|| destBoardState.getCellContents(x, y-1) >= 10)
					++blanks;
			}
			else
				++blanks;
			if(y < height - 1)
			{
				if(destBoardState.getCellContents(x, y+1) == LightUp.CELL_LIGHT)
					++bulbs;
				if(destBoardState.getCellContents(x, y+1) == LightUp.CELL_EMPTY|| destBoardState.getCellContents(x, y+1) >= 10)
					++blanks;
			}
			else
				++blanks;


		 return ((bulbs + blanks <= 4) && (bulbs == destBoardState.getCellContents(x, y)-10));
	 }

	 private boolean checkBlockCompletable(BoardState destBoardState, int x, int y, int width, int height)
	 {

			int bulbs = 0;
			int blanks = 0;
			if(x > 0)
			{
				if(destBoardState.getCellContents(x-1, y) == LightUp.CELL_LIGHT)
					++bulbs;
				if(destBoardState.getCellContents(x-1, y) == LightUp.CELL_EMPTY || destBoardState.getCellContents(x-1, y) >= 10)
					++blanks;
			}
			else
				++blanks;
			if(x < width - 1)
			{
				if(destBoardState.getCellContents(x+1, y) == LightUp.CELL_LIGHT)
					++bulbs;
				if(destBoardState.getCellContents(x+1, y) == LightUp.CELL_EMPTY|| destBoardState.getCellContents(x+1, y) >= 10)
					++blanks;
			}
			else
				++blanks;
			if(y > 0)
			{
				if(destBoardState.getCellContents(x, y-1) == LightUp.CELL_LIGHT)
					++bulbs;
				if(destBoardState.getCellContents(x, y-1) == LightUp.CELL_EMPTY|| destBoardState.getCellContents(x, y-1) >= 10)
					++blanks;
			}
			else
				++blanks;
			if(y < height - 1)
			{
				if(destBoardState.getCellContents(x, y+1) == LightUp.CELL_LIGHT)
					++bulbs;
				if(destBoardState.getCellContents(x, y+1) == LightUp.CELL_EMPTY|| destBoardState.getCellContents(x, y+1) >= 10)
					++blanks;
			}
			else
				++blanks;


		 return (4 - blanks - bulbs == 4 - (destBoardState.getCellContents(x, y)-10) - blanks);
	 }

	 private void completeBlock(BoardState destBoardState, int x, int y, int width, int height)
	 {

			if(x > 0)
			{
				if(destBoardState.getCellContents(x-1, y) == LightUp.CELL_UNKNOWN)
					destBoardState.setCellContents(x-1, y, LightUp.CELL_EMPTY);
			}
			if(x < width - 1)
			{
				if(destBoardState.getCellContents(x+1, y) == LightUp.CELL_UNKNOWN)
					destBoardState.setCellContents(x+1, y, LightUp.CELL_EMPTY);
			}
			if(y > 0)
			{
				if(destBoardState.getCellContents(x, y-1) == LightUp.CELL_UNKNOWN)
					destBoardState.setCellContents(x, y-1, LightUp.CELL_EMPTY);
			}
			if(y < height - 1)
			{
				if(destBoardState.getCellContents(x, y+1) == LightUp.CELL_UNKNOWN)
					destBoardState.setCellContents(x, y+1, LightUp.CELL_EMPTY);
			}
	 }

}
