package edu.rpi.phil.legup;

import java.awt.Point;
import java.util.Vector;

public class RuleApplication
{
	public boolean isValid = false;
	
	public int newValue = 0;
	public Point location = null;
	
	public boolean isParent = false;
	public Point parentApplication = null;
	public Vector<RuleApplication> children = null;
	
	public RuleApplication(Point at)
	{
		//invalid rule, default
		location = at;
	}
}
