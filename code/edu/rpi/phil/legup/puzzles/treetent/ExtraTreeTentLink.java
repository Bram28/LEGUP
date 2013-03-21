package edu.rpi.phil.legup.puzzles.treetent;

import java.awt.Point;

public class ExtraTreeTentLink implements java.io.Serializable
{
	static final long serialVersionUID = 9010L;
	
	Point pos1 = new Point();
	Point pos2 = new Point();

	public ExtraTreeTentLink(Point point1, Point point2)
	{
		//Order the input lowest to highest
		if(point2.y == point1.y)
		{
			if(point2.x < point1.x)
			{
				this.pos1 = point2;
				this.pos2 = point1;
			}
			else
			{
				this.pos1 = point1;
				this.pos2 = point2;
			}
		}
		else if(point2.y < point1.y)
		{
			this.pos1 = point2;
			this.pos2 = point1;
		}
		else
		{
			this.pos1 = point1;
			this.pos2 = point2;
		}
	}
	
	public boolean equals(Object o)
	{
		boolean rv = false;
		
		if (o instanceof ExtraTreeTentLink)
		{
			ExtraTreeTentLink e = (ExtraTreeTentLink)o;
			
			rv = (e.pos1.equals( pos1) && e.pos2.equals( pos2)) || 
			(e.pos2.equals( pos1) && e.pos1.equals( pos2));
		}
		
		return rv;
	}
	//serializes the data for load/save proof
	public String toString()
	{
		//intended to be splitted first by ;, then by ,
		return pos1.x + "," + pos1.y + "," + pos2.x + "," + pos2.y;
	}
}
