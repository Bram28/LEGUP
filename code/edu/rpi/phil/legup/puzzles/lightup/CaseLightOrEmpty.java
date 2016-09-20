package edu.rpi.phil.legup.puzzles.lightup;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.ImageIcon;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.CaseRule;
import edu.rpi.phil.legup.PuzzleModule;
import edu.rpi.phil.legup.newgui.LEGUP_Gui;
import edu.rpi.phil.legup.Permutations;

public class CaseLightOrEmpty extends CaseRule
{
	static final long serialVersionUID = -1977535413148184084L;
	public String getImageName() 
	{
		if (LEGUP_Gui.LIGHT_UP_LEGACY == true)
		{
			return "images/lightup/cases/LightOrEmptyLegacy.png";
		}
		else
		{
			return "images/lightup/cases/LightOrEmpty.png";
		}
	}
	public CaseLightOrEmpty()
	{
		setName("Light or Empty");
		description = "Each blank cell is either a light or empty.";
		//image = new ImageIcon("images/lightup/cases/LightOrWhite.png");

		defaultApplicationText= "Select an unknown square.";
	}

	public String checkCaseRuleRaw(BoardState state)
	{
		String rv = null;
		BoardState parent = state.getSingleParentState();
		if (parent.getChildren().size() != 2)
		{
			rv = "This case rule can only be applied on a two-way split.";
		}
		else
		{
			BoardState one = parent.getChildren().get(0);
			BoardState two = parent.getChildren().get(1);

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
					two.getCellContents(p.x,p.y) == LightUp.CELL_EMPTY) ||
					(two.getCellContents(p.x,p.y) == LightUp.CELL_LIGHT &&
						one.getCellContents(p.x,p.y) == LightUp.CELL_EMPTY)))
				{
					rv = "In this case rule, one state's cell must be white and the other a light.";
				}
				else if (parent.getCellContents(p.x,p.y) != LightUp.CELL_UNKNOWN)
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
			states.add( LightUp.CELL_EMPTY );
			states.add( LightUp.CELL_LIGHT );

			Permutations.permutationCell( state, location, states );

			for(int i = 0; i < state.getChildren( ).size( ); ++i)
			{
				LightUp.fillLight(state.getChildren( ).get( i ));
			}

			state.setCaseSplitJustification(this);

			return true;
		}
		return false;
	}
}
