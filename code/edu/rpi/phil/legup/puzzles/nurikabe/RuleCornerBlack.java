package edu.rpi.phil.legup.puzzles.nurikabe;

import javax.swing.ImageIcon;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.PuzzleRule;
import edu.rpi.phil.legup.Contradiction;
import edu.rpi.phil.legup.ConnectedRegions;

import java.awt.Point;
import java.util.Set;
import java.util.LinkedHashSet;

public class RuleCornerBlack extends PuzzleRule
{
	private static final long serialVersionUID = 889434116L;

	RuleCornerBlack()
	{
		setName("Corner Black");
		description = "If there is only one white square connected to unkowns and " +
			"one more white is needed then the angles of that white square are black";
		image = new ImageIcon("images/nurikabe/rules/CornerBlack.png");
	}
	public String getImageName()
	{
		return "images/nurikabe/rules/CornerBlack.png";
	}

	protected String checkRuleRaw(BoardState destBoardState) {
		BoardState origBoardState = destBoardState.getSingleParentState();

		// Check for only one branch
		if (destBoardState.getParents().size() != 1)
		{
			return "This rule only involves having a single branch!";
		}

		int width = destBoardState.getWidth();
		int height = destBoardState.getHeight();

		// Set<Contradiction> contras = new LinkedHashSet<Contradiction>();
		Contradiction tooFewContra = new ContradictionTooFewSpaces();

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (destBoardState.getCellContents(x, y) != origBoardState.getCellContents(x, y)) {
					if (destBoardState.getCellContents(x, y) != Nurikabe.CELL_BLACK) {
						return "Only black cells are allowed for this rule!";
					}

					BoardState modified = origBoardState.copy();
					// modified.getBoardCells()[y][x] = Nurikabe.CELL_WHITE;

					boolean validPoint = false;

					// Check each corner of the changed cell
					for (int d = -1; d < 2; d+=2) {
						if ((x+d >= 0 && x+d < width) && (y+d >= 0 && y+d < height)
								&& modified.getCellContents(x+d, y+d) >= Nurikabe.CELL_WHITE)	// >= is used to account for numbered cells
						{
							// Series of if statements to check conditions of rule
							// First check: cells adjacent to changed cell and white region corner are empty
							if (modified.getCellContents(x+d, y) == Nurikabe.CELL_UNKNOWN
									&& modified.getCellContents(x, y+d) == Nurikabe.CELL_UNKNOWN)
							{
								modified.getBoardCells()[y+d][x] = Nurikabe.CELL_BLACK;
								modified.getBoardCells()[y][x+d] = Nurikabe.CELL_BLACK;
								// Second check: corner is only way to escape from the white region
								if (tooFewContra.checkContradictionRaw(modified) == null) {
									Set<Point> reg = ConnectedRegions.getRegionAroundPoint(new Point(x+d, y+d), Nurikabe.CELL_BLACK,
									 									modified.getBoardCells(), modified.getWidth(), modified.getHeight());
									int regionNum = 0;
									for (Point p : reg) {
										if (modified.getCellContents(p.x, p.y) > 10) {
											if (regionNum == 0) {
												regionNum = modified.getCellContents(p.x, p.y);
											}
											else return "There is a MultipleNumbers Contradiction on the board.";
										}
									}
									//Third check: The white region kittycorner to this currently has one less cell than required
									if (regionNum > 0 && reg.size() == regionNum-11) {
										validPoint = true;
										break;
									}
								}
							}
						}

						if ((x+d >= 0 && x+d < width) && (y-d >= 0 && y-d < height)
								&& modified.getCellContents(x+d, y-d) >= Nurikabe.CELL_WHITE)
						{
							// Series of if statements to check conditions of rule
							// First check: cells adjacent to changed cell and white region corner are empty
							if (modified.getCellContents(x+d, y) == Nurikabe.CELL_UNKNOWN
									&& modified.getCellContents(x, y-d) == Nurikabe.CELL_UNKNOWN)
							{
								modified.getBoardCells()[y-d][x] = Nurikabe.CELL_BLACK;
								modified.getBoardCells()[y][x+d] = Nurikabe.CELL_BLACK;
								// Second check: corner is only way to escape from the white region
								if (tooFewContra.checkContradictionRaw(modified) == null) {
									Set<Point> reg = ConnectedRegions.getRegionAroundPoint(new Point(x+d, y-d), Nurikabe.CELL_BLACK,
																		modified.getBoardCells(), modified.getWidth(), modified.getHeight());
									int regionNum = 0;
									for (Point p : reg) {
										if (modified.getCellContents(p.x, p.y) > 10) {
											if (regionNum == 0) {
												regionNum = modified.getCellContents(p.x, p.y);
											}
											else return "There is a MultipleNumbers Contradiction on the board!";
										}
									}
									//Third check: The white region kittycorner to this currently has one less cell than required
									if (regionNum > 0 && reg.size() == regionNum-11) {
										validPoint = true;
										break;
									}
								}
							}
						}


					}
					if (!validPoint) return "This is not a valid use of the corner black rule!";
				}
			}
		}
		return null;
	}
}
