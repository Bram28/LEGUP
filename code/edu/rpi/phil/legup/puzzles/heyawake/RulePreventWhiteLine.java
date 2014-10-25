package edu.rpi.phil.legup.puzzles.heyawake;

import javax.swing.ImageIcon;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.PuzzleRule;

public class RulePreventWhiteLine extends PuzzleRule
{
	private static final long serialVersionUID = 407979836L;

	RulePreventWhiteLine()
	{
		setName("Prevent White Line");
		description = "Cells that will cause a white line across three regions must be black.";
		image = new ImageIcon("images/heyawake/rules/WhiteAroundBlack.png");
	}
	
	public String getImageName()
	{
		return "images/heyawake/rules/WhiteAroundBlack.png";
	}
	
	public void print()
	{
		System.out.print(getName());
	}
	protected String checkRuleRaw(BoardState destBoardState)
	{
		String error = null;
		boolean changed = false;
		BoardState origBoardState = destBoardState.getSingleParentState();
		BoardState altBoard = destBoardState.copy();
		String conError=null;
		ContradictionWhiteLine contradiction = new ContradictionWhiteLine();
		// Check for only one branch
		if (destBoardState.getTransitionsTo().size() != 1)
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
						//Use the ContradictionWhiteLine in order to determine if there would be a white line that crosses 3 regions if the
						//black cell was a white cell.
						
						changed = true;
						if (newState != Heyawake.CELL_BLACK || origState != Heyawake.CELL_UNKNOWN)
						{
							error = "This rule only involves adding black cells!";
							break;
						}
						else
						{
							altBoard.setCellContents(x,y,Heyawake.CELL_WHITE);
							conError = contradiction.checkContradictionRaw(altBoard);
							altBoard.setCellContents(x,y,Heyawake.CELL_BLACK);
							if(conError!=null)
							{
								error = "You must add a black cell to avoid a white line to use this rule!";
								break;
							}
						}
					}
				}
			}
			
			if (error == null && !changed)
			{
				error = "You must add a black cell to use this rule!";
			}
		}
		
		return error;
	}
	protected boolean doDefaultApplicationRaw(BoardState destBoardState)
	{
		BoardState origBoardState = destBoardState.getSingleParentState();
		boolean changed = false;
		String error = null;
		
		if (origBoardState != null && destBoardState.getTransitionsTo().size() == 1)
		{
			int width = destBoardState.getWidth();
			int height = destBoardState.getHeight();			
			
			for(int x = 0; x < width; x++)
			{
				for(int y = 0; y < height; y++)
				{
					if(destBoardState.getCellContents(x,y)==Heyawake.CELL_UNKNOWN)
					{
						//Try to make every unknown cell black.  If the cell doesn't cause an error than it must follow the rules, otherwise change it back.
						destBoardState.setCellContents(x,y,Heyawake.CELL_BLACK);
						error =checkRuleRaw(destBoardState);
						if(error !=null)
						{
							destBoardState.setCellContents(x,y,Heyawake.CELL_UNKNOWN);
						}
					}
					
				}
			}
			
		}
		error = checkRuleRaw(destBoardState);
			
		if (error == null)
		{
			changed = true;
			// valid change
		}
		if(!changed)
		{
			destBoardState = origBoardState.copy();
		}
			
		return changed;
	}
}
