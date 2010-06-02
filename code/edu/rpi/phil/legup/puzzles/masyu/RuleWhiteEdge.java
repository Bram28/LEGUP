package edu.rpi.phil.legup.puzzles.masyu;

import javax.swing.ImageIcon;

public class RuleWhiteEdge extends MasyuRule {

	
	public RuleWhiteEdge()
	{
		name = "White Edge";
		description = "White pearls have a straight line going through them with a turn next to them.";
		image = new ImageIcon("images/masyu/Rules/RuleWhiteEdge.png");
	}
	@Override
	public Adder getAdder() {
		return new RuleWhiteEdgeAdder();
	}

	@Override
	public Checker getChecker() {
		return new RuleWhiteEdgeChecker();
	}
}

class RuleWhiteEdgeAdder extends Adder
{}//add faster work

class RuleWhiteEdgeChecker implements Checker
{
	public String check(BoardAccessor ba) {
		int center = ba.getOrigCell(0, 0);
		if(ba.isWhite(center))
		{
			int n = BoardAccessor.NORTH, s = BoardAccessor.SOUTH; 
			int oneUp = ba.getOrigCell(0, 1), oneDown = ba.getOrigCell(0, -1);
			if(ba.hasDir(oneUp, s) ||
			   ba.hasDir(oneDown, n) ||
			   ba.hasDir(center, s))
				return null;
			
			{
				//not automatic, must have been forced
				int oneLeft = ba.getOrigCell(-1, 0), oneRight = ba.getOrigCell(1,0);
				if(!ba.validConnection(oneLeft, BoardAccessor.EAST))
					return null;
				if(!ba.validConnection(oneRight, BoardAccessor.WEST))
					return null;
				//can't bend, so must stop
				
				boolean SLN = ba.validConnection(oneLeft,n) && 
							  ba.validConnection(ba.getOrigCell(-1,1), s);
				boolean SLS = ba.validConnection(oneLeft, s) &&
							  ba.validConnection(ba.getOrigCell(-1,-1), n);
				boolean SRN = ba.validConnection(oneRight,n) &&
				  			  ba.validConnection(ba.getOrigCell(1,1), s);
				boolean SRS = ba.validConnection(oneRight, s) &&
	 			   			  ba.validConnection(ba.getOrigCell(1,-1), n);
				if(SLN || SLS || SRN || SRS)
				{
					return "Invalid";
				}
				return null;
			}
		}

		/*if(ba.isWhite(ba.getOrigCell(0, 0)))
		{
			//something to the south
			if(ba.hasDir(ba.getOrigCell(0, 0),BoardAccessor.SOUTH))
				return null;
			//blocked to the left
			int left = ba.getOrigCell(-1, 0);
			if(!ba.validConnection(left, BoardAccessor.EAST))
				return null;
			//blocked to the right
			int right = ba.getOrigCell(1, 0);
			if(!ba.validConnection(right, BoardAccessor.WEST))
				return null;
			//can't turn -- check for left turn, if you can return error, then right
			int n = BoardAccessor.NORTH;
			int s = BoardAccessor.SOUTH;
			if(!ba.isWhite(left) && 
				((ba.validConnection(left, n) && 
					ba.validConnection(ba.getOrigCell(-1,-1),s)) ||
				 (ba.validConnection(left, s) &&
				    ba.validConnection(ba.getOrigCell(-1, 1), n))))
				return "Element not completely blocked";
			if(!ba.isWhite(right) && 
					((ba.validConnection(right, n) && 
						ba.validConnection(ba.getOrigCell(1,-1),s)) ||
					 (ba.validConnection(right, s) &&
					    ba.validConnection(ba.getOrigCell(1, 1), n))))
					return "Element not completely blocked";
			return null;
		}*/
		//must be white, this only applies if right next to a white cell
		//first case: got a connected white cell to north
		int north = ba.getOrigCell(0, 1);
		if(ba.isWhite(north))
		{
			if(ba.hasDir(north, BoardAccessor.SOUTH) || ba.hasDir(north, BoardAccessor.NORTH))
				return null;
			// not defined yet...
			BoardAccessor ba2 = new BoardAccessor(ba);
			ba2.move(BoardAccessor.NORTH);
			north = ba.getDestCell(0,1);
			//check to see if it is valid, then if it has N or S its valid
			if(check(ba2)==null && (ba.hasDir(north,BoardAccessor.NORTH) || 
					          ba.hasDir(north,BoardAccessor.SOUTH)))
			{
				return null;
			}
		}
		return "Invalid Placement";
	}

}
