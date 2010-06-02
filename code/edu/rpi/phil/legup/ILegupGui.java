package edu.rpi.phil.legup;

public interface ILegupGui
{
	/**
	 * Displays a puzzle/gui status message to the user.
	 * @param statusMessage Status message to display
	 */
	public void showStatus(String statusMessage);
	
	/**
	 * Displays an error message and closes
	 * @param errorMessage Error message to display
	 */
	public void errorEncountered(String errorMessage);
	
	/**
	 * Reloads the gui, most likely for a puzzle module switch
	 */
	public void reloadGui();
	
	/**
	 * Requests the GUI to prompt for a puzzle
	 */
	public void promptPuzzle();
}
