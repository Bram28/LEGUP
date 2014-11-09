package edu.rpi.phil.legup.newgui;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.Legup;
import edu.rpi.phil.legup.PuzzleModule;
import edu.rpi.phil.legup.Selection;

// callback used for the right-click functionality of NormalBoard
public class ChangeBoardCell implements ActionListener
{
	JPopupMenu storedMenu;
	Point toChange;
	PuzzleModule pm = Legup.getInstance().getPuzzleModule();

	public ChangeBoardCell(Point p)
	{
		toChange = p;
		storedMenu = new JPopupMenu();
		List<String> menuoptions = pm.getSelectableCellsList();
		for(String option : menuoptions)
		{
			if(option == null)continue;
			JMenuItem item = new JMenuItem(option);

			item.addActionListener(this);
			storedMenu.add(item);
		}
		//storedMenu.show(this,temp.x, temp.y);
	}

	public JPopupMenu getPopupMenu() { return storedMenu; }

	public void actionPerformed(ActionEvent e)
	{
		List<String> menuoptions = pm.getSelectableCellsList();
		// linearly search through the states to find which popup item was clicked (TODO: find better way or confirm none exists)
		for(int a=0; a < menuoptions.size(); a++)
		{
			if(e.getSource() == storedMenu.getComponent(a))
			{
				Selection selection = Legup.getInstance().getSelections().getFirstSelection();
				BoardState state = selection.getState();

				int optionchosen = a;

				BoardState next = state.conditionalAddTransition();
				if(next != null)
				{
					next.setCellContents(toChange.x, toChange.y,
						pm.getStateNumber(menuoptions.get(optionchosen)));

					//make sure annotations don't cover the final result
					pm.disableAnnotationsForCell(toChange.x,toChange.y); 
				}
			}
		}
	}
}
