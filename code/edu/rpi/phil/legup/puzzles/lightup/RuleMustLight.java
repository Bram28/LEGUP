package edu.rpi.phil.legup.puzzles.lightup;

import java.util.Set;
import java.util.LinkedHashSet;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.PuzzleRule;
import edu.rpi.phil.legup.Contradiction;

public class RuleMustLight extends PuzzleRule
{
	static final long serialVersionUID = 3220052913694553750L;
	public String getImageName() {return "images/lightup/rules/MustLight.png";}
	RuleMustLight()
	{
		setName("Must Light");
		description = "A cell must be a light if it is the only cell to be able to light another.";
	}

	/**
	* Checks if RuleMustLight was correctly applied to this board state.
	* If declaring a cell to be empty makes it impossible for another cell(s)
	* on the board to be lit, then that cell must be a light.
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
		contras.add(new ContradictionCannotLightACell());

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
					// Make sure cells placed are light cells
					if (destBoardState.getCellContents(x, y) != LightUp.CELL_LIGHT) {
						return "Only Light cells are allowed for this rule!";
					}
					// Make sure a light bulb is not placed in a lit cell
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
