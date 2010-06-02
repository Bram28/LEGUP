package edu.rpi.phil.legup.newgui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

public class TreeToolbarPanel extends JPanel implements ActionListener
{
	private static final long serialVersionUID = 8572197337878587284L;

	JButton addChild = new JButton(new ImageIcon("images" + File.separator + "AddChild.png"));
	JButton delChild = new JButton(new ImageIcon("images" + File.separator + "DelChild.png"));
	JButton merge = new JButton(new ImageIcon("images" + File.separator + "Merge.png"));
	JButton collapse = new JButton(new ImageIcon("images" + File.separator + "Collapse.png"));
	
	TreeFrame parent = null;
	
	TreeToolbarPanel(TreeFrame parent)
	{
		this.parent = parent;
		
		setLayout(new FlowLayout());
		add(addChild);
		addChild.addActionListener(this);
		add(delChild);
		delChild.addActionListener(this);
		add(merge);
		merge.addActionListener(this);
		add(collapse);
		collapse.addActionListener(this);
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == addChild)
		{
			parent.addChildAtCurrentState();
		}
		else if (e.getSource() == delChild)
		{
			parent.delChildAtCurrentState();
		}
		else if (e.getSource() == merge)
		{
			parent.mergeStates();
		}
		else if (e.getSource() == collapse)
		{
			parent.collapseStates();
		}
	}
	
	
}
