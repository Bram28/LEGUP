package edu.rpi.phil.legup.newgui;

import java.awt.FlowLayout;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JToggleButton;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.Justification;
import edu.rpi.phil.legup.Contradiction;
import edu.rpi.phil.legup.Legup;
import edu.rpi.phil.legup.Selection;

import javax.swing.*;
import java.awt.*;
import javax.swing.TransferHandler;
import java.awt.datatransfer.*;
import java.awt.event.*;
import javax.swing.ImageIcon;
import javax.swing.JToggleButton;

/**
 * Provides a user interface for contradiction justifications
 *
 */
public class ContradictionPanel extends JustificationPanel
{
	private static final long serialVersionUID = -2304281047341398965L;

	protected final ImageIcon icon = new ImageIcon("images/Contradictions.gif");
	protected final String name = "Contradictions";
	protected final String toolTip = "Contradictions";
	//MouseListener listener = new DragMouseAdapter();
	private Vector<Contradiction> contradictions = null;

	/**
	 * Create a new ContradictionPanel
	 */
	ContradictionPanel(JustificationFrame jf)
	{
		this.parentFrame = jf;
		setLayout(new WrapLayout());
	}

	/**
	 * Set the contradictions displayed by this contradiction panel
	 * @param contradictions the vector of Contradictions
	 */
	public void setContradictions(Vector<Contradiction> contradictions)
	{
		this.contradictions = contradictions;
		clearButtons();

		buttons = new JToggleButton[contradictions.size()];

		for (int x = 0; x < contradictions.size(); ++x)
		{
			Contradiction c = contradictions.get(x);
			buttons[x] = new JToggleButton(c.getImageIcon());
			this.parentFrame.getButtonGroup().add(buttons[x]);

			buttons[x].setToolTipText(c.getName() + ": " + c.getDescription());
			buttons[x].addActionListener(this);
			//removed due to drag-drop being de-prioritized
			//buttons[x].addMouseListener(listener);
			//buttons[x].setTransferHandler(new TransferHandler("icon"));
			add(buttons[x]);
		}

		revalidate();
	}

	/**
	 * Check if the given contradiction can be applied to current board state
	 * @param c the contradiction to be applied
	 */
	private void checkContradiction(Contradiction c)
	{
		Selection sel = Legup.getInstance().getSelections().getFirstSelection();

		if (sel.isState())
		{
			BoardState state = sel.getState();

			// Update: Only show validity if Immediate Feedback is on
			state.setJustification(c);
			String error = c.checkContradiction(state);
			JustificationFrame.justificationApplied(state, c);
			parentFrame.resetJustificationButtons();

			if (error == null && LEGUP_Gui.profFlag(LEGUP_Gui.IMD_FEEDBACK)) parentFrame.setStatus(true,"The rule is applied correctly!");
			else if (LEGUP_Gui.profFlag(LEGUP_Gui.IMD_FEEDBACK)) parentFrame.setStatus(false, error);
		}
		else
			parentFrame.setStatus(false, "Contradictions can only be applied to states, not transitions.");
	}

	/**
	 * Depresses the current rule button for user display
	 * @param c Rule to be pressed
	 * @return Whether or not the rule exists
	 */
	public boolean setContradiction(Contradiction c)
	{
		for (int x = 0; x < contradictions.size(); ++x)
		{
			if (contradictions.get(x).equals(c))
			{
				buttons[x].setSelected(true);
				checkContradiction(c);
				return true;
			}
		}
		return false;
	}

	@Override
	protected Justification addJustification(int button)
	{
		Selection selection = Legup.getInstance().getSelections().getFirstSelection();
		BoardState cur = selection.getState();
		
		if (cur.isModifiable()) {
			if (cur.getSingleParentState().getCaseRuleJustification() != null)
				return null;
			
			cur.setJustification(contradictions.get(button));
		} else {
			if (cur.getCaseRuleJustification() != null)
				return null;
			
			//add new transition
			BoardState next = cur.addTransitionFrom();
			next.setJustification(contradictions.get(button));
			Legup.getInstance().getSelections().setSelection(new Selection(next, false));
			return contradictions.get(button);
		}
	}

	@Override
	protected void checkJustification(int button)
	{
		checkContradiction(contradictions.get(button));
	}

	@Override
	protected Justification doDefaultApplication(int index, BoardState state)
	{
		//There currently are no default applications for contradictions
		//Maybe if we implment justifying a contradiction location
		return null;
	}
}

