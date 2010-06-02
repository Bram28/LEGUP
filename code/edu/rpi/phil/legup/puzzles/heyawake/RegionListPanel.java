package edu.rpi.phil.legup.puzzles.heyawake;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import edu.rpi.phil.legup.newgui.TransitionChangeListener;

public class RegionListPanel extends JTree implements MouseListener, TransitionChangeListener, TreeSelectionListener
{
	private static final long serialVersionUID = -2304281047341398965L;
	
	private HeyawakeEditorBoardFrame parent;
	
	public RegionListPanel(HeyawakeEditorBoardFrame parent)
	{
		super(new DefaultMutableTreeNode("Regions Root"));
	    getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
	    addTreeSelectionListener(this);
		this.parent = parent;
		Vector<Region> regionsList = parent.getRegions();
		if(regionsList.size() > 0)
		{
			for(int cnt = 0; cnt < regionsList.size(); ++cnt)
			{
				initRegion(regionsList.elementAt(cnt).getValue());
			}
		}
	}
	
	public void addRegion(int value)
	{
		parent.addRegion(value);
		this.setSelectionRow(this.getRowCount()-1);
		DefaultMutableTreeNode root = (DefaultMutableTreeNode)this.treeModel.getRoot();
		root.add(new DefaultMutableTreeNode("Region " + value));
		((DefaultTreeModel)this.treeModel).reload();
		this.expandRow(0);
		this.setSelectionRow(this.getRowCount()-1);
		//this.setSelectionPath(treePath);
	}
	
	public void initRegion(int value)
	{
		this.setSelectionRow(this.getRowCount()-1);
		DefaultMutableTreeNode root = (DefaultMutableTreeNode)this.treeModel.getRoot();
		root.add(new DefaultMutableTreeNode("Region " + value));
		((DefaultTreeModel)this.treeModel).reload();
		this.expandRow(0);
		this.setSelectionRow(this.getRowCount()-1);
		//this.setSelectionPath(treePath);
	}
	
	public void addRegion()
	{
		String regionText = parent.addRegion();
		if(regionText.length() > 0)
		{
			this.setSelectionRow(this.getRowCount()-1);
			DefaultMutableTreeNode root = (DefaultMutableTreeNode)this.treeModel.getRoot();
			root.add(new DefaultMutableTreeNode("Region " + regionText));
			((DefaultTreeModel)this.treeModel).reload();
			this.expandRow(0);
			this.setSelectionRow(this.getRowCount()-1);
			//this.setSelectionPath(treePath);
		}
	}
	
	public int getSelected()
	{
		if(this.getSelectionRows() == null)
			return -1;
		return this.getSelectionRows()[0] - 1;
	}
	
	public void setSelected(int selectedIndex)
	{
		this.setSelectionRow(selectedIndex);
	}
	
	public void mouseClicked(MouseEvent arg0){}
	public void mousePressed(MouseEvent arg0){}
	public void mouseEntered(MouseEvent arg0){}
	public void mouseExited(MouseEvent arg0){}
	public void mouseReleased(MouseEvent e){}
	public void transitionChanged(){}
	public void valueChanged(TreeSelectionEvent e) 
	{
		parent.repaint();
	}
}
