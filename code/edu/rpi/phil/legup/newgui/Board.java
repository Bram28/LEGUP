package edu.rpi.phil.legup.newgui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.Vector;
import edu.rpi.phil.legup.BoardDrawingHelper;
import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.Legup;
import edu.rpi.phil.legup.PuzzleModule;
import edu.rpi.phil.legup.Selection;

public class Board extends DynamicViewer implements BoardDataChangeListener
{
	private static final long serialVersionUID = -2304281047341398965L;

	private LEGUP_Gui parent = null;
	private Point lastMousePoint = null; // the last left click mouse location

	Board(LEGUP_Gui gui)
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
		BoardDrawingHelper.draw(g);
	}

	protected void mousePressedAt(Point p, MouseEvent e)
	{
		Selection selection = Legup.getInstance().getSelections().getFirstSelection();

		//The board concerned with receiving input for states
		if (!selection.isState())
		{
			parent.showStatus("You can not modify transitions.");
			return;
		}

		BoardState state = selection.getState();
		Vector<BoardState> parentStates = state.getTransitionsTo();

		PuzzleModule pm = Legup.getInstance().getPuzzleModule();

		if(pm == null)
			return; //This doesn't make sense but it was already here

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
			else if (parentStates.size() == 0)
			{
				// can't add to the root state, print an error
				parent.showStatus("You can not change the initial state.");
			}
			else if (state.getTransitionsFrom().size() > 0 && LEGUP_Gui.profFlag(LEGUP_Gui.INTERN_RO))
			{
				parent.showStatus("You cannot modify internal nodes in this proof mode");
			}
			else
			{
				if (p.x >= 0 && p.y >= 0)
				{
					lastMousePoint = new Point(p);
					if (p.x < w && p.y < h)
					{ // p.x and p.y hold the grid point now!

						if (state.isModifiableCell(p.x,p.y))
						{
							pm.mousePressedEvent(state,p);

							repaint();
						}
						else
							parent.showStatus("You are not allowed to change that cell.");
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
			BoardState cur = selection.getState();
			Legup.getInstance().getSelections().setSelection(new Selection(cur.addTransitionFrom(),false));
		}
		else if (e.getButton() == MouseEvent.BUTTON1)
		{
			BoardState state = selection.getState();
			Vector<BoardState> parentStates = state.getTransitionsTo();

			if (lastMousePoint != null && parentStates.size() > 0) // not root state
			{
				PuzzleModule pz = Legup.getInstance().getPuzzleModule();
				Dimension d = pz.getImageSize();
				int imW = d.width;
				int imH = d.height;
				int w  = state.getWidth();
				int h = state.getHeight();

				p.x -= imW;
				p.y -= imH;

				if (p.x > 0 && p.y > 0)
				{
					p.x /= imW;
					p.y /= imH;

					if (p.x < w && p.y < h)
					{ // p.x and p.y hold the grid point now!
						pz.mouseDraggedEvent(state,lastMousePoint,p);
						repaint();
					}
				}
			}
		}
	}

	public void boardDataChanged(BoardState state)
	{
		repaint();
	}
}
