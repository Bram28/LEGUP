package edu.rpi.phil.legup.puzzles.lightup;

import java.util.Set;
import java.util.LinkedHashSet;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.PuzzleRule;
import edu.rpi.phil.legup.newgui.LEGUP_Gui;
import edu.rpi.phil.legup.Contradiction;

public class RuleFinishWithEmpty extends PuzzleRule
{
	static final long serialVersionUID = 2828176895339413023L;
	public String getImageName() 
	{
			return "images/lightup/rules/FinishWithEmpty.png";
	}
	RuleFinishWithEmpty()
	{
		setName("Finish with Empty");
		description = "The remaining unknowns around a block must be empty if the number is satisfied.";
	}

	/**
	* Checks if RuleFinishWithEmpty was correctly applied to this board state.
	* If declaring a cell adjacent to a number to be a bulb causes a contradiction
	* where too many light bulbs can be placed to satisfy the number the rule
	* was applied correctly. (i.e. the number of light bulb cells adjacent to number
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
		contras.add(new ContradictionTooManyBulbs());

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
					//Make sure cells placed are empty cells
					if (destBoardState.getCellContents(x, y) != LightUp.CELL_EMPTY) {
						return "Only empty cells are allowed for this rule!";
					}

					// Create alternative boardstate to apply other case/contradiction
					BoardState modified = origBoardState.copy();
					modified.getBoardCells()[y][x] = LightUp.CELL_LIGHT;
					for (Contradiction c : contras) {
						if (c.checkContradictionRaw(modified) != null)
							return "It is not required for the modified cell(s) to be empty!";
					}
				}
			}
		}
		return null;
	}
}
