package edu.rpi.phil.legup.newgui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.Vector;
import java.util.List;
import javax.swing.JComponent;
import edu.rpi.phil.legup.BoardDrawingHelper;
import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.Legup;
import edu.rpi.phil.legup.PuzzleModule;
import edu.rpi.phil.legup.Selection;

public class NormalBoard extends Board
{
	private static final long serialVersionUID = -2304281047341398965L;
	private LEGUP_Gui parent = null;
	private Point lastMousePoint = null; // the last left click mouse location
	private Point lastRightMousePoint = null;
	private int count = 0;

	NormalBoard(LEGUP_Gui gui)
	{
		parent = gui;

		BoardState.addCellChangeListener(this);

		setBackground( new Color(0xE0E0E0) );

	}

	public void initSize()
	{ // initialize the size of the panel (can't do it on init because we don't know the size of tile)
		//setPreferredSize(getProperSize());
		//revalidate();
		setSize( getProperSize() );
		zoomFit();
	}

	private Dimension getProperSize()
	{
		Dimension rv = new Dimension();
		Selection selection = Legup.getInstance().getSelections().getFirstSelection();

		if (selection.isState())
		{
			BoardState state = selection.getState();
			PuzzleModule pz = Legup.getInstance().getPuzzleModule();

			if (pz != null)
			{
				Dimension d = pz.getImageSize();
				int w  = state.getWidth();
				int h = state.getHeight();

				rv.width = d.width * (w + 2);
				rv.height = d.height * (h + 2);
			}
		}

		return rv;
	}

	protected void draw( Graphics2D g )
	{
		count++;
		//System.out.println("Redrawing number " + count);
		BoardDrawingHelper.draw(g,null);
	}

	protected void mousePressedAt(Point p, MouseEvent e)
	{
		Selection selection = Legup.getInstance().getSelections().getFirstSelection();

		//The board concerned with receiving input for states
		if (!selection.isState())
		{
			parent.showStatus("You can not modify transitions.", true);
			return;
		}

		BoardState state = selection.getState();
		List<BoardState> parentStates = state.getParents();

		PuzzleModule pm = Legup.getInstance().getPuzzleModule();

		if(pm == null)return; //Don't respond to clicks on the board if no puzzle is loaded

		if (e.getButton() == MouseEvent.BUTTON3)
		{
			int w = state.getWidth();
			int h = state.getHeight();
			p = mouseCoordsToGridCoords(state, pm, p);
			if(pm.defaultApplication != null)
			{
				JustificationFrame.justificationApplied(state,pm.defaultApplication);
				pm.defaultApplication.doDefaultApplication(state,pm,p);
				pm.defaultApplication = null;
			}
			else if (state.getChildren().size() > 0 && LEGUP_Gui.profFlag(LEGUP_Gui.INTERN_RO))
			{
				parent.showStatus("You cannot modify internal nodes in this proof mode", true);
			}
			else
			{
				if (p.x >= 0 && p.y >= 0)
				{
					if (p.x < w && p.y < h)
					{ // p.x and p.y hold the grid point now!

						if (state.isModifiableCell(p.x,p.y) || pm.isRemodifiable(state.getSingleParentState().getCellContents(p.x, p.y)))
						{
							new ChangeBoardCell(p).getPopupMenu().show(this,
								e.getX() + ((JComponent)e.getSource()).getX(),
								e.getY() + ((JComponent)e.getSource()).getY()
							);
							lastRightMousePoint = p;
						}
						else {
							System.out.println("TESTING");
							parent.showStatus("You are not allowed to chage that cell.", true);
						}
					}
				}
			}
		}
		if (e.getButton() == MouseEvent.BUTTON1)
		{
			Dimension d = pm.getImageSize();
			int imW = d.width;
			int imH = d.height;
			int w  = state.getWidth();
			int h = state.getHeight();
			lastMousePoint = null;

			p.x -= imW;
			p.y -= imH;

			p.x = (int)(Math.floor((double)p.x/imW));
			p.y = (int)(Math.floor((double)p.y/imH));

			if(pm.defaultApplication != null)
			{
				JustificationFrame.justificationApplied(state,pm.defaultApplication);
				pm.defaultApplication.doDefaultApplication(state,pm,p);
				pm.defaultApplication = null;
			}
			/*else if (parentStates.size() == 0)
			{
				// can't add to the root state, print an error
				//parent.showStatus("You can not change the initial state.", true);
			}*/
			else if (state.getChildren().size() > 0 && LEGUP_Gui.profFlag(LEGUP_Gui.INTERN_RO))
			{
				parent.showStatus("You cannot modify internal nodes in this proof mode", true);
			}
			else
			{
				if (p.x >= 0 && p.y >= 0)
				{
					lastMousePoint = new Point(p);
					if (p.x < w && p.y < h)
					{ // p.x and p.y hold the grid point now!
						if (state.isModifiableCell(p.x,p.y) || (state.getSingleParentState() != null && pm.isRemodifiable(state.getSingleParentState().getCellContents(p.x, p.y))))
						{
							BoardState next = state.conditionalAddTransition();
							if(next != null) { pm.mousePressedEvent(next,p); }
						}
						else
                        {
							parent.showStatus("You are not allowed to change that cell.", true);
                        }
					}
				}

				if (p.x == -1 && p.y >= 0 && p.y < h)
					pm.labelPressedEvent(state, p.y, BoardState.LABEL_LEFT);
				else if (p.y == -1 && p.x >= 0 && p.y < w)
					pm.labelPressedEvent(state, p.x, BoardState.LABEL_TOP);
				else if (p.x == w && p.y >= 0 && p.y < h)
					pm.labelPressedEvent(state, p.y, BoardState.LABEL_RIGHT);
				else if (p.y == h && p.x >= 0 && p.x < w)
					pm.labelPressedEvent(state, p.x, BoardState.LABEL_BOTTOM);

			}
		}
	}


	protected void mouseReleasedAt(Point p, MouseEvent e)
	{
		Selection selection = Legup.getInstance().getSelections().getFirstSelection();
		if (!selection.isState())
			return;

		if (e.getButton() == MouseEvent.BUTTON3)
		{
			/* old rightclick stuff, adding changes. I'm replacing this with rightclick menu stuff - Avi
			  BoardState cur = selection.getState();

			if (cur.isModifiable())
				Legup.getInstance().getSelections().setSelection(new Selection(cur.endTransition(), false));
			*/
			//putting the code for rightclick menu in the above mousePressedAt() function
		}
		else if (e.getButton() == MouseEvent.BUTTON1)
		{
			BoardState state = selection.getState();
			List<BoardState> parentStates = state.getParents();

			if (lastMousePoint != null && parentStates.size() > 0) // not root state
			{
				int w = state.getWidth();
				int h = state.getHeight();
				PuzzleModule pm = Legup.getInstance().getPuzzleModule();
				p = mouseCoordsToGridCoords(state, pm, p);
				if((p.x >= 0 && p.y >= 0) && (p.x < w && p.y < h))
				{
					pm.mouseDraggedEvent(state,lastMousePoint,p);
				}
			}
		}
	}

	public void boardDataChanged(BoardState state)
	{
		repaint();
	}
}
