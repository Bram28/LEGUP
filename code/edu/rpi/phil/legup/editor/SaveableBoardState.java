package edu.rpi.phil.legup.editor;

import java.awt.Point;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import edu.rpi.phil.legup.BoardState;

/* 	This is a static board state that is encodable and decodable using XMLEncoder and
 	XMLDecoder. This is the new format for saving board states.
 	The name of the puzzlemodule is also saved within the puzzle.
 	
 	@author Stan Bak
 */
public class SaveableBoardState
{
	public int height;
	public int width;
	public int[][] boardCells;
	public int[] topLabels;
	public int[] bottomLabels;
	public int[] leftLabels;
	public int[] rightLabels;
	public String puzzleMod;
	public ArrayList <Object> extraData = new ArrayList <Object>();
	public Point location = new Point();
	
    private static String lastLoadedPuzzleModule = null;
	
    public SaveableBoardState() {}
    
    // a bit of a workaround, since boardstates dont store what puzzlemodule they are using
    public static String getLastLoadedPuzzleModule()
    {
    	return lastLoadedPuzzleModule;
    }
    
    public static BoardState loadState(String filename)
    {
    	SaveableBoardState rv = null;
    	
    	try
    	{
	    	XMLDecoder d = new XMLDecoder(
	                new BufferedInputStream(
	                    new FileInputStream(filename)));
			Object result = d.readObject();
			d.close();
			
			rv = (SaveableBoardState)result;
			
			lastLoadedPuzzleModule = rv.puzzleMod;
    	}
    	catch (Exception e)
    	{
    		JOptionPane.showMessageDialog(null,"Error loading: " + e);
    		rv = null;
    	}
    	
    	BoardState state = BoardState.loadFromSaveableBoardState(rv); 
    	
    	if(state == null)
    		JOptionPane.showMessageDialog(null,"Board Load Failed");
    		
    	return state;
    }
    
    public static boolean saveState(BoardState state, String puzzleModuleName, String filename)
    {
    	boolean rv = true;
    	if (!filename.toLowerCase().endsWith(".xml"))
    		filename = filename + ".xml";
    	
    	SaveableBoardState s = state.getAsSaveableBoardState();
    	s.puzzleMod = puzzleModuleName;
    	
    	try
    	{
	    	 XMLEncoder e = new XMLEncoder(
	                 new BufferedOutputStream(
	                     new FileOutputStream(filename)));
			e.writeObject(s);
			e.close();
    	}
    	catch (Exception e)
    	{
    		JOptionPane.showMessageDialog(null,"Error saving: " + e);
    		rv = false;
    	}
    	
    	return rv;
    }

	public int[][] getBoardCells()
	{
		return boardCells;
	}

	public void setBoardCells(int[][] boardCells)
	{
		this.boardCells = boardCells;
	}

	public int[] getBottomLabels()
	{
		return bottomLabels;
	}

	public void setBottomLabels(int[] bottomLabels)
	{
		this.bottomLabels = bottomLabels;
	}

	public int getHeight()
	{
		return height;
	}

	public void setHeight(int height)
	{
		this.height = height;
	}

	public int[] getLeftLabels()
	{
		return leftLabels;
	}

	public void setLeftLabels(int[] leftLabels)
	{
		this.leftLabels = leftLabels;
	}

	public String getPuzzleMod()
	{
		return puzzleMod;
	}

	public void setPuzzleMod(String puzzleMod)
	{
		this.puzzleMod = puzzleMod;
	}

	public int[] getRightLabels()
	{
		return rightLabels;
	}

	public void setRightLabels(int[] rightLabels)
	{
		this.rightLabels = rightLabels;
	}

	public int[] getTopLabels()
	{
		return topLabels;
	}

	public void setTopLabels(int[] topLabels)
	{
		this.topLabels = topLabels;
	}

	public int getWidth()
	{
		return width;
	}

	public void setWidth(int width)
	{
		this.width = width;
	}

	public static void setLastLoadedPuzzleModule(String lastLoadedPuzzleModule)
	{
		SaveableBoardState.lastLoadedPuzzleModule = lastLoadedPuzzleModule;
	}

	public ArrayList<Object> getExtraData()
	{
		return extraData;
	}

	public void setExtraData(ArrayList<Object> extraData)
	{
		this.extraData = extraData;
	}

	public Point getLocation()
	{
		return location;
	}

	public void setLocation(Point location)
	{
		this.location = location;
	}
    
}
