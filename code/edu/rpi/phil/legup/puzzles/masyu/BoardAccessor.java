package edu.rpi.phil.legup.puzzles.masyu;

import edu.rpi.phil.legup.BoardState;

public class BoardAccessor {
	public BoardAccessor(BoardState destBoardState, BoardState origBoardState, int direction, int x, int y)
	{
		this.destBoardState = destBoardState;
		this.origBoardState = origBoardState;
		this.direction = direction % 4;
		this.bx = x;
		this.by = y;
	}
	public BoardAccessor(BoardAccessor ba)
	{
		this.destBoardState = ba.destBoardState;
		this.origBoardState = ba.origBoardState;
		this.direction = ba.direction % 4;
		this.bx = ba.bx;
		this.by = ba.by;
	}
	public BoardState getDestBoardState()
	{
		return destBoardState;
	}
	public BoardState getOrigBoardState()
	{
		return origBoardState;
	}
	public int getOrigCell(int x, int y)
	{
		return getCell(x,y,origBoardState);
	}
	public int getDestCell(int x, int y)
	{
		return getCell(x,y,destBoardState);	
	}
	/**
	 * Returns true if it is possible for a wall to be in a certain direction
	 * @param cellValue value of the cell
	 * @param direction direction to add wall
	 * @return True iff there is no wall there and one can be added
	 */
	public boolean validConnection(int cellValue, int direction)
	{
		if(cellValue == -1)
			return false;
		if(hasDir(cellValue,direction))
			return true;
		int other = 0;
		for(int i = 0; i < 4; i++)
			if(i != direction && hasDir(cellValue,i))
				other++;
		return other < 2;
	}
	public int getCell(int i, int j, BoardState state)
	{
		if(state == null)
			return -1;
		int nx = -1, ny = -1;
		switch(direction)
		{
		case NORTH:	{nx = bx + i;ny = by - j;break;}
		case SOUTH: {nx = bx - i;ny = by + j;break;}
		case EAST: {nx = bx + j;ny = by + i;break;}
		case WEST: {nx = bx - j;ny = by - i;break;}
		}
		if(nx < 0 || ny < 0)
			return -1;
		if(nx > state.getWidth() - 1 || ny > state.getHeight() - 1)
			return -1;
		return state.getCellContents(nx, ny);
	}
	public void move(int dir)
	{
		int newDir = direction + dir;
		newDir %= 4;
		switch(newDir)
		{
		case NORTH: by--;break;
		case EAST: bx++;break;
		case SOUTH: by++;break;
		case WEST: bx--;break;
		}
	}
	public boolean hasDir(int cellValue, int dir)
	{
		if(cellValue == -1)
			return false;
		int newdir = direction + dir;
		newdir %= 4;
		switch(newdir)
		{
		case NORTH:return Masyu.hasNorth(cellValue);
		case EAST: return Masyu.hasEast(cellValue);
		case SOUTH:return Masyu.hasSouth(cellValue);
		case WEST: return Masyu.hasWest(cellValue);
		}
		return false;
	}
	public final boolean isBlack(int cellValue)
	{
		return cellValue != -1 && Masyu.isBlack(cellValue);
	}
	public final boolean isWhite(int cellValue)
	{
		return cellValue != -1 && Masyu.isWhite(cellValue);
	}
	public int getX(){return bx;}
	public int getY(){return by;}
	public int getDir(){return direction;}
	public void setX(int x){bx = x;}
	public void setY(int y){by = y;}
	public void setDir(int dir){direction = dir%4;}
	public void turn(int dir){direction = (direction + dir)%4;}
	private BoardState destBoardState, origBoardState;
	private int direction;
	private int bx,by;
	public static final int NORTH = 0;
	public static final int EAST = 1;
	public static final int SOUTH = 2;
	public static final int WEST = 3;
	public String toString()
	{
		return "(" + bx + "," + by + ")";
	}
	
}