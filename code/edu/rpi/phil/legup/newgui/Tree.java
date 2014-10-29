package edu.rpi.phil.legup.newgui;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.Justification;
import edu.rpi.phil.legup.CaseRule;
import edu.rpi.phil.legup.Legup;
import edu.rpi.phil.legup.Selection;
import edu.rpi.phil.legup.saveable.SaveableProof;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.Point;

import java.util.ArrayList;
import java.util.Date;
import java.util.Stack;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

import javax.swing.BorderFactory; 
//import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

public class Tree extends JPanel implements JustificationAppliedListener, TreeSelectionListener, BoardDataChangeListener, TransitionChangeListener
{
	private static final long serialVersionUID = -2304281047341398965L;
	
	public boolean modifiedSinceSave = false;
	public boolean modifiedSinceUndoPush = false;
	
	public byte[] origInitState = null;
	public Stack<byte[]> undoStack = new Stack<byte[]>();
	public Stack<ArrayList<Integer>> undoStackState = new Stack<ArrayList<Integer>>();
	public Stack<byte[]> redoStack = new Stack<byte[]>();
	public Stack<ArrayList<Integer>> redoStackState = new Stack<ArrayList<Integer>>();
	public boolean tempSuppressUndoPushing = false;
	
	public int updateStatusTimer = 0;
	
	private class TreeToolbar extends JPanel implements ActionListener
	{
		private static final long serialVersionUID = 8572197337878587284L;

		JButton addChild = new JButton(new ImageIcon("images/AddChild.png"));
		JButton delChild = new JButton(new ImageIcon("images/DelChild.png"));
		JButton merge = new JButton(new ImageIcon("images/Merge.png"));
		JButton collapse = new JButton(new ImageIcon("images/Collapse.png"));
		
		TreeToolbar()
		{
			this.setLayout(new GridLayout(2,2));
			add(addChild);
			addChild.addActionListener(this);
			addChild.setEnabled(false);
			addChild.setToolTipText("Finalize CaseRule");
			//addChild.setEnabled(false);
			//addChild.setToolTipText("Add node (select justification first)");
			add(delChild);
			delChild.addActionListener(this);
			delChild.setToolTipText("Remove currently selected node");
			add(merge);
			merge.addActionListener(this);
			merge.setToolTipText("Merge nodes");
			add(collapse);
			collapse.addActionListener(this);
			collapse.setToolTipText("Collapse nodes");
		}

		public void actionPerformed(ActionEvent e)
		{
			if( e.getSource() == addChild )
			{
				BoardState cur = Legup.getCurrentState();
				//cur.getSingleParentState().getTransitionsFrom().lastElement().getCaseRuleJustification();
				cur.setCaseRuleJustification(cur.getSingleParentState().getFirstChild().getCaseRuleJustification());
				addChildAtCurrentState();
			}
			else if( e.getSource() == delChild )
			{
				delChildAtCurrentState();
			}
			else if( e.getSource() == merge )
			{
				mergeStates();
			}
			else if( e.getSource() == collapse )
			{
				//there was some sort of oddity around here during a merge - Avi
				//delCurrentState();
				collapseStates();
			}
		}

	}

	private TreeToolbar toolbar = new TreeToolbar();
	public TreePanel treePanel = new TreePanel();
	private LEGUP_Gui gui;
	
	private JLabel status = new JLabel();
	public JLabel getStatus(){return status;}
	Tree( LEGUP_Gui gui ){
		this.gui = gui;
		
		JPanel main = new JPanel();
		
		main.setLayout( new BorderLayout() );
		
		main.add(toolbar,BorderLayout.WEST);
		main.add(treePanel,BorderLayout.CENTER);
		
		//status.setPreferredSize(new Dimension(150,20));
		main.add(status,BorderLayout.SOUTH);
				
		TitledBorder title = BorderFactory.createTitledBorder("Tree");
		title.setTitleJustification(TitledBorder.CENTER);
		main.setBorder(title);
		
		setLayout( new BorderLayout() );
		add(main);
		
		// listeners
		JustificationFrame.addJustificationAppliedListener(this);
		gui.legupMain.getSelections().addTreeSelectionListener(this);
		BoardState.addCellChangeListener(this);
		undoStack = new Stack<byte[]>();
		undoStackState = new Stack<ArrayList<Integer>>();
		redoStack = new Stack<byte[]>();
		redoStackState = new Stack<ArrayList<Integer>>();
		tempSuppressUndoPushing = false;
		origInitState = null;
		
		updateStatusTimer = 0;
	}
	
	public void undo()
	{
		if(undoStack.size() > 0)
		{
			tempSuppressUndoPushing = true;
			BoardState state = SaveableProof.bytesToState(undoStack.peek());
			redoStack.push(SaveableProof.stateToBytes(Legup.getInstance().getInitialBoardState()));
			redoStackState.push(Legup.getCurrentState().getPathToNode());
			Legup.getInstance().setInitialBoardState(state);
			Legup.setCurrentState(BoardState.evaluatePathToNode(undoStackState.peek()));
			undoStack.pop();
			undoStackState.pop();
			tempSuppressUndoPushing = false;
		}
		if(undoStack.size() == 0)
		{
			if(origInitState != null)
			{
				BoardState state = SaveableProof.bytesToState(origInitState);
				Legup.getInstance().setInitialBoardState(state);
				while(state.getTransitionsFrom().size()>0)state = state.getTransitionsFrom().lastElement();
				Legup.setCurrentState(state);
			}
		}
	}
	public void redo()
	{
		if(redoStack.size() > 0)
		{
			tempSuppressUndoPushing = true;
			BoardState state = SaveableProof.bytesToState(redoStack.peek());
			undoStack.push(SaveableProof.stateToBytes(Legup.getInstance().getInitialBoardState()));
			undoStackState.push(Legup.getCurrentState().getPathToNode());
			Legup.getInstance().setInitialBoardState(state);
			Legup.setCurrentState(BoardState.evaluatePathToNode(redoStackState.peek()));
			redoStack.pop();
			redoStackState.pop();
			tempSuppressUndoPushing = false;
		}
	}
	
	/**
	 * Add a child to the sate that is currently selected
	 *
	 */
	public void addChildAtCurrentState()
	{
		/*if (currentJustificationApplied instanceof CaseRule){
			toolbar.addChild.setEnabled(true);
		} else {
			toolbar.addChild.setEnabled(false);
		}*/
		treePanel.addChildAtCurrentState(currentJustificationApplied);
		currentJustificationApplied = null;
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
	
	/**
	 * Delete the current state and reposition the children
	 */
	public void delCurrentState()
	{
		treePanel.delCurrentState();
	}

	public void justificationApplied(BoardState state, Justification j)
	{
		/*if (j instanceof CaseRule){
			toolbar.addChild.setEnabled(true);
		} else {
			toolbar.addChild.setEnabled(false);
		}*/
		currentJustificationApplied = j;
		j = null;
		repaint();
	}
	
	public Justification getCurrentJustificationApplied(){
		return currentJustificationApplied;
	}
	private Justification currentJustificationApplied = null;
	
	public static void colorTransitions()
	{
		if(Legup.getInstance().getInitialBoardState() == null)return;
		if(Legup.getInstance().getGui().checkImmediateFeedback())
		{
			Legup.getInstance().getInitialBoardState().evalDelayStatus();
		}
		else
		{
			BoardState.removeColorsFromTransitions();
		}
		Legup.getInstance().getGui().getTree().treePanel.repaint();
	}
	
	public void treeSelectionChanged(ArrayList <Selection> newSelectionList)
	{
		//System.out.println("tree select changed");
		BoardState cur = Legup.getCurrentState();
		if(cur.getSingleParentState() != null)
		{
			if(cur.getSingleParentState().getFirstChild() != null)
			{
				if(cur.getSingleParentState().getFirstChild().getCaseRuleJustification() != null)
				{
					toolbar.addChild.setEnabled(true);
				}
				else
				{
					toolbar.addChild.setEnabled(false);
				}
			}
			else
			{
				toolbar.addChild.setEnabled(false);
			}
		}
		else
		{
			toolbar.addChild.setEnabled(false);
		}
		if(modifiedSinceUndoPush)
		{
			pushUndo();
		}
		modifiedSinceSave = true;
		updateStatus();
		colorTransitions();
	}
	
	public void transitionChanged()
	{
		//pushUndo();
	}
	
	public void pushUndo()
	{
		if(!tempSuppressUndoPushing)
		{
			boolean pushTwice = (undoStack.size() == 0);
			byte[] bytesOfState = SaveableProof.stateToBytes(Legup.getInstance().getInitialBoardState()); 
			if(undoStack.size() > 0)if(bytesOfState.equals(undoStack.peek()))return;
			redoStack.clear();
			redoStackState.clear();
			undoStack.push(bytesOfState);
			undoStackState.push(Legup.getCurrentState().getPathToNode());
			modifiedSinceUndoPush = false;
			if(pushTwice)pushUndo();
		}
	}
	
	public void boardDataChanged(BoardState state)
	{
		modifiedSinceSave = true;
		modifiedSinceUndoPush = true;
		updateStatus();
		colorTransitions();
	}
	
	public void updateStatus()
	{
		updateStatusTimer = ((updateStatusTimer-1) > 0)?(updateStatusTimer-1):0;
		if(updateStatusTimer > 0)return;
		this.status.setText("");
	}
}
