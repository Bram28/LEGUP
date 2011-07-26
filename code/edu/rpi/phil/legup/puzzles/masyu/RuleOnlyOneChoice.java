package edu.rpi.phil.legup.puzzles.masyu;

import javax.swing.ImageIcon;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.PuzzleRule;
import java.util.ArrayList;
import java.awt.Point;


public class RuleOnlyOneChoice extends PuzzleRule{
	
	public RuleOnlyOneChoice() {
		setName("Only Choice");
		description = "Must go in the only direction available.";
		image = new ImageIcon("images/masyu/Rules/RuleOnlyOneChoice.png");
	}
	/**
	 * Checks to see if the rule was correctly applied For this rule, for each
	 * added there must be a line on the other side
	 * 
	 * @param state
	 *			The board state
	 * @return null if the contradiction was applied correctly, the error String
	 *		 otherwise
	 */
	protected String checkRuleRaw(BoardState destBoardState)
	{
		String error = null;
		boolean changed = false;
		BoardState origBoardState = destBoardState.getSingleParentState();
		
		return error;
	}


	/**
	 * Tries to apply the rule everywhere and returns true if it can do so.
	 * 
	 * @author Bryan
	 * @param destBoardState
	 *			the board to work with
	 * @param pm
	 *			the puzzle module
	 * @see edu.rpi.phil.legup.PuzzleRule#doDefaultApplicationRaw(edu.rpi.phil.legup.BoardState,
	 *	  edu.rpi.phil.legup.PuzzleModule)
	 */
	protected boolean doDefaultApplicationRaw(BoardState destBoardState)
	{
		BoardState origBoardState = destBoardState.getSingleParentState();
		boolean changed = false;
		int width = destBoardState.getWidth();
		int height = destBoardState.getHeight();
		ArrayList<Object> destExtraData = destBoardState.getExtraData();
		Point[][] destLineData =(Point[][]) destExtraData.get(0);
		ArrayList<Object> origExtraData = origBoardState.getExtraData();
		Point[][] origLineData =(Point[][]) origExtraData.get(0);
		for (int y = 0; y < height;y++)
		{
			for (int x = 0; x < width; x++)
			{
				int possibleDirections = 0;
				for(int dx = -1;dx<2;dx++)
				{
					for(int dy = -1; dy<2;dy++)
					{
						if(dx!=0 && dy!=0)
							continue;
						if(dx+x<0 || dx+x>=width)
							continue;
						if(dy+y<0 || dy+y>=height)
							continue;
						if(dx==0 && dy==0)
							continue;
						possibleDirections++;
					}
				}
			}
		}

		return changed;
	}
}

