package edu.rpi.phil.legup.editor;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import edu.rpi.phil.legup.BoardImage;
import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.PuzzleModule;

/**
 * Similar to a BoardPanel but specifically designed for the editor in mind
 * @author Stan
 *
 */
public class EditorBoardFrame extends JFrame implements WindowListener
{
	private static final long serialVersionUID = -2304281047341398965L;
	
	JScrollPane scrollPane;
	EditorBoardPanel panel;
	PuzzleModule pm;
	static final Font largeFont = new Font("Arial",Font.PLAIN,16);
	BoardState curState = null;
	PuzzleEditor parent = null;
	
	/**
	 * Constructor
	 * @param state the state we're representing
	 * @param pm the loaded puzzle module for this puzzle
	 */
	public EditorBoardFrame(BoardState state, PuzzleModule pm, PuzzleEditor parent)
	{
		this.parent = parent;
		curState = state;
		this.pm = pm;
		panel = new EditorBoardPanel();
		scrollPane = new JScrollPane(panel);
		add(scrollPane);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(this);
		
		panel.initSize();
		panel.revalidate();
		pack();
		setLocation(500-getWidth() / 2, 300);
		setTitle("Initial State");
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
		
		/*protected void paintComponent( Graphics g ){
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			edu.rpi.phil.legup.BoardDrawingHelper.draw(g2);
		}*/
		// Why reinvent the wheel?
		protected void paintComponent( Graphics g ) 
		{
			super.paintComponent(g);
			
			BoardState state = curState;
			
			if (state != null)
			{
				PuzzleModule pz = pm;
				
				if (pz != null)
				{
					Dimension d = pz.getImageSize();
					int imW = d.width;
					int imH = d.height;
					
					int w  = state.getWidth();
					int h = state.getHeight();
					
					// TODO
					Graphics2D g2 = (Graphics2D) g;
					g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON );
					g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING,
						RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
					
					for (int y = 0; y < h; ++y)
					{
						for (int x = 0; x < w; ++x)
						{
							int val = state.getCellContents(x, y);
							pz.drawCell(g2, x, y, state);
							/*
							String imagePath = pz.getImageLocation(val);
							
							Image i = new ImageIcon(imagePath).getImage();
							
							g.drawImage(i,imW + x * imW, imH + y * imH, null);
							*/
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
					pz.drawExtraData(g,state.getExtraData(),state.extraDataDelta,new Rectangle(imW,imH,imW * w, imH * h),w,h);
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
			
			if (state != null)
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
								int next = pz.getAbsoluteNextCellValue(p.x,p.y,state);
								state.setCellContents(p.x,p.y,next);
								
							}
							else
							{ // right click
								BoardImage[] ims = pz.getAllCenterImages();
								
								int index = ImageChooserDialog.chooseImage(ims);
								
								if (index != -1)
								{
									state.setCellContents(p.x,p.y,ims[index].boardIndex);
								}
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
									BoardImage[] ims = pz.getAllBorderImages();
									
									if (ims.length > 0)
									{
									
										int index = ImageChooserDialog.chooseImage(ims);
										
										if (index != -1)
										{
											state.setLabel(label_direction[x],label_index[x],
													ims[index].boardIndex);	
										}
									}
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
		parent.closePuzzle();
	}
	
	public void windowOpened(WindowEvent arg0){}	
	public void windowClosed(WindowEvent arg0){}
	public void windowIconified(WindowEvent arg0){}
	public void windowDeiconified(WindowEvent arg0){}
	public void windowActivated(WindowEvent arg0){}
	public void windowDeactivated(WindowEvent arg0){}
}
