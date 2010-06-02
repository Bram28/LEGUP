package edu.rpi.phil.legup.puzzles.masyu;

import javax.swing.ImageIcon;

public class RuleNearWhite extends MasyuRule {

	public RuleNearWhite() {
		name = "Near White";
		description = "At least one cell near a white cell must turn.";
		image = new ImageIcon("images/masyu/Rules/RuleNearWhite.png");

	}
}

class RuleNearWhiteAdder extends Adder{}//add more...
class RuleNearWhiteChecker implements Checker
{
	public String check(BoardAccessor ba) {
		//only deal with two cases, which are mirror images
		//white to left, nothing down, two to left straight across, white connected to this
		
		int left = ba.getOrigCell(-1,0);
		int right = ba.getOrigCell(1,0);
		
		if(!ba.isWhite(left) && !ba.isWhite(right))
			return "Must be a white pearl nearby";
		if(!ba.isWhite(left))
			return checkRight(ba);
		if(!ba.isWhite(right))
			return checkLeft(ba);
		
		if(checkLeft(ba) == null)
			return null;
		if(checkRight(ba) == null)
			return null;
			
		return "Neither side works";
	}
	public String checkLeft(BoardAccessor ba)
	{
		int l = BoardAccessor.EAST;
		int r = BoardAccessor.WEST;
		//preconditions must be that the cell to the left of ba must be white
		int left = ba.getOrigCell(-1, 0);
		int left2 = ba.getOrigCell(-2, 0);
		if(!ba.hasDir(left, l) && !ba.hasDir(left, r))
			return "White cell must have direction specified";
		if(!ba.hasDir(left2, l) || !ba.hasDir(left2, r))
			return "Cell on other side of white must be straight and connected to white";
		if(ba.validConnection(ba.getOrigCell(0, 0), BoardAccessor.SOUTH) &&
		   ba.validConnection(ba.getOrigCell(0, -1), BoardAccessor.NORTH))
			return "Path opposite direction being added must be blocked";
		return null;
	}
	public String checkRight(BoardAccessor ba)
	{
		int l = BoardAccessor.EAST;
		int r = BoardAccessor.WEST;
		//preconditions must be that the cell to the left of ba must be white
		int right = ba.getOrigCell(1, 0);
		int right2 = ba.getOrigCell(2, 0);
		if(!ba.hasDir(right, l) && !ba.hasDir(right, r))
			return "White cell must have direction specified";
		if(!ba.hasDir(right2, l) || !ba.hasDir(right2, r))
			return "Cell on other side of white must be straight and connected to white";
		if(ba.validConnection(ba.getOrigCell(0, 0), BoardAccessor.SOUTH) &&
		   ba.validConnection(ba.getOrigCell(0, -1), BoardAccessor.NORTH))
			return "Path opposite direction being added must be blocked";
		return null;
	}
}