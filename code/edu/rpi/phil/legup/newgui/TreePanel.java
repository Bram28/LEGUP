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
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseAdapter;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.Vector;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JComponent;
import javax.swing.JViewport;
import javax.swing.ViewportLayout;
import javax.swing.event.PopupMenuListener;
import javax.swing.ImageIcon;
import edu.rpi.phil.legup.BoardDrawingHelper;
import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.CaseRule;
import edu.rpi.phil.legup.Legup;
import edu.rpi.phil.legup.PuzzleModule;
import edu.rpi.phil.legup.Selection;
import edu.rpi.phil.legup.Justification;
import edu.rpi.phil.legup.Contradiction;

public class TreePanel extends DynamicViewer implements TransitionChangeListener, TreeSelectionListener
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

	private Rectangle bounds = new Rectangle(0,0,0,0);

	private int xOffset = 0;
	private int yOffset = 0;

	private Point mousePoint;
	private static Selection mouseOver;
	
	//hashmap should be based off of boardstate, which is unique, instead of position, since position can be shifted
    private Map<BoardState, Color> collapseColorHash = new HashMap<BoardState, Color>();
    
	//Path for node images
	//Currently only classic and smiley options exist
	private static final String NodeImgs = "images/tree/smiley/";

	public TreePanel()
	{
		super();
		// System.out.println("TreePanel created");
		BoardState.addTransitionChangeListener(this);
		Legup.getInstance().getSelections().addTreeSelectionListener(this);

		//setDefaultPosition(-60,-80);
		setSize(new Dimension(100, 200));
		setPreferredSize(new Dimension(640, 160));
		//zoomTo(1);
		//System.out.println("scale is " + getZoom());
		//zoom(0, new Point(-60, 80));
	}
	public TreePanel(boolean b) { super(b); }
	public void actionPerformed(ActionEvent e)
	{
		// System.out.println("actionPerformed");
	}

	private BoardState getLastCollapsed(BoardState s)
	{
		return getLastCollapsed(s, null);
	}

	private BoardState getLastCollapsed(BoardState s, int[] outptrNumTransitions)
	{
		List<BoardState> children = s.getChildren();
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
		if(outptrNumTransitions != null) { outptrNumTransitions[0] = numTransitions;  }
		return rv;
	}

	// recursively computes the bounding rectangle of the tree
	private Rectangle getTreeBounds( BoardState state ){
		// get the position of the current node and add padding
		Rectangle b = new Rectangle( state.getLocation() );
		b.grow( 2*NODE_RADIUS, 2*NODE_RADIUS );
		// Adjust the rectangle so that rule popups aren't cut off
		float scale = (100/(float)getZoom());
		b.setBounds((int)b.getX()-(int)(100*scale), (int)b.getY(), (int)b.getWidth()+(int)(400*scale), (int)b.getHeight()+(int)(200*scale));
		// get the relevant child nodes
		List<BoardState> children = state.isCollapsed()
			? getLastCollapsed(state).getChildren()
			: state.getChildren();
		// compute the union of the child bounding boxes recursively
		for (int c = 0; c < children.size(); c++)
		{
			b = b.union( getTreeBounds( children.get(c) ) );
		}
		return b;
	}

	public void updateTreeSize()
	{
		if (Legup.getInstance().getInitialBoardState() == null) {
			return;
		}
		bounds = getTreeBounds(Legup.getInstance().getInitialBoardState());
		setSize(bounds.getSize());
		BoardState state = Legup.getInstance().getInitialBoardState();
		if( bounds.y != 60 )
		{
			state.adjustOffset( new Point( 60-bounds.y, 0 ) );
		}
	}

	public void reset()
	{
		BoardState state = Legup.getInstance().getInitialBoardState();
		if( bounds.x != 0 || bounds.y != 0 )
		{
			state.setOffset( new Point( state.getOffset().x-bounds.x, state.getOffset().y-bounds.y ) );
			updateTreeSize();
		}
	}

	public void draw( Graphics2D g )
	{
		currentStateBoxes.clear();
		BoardState state = Legup.getInstance().getInitialBoardState();
		if(state != null)
		{
			setSize( bounds.getSize() );
			g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
			g.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
			drawTree(g,state);
			drawCurrentStateBoxes(g);
			if (mouseOver != null) drawMouseOver(g);
		}
	}

	public void zoomFit()
	{
		// find the ideal width and height scale
		zoomTo(1.0);
		updateTreeSize();
		double fitwidth = (viewport.getWidth()-8.0) / (getSize().width - 200);
		double fitheight = (viewport.getHeight()-8.0) / (getSize().height - 120);
		// choose the smaller of the two and zoom
		zoomTo( (fitwidth < fitheight) ? fitwidth : fitheight );
		viewport.setViewPosition(new Point(0,0));
	}

	public void zoomReset()
	{
		zoomTo(1.0);
		viewport.setViewPosition(new Point(0,0));
	}

	/**
	 * Get the boardstate / transition at a point in the tree
	 * @param state the state to check now (starts at root)
	 * @param where the point where the user clicked
	 * @return the node or transition the user selected, or null if he or she missed
	 */
	private Selection getSelectionAtPoint(BoardState state, Point where)
	{
		if(state == null)return null;
		Selection rv = null;
		Point loc = state.getLocation();
		boolean isCollapsed = state.isCollapsed();
		final int radius = isCollapsed ? (2 * NODE_RADIUS) : NODE_RADIUS;

		Point draw = new Point(loc.x - radius, loc.y - radius);
		// distance from a transition which is considered clicking on it, squared
		final int MAX_CLICK_DISTANCE_SQ = 5*5;
		Shape myBounds;
		//System.out.println("getSelectionAtPoint called for (" + where.x + "," + where.y + ") on node at point (" + state.getLocation().x + "," + state.getLocation().y + ")");
		if(state.isModifiable())
		{
			/*draw.x += 128;
			int[] points_x = new int[3];
			int[] points_y = new int[3];
			for(int c1 = 0;c1 < 3;c1+=1)
			{
				points_x[c1] = (int)(draw.x+radius*Math.cos(Math.toRadians(c1*120)));
				points_y[c1] = (int)(draw.y+radius*Math.sin(Math.toRadians(c1*120)));
			}
			myBounds = new Polygon(points_x,points_y,3);*/
			draw.x -= radius/2;
			draw.y -= radius/2;
			myBounds = new Ellipse2D.Float(draw.x,draw.y,3*radius,3*radius);
		}
		else
		{
			myBounds = new Ellipse2D.Float(draw.x,draw.y,2 * radius,2 * radius);
		}
		
		boolean stateSelected = myBounds.contains(where);

		if (stateSelected && isCollapsed)
		{
			List<BoardState> parents = state.getParents();

			if (parents.size() == 1 && parents.get(0).isCollapsed())
				stateSelected = false; // can't select a collapsed state
		}

		if (stateSelected)
		{
			rv = new Selection(state,false);
		}
		else
		{
			for(BoardState b : state.getChildren())
			{
				Selection s = getSelectionAtPoint(b,where);
				if(s != null)rv = s;
			}
		}

		return rv;
	}
	/**
	* Toggle a state in a selection (something was ctrl + clicked)
	* @param state the state to check now (starts at root)
	* @param where the point where the user ctrl + clicked
	*/
	private void toggleSelection(BoardState state, Point where)
	{
		Selection s = getSelectionAtPoint(state, where);
		Legup.getInstance().getSelections().toggleSelection(s);
	}
	/**
	* Select a new state or transition that the user clicked on
	* @param state the state we're at
	* @param where the point where the user clicked
	* @return the new Selection
	*/
	private Selection newSelection(BoardState state, Point where)
	{
		Selection s = getSelectionAtPoint(state, where);
		Legup.getInstance().getSelections().setSelection(s);
		return s;
	}

	protected void mouseMovedAt(Point p, MouseEvent e)
	{
		Selection prev = mouseOver;
		mouseOver = getSelectionAtPoint(Legup.getInstance().getInitialBoardState(), p);
		mousePoint = p;

		if( prev != null || mouseOver != null )
			repaint();
		if( prev != null ^ mouseOver != null )
			Legup.getInstance().refresh();
		if( prev != null && mouseOver != null )
			if( !prev.equals(mouseOver) )
				Legup.getInstance().refresh();
	}

	public static Selection getMouseOver()
	{
		return mouseOver;
	}

	protected void mouseDraggedAt(Point p, MouseEvent e) {
		if (lastMovePoint == null)
			lastMovePoint = new Point(p);
//		repaint();
	}

	protected void highlightSelectedTransition(Point p)
	{
		Selection sel = getSelectionAtPoint(Legup.getInstance().getInitialBoardState(), p);
		if(sel != null && sel.getState().isModifiable())
		{
				Legup.getInstance().getGui().getJustificationFrame().
				setSelectionByJustification(sel.getState().getJustification());
		}
	}
	
	public void mouseReleasedAt(Point p, MouseEvent e)
	{
		if( e.getButton() == MouseEvent.BUTTON1 )
		{
			lastMovePoint = new Point(p);
			if ( e.isControlDown() ) {
				// add to selection
				toggleSelection( Legup.getInstance().getInitialBoardState(), p );
			} else {
				// make a new selection
				newSelection( Legup.getInstance().getInitialBoardState(), p );
				highlightSelectedTransition(p);
			}
			// right click
		}
	}
	public void mouseWheelMovedAt( MouseWheelEvent e )
	{
		updateTreeSize();
	}

	public BoardState addChildAtCurrentState(Object justification)
	{
		Selection selection = Legup.getInstance().getSelections().getFirstSelection();
		BoardState cur = selection.getState();
		if((cur.getChangedCells().size() > 0)||(cur.extraDataChanged()))
		{
			if (cur.isModifiable() && selection.isState())
			{
				Legup.setCurrentState(cur.endTransition());
			}
		}
		updateTreeSize();
		return cur;
	}
	
	public boolean checkIfBranchIsContradiction(BoardState state)
	{
		if (state.getChildren().size() == 1)
		{
			BoardState child = state.getChildren().get(0);
			while (child.getChildren().size() == 1)
			{
				child = child.getChildren().get(0);
			}
			
			if (child.getStatus() == BoardState.STATUS_CONTRADICTION_CORRECT)
			{
				return true;
			}
		}
		
		return false;
	}
	
	public boolean checkIfBranchesConverge(BoardState child0, BoardState child1)
	{
		//collapse usually stops before a merge.
		//but...if each branch is merged back together, then just collapse the merge as well.

		//check branch 0
		BoardState branch0 = child0;
		//System.out.println("child0 starts at " + branch0.getLocation());
		while (branch0.getChildren().size() == 1)
		{
			branch0 = branch0.getChildren().get(0);

			if (branch0.getParents().size() == 2)
				break;
		}

		//check branch 1
		BoardState branch1 = child1;
		//System.out.println("child1 starts at " + branch1.getLocation());
		while (branch1.getChildren().size() == 1)
		{
			branch1 = branch1.getChildren().get(0);
			if (branch1.getParents().size() == 2)
				break;
		}

		if (branch0.getLocation().equals(branch1.getLocation()))
		{
			return true;
		}
		else
			return false;
	}
	
	public boolean handleAllBranchesContra(BoardState state)
	{
		BoardState child0 = state.getChildren().get(0);
		BoardState child1 = state.getChildren().get(1);
		
		boolean isChild0Contra = checkIfBranchIsContradiction(child0);
		boolean isChild1Contra = checkIfBranchIsContradiction(child1);
		
		//both branches lead to contradiction
		if (isChild0Contra && isChild1Contra)
		{
			if (child0.isCollapsed() && child1.isCollapsed())
			{
				//both branches collapsed, so decollapse both
				child0.setOffset(new Point(-15, child0.getOffset().y));
				child1.setOffset(new Point(15, child1.getOffset().y));
			}
			else
			{
				//collapse both branches
				child0.setOffset(new Point(0, child0.getOffset().y));
				child1.setOffset(new Point(0, child1.getOffset().y));
			}
			
			//propogate changes
			child0.toggleCollapse();
			child1.toggleCollapse();
			
			return true;
		}
		
		return false;
	}
		
	public void handleAllBranchesMerged(BoardState state)
	{
		BoardState parent0 = state.getParents().get(0);
		BoardState parent1 = state.getParents().get(1);
		
		//find the node farthest back in the tree for each branch
		while (parent0.getParents().get(0).getChildren().size() < 2)
			parent0 = parent0.getParents().get(0);
		while (parent1.getParents().get(0).getChildren().size() < 2)
			parent1 = parent1.getParents().get(0);
	
		//WORKS, but produces uglier result 
		//meaning it produces a fast-forward transition, state node, and transition
		//instead of just one fast-forward
		if (parent0.isCollapsed() && parent1.isCollapsed())
		{
			//decollapse both
			parent0.setOffset(new Point(-15, parent0.getOffset().y));
			parent1.setOffset(new Point(15, parent1.getOffset().y));
		}
		else
		{
			//collapse all
			parent0.setOffset(new Point(0, parent0.getOffset().y));
			parent1.setOffset(new Point(0, parent1.getOffset().y));
		}
		
		//propogate changes
		parent0.toggleCollapse();
		parent1.toggleCollapse();
		
		//This is the better looking solution!
		//Partially works//
		//However decollapse is broken. Also, the gap between nodes is too large.
		//parent0.toggleCollapseRecursiveMerge(parent0.getLocation().x, parent0.getLocation().y, true);
		//parent1.toggleCollapseRecursiveMerge(parent1.getLocation().x, parent1.getLocation().y, true);
	}
	
	public void syncCollapse(BoardState state, int numBranches)
	{	
		//first determine if all the branches are collapsed
		boolean allCollapsed = true;
		for (int i = 0; i < numBranches; i++)
		{
			BoardState child = state.getChildren().get(i);
			BoardState childFirstNode = child.getChildren().get(0);
			if (childFirstNode.getChildren().size() < 1)
				continue;
			
			BoardState childFirstTransition = childFirstNode.getChildren().get(0);
			
			allCollapsed &= childFirstTransition.isCollapsed();
		}
		
		//then handle collapsing for each branch
		for (int i = 0; i < numBranches; i++)
		{
			BoardState child = state.getChildren().get(i);
			BoardState childFirstNode = child.getChildren().get(0);
			if (childFirstNode.getChildren().size() < 1)
				continue;
			
			BoardState childNextTransition = childFirstNode.getChildren().get(0);
			
			//if all collapsed, then undo collapse
			if (allCollapsed)
			{
				childNextTransition.toggleCollapse();
			}
			//otherwise collapse if not already collapsed
			else if (!childNextTransition.isCollapsed())
			{
				childNextTransition.toggleCollapse();
			}
		}
	}

	public void collapseCurrentState()
	{
		Selection s = Legup.getInstance().getSelections().getFirstSelection();

		BoardState state = s.getState();
		
		//toggle collapse for the collapse icon or for nodes before it
		if (state.isCollapsed())
		{
			state.toggleCollapse();
		}
		else
		{
			//Collapse all branches into one if all branches lead to contradiction
			if (!state.isModifiable() && state.getChildren().size() >= 2)
			{
				handleAllBranchesContra(state);

				syncCollapse(state, state.getChildren().size());
			}
			
			//collapse if this is the convergence of multiple branches
			//meaning you clicked on a converged transition node
			if (state.isModifiable() && state.getParents().size() > 1)
				handleAllBranchesMerged(state);
			
			//if the state is a transition then collapse everything to the right of it
			if (state.isModifiable() && state.getChildren().size() == 1 
					&& state.getParents().get(0).getChildren().size() < 2)
				state.toggleCollapse();
		}
		getCollapseColor(state);
		
		updateTreeSize();
		repaint();
	}
	
	//This function must be called before the board collapsing takes place, otherwise transition data will be hidden
	public void getCollapseColor(BoardState lastCollapsed)
	{	
		BoardState iterBoard = lastCollapsed;
		boolean overallColor = true;
		
		//collapse is colored green if all of the transitions are green. 
		//if there is one red, the entire thing is red
		while (iterBoard.getChildren().size() == 1 
				&& iterBoard.getChildren().get(0).getParents().size() < 2)
		{
			int status = iterBoard.getStatus();
			if (status == BoardState.STATUS_RULE_CORRECT || status ==  BoardState.STATUS_CONTRADICTION_CORRECT)
			{
				overallColor &= true;
			}
			else if (status == BoardState.STATUS_RULE_INCORRECT || status ==  BoardState.STATUS_CONTRADICTION_INCORRECT)
			{
				overallColor &= false;
			}
			
			//get children
			iterBoard = iterBoard.getChildren().get(0);	
		}

		//save multiple colors, because collapse might produce different results on different parts of the tree
		//iterBoard should be the leaf node
		if (overallColor)
			this.collapseColorHash.put(iterBoard, Color.GREEN);
		else 
			this.collapseColorHash.put(iterBoard, Color.RED);
	}

	/**
	 * Delete the current state and associated transition then fix the children
	 */
	public void delCurrentState()
	{
		Selection s = Legup.getInstance().getSelections().getFirstSelection();
		BoardState currentState = s.getState();
		
		// make sure we don't delete the initial board state
		if (currentState.getParents().size() == 0)
			return;
		
		// choose the previous state and move the children from after state
		BoardState parentState = null;
		BoardState childState = null;
		
		if (currentState.isModifiable()) {
			parentState = currentState.getSingleParentState();
			childState = currentState.endTransition();
			parentState.getChildren().remove(currentState);
			currentState.getParents().remove(parentState);
		} else {
			parentState = currentState.getSingleParentState().getSingleParentState();
			childState = currentState;
			parentState.getChildren().remove(currentState.getSingleParentState());
			currentState.getSingleParentState().getParents().remove(parentState);
		}
		
		BoardState.reparentChildren(childState, parentState);
		
		// delete the current state
		if (currentState.isModifiable()) {
			currentState.deleteState();
		} else {
			currentState.getSingleParentState().deleteState();
		}
		
		Legup.getInstance().getSelections().setSelection(new Selection(parentState, false));
		updateTreeSize();
	}

	/**
	 * Delete the child and child's subtree starting at the current state
	 */
	public void delChildAtCurrentState()
	{
		if(!Legup.getInstance().getGui().checkImmediateFeedback())BoardState.removeColorsFromTransitions();
		Selection s = Legup.getInstance().getSelections().getFirstSelection();
		BoardState state = s.getState();
		
		
		if (s.isState())
		{ // state
			// make sure we don't delete the initial board state
			List<BoardState> parentStates = state.getParents();
			if (parentStates.size() == 0)
				return;
				
			// use to select the previous state
			BoardState parent = parentStates.get(0);
			
			state.deleteState();
			
			Legup.getInstance().getSelections().setSelection(new Selection(parent, false));
		}
		else
		{ //  transition, delete all the things we're trasitioning from
			
			// select current state
			Legup.getInstance().getSelections().setSelection(new Selection(state, false));

			// delete children states
			List<BoardState> children = state.getChildren();

			while (children.size() > 0)
			{
				BoardState child = children.get(0);

				child.deleteState();

				children.remove(0);
			}
		}
		updateTreeSize();
	}

	/**
	 * Merge the two or more selected states
	 * TODO: add elegant error handling
	 */
	public void mergeStates()
	{
		ArrayList <Selection> selected = Legup.getInstance().getSelections().getCurrentSelection();

		if (selected.size() > 1)
		{
			boolean allStates = true;

			for (int x = 0; x < selected.size(); ++x)
			{
				if (selected.get(x).isTransition())
				{
					allStates = false;
					break;
				}
				else if (selected.get(x).getState().isModifiable())
				{
					allStates = false;
					break;
				}
			}

			if (allStates)
			{
				ArrayList <BoardState> parents = new ArrayList <BoardState>();

				for (int x = 0; x < selected.size(); ++x)
					parents.add(selected.get(x).getState());

				BoardState.merge(parents, false);
			}
			else
				System.out.println("not all states");
		}
		else
			System.out.println("< 2 selected");
		updateTreeSize();
	}
	public void transitionChanged()
	{
		updateTreeSize();
	}

	public void treeSelectionChanged(ArrayList <Selection> newSelection)
	{
	}

	/**
	 * Recursively renders the tree below <code>state</code>.
	 * Passing in the root node will effectively draw the entire tree.
	 * @param g the Graphics to draw on
	 * @param state the state we're drawing
	 */
	private void drawTree(Graphics g, BoardState state)
	{
		// System.out.println("Board dimensions are " + state.getWidth() + "x" + state.getHeight());
		Graphics2D g2D = (Graphics2D)g;

		ArrayList <Selection> sel = Legup.getInstance().getSelections().getCurrentSelection();
		boolean isCollapsed = state.isCollapsed();
		BoardState lastCollapsed = null;
		
		boolean flag = LEGUP_Gui.profFlag(LEGUP_Gui.IMD_FEEDBACK);
		List<BoardState> children = null;
		Point draw;

		g.setColor(Color.black);
		draw = (Point)state.getLocation().clone();
		if (!isCollapsed)
			children = state.getChildren();
		else
		{
			int[] ptrNumTransitions = new int[1];
			lastCollapsed = getLastCollapsed(state, ptrNumTransitions);
			Point nextPoint = (Point)lastCollapsed.getLocation().clone();
			draw.x = (draw.x + nextPoint.x)/2;

			children = lastCollapsed.getChildren();
		}

		for (int c = 0; c < children.size(); ++c)
		{
			BoardState b = children.get(c);
			Point childPoint = (Point)b.getLocation().clone();
			if(b.isCollapsed())
			{
				childPoint.x = (childPoint.x + getLastCollapsed(state).getLocation().x)/2;
			}

			if (children.size() == 1)
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

					for (int index = 0; index < children.size(); ++index)
					{
						if (c == index) // skip ourselves
							continue;

						BoardState sibling = children.get(index);

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
			//if (b.getChildren().size() > 0)
				drawTree(g, b);
				//drawTree(g, b.getChildren().get(0));
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
			drawNode(g, draw.x, draw.y, state);
		else
			drawCollapsedNode(g, draw.x, draw.y, lastCollapsed);
		
		if (isCollapsed && sel.contains(theSelection))
		{
			g.setColor(Color.green);
			g2D.setStroke(medium);
			final int diam = NODE_RADIUS + NODE_RADIUS;
			g.drawRect( draw.x - diam, draw.y - diam, diam * 2, diam * 2 );
		}
		
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
			g.setColor(Color.black);
			g2D.setStroke(thin);
			g.drawOval( x - NODE_RADIUS, y - NODE_RADIUS, diam, diam );
			if (sel.contains(theSelection))
			{
				g.setColor(Color.green);
				g2D.setStroke(medium);
				g.drawRect( x - diam, y - diam, diam * 2, diam * 2 );
			}
		}
		else
		{
			{
				g2D.fill(triangle);
				g.setColor(Color.black);
				g2D.setStroke(thin);
				g.drawPolygon(triangle);
				if (sel.contains(theSelection))
				{
					g.setColor(Color.green);
					g2D.setStroke(medium);
					g.drawRect( x - diam, y - diam, diam * 2, diam * 2 );
				}
			}
			if(state.getJustification() instanceof Contradiction)
			{
				g.setColor(Color.red);
				g2D.drawLine(x-NODE_RADIUS+3*NODE_RADIUS,y-NODE_RADIUS,x+NODE_RADIUS+3*NODE_RADIUS,y+NODE_RADIUS);
				g2D.drawLine(x+NODE_RADIUS+3*NODE_RADIUS,y-NODE_RADIUS,x-NODE_RADIUS+3*NODE_RADIUS,y+NODE_RADIUS);
				g.setColor((sel.contains(theSelection)? Color.blue : Color.black));
			}
		}
		boolean flag = LEGUP_Gui.profFlag(LEGUP_Gui.IMD_FEEDBACK);
	}
	
	/**
	 * When the user collapses the nodes, find out which board state was collapsed. The board state can then be used to find out 
	 * the overall color for the collapsed transition(s)
	 * @param lastCollapsed BoardState before the collapsed transition(s)
	 * @param changedCells Cells modified during the collapse
	 * @return OVerall color for the collapsed transition(s)
	 */
	private Color getCollapsedTransitionColor(BoardState lastCollapsed)
	{
		Color transitionColor = new Color(255, 255, 155);
	
		//get last node
		BoardState iterBoard = lastCollapsed;
		while (iterBoard.getChildren().size() == 1 &&
				iterBoard.getChildren().get(0).getParents().size() < 2)
			iterBoard = iterBoard.getChildren().get(0);
		
		transitionColor = new Color(255, 255, 155);
		if (collapseColorHash.containsKey(iterBoard))
			transitionColor = collapseColorHash.get(iterBoard);
		
		return transitionColor;
	}

	/**
	 * Draw a collapsed node at the current location
	 * @param g the Graphics to draw with
	 * @param x the x location to draw it on
	 * @param y the y location to draw it on
	 */
	private void drawCollapsedNode(Graphics g, int x, int y, BoardState lastCollapsed)
	{
		x += 5;
		final int rad = SMALL_NODE_RADIUS;
		final int diam = 2 * rad;
		final int deltaX = -COLLAPSED_DRAW_DELTA_X + 2;
		final int deltaY = -COLLAPSED_DRAW_DELTA_Y;
		
		Color transitionColor = getCollapsedTransitionColor(lastCollapsed);
	
		Graphics2D g2D = (Graphics2D)g;
		g2D.setStroke(thin);
		g2D.setColor(Color.black);
        for (int c = 0; c < 3; ++c)
        {
            Polygon tri = makeTriangle(x - rad + (c - 1) * deltaX, y, diam/2);
            g.setColor(transitionColor);
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

	/**
	 * When the user hovers over the transition, draws the corresponding justification image
	 * @param g the graphics to use to draw
	 */
	private void drawMouseOver(Graphics2D g)
	{	
		BoardState B = mouseOver.getState();
		//J contains both basic rules and contradictions
		Justification J = B.getJustification();
		int w, h;
		g.setStroke(thin);

		w = (int)(100 * (100/(float)getZoom()));
		h = (int)(100 * (100/(float)getZoom()));
		float scale = (100/(float)getZoom());
		int offset = (int)(scale*30);

		JViewport vp = getViewport();
		BufferedImage image = new BufferedImage(vp.getWidth(), vp.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g_tmp = image.createGraphics();
		int v_offset = 0;

		if((mouseOver.getState().getJustification() != null)||(mouseOver.getState().getCaseRuleJustification() != null))
		{
			if((mouseOver.getState().justificationText != null)&&(mouseOver.getState().getColor() != TreePanel.nodeColor))
			{
				g_tmp.setColor(Color.black);
				String[] tmp = mouseOver.getState().justificationText.split("\n");
				v_offset = 10+tmp.length*14;
				for(int c1=0;c1<tmp.length;c1++)
				{
					g_tmp.drawString(tmp[c1],0,(14*c1)+10);
				}
			}
			g_tmp.setColor(Color.gray);
			g_tmp.drawRect(0,v_offset,100,100);
		}

		if (J != null)
		{
			g_tmp.drawImage(J.getImageIcon().getImage(), 0, v_offset, null);
		}
		CaseRule CR = B.getCaseSplitJustification();
		if (CR != null)
		{
			g_tmp.drawImage(CR.getImageIcon().getImage(), 0, v_offset, null);
			return;
		}

		g.drawImage(image, mousePoint.x+(int)(scale*30), mousePoint.y-(int)(scale*30), (int)(scale*vp.getWidth()), (int)(scale*vp.getHeight()), null);
	}
}
