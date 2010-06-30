package edu.rpi.phil.legup;
import edu.rpi.phil.legup.newgui.TreePanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

public abstract class BoardDrawingHelper
{
	public static Color greenFilter = new Color(0,255,0,128);
	public static Color redFilter = new Color(255,0,0,128);
	public static Color orangeFilter = new Color(255,165,0,128);
	public static Color purpleFilter = new Color(240,0,240,128);
	public static Color blueFilter = new Color(000,255,255,64);

	private static final Font largeFont = new Font("Arial",Font.PLAIN,16);
	private static final Font smallFont = new Font("Arial",Font.PLAIN,10);
	private static final Font labelFont = new Font("Arial",Font.BOLD,16);
	private static final boolean init = false;

	private static int imageWidth;
	private static int imageHeight;
	private static int fontLeft;
	private static int halffontLeft;
	private static int fontTop;
	private static int width;
	private static int height;

	private static void initGraphics(Graphics2D g)
	{
		PuzzleModule pm = Legup.getInstance().getPuzzleModule();
		if(pm == null)
			return;

		Dimension d = pm.getImageSize();
		imageWidth = d.width;
		imageHeight = d.height;

		Rectangle2D fontRect = g.getFontMetrics(labelFont).getStringBounds("AA", g);
		fontLeft = (int)((imageWidth - fontRect.getWidth()) / 2.0);
		halffontLeft = (int)((imageWidth - (fontRect.getWidth() / 2)) / 2.0);
		fontTop = (int)(( imageHeight - fontRect.getHeight()) / -2.0);
	}


	public static void drawImage(Graphics2D g, int x, int y, Image img)
	{
		g.drawImage(img, imageWidth * (x + 1), imageHeight * (y + 1), imageWidth, imageHeight, null);
	}


	public static void drawText( Graphics2D g, int x, int y, String text)
	{

		PuzzleModule pm = Legup.getInstance().getPuzzleModule();
		if(pm == null)
			return;

		//Determine the rectangle that our text will be so we can center it

		if(text.length() == 1)
			g.drawString(text,pm.getImageSize().width * (x + 1) + halffontLeft, pm.getImageSize().height * (y + 2) + fontTop);
		else
			g.drawString(text,pm.getImageSize().width * (x + 1) + fontLeft, pm.getImageSize().height * (y + 2) + fontTop);
	}

	public static String numberToLetters(int number)
	{
		String s = "";
		while(number != 0)
		{
			int rem = (number % 26) + 64;
			number = number / 26;
			char c = (char)rem;
			s = c + s;
		}
		return s;
	}


	/**
	 *	- Daniel P - February 20, 2009 -
	 *
	 *	If this variable is true, then the feature that "animates" choices will be activated.
	 *	To turn off this feature, set it to false
	 */
	public static final boolean ANIMATE_SPLIT_CASE = true;
	private static Color orangeSquare = ((!ANIMATE_SPLIT_CASE) ? new Color(225,182,100,255) : new Color(255,182,100,128));

	public static void draw( Graphics2D g )
	{
		initGraphics(g);

		PuzzleModule pm = Legup.getInstance().getPuzzleModule();
		Selection selection = Legup.getInstance().getSelections().getFirstSelection();
		BoardState state = selection.getState();

		int width  = state.getWidth();
		int height = state.getHeight();

		//Save current color so we can put it back later
		Color pre = g.getColor();

		BoardState origState = null, newState = state;
		if (state.getTransitionsTo().size() == 1)
			origState = state.getTransitionsTo().get(0);
		boolean showOrange;

		if (TreePanel.getMouseOver() != null)
		{
			selection = TreePanel.getMouseOver();
			if (selection.isState()) origState = newState = selection.getState();
			else newState = (origState = selection.getState()).getTransitionsFrom().get(0);
			showOrange = true;
		}
		else showOrange = false;

		if (ANIMATE_SPLIT_CASE && !selection.isState())
			newState = origState.getTransitionsFrom().get((int)((System.currentTimeMillis()/1000)%origState.getTransitionsFrom().size()));

		//Loop over every cell
		for (int y = 0; y < height; ++y)
		{
			for (int x = 0; x < width; ++x)
			{
				//Draw the cell
				pm.drawCell(g, x, y, newState);

				//Determine if we need to add a different color based on transitions and changes made
				Color filterColor = determineTransitionColor(x, y, newState, origState, showOrange);
				//Color it, if needed
				if(filterColor != null)
				{
					g.setColor(filterColor);
					g.fillRect(imageWidth + x * imageWidth, imageHeight + y * imageHeight, imageWidth, imageHeight);
				}
			}
		}

		if(newState.getHintCells() != null)
		{
			g.setColor(purpleFilter);

			for(Point p : state.getHintCells())
			{
				g.fillRect(imageWidth + p.x * imageWidth, imageHeight + p.y * imageHeight, imageWidth, imageHeight);
			}
		}

		//Put color back
		g.setColor(pre);

		drawLabels(g, state);
		// Draw grid on top, handled by puzzle module
		pm.drawGrid(g,new Rectangle(imageWidth,imageHeight,imageWidth * width, imageHeight * height),width,height);
		//Let the puzzle module draw its special stuff
		try
		{
			pm.drawExtraData(g,newState.getExtraData(),new Rectangle(imageWidth,imageHeight,imageWidth * width, imageHeight * height),width,height);
		}
		catch (Exception e) // There is a massive problem with Board Listeners.  Simulated generation should not be drawn
		{
			System.out.println("Another one bites the dust: " + e);
		}
	}

	public static Color determineTransitionColor(int x, int y, BoardState newState, BoardState oldState, boolean showOrange)
	{
		int curVal = newState.getCellContents(x, y);
		int prevVal = ((oldState == null) ? curVal : oldState.getCellContents(x, y));

		//Draw green if a cell has been changed from CELL_UNKNOWN to a value
		//Draw red if a cell has been changed, and not from an unknown
		//If it is a transition then draw all cells which don't match as orange
		if (prevVal == PuzzleModule.CELL_UNKNOWN && curVal != prevVal)
			if (showOrange) return orangeSquare;
			else return greenFilter;
		else if (prevVal != PuzzleModule.CELL_UNKNOWN && prevVal != curVal)
			return redFilter;
		else if (oldState == null && newState.getTransitionsTo().size() > 1)  //Transition
		{
			// check if it's identical in all children if not draw orange
			for (BoardState parent : newState.getTransitionsTo())
			{
				if (parent.getCellContents(x,y) != curVal)
					return orangeSquare;
			}
			return null;
		}
		else return null;

		/* Obsolete
		if(selection.isState()) //State
		{
			if (!showOrange) return null;
			int pval = val;
			boolean parentEmpty = false;
			//Loop through all the previous states, remember, this can be 0, 1, or more
			for(BoardState parent : state.getTransitionsTo())
			{
				pval = parent.getCellContents(x, y);
				if(pval == PuzzleModule.CELL_UNKNOWN && val != PuzzleModule.CELL_UNKNOWN)
					parentEmpty = true; //We can't quite call this one as red yet, see below
				else if(pval != val)
					return redFilter; //If the value is differen't return immediately
			}

			//If a state has multiple parents, we aren't allowed to add new information so
			//going from empty->value is red
			if(parentEmpty)
			{
				if(state.getTransitionsTo().size() > 1)
					return redFilter;
				else
					return greenFilter;
			}
		}
		return null; */
	}

	public static void drawLabels(Graphics2D g, BoardState state)
	{
		PuzzleModule pm = Legup.getInstance().getPuzzleModule();
		int imageWidth = pm.getImageSize().width;
		int imageHeight = pm.getImageSize().height;
		int h = state.getHeight();
		int w = state.getWidth();
		//Draw background
		g.setColor(Color.GRAY);
		g.fillRect(imageWidth, 0, imageWidth * w, imageHeight);
		g.fillRect(imageWidth, imageHeight * (h + 1), imageWidth * w, imageHeight);
		g.fillRect(0, imageHeight, imageWidth, imageHeight * h);
		g.fillRect(imageWidth * (w + 1), imageHeight, imageWidth, imageHeight * h);

		//Draw the headers
		Font old = g.getFont();
		g.setColor(Color.BLACK);
		g.setFont(labelFont);


		for (int x = 0; x < w; ++x)
		{
			int val = state.getLabel(BoardState.LABEL_TOP,x);
			pm.drawTopLabel(g, val, x, -1);

			val = state.getLabel(BoardState.LABEL_BOTTOM,x);
			pm.drawBottomLabel(g, val, x, h);
		}

		for (int y = 0; y < h; ++y)
		{
			int val = state.getLabel(BoardState.LABEL_LEFT,y);
			pm.drawLeftLabel(g, val, -1, y);

			val = state.getLabel(BoardState.LABEL_RIGHT,y);
			pm.drawRightLabel(g, val, w, y);
		}

		g.setFont(old);
	}
}
