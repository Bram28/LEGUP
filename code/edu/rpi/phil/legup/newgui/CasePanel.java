package edu.rpi.phil.legup.newgui;

import java.awt.FlowLayout;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JToggleButton;
import javax.swing.JOptionPane;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.Justification;
import edu.rpi.phil.legup.CaseRule;
import edu.rpi.phil.legup.Legup;
import edu.rpi.phil.legup.Selection;
import edu.rpi.phil.legup.Permutations;
import edu.rpi.phil.legup.newgui.CaseRuleSelectionHelper;
import edu.rpi.phil.legup.PuzzleModule;
import edu.rpi.phil.legup.puzzles.treetent.TreeTent;
import edu.rpi.phil.legup.puzzles.treetent.CaseLinkTree;
import edu.rpi.phil.legup.puzzles.treetent.CaseLinkTent;
import edu.rpi.phil.legup.puzzles.treetent.ExtraTreeTentLink;
import edu.rpi.phil.legup.puzzles.lightup.LightUp;
import edu.rpi.phil.legup.puzzles.lightup.CaseSatisfyNumber;

import javax.swing.*;
import java.awt.*;
import javax.swing.TransferHandler;
import java.awt.datatransfer.*;
import java.awt.event.*;
import javax.swing.ImageIcon;
import javax.swing.JToggleButton;
/**
 * Provides a user interface for users to provide case rule justifications
 *
 */
public class CasePanel extends JustificationPanel
{
	private static final long serialVersionUID = -2304281047341398965L;

	protected final ImageIcon icon = new ImageIcon("images/Case Rules.gif");
	protected final String name = "Case Rules";
	protected final String toolTip = "Case Rules";
	//MouseListener listener = new DragMouseAdapter();
	private Vector<CaseRule> caseRules = null;

	private CaseRule defaultApplication; //NEEDED! Not yet reimplmented!

	/**
	 * Create a new CasePanel
	 */
	CasePanel(JustificationFrame jf)
	{
		this.parentFrame = jf;
		setLayout(new WrapLayout());
	}

	/**
	 * set the case rules displayed by this case rule panel
	 * @param caseRules the vector of CaseRules
	 */
	public void setCaseRules(Vector<CaseRule> caseRules)
	{
		this.caseRules = caseRules;
		clearButtons();

		buttons = new JToggleButton[caseRules.size()];

		for (int x = 0; x < caseRules.size(); ++x)
		{
			CaseRule c = caseRules.get(x);
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
	 * Check if the given case Rule can be applied to current board state
	 * @param c the case rule to be applied
	 */
	private void checkCaseRule(CaseRule c)
	{
		Selection sel = Legup.getInstance().getSelections().getFirstSelection();

		if (sel.isTransition())
		{
			BoardState state = sel.getState();

			// Update: Check only if immediate feedback enabled
			state.setCaseSplitJustification(c);
			String error = c.checkCaseRule(state);
			JustificationFrame.justificationApplied(state, c);
			parentFrame.resetJustificationButtons();

			if (error == null && LEGUP_Gui.profFlag(LEGUP_Gui.IMD_FEEDBACK))
				parentFrame.setStatus(true,"The case rule is applied correctly!");
			else if (LEGUP_Gui.profFlag(LEGUP_Gui.IMD_FEEDBACK)) parentFrame.setStatus(false, error);
		}
		else
		{
			parentFrame.resetJustificationButtons();
			parentFrame.setStatus(false, "Case Rules can only be applied to transitions, not states.");
			sel.getState().setJustification(null);
		}

		//parent.rep
	}


	/**
	 * Depresses the current rule button for user display
	 * @param c Rule to be pressed
	 * @return Whether or not the rule exists
	 */
	public boolean setCaseRule(CaseRule c)
	{
		for (int x = 0; x < caseRules.size(); ++x)
		{
			if (caseRules.get(x).equals(c))
			{
				buttons[x].setSelected(true);
				checkCaseRule(c);
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
		
		if (cur.getTransitionsFrom().size() > 0)
			return null;
		if (cur.isModifiable() && Legup.getInstance().getGui().autoGenCaseRules)
			return null;
		if (!cur.isModifiable() && !Legup.getInstance().getGui().autoGenCaseRules)
			return null;
		if (cur.getCaseRuleJustification() != null)
			return null;

		CaseRule r = caseRules.get(button);
		
		/*int quantityofcases = Integer.valueOf(JOptionPane.showInputDialog(null,"How many branches?")).intValue();
		if(quantityofcases > 10)quantityofcases = 10; //some sanity checks on the input, to prevent
		if(quantityofcases < 2)quantityofcases = 2; //the user from creating 100 nodes or something
		*/
		if(Legup.getInstance().getGui().autoGenCaseRules)
		{
			CaseRuleSelectionHelper crsh = new CaseRuleSelectionHelper(null/*Legup.getInstance().getGui()*/);
			crsh.mode = caseRules.get(button).crshMode();
			crsh.tileTypes = caseRules.get(button).crshTileType();
            crsh.showInNewDialog();
			if((crsh.pointSelected.x == -5) && (crsh.pointSelected.y == -5))
			{
				//System.out.println("Nothing selected.");
				return null;
			}
			else
			{
				//System.out.println("Point ("+crsh.pointSelected.x+","+crsh.pointSelected.y+") selected.");
				PuzzleModule pm = Legup.getInstance().getPuzzleModule();
				//Legup.getInstance().getGui().getTree().tempSuppressUndoPushing = true;
				BoardState b = caseRules.get(button).autoGenerateCases(cur,crsh.pointSelected);
				if(b != null)Legup.getInstance().setCurrentState(b);
				if((cur.getTransitionsFrom().size() > 0) && (cur.getTransitionsFrom().get(0) != null))
				{
					Legup.setCurrentState(cur.getTransitionsFrom().get(0));
				}
				//Legup.getInstance().getGui().getTree().tempSuppressUndoPushing = false;
				//Legup.getInstance().getGui().getTree().pushUndo();
			}
		}
		else
		{
			if(cur.getSingleParentState().getTransitionsFrom().size() <= 1)cur.setCaseSplitJustification(caseRules.get(button));
			else
			{
				cur.setCaseSplitJustification(cur.getSingleParentState().getTransitionsFrom().get(0).getCaseRuleJustification());
				if(cur.getSingleParentState().getTransitionsFrom().get(0).getCaseRuleJustification() != caseRules.get(button))
				{
					String msg = "Different case rules cannot be selected for the same branch set, the rule used for the first branch was used instead of the one selected.";
					Legup.getInstance().getGui().showStatus(msg,true,8); //timer constant found via trial & error
					//JOptionPane.showMessageDialog(null,msg);
					//System.out.println(msg);
				}
			}
		}
		//Legup.setCurrentState(cur.getTransitionsFrom().get(0));
		return r;
	}

	@Override
	protected void checkJustification(int button)
	{
		checkCaseRule(caseRules.get(button));
	}

	@Override
	protected Justification doDefaultApplication(int index, BoardState state)
	{
		//We set the current default application so we know which to apply for later
		CaseRule r = caseRules.get(index);
		boolean legal = r.startDefaultApplication(state);

		if (!legal)
		{
			parentFrame.setStatus(false, "There is not legal default application that can be applied.");
			return null;
		}
		else
		{
			parentFrame.setStatus(true, r.getApplicationText());
			Legup.getInstance().getPuzzleModule().defaultApplication = r;
			this.defaultApplication = r;
			return r;
		}
	}
}
