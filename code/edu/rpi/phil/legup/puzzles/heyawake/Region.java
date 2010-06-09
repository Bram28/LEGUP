package edu.rpi.phil.legup.puzzles.heyawake;

import java.util.Vector;
import edu.rpi.phil.legup.puzzles.heyawake.CellLocation;
import edu.rpi.phil.legup.BoardState;

public class Region
{
	private int value;
	private Vector <CellLocation> cells;
	
	public Region()
	{
		this.value = 0;
		cells = new Vector <CellLocation>();
	}
	
	public Region(int value)
	{
		this.value = value;
		cells = new Vector <CellLocation>();
	}
	
	public void addCell(CellLocation cell)
	{
		cells.add(cell);
	}
	
	public void addCell(int x, int y)
	{
		cells.add(new CellLocation(x,y));
	}
	
	public CellLocation getCell(int indx)
	{
		return cells.elementAt(indx);
	}
	
	public int getCellIndex(int x, int y)
	{
		if(cells.size() == 0)
			return -1;
		int cnt;
		for(cnt = 0; cnt < cells.size(); ++cnt)
		{
			if((cells.elementAt(cnt)).getX() == x && (cells.elementAt(cnt)).getY() == y)
				break;
		}
		if(cnt >= cells.size())
			return -1;
		return cnt;
	}
	
	public void removeCell(int indx)
	{
		cells.removeElementAt(indx);
	}
	
	public void removeCell(int x, int y)
	{
		int temp = getCellIndex(x,y);
		if(temp != -1)
			cells.removeElementAt(temp);
	}
	
	public int getValue()
	{
		return value;
	}
	
	public Vector<CellLocation> getCells()
	{
		return cells;
	}
	
	public CellLocation getDimensions()
	{
		int x1, x2, y1, y2;
    	CellLocation tempcell;
    	
		if(cells.size() > 0)
		{
			x1 = x2 = cells.get(0).getX();
			y1 = y2 = cells.get(0).getY();
    		for(int c = 0; c < cells.size(); ++c)
    		{
    			tempcell = cells.get(c);
    			if(tempcell.getX() < x1)
    				x1 = tempcell.getX();
    			if(tempcell.getX() > x2)
    				x2 = tempcell.getX();
    			if(tempcell.getY() < x1)
    				y1 = tempcell.getY();
    			if(tempcell.getY() > y2)
    				y2 = tempcell.getY();
    		}
    		return new CellLocation((x2-x1), (y2-y1));
		}
		return null;
	}
	
	public void setValue(int value)
	{
		this.value = value;
	}
	
	public void setCells(Vector <CellLocation> cells)
	{
		this.cells = cells;
	}
	
	public Region copy()
	{
		Region tempRegion = new Region();
		tempRegion.value = this.value;
		tempRegion.cells = new Vector<CellLocation>(this.cells);
		return tempRegion;
	}
	
    public static Vector <Region> getRegions(Region[] boardRegions, int regionCount)
    {
    	Vector <Region> tempvec = new Vector <Region>();
    	if(regionCount > 0)
    	{
    		for(int cnt = 0; cnt < regionCount; ++cnt)
    		{
    			tempvec.add(boardRegions[cnt].copy());
    		}
    	}
    	return tempvec;
    }
    
    public static Vector<Region> getRegions(BoardState state)
    {
    	return getRegions((Region[])state.getExtraData().get(0), ((Integer)state.getExtraData().get(1)).intValue());
    }
    
    public static BoardState setRegions(Vector <Region> regionVector, BoardState state)
    {
   		Region[] temparray = new Region[regionVector.size()];
    	regionVector.toArray(temparray);
    	state.getExtraData().set(0, temparray);
    	state.getExtraData().set(1, Integer.valueOf(regionVector.size()));
    	return state;
    }
}
