package edu.rpi.phil.legup.newgui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.ImageIcon;

import edu.rpi.phil.legup.Legup;

public class TutorToolbarPanel extends JPanel implements ActionListener
{
	private static final long serialVersionUID = 8572197337878587284L;
	
	private JButton example = new JButton(new ImageIcon("images/Tutor.gif"));
	
	// AI stuff
	private edu.rpi.phil.legup.AI myAI = new edu.rpi.phil.legup.AI();
	// end
	
	TutorFrame parent;
	TutorToolbarPanel(TutorFrame parent)
	{
		this.parent = parent;
		
		setLayout(new FlowLayout());
		
		add(example);
		example.addActionListener(this);
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == example)
		{
			myAI.setBoard(Legup.getInstance().getPuzzleModule());
			parent.tutorPrintln(myAI.findRuleApplication(Legup.getInstance().getSelections().getFirstSelection().getState()));
		}
	}
}
