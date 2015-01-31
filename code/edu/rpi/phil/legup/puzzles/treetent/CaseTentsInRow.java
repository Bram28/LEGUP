package edu.rpi.phil.legup.puzzles.treetent;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.ImageIcon;

import edu.rpi.phil.legup.Legup;
import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.CaseRule;
import edu.rpi.phil.legup.Permutations;
import edu.rpi.phil.legup.PuzzleModule;
import edu.rpi.phil.legup.newgui.CaseRuleSelectionHelper;

public class CaseTentsInRow extends CaseRule
{
	static final long serialVersionUID = 9506L;
	protected final String defaultApplicationText= "Select a row number.";
	public int crshMode(){return CaseRuleSelectionHelper.MODE_COL_ROW;}
	
	public BoardState autoGenerateCases(BoardState cur, Point pointSelected)
	{
		boolean row = (pointSelected.x == -1)? true : false;
		int where = (row)? pointSelected.y : pointSelected.x;
		int num_blanks = cur.numEmptySpaces(where,row);
		int[] whatgoesintheblanks = new int[num_blanks];
		for(int c1=0;c1<whatgoesintheblanks.length;c1++)
		{
			whatgoesintheblanks[c1] = 0;
		}
		int num_defaults = 0;
		
		//start with what the label says
		int correct_tents = row?(TreeTent.translateNumTents(cur.getLabel(BoardState.LABEL_RIGHT,where))):(TreeTent.translateNumTents(cur.getLabel(BoardState.LABEL_BOTTOM,where)));
		//subtract the amount of tents already in the row
		for(int n=0;n<((row)?(cur.getWidth()):(cur.getHeight()));n++)
		{
			correct_tents -= (TreeTent.CELL_TENT == (cur.getCellContents(row?n:where,row?where:n)))?1:0;
		}
		//set the amount of defaults (grass) to the number of tiles that need to be filled minus the correct number of tents
		num_defaults = num_blanks - correct_tents;
		
		//System.out.println(num_defaults);
		if(num_defaults < 0)return null; //state is a contradiction in a way that interferes with the construction of a caserule
		while(Permutations.nextPermutation(whatgoesintheblanks,num_defaults))
		{
			BoardState tmp = cur.addTransitionFrom();
			tmp.setCaseSplitJustification(this);
			tmp.fillBlanks(where,row,whatgoesintheblanks);
			tmp.endTransition();
		}
		return Legup.getCurrentState();
	}
	
	public String getImageName() {return "images/treetent/case_rowcount.png";}
	public CaseTentsInRow()
	{
		setName("Fill In row");
		description = "A row must have the number of tents of its clue.";
		//image = new ImageIcon("images/treetent/case_rowcount.png");
	}
	
	private int numTentsNeededInRow(BoardState state, int row){
		int label = state.getLabel(BoardState.LABEL_RIGHT, row);
		return TreeTent.translateNumTents(label);
	}
	private int numTentsNeededInColumn(BoardState state, int col){
		int label = state.getLabel(BoardState.LABEL_BOTTOM, col);
		return TreeTent.translateNumTents(label);
	}
	
	private int numTentsInRow(BoardState boardState, int row)
    {
		int width = boardState.getWidth();
		int numTents = 0;
		
		for (int i=0;i<width;i++)
		{
			if (boardState.getCellContents(i,row) == 2) // if this cell contains a tent
			{
			    numTents++;
			}
		}
		return numTents;
    }
	private int numTentsInColumn(BoardState boardState, int col)
    {
		int height = boardState.getHeight();
		int numTents = 0;
		
		for (int i=0;i<height;i++)
		{
			if (boardState.getCellContents(col,i) == 2) // if this cell contains a tent
			{
			    numTents++;
			}
		}
		return numTents;
    }
	
	private int numEmptySpacesInRow(BoardState boardState, int row)
    {
		int width = boardState.getWidth();
		int numEmpty = 0;
		
		for (int i=0;i<width;i++)
		{
			if (boardState.getCellContents(i,row) == 0) // if this cell is unknown
			{
				numEmpty++;
			}
		}
		return numEmpty;
    }
	private int numEmptySpacesInColumn(BoardState boardState, int col)
    {
		int height = boardState.getHeight();
		int numEmpty = 0;
		
		for (int i=0;i<height;i++)
		{
			if (boardState.getCellContents(col,i) == 0) // if this cell is unknown
			{
				numEmpty++;
			}
		}
		return numEmpty;
    }
	private static int choose(int x, int y) {
	    if (y < 0 || y > x) return 0;
	    if (y > x/2) {
	        // choose(n,k) == choose(n,n-k), 
	        // so this could save a little effort
	        y = x - y;
	    }

	    int denominator = 1, numerator = 1;
	    for (int i = 1; i <= y; i++) {
	        denominator *= i;
	        numerator *= (x + 1 - i);
	    }
	    return numerator / denominator;
	}
	public String checkCaseRuleRaw(BoardState state)
	{
		BoardState parent = state.getSingleParentState();
		if(parent == null)return "The parent state is null.";
		String rv = null;
		int affectedRow = -1; // a value of -1 indicates that a column is being affected, not a row
		int affectedColumn = -1;
		int numChildStates = parent.getChildren().size();  // how many branches do we have?
		// we will first check one state to see which row/column we are working with 
		// (we still will need to check the rest of the states to make sure they are also changing this row/col)
		BoardState one = parent.getChildren().get(0);
		ArrayList<Point> pointsChangedInFirstNewState = BoardState.getDifferenceLocations(parent,one);
		if(pointsChangedInFirstNewState.size() < 2)
		{
			//this is an "arbitrary" limitation mandated by the algorithm for determining direction
			//changing the way this rule is validated is neccessary for it to be a FULL substitute of
			//RullAllGrass ("finish grass") and RuleAllTents ("finish tents")
			return "At least two squares must be affected by this split";
		}
		// we first check two points to see which row/col they share
		Point firstPointChanged = pointsChangedInFirstNewState.get(0);
		Point secondPointChanged = pointsChangedInFirstNewState.get(1);
		if(firstPointChanged.x == secondPointChanged.x){
			affectedColumn = firstPointChanged.x;
		}
		else{
			if(firstPointChanged.y == secondPointChanged.y){
				affectedRow = firstPointChanged.y;
			}
			else{
				return "Changes must be made within one row or column";
			}
		}
		// now we know which row or column has been affected.
		// next we check how many permutations we will have in this row/column
		int numTentsTotal;
		int numTentsExisting;
		int numEmptySpaces;
		if(affectedRow != -1){ // if we are dealing with a row being changed
			numTentsTotal = numTentsNeededInRow(parent,affectedRow);
			numTentsExisting = numTentsInRow(parent,affectedRow);
			numEmptySpaces = numEmptySpacesInRow(parent,affectedRow);
		}
		else{ // if we are dealing with a column being changed
			numTentsTotal = numTentsNeededInColumn(parent,affectedColumn);
			numTentsExisting = numTentsInColumn(parent, affectedColumn);
			numEmptySpaces = numEmptySpacesInColumn(parent,affectedColumn);
		}
		int numTentsNeeded = numTentsTotal - numTentsExisting;
		if(numTentsNeeded > numEmptySpaces){
			return "There is no way to place "+numTentsNeeded+" tents in "+numEmptySpaces+" empty spaces.";
		}
		// now we do a combinatorial to figure out how many ways there are to place these tents in these empty spaces
		// numEmptySpaces choose numTentsNeeded
		
		int numCombinations = choose(numEmptySpaces,numTentsNeeded);
		if(numChildStates != numCombinations){
			return "The number of branches must be equal to the number of possible\nconfigurations for "+numTentsNeeded+" tents in "+numEmptySpaces+" empty spaces";
		}
		Vector<BoardState> allChildStates = parent.getChildren();
		for(int i = 0; i < allChildStates.size(); i++){
			BoardState currentChildState = allChildStates.get(i);
			ArrayList<Point> pointsChanged = BoardState.getDifferenceLocations(parent,currentChildState);
			if(pointsChanged.size() != numEmptySpaces){
				return "The number of changed cells in each child state must\nbe equal to the number of unfilled states\nin the relevant row or column of the parent";
			}
			
			// make sure that no child states are the same
			for(int j = i+1; j < allChildStates.size(); j++){
				ArrayList<Point> childDifferences = BoardState.getDifferenceLocations(allChildStates.get(j),currentChildState);
				if(childDifferences.size() == 0){
					return "No two child nodes may be the same";
				}
			}
					
			// make sure that all affected cells are in the affected row or column
			for(int j = 0; j < pointsChanged.size(); j++){
				if(affectedRow != -1){ 
					if(pointsChanged.get(j).y != affectedRow){
						return "Each changed cell in each child state must be in the same row or column";
					}
				}
				else{ 
					if(pointsChanged.get(j).x != affectedColumn){
						return "Each changed cell in each child state must be in the same row or column";
					}
				}
				
			}
			// make sure that there are the correct amount of tents placed in the row or column
			int numTentsPlaced = 0;
			for(int j = 0; j < pointsChanged.size(); j++){
				if(currentChildState.getCellContents(pointsChanged.get(j).x ,pointsChanged.get(j).y) == TreeTent.CELL_TENT){
					numTentsPlaced++;
				}
			}
			if(numTentsPlaced != numTentsNeeded){
				return "The number of tents placed in each child cell must be equal\nto the number needed to complete the changed row or column.";
			}
		}
		return rv;
	}
	
	public boolean startDefaultApplicationRaw(BoardState state)
	{
		return true;
	}
	
	public boolean doDefaultApplicationRaw(BoardState state, PuzzleModule pm ,Point location)
	{
		if(location.y > 0 && location.y < state.getHeight( ))
		{
			int tents = 0;
			int unknowns = 0;
			for(int x = 0; x < state.getWidth( ); ++x)
			{
				if(state.getCellContents( x, location.y ) == TreeTent.CELL_TENT)
					++tents;
				else if(state.getCellContents( x, location.y ) == TreeTent.CELL_UNKNOWN)
					++unknowns;
			}
			int tentsneeded = TreeTent.translateNumTents(state.getLabel(BoardState.LABEL_RIGHT, location.y)) - tents;
			int grassneeded = unknowns - tentsneeded;
			
			Vector<Integer> states = new Vector<Integer>();
			states.add( TreeTent.CELL_TENT );
			states.add( TreeTent.CELL_GRASS );
			Vector<Integer> statecounts = new Vector<Integer>();
			statecounts.add( tentsneeded );
			statecounts.add( grassneeded );
			Vector<Integer> conditions = new Vector<Integer>();
			conditions.add( TreeTent.CELL_UNKNOWN);
			Permutations.permutationRow( state, location.y, states, statecounts, conditions );
			state.setCaseSplitJustification(this);
			Permutations.caseContradictionFinder( state, pm );
			return true;
		}
		return false;
	}
}
