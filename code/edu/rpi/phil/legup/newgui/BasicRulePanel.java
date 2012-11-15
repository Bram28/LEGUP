package edu.rpi.phil.legup.newgui;

import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


import java.util.Vector;

import javax.swing.*;
import java.awt.*;

import javax.swing.TransferHandler;
import java.awt.datatransfer.*;
import java.awt.event.*;
import javax.swing.ImageIcon;
import javax.swing.JToggleButton;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.Legup;
import edu.rpi.phil.legup.PuzzleRule;
import edu.rpi.phil.legup.Selection;

/**
 * Provides a user interface for users to provide basic rule justifications
 *
 */
public class BasicRulePanel extends JustificationPanel
{
	private static final long serialVersionUID = -2304281047341398965L;

	protected final ImageIcon icon = new ImageIcon("images/Basic Rules.gif");
	protected final String name = "Basic Rules";
	protected final String toolTip = "Basic Rules";
	//MouseListener listener = new DragMouseAdapter();
	
	private Vector<PuzzleRule> rules = null;

	/**
	 * Create a new RulePanel
	 */
	BasicRulePanel(JustificationFrame jf)
	{
		this.parentFrame = jf;
		setLayout(new WrapLayout());
	}

	/**
	 * set the rules displayed by this rule panel
	 * @param rules the vector of PuzzleRules
	 */
	public void setRules(Vector<PuzzleRule> rules)
	{
		this.rules = rules;
		clearButtons();

		buttons = new JToggleButton[rules.size()];

		for (int x = 0; x < rules.size(); ++x)
		{
			PuzzleRule pr = rules.get(x);
			buttons[x] = new JToggleButton(pr.getImageIcon());
			this.parentFrame.getButtonGroup().add(buttons[x]);

			buttons[x].setToolTipText(pr.getName() + ": " + pr.getDescription());
			buttons[x].addActionListener(this);
			//removed due to drag-drop being de-prioritized
			//buttons[x].addMouseListener(listener);
			//buttons[x].setTransferHandler(new TransferHandler("icon"));
			add(buttons[x]);
		}

		revalidate();
	}

	/**
	 * Check if the given rule can be applied to current board state using the current puzzle module
	 * This also sets the justification for the current state
	 * @param r the rule to be applied
	 */
	private void checkRule(PuzzleRule r)
	{
		Selection sel = Legup.getInstance().getSelections().getFirstSelection();

		if (sel == null)
		{
			return;
		}

		if( !sel.isState())
		{
			parentFrame.setStatus(false, "Basic rules can only be applied to states, not transitions.");
			return;
		}

		// Update: Only show status if Immediate Feedback is allowed
		BoardState state = sel.getState();
		state.setJustification(r);

		String error = r.checkRule(state);

		JustificationFrame.justificationApplied(state, r);
		parentFrame.resetJustificationButtons();

		if (error == null && LEGUP_Gui.profFlag(LEGUP_Gui.IMD_FEEDBACK))
			parentFrame.setStatus(true,"The rule is applied correctly!");
		else if (LEGUP_Gui.profFlag(LEGUP_Gui.IMD_FEEDBACK)) parentFrame.setStatus(false, error);
	}

	/**
	 * Depresses the current rule button for user display
	 * @param c Rule to be pressed
	 * @return Whether or not the rule exists
	 */
	public boolean setRule(PuzzleRule c)
	{
		for (int x = 0; x < rules.size(); ++x)
		{
			if (rules.get(x).equals(c))
			{
				buttons[x].setSelected(true);
				checkRule(c);
				return true;
			}
		}
		return false;
	}

	@Override
	protected void addJustification(int button)
	{
		Selection selection = Legup.getInstance().getSelections().getFirstSelection();
		BoardState cur = selection.getState();
		
		if (cur.isModifiable()) {
			if (cur.getSingleParentState().getCaseRuleJustification() != null)
				return;
			
			cur.setJustification(rules.get(button));
		} else {
			if (cur.getCaseRuleJustification() != null)
				return;
			
			//add new transition (if the state has no children)
			BoardState next;
			if(cur.getTransitionsFrom().size() == 0) //add new node with justification
			{
				next = cur.addTransitionFrom();
			}
			else if (cur.getTransitionsFrom().size() == 1) //change justification on existing node
			{
				next = cur.getTransitionsFrom().lastElement();
			}
			else return; //don't do anything if there's already a split (since this is a basic rule)
			next.setJustification(rules.get(button));
			Legup.getInstance().getSelections().setSelection(new Selection(next, false));
		}
		
	}

	@Override
	protected void checkJustification(int button)
	{
		checkRule(rules.get(button));
	}
	
	@Override
	protected void doDefaultApplication(int index, BoardState state)
	{
		PuzzleRule r = rules.get(index);
		state.setJustification(r);
		JustificationFrame.justificationApplied(state, r);
		boolean legal = r.doDefaultApplication(state);

		if (!legal)
			parentFrame.setStatus(false, "There is not legal default application that can be applied.");
		else
			parentFrame.setStatus(true, "The rule is applied correctly!");
	}
	
	
	        
}
