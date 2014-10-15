package edu.rpi.phil.legup.newgui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseAdapter;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
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

public class TreePanelDV extends DynamicViewer implements TransitionChangeListener, TreeSelectionListener
{
	private static final long serialVersionUID = 2272172376353427845L;

	public static final Color nodeColor = new Color(255,255,155);
	public static final int NODE_RADIUS = 10;
	private static final int SMALL_NODE_RADIUS = 7;
	private static final int COLLAPSED_DRAW_DELTA_X = 10;
	private static final int COLLAPSED_DRAW_DELTA_Y = 10;

	private ArrayList <Rectangle> currentStateBoxes = new ArrayList <Rectangle>();
	private Point selectionOffset = null;
	private Point lastMovePoint = null;

	private static final float floater[] = new float[] {(float)(5.0), (float)(10.0)}; // dashed setup
	private static final float floater2[] = new float[] {(float)(2.0), (float)(3.0)}; // dotted setup
	private static final Stroke dashed =  new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 10, floater, 0);
	private static final Stroke dotted =  new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 10, floater2, 0);
	private static final Stroke medium = new BasicStroke(2);
	private static final Stroke thin = new BasicStroke(1);

	//Path for node images
	//Currently only classic and smiley options exist
	private static final String NodeImgs = "images/tree/smiley/";

    public TreePanelDV() { super(); System.out.println("test"); }
    public TreePanelDV(boolean b) { super(b); }
	public void actionPerformed(ActionEvent e) {}
	public void initSize() {}
	public void draw( Graphics2D g ) {}
	public void mousePressedAt(Point p, MouseEvent e) {}
	public void mouseReleasedAt(Point p, MouseEvent e) {}
	public void boardDataChanged(BoardState state) {}



	public BoardState addChildAtCurrentState(Object justification)
	{
		//this was what was in the rightclick before the menu - Avi
		Selection selection = Legup.getInstance().getSelections().getFirstSelection();
		BoardState cur = selection.getState();
		if((cur.getChangedCells().size() > 0)||(cur.extraDataChanged()))
		{
			if (cur.isModifiable() && selection.isState())
			{
				//cur.setModifiableState(false);
				//cur.finalize_cells();
				Legup.setCurrentState(cur.endTransition());
			}
		}
		return cur;
	}

	public void delChildAtCurrentState()
	{
	}

	public void delCurrentState()
	{
	}

	public void collapseCurrentState()
	{
	}

	public void mergeStates()
	{
	}

	public void transitionChanged()
	{
		repaint();
	}

	public void treeSelectionChanged(ArrayList <Selection> newSelection)
	{
	}

}
