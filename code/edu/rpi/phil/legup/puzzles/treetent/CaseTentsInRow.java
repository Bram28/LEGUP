package edu.rpi.phil.legup.puzzles.treetent;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.ImageIcon;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.CaseRule;
import edu.rpi.phil.legup.Permutations;
import edu.rpi.phil.legup.PuzzleModule;

public class CaseTentsInRow extends CaseRule
{
	
	
	protected final String defaultApplicationText= "Select a row number.";
	
	public CaseTentsInRow()
	{
		name = "Fill In row";
		description = "A row must have the number of tents of its clue.";
		image = new ImageIcon("images/treetent/case_rowcount.png");
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
			BoardState one = (BoardState)state.getTransitionsFrom().get(0);
			BoardState two = (BoardState)state.getTransitionsFrom().get(1);
						
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
				Point p = (Point)dif.get(0);
				
				if (!((one.getCellContents(p.x,p.y) == TreeTent.CELL_TENT && 
					two.getCellContents(p.x,p.y) == TreeTent.CELL_GRASS) ||
					(two.getCellContents(p.x,p.y) == TreeTent.CELL_TENT && 
						one.getCellContents(p.x,p.y) == TreeTent.CELL_GRASS)))
				{
					rv = "In this case rule, one state's cell must contain grass and the other a tent.";
				}
				else if (state.getCellContents(p.x,p.y) != TreeTent.CELL_UNKNOWN)
				{
					rv = "The parent cell that you're applying the case rule on must be a blank cell.";
				}
			}
			
		}
		rv = null;
		return rv;
	}
	
	public boolean startDefaultApplicationRaw(BoardState state)
	{
		return true;
	}
	
	public boolean doDefaultApplicationRaw(BoardState state, PuzzleModule pm ,Point location)
	{
		if(location.y > 0 && location.y < state.getHeight( ))
		{
			int tents = 0;
			int unknowns = 0;
			for(int x = 0; x < state.getWidth( ); ++x)
			{
				if(state.getCellContents( x, location.y ) == TreeTent.CELL_TENT)
					++tents;
				else if(state.getCellContents( x, location.y ) == TreeTent.CELL_UNKNOWN)
					++unknowns;
			}
			int tentsneeded = TreeTent.translateNumTents(state.getLabel(BoardState.LABEL_RIGHT, location.y)) - tents;
			int grassneeded = unknowns - tentsneeded;
			
			Vector<Integer> states = new Vector<Integer>();
			states.add( TreeTent.CELL_TENT );
			states.add( TreeTent.CELL_GRASS );
			Vector<Integer> statecounts = new Vector<Integer>();
			statecounts.add( tentsneeded );
			statecounts.add( grassneeded );
			Vector<Integer> conditions = new Vector<Integer>();
			conditions.add( TreeTent.CELL_UNKNOWN);
			Permutations.permutationRow( state, location.y, states, statecounts, conditions );
			state.setCaseSplitJustification(this);
			Permutations.caseContradictionFinder( state, pm );
			return true;
		}
		return false;
	}
}
