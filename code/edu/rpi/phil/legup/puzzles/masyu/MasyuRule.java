package edu.rpi.phil.legup.puzzles.masyu;

import edu.rpi.phil.legup.BoardState;
//import edu.rpi.phil.legup.PuzzleModule;
import edu.rpi.phil.legup.PuzzleRule;

public abstract class MasyuRule extends PuzzleRule {

	public Checker getChecker()
	{
		String cname = getClass().getCanonicalName();
		cname += "Checker";
		
		try {
			return (Checker)Class.forName(cname).newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	public Adder getAdder()
	{
		String cname = getClass().getCanonicalName();
		cname += "Adder";
		
		try {
			return (Adder)Class.forName(cname).newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	
	protected String checkRuleRaw(BoardState destBoardState) {
		String error = null;
		boolean changed = false;
		BoardState origBoardState = destBoardState.getSingleParentState();

		// Check for only one branch
		if (destBoardState.getTransitionsTo().size() != 1) {
			error = "This rule only involves having a single branch!";
		} else {
			int height = origBoardState.getHeight();
			int width = origBoardState.getWidth();
			Checker rbec = getChecker();
			//System.out.println("Checking");
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
	}
	protected boolean doDefaultApplicationRaw(BoardState destBoardState) {
		//System.out.println("Doing default application");
		BoardState origBoardState = destBoardState.getSingleParentState();
		boolean changed = false,uchanged = false;
		int width = destBoardState.getWidth();
		int height = destBoardState.getHeight();
		Adder c = getAdder();
		//int destValue;
		
		if (origBoardState != null && destBoardState.getTransitionsTo().size() == 1) {
			BoardAccessor ba = new BoardAccessor(destBoardState, origBoardState, 0,0,0);
			do{
				//System.out.println("Making Run");
				changed = false;
				for (int x = 0; x < width; x++) {
					ba.setX(x);
					for (int y = 0; y < height; y++) {
						ba.setY(y);
						if(Masyu.addDirections(c, destBoardState, x, y))
						{
							changed = true;
						}
					}	
				}
				uchanged |= changed;
			} while(changed);
		}
		

		//if (!changed) {
			//???, changes local variable...
			//if(origBoardState != null)
			//	destBoardState = origBoardState.copy();
		//}

		return uchanged;
	}
}
