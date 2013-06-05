package edu.rpi.phil.legup.newgui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseAdapter;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Vector;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JComponent;
import javax.swing.event.PopupMenuListener;
import edu.rpi.phil.legup.BoardDrawingHelper;
import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.CaseRule;
import edu.rpi.phil.legup.Legup;
import edu.rpi.phil.legup.PuzzleModule;
import edu.rpi.phil.legup.Selection;

public class Board extends DynamicViewer implements BoardDataChangeListener, ActionListener
{
	private static final long serialVersionUID = -2304281047341398965L;
	public int optionchosen;
	private LEGUP_Gui parent = null;
	private Point lastMousePoint = null; // the last left click mouse location
	private Point lastRightMousePoint = null;
	JPopupMenu storedMenu = new JPopupMenu();
	String storedMenuOptions[];
	
	public void actionPerformed(ActionEvent e)
	{ 
		for(int a=0;a<Legup.getInstance().getPuzzleModule().numAcceptableStates();a++)
		{
			if(e.getSource() == storedMenu.getComponent(a))
			{
				Selection selection = Legup.getInstance().getSelections().getFirstSelection();
				BoardState state = selection.getState();
				PuzzleModule pm = Legup.getInstance().getPuzzleModule();

				optionchosen = a;
				
				BoardState next = state.conditionalAddTransition();
				if(next != null)next.setCellContents(lastRightMousePoint.x,lastRightMousePoint.y,pm.getStateNumber(storedMenuOptions[optionchosen]));
			}
		}
	}

	
	class PopupListener extends MouseAdapter {
	        JPopupMenu pop;
	 
	        PopupListener(JPopupMenu popupMenu) {
	            pop = popupMenu;
	        }
	 
	        public void mousePressed(MouseEvent e) {
	            maybeShowPopup(e);
	        }
	 
	        public void mouseReleased(MouseEvent e) {
	            maybeShowPopup(e);
	        }
	 
	        private void maybeShowPopup(MouseEvent e) {
	            if (e.isPopupTrigger()) {
	                pop.show(e.getComponent(),
	                           e.getX(), e.getY());
	            }
	        }
	    }
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

	private int count = 0;
	protected void draw( Graphics2D g )
	{
		count++;
		//System.out.println("Redrawing number " + count);
		BoardDrawingHelper.draw(g,null,0);
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
		Vector<BoardState> parentStates = state.getTransitionsTo();

		PuzzleModule pm = Legup.getInstance().getPuzzleModule();

		if(pm == null)return; //Don't respond to clicks on the board if no puzzle is loaded
		
		if (e.getButton() == MouseEvent.BUTTON3)
		{
			//System.out.println("Right mouse clicked.");
			//copied from MouseEvent.BUTTON1 code below
			Dimension d = pm.getImageSize();
			int imW = d.width;
			int imH = d.height;
			int w  = state.getWidth();
			int h = state.getHeight();

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
			else if (state.getTransitionsFrom().size() > 0 && LEGUP_Gui.profFlag(LEGUP_Gui.INTERN_RO))
			{
				parent.showStatus("You cannot modify internal nodes in this proof mode", true);
			}
			else
			{
				if (p.x >= 0 && p.y >= 0)
				{
					if (p.x < w && p.y < h)
					{ // p.x and p.y hold the grid point now!

						if (state.isModifiableCell(p.x,p.y))
						{
							JPopupMenu pop = new JPopupMenu();
							String[] menuoptions = new String[pm.numAcceptableStates()];

							for(int c1=0;c1<pm.numAcceptableStates();c1++)
							{
								menuoptions[c1] = pm.getStateName(c1);
								//System.out.println("numAcceptableStates: "+ pm.numAcceptableStates());
								//System.out.println("menuoptions["+c1+"]: "+ menuoptions[c1]);
							}
							for(int a = 0; a < pm.numAcceptableStates(); a++)
							{
								if(menuoptions[a] == null)continue;
								JMenuItem item = new JMenuItem(menuoptions[a]);

								item.addActionListener(this);
								pop.add(item);
							}
							pop.show(this,e.getX()+ ((JComponent)e.getSource()).getX(), e.getY()+((JComponent)e.getSource()).getY());
							//pop.show(this,temp.x, temp.y);
							storedMenu = pop;
							storedMenuOptions = menuoptions;
							lastRightMousePoint = p;
							
							// This is unnecessary, board is repainted on
							// boardstate change anyway
							//repaint();
						}
						else
							parent.showStatus("You are not allowed to change that cell.", true);
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
			else if (state.getTransitionsFrom().size() > 0 && LEGUP_Gui.profFlag(LEGUP_Gui.INTERN_RO))
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

						if (state.isModifiableCell(p.x,p.y))
						{
							BoardState next = state.conditionalAddTransition();
							if(next != null)pm.mousePressedEvent(next,p);
							// This is unnecessary, board is repainted on
							// boardstate change anyway
							//repaint();
						}
						else
							parent.showStatus("You are not allowed to change that cell.", true);
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
						// This is unnecessary, board is repainted on
						// boardstate change anyway
						//repaint();
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
