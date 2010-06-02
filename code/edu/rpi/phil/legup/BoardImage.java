package edu.rpi.phil.legup;

import java.awt.Image;

import javax.swing.ImageIcon;

public class BoardImage
{
	public Image i = null;
	public int boardIndex = -1;
	
	public BoardImage()
	{
		i = null;
		boardIndex = -1;
	}
	
	public BoardImage(Image im, int index)
	{
		i = im;
		boardIndex = index;
	}
	
	public BoardImage(String path, int index)
	{
		i = new ImageIcon(path).getImage();
		boardIndex = index;
	}
}
