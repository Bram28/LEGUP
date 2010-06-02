package edu.rpi.phil.legup.puzzles.masyu;

import javax.swing.ImageIcon;

public class RuleOnlyOneChoice extends MasyuRule {
	
	public RuleOnlyOneChoice() {
		name = "Only Choice";
		description = "Must go in the only direction available.";
		image = new ImageIcon("images/masyu/Rules/RuleOnlyOneChoice.png");
	}
}

class RuleOnlyOneChoiceAdder extends Adder
{
	
}
class RuleOnlyOneChoiceChecker implements Checker
{
	int dir1, dir2; //used for twopaths as temporary holders
	public String twoPaths(BoardAccessor ba)
	{
		dir1 = dir2 = -1;
		int current = ba.getOrigCell(0, 0);
		
		if(ba.hasDir(current, BoardAccessor.SOUTH) || 
				ba.validConnection(ba.getOrigCell(0, -1),BoardAccessor.NORTH))
			dir1 = BoardAccessor.SOUTH;
		
		if(ba.hasDir(current, BoardAccessor.EAST) ||
				ba.validConnection(ba.getOrigCell(1, 0), BoardAccessor.WEST))
			if(dir1 == -1)
				dir1 = BoardAccessor.EAST;
			else
				dir2 = BoardAccessor.EAST;
		if(ba.hasDir(current, BoardAccessor.WEST) ||
				ba.validConnection(ba.getOrigCell(-1, 0), BoardAccessor.EAST))
			if(dir1 == -1)
				dir1 = BoardAccessor.WEST;
			else if(dir2 == -1)
				dir2 = BoardAccessor.WEST;
			else //both directions taken
				return "Full on West";
		
		if(ba.hasDir(current, BoardAccessor.NORTH) ||
				ba.validConnection(ba.getOrigCell(0, 1), BoardAccessor.SOUTH))
			if(dir1 == -1) //can't finish
				return "Only north";
			else if(dir2 == -1)
				dir2 = BoardAccessor.NORTH;
			else
				return "Full on North";
		return null;
	}
	
	public String check(BoardAccessor ba)
	{
		//3 cases: direct yes, bidir search
		
		//direct yes: 2 directions, one is North, other is pre existing
		/*String s = twoPaths(ba);
		if(s == null)
		{
			//don't need to check dir1 because of how twoDirs works
			if(dir2 == BoardAccessor.NORTH && ba.hasDir(ba.getOrigCell(0, 0), dir1))
				return null;
			return "Almost made it: " + dir1 + " " + dir2 + " " + ba.getDir();
		}*/
		
		String s = checkPrime(ba);
		if(s == null)
			return null;
		//System.out.println(s + " " + ba.getDir());
		
		BoardAccessor ba2 = new BoardAccessor(ba);
		ba2.move(BoardAccessor.NORTH);
		ba2.setDir(ba2.getDir() + 2);
		if(checkPrime(ba2) == null) 
			return null;
		
		return s;
		
		//return "Something isn't right yet: " + s;
	}
	public String checkPrime(BoardAccessor ba) {

		String s = twoPaths(ba);
		if(s == null)
		{
			//don't need to check dir1 because of how twoDirs works
			if(dir2 == BoardAccessor.NORTH && ba.hasDir(ba.getOrigCell(0, 0), dir1))
				return null;
			return "Almost made it: " + dir1 + " " + dir2 + " " + ba.getDir();
		}
		
		/*int current = ba.getOrigCell(0, 0);
		if(ba.hasDir(current,BoardAccessor.EAST))
		{	

			if(ba.hasDir(current, BoardAccessor.WEST) || ba.hasDir(current, BoardAccessor.SOUTH))
				return "Can't do that";
			if(ba.validConnection(ba.getOrigCell(0,-1), BoardAccessor.NORTH))
				return "EN";
			if(ba.validConnection(ba.getOrigCell(1, 0), BoardAccessor.WEST))
				return "EE";
	
			return null;
		}
		if(ba.hasDir(current,BoardAccessor.WEST))
		{	
			if(ba.hasDir(current, BoardAccessor.SOUTH))
				return "Can't do that";
			if(ba.validConnection(ba.getOrigCell(0,-1), BoardAccessor.NORTH))
			{
				return "WN";
			}
			if(ba.validConnection(ba.getOrigCell(1, 0), BoardAccessor.EAST))
			{
				return "WE";
			}
			return null;
		}
		if(ba.hasDir(current,BoardAccessor.SOUTH))
		{	
			if(ba.validConnection(ba.getOrigCell(-1,0), BoardAccessor.EAST))
				return "SE";
			if(ba.validConnection(ba.getOrigCell(1, 0), BoardAccessor.WEST))
				return "SW";
			return null;
		}
		
		 */
		
		//continue nearby

		return "No";
		
	}	
}