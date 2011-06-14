package edu.rpi.phil.legup.puzzles.masyu;

import java.awt.Point;

/**
 * Adapted code from TreeTent's ExtraTreeTentLink object to use for Masyu's lines.
 */
public class ExtraLine
{
	Point pos1 = new Point();
	Point pos2 = new Point();

	public ExtraLine(Point point1, Point point2)
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
		
		if (o instanceof ExtraLine)
		{
			ExtraLine e = (ExtraLine)o;
			
			rv = (e.pos1.equals( pos1) && e.pos2.equals( pos2)) || 
			(e.pos2.equals( pos1) && e.pos1.equals( pos2));
		}
		
		return rv;
	}
}
