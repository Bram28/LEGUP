package edu.rpi.phil.legup.puzzles.lightup;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.ImageIcon;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.CaseRule;
import edu.rpi.phil.legup.PuzzleModule;
import edu.rpi.phil.legup.Permutations;

public class CaseSatisfyNumber extends CaseRule
{
	public CaseSatisfyNumber()
	{
		name = "Satisfy Number";
		description = "The different ways a blocks number can be satisfied.";
		image = new ImageIcon("images/lightup/cases/SatisfyNumber.png");
	}
	
	public String checkCaseRuleRaw(BoardState state)
	{
		String rv = null;
		
		if (state.getTransitionsFrom().size() < 2 || state.getTransitionsFrom().size() > 6 || state.getTransitionsFrom().size() == 5)
		{
			rv = "This case rule can only be applied on a split of 2,3,4 or 6.";
		}
		else if(state.getTransitionsFrom().size() == 2)
		{
			BoardState one = state.getTransitionsFrom().get(0);
			BoardState two = state.getTransitionsFrom().get(1);
			
			if(BoardState.getDifferenceLocations(state,one).size() != 2
					|| BoardState.getDifferenceLocations(state,two).size() != 2)
			{
				return "A 2 way split of this type must have exactly 2 cells changed from the parent.";
			}
			ArrayList<Point> dif = BoardState.getDifferenceLocations(one,two);
			if(dif.size() != 2)
			{
				return "A 2 way split of this type must have exactly 2 cells changed from the parent.";
			}
			Point cell1 = dif.get(0);
			Point cell2 = dif.get(1);
			Point block = findBlock(cell1, cell2, state);
			
			if(block == null)
			{
				return "Changes must be made around a single block.";
			}
			
			if(state.getCellContents(cell1.x, cell1.y) != LightUp.CELL_UNKNOWN || state.getCellContents(cell2.x, cell2.y) != LightUp.CELL_UNKNOWN )
			{
				return "Changes can only be made to an unknown cell.";
			}
			
			if(one.getCellContents(cell1.x, cell1.y) == one.getCellContents(cell2.x, cell2.y))
			{
				return "A 2 way split of this type must have 1 lightbulb and 1 blank cell in each child state.";
			}
			return null;
		}
		else if(state.getTransitionsFrom().size() == 3)
		{
			BoardState one = state.getTransitionsFrom().get(0);
			BoardState two = state.getTransitionsFrom().get(1);
			BoardState three = state.getTransitionsFrom().get(2);
			
			if(BoardState.getDifferenceLocations(state,one).size() != 3
					|| BoardState.getDifferenceLocations(state,two).size() != 3
					|| BoardState.getDifferenceLocations(state,three).size() != 3)
			{
				return "A 3 way split of this type must have exactly 3 cells changed from the parent.";
			}

			ArrayList<Point> dif = BoardState.getDifferenceLocations(one,two);
			if(dif.size() != 2)
			{
				return "A 2 way split of this type must have exactly 2 cells changed from the parent.";
			}
			
			ArrayList<Point> dif2 = BoardState.getDifferenceLocations(one,three);
			if(dif2.size() != 2)
			{
				return "A 2 way split of this type must have exactly 2 cells changed from the parent.";
			}
			if(!dif.contains(dif2.get(0)))
				dif.add(dif2.get(0));
			if(!dif.contains(dif2.get(1)))
				dif.add(dif2.get(1));
			
			dif2 = BoardState.getDifferenceLocations(one,three);
			if(dif2.size() != 2)
			{
				return "A 2 way split of this type must have exactly 2 cells changed from the parent.";
			}
			if(!dif.contains(dif2.get(0)))
				dif.add(dif2.get(0));
			if(!dif.contains(dif2.get(1)))
				dif.add(dif2.get(1));
			
			if(dif.size() != 3)
			{
				return "A 2 way split of this type must have exactly 2 cells changed from the parent.";
			}
			
			Point cell1 = dif.get(0);
			Point cell2 = dif.get(1);
			Point cell3 = dif.get(2);
			Point block = findBlock(cell1, cell2, cell3);
			
			if(block == null)
			{
				return "Changes must be made around a single block.";
			}
			
			if(state.getCellContents(cell1.x, cell1.y) != LightUp.CELL_UNKNOWN || state.getCellContents(cell2.x, cell2.y) != LightUp.CELL_UNKNOWN || state.getCellContents(cell3.x, cell3.y) != LightUp.CELL_UNKNOWN)
			{
				return "Changes can only be made to an unknown cell.";
			}
			
			int bulbs = checkBlock(block, state);
			int temp = 0;
			if(bulbs == -1)
			{
				return "Each child state in a 3 way split must be unique.";
			}
			else if(bulbs == 1)
			{
				temp = 0;
				if(one.getCellContents(cell1.x,cell1.y) == LightUp.CELL_LIGHT)
					++temp;
				if(one.getCellContents(cell2.x,cell2.y) == LightUp.CELL_LIGHT)
					++temp;
				if(one.getCellContents(cell3.x,cell3.y) == LightUp.CELL_LIGHT)
					++temp;
				if(temp != 1)
					return "This block requires only one additional bulb in each branch.";
				
				temp = 0;
				if(two.getCellContents(cell1.x,cell1.y) == LightUp.CELL_LIGHT)
					++temp;
				if(two.getCellContents(cell2.x,cell2.y) == LightUp.CELL_LIGHT)
					++temp;
				if(two.getCellContents(cell3.x,cell3.y) == LightUp.CELL_LIGHT)
					++temp;
				if(temp != 1)
					return "This block requires only one additional bulb in each branch.";
				
				temp = 0;
				if(three.getCellContents(cell1.x,cell1.y) == LightUp.CELL_LIGHT)
					++temp;
				if(three.getCellContents(cell2.x,cell2.y) == LightUp.CELL_LIGHT)
					++temp;
				if(three.getCellContents(cell3.x,cell3.y) == LightUp.CELL_LIGHT)
					++temp;
				if(temp != 1)
					return "This block requires only one additional bulb in each branch.";
			}
			else if(bulbs == 2)
			{
				temp = 0;
				if(one.getCellContents(cell1.x,cell1.y) == LightUp.CELL_LIGHT)
					++temp;
				if(one.getCellContents(cell2.x,cell2.y) == LightUp.CELL_LIGHT)
					++temp;
				if(one.getCellContents(cell3.x,cell3.y) == LightUp.CELL_LIGHT)
					++temp;
				if(temp != 2)
					return "This block requires two additional bulbs in each branch.";
				
				temp = 0;
				if(two.getCellContents(cell1.x,cell1.y) == LightUp.CELL_LIGHT)
					++temp;
				if(two.getCellContents(cell2.x,cell2.y) == LightUp.CELL_LIGHT)
					++temp;
				if(two.getCellContents(cell3.x,cell3.y) == LightUp.CELL_LIGHT)
					++temp;
				if(temp != 2)
					return "This block requires two additional bulbs in each branch.";
				
				temp = 0;
				if(three.getCellContents(cell1.x,cell1.y) == LightUp.CELL_LIGHT)
					++temp;
				if(three.getCellContents(cell2.x,cell2.y) == LightUp.CELL_LIGHT)
					++temp;
				if(three.getCellContents(cell3.x,cell3.y) == LightUp.CELL_LIGHT)
					++temp;
				if(temp != 2)
					return "This block requires two additional bulbs in each branch.";
			}
			
			return null;
		}
		else if(state.getTransitionsFrom().size() == 4)
		{
			BoardState one = state.getTransitionsFrom().get(0);
			BoardState two = state.getTransitionsFrom().get(1);
			BoardState three = state.getTransitionsFrom().get(2);
			BoardState four = state.getTransitionsFrom().get(3);
			
			if(BoardState.getDifferenceLocations(state,one).size() != 4
					|| BoardState.getDifferenceLocations(state,two).size() != 4
					|| BoardState.getDifferenceLocations(state,three).size() != 4
					|| BoardState.getDifferenceLocations(state,four).size() != 4)
			{
				return "A 4 way split of this type must have exactly 4 cells changed from the parent.";
			}
		}
		else if(state.getTransitionsFrom().size() == 6)
		{
			BoardState one = state.getTransitionsFrom().get(0);
			BoardState two = state.getTransitionsFrom().get(1);
			BoardState three = state.getTransitionsFrom().get(2);
			BoardState four = state.getTransitionsFrom().get(3);
			BoardState five = state.getTransitionsFrom().get(4);
			BoardState six = state.getTransitionsFrom().get(5);
			
			if(BoardState.getDifferenceLocations(state,one).size() != 4
					|| BoardState.getDifferenceLocations(state,two).size() != 4
					|| BoardState.getDifferenceLocations(state,three).size() != 4
					|| BoardState.getDifferenceLocations(state,four).size() != 4
					|| BoardState.getDifferenceLocations(state,five).size() != 4
					|| BoardState.getDifferenceLocations(state,six).size() != 4)
			{
				return "A 6 way split of this type must have exactly 4 cells changed from the parent.";
			}
		}
		else
		{
			return "Error in applying case rule.";		
		}
			
		return rv;
	}
	
	Point findBlock(Point cell1, Point cell2, BoardState state)
	{
		if(cell1.x == cell2.x)
		{
			if(cell1.y + 2 == cell2.y)
			{
				return new Point(cell1.x, cell1.y+1);
			}
			else if(cell1.y - 2 == cell2.y)
			{
				return new Point(cell1.x, cell1.y-1);
			}
			else
				return null;
		}
		else if(cell1.y == cell2.y)
		{
			if(cell1.x + 2 == cell2.x)
			{
				return new Point(cell1.x + 1, cell1.y);
			}
			else if(cell1.x - 2 == cell2.x)
			{
				return new Point(cell1.x - 1, cell1.y);
			}
			else
				return null;
		}
		else if(cell1.x + 1 == cell2.x)
		{
			if(cell1.y + 1 == cell2.y)
			{
				return lookUpBlock(cell1,cell2, new Point(cell1.x + 1, cell1.y),  new Point(cell1.x, cell1.y+1), state);
			}
			else if(cell1.y - 1 == cell2.y)
			{
				return lookUpBlock(cell1,cell2, new Point(cell1.x + 1, cell1.y),  new Point(cell1.x, cell1.y-1), state);
			}
			else
				return null;
		}
		else if(cell1.x - 1 == cell2.x)
		{
			if(cell1.y + 1 == cell2.y)
			{
				return lookUpBlock(cell1,cell2, new Point(cell1.x - 1, cell1.y),  new Point(cell1.x, cell1.y+1), state);
			}
			else if(cell1.y - 1 == cell2.y)
			{
				return lookUpBlock(cell1,cell2, new Point(cell1.x - 1, cell1.y),  new Point(cell1.x, cell1.y-1), state);
			}
			else
				return null;
		}
		else
			return null;
	}

	Point findBlock(Point cell1, Point cell2, Point cell3)
	{
		if(cell1.x == cell2.x)
		{
			if(cell1.y + 2 == cell2.y)
			{
				if(cell1.y + 1 == cell3.y)
				{
					if(cell1.x - 1 == cell3.x || cell1.x + 1 == cell3.x)
						return new Point(cell1.x, cell1.y + 1);
					else
						return null;
				}
				else return null;
			}
			else if(cell1.y - 2 == cell2.y)
			{
				if(cell1.y - 1 == cell3.y)
				{
					if(cell1.x - 1 == cell3.x || cell1.x + 1 == cell3.x)
						return new Point(cell1.x, cell1.y - 1);
					else
						return null;
				}
				else return null;
			}
			else
				return null;
		}
		else if(cell1.x == cell3.x)
		{
			if(cell1.y + 2 == cell3.y)
			{
				if(cell1.y + 1 == cell2.y)
				{
					if(cell1.x - 1 == cell2.x || cell1.x + 1 == cell2.x)
						return new Point(cell1.x, cell1.y + 1);
					else
						return null;
				}
				else return null;
			}
			else if(cell1.y - 2 == cell3.y)
			{
				if(cell1.y - 1 == cell2.y)
				{
					if(cell1.x - 1 == cell2.x || cell1.x + 1 == cell2.x)
						return new Point(cell1.x, cell1.y - 1);
					else
						return null;
				}
				else return null;
			}
			else
				return null;
		}
		else if(cell3.x == cell2.x)
		{
			if(cell3.y + 2 == cell2.y)
			{
				if(cell3.y + 1 == cell1.y)
				{
					if(cell3.x - 1 == cell1.x || cell3.x + 1 == cell1.x)
						return new Point(cell3.x, cell3.y + 1);
					else
						return null;
				}
				else return null;
			}
			else if(cell3.y - 2 == cell2.y)
			{
				if(cell3.y - 1 == cell1.y)
				{
					if(cell3.x - 1 == cell1.x || cell3.x + 1 == cell1.x)
						return new Point(cell3.x, cell3.y - 1);
					else
						return null;
				}
				else return null;
			}
			else
				return null;
		}
		
		else if(cell1.y == cell2.y)
		{
			if(cell1.x + 2 == cell2.x)
			{
				if(cell1.x + 1 == cell3.x)
				{
					if(cell1.y - 1 == cell3.y || cell1.y + 1 == cell3.y)
						return new Point(cell1.x + 1, cell1.y );
					else
						return null;
				}
				else return null;
			}
			else if(cell1.x - 2 == cell2.x)
			{
				if(cell1.x - 1 == cell3.x)
				{
					if(cell1.y - 1 == cell3.y || cell1.y + 1 == cell3.y)
						return new Point(cell1.x - 1, cell1.y);
					else
						return null;
				}
				else return null;
			}
			else
				return null;
		}
		else if(cell1.y == cell3.y)
		{
			if(cell1.x + 2 == cell3.x)
			{
				if(cell1.x + 1 == cell2.x)
				{
					if(cell1.y - 1 == cell2.y || cell1.y + 1 == cell2.y)
						return new Point(cell1.x + 1, cell1.y );
					else
						return null;
				}
				else return null;
			}
			else if(cell1.x - 2 == cell3.x)
			{
				if(cell1.x - 1 == cell2.x)
				{
					if(cell1.y - 1 == cell2.y || cell1.y + 1 == cell2.y)
						return new Point(cell1.x - 1, cell1.y);
					else
						return null;
				}
				else return null;
			}
			else
				return null;
		}
		else if(cell3.y == cell2.y)
		{
			if(cell3.x + 2 == cell2.x)
			{
				if(cell3.x + 1 == cell1.x)
				{
					if(cell3.y - 1 == cell1.y || cell3.y + 1 == cell1.y)
						return new Point(cell3.x + 1, cell3.y );
					else
						return null;
				}
				else return null;
			}
			else if(cell3.x - 2 == cell2.x)
			{
				if(cell3.x - 1 == cell1.x)
				{
					if(cell3.y - 1 == cell1.y || cell3.y + 1 == cell1.y)
						return new Point(cell3.x - 1, cell3.y);
					else
						return null;
				}
				else return null;
			}
			else
				return null;
		}
		
		else
			return null;
	}
	
	Point findBlock(Point cell1, Point cell2, Point cell3, Point cell4)
	{
		int minx = Math.min(Math.min(cell1.x, cell2.x),Math.min(cell3.x, cell4.x));
		int maxx = Math.max(Math.max(cell1.x, cell2.x),Math.max(cell3.x, cell4.x));
		
		int miny = Math.min(Math.min(cell1.y, cell2.y),Math.min(cell3.x, cell4.y));
		int maxy = Math.max(Math.max(cell1.y, cell2.y),Math.max(cell3.x, cell4.y));
		
		int countminx = 0;
		int countmaxx = 0;
		int countminy = 0;
		int countmaxy = 0;
		
		if(minx + 2 == maxx)
		{
			if(miny + 2 == maxy)
			{
				if(cell1.x == minx)
					++countminx;
				if(cell1.x == maxx)
					++countmaxx;
				if(cell1.y == miny)
					++countminx;
				if(cell1.y == maxy)
					++countmaxx;
				
				if(cell2.x == minx)
					++countminx;
				if(cell2.x == maxx)
					++countmaxx;
				if(cell2.y == miny)
					++countminx;
				if(cell2.y == maxy)
					++countmaxx;
				
				if(cell3.x == minx)
					++countminx;
				if(cell3.x == maxx)
					++countmaxx;
				if(cell3.y == miny)
					++countminx;
				if(cell3.y == maxy)
					++countmaxx;
				
				if(cell4.x == minx)
					++countminx;
				if(cell4.x == maxx)
					++countmaxx;
				if(cell4.y == miny)
					++countminx;
				if(cell4.y == maxy)
					++countmaxx;
				
				if(countminx == 1 && countmaxx == 1 && countminy == 1 && countmaxy == 1)
				{
					return new Point(minx + 1, miny + 1);
				}
				else
					return null;
			}
			else
				return null;
		}
		else
			return null;
	}
	
	Point lookUpBlock( Point cell1, Point cell2, Point test1, Point test2, BoardState state)
	{
		if(state.getCellContents(test1.x,test1.y) > 10 && state.getCellContents(test1.x,test1.y) < 14)
		{
			int cellValue = state.getCellContents(test1.x, test1.y);
			cellValue -= 10;
			int blanks = 0;
			int bulbs = 0;
			
			if(test1.x + 1 < state.getWidth())
			{
				if(state.getCellContents(test1.x + 1, test1.y) > 1)
					++blanks;
				else if(state.getCellContents(test1.x + 1, test1.y) == 1)
					++bulbs;
			}
			else
				++blanks;
			
			if(test1.x - 1 > 0)
			{
				if(state.getCellContents(test1.x - 1, test1.y) > 1)
					++blanks;
				else if(state.getCellContents(test1.x - 1, test1.y) == 1)
					++bulbs;
			}
			else
				++blanks;
			
			if(test1.y + 1 < state.getHeight())
			{
				if(state.getCellContents(test1.x, test1.y + 1) > 1)
					++blanks;
				else if(state.getCellContents(test1.x, test1.y + 1) == 1)
					++bulbs;
			}
			else
				++blanks;
			
			if(test1.y - 1 > 0)
			{
				if(state.getCellContents(test1.x, test1.y - 1) > 1)
					++blanks;
				else if(state.getCellContents(test1.x, test1.y - 1) == 1)
					++bulbs;
			}
			else
				++blanks;
			
			if(cellValue - bulbs == 1)
			{
				if(bulbs + blanks == 2)
				{
					return test1;
				}
			}
		
		}
		if(state.getCellContents(test2.x,test2.y) > 10 && state.getCellContents(test2.x,test2.y) < 14)
		{
			int cellValue = state.getCellContents(test2.x, test2.y);
			cellValue -= 10;
			int blanks = 0;
			int bulbs = 0;
			
			if(test2.x + 1 < state.getWidth())
			{
				if(state.getCellContents(test2.x + 1, test2.y) > 1)
					++blanks;
				else if(state.getCellContents(test2.x + 1, test2.y) == 1)
					++bulbs;
			}
			else
				++blanks;
			
			if(test2.x - 1 > 0)
			{
				if(state.getCellContents(test2.x - 1, test2.y) > 1)
					++blanks;
				else if(state.getCellContents(test2.x - 1, test2.y) == 1)
					++bulbs;
			}
			else
				++blanks;
			
			if(test2.y + 1 < state.getHeight())
			{
				if(state.getCellContents(test2.x, test2.y + 1) > 1)
					++blanks;
				else if(state.getCellContents(test2.x, test2.y + 1) == 1)
					++bulbs;
			}
			else
				++blanks;
			
			if(test2.y - 1 > 0)
			{
				if(state.getCellContents(test2.x, test2.y - 1) > 1)
					++blanks;
				else if(state.getCellContents(test2.x, test2.y - 1) == 1)
					++bulbs;
			}
			else
				++blanks;
			
			if(cellValue - bulbs == 1)
			{
				if(bulbs + blanks == 2)
				{
					return test2;
				}
			}
		}
		return null;
	}

	int checkBlock(Point block, BoardState state)
	{
		if(state.getCellContents(block.x,block.y) > 10 && state.getCellContents(block.x,block.y) < 14)
		{
			int cellValue = state.getCellContents(block.x, block.y);
			cellValue -= 10;
			int blanks = 0;
			int bulbs = 0;
			
			if(block.x + 1 < state.getWidth())
			{
				if(state.getCellContents(block.x + 1, block.y) > 1)
					++blanks;
				else if(state.getCellContents(block.x + 1, block.y) == 1)
					++bulbs;
			}
			else
				++blanks;
			
			if(block.x - 1 > 0)
			{
				if(state.getCellContents(block.x - 1, block.y) > 1)
					++blanks;
				else if(state.getCellContents(block.x - 1, block.y) == 1)
					++bulbs;
			}
			else
				++blanks;
			
			if(block.y + 1 < state.getHeight())
			{
				if(state.getCellContents(block.x, block.y + 1) > 1)
					++blanks;
				else if(state.getCellContents(block.x, block.y + 1) == 1)
					++bulbs;
			}
			else
				++blanks;
			
			if(block.y - 1 > 0)
			{
				if(state.getCellContents(block.x, block.y - 1) > 1)
					++blanks;
				else if(state.getCellContents(block.x, block.y - 1) == 1)
					++bulbs;
			}
			else
				++blanks;
			
			if(cellValue - bulbs == 1 || cellValue - bulbs == 2)
			{
				if(bulbs + blanks == 1)
				{
					return cellValue - bulbs;
				}
			}
		}
		return -1;
	}

	public boolean startDefaultApplicationRaw(BoardState state)
	{
		return true;
	}
	
	public boolean doDefaultApplicationRaw(BoardState state, PuzzleModule pm ,Point location)
	{
		Vector<Point> cells = new Vector<Point>();
		cells.add( new Point(0,0));
		cells.add(new Point(0,1));
		Vector<Integer> states = new Vector<Integer>();
		states.add( null );
		states.add( 2 );
		Vector<Integer> statecounts = new Vector<Integer>();
		statecounts.add( 1 );
		statecounts.add( 9 );
		Permutations.permutationRow( state, 1, states, statecounts );
		return true;
	}
}
