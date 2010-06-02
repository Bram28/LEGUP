package edu.rpi.phil.legup.newgui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.Legup;
import edu.rpi.phil.legup.Selection;

/**
 * This class represents a panel for storing justifications
 * Inherited by ContradictionPanel, BasicRulePanel, and CasePanel
 *
 */
public abstract class JustificationPanel extends JPanel implements ActionListener
{
	private static final long serialVersionUID = -2304281047341398965L;

	protected ImageIcon icon = null;
	protected String name = "";
	protected String toolTip = "";
	
	protected JToggleButton[] buttons = null;
	
	protected JustificationFrame parentFrame = null;
	
	private Object lastSource = null;
	private long lastTime = 0;
	
	
	/**
	 * a button was pressed
	 * @param index the index of the pressed button in the buttons array
	 * @param defaultApplication are we to do the default application of the rule?
	 */
	protected void buttonPressed(int button, boolean defaultApplication)
	{
		if (!defaultApplication)
			checkJustification(button);
		else
			startDefaultApplication(button);
	}
	
	protected abstract void checkJustification(int button);
	protected abstract void doDefaultApplication(int button, BoardState state);
	
	protected final void startDefaultApplication(int index)
	{
		Selection sel = Legup.getInstance().getSelections().getFirstSelection();
		
		BoardState state = sel.getState();
		
		if(!sel.isState() || state.getTransitionsFrom().size() != 0)
		{
			//Must select a state with no children
			parentFrame.setStatus(false, "Default application of case rules must be applied to a state with no children!");
			return;
		}
			
		if (index == -1)
		{
			//?
			return;
		}
		
		if(!Legup.getInstance().getConfig().allowDefaultApplication())
		{
			//No default application permission
			parentFrame.setStatus(false, "Default rule applications are disabled!");
			return;
		}
		
		doDefaultApplication(index, state);
	}
	
	
	/**
	 * Clear the buttons off this panel
	 *
	 */
	protected final void clearButtons()
	{
		if (buttons != null)
		{
			removeAll();
			for (int x = 0; x < buttons.length; ++x)
			{
				buttons[x].removeActionListener(this);
			}
		}
	}
	
	
	//Action Listener method
	/**
	 * Handles an action performed event
	 */
	public void actionPerformed(ActionEvent e)
	{
		boolean useDefault = false;
		Object source = e.getSource();
		long time = e.getWhen();

		if (source == lastSource && (time - lastTime < 500 ))
			useDefault = true;
		
		lastSource = source;
		lastTime = e.getWhen();
		
		for (int x = 0; x < buttons.length; ++x)
		{
			if (source == buttons[x])
			{
				buttonPressed(x,useDefault);
				return;
			}
		}
	}
}
