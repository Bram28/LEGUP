package edu.rpi.phil.legup.puzzles.treetent;

import java.awt.Point;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import edu.rpi.phil.legup.BoardState;

public class RuleTentForTree extends RuleNewLink
{
	static final long serialVersionUID = 9516L;
	public String getImageName() {return "images/treetent/NewTreeLink.png";}
    public RuleTentForTree()
    {
			//Combine with ruleLastCampingSpot
    	setName("Tent for Tree");
        description = "If only one unlinked tent and no blank cells are adjacent to an unlinked tree, the unlinked tree must link to the unlinked tent.";
        //image = new ImageIcon("images/treetent/NewTreeLink.png");
    }

    protected String checkCellNeededLink(Point tree, Point tent, BoardState state, ArrayList<Object> validLinks)
    {
    	return this.checkTreeNeededLink(tree, tent, state, validLinks);
    }
}
