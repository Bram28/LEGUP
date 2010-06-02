package edu.rpi.phil.legup.puzzles.treetent;

import java.awt.Point;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import edu.rpi.phil.legup.BoardState;

public class RuleNewTentLink extends RuleNewLink{

	
    public RuleNewTentLink()
    {
    	name = "New Link Tent";
    	description = "A tent must link to a tree if there are no unlinked trees near the tent.";
    	image = new ImageIcon("images/treetent/NewTentLink.png");
    }
    
    protected boolean checkCellNeededLink(Point tree, Point tent, BoardState state, ArrayList<Object> validLinks)
    {
    	return this.checkTentNeededLink(tree, tent, state, validLinks);
    }
}