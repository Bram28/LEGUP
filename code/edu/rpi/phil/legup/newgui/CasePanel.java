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
import edu.rpi.phil.legup.newgui.CaseRuleSelectionHelper;
import edu.rpi.phil.legup.PuzzleModule;
import edu.rpi.phil.legup.puzzles.treetent.TreeTent;
import edu.rpi.phil.legup.puzzles.treetent.CaseLinkTree;
import edu.rpi.phil.legup.puzzles.treetent.ExtraTreeTentLink;

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
			Object[] msg = new Object[2];
			CaseRuleSelectionHelper crsh = new CaseRuleSelectionHelper(null/*Legup.getInstance().getGui()*/);
			crsh.mode = caseRules.get(button).crshMode();
			crsh.tileType = caseRules.get(button).crshTileType();
			msg[0] = "Select where you would like to apply the CaseRule, and then select ok.";
			msg[1] = crsh;
			JOptionPane.showMessageDialog(null,msg);
			if((crsh.pointSelected.x == -5) && (crsh.pointSelected.y == -5))
			{
				//System.out.println("Nothing selected.");
				return null;
			}
			else
			{
				//System.out.println("Point ("+crsh.pointSelected.x+","+crsh.pointSelected.y+") selected.");
				PuzzleModule pm = Legup.getInstance().getPuzzleModule();
				if(crsh.mode == CaseRuleSelectionHelper.MODE_TILE)
				{
					int quantityofcases = Legup.getInstance().getPuzzleModule().numAcceptableStates(); 
					for (int i = 1; i < quantityofcases; i++)
					{
						BoardState tmp = cur.addTransitionFrom();
						tmp.setCaseSplitJustification(caseRules.get(button));
						tmp.setCellContents(crsh.pointSelected.x,crsh.pointSelected.y,pm.getStateNumber(pm.getStateName(i)));
						tmp.endTransition();
					}
				}
				else if(crsh.mode == CaseRuleSelectionHelper.MODE_COL_ROW)
				{
					boolean row = (crsh.pointSelected.x == -1)? true : false;
					int where = (row)? crsh.pointSelected.y : crsh.pointSelected.x;
					int num_blanks = cur.numEmptySpaces(where,row);
					int[] whatgoesintheblanks = new int[num_blanks];
					int num_cases = 1;
					for(int c1=0;c1<num_blanks;c1++)
					{
						num_cases = num_cases*(Legup.getInstance().getPuzzleModule().numAcceptableStates()-1);
						whatgoesintheblanks[c1] = 1;
					}
					for(int c1=0;c1<num_cases;c1++)
					{
						boolean skip = false;
						if(pm instanceof TreeTent)
						{
							int num_tents = 0;
							for(int n=0;n<num_blanks;n++)
							{
								if(whatgoesintheblanks[n] == 1)num_tents++;
							}
							int correct_tents = 0;
							if(row)
							{
								correct_tents = TreeTent.translateNumTents(cur.getLabel(BoardState.LABEL_RIGHT,where)); 
							}
							else
							{
								correct_tents = TreeTent.translateNumTents(cur.getLabel(BoardState.LABEL_BOTTOM,where)); 
							}
							for(int n=0;n<((row)?(cur.getWidth()):(cur.getHeight()));n++)
							{
								//subtract the amount of tents already in the row
								correct_tents -= (TreeTent.CELL_TENT == (cur.getCellContents(row?n:where,row?where:n)))?1:0;
							}
							if(num_tents != correct_tents)skip=true;
						}
						if(!skip)
						{
							BoardState tmp = cur.addTransitionFrom();
							tmp.setCaseSplitJustification(caseRules.get(button));
							tmp.fillBlanks(where,row,whatgoesintheblanks);
							tmp.endTransition();
						}
						whatgoesintheblanks[0]++;
						for(int c2=0;c2+1<num_blanks;c2++)
						{
							if(whatgoesintheblanks[c2] >= pm.numAcceptableStates())
							{
								whatgoesintheblanks[c2]=1;
								whatgoesintheblanks[c2+1]++;
							}
						}
					}
				}
				else if(crsh.mode == CaseRuleSelectionHelper.MODE_TILETYPE)
				{
					if(pm instanceof TreeTent)
					{
						if(caseRules.get(button) instanceof CaseLinkTree)
						{
							//int num_adj_blanks = ((CaseLinkTree)caseRules.get(button)).calcAdjacentTiles(cur,crsh.pointSelected,TreeTent.CELL_UNKNOWN);
							for(int c1=0;c1<4;c1++) //4: one for each orthagonal direction
							{
								int x = crsh.pointSelected.x;
								int y = crsh.pointSelected.y;
								if(c1<2)x += ((c1%2 == 0)?-1:1);
								else y += ((c1%2 == 0)?-1:1);
								if(x < 0 || x >= cur.getWidth() || y < 0 || y >= cur.getHeight())continue;
								if(cur.getCellContents(x,y) != TreeTent.CELL_UNKNOWN)continue;
								BoardState tmp = cur.addTransitionFrom();
								tmp.setCaseSplitJustification(caseRules.get(button));
								tmp.setCellContents(x,y,TreeTent.CELL_TENT);
								for(int c2=0;c2<4;c2++)
								{
									if(c1 == c2)continue;
									int x2 = crsh.pointSelected.x;
									int y2 = crsh.pointSelected.y;
									if(c2<2)x2 += ((c2%2 == 0)?-1:1);
									else y2 += ((c2%2 == 0)?-1:1);
									if(x2 < 0 || x2 >= cur.getWidth() || y2 < 0 || y2 >= cur.getHeight())continue;
									if(cur.getCellContents(x2,y2) != TreeTent.CELL_UNKNOWN)continue;
									tmp.setCellContents(x2,y2,TreeTent.CELL_GRASS);
								}
								ExtraTreeTentLink link = new ExtraTreeTentLink(new Point(x,y),crsh.pointSelected);
								tmp.addExtraData(link);
								tmp.extraDataDelta.add(link);
								tmp.endTransition();
							}
						}
					}
				}
				if((cur.getTransitionsFrom().size() > 0) && (cur.getTransitionsFrom().get(0) != null))
				{
					Legup.getInstance().getSelections().setSelection(new Selection(cur.getTransitionsFrom().get(0),false));
				}
			}
		}
		else
		{
			cur.setCaseSplitJustification(caseRules.get(button));
		}
		//Legup.getInstance().getSelections().setSelection(new Selection(cur.getTransitionsFrom().get(0), false));
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
