package edu.rpi.phil.legup.puzzles.heyawake;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JDialog;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.PuzzleModule;
import edu.rpi.phil.legup.editor.PuzzleEditor;

/**
 * Similar to a the regular editor but specifically designed for the heyewake puzzle
 * @author Matt Morrow
 *
 */
public class HeyawakeEditorBoardFrame extends JDialog implements WindowListener, KeyListener
{
	private static final long serialVersionUID = -2304281047341398965L;
	
	JScrollPane scrollPane;
	EditorBoardPanel panel;
	PuzzleModule pm;
	static final Font largeFont = new Font("Arial",Font.PLAIN,16);
	BoardState curState = null;
	PuzzleEditor parent = null;
	private RegionListFrame regionListFrame = null;
	Vector <Region> regions;
	int[][] cellRegions;
	private int lastx = -1;
	private int lasty = -1;
	
	/**
	 * Constructor
	 * @param state the state we're representing
	 * @param pm the loaded puzzle module for this puzzle
	 */
	public HeyawakeEditorBoardFrame(BoardState state, PuzzleModule pm, PuzzleEditor peditor)
	{
		parent = peditor;
		curState = state;
		
		this.pm = pm;
		panel = new EditorBoardPanel();
		scrollPane = new JScrollPane(panel);
		add(scrollPane);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(this);
		addKeyListener(this);
		int w  = this.curState.getWidth();
		int h = this.curState.getHeight();
		cellRegions = new int[h][w];
		if(curState.getExtraData().size() < 3)
		{
			curState.getExtraData().clear();
			curState.addExtraData(new Region[0]);
			curState.addExtraData(Integer.valueOf(0));
			int[][] temp = new int[h][w];
			for (int y = 0; y < h; ++y)
			{
				for (int x = 0; x < w; ++x)
				{
					temp[y][x] = -1;
				}
			}
			curState.addExtraData(temp);
		}
		
		for (int y = 0; y < h; ++y)
		{
			for (int x = 0; x < w; ++x)
			{
				setNeighbors(x, y);
			}
		}
		
		regions = Region.getRegions(curState);
		
		panel.initSize();
		panel.revalidate();
		pack();
		setLocation(500-getWidth() / 2, 300);
		setTitle("Initial Regions");
		regionListFrame = new RegionListFrame("Region List", this);
		regionListFrame.setVisible(true);
		regionListFrame.setLocation(this.getX() + this.getWidth(), this.getY());
	}
	
	public void saving()
	{
		curState = Region.setRegions(regions, curState);
	}
	
	public String addRegion()
	{
		int rv = -12180;
		String temp_input = "";
		while (rv == -12180)
		{	
			temp_input= JOptionPane.showInputDialog("Enter the value for the region.");
			
			if (temp_input == null)
				break;
			
			try
			{
				rv = Integer.parseInt(temp_input);
			}
			catch (NumberFormatException e)
			{
				JOptionPane.showMessageDialog(null, temp_input + " is not an interger.");
			}
		}
		if(rv == -12180)
			temp_input = "";
		else
			this.regions.add(new Region(rv));
		return temp_input;
	}
	
	public void addRegion(int value)
	{
		this.regions.add(new Region(value));
	}
	
	public Vector<Region> getRegions()
	{
		return this.regions;
	}
	
	private void removeCell(int x, int y)
	{
		for(int cnt = 0; cnt < regions.size(); ++cnt)
		{
			((Region)regions.elementAt(cnt)).removeCell(x, y);
		}
	}
	
	public void setNeighbors(int x, int y)
	{
		BoardState state = curState;
		//int region = state.getCellRegion(x, y);
		if(x > 0)
		{
			//if(state.getCellRegion(x - 1, y) != -1)
			{
				cellRegions[y][x-1]  = getNeighbors(x - 1,y);
			}
		}
		if(x < state.getWidth()-1)
		{
			//if(state.getCellRegion(x + 1, y) != -1)
			{
				cellRegions[y][x+1]  = getNeighbors(x + 1,y);
			}
		}
		if(y > 0)
		{
			//if(state.getCellRegion(x, y-1) != -1)
			{
				cellRegions[y-1][x]  = getNeighbors(x ,y-1);
			}
		}
		if(y < state.getHeight()-1)
		{
			//if(state.getCellRegion(x, y + 1) != -1)
			{
				cellRegions[y+1][x]  = getNeighbors(x ,y+1);
			}
		}
	}
	
	public int getNeighbors(int x, int y)
	{
		BoardState state = curState;
		int[][] regions = ((int[][])state.getExtraData().get(2));
		int region = regions[y][x];
		if(region < 0)
			return -1;
		int temp = 0;
		if(x > 0)
		{
			if(regions[y][x-1] != region)
			{
				temp = temp | 8;
			}
		}
		else
		{
			temp = temp | 8;
		}
		if(x < state.getWidth()-1)
		{
			if(regions[y][x+1] != region)
			{
				temp = temp | 2;
			}
		}
		else
		{
			temp = temp | 2;
		}
		if(y > 0)
		{
			if(regions[y-1][x] != region)
			{
				temp = temp | 1;
			}
		}
		else
		{
			temp = temp | 1;
		}
		if(y < state.getHeight()-1)
		{
			if(regions[y+1][x] != region)
			{
				temp = temp | 4;
			}
		}
		else
		{
			temp = temp | 4;
		}
		return temp;
	}
	
	public void close()
	{
		Region.setRegions(regions, curState);
		regionListFrame.dispose();
		this.dispose();
	}
	
	class EditorBoardPanel extends JPanel implements MouseListener
	{		
		private static final long serialVersionUID = -2304281047341398965L;
		
		public EditorBoardPanel()
		{			
			addMouseListener(this);
		}
		
		public void initSize()
		{ // initialize the size of the panel (can't do it on init because we don't know the size of tile)
			setPreferredSize(getProperSize());
			revalidate();
		}
		
		public Dimension getProperSize()
		{
			Dimension rv = new Dimension();
			BoardState state = curState;
			
			if (state != null)
			{
				PuzzleModule pz = pm;
				
				if (pz != null)
				{
					Dimension d = pz.getImageSize();
					int w  = state.getWidth();
					int h = state.getHeight();
		
					rv.width = d.width * (w + 2);
					rv.height = d.height * (h + 2);
				}
			}
			
			return rv;
		}
		
		protected void paintComponent( Graphics g ) 
		{
			super.paintComponent(g);
			
			BoardState state = curState;
			
			if (state != null);
			{
				PuzzleModule pz = pm;
				
				if (pz != null)
				{
					Dimension d = pz.getImageSize();
					int imW = d.width;
					int imH = d.height;
					
					int w  = state.getWidth();
					int h = state.getHeight();
					
					int selectedRegion = regionListFrame.getSelected();
					
					for (int y = 0; y < h; ++y)
					{
						for (int x = 0; x < w; ++x)
						{
							int val = getNeighbors(x,y);
							String imagePath = "";
							if(((int[][])state.getExtraData().get(2))[y][x] == selectedRegion)
								imagePath = "images/regions/selected/" + val+ ".gif";
							else
								imagePath = "images/regions/" + val+ ".gif";
							
							Image i = new ImageIcon(imagePath).getImage();
							
							g.drawImage(i,imW + x * imW, imH + y * imH, null);
						}
					}
					
					// do headers
					setFont(largeFont);
					for (int x = 0; x < w; ++x)
					{
						int val = state.getLabel(BoardState.LABEL_TOP,x);
						String imagePath = pz.getImageLocation(val);
						Image i = new ImageIcon(imagePath).getImage();
						g.drawImage(i,imW + x * imW, 0,null);
						
						val = state.getLabel(BoardState.LABEL_BOTTOM,x);
						imagePath = pz.getImageLocation(val);
						i = new ImageIcon(imagePath).getImage();
						g.drawImage(i,imW + x * imW, imH * (h + 1),null);				
					}
					
					for (int y = 0; y < h; ++y)
					{
						int val = state.getLabel(BoardState.LABEL_LEFT,y);
						String imagePath = pz.getImageLocation(val);
						Image i = new ImageIcon(imagePath).getImage();
						g.drawImage(i,0, imH * (y + 1),null);
						
						val = state.getLabel(BoardState.LABEL_RIGHT,y);
						imagePath = pz.getImageLocation(val);
						i = new ImageIcon(imagePath).getImage();
						g.drawImage(i,imW * (w + 1), imH * (y + 1),null);
					}
					
					// do grid
					pz.drawGrid(g,new Rectangle(imW,imH,imW * w,imH * h),w,h);
				}
			}
		}

		public void mouseClicked(MouseEvent e){}
		
		public void mouseReleased(MouseEvent e){}
		public void mouseEntered(MouseEvent e){}
		public void mouseExited(MouseEvent e){}
		
		public void mousePressed(MouseEvent e)
		{
			BoardState state = curState;
			
			if (state != null);
			{
				PuzzleModule pz = pm;
				
				if (pz != null)
				{
					Dimension d = pz.getImageSize();
					int imW = d.width;
					int imH = d.height;
					int w  = state.getWidth();
					int h = state.getHeight();
					Point p = e.getPoint();
					
					p.x /= imW;
					p.y /= imH;
					

					//System.out.println("x = " + p.x + ", y = " + p.y);
					
					if ((p.x > 0 && p.y > 0) && (p.x <= w && p.y <= h))
					{		
						--p.x;
						--p.y;
						
						if (p.x < w && p.y < h)
						{ // p.x and p.y hold the grid point now!
							if (e.getButton() == MouseEvent.BUTTON1)
							{ // left click
								if(e.isShiftDown() && lastx != -1 && lasty != -1)
								{
									int minx=Math.min(p.x,lastx);
									int maxx=Math.max(p.x,lastx);
									int maxy=Math.max(p.y,lasty);
									for(;minx <= maxx; ++minx)
									{
										for(int miny=Math.min(p.y,lasty);miny <= maxy; ++miny)
										{
											removeCell(minx, miny);
											int next = regionListFrame.getSelected();
											((int[][])curState.getExtraData().get(2))[miny][minx] = next;
											if(next == -1)
												cellRegions[miny][minx] = -1;
											else
											{
												cellRegions[miny][minx] = getNeighbors(minx,miny);
												((Region)regions.elementAt(next)).addCell(minx, miny);
											}
										}
									}
									lastx = -1;
									lasty = -1;
								}
								else
								{
									removeCell(p.x, p.y);
									int next = regionListFrame.getSelected();
									((int[][])curState.getExtraData().get(2))[p.y][p.x] = next;
									if(next == -1)
										cellRegions[p.y][p.x] = -1;
									else
									{
										cellRegions[p.y][p.x] = getNeighbors(p.x,p.y);
										((Region)regions.elementAt(next)).addCell(p.x, p.y);
									}
									lastx = p.x;
									lasty = p.y;
								}

								setNeighbors(p.x, p.y);
							}
							else
							{ //right click
								int selectedRegion = ((int[][])state.getExtraData().get(2))[p.y][p.x] + 1;
								if(selectedRegion < 1)
									selectedRegion = 0;
								regionListFrame.setSelected(selectedRegion);
								
							}
							
							repaint();
						}
					}
					else
					{						
						boolean[] conditions = 
						{
							(p.x == 0 && p.y > 0 && p.y <= h), // left
							(p.x == w + 1 && p.y > 0 && p.y <= h), // right
							(p.y == 0 && p.x > 0 && p.x <= w), // top
							(p.y == h + 1 && p.x > 0 && p.x <= w) // bottom
						};
						
						int[] label_direction = 
						{
							BoardState.LABEL_LEFT,
							BoardState.LABEL_RIGHT,
							BoardState.LABEL_TOP,
							BoardState.LABEL_BOTTOM
						};
						
						int[] label_index = 
						{
							p.y - 1, p.y - 1, p.x - 1, p.x - 1
						};
						
						for (int x = 0; x < conditions.length; ++x)
						{
							if (conditions[x])
							{
								if (e.getButton() == MouseEvent.BUTTON1)
								{ // left click		
									int val = state.getLabel(label_direction[x],label_index[x]);
									
									state.setLabel(label_direction[x],label_index[x],
											pm.getNextLabelValue(val));									
								}
								else
								{
									//do nothing for now
								}
								
								repaint();
								break;
							}
						}
					}
				}
			}
		}
	}

	public void windowClosing(WindowEvent e)
	{
		parent.setEditorVisible(true);
		
		regionListFrame.dispose();
		this.close();
	}
	
	public void windowClosed(WindowEvent arg0){}
	public void windowOpened(WindowEvent arg0){}	
	public void windowIconified(WindowEvent arg0){}
	public void windowDeiconified(WindowEvent arg0){}
	public void windowActivated(WindowEvent arg0){}
	public void windowDeactivated(WindowEvent arg0){}
	
	
	public void keyTyped(KeyEvent e)
	{
		if(e.getKeyChar() == '1')
		{
			regionListFrame.addRegion(1);
		}
		else if(e.getKeyChar() == '2')
		{
			regionListFrame.addRegion(2);
		}
		else if(e.getKeyChar() == '3')
		{
			regionListFrame.addRegion(3);
		}
		else if(e.getKeyChar() == '4')
		{
			regionListFrame.addRegion(4);
		}
		else if(e.getKeyChar() == '5')
		{
			regionListFrame.addRegion(5);
		}
		else if(e.getKeyChar() == '6')
		{
			regionListFrame.addRegion(6);
		}
		else if(e.getKeyChar() == '7')
		{
			regionListFrame.addRegion(7);
		}
		else if(e.getKeyChar() == '8')
		{
			regionListFrame.addRegion(8);
		}
		else if(e.getKeyChar() == '9')
		{
			regionListFrame.addRegion(9);
		}
		else if(e.getKeyChar() == '0')
		{
			regionListFrame.addRegion(0);
		}
		else if(e.getKeyChar() == '-')
		{
			regionListFrame.addRegion(-1);
		}
		else if(e.getKeyChar() == '`')
		{
			regionListFrame.addRegion();
		}
	}
	public void keyPressed(KeyEvent e){}
	public void keyReleased(KeyEvent e){}
}
