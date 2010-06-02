package edu.rpi.phil.legup.puzzles.heyawake;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

public class RegionToolbarPanel extends JPanel implements ActionListener
{
	private static final long serialVersionUID = 8572197337878587284L;

	JButton addChild = new JButton(new ImageIcon("images" + File.separator + "AddRegion.png"));
	RegionListFrame parent = null;
	
	RegionToolbarPanel(RegionListFrame parent)
	{
		this.parent = parent;
		
		setLayout(new FlowLayout());
		add(addChild);
		addChild.addActionListener(this);
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == addChild)
		{
			parent.addRegion();
		}
	}
}
