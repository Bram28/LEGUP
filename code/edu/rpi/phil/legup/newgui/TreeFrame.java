package edu.rpi.phil.legup.newgui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.KeyStroke;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.Legup;
import edu.rpi.phil.legup.Selection;

public class TreeFrame extends JInternalFrame implements JustificationAppliedListener, TreeSelectionListener, BoardDataChangeListener
{
	private static final long serialVersionUID = -2304281047341398965L;
	
	private TreeToolbarPanel toolbar = new TreeToolbarPanel(this);
	TreePanel treePanel = null;
	private LEGUP_Gui gui = null;
	
	private JLabel status = new JLabel();

	TreeFrame(String title, Legup main, LEGUP_Gui gui)
	{
		super(title);
		
		this.gui = gui;
		treePanel = new TreePanel();	
		
		Container c = getContentPane();
		
		c.setLayout(new BorderLayout());
		
		c.add(toolbar,BorderLayout.NORTH);
		c.add(treePanel,BorderLayout.CENTER);
		
		status.setPreferredSize(new Dimension(150,20));
		c.add(status,BorderLayout.SOUTH);
		
		JustificationFrame.addJustificationAppliedListener(this);
		
		gui.legupMain.getSelections().addTreeSelectionListener(this);
		BoardState.addCellChangeListener(this);
		
		setupKeyEvents();
	}
	
	/**
	 * Initializes key receptors on this and children components
	 */
	private void setupKeyEvents()
	{
		KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0);
		this.getInputMap(javax.swing.JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(stroke, "KeyEvent.VK_UP");
		this.getActionMap().put("KeyEvent.VK_UP", new AbstractAction() {private static final long serialVersionUID = -2304281047341398965L; public void actionPerformed(ActionEvent event) {navigateTree(KeyEvent.VK_UP);}});
		
		stroke = KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0);
		this.getInputMap(javax.swing.JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(stroke, "KeyEvent.VK_DOWN");
		this.getActionMap().put("KeyEvent.VK_DOWN", new AbstractAction() {private static final long serialVersionUID = -2304281047341398965L; public void actionPerformed(ActionEvent event) {navigateTree(KeyEvent.VK_DOWN);}});
		
		stroke = KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0);
		this.getInputMap(javax.swing.JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(stroke, "KeyEvent.VK_LEFT");
		this.getActionMap().put("KeyEvent.VK_LEFT", new AbstractAction() {private static final long serialVersionUID = -2304281047341398965L; public void actionPerformed(ActionEvent event) {navigateTree(KeyEvent.VK_LEFT);}});
		
		stroke = KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0);
		this.getInputMap(javax.swing.JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(stroke, "KeyEvent.VK_RIGHT");
		this.getActionMap().put("KeyEvent.VK_RIGHT", new AbstractAction() {private static final long serialVersionUID = -2304281047341398965L; public void actionPerformed(ActionEvent event) {navigateTree(KeyEvent.VK_RIGHT);}});
		
	}
	
	
	/**
	 * Add a child to the sate that is currently selected
	 *
	 */
	public void addChildAtCurrentState()
	{
		treePanel.addChildAtCurrentState();
	}
	
	/**
	 * Collapse states in the tree view
	 */
	public void collapseStates()
	{
		treePanel.collapseCurrentState();
	}
	
	/**
	 * Merge the selected states
	 *
	 */
	public void mergeStates()
	{
		treePanel.mergeStates();
	}
	
	/**
	 * Delete the child subtree starting at the current state
	 */
	public void delChildAtCurrentState()
	{
		treePanel.delChildAtCurrentState();
	}

	public void justificationApplied(BoardState state, Object j)
	{
		repaint();
	}
	
	public void treeSelectionChanged(ArrayList <Selection> newSelectionList)
	{
		updateStatus();
	}
	
	public void boardDataChanged(BoardState state)
	{
		updateStatus();
	}
	
	public void updateStatus()
	{
		ArrayList <Selection> newSelectionList = gui.legupMain.getSelections().getCurrentSelection();
		
		if (newSelectionList != null && newSelectionList.size() == 1 
				&& newSelectionList.get(0).isState())
		{
			Selection newSelection = newSelectionList.get(0);
			BoardState newState = newSelection.getState();
			this.status.setText("States: " + newState.countStates() + " Branches: " + newState.countLeaves() + " Max Depth: " + newState.countDepth());
		}
		else
		{
			this.status.setText("");
		}
	}
	
	
	private long keyPressTime = 0;
	private int lastKeyDirection = -1;
	private void navigateTree(int direction)
	{
		Date now = new Date();
		if(now.getTime() < keyPressTime + 200 && lastKeyDirection == direction)
		{
			return;
		}
		keyPressTime = now.getTime();
		lastKeyDirection = direction;
		
		ArrayList<Selection> s = gui.legupMain.getSelections().getCurrentSelection();
		if(s == null)
		{
			return;
		}
		
		if(s.size() != 1 || s.get(0).isTransition())
		{
			return;
		}
		BoardState state = s.get(0).getState();
		
		if(direction == KeyEvent.VK_UP)
		{
			BoardState parent = state.getSingleParentState();
			if(parent != null)
			{
				gui.legupMain.getSelections().setSelection(new Selection(parent, false));
			}
		}
		else if(direction == KeyEvent.VK_DOWN)
		{
			if(state.getTransitionsFrom().size() > 0)
			{
				BoardState child = state.getTransitionsFrom().get(0);
				if(child != null)
				{
					gui.legupMain.getSelections().setSelection(new Selection(child, false));
				}
			}
		}
		else if(direction == KeyEvent.VK_RIGHT)
		{
			BoardState parent = state.getSingleParentState();
			if(parent != null)
			{
				if(parent.getTransitionsFrom().size() > 1)
				{
					for(int x = 0; x < parent.getTransitionsFrom().size() - 1; ++x)
					{
						if(parent.getTransitionsFrom().get(x) == state)
						{
							BoardState sib = parent.getTransitionsFrom().get(x + 1);
							if(sib != null)
							{
								gui.legupMain.getSelections().setSelection(new Selection(sib, false));
							}
						}
					}
				}
			}
		}
		else if(direction == KeyEvent.VK_LEFT)
		{
			BoardState parent = state.getSingleParentState();
			if(parent != null)
			{
				if(parent.getTransitionsFrom().size() > 1)
				{
					for(int x = parent.getTransitionsFrom().size() - 1; x > 0; --x)
					{
						if(parent.getTransitionsFrom().get(x) == state)
						{
							BoardState sib = parent.getTransitionsFrom().get(x - 1);
							if(sib != null)
							{
								gui.legupMain.getSelections().setSelection(new Selection(sib, false));
							}
						}
					}
				}
			}
		}
	}
}
