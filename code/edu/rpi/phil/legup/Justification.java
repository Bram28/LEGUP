package edu.rpi.phil.legup;

import javax.swing.ImageIcon;
import java.io.IOException;
import java.lang.ClassNotFoundException;
import java.io.ObjectStreamException;
import java.net.URL;

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
	
	public Justification()
	{
		name = "Default Justification";
		description = "A blank justification";
		loadImage();
	}
	
	public abstract String getImageName();// {return "images/unknown.gif";}
	
	public void loadImage()
	{
		String imageName = getImageName();
		if(imageName != null)
		{
			URL res = ClassLoader.getSystemResource(imageName);
			if(res == null)
			{
				throw new Error(String.format("Image \"%s\" does not exist (needed for \"%s\")", imageName, this.getClass()));
			}
			image = new ImageIcon(res);
		}
	}
	
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
	
	//methods from Serializable are given custom implementations to work around ImageIcon having different
	//serial IDs in different versions of java
	private void writeObject(java.io.ObjectOutputStream stream)throws IOException
	{
		stream.writeObject(name);
		stream.writeObject(description);
	}
	private void readObject(java.io.ObjectInputStream stream)throws IOException, ClassNotFoundException
	{
		name = (String)stream.readObject();
		description = (String)stream.readObject();
		loadImage();
	}
	private void readObjectNoData()throws ObjectStreamException
	{
		name = "Default Justification";
		description = "A blank justification";
		loadImage();
	}
}
