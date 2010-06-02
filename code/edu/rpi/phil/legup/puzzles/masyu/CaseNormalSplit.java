package edu.rpi.phil.legup.puzzles.masyu;

import java.awt.Point;
import java.util.ArrayList;

import javax.swing.ImageIcon;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.CaseRule;

public class CaseNormalSplit extends CaseRule {
	
	
	public CaseNormalSplit()
	{
		name = "Normal Split";
		description = "They must go somewhere.";
		image = new ImageIcon("images/masyu/Rules/CaseNormalSplit.png");
	}

	public String checkCaseRuleRaw(BoardState state)
	{
		if (state.getTransitionsFrom().size() == 1)
		{
			return "You must split";
		}
		if(state.getTransitionsFrom().size() > 3)
		{
			return "There can be at most 3 ways to split";
		}
		boolean n,s,e,w;
		n = s = e = w = false;
		//check first 2, then check for third
		ArrayList<Point> d1 = BoardState.getDifferenceLocations(state, state.getTransitionsFrom().get(0));
		ArrayList<Point> d2 = BoardState.getDifferenceLocations(state, state.getTransitionsFrom().get(1));
		
		ArrayList<Point> intersection = new ArrayList<Point>(d1);
		intersection.retainAll(d2);
		if(intersection.size() != 1)
		{
			return "Changes must center around a single location";
		}
		Point pmain = intersection.get(0);
		int mainVal = state.getCellContents(pmain.x, pmain.y);
		
		switch(mainVal)
		{
		case Masyu.NORTH:n = true;break;
		case Masyu.SOUTH:s = true;break;
		case Masyu.EAST:e = true;break;
		case Masyu.WEST:w = true;break;
		default: return "Original state must have only one path spot and be blank";
		}
		for(BoardState child:state.getTransitionsFrom())
		{
			ArrayList<Point> dif;
			if(child == state.getTransitionsFrom().get(0))
				dif = d1;
			else if(child == state.getTransitionsFrom().get(1))
				dif = d2;
			else
				dif = BoardState.getDifferenceLocations(state, child);
			if(dif.size() == 0)
				return "You must change the children";
			if(dif.size() > 2)
				return "You can change at most two cells at a time";
			//newVal should only have one spot
			int newVal = child.getCellContents(pmain.x, pmain.y) ^ mainVal;
			switch(newVal)
			{
			case Masyu.NORTH:if(n) return "Use different paths"; else n = true;break;
			case Masyu.SOUTH:if(s) return "Use different paths"; else s = true;break;
			case Masyu.EAST:if(e) return "Use different paths"; else e = true;break;
			case Masyu.WEST:if(w) return "Use different paths"; else w = true;break;
			default: return "Children must have only one path off";
			}
			if(dif.size() == 2)
			{
				Point p = dif.get(0);
				if(p.equals(pmain))
					p = dif.get(1);
				
				BoardAccessor ba = new BoardAccessor(child,state,0,p.x,p.y);
				
				switch(newVal)
				{
				case Masyu.NORTH: ba.setDir(BoardAccessor.NORTH);break;
				case Masyu.EAST: ba.setDir(BoardAccessor.EAST);break;
				case Masyu.SOUTH: ba.setDir(BoardAccessor.SOUTH);break;
				case Masyu.WEST: ba.setDir(BoardAccessor.WEST);break;
				}
				if(p.x != ba.getX() || p.y != ba.getY())
					return "Second point must be a result of picking first point.";
				int change = ba.getOrigCell(0,0) ^ ba.getDestCell(0,0); 
				
				//bitwise flip between NS and EW
				if((((newVal ^ 5) & ~10) != change && (((newVal ^ 10) & ~5)) != change))
					return "Only change allowed in second point is to finish the line"; 
			}
		}
		if(!(n && s && e && w))
		{
			
			BoardAccessor ba = new BoardAccessor(null,state,BoardAccessor.NORTH,pmain.x,pmain.y);
			//check for missing
			
			if(!n)
				if(ba.validConnection(ba.getOrigCell(0, 1), BoardAccessor.SOUTH))
					return "Must have a path to the North";
			if(!s)
				if(ba.validConnection(ba.getOrigCell(0, -1), BoardAccessor.NORTH))
					return "Must have a path to the South";
			if(!e)
				if(ba.validConnection(ba.getOrigCell(1, 0), BoardAccessor.WEST))
					return "Must have a path to the East";
			if(!w)
				if(ba.validConnection(ba.getOrigCell(-1, 0), BoardAccessor.EAST))
					return "Must have a path to the West";
		}
		
		return null;
	}

}
