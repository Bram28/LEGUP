package edu.rpi.phil.legup;

import java.awt.Point;
import java.util.Vector;
import edu.rpi.phil.legup.newgui.CaseRuleSelectionHelper;

/**
 * This clas represents a Case Rule, which can be applied to the parent of the splitting
 * @author Stan
 *
 */
public abstract class CaseRule extends Justification implements java.io.Serializable
{	
	static final long serialVersionUID = 9003L;
	protected String defaultApplicationText;
	public int crshMode(){return CaseRuleSelectionHelper.MODE_TILE;}
	public Vector<Integer> crshTileType()
	{
		return new Vector<Integer>(0);
	}
	
	/**
	 * Was this case rule applied correctly to this parent state
	 * @param state the state where we apply it
	 * @return null iff it was a valid application, the error string otherwise
	 */
	public final String checkCaseRule(BoardState state)
	{
		//Perhaps there are other checks needed?
		if(!allButOneChildLeadToContradiction(state.getSingleParentState()))
		{
			return "This set of cases does not lead to exactly one non-contradictory state.";
		}
		return checkCaseRuleRaw(state);
	}
	
	public boolean allButOneChildLeadToContradiction(BoardState state)
	{
		return (state.doesNotLeadToContradiction() != null);
	}
	
	protected abstract String checkCaseRuleRaw(BoardState state);
	
	
	public String getApplicationText()
	{
		return defaultApplicationText;
	}
	
public boolean startDefaultApplication(BoardState state)
    {
    	return startDefaultApplicationRaw(state);
    }


	protected boolean startDefaultApplicationRaw(BoardState state)
    {
    	return false;
    }
	
	
	public boolean doDefaultApplication(BoardState state, PuzzleModule pm, Point location)
    {
    	boolean rv = doDefaultApplicationRaw(state, pm, location);
    	
    	if (rv)
    	{
    		state.boardDataChanged();
    	}
    	
    	return rv;
    }
    
    /**
     * Apply the default application of this rule
     * @param state the board we're using
     * @param pm the puzzle module we're using
     * @return true iff we have applied a rule correctly
     */
    protected boolean doDefaultApplicationRaw(BoardState state, PuzzleModule pm, Point location)
    {
    	return false;
    }
}

