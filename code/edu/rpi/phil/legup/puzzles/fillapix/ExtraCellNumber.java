package edu.rpi.phil.legup.puzzles.fillapix;

import java.awt.Point;
import java.util.ArrayList;

import edu.rpi.phil.legup.BoardState;

public class ExtraCellNumber
{

	private Point loc;
	private int val;

	public ExtraCellNumber()
	{
		loc = new Point(0, 0);
		val = 0;
	}

	public Point getPoint()
	{
		return loc;
	}

	public int getVal()
	{
		return val;
	}

	public void setPoint(Point p)
	{
		loc = p;
	}
	public void setVal(int v)
	{
		val = v;
	}
	public boolean valid(BoardState state)
	{
		int numCells = getNeighbouringCells(state).size();
		int white = getWhiteCells(state).size(), black = getBlackCells(state).size();

		return (black <= val && white <= numCells-val);
	}

	private ArrayList<Point> getNeighbouringCells(BoardState state)
	{
		ArrayList<Point> list = new ArrayList<Point>();
		for (int x = loc.x-1; x <= loc.x+1; x++) if (x >= 0 && x < state.getWidth())
			for (int y = loc.y-1; y <= loc.y+1; y++) if (y >= 0 && y < state.getHeight())
				list.add(new Point(x, y));

		return list;
	}

	public ArrayList<Point> getWhiteCells(BoardState state)
	{
		return _filter(state, Fillapix.EMPTY);
	}
	public ArrayList<Point> getBlackCells(BoardState state)
	{
		return _filter(state, Fillapix.FILLED);
	}
	public ArrayList<Point> getUnknownCells(BoardState state)
	{
		return _filter(state, Fillapix.UNKNOWN);
	}

	private ArrayList<Point> _filter(BoardState state, int cellVal)
	{
		ArrayList<Point> result = new ArrayList<Point>();
		for (Point p : getNeighbouringCells(state)) if (state.getCellContents(p.x, p.y) == cellVal) result.add(p);
		return result;
	}

	public ArrayList<Point> getForcedFills(BoardState state)
	{
		ArrayList<Point> potReturn = getUnknownCells(state);
		if (val == potReturn.size()+getBlackCells(state).size()) return potReturn;
		else return new ArrayList<Point>();
	}
	public ArrayList<Point> getForcedWhite(BoardState state)
	{
		int numCells = getNeighbouringCells(state).size();
		ArrayList<Point> potReturn = getUnknownCells(state);
		if (numCells-val == potReturn.size()+getWhiteCells(state).size()) return potReturn;
		else return new ArrayList<Point>();
	}

	private static boolean adjacencyRuleSatisfied(ExtraCellNumber X, ExtraCellNumber Y, BoardState state)
	{
		int x = Math.abs(X.getPoint().x - Y.getPoint().x), y = Math.abs(Y.getPoint().y - X.getPoint().y);

		if (x > 2 || y > 2) return false;

		int goal = Y.getVal()-X.getVal();

		ArrayList<Point> temp = Y.getNeighbouringCells(state);
		ArrayList<Point> xcells = X.getNeighbouringCells(state);
		ArrayList<Point> ycells = new ArrayList<Point>(temp);
		ycells.removeAll(xcells); xcells.removeAll(temp);

		int count = ycells.size();

		for (Point point : ycells) if (state.getCellContents(point.x, point.y) == Fillapix.EMPTY) if (--count < goal) return false;
		for (Point point : xcells) if (state.getCellContents(point.x, point.y) == Fillapix.FILLED) if (--count < goal) return false;

		return (count == goal);
	}
	public static ArrayList<Point> adjacencyTestBlack(ExtraCellNumber A, ExtraCellNumber B, BoardState state)
	{
		ExtraCellNumber X = ((A.getVal() > B.getVal()) ? B : A);
		ExtraCellNumber Y = ((X == A) ? B : A);

		if (adjacencyRuleSatisfied(X, Y, state))
		{
			ArrayList<Point> result = Y.getNeighbouringCells(state);
			result.removeAll(X.getNeighbouringCells(state));

			for (int i = 0; i < result.size(); i++)
				if (state.getCellContents(result.get(i).x, result.get(i).y) != Fillapix.UNKNOWN)
					result.remove(i--);

			return result;
		}
		else return new ArrayList<Point>();
	}
	public static ArrayList<Point> adjacencyTestWhite(ExtraCellNumber A, ExtraCellNumber B, BoardState state)
	{
		ExtraCellNumber X = ((A.getVal() > B.getVal()) ? B : A);
		ExtraCellNumber Y = ((X == A) ? B : A);

		if (adjacencyRuleSatisfied(X, Y, state))
		{
			ArrayList<Point> result = X.getNeighbouringCells(state);
			result.removeAll(Y.getNeighbouringCells(state));

			for (int i = 0; i < result.size(); i++)
				if (state.getCellContents(result.get(i).x, result.get(i).y) != Fillapix.UNKNOWN)
					result.remove(i--);

			return result;
		}
		else return new ArrayList<Point>();
	}

}