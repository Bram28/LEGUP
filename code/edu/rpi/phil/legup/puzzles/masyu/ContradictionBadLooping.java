package edu.rpi.phil.legup.puzzles.masyu;

import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.ImageIcon;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.Contradiction;

public class ContradictionBadLooping extends Contradiction {
	
	public ContradictionBadLooping()
	{
		name = "Bad Looping";
		description = "There must be one loop everywhere.";
		image = new ImageIcon("images/masyu/Rules/ContradictionBadLooping.png");
	}

	/**
	 * Checks if the contradiction was applied correctly to this board state
	 *
	 * @param state The board state
	 * @return null if the contradiction was applied correctly, the error String otherwise
	 */
	protected String checkContradictionRaw(BoardState state)
	{
		int height = state.getHeight();
		int width = state.getWidth();
		
		LinkedList<Point> pearls = new LinkedList<Point>();
		int num_pearls = 0;
		for (int y=0;y<height;y++)
		{
			for (int x=0;x<width;x++)
			{
				int value = state.getCellContents(x, y);
				if(Masyu.isBlack(value) || Masyu.isWhite(value))
				{
					num_pearls++;
					if((value & 15) != 0)
						pearls.add(new Point(x,y));
					
				}
			}
		}

		while(pearls.size() != 0)
		{
			Point start = pearls.getFirst();
			int sx = (int)start.getX();
			int sy = (int)start.getY();
			
			BoardAccessor ba = new BoardAccessor(state,null,0,sx,sy);
			ArrayList<Point> found = new ArrayList<Point>();
			if(travel(ba,found))
			{
				if(found.size() != num_pearls)
					return null; //yay, contradiction found
				return "There are no looping problems";
			}
			pearls.removeAll(found);
			pearls.remove(start);
		}
		return "No loops found";
	}
	/**
	 * Travels on board using ba in all available directions from the starting location.
	 * Returns -1 if there is no complete loop, otherwise it returns the total number of
	 * pearls touched.
	 * @param ba
	 * @return
	 */
	protected boolean travel(BoardAccessor ba, ArrayList<Point> pearlsFound)
	{
		int sx = ba.getX(), sy = ba.getY();
		pearlsFound.clear();
		for(int i = 0; i < 4; i++)
		{
			ba.setX(sx);
			ba.setY(sy);
			ba.setDir(i);
			if(!ba.hasDir(ba.getDestCell(0,0), BoardAccessor.NORTH))
				continue;
			ba.move(BoardAccessor.NORTH);
			while(ba.getX() != sx || ba.getY() != sy)
			{
				int ccell = ba.getDestCell(0,0);
				if(ccell >= 0x10) //white or black
					pearlsFound.add(new Point(ba.getX(),ba.getY()));
				if(ba.hasDir(ccell,BoardAccessor.NORTH))
				{}
				else if(ba.hasDir(ccell,BoardAccessor.EAST))
					ba.turn(BoardAccessor.EAST);
				else if(ba.hasDir(ccell, BoardAccessor.WEST))
					ba.turn(BoardAccessor.WEST);
				else
					break;
				ba.move(BoardAccessor.NORTH);
			}
			if(ba.getX() == sx && ba.getY() == sy)
				return true;
		}
		return false;
	}
}
