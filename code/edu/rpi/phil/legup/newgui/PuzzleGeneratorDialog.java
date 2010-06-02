package edu.rpi.phil.legup.newgui;

import edu.rpi.phil.legup.PuzzleGeneration;

import java.awt.Container;
import java.awt.Dimension;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class PuzzleGeneratorDialog extends JDialog
{

	public static final int PUZZLE_CHOSEN = 1,
									CANCELED = 2;

	private int choice = CANCELED;
	private String strChoice;
	private int diffChoice;

	private final JComboBox combo;
	private final JComboBox dCombo;

	public PuzzleGeneratorDialog(JFrame parent)
	{
		super(parent, "Puzzle Generation Chooser", true);

		JLabel choose = new JLabel("Choose a Puzzle: ");
		choose.setLocation(20, 20);
		choose.setSize(choose.getPreferredSize());
		combo = new JComboBox(PuzzleGeneration.validPuzzles);
		combo.setSize(combo.getPreferredSize());
		combo.setLocation(30+choose.getWidth(), 20+(choose.getHeight()-combo.getHeight())/2);

		JLabel diff = new JLabel("Difficulty Level: ");
		diff.setLocation(20, 50);
		diff.setSize(diff.getPreferredSize());
		dCombo = new JComboBox(PuzzleGeneration.difficulties);
		dCombo.setSelectedIndex(PuzzleGeneration.NORMAL);
		dCombo.setSize(dCombo.getPreferredSize());
		dCombo.setLocation(30+diff.getWidth(), 50+(diff.getHeight()-dCombo.getHeight())/2);

		JButton OK = new JButton("OK");
		JButton CANCEL = new JButton("Cancel");
		OK.setLocation(20, 80);
		OK.setSize(OK.getPreferredSize());
		CANCEL.setLocation(20, 80);
		CANCEL.setSize(CANCEL.getPreferredSize());

		Container c = getContentPane();
		c.setLayout(null);
		c.add(choose);
		c.add(combo);
		c.add(diff);
		c.add(dCombo);
		c.add(OK);
		c.add(CANCEL);
		c.validate();
		c.setPreferredSize(new Dimension(dCombo.getWidth()+dCombo.location().x+20, OK.getHeight()+OK.location().y+20));

		pack();
		int seg = (getWidth()-40-OK.getWidth()-CANCEL.getWidth())/3;
		OK.setLocation(seg+20, 80);
		CANCEL.setLocation(getWidth()-20-seg-CANCEL.getWidth(), 80);

		OK.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				choice = PUZZLE_CHOSEN;
				setVisible(false);
			}
		});
		CANCEL.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				choice = CANCELED;
				setVisible(false);
			}
		});
	}

	public int getChoice()
	{
		return choice;
	}

	public String puzzleChosen()
	{
		return (String)combo.getSelectedItem();
	}
	public int difficultyChosen()
	{
		return dCombo.getSelectedIndex();
	}

}