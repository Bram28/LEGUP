package edu.rpi.phil.legup.puzzles.lightup;

import java.util.Set;
import java.util.LinkedHashSet;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.PuzzleModule;
import edu.rpi.phil.legup.PuzzleRule;
import edu.rpi.phil.legup.RuleApplication;
import edu.rpi.phil.legup.newgui.LEGUP_Gui;
import edu.rpi.phil.legup.Contradiction;

public class RuleFinishWithBulbs extends PuzzleRule
{
	static final long serialVersionUID = 5613497586353427743L;
	public String getImageName() 
	{
		if (LEGUP_Gui.LIGHT_UP_LEGACY == true)
		{
			return "images/lightup/rules/FinishWithBulbsLegacy.png"; 
		}
		else
		{
			return "images/lightup/rules/FinishWithBulbs.png"; 
		}
	}
	RuleFinishWithBulbs() {
		setName("Finish with Bulbs");
		description = "The remaining unknowns around a block must be bulbs to satisfy the number.";
	}

	/**
	* Checks if RuleFinishWithBulbs was correctly applied to this board state.
	* If declaring a cell adjacent to a number to be empty causes a contradiction
	* where not enough light bulbs can be placed to satisfy the number the rule
	* was applied correctly. (i.e. the number of unknown cells adjacent to number
	* equals the number).
	*
	* @param state The board state
	* @return null if the contradiction was applied correctly, the error String otherwise
	*/
	protected String checkRuleRaw(BoardState destBoardState) {
		// Check for only one branch
		if (destBoardState.getParents().size() != 1) {
			return "This rule only involves having a single branch!";
		}

		// Add contradictions to check to set contras
		Set<Contradiction> contras = new LinkedHashSet<Contradiction>();
		contras.add(new ContradictionTooFewBulbs());

		// Copy the parent state to compare with current state to find changes
		BoardState origBoardState = destBoardState.getSingleParentState();
		int width = origBoardState.getWidth();
		int height = origBoardState.getHeight();

		// Check each tile for any changes,
		// make sure changes are allowed
		// check that doing the opposite case would lead to a contradiction (thus proving the rule)
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				// Check for changes
				if (destBoardState.getCellContents(x, y) != origBoardState.getCellContents(x, y)) {
					//Make sure cells placed are light cells
					if (destBoardState.getCellContents(x, y) != LightUp.CELL_LIGHT) {
						return "Only empty cells are allowed for this rule!";
					}
					if (new ContradictionBulbsInPath().checkContradictionRaw(destBoardState) == null) {
						return "You cannot place a bulb in an already lit cell!";
					}

					// Create alternative boardstate to apply other case/contradiction
					BoardState modified = origBoardState.copy();
					modified.getBoardCells()[y][x] = LightUp.CELL_EMPTY;
					for (Contradiction c : contras) {
						if (c.checkContradictionRaw(modified) != null)
							return "It is not required for the modified cell(s) to be Lights!";
					}
				}
			}
		}
		return null;
	}
}
