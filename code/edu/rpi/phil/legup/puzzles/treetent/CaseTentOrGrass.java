package edu.rpi.phil.legup.puzzles.treetent;

import java.awt.Point;
import java.util.ArrayList;

import javax.swing.ImageIcon;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.CaseRule;

public class CaseTentOrGrass extends CaseRule
{
	
	public CaseTentOrGrass()
	{
		setName("Tree or Grass");
		description = "Each blank cell is either a tent or grass.";
		image = new ImageIcon("images/treetent/caseTentOrGrass.png");
	}
	
	public String checkCaseRuleRaw(BoardState state)
	{
		String rv = null;
		BoardState parent = state.getSingleParentState(); 
		if (parent.getTransitionsFrom().size() != 2)
		{
			rv = "This case rule can only be applied on a two-way split.";
		}
		else
		{
			BoardState one = parent.getTransitionsFrom().get(0);//.getTransitionsFrom().get(0);
			BoardState two = parent.getTransitionsFrom().get(1);//.getTransitionsFrom().get(0);
			
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
				
				if (!((one.getCellContents(p.x,p.y) == TreeTent.CELL_TENT && 
					two.getCellContents(p.x,p.y) == TreeTent.CELL_GRASS) ||
					(two.getCellContents(p.x,p.y) == TreeTent.CELL_TENT && 
						one.getCellContents(p.x,p.y) == TreeTent.CELL_GRASS)))
				{
					rv = "In this case rule, one state's cell must contain grass and the other a tent.";
				}
				else if (parent.getCellContents(p.x,p.y) != TreeTent.CELL_UNKNOWN)
				{
					rv = "The parent cell that you're applying the case rule on must be a blank cell.";
				}
			}
			
		}
			
		return rv;
	}
}
