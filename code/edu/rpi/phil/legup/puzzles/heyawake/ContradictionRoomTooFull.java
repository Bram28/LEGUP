package edu.rpi.phil.legup.puzzles.heyawake;

import java.util.Vector;

import javax.swing.ImageIcon;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.Contradiction;

public class ContradictionRoomTooFull extends Contradiction
{	 
	private static final long serialVersionUID = 969529532L;

	 ContradictionRoomTooFull()
	 {
		setName("Room Too Full");
		description = "A room can't have more than its number of blacks.";
		image = new ImageIcon("images/heyawake/contradictions/RoomTooFull.png");
	 }
	 
	 /**
	 * Checks if the contradiction was applied correctly to this board state
	 *
	 * @param state The board state
	 * @return null if the contradiction was applied correctly, the error String otherwise
	 */
	protected String checkContradictionRaw(BoardState state)
	{
		String error = null;

		int countwhite, countblack, countunknown, cellval;
		Vector<CellLocation> cells;
		CellLocation tempcell;
		Region[] regions = (Region[])state.getExtraData().get(0);
		int regionCount = ((Integer)(state.getExtraData().get(1))).intValue();
		for(int indx = 0; indx < regionCount; ++indx)
		{
			countwhite = countblack = countunknown = 0;
			cells = regions[indx].getCells();
			if(cells.size() > 0)
			{
				for(int c = 0; c < cells.size(); ++c)
				{
					tempcell = cells.get(c);
					cellval = state.getCellContents(tempcell.x, tempcell.y);
					if(cellval == 1)
					{
						++countwhite;
					}
					else if(cellval == 2)
					{
						++countblack;
					}
					else
					{
						++countunknown;
					}
				}
				if(countblack > regions[indx].getValue() && regions[indx].getValue() > -1)
				{
					return error;
				}
			}
			
		}
		
		error = "No room has too many black cells in it.";

		return error;
	}
}
