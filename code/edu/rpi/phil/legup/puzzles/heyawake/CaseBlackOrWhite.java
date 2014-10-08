package edu.rpi.phil.legup.puzzles.heyawake;

import java.awt.Point;
import java.util.ArrayList;

import javax.swing.ImageIcon;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.CaseRule;

public class CaseBlackOrWhite extends CaseRule
{	
	private static final long serialVersionUID = 971363234L;

	public CaseBlackOrWhite()
	{
		setName("Cell black or white");
		description = "Each blank cell is either black or white.";
		image = new ImageIcon("images/heyawake/cases/BlackOrWhite.png");
	}
	
	public String checkCaseRuleRaw(BoardState state)
	{
		String rv = null;
		
		if (state.getTransitionsFrom().size() != 2)
		{
			rv = "This case rule can only be applied on a two-way split.";
		}
		else
		{
			BoardState one = state.getTransitionsFrom().get(0);
			BoardState two = state.getTransitionsFrom().get(1);
						
			ArrayList<Point> dif = BoardState.getDifferenceLocations(one,two);
			
			if (dif.size() > 1)
			{
				rv = "Your two-way split is only allowed to change a single cell with this rule.";
			}
			else if (dif.size() == 0)
			{
				rv = "Your two-way split must change a single cell with this rule.";
			}
			else
			{
				Point p = dif.get(0);
				
				if (!((one.getCellContents(p.x,p.y) == Heyawake.CELL_BLACK && 
					two.getCellContents(p.x,p.y) == Heyawake.CELL_WHITE) ||
					(two.getCellContents(p.x,p.y) == Heyawake.CELL_BLACK && 
						one.getCellContents(p.x,p.y) == Heyawake.CELL_WHITE)))
				{
					rv = "In this case rule, one state's cell must be white and the other black.";
				}
				else if (state.getCellContents(p.x,p.y) != Heyawake.CELL_UNKNOWN)
				{
					rv = "The parent cell that you're applying the case rule on must be a blank cell.";
				}
			}
			
		}
			
		return rv;
	}
}
