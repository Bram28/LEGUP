package edu.rpi.phil.legup.newgui;

import edu.rpi.phil.legup.BoardDrawingHelper;
import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.CaseRule;
import edu.rpi.phil.legup.CellPredicate;
import edu.rpi.phil.legup.Legup;
import edu.rpi.phil.legup.PuzzleModule;
import edu.rpi.phil.legup.Selection;
import edu.rpi.phil.legup.newgui.DynamicViewer;
import edu.rpi.phil.legup.newgui.LEGUP_Gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import java.util.ArrayList;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

public class CaseRuleSelectionHelper extends Board implements TreeSelectionListener
{
	static final long serialVersionUID = -489237132432L;

    public CellPredicate validCell = null;

	public Point pointSelected = null;
	public boolean allowLabels = Legup.getInstance().getPuzzleModule().hasLabels();
	public JDialog dialog = null;
	public volatile Object notifyOnSelection = null;

	public static boolean HIGHLIGHT_SELECTABLES = true;
	public static boolean CLOSE_ON_SELECTION = true;
	public static String helpMessage = (HIGHLIGHT_SELECTABLES && CLOSE_ON_SELECTION)?
			"Click an blue-highlighed square to apply the case rule there.":
			"Select where you would like to apply the CaseRule, and then select ok.";

	public CaseRuleSelectionHelper(CellPredicate cp)
	{
		super(false);
		setPreferredSize(new Dimension(600,400));
		setBackground(new Color(0xE0E0E0));
		setSize(getProperSize());
		zoomFit();
		zoomTo(1.0);
		Legup.getInstance().getSelections().addTreeSelectionListener(this);
		validCell = cp;
	}

    public void showInNewDialog()
    {
        Object[] msg = new Object[2];
        msg[0] = helpMessage;
        msg[1] = this;
        //JOptionPane.showMessageDialog(null,msg);
        JOptionPane pane = new JOptionPane(msg);
        pane.setOptions(new Object[]{"Cancel"});
        this.dialog = pane.createDialog("Case Rule selection");
        this.dialog.pack();
        this.dialog.setVisible(true);
    }

	private Dimension getProperSize()
	{
		Dimension rv = new Dimension();
		Selection selection = Legup.getInstance().getSelections().getFirstSelection();

		if (selection.isState())
		{
			BoardState state = selection.getState();
			PuzzleModule pz = Legup.getInstance().getPuzzleModule();

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

	protected void draw( Graphics2D g )
	{
		BoardDrawingHelper.draw(g,this);//pointSelected,mode);
	}

	public boolean isForbiddenTile(Point p)
	{
		return verifyAndNormalizePoint(p) == null;
	}

	public Point verifyAndNormalizePoint(Point p) {
		BoardState state = Legup.getCurrentState();
		int w = state.getWidth(); int h = state.getHeight();
		if(p.x == w) { p.x = -1; }
		if(p.y == h) { p.y = -1; }
		return validCell.check(state, p.x, p.y) ? p : null;
	}

	protected void mousePressedAt(Point p, MouseEvent e)
	{
		if (e.getButton() == MouseEvent.BUTTON1)
		{
			BoardState state = Legup.getCurrentState();
			PuzzleModule pm = Legup.getInstance().getPuzzleModule();
			Dimension d = pm.getImageSize();

			int imW = d.width;
			int imH = d.height;
			int w  = state.getWidth();
			int h = state.getHeight();

			p.x -= imW;
			p.y -= imH;

			p.x = (int)(Math.floor((double)p.x/imW));
			p.y = (int)(Math.floor((double)p.y/imH));

			if((p = verifyAndNormalizePoint(p)) != null)
			{
				pointSelected = p;
				selectionMade();
			}
		}
		repaint();
	}
	protected void selectionMade()
	{
		if(CLOSE_ON_SELECTION)
		{
			if(dialog != null)
			{
				if(pointSelected != null)
				{
					dialog.setVisible(false);
				}
			}
			else if(notifyOnSelection != null)
			{
				Legup.getInstance().getGui().popBoard();
				//notifyOnSelection.notify();
				//System.out.println("setting CRSH.notifyOnSelection to null.");
				notifyOnSelection = null;
			}
		}
	}

	protected void mouseReleasedAt(Point p, MouseEvent e) {}
    public void initSize() { System.out.println("CaseRuleSelectionHelper#initSize() called."); }

    public static Color caseRuleTargetHighlight = new Color(0,192,255,192);
    public static final Color cyanFilter = BoardDrawingHelper.cyanFilter;
    public void drawBoardOverlay(Graphics2D g, int width, int height, int imageWidth, int imageHeight)
    {
        for(int x = -1;x < width;++x)
        {
            for(int y = -1;y < height;++y)
            {
                if(HIGHLIGHT_SELECTABLES && !isForbiddenTile(new Point(x,y)))
                {
                    g.setStroke(new BasicStroke(3f));
                    g.setColor(caseRuleTargetHighlight);
                    g.fillRect(
                            (x+1) * imageWidth,
                            (y+1) * imageHeight,
                            imageWidth - 0,
                            imageHeight - 0);
                }
            }
        }
    }
	public void temporarilyReplaceBoard(LEGUP_Gui gui, Object toNotify)
    {
        gui.pushBoard(this);
        notifyOnSelection = toNotify;
    }
    public void blockUntilSelectionMade()
    {
        while((notifyOnSelection != null) && (pointSelected == null)) {}
    }
    public void boardDataChanged(BoardState state) {}
    public void treeSelectionChanged(ArrayList<Selection> newSelection) { selectionMade(); }
}
