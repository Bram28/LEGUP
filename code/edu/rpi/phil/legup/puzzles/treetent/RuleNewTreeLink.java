package edu.rpi.phil.legup.puzzles.treetent;

import java.awt.Point;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import edu.rpi.phil.legup.BoardState;

public class RuleNewTreeLink extends RuleNewLink
{
	static final long serialVersionUID = 9516L;
	public String getImageName() {return "images/treetent/NewTreeLink.png";}
    public RuleNewTreeLink()
    {
    	setName("New Tree Link");
    	description = "A tree must link to a tent if there are no unknowns or unlinked tents near the tree.";
    	//image = new ImageIcon("images/treetent/NewTreeLink.png");
    }

    protected String checkCellNeededLink(Point tree, Point tent, BoardState state, ArrayList<Object> validLinks)
    {
    	return this.checkTreeNeededLink(tree, tent, state, validLinks);
    }
}
