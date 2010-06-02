package edu.rpi.phil.legup.newgui;

import java.util.ArrayList;

import edu.rpi.phil.legup.Selection;

/**
 * This object defines one that listens to changes in board states
 * @author Stan
 *
 */
public interface TreeSelectionListener
{
	/**
	 * The tree selection has changed
	 * @param newSelection the new Selection
	 */
	public void treeSelectionChanged(ArrayList <Selection> newSelection);
}
