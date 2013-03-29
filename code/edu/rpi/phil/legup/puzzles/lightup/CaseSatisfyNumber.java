package edu.rpi.phil.legup.puzzles.lightup;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.ImageIcon;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.CaseRule;
import edu.rpi.phil.legup.PuzzleModule;
import edu.rpi.phil.legup.Permutations;
import edu.rpi.phil.legup.puzzles.treetent.CaseLinkTree;

public class CaseSatisfyNumber extends CaseRule
{
	public CaseSatisfyNumber()
	{
		setName("Satisfy Number");
		description = "The different ways a blocks number can be satisfied.";
		image = new ImageIcon("images/lightup/cases/SatisfyNumber.png");
	}
	
	public String checkCaseRuleRaw(BoardState state)
	{
		String rv = null;
		BoardState parent = state.getSingleParentState();
		if(parent.getTransitionsFrom().size() > 4)
		{
			rv = "Only the blank tiles adjacent to a single block are to be modified\nin one step using this rule.";
		}
		else
		{
			int num_children = parent.getTransitionsFrom().size();
			Point p = findCommonBlock(parent,state);
			int block_value = (p != null)?getBlockValue(parent.getCellContents(p.x,p.y)):-2;
			int num_adj_blanks = CaseLinkTree.calcAdjacentTiles(parent,p,LightUp.CELL_UNKNOWN);
			int num_adj_lights = CaseLinkTree.calcAdjacentTiles(parent,p,LightUp.CELL_LIGHT);
			int num_intended_branches = Permutations.combination(num_adj_blanks-num_adj_lights,block_value);
			if(block_value == -1) //this will never execute since findCommonBlock only returns blocks. Replace with findCommonTile and this should work
			{
				rv = "The cell whose adjacents are modified is not a numbered block.";
			}
			else if(p == null)
			{
				rv = "All the cells modified should be adjacent to a single numbered block.";
			}
			else if(num_intended_branches != num_children)
			{
				rv = "There are not the same amount of branches as required to have\nall combinations of lights adjacent to a single block.";
			}
			if(rv != "")return rv; //ensures that the conditions checked above are not overwritten
			Vector<Point> lights = new Vector<Point>(); //location of light in each branch
			for(BoardState b : parent.getTransitionsFrom())
			{
				if(CaseLinkTree.calcAdjacentTiles(b,p,LightUp.CELL_UNKNOWN) != 0)
				{
					rv = "All tiles adjacent to the block must be filled, which\nis not the case for branch "+(parent.getTransitionsFrom().indexOf(b)+1);
					break;
				}
				ArrayList<Point> dif = BoardState.getDifferenceLocations(b,parent);
				if(dif.size() != num_adj_blanks)
				{
					rv = "Only cells adjacent to the block should be modified,\nwhich is not the case for branch "+(parent.getTransitionsFrom().indexOf(b)+1);
					break;
				}
				int num_lights_added = 0;
				for(Point p2 : dif)
				{
					if(b.getCellContents(p2.x,p2.y) == LightUp.CELL_LIGHT)num_lights_added++;
				}
				if(num_lights_added != block_value)
				{
					rv = "Branch "+(parent.getTransitionsFrom().indexOf(b)+1)+" does not have the correct amount of lights.";
					break;
				}
				for(BoardState b2 : parent.getTransitionsFrom()) //check sibling equivalence
				{
					if(b==b2)continue;
					if(BoardState.getDifferenceLocations(b,b2).size()==0)
					{
						rv = "Branch "+(parent.getTransitionsFrom().indexOf(b)+1)+" is the same as branch "+(parent.getTransitionsFrom().indexOf(b2)+1)+".";
					}
				}
			}
		}
		
		/*if (parent.getTransitionsFrom().size() < 2 || parent.getTransitionsFrom().size() > 6 || parent.getTransitionsFrom().size() == 5)
		{
			rv = "This case rule can only be applied on a split of 2,3,4 or 6.";
		}
		else if(parent.getTransitionsFrom().size() == 2)
		{
			BoardState one = parent.getTransitionsFrom().get(0);
			BoardState two = parent.getTransitionsFrom().get(1);
			
			if(BoardState.getDifferenceLocations(parent,one).size() != 2
					|| BoardState.getDifferenceLocations(parent,two).size() != 2)
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
			Point block = findBlock(cell1, cell2, parent);
			
			if(block == null)
			{
				return "Changes must be made around a single block.";
			}
			
			if(parent.getCellContents(cell1.x, cell1.y) != LightUp.CELL_UNKNOWN || parent.getCellContents(cell2.x, cell2.y) != LightUp.CELL_UNKNOWN )
			{
				return "Changes can only be made to an unknown cell.";
			}
			
			if(one.getCellContents(cell1.x, cell1.y) == one.getCellContents(cell2.x, cell2.y))
			{
				return "A 2 way split of this type must have 1 lightbulb and 1 blank cell in each child state.";
			}
			return null;
		}
		else if(state.getTransitionsFrom().size() == 3) //this looks only partially implemented, messages involving 2
		{
			BoardState one = parent.getTransitionsFrom().get(0);
			BoardState two = parent.getTransitionsFrom().get(1);
			BoardState three = parent.getTransitionsFrom().get(2);
			
			if(BoardState.getDifferenceLocations(parent,one).size() != 3
					|| BoardState.getDifferenceLocations(parent,two).size() != 3
					|| BoardState.getDifferenceLocations(parent,three).size() != 3)
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
			
			if(parent.getCellContents(cell1.x, cell1.y) != LightUp.CELL_UNKNOWN || parent.getCellContents(cell2.x, cell2.y) != LightUp.CELL_UNKNOWN || parent.getCellContents(cell3.x, cell3.y) != LightUp.CELL_UNKNOWN)
			{
				return "Changes can only be made to an unknown cell.";
			}
			
			int bulbs = checkBlock(block, parent);
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
		else if(parent.getTransitionsFrom().size() == 4)
		{
			BoardState one = parent.getTransitionsFrom().get(0);
			BoardState two = parent.getTransitionsFrom().get(1);
			BoardState three = parent.getTransitionsFrom().get(2);
			BoardState four = parent.getTransitionsFrom().get(3);
			
			if(BoardState.getDifferenceLocations(state,one).size() != 4
					|| BoardState.getDifferenceLocations(state,two).size() != 4
					|| BoardState.getDifferenceLocations(state,three).size() != 4
					|| BoardState.getDifferenceLocations(state,four).size() != 4)
			{
				return "A 4 way split of this type must have exactly 4 cells changed from the parent.";
			}
		}
		else if(parent.getTransitionsFrom().size() == 6)
		{
			BoardState one = parent.getTransitionsFrom().get(0);
			BoardState two = parent.getTransitionsFrom().get(1);
			BoardState three = parent.getTransitionsFrom().get(2);
			BoardState four = parent.getTransitionsFrom().get(3);
			BoardState five = parent.getTransitionsFrom().get(4);
			BoardState six = parent.getTransitionsFrom().get(5);
			
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
		}*/
			
		return rv;
	}
	
	static Point findCommonBlock(BoardState parent,BoardState state)
	{
		ArrayList<Point> dif = BoardState.getDifferenceLocations(parent,state);
		if(dif.size() == 2)return findBlock(dif.get(0),dif.get(1),state);
		else if(dif.size() == 3)return findBlock(dif.get(0),dif.get(1),dif.get(2));
		else if(dif.size() == 4)return findBlock(dif.get(0),dif.get(1),dif.get(2),dif.get(3));
		else return null;
	}
	
	static Point findBlock(Point cell1, Point cell2, BoardState state)
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

	static Point findBlock(Point cell1, Point cell2, Point cell3)
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
	
	static Point findBlock(Point cell1, Point cell2, Point cell3, Point cell4)
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
	
	static Point lookUpBlock( Point cell1, Point cell2, Point test1, Point test2, BoardState state)
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
	
	int getBlockValue(int cell)
	{
		if(cell == LightUp.CELL_BLOCK0)return 0;
		else if(cell == LightUp.CELL_BLOCK1)return 1;
		else if(cell == LightUp.CELL_BLOCK2)return 2;
		else if(cell == LightUp.CELL_BLOCK3)return 3;
		else if(cell == LightUp.CELL_BLOCK4)return 4;
		else return -1;
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
