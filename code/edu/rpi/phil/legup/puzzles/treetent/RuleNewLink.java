

package edu.rpi.phil.legup.puzzles.treetent;

import java.awt.Point;
import java.util.ArrayList;

import edu.rpi.phil.legup.BoardState;
//import edu.rpi.phil.legup.PuzzleModule;
import edu.rpi.phil.legup.PuzzleRule;

public abstract class RuleNewLink extends PuzzleRule
{
	static final long serialVersionUID = 9514L;
	public RuleNewLink()
    {
    	super();
    }

    /**
     * Check if this new link is valid
     * @param source
     * @param dest
     * @param state the board state
     * @return true iff the new link is valid
     */
    protected String checkTreeNeededLink(Point tree, Point tent, BoardState state, ArrayList<Object> validLinks)
	{
		int w = state.getWidth();
		int h = state.getHeight();
		ArrayList <Point> points = new ArrayList<Point>();
		
		// first make sure this tree was not already linked
		for (int a = 0; a < validLinks.size(); ++a)
		{
			ExtraTreeTentLink e = (ExtraTreeTentLink)validLinks.get(a);
				
			if (e.pos1.equals(tree) || e.pos2.equals(tree))
			{
				return "the tree already has a link.";
			}
		}
		
		//Make sure the tree does not have any unknowns or unlinked tents around it
		
		// Check Left
		if (tree.x > 0)
			points.add(new Point(tree.x-1,tree.y));

		// Check Up
		if (tree.y > 0)
			points.add(new Point(tree.x,tree.y - 1));

		// Check Down
		if (tree.y + 1 < h)
			points.add(new Point(tree.x,tree.y + 1));
	
		// Check Right
		if (tree.x + 1 < w)
			points.add(new Point(tree.x+1,tree.y));

		for (int x = 0; x < points.size(); ++x)
		{
			Point p = points.get(x);
			int contents = state.getCellContents(p.x,p.y);
			
			if (p.equals(tent))
				continue; // this is the inference they're making
			
			//If we find an unknown, this isn't valid
			if(contents == TreeTent.CELL_UNKNOWN)
			{
				return "there is a blank (which has potential to be a tent).";
			}
			else if(contents == TreeTent.CELL_TENT)
			{
				//Check to see if this other tent has any previous links
				//If it doesn't, this isn't valid
				boolean found = false;
				for (int a = 0; a < validLinks.size(); ++a)
				{
					ExtraTreeTentLink e = (ExtraTreeTentLink)validLinks.get(a);
					if (e.pos1.equals(p) || e.pos2.equals(p))
					{
						found = true;
					}					
				}
				return (found) ? null : "the tree has more than one adjacent unlinked tent.";
			}
		}
		return null;
    }

    protected String checkTentNeededLink(Point tree, Point tent, BoardState state, ArrayList<Object> validLinks)
	{
		int w = state.getWidth();
		int h = state.getHeight();
		ArrayList <Point> points = new ArrayList<Point>();
		
		// first make sure this tent was not already linked
		for (int a = 0; a < validLinks.size(); ++a)
		{
			ExtraTreeTentLink e = (ExtraTreeTentLink)validLinks.get(a);
				
			if (e.pos1.equals(tent) || e.pos2.equals(tent))
			{
				return "the tent already has a link.";
			}
		}
		
		//Make sure the tree does not have any unknowns or unlinked tents around it
		
		// Check Left
		if (tent.x > 0)
			points.add(new Point(tent.x-1,tent.y));

		// Check Up
		if (tent.y > 0)
			points.add(new Point(tent.x,tent.y - 1));

		// Check Down
		if (tent.y + 1 < h)
			points.add(new Point(tent.x,tent.y + 1));
	
		// Check Right
		if (tent.x + 1 < w)
			points.add(new Point(tent.x+1,tent.y));

		for (int x = 0; x < points.size(); ++x)
		{
			Point p = points.get(x);
			int contents = state.getCellContents(p.x,p.y);
			
			if (p.equals(tree))
				continue; // this is the inference they're making
			
			//If we find an unlinked tree, this isn't valid
			if(contents == TreeTent.CELL_TREE)
			{
				//Check to see if this other tree has any previous links
				//If it doesn't, this isn't valid
				boolean found = false;
				for (int a = 0; a < validLinks.size(); ++a)
				{
					ExtraTreeTentLink e = (ExtraTreeTentLink)validLinks.get(a);
					if (e.pos1.equals(p) || e.pos2.equals(p))
					{
						found = true;
					}					
				}
				return (found) ? null : "the tent has more than one adjacent unlinked tree.";
			}
		}
		return null;
    }
    
    protected abstract String checkCellNeededLink(Point tree, Point tent, BoardState state, ArrayList<Object> validLinks);

    
	protected String checkRuleRaw(BoardState destBoardState)
    {
    	String error = null;
    	BoardState origBoardState = destBoardState.getSingleParentState();
    	
    	// Check for only one branch
		if (destBoardState.getTransitionsTo().size() != 1)
		{
			return "This rule only involves having a single branch!";
		}

		//Make sure all the cell contents are the same
		for (int y = 0; y < origBoardState.getHeight() && error == null; ++y)
		{
			for (int x = 0; x < origBoardState.getWidth(); ++x)
			{
				int origState = origBoardState.getCellContents(x,y);
				int newState = destBoardState.getCellContents(x,y);
				
				if (origState != newState)
				{
					return "You can only add links with this rule!";
				}
			}
		}
		
		// make sure they didn't remove any links
		ArrayList <Object> destExtra = destBoardState.getExtraData();
		ArrayList <Object> origExtra = origBoardState.getExtraData();
		
		if (!destExtra.containsAll(origExtra))
		{
			return "You may not remove links with this rule, only add them!";
		}
		
		//Make sure they added a link
		ArrayList <Object> dif = new ArrayList<Object>(destExtra);
		dif.removeAll(origExtra);
		
		if (dif.size() == 0)
		{
			return "You must add a link to use this rule!";
		}
		
		//For every link added we must check if it was valid
		for (int x = 0; x < dif.size(); ++x)
		{
			//One end must be the tree and the other must be the tent
			ExtraTreeTentLink e = (ExtraTreeTentLink)dif.get(x);
			Point tree = e.pos1;
			Point tent = e.pos2;
			
			int state = destBoardState.getCellContents(tree.x,tree.y);
			
			if (state == TreeTent.CELL_TENT)
			{ // swap them
				Point temp = tree;
				tree = tent;
				tent = temp;
			}
			
			if (destBoardState.getCellContents(tree.x,tree.y) != TreeTent.CELL_TREE)
			{
				error = "You can only link a tree to a tent with this rule!";
				break;
			}
			
			if (destBoardState.getCellContents(tent.x,tent.y) != TreeTent.CELL_TENT)
			{
				error = "You can only link a tree to a tent with this rule!";
				break;
			}
			
			//Check adjacency
			if(tent.x - tree.x == 1 || tent.x - tree.x == -1)
			{
				if(!(tent.y - tree.y == 0))
				{
					error = "Linked cells must be adjacent.";
				}
			}
			else if(tent.x - tree.x == 0)
			{
				if(!(tent.y - tree.y == 1 || tent.y - tree.y == -1))
				{
					error = "Linked cells must be adjacent.";
				}
			}
			else
			{
				error = "Linked cells must be adjacent.";
			}
				
			
			// check the validity
			String err_string = checkCellNeededLink(tree,tent,destBoardState,origExtra); 
			if(err_string != null)
			{
				String let = String.valueOf((char)(tent.y + (int)'A'));
				
				error = "Your link addition to the tent at " + 
					let + "" 
					+ (tent.x + 1)  + " is invalid because\n" + err_string;
				break;
			}
		}
		
		TreeTent.setAnnotations(destBoardState);
		
		return error;
    }
    
    /**
     * Apply the default application of this rule
     * @param state the board we're using
     * @param pm the puzzle module we're using
     * @return true iff we have applied a rule correctly
     */
    protected boolean doDefaultApplicationRaw(BoardState state)
    {
    	BoardState parent = state.getSingleParentState();
    	boolean changed = false;
    	
    	if (parent != null && state.getTransitionsTo().size() == 1)
    	{
	    	int w = state.getWidth();
	    	int h = state.getHeight();
	    	
	    	ArrayList <Object> extra = state.getExtraData();
	    	
	    	for (int y = 0; y < h; ++y)
	    	{
	    		for (int x = 0; x < w; ++x)
	    		{
	    			if(state.getCellContents(x, y) != TreeTent.CELL_TREE)
	    				continue;
	    			
	    			//Find all adjacent tents
	    			ArrayList<Point> points = new ArrayList<Point>();
	    			
	    			//Check if a link to the right would be valid
	    			if(x < w - 1)
	    			{
		    			if(state.getCellContents(x+1, y) == TreeTent.CELL_TENT)
		    			{
			    			points.add(new Point(x+1,y));
		    			}
	    			}
	    			
	    			//Check if a link to the left would be valid
	    			if(x > 0)
	    			{
		    			if(state.getCellContents(x-1, y) == TreeTent.CELL_TENT)
		    			{
			    			points.add(new Point(x-1,y));
		    			}
	    			}
	    			
	    			//Check if a link down would be valid
	    			if(y < h - 1)
	    			{
		    			if(state.getCellContents(x, y+1) == TreeTent.CELL_TENT)
		    			{
			    			points.add(new Point(x,y+1));
		    			}
	    			}
	    			
	    			//Check if a link up would be valid
	    			if(y > 0)
	    			{
		    			if(state.getCellContents(x, y-1) == TreeTent.CELL_TENT)
		    			{
			    			points.add(new Point(x,y-1));
		    			}
	    			}
	    			
	    			Point here = new Point(x,y);
	    			for(Point p : points)
	    			{
		    			if(checkCellNeededLink(here,p,state,extra) == null)
		    			{
		    				ExtraTreeTentLink e = new ExtraTreeTentLink(here,p);
	    					if (!extra.contains(e))
	    					{
	    						extra.add(e);
	    						
	    						boolean ok = checkRuleRaw(state) == null;
	    						
	    						if (!ok)
	    						{
	    							extra.remove(e);
	    						}
	    						else
	    						{
	    							changed = true;
	    						}
	    					}
		    			}
	    			}
	    		}
	    	}
    	}
	    	
	    return changed;
    }
}
