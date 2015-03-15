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
    protected Justification getNthJustification(int n) { return caseRules.get(n); }

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

    protected boolean doCaseRuleAutogen(Point point, BoardState cur, int button)
    {
        if((point.x == -5) && (point.y == -5) &&
					caseRules.get(button).getSelectionHelper().mode != CaseRuleSelectionHelper.MODE_NO_TILE_SELECT)
        {
            return false;
        }
        else
        {
            PuzzleModule pm = Legup.getInstance().getPuzzleModule();
            //Legup.getInstance().getGui().getTree().tempSuppressUndoPushing = true;
            BoardState b = caseRules.get(button).autoGenerateCases(cur,point);
            if(b != null) Legup.setCurrentState(b);
            if((cur.getChildren().size() > 0) && (cur.getChildren().get(0) != null))
            {
                Legup.setCurrentState(cur.getChildren().get(0));
            }
            //Legup.getInstance().getGui().getTree().tempSuppressUndoPushing = false;
            //Legup.getInstance().getGui().getTree().pushUndo();
            return true;
        }
    }

    boolean experimentalCaseRuleBoardSwap = true; //still a bit buggy, so use a flag

	@Override
	protected Justification addJustification(final int button)
	{
		Selection selection = Legup.getInstance().getSelections().getFirstSelection();
		final BoardState cur = selection.getState();

		if (cur.getChildren().size() > 0)
			return null;
		if (cur.isModifiable() && Legup.getInstance().getGui().checkCaseRuleGen())
			return null;
		if (!cur.isModifiable() && !Legup.getInstance().getGui().checkCaseRuleGen())
			return null;
		if (cur.getCaseRuleJustification() != null)
			return null;

		final CaseRule r = caseRules.get(button);

		/*int quantityofcases = Integer.valueOf(JOptionPane.showInputDialog(null,"How many branches?")).intValue();
		if(quantityofcases > 10)quantityofcases = 10; //some sanity checks on the input, to prevent
		if(quantityofcases < 2)quantityofcases = 2; //the user from creating 100 nodes or something
		*/
		if(Legup.getInstance().getGui().checkCaseRuleGen())
		{
			final CaseRuleSelectionHelper crsh = caseRules.get(button).getSelectionHelper();
            if(!experimentalCaseRuleBoardSwap)
            {
                crsh.showInNewDialog();
                if(!doCaseRuleAutogen(crsh.pointSelected, cur, button)) { return null; }
            }
            else
            {
                //Board theBoard = Legup.getInstance().getGui().getBoard();
                //Legup.getInstance().getGui().setBoard(crsh);
                if(!(Legup.getInstance().getGui().getBoard() instanceof CaseRuleSelectionHelper))
                {
                    crsh.temporarilyReplaceBoard(Legup.getInstance().getGui(), this);
                    //try { this.wait(); } catch(Exception e){e.printStackTrace();}
                    new Thread(new Runnable(){ public void run() {
                        crsh.blockUntilSelectionMade();
                        if(doCaseRuleAutogen(crsh.pointSelected, cur, button))
                        {
                            buttonPressedContinuation1(r);
                        }
                        Legup.getInstance().getSelections().removeTreeSelectionListener(crsh);
                    }}).start();
                }
            }
		}
		else
		{
			if(cur.getSingleParentState().getChildren().size() <= 1)cur.setCaseSplitJustification(caseRules.get(button));
			else
			{
				cur.setCaseSplitJustification(cur.getSingleParentState().getChildren().get(0).getCaseRuleJustification());
				if(cur.getSingleParentState().getChildren().get(0).getCaseRuleJustification() != caseRules.get(button))
				{
					String msg = "Different case rules cannot be selected for the same branch set, the rule used for the first branch was used instead of the one selected.";
					Legup.getInstance().getGui().showStatus(msg,true,8); //timer constant found via trial & error
					//JOptionPane.showMessageDialog(null,msg);
					//System.out.println(msg);
				}
			}
		}
		//Legup.setCurrentState(cur.getChildren().get(0));
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
