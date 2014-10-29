package edu.rpi.phil.legup.puzzles.masyu;

import javax.swing.ImageIcon;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.PuzzleRule;
public class RuleNearWhite extends PuzzleRule {
	private static final long serialVersionUID = 327207911L;

    public String getImageName()
    {
    	return "images/masyu/Rules/RuleNearWhite.png";
    }

	public RuleNearWhite() {
		setName("Near White");
		description = "At least one cell near a white cell must turn.";
		image = new ImageIcon("images/masyu/Rules/RuleNearWhite.png");

	}
}

