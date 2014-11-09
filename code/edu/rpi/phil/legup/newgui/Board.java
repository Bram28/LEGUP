package edu.rpi.phil.legup.newgui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseAdapter;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Vector;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JComponent;
import javax.swing.event.PopupMenuListener;
import edu.rpi.phil.legup.BoardDrawingHelper;
import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.CaseRule;
import edu.rpi.phil.legup.Legup;
import edu.rpi.phil.legup.PuzzleModule;
import edu.rpi.phil.legup.Selection;

public abstract class Board extends DynamicViewer implements BoardDataChangeListener
{
	private static final long serialVersionUID = 2272172621376357845L;

    protected Board() { super(); }
    protected Board(boolean b) { super(b); }
	abstract public void initSize();
	abstract protected void draw( Graphics2D g );
	abstract protected void mousePressedAt(Point p, MouseEvent e);
	abstract protected void mouseReleasedAt(Point p, MouseEvent e);
	abstract public void boardDataChanged(BoardState state);
}
