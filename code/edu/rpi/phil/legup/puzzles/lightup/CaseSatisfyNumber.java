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
import edu.rpi.phil.legup.newgui.CaseRuleSelectionHelper;

public class CaseSatisfyNumber extends CaseRule
{
	public int crshMode(){return CaseRuleSelectionHelper.MODE_TILETYPE;}
	public Vector<Integer> crshTileType()
	{
		Vector<Integer> retval = new Vector<Integer>();
		retval.add(LightUp.CELL_BLOCK0);
		retval.add(LightUp.CELL_BLOCK1);
		retval.add(LightUp.CELL_BLOCK2);
		retval.add(LightUp.CELL_BLOCK3);
		retval.add(LightUp.CELL_BLOCK4);
		return retval;
	}
	
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
		{
			int num_children = parent.getTransitionsFrom().size();
			Vector<Point> points = findCommonTile(parent,state,crshTileType());
			Point p = (points.size()==1)?points.get(0):null;
			int block_value = (p != null)?getBlockValue(parent.getCellContents(p.x,p.y)):-2;
			int num_adj_blanks = CaseLinkTree.calcAdjacentTiles(parent,p,LightUp.CELL_UNKNOWN);
			int num_adj_lights = CaseLinkTree.calcAdjacentTiles(parent,p,LightUp.CELL_LIGHT);
			int num_intended_branches = Permutations.combination(num_adj_blanks,block_value-num_adj_lights);
			if(num_children == 1)
			{
				rv = "Use a basic rule instead of a case rule when\nonly one case can be created.";
			}
			else if(p == null)
			{
				rv = "All the cells modified should be adjacent to a single numbered block.";
			}
			else if(num_intended_branches != num_children)
			{
				rv = "There are not the same amount of branches as required to have\nall combinations of lights adjacent to a single block.";
			}
			if(rv != null)return rv; //ensures that the conditions checked above are not overwritten
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
				if(CaseLinkTree.calcAdjacentTiles(b,p,LightUp.CELL_LIGHT) != block_value)
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
		return rv;
	}
	
	//returns the tiles that are adjacent to all changed tiles between parent and state
	//if types is null, all tiles are returned, if not, only tiles whitelisted in types are counted
	static Vector<Point> findCommonTile(BoardState parent,BoardState state,Vector<Integer> types)
	{
		ArrayList<Point> dif = BoardState.getDifferenceLocations(parent,state);
		Vector<Point> ret = new Vector<Point>();
		Vector<Integer> adjacents = new Vector<Integer>();
		if(dif.size() >= 2)
		{
			for(int x=0;x<parent.getHeight();x++)
			{
				for(int y=0;y<parent.getWidth();y++)
				{
					boolean is_common_point = false;
					int num_adjacents = 0;
					int tmp_x;// = x;
					int tmp_y;// = y;
					Point tmp_p = null;
					for(int dir=0;dir<4;dir++)
					{
						tmp_x = (dir<2)?((dir%2==0)?(x-1):(x+1)):x; //these two lines enumerate all orthagonal
						tmp_y = (dir<2)?y:((dir%2==0)?(y-1):(y+1)); //directions 1 unit from (x,y)
						if(tmp_x < 0 || tmp_x >= parent.getWidth())continue;
						if(tmp_y < 0 || tmp_y >= parent.getHeight())continue;
						if(parent.getCellContents(tmp_x,tmp_y) != PuzzleModule.CELL_UNKNOWN)continue;
						tmp_p = new Point(tmp_x,tmp_y);
						//System.out.println("At ("+tmp_x+","+tmp_y+") from ("+x+","+y+"), dir == "+dir);
						if(dif.contains(tmp_p))
						{
							is_common_point = true;
							num_adjacents++;
						}
					}
					if(is_common_point)
					{
						tmp_p = new Point(x,y);
						if(!ret.contains(tmp_p))
						{
							if(types == null || types.contains(parent.getCellContents(x,y)))
							{
								ret.add(tmp_p);
								adjacents.add(num_adjacents);
								//System.out.println(tmp_p+" has "+num_adjacents+" adjacents.");
							}
						}
					}
				}
			}
		}
		int max_adjs = 0;
		//assumes ret and adjacent are the same size
		//removes any element with less adjacent difs than the maximum
		for(int c1=0;c1<ret.size();c1++)
		{
			if(adjacents.get(c1) > max_adjs)max_adjs = adjacents.get(c1);
		}
		for(int c1=0;c1<ret.size();c1++)
		{
			if(adjacents.get(c1) < max_adjs)
			{
				ret.remove(c1);
				adjacents.remove(c1);
				c1--;
			}
		}
		return ret;
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
	
	public static int getBlockValue(int cell)
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
