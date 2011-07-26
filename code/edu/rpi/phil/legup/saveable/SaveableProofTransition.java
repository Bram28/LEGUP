package edu.rpi.phil.legup.saveable;

import java.awt.Point;
import java.util.Vector;

public class SaveableProofTransition
{
	//transition from
	public int id1 = -1;
	//transition to
	public int id2 = -1;
	//board coords of effected cells
	public Vector <Point> changed;
	//
	//reason why
	public String justification = null;
	//case rule?
	public boolean isCaseRule;
	
	public SaveableProofTransition(){}
	
	public SaveableProofTransition(int id1, int id2, String justification, boolean isCase)
	{
		this.id1 = id1;
		this.id2 = id2;
		this.justification = justification;
		this.isCaseRule = isCase;
	}

	public int getId1()
	{
		return id1;
	}

	public void setId1(int id1)
	{
		this.id1 = id1;
	}

	public int getId2()
	{
		return id2;
	}

	public void setId2(int id2)
	{
		this.id2 = id2;
	}

	public String getJustification()
	{
		return justification;
	}

	public void setJustification(String justification)
	{
		this.justification = justification;
	}

	public boolean isCaseRule()
	{
		return isCaseRule;
	}

	public void setCaseRule(boolean isCaseRule)
	{
		this.isCaseRule = isCaseRule;
	}
}
