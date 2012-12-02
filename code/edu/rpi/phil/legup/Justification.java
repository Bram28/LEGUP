package edu.rpi.phil.legup;

import javax.swing.ImageIcon;

/**
 * An abstract class representing all types of Justifications
 *
 */
public abstract class Justification implements java.io.Serializable
{
	static final long serialVersionUID = 9002L;

	String name = "Default Justification";
	protected String description = "A blank justification";
	protected ImageIcon image = null;
	
	public String getName()
	{
		return name;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	public ImageIcon getImageIcon()
	{
		return image;
	}
	
	/**
	 * Determines whether or not an AI should attempt to use this justification
	 * @return true iff the AI should consider the justification
	 */
	public boolean isAIUsable()
	{
		return true;
	}
	
	
	
    //Object methods
	
	public String toString()
	{
		return getName();
	}
	
	public boolean equals(Object other)
	{
		if (other instanceof Justification)
		{
			if(this.getClass() == other.getClass())
				return ((Justification)other).getName() == getName();
		}
		
		return false;
	}

	public void setName(String name) {
		this.name = name;
	}
}
