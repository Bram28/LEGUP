package edu.rpi.phil.legup.puzzles.heyawake;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

public class RegionListFrame extends JFrame
{
	private static final long serialVersionUID = -2304281047341398965L;
	
	private RegionListPanel regionListPanel;
	private JScrollPane treeView;
	private RegionToolbarPanel treeButton;
	
	public RegionListFrame(String title, HeyawakeEditorBoardFrame parent)
	{
		super(title);
		this.setSize(70, 200);
		regionListPanel = new RegionListPanel(parent);
		treeView = new JScrollPane(regionListPanel);
		treeButton = new RegionToolbarPanel(this);
		
		Container c = getContentPane();
		
		c.setLayout(new BorderLayout());
		
		c.add(treeButton,BorderLayout.NORTH);
		c.add(treeView,BorderLayout.CENTER);
	}
	
	public void addRegion()
	{
		regionListPanel.addRegion();
	}
	
	public void addRegion(int value)
	{
		regionListPanel.addRegion(value);
	}
	
	public int getSelected()
	{
		return regionListPanel.getSelected();
	}
	
	public void setSelected(int s)
	{
		regionListPanel.setSelected(s);
	}
}
