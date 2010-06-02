package edu.rpi.phil.legup.newgui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.CaseRule;
import edu.rpi.phil.legup.Contradiction;
import edu.rpi.phil.legup.Legup;
import edu.rpi.phil.legup.PuzzleModule;
import edu.rpi.phil.legup.PuzzleRule;
import edu.rpi.phil.legup.Selection;

/**
 * Frame for holding tabbed contradiction panels for user justification.
 *
 */
public class JustificationFrame extends JInternalFrame implements TreeSelectionListener, BoardDataChangeListener
{
	private static final long serialVersionUID = -2304281047341398965L;

	private BasicRulePanel basicRulePanel = null;
	private ContradictionPanel contradictionPanel = null;
	private CasePanel casePanel = null;

	private static final String checkBox = "<font style=\"color:#00CD00\"> \u2714 </font>";
	private static final String xBox = "<font style=\"color:#FF0000\"> \u2718 </font>";
	private static final String htmlHead = "<html>";
	private static final String htmlTail = "</html>";

	private JTabbedPane tabs = new JTabbedPane();
	private JLabel status = new JLabel();

	private static Vector <JustificationAppliedListener> justificationListeners =
		new Vector <JustificationAppliedListener>();

	private ButtonGroup bg = new ButtonGroup();
	ButtonGroup getButtonGroup(){return bg;}

	JustificationFrame(String title, LEGUP_Gui parent)
	{
		super(title);

		basicRulePanel = new BasicRulePanel(this);
		tabs.addTab(basicRulePanel.name, basicRulePanel.icon, basicRulePanel, basicRulePanel.toolTip);

		casePanel = new CasePanel(this);
		tabs.addTab(casePanel.name, casePanel.icon, casePanel, casePanel.toolTip);

		contradictionPanel = new ContradictionPanel(this);
		tabs.addTab(contradictionPanel.name, contradictionPanel.icon, contradictionPanel, contradictionPanel.toolTip);

		JScrollPane scroller = new JScrollPane(tabs);

		JPanel main = new JPanel();
		main.setLayout(new BorderLayout());
		main.add(scroller,BorderLayout.CENTER);

		status.setPreferredSize(new Dimension(400,20));
		main.add(status,BorderLayout.SOUTH);

		add(main);

		Legup.getInstance().getSelections().addTreeSelectionListener(this);
		BoardState.addCellChangeListener(this);
	}

	/**
	 * Reset the justification button and status string
	 *
	 */
	public void resetJustificationButtons()
	{
		// bg.clearSelection();
		resetStatus();
	}

	/**
	 * Reset the status label to the emtpy string
	 *
	 */
	public void resetStatus()
	{
		status.setText("");
	}

	/**
	 * Set the status label to a value. Use resetStatus to clear it.
	 * @param check true iff we want a check box, if false we'll have a red x box
	 * @param text the text we're setting the label to display
	 */
	public void setStatus(boolean check, String text)
	{
		String box = (check ? checkBox : xBox);
		status.setText(htmlHead + box + text + htmlTail);
	}

	public static void addJustificationAppliedListener(JustificationAppliedListener j)
	{
		justificationListeners.add(j);
	}

	public static void justificationApplied(BoardState s, Object j)
	{
		for (int x = 0; x < justificationListeners.size(); ++x)
		{
			JustificationAppliedListener l = justificationListeners.get(x);
			l.justificationApplied(s,j);
		}
	}

	public void setJustifications(PuzzleModule pm)
	{
		basicRulePanel.setRules(pm.getRules());
		contradictionPanel.setContradictions(pm.getContradictions());
		casePanel.setCaseRules(pm.getCaseRules());
	}

	//TreeSelectionListener methods
	public void treeSelectionChanged(ArrayList <Selection> newSelectionList)
	{
		//Check we are dealing with 1 state/case
		if (newSelectionList.size() != 1)
			return;

		Selection newSelection = newSelectionList.get(0);
		if (newSelection == null)
			return;

		BoardState newState = newSelection.getState();

		if(newSelection.isState()) //Contradiction and basic rule
		{
			Object j = newState.getJustification();

			if (j == null)
			{
				bg.clearSelection();
				resetJustificationButtons();
			}
			else if (j instanceof PuzzleRule)
			{
				PuzzleRule pr = (PuzzleRule)j;
				if(basicRulePanel.setRule(pr))
					tabs.setSelectedComponent(basicRulePanel);
			}
			else if (j instanceof Contradiction)
			{
				Contradiction pr = (Contradiction)j;
				if(contradictionPanel.setContradiction(pr))
					tabs.setSelectedComponent(contradictionPanel);
			}
		}
		else //Case Rule
		{
			CaseRule j = newState.getCaseSplitJustification();

			if (j == null)
				resetJustificationButtons();
			else
			{
				if(casePanel.setCaseRule(j))
					tabs.setSelectedComponent(casePanel);
			}
			tabs.setSelectedComponent(casePanel);
		}
	}

	public void boardDataChanged(BoardState state)
	{
		this.resetStatus();
	}
}
