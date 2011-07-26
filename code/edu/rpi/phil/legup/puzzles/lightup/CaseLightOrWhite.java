package edu.rpi.phil.legup.puzzles.lightup;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.ImageIcon;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.CaseRule;
import edu.rpi.phil.legup.PuzzleModule;
import edu.rpi.phil.legup.Permutations;

public class CaseLightOrWhite extends CaseRule
{
	public CaseLightOrWhite()
	{
		setName("Light or White");
		description = "Each blank cell is either a light or white.";
		image = new ImageIcon("images/lightup/cases/LightOrWhite.png");
		
		defaultApplicationText= "Select an unknown square.";
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
				
				if (!((one.getCellContents(p.x,p.y) == LightUp.CELL_LIGHT && 
					two.getCellContents(p.x,p.y) == LightUp.CELL_BLANK) ||
					(two.getCellContents(p.x,p.y) == LightUp.CELL_LIGHT && 
						one.getCellContents(p.x,p.y) == LightUp.CELL_BLANK)))
				{
					rv = "In this case rule, one state's cell must be white and the other a light.";
				}
				else if (state.getCellContents(p.x,p.y) != LightUp.CELL_UNKNOWN)
				{
					rv = "The parent cell that you're applying the case rule on must be a blank cell.";
				}
			}
			
		}
			
		return rv;
	}

	public boolean startDefaultApplicationRaw(BoardState state)
	{
		return true;
	}
	
	public boolean doDefaultApplicationRaw(BoardState state, PuzzleModule pm ,Point location)
	{
		if(location.x < 0 || location.y < 0 || location.x >= state.getWidth( ) || location.y >= state.getHeight( ))
			return false;
		if(state.getCellContents(location.x, location.y) == LightUp.CELL_UNKNOWN)
		{
			Vector<Integer> states = new Vector<Integer>();
			states.add( LightUp.CELL_BLANK );
			states.add( LightUp.CELL_LIGHT );
			
			Permutations.permutationCell( state, location, states );
			
			for(int i = 0; i < state.getTransitionsFrom( ).size( ); ++i)
			{
				LightUp.fillLight(state.getTransitionsFrom( ).get( i ));
			}
			
			state.setCaseSplitJustification(this);
			
			return true;
		}
		return false;
	}
}
