package edu.rpi.phil.legup.puzzles.masyu;

import java.awt.Point;
import java.util.ArrayList;

import javax.swing.ImageIcon;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.CaseRule;

public class CaseWhiteSplit extends CaseRule {

	
	public CaseWhiteSplit()
	{
		name = "White Split";
		description = "White pearls must either be vertical or horizontal.";
		image = new ImageIcon("images/masyu/Rules/CaseWhiteSplit.png");
	}
	
	public String checkCaseRuleRaw(BoardState state)
	{
		String rv = null;
		
		if (state.getTransitionsFrom().size() != 2)
		{
			rv = "This case rule can only be applied on a two-way split.";
		}
		else
		{
			BoardState one = state.getTransitionsFrom().get(0);
			BoardState two = state.getTransitionsFrom().get(1);
						
			ArrayList<Point> dif = BoardState.getDifferenceLocations(state,one);
			dif.addAll(BoardState.getDifferenceLocations(state,two));
			
			if (dif.size() == 0)
			{
				rv = "Your two-way split must change a single cell with this rule.";
			}
			else
			{
				//find middle point
				Point mid = null;
				{
					for(Point i:dif)
					{
						boolean good = true;
						for(Point j:dif)
							if(i.equals(j))
								continue;
							else if(i.x == j.x)
								if(i.y == j.y + 1 || i.y == j.y - 1)
									continue;
								else
									good = false;
							else if(i.y == j.y)
								if(i.x == j.x + 1 || i.x == j.x - 1)
									continue;
								else
									good = false;
							else
								good = false;
						if(good)
						{
							mid = i;
							break;
						}				
					}
					if(mid == null)
						return "There must be a + of spots changed at most";
				}
				
				if(!Masyu.isWhite(state.getCellContents(mid.x, mid.y)))
					return "Middle spot must be white";
				if(state.getCellContents(mid.x, mid.y) != Masyu.WHITE)
					return "Middle spot must start blank";
				int caseUpDown = one.getCellContents(mid.x, mid.y), caseLeftRight;
				if(Masyu.hasEast(caseUpDown) || Masyu.hasWest(caseUpDown))
				{
					caseLeftRight = caseUpDown;
					caseUpDown = two.getCellContents(mid.x, mid.y);
					BoardState temp = one;
					one = two;
					two = temp;
				}
				else
					caseLeftRight = two.getCellContents(mid.x, mid.y);
				//hold correct values now...
				if(!Masyu.hasNorth(caseUpDown) && !Masyu.hasSouth(caseUpDown))
				{
					rv = "You must specify the up-down side";
				}
				else if((!Masyu.hasEast(caseLeftRight) && !Masyu.hasWest(caseLeftRight)) ||
						Masyu.hasNorth(caseLeftRight) || Masyu.hasSouth(caseLeftRight))
				{
					rv = "You must specify the left-right side";
				}
				else
				{	
					//check to make sure everything done was valid
					BoardState temp = state.copy();
					
					BoardAccessor ba1 = new BoardAccessor(temp, one, 0, mid.x, mid.y);
					BoardAccessor ba2 = new BoardAccessor(temp, two, 0, mid.x, mid.y);
					RuleWhiteEdgeChecker c = new RuleWhiteEdgeChecker();
					
					for(Point p : dif)
					{
						if(p == mid)
							continue;
						temp.setCellContents(mid.x, mid.y, Masyu.NORTH);
						String s = Masyu.checkDirections(c, ba1);
						if(s != null)
							return s + "  This is on the UD split side.";
						temp.setCellContents(mid.x, mid.y, Masyu.EAST);
						s = Masyu.checkDirections(c,ba2);
						if(s != null)
							return s + "  This is on the LR split side.";
					}
					
				}
			}
		}
		return rv;
	}

}
