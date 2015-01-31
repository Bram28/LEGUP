package edu.rpi.phil.legup.puzzles.nurikabe;

import javax.swing.ImageIcon;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.PuzzleRule;

import java.awt.Point;
import java.util.Set;
import java.util.LinkedHashSet;


public class RuleCornerBlack extends PuzzleRule
{
	private static final long serialVersionUID = 889434116L;

	RuleCornerBlack()
	{
		setName("Corner Black");
		description = "If there is only one white square connected to unkowns and one more white is needed then the angles of that white square are black";
		image = new ImageIcon("images/nurikabe/rules/CornerBlack.png");
	}
	public String getImageName()
	{
		return "images/nurikabe/rules/CornerBlack.png";
	}

	protected String checkRuleRaw(BoardState destBoardState)
	{
		String error = null;
		BoardState origBoardState = destBoardState.getSingleParentState();

		// Check for only one branch
		if (destBoardState.getParents().size() != 1)
		{
			return "This rule only involves having a single branch!";
		}

		int width = destBoardState.getWidth();
		int height = destBoardState.getHeight();

		for (int y = 0; y < height; y++)
		{
			for (int x = 0; x < width; x++)
			{
				if (destBoardState.getCellContents(x, y) != origBoardState.getCellContents(x, y))
				{
					if (destBoardState.getCellContents(x, y) != Nurikabe.CELL_BLACK)
					{
						return "Only black cells are allowed for this rule!";
					}

					Set<Point> whiteTwos = whiteTwoLocation(destBoardState, new Point(x, y), width, height);
					if (whiteTwos.size() == 0)
						return "The black must be kitty-corner to a white 2 cell!";

					int correctTwos = 0;

					for (Point p : whiteTwos)
					{
						Set<Point> openAdjs = openAdjacents(destBoardState, p, width, height);
						if (openAdjs == null)
							return "The region is already completed!";
						if (openAdjs.size() != 2)
							continue;
						int correctAdjs = 0;
						for (Point o : openAdjs)
						{
							if (o.distance(x, y) == 1)
							correctAdjs++;
						}
						if (correctAdjs == 2)
							correctTwos++;
					}
					if (correctTwos == 0)
						return "There must be two unknown cells adjacent to the white 2!";
				}
			}
		}
		return null;
	}

	/**
		Finds the location of the #2 white cell on the blacks corner
		Returns a point with values (-1,-1) if no such cell exists
	*/
	private Set<Point> whiteTwoLocation(BoardState board, Point black, int width, int height)
	{
		Set<Point> targets = new LinkedHashSet<Point>();

		if (black.x-1 > 0 && black.y-1 > 0)
		{
			if (board.getCellContents(black.x-1, black.y-1) == 12)
				targets.add(new Point(black.x-1, black.y-1));
		}

		if (black.x+1 < width && black.y-1 > 0)
		{
			if (board.getCellContents(black.x+1, black.y-1) == 12)
				targets.add(new Point(black.x+1, black.y-1));
		}

		if (black.x-1 > 0 && black.y+1 < height)
		{
			if (board.getCellContents(black.x-1, black.y+1) == 12)
				targets.add(new Point(black.x-1, black.y+1));
		}

		if (black.x+1 < width && black.y+1 < height)
		{
			if (board.getCellContents(black.x+1, black.y+1) == 12)
				targets.add(new Point(black.x+1, black.y+1));
		}

		return targets;
	}

	/**
		returns the locations of the unknown cells which are adjacent to the white two
	*/
	private Set<Point> openAdjacents(BoardState board, Point white, int width, int height)
	{
		Set<Point> targets = new LinkedHashSet<Point>();

		if (white.x-1 > 0)
		{
			if (board.getCellContents(white.x-1, white.y) == Nurikabe.CELL_UNKNOWN)
				targets.add(new Point(white.x-1, white.y));
			if (board.getCellContents(white.x-1, white.y) == Nurikabe.CELL_WHITE)
				return null;
		}

		if (white.x+1 < width)
		{
			if (board.getCellContents(white.x+1, white.y) == Nurikabe.CELL_UNKNOWN)
				targets.add(new Point(white.x+1, white.y));
			if (board.getCellContents(white.x+1, white.y) == Nurikabe.CELL_WHITE)
				return null;
		}

		if (white.y+1 < height)
		{
			if (board.getCellContents(white.x, white.y+1) == Nurikabe.CELL_UNKNOWN)
				targets.add(new Point(white.x, white.y+1));
			if (board.getCellContents(white.x, white.y+1) == Nurikabe.CELL_WHITE)
				return null;
		}

		if (white.y-1 > 0)
		{
			if (board.getCellContents(white.x, white.y-1) == Nurikabe.CELL_UNKNOWN)
				targets.add(new Point(white.x, white.y-1));
			if (board.getCellContents(white.x, white.y+1) == Nurikabe.CELL_WHITE)
				return null;
		}

		return targets;
	}
}
