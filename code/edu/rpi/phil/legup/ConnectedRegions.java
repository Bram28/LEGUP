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
		Set<Integer> boundryCells = new HashSet<Integer>();
		boundryCells.add(boundryCell);
		return getConnectedRegions(boundryCells, cells, width, height);
	}
	public static List<Set<Point>> getConnectedRegions(Set<Integer> boundryCells, int[][] cells, int width, int height)
	{
		boolean[][] visited = new boolean[height][width];
		List<Set<Point>> results = new ArrayList<Set<Point>>();
		for(int y=0; y<height; y++)
		{
			for(int x=0; x<width; x++)
			{
				Set<Point> region = floodfill(boundryCells, cells, visited, width, height, x, y);
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
	public static Set<Point> getRegionAroundPoint(Point p, int boundryCell, int[][] cells, int width, int height) {
		Set<Integer> boundryCells = new HashSet<Integer>();
		boundryCells.add(boundryCell);
		return getRegionAroundPoint(p, boundryCells, cells, width, height);
	}
	public static Set<Point> getRegionAroundPoint(Point p, Set<Integer> boundryCells, int[][] cells, int width, int height) {
		return floodfill(boundryCells, cells, new boolean[height][width], width, height, p.x, p.y);
	}
	private static Set<Point> floodfill(Set<Integer> boundryCells, int[][] cells, boolean[][] visited, int w, int h, int x, int y)
	{
		HashSet<Point> result = new HashSet<Point>();
		if((x < 0) || (x >= w)) { return result; }
		if((y < 0) || (y >= h)) { return result; }
		if(!visited[y][x] && (!boundryCells.contains(cells[y][x])))
		{
			result.add(new Point(x, y));
			visited[y][x] = true;
			for(int delta=-1; delta<2; delta+=2)
			{
				result.addAll(floodfill(boundryCells, cells, visited, w, h, x+delta, y));
				result.addAll(floodfill(boundryCells, cells, visited, w, h, x, y+delta));
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
	(int[] 2 0 1 0)
	(int[] 0 1 0 0)
	(int[] 1 0 0 0)
))
(define regions (cr:getConnectedRegions 1 testcells 4 4))
regions
(map (cut cr:regionContains 2 testcells <>) (list (regions 0) (regions 1)))

*/
