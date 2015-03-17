package edu.rpi.phil.legup;

import java.awt.Point;
//import java.util.LinkedHashSet;
import edu.rpi.phil.legup.newgui.CaseRuleSelectionHelper;

/**
 * This clas represents a Case Rule, which can be applied to the parent of the splitting
 * @author Stan
 *
 */
public abstract class CaseRule extends Justification
{	
	static final long serialVersionUID = 9003L;
	protected String defaultApplicationText;
    public CaseRuleSelectionHelper getSelectionHelper()
    {
        return new CaseRuleSelectionHelper(CaseRuleSelectionHelper.onlyModifiableCells());
    }
	//do the case rule autogeneration, return the state to be transitioned to
	public BoardState autoGenerateCases(BoardState cur, Point pointSelected)
	{
		PuzzleModule pm = Legup.getInstance().getPuzzleModule();
		int quantityofcases = pm.numAcceptableStates(); 
		for (int i = 1; i < quantityofcases; i++)
		{
			BoardState tmp = cur.addTransitionFrom();
			tmp.setCaseSplitJustification(this);
			tmp.setCellContents(pointSelected.x,pointSelected.y,pm.getStateNumber(pm.getStateName(i)));
			tmp.endTransition();
		}
		return Legup.getCurrentState();
	}
	
	/**
	 * Was this case rule applied correctly to this parent state
	 * @param state the state where we apply it
	 * @return null iff it was a valid application, the error string otherwise
	 */
	public final String checkCaseRule(BoardState state)
	{
		String rv = checkCaseRuleRaw(state);
		BoardState parent = state.getSingleParentState(); 
		if(parent != null)
		if((rv == null) && (parent.numNonContradictoryChildren() > 1))
		{
			rv = caseSetupMessage();
		}
		return rv;
	}
	
	public static String caseSetupMessage()
	{
		return "The cases are set up correctly, but not all\nbut one of them lead to a contradiction.";
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

