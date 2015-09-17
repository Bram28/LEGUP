package edu.rpi.phil.legup.puzzles.lightup;

import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Vector;

import javax.swing.ImageIcon;

import edu.rpi.phil.legup.Contradiction;
import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.CaseRule;
import edu.rpi.phil.legup.CellPredicate;
import edu.rpi.phil.legup.Legup;
import edu.rpi.phil.legup.Permutations;
import edu.rpi.phil.legup.PuzzleModule;
import edu.rpi.phil.legup.newgui.CaseRuleSelectionHelper;
import edu.rpi.phil.legup.puzzles.treetent.CaseLinkTree;

public class CaseSatisfyNumber extends CaseRule
{
	static final long serialVersionUID = 5238481899970588295L;

	// CaseRuleSelectionHelper Methods to highlight cells with a number in them
	public CaseRuleSelectionHelper getSelectionHelper()
  {
  	return new CaseRuleSelectionHelper(CellPredicate.typeWhitelist(getTileTypes()));
	}
  private Set<Integer> tileTypes = null;
  public Set<Integer> getTileTypes()
  {
    if(tileTypes == null)
    {
      tileTypes = new LinkedHashSet<Integer>();
      tileTypes.add(LightUp.CELL_BLOCK0);
      tileTypes.add(LightUp.CELL_BLOCK1);
      tileTypes.add(LightUp.CELL_BLOCK2);
      tileTypes.add(LightUp.CELL_BLOCK3);
      tileTypes.add(LightUp.CELL_BLOCK4);
    }
    return tileTypes;
  }

	// AutoGenerateCases Method will generate every possible case that does not directly lead to a contradiction
	public BoardState autoGenerateCases(BoardState cur, Point pointSelected)
	{
		PuzzleModule pm = Legup.getInstance().getPuzzleModule();
		int num_blanks = CaseLinkTree.calcAdjacentTiles(cur,pointSelected,LightUp.CELL_UNKNOWN);
		int num_lights = CaseLinkTree.calcAdjacentTiles(cur,pointSelected,LightUp.CELL_LIGHT);
		int num_lights_needed = CaseSatisfyNumber.getBlockValue(cur.getCellContents(pointSelected.x,pointSelected.y))-num_lights;
		int num_empties = num_blanks - num_lights_needed;
		int[] whatgoesintheblanks = new int[num_blanks];
		for(int c1=0;c1<num_blanks;c1++)
		{
			whatgoesintheblanks[c1] = 0;
		}

		// Used to remove any cases which have a bulb in light
		Contradiction contra = new ContradictionBulbsInPath();

		while(Permutations.nextPermutation(whatgoesintheblanks,num_empties))
		{
			BoardState tmp = cur.copy();
			int counter = 0;
			Vector<Point> pointsChanged = new Vector<Point>();
			for(int c3=0;c3<4;c3++)
			{
				int x = pointSelected.x + ((c3<2) ? ((c3%2 == 0)?-1:1) : 0);
				int y = pointSelected.y + ((c3<2) ? 0 : ((c3%2 == 0)?-1:1));
				if(x < 0 || x >= cur.getWidth() || y < 0 || y >= cur.getHeight())continue;
				if(cur.getCellContents(x,y) != LightUp.CELL_UNKNOWN)continue;
				tmp.setCellContents(x,y,pm.getStateNumber(pm.getStateName(whatgoesintheblanks[counter])));
				if(pm.getStateNumber(pm.getStateName(whatgoesintheblanks[counter])) == LightUp.CELL_LIGHT)
					pointsChanged.add(new Point(x, y));
				++counter;
			}

			if (contra.checkContradictionRaw(tmp) == null) continue; // Do not add case if light is in already lit area

			tmp = cur.addTransitionFrom();
			tmp.setCaseSplitJustification(this);
			for (Point p : pointsChanged) {
				tmp.setCellContents(p.x, p.y, LightUp.CELL_LIGHT);
			}
			tmp.endTransition();
		}
		return Legup.getCurrentState();
	}

	public String getImageName() {return "images/lightup/cases/SatisfyNumber.png";}
	public CaseSatisfyNumber()
	{
		setName("Satisfy Number");
		description = "The different ways a blocks number can be satisfied.";
	}

	public String checkCaseRuleRaw(BoardState state)
	{
		/* Uncomment to make a case rule application with a single case an error */ 
		// BoardState parent = state.getSingleParentState();
		// if (parent != null && parent.getChildren().size() < 2){
		// 	return "This case rule can only be applied on a split transition";
		// }
		return null;
	}

	//returns the tiles that are adjacent to all changed tiles between parent and state
	//if types is null, all tiles are returned, if not, only tiles whitelisted in types are counted
	static Vector<Point> findCommonTile(BoardState parent,BoardState state,Set<Integer> types)
	{
		ArrayList<Point> dif = BoardState.getDifferenceLocations(parent,state);
		Vector<Point> ret = new Vector<Point>();
		Vector<Integer> adjacents = new Vector<Integer>();
		if(dif.size() >= 1)
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
						tmp_x = (dir<2)? ((dir%2==0)?(x-1):(x+1)) :x; //these two lines enumerate all orthagonal
						tmp_y = (dir<2)?y: ((dir%2==0)?(y-1):(y+1)) ; //directions 1 unit from (x,y)
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

	public boolean aultApplicationRaw(BoardState state, PuzzleModule pm ,Point location)
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
