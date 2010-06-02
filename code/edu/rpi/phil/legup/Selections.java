package edu.rpi.phil.legup;

import java.util.ArrayList;
import edu.rpi.phil.legup.newgui.TreeSelectionListener;

public class Selections
{

	private ArrayList <Selection> currentSelection = new ArrayList <Selection>();

    private ArrayList <TreeSelectionListener> treeSelectionListeners = new ArrayList <TreeSelectionListener>();

	/**
	 * Get the first board state in the selection
	 * @return the first Selection of all of the states we have selected
	 * Should never return null
	 */
	public Selection getFirstSelection()
	{
		if (currentSelection.size() > 0)
			return currentSelection.get(0);
		else
			return new Selection(Legup.getInstance().getInitialBoardState(), false);
	}
    
    /**
     * Gets the currently selected board
     *
     * @return The selection(s)
     */ 
    public ArrayList <Selection> getCurrentSelection()
    {
    	return currentSelection;
    }
    
    /**
     * Toggle this boardstate / transition from the set of selected objects
     * @param s the Selection we're toggling
     */
    public void toggleSelection(Selection s)
    {
    	if (s != null)
    	{	    
    		if (currentSelection.contains(s))
	    	{
    			//Don't remove if it is the last element
        		if(currentSelection.size() == 1)
        			return;
	    		currentSelection.remove(s);
	    	}
    		else
    		{
	    		currentSelection.add(s);
	    	}	    	
	    	
	    	notifySelectionListeners();
    	}
    }
	
    /**
     * Sets the currently selected board state, or transition
     *
     * @param s the new Selection
     */    
    public void setSelection(Selection s)
    {
    	if (s != null)
    	{
    		currentSelection.clear();    	
    	
	    	currentSelection.add(s);
	    	
	    	notifySelectionListeners();
    	}
    }
    
    /**
     * Notify the selection listeners that the selection has changed
     */
    private void notifySelectionListeners()
    {
    	for (TreeSelectionListener l : treeSelectionListeners)
    	{
    		l.treeSelectionChanged(currentSelection);
    	}
    }
    
    public void addTreeSelectionListener(TreeSelectionListener l)
    {
    	treeSelectionListeners.add(l);
    }
}
