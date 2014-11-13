package edu.rpi.phil.legup;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConnectedRegions
{
	public static List<Set<Point>> getConnectedRegions(int boundryCell, int[][] cells, int width, int height)
	{
		boolean[][] visited = new boolean[height][width];
		List<Set<Point>> results = new ArrayList<Set<Point>>();
		for(int y=0; y<height; y++)
		{
			for(int x=0; x<width; x++)
			{
				Set<Point> region = floodfill(boundryCell, cells, visited, width, height, x, y);
				if(region.size() > 0) { results.add(region); }
			}
		}
		return results;
	}
	public static boolean regionContains(int toFind, int[][] cells, Set<Point> region)
	{
		for(Point p : region) { if(cells[p.y][p.x] == toFind) { return true; } }
		return false;
	}
	private static Set<Point> floodfill(int boundryCell, int[][] cells, boolean[][] visited, int w, int h, int x, int y)
	{
		HashSet<Point> result = new HashSet<Point>();
		if((x < 0) || (x >= w)) { return result; }
		if((y < 0) || (y >= h)) { return result; }
		if(!visited[y][x] && (cells[y][x] != boundryCell))
		{
			result.add(new Point(x, y));
			visited[y][x] = true;
			for(int delta=-1; delta<2; delta+=2)
			{
				result.addAll(floodfill(boundryCell, cells, visited, w, h, x+delta, y));
				result.addAll(floodfill(boundryCell, cells, visited, w, h, x, y+delta));
			}
		}
		return result;
	}
}

/*
; Tests (in kawa)
(define cr edu.rpi.phil.legup.ConnectedRegions)
(define testcells (int[][]
	(int[] 0 1 0 0)
	(int[] 0 0 1 0)
	(int[] 0 1 0 0)
	(int[] 1 0 0 0)
))
(define regions (cr:getConnectedRegions 1 testcells 4 4))
*/
