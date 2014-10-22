package edu.rpi.phil.legup.newgui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Polygon;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseAdapter;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JComponent;
import javax.swing.JViewport;
import javax.swing.ViewportLayout;
import javax.swing.event.PopupMenuListener;
import edu.rpi.phil.legup.BoardDrawingHelper;
import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.CaseRule;
import edu.rpi.phil.legup.Legup;
import edu.rpi.phil.legup.PuzzleModule;
import edu.rpi.phil.legup.Selection;
import edu.rpi.phil.legup.Justification;
import edu.rpi.phil.legup.Contradiction;

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

	private Point mousePoint;
	private static Selection mouseOver;

	//Path for node images
	//Currently only classic and smiley options exist
	private static final String NodeImgs = "images/tree/smiley/";

	public TreePanelDV()
	{
		super();
		System.out.println("TreePanelDV created");
		BoardState.addTransitionChangeListener(this);
		Legup.getInstance().getSelections().addTreeSelectionListener(this);

		//setDefaultPosition(-60,-80);
		setPreferredSize(new Dimension(640,160));
		zoomTo(1);
		System.out.println("scale is " + getZoom());
		//zoom(0, new Point(-60, 80));
	}
	public TreePanelDV(boolean b) { super(b); }
	public void actionPerformed(ActionEvent e)
	{
		System.out.println("actionPerformed");
	}

	// public void initSize()
	// {
	// 	System.out.println("initSize");
	// 	setSize( getProperSize() );
	// 	zoomFit();
	// }
	// private Dimension getProperSize()
	// {
	// 	Dimension rv = new Dimension();
	// 	Selection selection = Legup.getInstance().getSelections().getFirstSelection();

	// 	if (selection.isState())
	// 	{
	// 		BoardState state = selection.getState();
	// 		PuzzleModule pz = Legup.getInstance().getPuzzleModule();

	// 		if (pz != null)
	// 		{
	// 			Dimension d = pz.getImageSize();
	// 			int w  = state.getWidth();
	// 			int h = state.getHeight();

	// 			rv.width = d.width * (w + 2);
	// 			rv.height = d.height * (h + 2);
	// 		}
	// 	}

	// 	return rv;
	// }

	private BoardState getLastCollapsed(BoardState s)
	{
		return getLastCollapsed(s, null);
	}

	private BoardState getLastCollapsed(BoardState s, int[] outptrNumTransitions)
	{
		Vector <BoardState> children = s.getTransitionsFrom();
		BoardState rv = s;
		int numTransitions = 0;

		if (children.size() == 1)
		{
			BoardState child = children.get(0);

			if (child.isCollapsed())
			{
				++numTransitions;
				rv = getLastCollapsed(child);
			}
		}
		if(outptrNumTransitions != null) { outptrNumTransitions[0] = numTransitions; }
		return rv;
	}

	public void draw( Graphics2D g )
	{
		// super.paintComponent(g);
		System.out.println("draw");
		currentStateBoxes.clear();
		BoardState state = Legup.getInstance().getInitialBoardState();
		if( state != null ){
			drawTree(g,state);
			drawCurrentStateBoxes(g);
			if (mouseOver != null) drawMouseOver(g);
		}
		JViewport vp = getViewport();
		//zoom(0, new Point(-60, 80));
		System.out.println("Position: " + vp.getX() + ", " + vp.getY());
		System.out.println("Size: " + vp.getWidth() + ", " + vp.getHeight());
	}
	public void paint( Graphics2D g )
	{
		System.out.println("paint");
	}
	public void mousePressedAt(Point p, MouseEvent e)
	{
		System.out.println("mousePressedAt");
	}
	public void mouseReleasedAt(Point p, MouseEvent e) {}
	public void boardDataChanged(BoardState state)
	{
		System.out.println("boardDataChanged");
	}

	protected void paintComponent(Graphics g)
	{
		draw((Graphics2D)g);
	}

	public BoardState addChildAtCurrentState(Object justification)
	{
		System.out.println("addChildAtCurrentState");
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
		System.out.println("delChildAtCurrentState");
	}

	public void delCurrentState()
	{
		System.out.println("delCurrentState");
	}

	public void collapseCurrentState()
	{
		System.out.println("collapseCurrentState");
	}

	public void mergeStates()
	{
		System.out.println("mergeStates");
	}

	public void transitionChanged()
	{
		System.out.println("transitionChanged");
		repaint();
	}

	public void treeSelectionChanged(ArrayList <Selection> newSelection)
	{
		System.out.println("treeSelectionChanged");
	}

	public void repaint()
	{
		System.out.println("repainting treepanel");
		super.repaint();
	}
	/**
	 * Recursively renders the tree below <code>state</code>.
	 * Passing in the root node will effectively draw the entire tree.
	 * @param g the Graphics to draw on
	 * @param state the state we're drawing
	 */
	private void drawTree(Graphics g, BoardState state)
	{
		Graphics2D g2D = (Graphics2D)g;
		ArrayList <Selection> sel = Legup.getInstance().getSelections().getCurrentSelection();
		boolean isCollapsed = state.isCollapsed();

		boolean flag = LEGUP_Gui.profFlag(LEGUP_Gui.IMD_FEEDBACK);
		Vector <BoardState> transitionsFrom = null;
		Point draw;
		if(mouseOver != null)
		if((mouseOver.getState().getJustification() != null)||(mouseOver.getState().getCaseRuleJustification() != null))
		{
			draw = mousePoint;
			if((mouseOver.getState().justificationText != null)&&(mouseOver.getState().getColor() != TreePanel.nodeColor))
			{
				g.setColor(Color.black);
				String[] tmp = mouseOver.getState().justificationText.split("\n");
				for(int c1=0;c1<tmp.length;c1++)
				{
					g2D.drawString(tmp[c1],draw.x,draw.y-10*(3+tmp.length)+10*c1);
				}
			}
			//g2D.drawString("color:"+mouseOver.getState().getColor().toString(),draw.x,draw.y-30);
			//g2D.drawString("status:"+mouseOver.getState().getStatus(),draw.x-50,draw.y-30);
			//g2D.drawString("lTC:"+mouseOver.getState().leadsToContradiction(),draw.x,draw.y-20);
			//g2D.drawString("Depth:"+mouseOver.getState().getDepth(),draw.x,draw.y-30);
			//g2D.drawString("dnltc:"+(mouseOver.getState().doesNotLeadToContradiction() == null),draw.x,draw.y-30);
			g.setColor(Color.gray);
			g2D.drawRect(draw.x+30,draw.y-30,100,100);
		}
		g.setColor(Color.black);
		draw = (Point)state.getLocation().clone();
		if (!isCollapsed)
			transitionsFrom = state.getTransitionsFrom();
		else
		{
			int[] ptrNumTransitions = new int[1];
			BoardState lastCollapsed = getLastCollapsed(state, ptrNumTransitions);
			//draw.x += COLLAPSED_DRAW_DELTA_X * ptrNumTransitions[0];
			Point nextPoint = (Point)lastCollapsed.getLocation().clone();
			draw.x = (draw.x + nextPoint.x)/2;

			transitionsFrom = lastCollapsed.getTransitionsFrom();
		}

		for (int c = 0; c < transitionsFrom.size(); ++c)
		{
			BoardState b = transitionsFrom.get(c);
			Point childPoint = (Point)b.getLocation().clone();
			if(b.isCollapsed())
			{
				childPoint.x = (childPoint.x + getLastCollapsed(state).getLocation().x)/2;
			}

			if (transitionsFrom.size() == 1)
			{
				int status = (flag ? b.getStatus() : b.getDelayStatus());
				if (status == BoardState.STATUS_RULE_CORRECT || status ==  BoardState.STATUS_CONTRADICTION_CORRECT)
				{
					g.setColor(flag ? Color.green : new Color(0x80ff80));
					g2D.setStroke(medium);
				}
				else if (status == BoardState.STATUS_RULE_INCORRECT || status ==  BoardState.STATUS_CONTRADICTION_INCORRECT)
				{
					g.setColor(flag ? Color.red : new Color(0xff8080));
					g2D.setStroke(medium);
				}
				else
					g.setColor(flag ? Color.black : Color.gray);

				drawTransition(new Line2D.Float(draw.x, draw.y, childPoint.x-NODE_RADIUS, childPoint.y), g, state, b.isCollapsed());
				//System.out.format("%d, %d,   %d, %d\n", childPoint.x, childPoint.y, state.getLocation().x, state.getLocation().y);
				g2D.setStroke(thin);
			}
			else
				/*
				 * We might need to do a dotted transition type thing because green implies justified,
				 * while a case rule is not justified until all but one child lead to a contradiction
				 */
			{
				if (state.getCaseSplitJustification() == null)
					g.setColor(flag ? Color.black : Color.gray);
				else if (state.isJustifiedCaseSplit() != null) // invalid split
					g.setColor(flag ? Color.red : new Color(0xff8080));
				else
					g.setColor(flag ? Color.green : new Color(0x80ff80));

				// set the stroke depending on whether it leads to a contradiction or is the last state
				if (state.getCaseSplitJustification() == null)
					g2D.setStroke(thin);
				else if (b.leadsToContradiction())
				{
					g2D.setStroke(medium);
				}
				else
				{
					// maybe all the other ones are contradictions (proof by contradiction)
					boolean allOthersLeadToContradiction = true;

					for (int index = 0; index < transitionsFrom.size(); ++index)
					{
						if (c == index) // skip ourselves
							continue;

						BoardState sibling = transitionsFrom.get(index);

						if (!sibling.leadsToContradiction())
						{
							allOthersLeadToContradiction = false;
							break;
						}
					}

					if (allOthersLeadToContradiction)
						g2D.setStroke(medium);
					else
						g2D.setStroke(dotted);
				}

				drawTransition(new Line2D.Float(draw.x, draw.y, childPoint.x-NODE_RADIUS, childPoint.y), g, state, b.isCollapsed());

				g2D.setStroke(thin);
			}

			//**********************Source of node issue*************************//
			//if (b.getTransitionsFrom().size() > 0)
				drawTree(g, b);
				//drawTree(g, b.getTransitionsFrom().get(0));
		}

		Selection theSelection = new Selection(state,false);

		if (sel.contains(theSelection))
		{ // handle updating the selection information
			int deltaY = 0;
			int yRad = 36;

			if (isCollapsed)
			{
				deltaY = -2 * COLLAPSED_DRAW_DELTA_Y; // times 2 because draw.y is already adjusted
				yRad += 2 * COLLAPSED_DRAW_DELTA_Y;
			}

			//currentStateBoxes.add(new Rectangle(draw.x - 18, draw.y - 18 + deltaY,36,yRad));
		}

		if (!isCollapsed)
		{
			drawNode(g,draw.x, draw.y,state);
		}
		else
			drawCollapsedNode(g,draw.x,draw.y);
		// to prevent the drawing of contradictions from taking over the CPU
		try {
			Thread.sleep(1);
		} catch (Exception e) {
			System.err.println("zzz...");
		}
	}

	/**
	 * Draw the current transition (will make it blue if it's part of the selection)
	 * @param trans the line of the transition we're drawing, starting at the source
	 * @param g the graphics to use
	 * @param parent the parent board state of the transition we're drawing
	 * @param collapsedChild is the child we're connecting to a collapsed state
	 */
	private void drawTransition(Line2D.Float trans, Graphics g,
			BoardState parent, boolean collapsedChild)
	{
		Graphics2D g2d = (Graphics2D)g;
		ArrayList <Selection> sel = Legup.getInstance().getSelections().getCurrentSelection();
		Selection theSelection = new Selection(parent,true);
		int nodeRadius = collapsedChild ? SMALL_NODE_RADIUS : NODE_RADIUS;

		g2d.setStroke(medium);
		g.setColor(((sel.contains(theSelection)) ? Color.blue : Color.gray));

		g2d.draw(trans);

		// we also want to draw the arrowhead
		final int ARROW_SIZE = 8;

		// find the tip of the arrow, the point NODE_RADIUS away from the destination endpoint
		double theta = Math.atan2(trans.y2 - trans.y1, trans.x2 - trans.x1);

		double nx = nodeRadius * Math.cos(theta);
		double ny = nodeRadius * Math.sin(theta);

		int px = Math.round(trans.x2);
		int py = Math.round(trans.y2);

		Polygon arrowhead = new Polygon();
		arrowhead.addPoint(px, py);

		nx = (ARROW_SIZE) * Math.cos(theta);
		ny = (ARROW_SIZE) * Math.sin(theta);

		px = (int)Math.round(trans.x2 - nx);
		py = (int)Math.round(trans.y2 - ny);
		// px and py are now the "base" of the arrowhead

		theta += Math.PI / 2.0;
		double dx = (ARROW_SIZE / 2) * Math.cos(theta);
		double dy = (ARROW_SIZE / 2) * Math.sin(theta);

		arrowhead.addPoint((int)Math.round(px + dx), (int)Math.round(py + dy));

		theta -= Math.PI;
		dx = (ARROW_SIZE / 2) * Math.cos(theta);
		dy = (ARROW_SIZE / 2) * Math.sin(theta);

		arrowhead.addPoint((int)Math.round(px + dx), (int)Math.round(py + dy));

		g2d.fill(arrowhead);
	}

	/**
	* Creates a triangle with specified x, y, and radius.
	* @param x of center of triangle
	* @param y of center of triangle
	* @param radius of circumscribing circle of triangle
	* @returns a Polygon with the points of the requested triangle.
	**/
	private Polygon makeTriangle(int x, int y, double radius)
	{
		Polygon triangle = new Polygon();
		for(double c1 = 0;c1 < 360;c1+=120)
		{
			triangle.addPoint((int)(x+radius*Math.cos(Math.toRadians(c1))),(int)(y+radius*Math.sin(Math.toRadians(c1))));
		}
	    return triangle;
	}

	/**
	 * Draw a node at a given location
	 * @param g the graphics to draw it with
	 * @param x the x location of the center of the node
	 * @param y the y location of the center of the node
	 * @param state the state to draw
	 */
	private void drawNode( Graphics g, int x, int y, BoardState state ){
		final int diam = NODE_RADIUS + NODE_RADIUS;
		Graphics2D g2D = (Graphics2D)g;
		g2D.setStroke(thin);
		Polygon triangle = makeTriangle(x, y, 1.5*NODE_RADIUS);
		Selection theSelection = new Selection(state,false);
		ArrayList <Selection> sel = Legup.getInstance().getSelections().getCurrentSelection();
		g.setColor(state.getColor());
		if(!state.isModifiable())
		{
			g.fillOval( x - NODE_RADIUS, y - NODE_RADIUS, diam, diam );
			g.setColor((sel.contains(theSelection)? Color.blue : Color.black));
			g2D.setStroke((sel.contains(theSelection)? medium : thin));
			//if(state == Legup.getInstance().getInitialBoardState().getFinalState())g.setColor(Color.red);
			g.drawOval( x - NODE_RADIUS, y - NODE_RADIUS, diam, diam );
		}
		else
		{
			{
				g2D.fill(triangle);
				g.setColor((sel.contains(theSelection)? Color.blue : Color.black));
				g2D.setStroke((sel.contains(theSelection)? medium : thin));
				//if(state == Legup.getInstance().getInitialBoardState().getFinalState())g.setColor(Color.red);
				g.drawPolygon(triangle);
			}
			if(state.getJustification() instanceof Contradiction)
			{
				/*g2D.fillRect(x-NODE_RADIUS,y-NODE_RADIUS,NODE_RADIUS*2,NODE_RADIUS*2);
				g.setColor((sel.contains(theSelection)? Color.blue : Color.black));
				g2D.setStroke((sel.contains(theSelection)? medium : thin));
				g2D.drawRect(x-NODE_RADIUS,y-NODE_RADIUS,NODE_RADIUS*2,NODE_RADIUS*2);*/
				g.setColor(Color.red);
				g2D.drawLine(x-NODE_RADIUS+3*NODE_RADIUS,y-NODE_RADIUS,x+NODE_RADIUS+3*NODE_RADIUS,y+NODE_RADIUS);
				g2D.drawLine(x+NODE_RADIUS+3*NODE_RADIUS,y-NODE_RADIUS,x-NODE_RADIUS+3*NODE_RADIUS,y+NODE_RADIUS);
				g.setColor((sel.contains(theSelection)? Color.blue : Color.black));
			}
		}
		boolean flag = LEGUP_Gui.profFlag(LEGUP_Gui.IMD_FEEDBACK);

		// extra drawing instructions
		/*int status = state.getStatus();
		Image i = images[status];

		if (i != null)
		{
			g.drawImage(i,x-i.getWidth(null)/2,y-i.getHeight(null)/2,null);
		}*/
	}

	/**
	 * Draw a collapsed node at the current location
	 * @param g the Graphics to draw with
	 * @param x the x location to draw it on
	 * @param y the y location to draw it on
	 */
	private void drawCollapsedNode(Graphics g,int x, int y)
	{
		final int rad = SMALL_NODE_RADIUS;
		final int diam = 2 * rad;
		final int deltaX = -COLLAPSED_DRAW_DELTA_X;
		final int deltaY = -COLLAPSED_DRAW_DELTA_Y;

		Graphics2D g2D = (Graphics2D)g;
		g2D.setStroke(thin);
		g2D.setColor(Color.black);
		//g2D.drawLine(x,y+2*deltaY,x,y);

		/*for (int c = 0; c < 3; ++c)
		{
			g.setColor(nodeColor);
			g.fillOval(x - rad + (c - 1) * deltaX,y - rad,diam,diam);
			g.setColor(Color.black);
			g.drawOval(x - rad + (c - 1) * deltaX,y - rad,diam,diam);
		}*/
        for (int c = 0; c < 3; ++c)
        {
            Polygon tri = makeTriangle(x - rad + (c - 1) * deltaX, y, diam/2);
            g.setColor(nodeColor);
            g.fillPolygon(tri);
            g.setColor(Color.black);
            g.drawPolygon(tri);
        }
	}

	/**
	 * Draw the current state boxes (the cached selection)
	 * @param g the graphics to use to draw
	 */
	private void drawCurrentStateBoxes(Graphics g)
	{
		if (currentStateBoxes != null)
		{
			Graphics2D g2d = (Graphics2D)g;

			g.setColor(Color.blue);
			g2d.setStroke(dashed);

			for (int x = 0; x < currentStateBoxes.size(); ++x)
				g2d.draw(currentStateBoxes.get(x));
		}
	}

	private void drawMouseOver(Graphics2D g)
	{
		BoardState B = mouseOver.getState();
		//J contains both basic rules and contradictions
		Justification J = B.getJustification();
		if (J != null)
		{
			g.drawImage(J.getImageIcon().getImage(), mousePoint.x+30, mousePoint.y-30, null);
		}
		CaseRule CR = B.getCaseSplitJustification();
		if (CR != null)
		{
			g.drawImage(CR.getImageIcon().getImage(), mousePoint.x+30, mousePoint.y-30, null);
			return;
		}
	}
}
