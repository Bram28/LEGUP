package edu.rpi.phil.legup.puzzles.heyawake;

public class CellLocation
{
	public int x;
	public int y;

	public CellLocation(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	public CellLocation()
	{
		this.x = 0;
		this.y = 0;
	}
	
	public int getX( )
	{
		return x;
	}

	public void setX( int x )
	{
		this.x = x;
	}

	public int getY( )
	{
		return y;
	}

	public void setY( int y )
	{
		this.y = y;
	}
	public String toString()
	{
		return ""+x+", "+y;
	}
}
