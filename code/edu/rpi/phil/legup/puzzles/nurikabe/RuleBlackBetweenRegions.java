package edu.rpi.phil.legup.puzzles.nurikabe;

import java.util.ArrayList;

import javax.swing.ImageIcon;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.PuzzleRule;
import edu.rpi.phil.legup.Contradiction;
import java.util.Set;
import java.util.LinkedHashSet;

public class RuleBlackBetweenRegions extends PuzzleRule
{
	private static final long serialVersionUID = 830456717L;

	 RuleBlackBetweenRegions()
	 {
		setName("Black Between Regions");
		description = "Any unknowns between two regions must be black.";
		image = new ImageIcon("images/nurikabe/rules/BetweenRegions.png");
	 }

	public String getImageName()
	{
		return "images/nurikabe/rules/BetweenRegions.png";
	}

	/**
	* Checks if the contradiction was applied correctly to this board state
	*
	* @param state The board state
	* @return null if the contradiction was applied correctly, the error String otherwise
	*/
	protected String checkRuleRaw(BoardState destBoardState)
	{
		Set<Contradiction> contras = new LinkedHashSet<Contradiction>();
		contras.add(new ContradictionMultipleNumbers());
		contras.add(new ContradictionTooManySpaces());

		BoardState origBoardState = destBoardState.getSingleParentState();
		int width = origBoardState.getWidth();
		int height = origBoardState.getHeight();

		// Check for only one branch
		if (destBoardState.getParents().size() != 1)
		{
			return "This rule only involves having a single branch!";
		}

		for (int y = 0; y < height; y++)
		{
			for (int x = 0; x < width; x++)
			{
				if (destBoardState.getCellContents(x, y) != origBoardState.getCellContents(x, y))
				{
					// Check prerequisites for the rule
					if (destBoardState.getCellContents(x, y) != Nurikabe.CELL_BLACK)
					{
						return "Only black cells are allowed for this rule!";
					}

					int numAdjacentWhite = 0;
					for (int delta = -1; delta < 2; delta += 2) {
						if ( (x+delta > 0 && x+delta < width)
								 && destBoardState.getCellContents(x+delta, y) > Nurikabe.CELL_BLACK)
							numAdjacentWhite++;

						if ( (y+delta > 0 && y+delta < height)
									&& destBoardState.getCellContents(x, y+delta) > Nurikabe.CELL_BLACK)
							numAdjacentWhite++;
					}
					if (numAdjacentWhite < 2){
						return "The new black cell must seperate two white regions for this rule!";
					}

					//check that at least one contradiction is satisfied
					BoardState modified = origBoardState.copy();
					modified.getBoardCells()[y][x]=Nurikabe.CELL_WHITE;

					int contrasSatisfied = 0;
					for (Contradiction c : contras)
					{
						if (c.checkContradictionRaw(modified) == null)
							contrasSatisfied++;
					}
					if (contrasSatisfied == 0)
						return "The black cell does not seperate two white regions!";
				}
			}
		}

		return null;
	}
}
