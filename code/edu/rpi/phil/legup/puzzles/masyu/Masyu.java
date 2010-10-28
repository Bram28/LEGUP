package edu.rpi.phil.legup.puzzles.masyu;

import java.awt.BasicStroke;
import java.awt.Stroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.Vector;
import java.util.ArrayList;
import java.awt.Rectangle;

import edu.rpi.phil.legup.BoardImage;
import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.CaseRule;
import edu.rpi.phil.legup.Contradiction;
import edu.rpi.phil.legup.PuzzleModule;
import edu.rpi.phil.legup.PuzzleRule;

// TODO: Auto-generated Javadoc
/**
 * The Class Masyu.
 */
public class Masyu extends PuzzleModule
{

    /**
     * The Constructor.
     */
    public Masyu()
    {
    }
    // Constant values for Board
    static final int BLANK = 0;
    static final int WHITE = 1;
    static final int BLACK = 2;
    private BasicStroke medium = new BasicStroke(2);

    /**
     * Take an action when the left mouse button is pressed.
     *
     * @param where the position where the pressed event occured
     * @param state the current board state
     */
    public void mousePressedEvent(BoardState state, Point where)
    {
    }

    /**
     * Take an action when a left mouse drag (or click) event occurs.
     *
     * @param to the to
     * @param state the state
     * @param from the from
     */
    private void initExtraData(BoardState state, ArrayList<Object> extraData)
    {
        extraData = new ArrayList<Object>();
        extraData.add((Object) new Point[state.getWidth()][state.getHeight()]);
        for (int x = 0; x < state.getWidth(); x++)
        {
            for (int y = 0; y < state.getHeight(); y++)
            {
                ((Point[][]) extraData.get(0))[x][y] = new Point(0, 0);
            }
        }
        state.setExtraData(extraData);
    }

    public void mouseDraggedEvent(BoardState state, Point from, Point to)
    {
        int toVal = state.getCellContents(to.x, to.y);
        int fromVal = state.getCellContents(from.x, from.y);
        ArrayList<Object> extraData = state.getExtraData();
        if (extraData.size() == 0)
        {
            initExtraData(state, extraData);
        }
        extraData = state.getExtraData();
        Point[][] lineData = (Point[][]) extraData.get(0);
        if (from.equals(to))
        {
            return;
        }
        if (from.x == to.x)
        {
            if (to.y > from.y)
            {
                for (int temp = from.y; temp < to.y; temp++)
                {
                    if (lineData[from.x][temp].x == 3 || lineData[from.x][temp].y == 3)
                    {
                        if (lineData[from.x][temp].x == 3)
                        {
                            lineData[from.x][temp].x = 0;
                        } else
                        {
                            lineData[from.x][temp].y = 0;
                        }

                        if (lineData[to.x][temp + 1].x == 1)
                        {
                            lineData[to.x][temp + 1].x = 0;
                        } else
                        {
                            lineData[to.x][temp + 1].y = 0;
                        }
                    } else
                    {
                        //add the link
                        if (lineData[from.x][temp].x == 0)
                        {
                            lineData[from.x][temp].x = 3;
                        } else
                        {
                            lineData[from.x][temp].y = 3;
                        }

                        if (lineData[to.x][temp + 1].x == 0)
                        {
                            lineData[to.x][temp + 1].x = 1;
                        } else
                        {
                            lineData[to.x][temp + 1].y = 1;
                        }
                    }
                }
            } else
            {
                for (int temp = to.y; temp < from.y; temp++)
                {
                    //dest is above
                    if (lineData[from.x][from.y].x == 1 || lineData[from.x][from.y].y == 1)
                    {
                        if (lineData[from.x][temp + 1].x == 1)
                        {
                            lineData[from.x][temp + 1].x = 0;
                        } else
                        {
                            lineData[from.x][temp + 1].y = 0;
                        }

                        if (lineData[to.x][temp].x == 3)
                        {
                            lineData[to.x][temp].x = 0;
                        } else
                        {
                            lineData[to.x][temp].y = 0;
                        }
                    } else
                    {
                        //add the link
                        if (lineData[from.x][temp + 1].x == 0)
                        {
                            lineData[from.x][temp + 1].x = 1;
                        } else
                        {
                            lineData[from.x][temp + 1].y = 1;
                        }

                        if (lineData[to.x][temp].x == 0)
                        {
                            lineData[to.x][temp].x = 3;
                        } else
                        {
                            lineData[to.x][temp].y = 3;
                        }
                    }
                }
            }
        } else if (from.y == to.y)
        {
            if (to.x > from.x)
            {
                for (int temp = from.x; temp < to.x; temp++)
                {
                    //dest is to the right
                    if (lineData[temp][from.y].x == 2 || lineData[temp][from.y].y == 2)
                    {
                        if (lineData[temp][from.y].x == 2)
                        {
                            lineData[temp][from.y].x = 0;
                        } else
                        {
                            lineData[temp][from.y].y = 0;
                        }

                        if (lineData[temp + 1][to.y].x == 4)
                        {
                            lineData[temp + 1][to.y].x = 0;
                        } else
                        {
                            lineData[temp + 1][to.y].y = 0;
                        }
                    } else
                    {
                        //add the link
                        if (lineData[temp][from.y].x == 0)
                        {
                            lineData[temp][from.y].x = 2;
                        } else
                        {
                            lineData[temp][from.y].y = 2;
                        }

                        if (lineData[temp + 1][to.y].x == 0)
                        {
                            lineData[temp + 1][to.y].x = 4;
                        } else
                        {
                            lineData[temp + 1][to.y].y = 4;
                        }
                    }
                }
            } else
            {
                for (int temp = to.x; temp < from.x; temp++)
                {
                    //dest is to the left
                    if (lineData[temp + 1][from.y].x == 4 || lineData[temp + 1][from.y].y == 4)
                    {
                        if (lineData[temp + 1][from.y].x == 4)
                        {
                            lineData[temp + 1][from.y].x = 0;
                        } else
                        {
                            lineData[temp + 1][from.y].y = 0;
                        }

                        if (lineData[temp][to.y].x == 4)
                        {
                            lineData[temp][to.y].x = 0;
                        } else
                        {
                            lineData[temp][to.y].y = 0;
                        }
                    } else
                    {
                        //add the link
                        if (lineData[temp + 1][from.y].x == 0)
                        {
                            lineData[temp + 1][from.y].x = 4;
                        } else
                        {
                            lineData[temp + 1][from.y].y = 4;
                        }

                        if (lineData[temp][to.y].x == 0)
                        {
                            lineData[temp][to.y].x = 2;
                        } else
                        {
                            lineData[temp][to.y].y = 2;
                        }
                    }
                }
            }
        } else
        {
            //error
        }
        state.setExtraData(extraData);
    }

    public void drawExtraData(Graphics gr, ArrayList<Object> extraData, Rectangle bounds, int w, int h)
    {
        Graphics2D g = (Graphics2D) gr;
        Stroke preStroke = g.getStroke();
        Color preColor = g.getColor();
        g.setColor(Color.blue);
        g.setStroke(new BasicStroke(2));

        double dx = bounds.width / (double) w;
        double dy = bounds.height / (double) h;
        double halfX = dx / 2;
        double halfY = dy / 2;
        Point[][] lineData = (Point[][]) extraData.get(0);
        for (int x = 0; x < w; x++)
        {
            for (int y = 0; y < h; y++)
            {
                if ((lineData[x][y].x == 1) || (lineData[x][y].y == 1))
                {
                    double x1 = bounds.x + (x * dx) + halfX;
                    double y1 = bounds.y + (y * dx) + halfY;

                    double x2 = bounds.x + (x * dx) + halfX;
                    double y2 = bounds.y + ((y - 1) * dx) + halfY;

                    g.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
                }
                if ((lineData[x][y].x == 2) || (lineData[x][y].y == 2))
                {
                    double x1 = bounds.x + (x * dx) + halfX;
                    double y1 = bounds.y + (y * dx) + halfY;

                    double x2 = bounds.x + ((x + 1) * dx) + halfX;
                    double y2 = bounds.y + (y * dx) + halfY;

                    g.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
                }
                if ((lineData[x][y].x == 3) || (lineData[x][y].y == 3))
                {
                    double x1 = bounds.x + (x * dx) + halfX;
                    double y1 = bounds.y + (y * dx) + halfY;

                    double x2 = bounds.x + (x * dx) + halfX;
                    double y2 = bounds.y + ((y + 1) * dx) + halfY;

                    g.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
                }
                if ((lineData[x][y].x == 4) || (lineData[x][y].y == 4))
                {
                    double x1 = bounds.x + (x * dx) + halfX;
                    double y1 = bounds.y + (y * dx) + halfY;

                    double x2 = bounds.x + ((x - 1) * dx) + halfX;
                    double y2 = bounds.y + (y * dx) + halfY;

                    g.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
                }
            }
        }

        g.setColor(preColor);
        g.setStroke(preStroke);
    }

    /**
     * Gets the location of an image which is used by this.
     *
     * @param cellValue the cell value
     *
     * @return the image location
     *
     * @see edu.rpi.phil.legup.PuzzleModule#getImageLocation(int)
     */
    public String getImageLocation(int cellValue)
    {
        return "images/masyu/" + Integer.valueOf(cellValue).toString() + ".gif";
    }

    /**
     * Initializes the board, does nothing at this point.
     *
     * @param state the state
     *
     * @see edu.rpi.phil.legup.PuzzleModule#initBoard(edu.rpi.phil.legup.BoardState)
     */
    /**
     * Get all the images (as strings to the image path) used by this puzzle in the center part.
     *
     * @return an array of strings to image paths
     */
    public BoardImage[] getAllCenterImages()
    {
        BoardImage[] s = new BoardImage[48];
        {
            for (int x = 0; x < 48; ++x)
            {
                s[x] = new BoardImage("images/masyu/" + Integer.valueOf(x).toString() + ".gif", x);
            }

        }

        return s;
    }

    /**
     * Get all the images (as strings to the image path) used by this puzzle in the border part.
     *
     * @return an array of strings to image paths
     */
    public BoardImage[] getAllBorderImages()
    {
        BoardImage[] s = new BoardImage[0];
        return s;
    }

    /**
     * Get the next label value if we're at this one (like the numbers around the border)
     * This is used when we're creating puzzles.
     *
     * @param curValue the current value of the label
     *
     * @return the next value of the label
     */
    public int getNextLabelValue(int curValue)
    {
        return 0;
    }

    /**
     * Gets the next cell value when creating puzzles.
     *
     * @param y the y
     * @param x the x
     * @param boardState the board state
     *
     * @return the absolute next cell value
     *
     * @see edu.rpi.phil.legup.PuzzleModule#getAbsoluteNextCellValue(int, int, edu.rpi.phil.legup.BoardState)
     */
    public int getAbsoluteNextCellValue(int x, int y, BoardState boardState)
    {
        //TODO: Break apart, comment, figure out perhaps how to optimize
        //also, perhaps if in the previous cell already have a line prevent removal
        int contents = boardState.getCellContents(x, y);
        return BLACK;
    }

    public int getNextCellValue(int x, int y, BoardState boardState)
    {
        int contents = boardState.getCellContents(x, y);
        return BLACK;
    }

    /**
     * Checks to see if the current board can result in the goal.
     *
     * @param currentBoard the current board
     * @param goalBoard the goal board
     *
     * @return true, if check goal
     *
     * @see edu.rpi.phil.legup.PuzzleModule#checkGoal(edu.rpi.phil.legup.BoardState, edu.rpi.phil.legup.BoardState)
     */
    public boolean checkGoal(BoardState currentBoard, BoardState goalBoard)
    {
        //TODO: Multiple solutions?
        return currentBoard.compareBoard(goalBoard);
    }

    /**
     * Gets a list of all the rules.
     *
     * @return the rules
     *
     * @see edu.rpi.phil.legup.PuzzleModule#getRules()
     */
    public Vector<PuzzleRule> getRules()
    {
        Vector<PuzzleRule> ruleList = new Vector<PuzzleRule>();
        //ruleList.add(new RuleFinishPath());
        //ruleList.add(new RuleBlackEdge());
        //ruleList.add(new RuleWhiteEdge());
        //ruleList.add(new RuleNearWhite());
        //ruleList.add(new RuleOnlyOneChoice());
        return ruleList;
    }

    /**
     * Gets a list of Contradictions associated with this puzzle.
     *
     * @return A Vector of Contradictions
     */
    public Vector<Contradiction> getContradictions()
    {
        Vector<Contradiction> contradictionList = new Vector<Contradiction>();
        //contradictionList.add(new ContradictionOnly2());
        //contradictionList.add(new ContradictionWhite());
        //contradictionList.add(new ContradictionBlack());
        //contradictionList.add(new ContradictionBadLooping());
        //contradictionList.add(new ContradictionNoOptions());
        return contradictionList;
    }

    /**
     * Gets list of Case Rules for this project.
     *
     * @return the case rules
     *
     * @see edu.rpi.phil.legup.PuzzleModule#getCaseRules()
     */
    public Vector<CaseRule> getCaseRules()
    {
        Vector<CaseRule> caseRules = new Vector<CaseRule>();
        //caseRules.add(new CaseWhiteSplit());
        //caseRules.add(new CaseBlackSplit());
        //caseRules.add(new CaseNormalSplit());
        return caseRules;
    }

    /**
     * Checks to see if the current board state is valid.
     *
     * @param boardState the board state
     * @return true, if check valid board state
     * @see edu.rpi.phil.legup.PuzzleModule#checkValidBoardState(edu.rpi.phil.legup.BoardState)
     */
    public boolean checkValidBoardState(BoardState boardState)
    {
        int height = boardState.getHeight();
        int width = boardState.getWidth();
        //TODO - write the code to check this

        return (height + width) > 0;
    }

    public void drawCell(Graphics2D g, int x, int y, int state)
    {
        // draws the components of a cell based on bitwise flags
        int sx = cellSize.width * (x + 1);
        int sy = cellSize.height * (y + 1);
        // draw pearls
        if (BLANK != state)
        {
            g.setColor((BLACK == state) ? Color.black : Color.white);
            g.fillOval(sx + 1, sy + 1, cellSize.width - 2, cellSize.height - 2);
            g.setColor(Color.black);
            g.drawOval(sx + 1, sy + 1, cellSize.width - 2, cellSize.height - 2);
        }
    }
}
