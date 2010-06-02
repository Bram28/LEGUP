package edu.rpi.phil.legup.saveable;

import java.awt.Point;
import java.util.ArrayList;


public class SaveableProofState
{
    public int height;
    public int width;
    public int[][] boardCells;
    public boolean[][] modifiableCells;
    public int[] topLabels;
    public int[] bottomLabels;
    public int[] leftLabels;
    public int[] rightLabels;
    public String puzzleName;
    public ArrayList <Object> extraData = new ArrayList <Object>();
	public boolean collapsed;
	public ArrayList<Point> hintCells;
	public Point offset;
	
	
	
    public SaveableProofState(){}
	
	//Getters & Setters
	public int getHeight()
	{
		return height;
	}
	public void setHeight(int height)
	{
		this.height = height;
	}
	public int getWidth()
	{
		return width;
	}
	public void setWidth(int width)
	{
		this.width = width;
	}
	public int[][] getBoardCells()
	{
		return boardCells;
	}
	public void setBoardCells(int[][] boardCells)
	{
		this.boardCells = boardCells;
	}
	public boolean[][] getModifiableCells()
	{
		return modifiableCells;
	}
	public void setModifiableCells(boolean[][] modifiableCells)
	{
		this.modifiableCells = modifiableCells;
	}
	public int[] getTopLabels()
	{
		return topLabels;
	}
	public void setTopLabels(int[] topLabels)
	{
		this.topLabels = topLabels;
	}
	public int[] getBottomLabels()
	{
		return bottomLabels;
	}
	public void setBottomLabels(int[] bottomLabels)
	{
		this.bottomLabels = bottomLabels;
	}
	public int[] getLeftLabels()
	{
		return leftLabels;
	}
	public void setLeftLabels(int[] leftLabels)
	{
		this.leftLabels = leftLabels;
	}
	public int[] getRightLabels()
	{
		return rightLabels;
	}
	public void setRightLabels(int[] rightLabels)
	{
		this.rightLabels = rightLabels;
	}
	public String getPuzzleMod()
	{
		return puzzleName;
	}
	public void setPuzzleMod(String puzzleMod)
	{
		this.puzzleName = puzzleMod;
	}
	public ArrayList<Object> getExtraData()
	{
		return extraData;
	}
	public void setExtraData(ArrayList<Object> extraData)
	{
		this.extraData = extraData;
	}
	public boolean isCollapsed()
	{
		return collapsed;
	}
	public void setCollapsed(boolean collapsed)
	{
		this.collapsed = collapsed;
	}
	public ArrayList<Point> getHintCells()
	{
		return hintCells;
	}
	public void setHintCells(ArrayList<Point> hintCells)
	{
		this.hintCells = hintCells;
	}
	public Point getOffset()
	{
		return offset;
	}
	public void setOffset(Point offset)
	{
		this.offset = offset;
	}
}
