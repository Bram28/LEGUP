package edu.rpi.phil.legup;

import edu.rpi.phil.legup.puzzles.sudoku.Sudoku;
import edu.rpi.phil.legup.puzzles.fillapix.Fillapix;

import javax.swing.JFrame;

public class PuzzleGeneration
{

	public static final int EASY = 0, NORMAL = 1, HARD = 2, OPTIMAL = 3, UNSOLVEABLE = 4;
	public static final String[] validPuzzles = {"Sudoku", "Fillapix"};
	public static final String[] difficulties = {"Easy: Simple Logic", "Medium: Advanced Logic",
																 "Hard: Limited Deduction", "Optimal: As Impossible as Possible"};

	private PuzzleGeneration()
	{
		throw new InternalError("DON'T $%#! INSTANTIATE ME!!!");
	}

	public static PuzzleModule getModule(String name)
	{
		if (name.equals("Sudoku")) return new Sudoku();
		else if (name.equals("Fillapix")) return new Fillapix();
		return null;
	}

	public static BoardState makePuzzle(String name, int difficulty, JFrame loadingHost)
	{
		return getModule(name).generatePuzzle(difficulty, loadingHost);
	}

}