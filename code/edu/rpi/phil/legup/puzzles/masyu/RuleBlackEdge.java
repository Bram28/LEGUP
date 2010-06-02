package edu.rpi.phil.legup.puzzles.masyu;

import javax.swing.ImageIcon;

public class RuleBlackEdge extends MasyuRule {

	
	/**
	 * Rule to make a path continuous between different cells.
	 */
	RuleBlackEdge() {
		name = "Black Turn";
		description = "Black cells must turn.";
		image = new ImageIcon("images/masyu/Rules/RuleBlackEdge.png");
	}

	/**
	 * Checks to see if the rule was correctly applied For this rule, for each
	 * added there must be a line on the other side
	 * 
	 * @param state
	 *            The board state
	 * @return null if the contradiction was applied correctly, the error String
	 *         otherwise
	 */
	/*protected String checkRuleRaw(BoardState destBoardState) {
		String error = null;
		boolean changed = false;
		BoardState origBoardState = destBoardState.getSingleParentState();

		// Check for only one branch
		if (destBoardState.getTransitionsTo().size() != 1) {
			error = "This rule only involves having a single branch!";
		} else {
			int height = origBoardState.getHeight();
			int width = origBoardState.getWidth();
			RuleBlackEdgeChecker rbec = new RuleBlackEdgeChecker();
			
			for (int y = 0; y < height && error == null; ++y) {
				for (int x = 0; x < width; ++x) {
					
					int origState = origBoardState.getCellContents(x, y);
					int newState = destBoardState.getCellContents(x, y);
					
					if (origState != newState) {
						changed = true;

						int amount = origState ^ newState;

						if (!Masyu.onlyAdds(newState, origState))
							error = "You cannot remove lines!";
						else
							error = Masyu.checkDirections(rbec, destBoardState, x, y, amount);
						if(error != null)
							return error;
					}
				}
			}

			if (error == null && !changed) {
				error = "You must change something to use this rule!";
			}
		}

		return error;
	}*/


	/**
	 * Tries to apply the rule everywhere and returns true if it can do so.
	 * 
	 * @author Bryan
	 * @param destBoardState
	 *            the board to work with
	 * @param pm
	 *            the puzzle module
	 * @see edu.rpi.phil.legup.PuzzleRule#doDefaultApplicationRaw(edu.rpi.phil.legup.BoardState,
	 *      edu.rpi.phil.legup.PuzzleModule)
	 */
	/*protected boolean doDefaultApplicationRaw(BoardState destBoardState,
			PuzzleModule pm) {
		BoardState origBoardState = destBoardState.getSingleParentState();
		boolean changed = false;
		int width = destBoardState.getWidth();
		int height = destBoardState.getHeight();
		RuleBlackEdgeAdder c = new RuleBlackEdgeAdder();
		//int destValue;

		if (origBoardState != null && destBoardState.getTransitionsTo().size() == 1) {
			BoardAccessor ba = new BoardAccessor(destBoardState, origBoardState, 0,0,0);
			for (int x = 0; x < width; x++) {
				ba.setX(x);
				for (int y = 0; y < height; y++) {
					ba.setY(y);
					if(Masyu.addDirections(c, destBoardState, x, y))
						changed = true;
				}	
			}
		}

		if (!changed) {
			if(origBoardState != null)
				destBoardState = origBoardState.copy();
		}

		return changed;
	}*/
}

class RuleBlackEdgeChecker implements Checker
{

	public String check(BoardAccessor ba) 
	{	
		if(ba.isBlack(ba.getDestCell(0,0)))
			return checkBlack(ba);
		return checkOther(ba);
	}
	public String checkBlack(BoardAccessor ba)
	{
		int oneDown = ba.getOrigCell(0, -1);
		if(oneDown == -1) //has wall to the south
			return null;
		//check either side
		if(ba.hasDir(oneDown, BoardAccessor.EAST) || ba.hasDir(oneDown, BoardAccessor.WEST))
			return null;
		int twoDown = ba.getOrigCell(0, -2);
		if(twoDown == -1)
			return null;
		if(ba.hasDir(twoDown,BoardAccessor.NORTH))
			return "Path not blocked for black pearl";
		int count = 0;
		if(ba.hasDir(twoDown, BoardAccessor.EAST))
			count++;
		if(ba.hasDir(twoDown, BoardAccessor.WEST))
			count++;
		if(ba.hasDir(twoDown, BoardAccessor.SOUTH))
			count++;
		if(count >= 2)
			return null;
		return "Path not blocked for black pearl";
	}
	public String checkOther(BoardAccessor ba)
	{
		int oneDown = ba.getDestCell(0, -1);
		if(oneDown != -1 && ba.isBlack(oneDown) && ba.hasDir(oneDown, BoardAccessor.NORTH))
			return null;
		int oneUp = ba.getDestCell(0, 1);
		if(oneUp == -1)
			return "No valid black pearl nearby";
		if(ba.isBlack(oneUp) && ba.hasDir(oneUp, BoardAccessor.SOUTH))
			return null;
		int twoUp = ba.getDestCell(0, 2);
		if(twoUp != -1 && ba.isBlack(twoUp) && ba.hasDir(twoUp, BoardAccessor.SOUTH))
			return null;
		return "No valid black pearl nearby";
	}
}

class RuleBlackEdgeAdder extends Adder
{
	/*private String error = null;
	public boolean canAdd(BoardAccessor ba) {
		error = null;
		if(ba.isBlack(ba.getOrigCell(0, 0)))
		{
			//currently have black cell, check behind
			if(!ba.validConnection(ba.getOrigCell(0, -1), BoardAccessor.NORTH))
			{
				return true;
			}
			//now check 2 behind
			if(!ba.validConnection(ba.getOrigCell(0, -2), BoardAccessor.NORTH))
			{
				return true;
			} 
			return false;
		}
		//handle other cases: right next to black or 2 away
		//right next to black which is blocked
		int oneForward = ba.getOrigCell(0, 1);
		if(ba.isBlack(oneForward))
		{
			if(ba.hasDir(oneForward,BoardAccessor.SOUTH) ||
			   !ba.validConnection(ba.getOrigCell(0, 2), BoardAccessor.NORTH) ||
			   !ba.validConnection(ba.getOrigCell(0, 3), BoardAccessor.NORTH))
			   return true;
		}
		int oneBack = ba.getOrigCell(0, -1);
		if(ba.isBlack(oneBack))
		{
			if(ba.hasDir(oneBack,BoardAccessor.NORTH) ||
			   !ba.validConnection(ba.getOrigCell(0, -2), BoardAccessor.SOUTH) ||
			   !ba.validConnection(ba.getOrigCell(0, -3), BoardAccessor.SOUTH)
				)
				return true;
		}
		int twoBack = ba.getOrigCell(0, 2);
		if(ba.isBlack(twoBack))
		{
			if(ba.hasDir(twoBack,BoardAccessor.SOUTH) ||
			   !ba.validConnection(ba.getOrigCell(0, 3), BoardAccessor.NORTH) ||
			   !ba.validConnection(ba.getOrigCell(0, 4), BoardAccessor.NORTH))
			   return true;
		}
		
		return false;
	}

	public String getError() {
		return error;
	}	*/
}


//old code, keep for archival purposes
/*private String checkOtherLocation(int x, int y, int amount, BoardState destBoardState, BoardState origBoardState) {
String error = null;

int height = origBoardState.getHeight();
int width = origBoardState.getWidth();

//check north

if(Masyu.hasNorth(amount))
{
	//check south by one spot	
	if(y < height - 1)
	{
		int oneDown = destBoardState.getCellContents(x,y + 1);
		if(Masyu.isBlack(oneDown) && Masyu.hasNorth(oneDown))
			amount &= ~Masyu.NORTH;
	}
}
if(Masyu.hasNorth(amount))
{
	//still has north, check two spots to the north
	if(y > 1)
	{
		int oneUp = destBoardState.getCellContents(x,y-1);
		if(!Masyu.isBlack(oneUp))
		{
			if(y <= 2)
				error = "No appropriate black square for nearby location to go north";
			else{
				int twoUp = destBoardState.getCellContents(x,y-2);
				if(!(Masyu.isBlack(twoUp) && Masyu.hasSouth(twoUp)))
					error = "No appropriate black square for nearby location to go north";
			}		
		} else {
			if(!Masyu.hasSouth(oneUp))
				error = "No appropriate black square for nearby location to go north";
		}
	}
}

//check south

if(Masyu.hasSouth(amount))
{
	//check north by one spot	
	if(y > 0)
	{
		int oneUp = destBoardState.getCellContents(x,y - 1);
		if(Masyu.isBlack(oneUp) && Masyu.hasNorth(oneUp))
			amount &= ~Masyu.SOUTH;
	}
}
if(Masyu.hasSouth(amount))
{
	//still has south, check two spots to the south
	if(y < height - 1)
	{
		int oneDown = destBoardState.getCellContents(x,y+1);
		if(!Masyu.isBlack(oneDown))
		{
			if(y <= 2)
				error = "No appropriate black square for nearby location to go south";
			else{
				int twoDown = destBoardState.getCellContents(x,y+2);
				if(!(Masyu.isBlack(twoDown) && Masyu.hasNorth(twoDown)))
					error = "No appropriate black square for nearby location to go south";
			}		
		} else {
			if(!Masyu.hasNorth(oneDown))
				error = "No appropriate black square for nearby location to go south";
		}
	}
}

//check east

if(Masyu.hasEast(amount))
{
	//check west by one spot	
	if(x > 1)
	{
		int oneWest = destBoardState.getCellContents(x-1,y);
		if(Masyu.isBlack(oneWest) && Masyu.hasWest(oneWest))
			amount &= ~Masyu.EAST;
	}
}
if(Masyu.hasEast(amount))
{
	//still has east, check two spots to the east
	if(x < width-1)
	{
		int oneEast = destBoardState.getCellContents(x+1,y);
		if(!Masyu.isBlack(oneEast))
		{
			if(x >= width-2)
				error = "No appropriate black square for nearby location to go east";
			else{
				int twoEast = destBoardState.getCellContents(x+2,y);
				if(!(Masyu.isBlack(twoEast) && Masyu.hasSouth(twoEast)))
					error = "No appropriate black square for nearby location to go east";
			}		
		} else {
			if(!Masyu.hasWest(oneEast))
				error = "No appropriate black square for nearby location to go east";
		}
	}
}

//check west

if(Masyu.hasWest(amount))
{
	//check east by one spot	
	if(x < width - 1)
	{
		int oneEast = destBoardState.getCellContents(x +1,y);
		if(Masyu.isBlack(oneEast) && Masyu.hasEast(oneEast))
			amount &= ~Masyu.NORTH;
	}
}
if(Masyu.hasWest(amount))
{
	//still has north, check two spots to the north
	if(y > 1)
	{
		int oneWest = destBoardState.getCellContents(x,y-1);
		if(!Masyu.isBlack(oneWest))
		{
			if(y <= 2)
				error = "No appropriate black square for nearby location to go north";
			else{
				int twoUp = destBoardState.getCellContents(x,y-2);
				if(!(Masyu.isBlack(twoUp) && Masyu.hasSouth(twoUp)))
					error = "No appropriate black square for nearby location to go north";
			}		
		} else {
			if(!Masyu.hasSouth(oneWest))
				error = "No appropriate black square for nearby location to go north";
		}
	}
}

return error;
}

protected String checkBlackLocation(int x, int y, int amount, BoardState destBoardState, BoardState origBoardState)
{
String error = null;

int height = origBoardState.getHeight();
int width = origBoardState.getWidth();
		
//black square, so check for wall in the new
// direction
// do so by checking for wall just below, then line,
// then wall, then line
if (Masyu.hasSouth(amount)) {
	if (y > 0) {

		int oneUp = origBoardState.getCellContents(
				x, y - 1);
		if (!Masyu.hasEast(oneUp)
				&& !Masyu.hasWest(oneUp) && (y > 1)) {
			int twoUp = origBoardState
					.getCellContents(x, y - 2), count = 0;
			// sum number of walls
			count += twoUp & 1 + ((twoUp & 2) >> 1)
					+ ((twoUp & 4) >> 2)
					+ ((twoUp & 8) >> 3);
			if (count < 2 || Masyu.hasSouth(twoUp))
				error = "Not enough closed space!";
		}
	}
}
if (Masyu.hasNorth(amount)) {
	if (y < height - 1) {
		int oneDown = origBoardState
				.getCellContents(x, y + 1);
		if (!Masyu.hasEast(oneDown)
				&& !Masyu.hasWest(oneDown)
				&& (y < height - 2)) {
			int twoDown = origBoardState
					.getCellContents(x, y + 2), count = 0;
			// sum number of walls
			count += twoDown & 1
					+ ((twoDown & 2) >> 1)
					+ ((twoDown & 4) >> 2)
					+ ((twoDown & 8) >> 3);
			if (count < 2
					|| Masyu.hasNorth(twoDown))
				error = "Not enough closed space!";
		}
	}
}

if (Masyu.hasEast(amount)) {
	if (x > 0) {
		int oneLeft = origBoardState
				.getCellContents(x - 1, y);
		if (!Masyu.hasNorth(oneLeft)
				&& !Masyu.hasSouth(oneLeft)
				&& (x > 1)) {
			int twoLeft = origBoardState
					.getCellContents(x - 2, y), count = 0;
			// sum number of walls
			count += twoLeft & 1
					+ ((twoLeft & 2) >> 1)
					+ ((twoLeft & 4) >> 2)
					+ ((twoLeft & 8) >> 3);
			if (count < 2 || Masyu.hasEast(twoLeft))
				error = "Not enough closed space!";
		}
	}
}
if (Masyu.hasWest(amount)) {
	if (x < width - 1) {
		int oneRight = origBoardState
				.getCellContents(x + 1, y);
		if (!Masyu.hasNorth(oneRight)
				&& !Masyu.hasSouth(oneRight)
				&& (x < width - 2)) {
			int twoRight = origBoardState
					.getCellContents(x + 2, y), count = 0;
			// sum number of walls
			count += twoRight & 1
					+ ((twoRight & 2) >> 1)
					+ ((twoRight & 4) >> 2)
					+ ((twoRight & 8) >> 3);
			if (count < 2
					|| Masyu.hasWest(twoRight))
				error = "Not enough closed space!";
		}
	}
}
return error;
}*/