package edu.rpi.phil.legup.puzzles.masyu;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.ImageIcon;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.CaseRule;

public class CaseBlackSplit extends CaseRule 
{
	public CaseBlackSplit()
	{
		name = "Black Split";
		description = "Black pearls must bend.";
		image = new ImageIcon("images/masyu/Rules/CaseBlackSplit.png");
	}

	public String checkCaseRuleRaw(BoardState state)
	{
		Vector<BoardState> transitions = state.getTransitionsFrom();

		if(transitions.size() == 2)
		{
			//must have one side already known
			BoardState one = transitions.get(0);
			BoardState two = transitions.get(1);

			ArrayList<Point> dif1 = BoardState.getDifferenceLocations(state, one);
			ArrayList<Point> dif2 = BoardState.getDifferenceLocations(state, two);

			ArrayList<Point> dif = new ArrayList<Point>( dif1 );
			dif.retainAll(dif2);

			if(dif.size() != 1)
				return "There must be exactly one common element";
			Point center = dif.get(0);
			int centercontents = state.getCellContents(center.x, center.y);
			if(!Masyu.isBlack(centercontents))
				return "Center cell must be black";
			int direction;
			switch(centercontents-0x20)
			{
			case Masyu.NORTH: direction = BoardAccessor.NORTH;break;
			case Masyu.EAST: direction = BoardAccessor.EAST;break;
			case Masyu.SOUTH: direction = BoardAccessor.SOUTH;break;
			case Masyu.WEST: direction = BoardAccessor.WEST;break;
			default: return "Origional direction cell must have exactly one line";
			}
			BoardAccessor left = new BoardAccessor(one,state,direction,center.x,center.y);
			BoardAccessor right;
			if(!left.hasDir(left.getDestCell(0, 0), BoardAccessor.WEST))
			{
				right = left;
				left = new BoardAccessor(two,state,direction,center.x,center.y);
				if(!left.hasDir(left.getDestCell(0,0), BoardAccessor.WEST))
					return "You must split to the left and right of the origional";
				ArrayList<Point> atemp = dif1;
				BoardState btemp = one;
				dif1 = dif2; one = two;
				dif2 = atemp; two = btemp;
			} else {
				right = new BoardAccessor(two,state,direction,center.x,center.y);
			}
			if(!right.hasDir(right.getDestCell(0, 0), BoardAccessor.EAST))
				return "You must split to the left and right of the origional";

			//now check to see if the remainer of what they added was valid
			dif1.remove(center);
			dif2.remove(center);
			if(dif1.size() > 2 || dif2.size() > 2)
				return "You cannot make more than two other changes";
			left.turn(BoardAccessor.WEST);
			left.move(BoardAccessor.NORTH);
			right.turn(BoardAccessor.EAST);
			right.move(BoardAccessor.NORTH);
			Point p = new Point(left.getX(), left.getY());
			int value;
			{
				dif1.remove(p);
				value = left.getDestCell(0,0);
				if(left.hasDir(value, BoardAccessor.EAST) || left.hasDir(value, BoardAccessor.WEST))
					return "Only straight line to the left";
				left.move(BoardAccessor.NORTH);
				p.x = left.getX(); p.y = left.getY();
				dif1.remove(p);
				if(!left.validConnection(left.getDestCell(0, 0), BoardAccessor.SOUTH))
					return "Must be able to have line back to black two to left"; 
				if(dif1.size() != 0)
					return "You added too much";
			}
			{
				p.setLocation(right.getX(), right.getY());
				dif2.remove(p);

				value = right.getDestCell(0,0);
				if(right.hasDir(value, BoardAccessor.EAST) || right.hasDir(value, BoardAccessor.WEST))
					return "Only straight line to the right";

				right.move(BoardAccessor.NORTH);
				p.x = right.getX(); p.y = right.getY();
				dif2.remove(p);

				if(!right.validConnection(right.getDestCell(0, 0), BoardAccessor.SOUTH))
					return "Must be able to have line back to black two to right"; 
				if(dif2.size() != 0)
					return "You added too much";
			}
			return null;		
		}
		if(transitions.size() == 4)
		{
			BoardState dirs[] = new BoardState[4];
			ArrayList<Point> c = BoardState.getDifferenceLocations(state, transitions.get(0)),
			difs[] = new ArrayList[4];

			for(int i = 0; i < 4; i++)
			{
				dirs[i] = transitions.get(i);
				difs[i] = BoardState.getDifferenceLocations(state,dirs[i]);
				c.retainAll(difs[i]);	
			}

			if(c.size() != 1)
				return "There must be exactly one common change";
			Point common = c.get(0);
			int centerVal = state.getCellContents(common.x, common.y);
			if(!Masyu.isBlack(centerVal))
				return "Center must be black";

			//use turning boardaccessor to make sure that we have the 4 required cases
			// and that each case is good.
			BoardAccessor ba = new BoardAccessor(null,state,0,common.x,common.y);

			for(int i = 0; i < 4; i++)	
			{
				ba.setDir(i);
				int j;
				for(j = 0; j < 4; j++)
				{
					int cell = ba.getCell(0, 0, dirs[j]);
					if(ba.hasDir(cell, BoardAccessor.NORTH) && 
							ba.hasDir(cell,BoardAccessor.EAST) &&
							!ba.hasDir(cell,BoardAccessor.SOUTH) &&
							!ba.hasDir(cell, BoardAccessor.WEST))
						break;
				}
				if(j == 4)
					return "You don't have all 4 directions";
				if(!difs[j].remove(common))
					return "You are repeating states";
				//boolean done = false;
				System.out.print("Left: " + ba.getDir() + difs[j].size());
				for(int k = 0; k < 2; k++)
				{
					ba.move(BoardAccessor.NORTH);
					if(difs[j].remove(new Point(ba.getX(), ba.getY())))
						System.out.print("Remove1");

					int current = ba.getCell(0, 0, dirs[j]);
					if(ba.hasDir(current,BoardAccessor.EAST) || ba.hasDir(current, BoardAccessor.WEST))
						return "One side has invalid directions, use two branch version";
					ba.move(BoardAccessor.NORTH);
					if(difs[j].remove(new Point(ba.getX(), ba.getY())))
							System.out.println("Remove2");
					if(!ba.validConnection(ba.getCell(0, 0, dirs[j]), BoardAccessor.SOUTH))
						return "Have to have the ability to add two out, try two branch version";
					ba.move(BoardAccessor.SOUTH);
					ba.move(BoardAccessor.SOUTH);

					ba.turn(BoardAccessor.EAST);
					
				}
				System.out.println();
				if(difs[j].size() > 0)
					return "You made changes which weren't in line with what was going on.";

			}
			return null;
		}

		return "Invalid number of transitions, must be either 2 or 4";
	}

}
