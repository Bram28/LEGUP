package edu.rpi.phil.legup.editor;

import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.Legup;
import edu.rpi.phil.legup.PuzzleModule;
import edu.rpi.phil.legup.newgui.PickGameDialog;

public class FillapixPuzzleEditor extends JFrame implements ActionListener
{
	private static final long serialVersionUID = -2304281047341398965L;

	JMenuItem save, newPuzzle, editPuzzle, exit, closePuzzle, editExtra, importPuzzle, batchImport;
	final FileDialog fileChooser;
	Legup lm = Legup.getInstance();
	PickGameDialog pgd = null;
	FillapixEditorBoardFrame initialBoard = null;
	String puzzleModuleName = null;

	public FillapixPuzzleEditor()
	{
		fileChooser = new FileDialog(this);
		createMenuBar();

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(200,65);

		setTitle("Puzzle Editor");
		setLocation(500-getWidth() / 2, 200);

		pgd = new PickGameDialog(this,lm,false);
	}

	private void createMenuBar()
	{
		JMenuBar b = new JMenuBar();
		JMenu file = new JMenu("File");
		b.add(file);

		newPuzzle = new JMenuItem("New Puzzle");
		newPuzzle.addActionListener(this);
		file.add(newPuzzle);

		editPuzzle = new JMenuItem("Open Puzzle...");
		editPuzzle.addActionListener(this);
		file.add(editPuzzle);

		file.addSeparator();

		closePuzzle = new JMenuItem("Close Puzzle");
		closePuzzle.addActionListener(this);
		file.add(closePuzzle);

		file.addSeparator();

		importPuzzle = new JMenuItem("Import Puzzle");
		importPuzzle.addActionListener(this);
		file.add(importPuzzle);

		batchImport = new JMenuItem("Batch Convert...");
		batchImport.addActionListener(this);
		file.add(batchImport);

		file.addSeparator();

		save = new JMenuItem("Save Puzzle");
		save.addActionListener(this);
		file.add(save);

		file.addSeparator();

		editExtra = new JMenuItem("Edit Extra Data");
		editExtra.addActionListener(this);
		file.add(editExtra);

		file.addSeparator();

		exit = new JMenuItem("Exit");
		exit.addActionListener(this);
		file.add(exit);

		setJMenuBar(b);
	}

	public static void main(String[] args)
	{
		new FillapixPuzzleEditor().setVisible(true);
	}

	/**
	 * Get the dimensions of the new puzzle or null if they cancel
	 * @return the dimensions of the puzzle the user inputs or null if they cancel
	 */
	public Dimension getNewPuzzleDimensions()
	{
		Dimension rv = null;
		int w, h;

		w = getPositiveInt("Width:");

		if (w != -1)
		{
			h = getPositiveInt("Height:");

			if (h != -1)
				rv = new Dimension(w,h);
		}


		return rv;
	}

	/**
	 * Get a positive integer from the user
	 * @return
	 */
	private int getPositiveInt(String prompt)
	{
		int rv = -1;

		while (rv == -1)
		{
			String temp_input= JOptionPane.showInputDialog(prompt);

			if (temp_input == null)
				break;

			try
			{
				rv = Integer.parseInt(temp_input);

				if (rv <= 0)
				{
					rv = -1;
					JOptionPane.showMessageDialog(null, temp_input + " is invalid.");
				}
			}
			catch (NumberFormatException e)
			{
				JOptionPane.showMessageDialog(null, temp_input + " is not an interger.");
			}
		}

		return rv;
	}

	public void closePuzzle()
	{
		if (initialBoard == null)
		{
			JOptionPane.showMessageDialog(null,"You don't have a puzzle open.");
		}
		else
		{
			initialBoard.setVisible(false);
			initialBoard = null;
		}
	}

	/// listeners
	public void actionPerformed(ActionEvent e)
	{
		Object c = e.getSource();

		if (c == exit)
			System.exit(0);
		else if (c == closePuzzle)
		{
			closePuzzle();
		}
		else if (c == newPuzzle)
		{
			if (initialBoard != null) // we already have a board open
			{
				JOptionPane.showMessageDialog(null,"You already have a puzzle open.");
			}
			else
			{
				pgd.setVisible(true);

				if (pgd.okPressed)
				{
					puzzleModuleName = pgd.getGame();
					boolean error = lm.loadPuzzleModule(puzzleModuleName);

					if (!error)
					{
						PuzzleModule pm = lm.getPuzzleModule();
						Dimension d = pm.getForcedDimension();

						if (d == null) // no forced dimensions
							d = getNewPuzzleDimensions();

						if (d != null) // they didn't cancel
						{
							BoardState i = new BoardState(d.width,d.height);

							pm.initBoard(i);

							initialBoard = new FillapixEditorBoardFrame(i, pm,this);
							initialBoard.setVisible(true);
						}
					}
					else
						JOptionPane.showMessageDialog(null,"Error loading puzzle module " + puzzleModuleName);
				}
			}
		}
		else if (c == editPuzzle)
		{
			if (initialBoard != null) // we already have a board open
			{
				JOptionPane.showMessageDialog(null, "You already have a puzzle open.");
			}
			else
			{
				fileChooser.setMode(FileDialog.LOAD);
				fileChooser.setTitle("Select Puzzle");
				fileChooser.setVisible(true);
				String filename = fileChooser.getFile();
				//String dir = fileChooser.getDirectory();

				if (filename != null) // user didn't pressed cancel
				{
					filename = fileChooser.getDirectory() + filename;
					// load puzzle
					BoardState s = SaveableBoardState.loadState(filename);

					if (s != null)
					{
						puzzleModuleName = SaveableBoardState.getLastLoadedPuzzleModule();
						boolean error = lm.loadPuzzleModule(puzzleModuleName);

						if (!error)
						{
							PuzzleModule pm = lm.getPuzzleModule();

							initialBoard = new FillapixEditorBoardFrame(s, pm,this);
							initialBoard.setVisible(true);
						}
						else
							JOptionPane.showMessageDialog(null,"Error loading puzzle module " + puzzleModuleName);
					}
				}
			}
		}
		else if (c == save)
		{ // save the current puzzle

			if (initialBoard == null)
			{
				JOptionPane.showMessageDialog(null,"You don't have a puzzle open.");
			}
			else
			{
				fileChooser.setMode(FileDialog.SAVE);
				fileChooser.setTitle("Save As...");
				fileChooser.setVisible(true);
				String filename = fileChooser.getFile();
				//String dir = fileChooser.getDirectory();

				if (filename != null) // user didn't pressed cancel
				{
					filename = fileChooser.getDirectory() + filename;
					// save puzzle
					SaveableBoardState.saveState(initialBoard.curState,puzzleModuleName, filename);
				}
			}
		}
		else if(c == editExtra)
		{
			if (initialBoard == null)
			{
				JOptionPane.showMessageDialog(null,"You don't have a puzzle open.");
			}
			else
			{
				//initialBoard.pm.editExtraData(initialBoard.curState, this);
			}
		}
		else if(c == importPuzzle)
		{
			if (initialBoard != null) // we already have a board open
			{
				JOptionPane.showMessageDialog(null,"You already have a puzzle open.");
			}
			else
			{
				pgd.setVisible(true);

				if (pgd.okPressed)
				{
					puzzleModuleName = pgd.getGame();
					boolean error = lm.loadPuzzleModule(puzzleModuleName);

					if (!error)
					{
						PuzzleModule pm = lm.getPuzzleModule();

						//Ask for a file to open
						fileChooser.setMode(FileDialog.LOAD);
						fileChooser.setTitle("Select Puzzle");
						fileChooser.setVisible(true);
						String file = fileChooser.getFile();
						//String dir = fileChooser.getDirectory();

						if (file != null) // user didn't pressed cancel
						{
							file = fileChooser.getDirectory() + file;

							BoardState i = pm.importPuzzle(file);
							if(i != null)
							{
								initialBoard = new FillapixEditorBoardFrame(i, pm,this);
								initialBoard.setVisible(true);
							}
							else
								JOptionPane.showMessageDialog(null,"Error loading puzzle " + file);
						}
					}
					else
						JOptionPane.showMessageDialog(null,"Error loading puzzle module " + puzzleModuleName);
				}
			}
		}
		else if(c == batchImport)
		{
			pgd.setVisible(true);

			if (pgd.okPressed)
			{
				puzzleModuleName = pgd.getGame();
				boolean error = lm.loadPuzzleModule(puzzleModuleName);

				if (!error)
				{
					PuzzleModule pm = lm.getPuzzleModule();

					//Ask for a directory to open
					fileChooser.setMode(FileDialog.LOAD);
					fileChooser.setTitle("Select Puzzle");
					fileChooser.setVisible(true);
					String dirName = fileChooser.getDirectory();

					if (dirName != null) // user didn't pressed cancel
					{
						File dir = new File(dirName);

						File[] files = dir.listFiles( );
						for(int i = 0; i < files.length; ++i)
						{
							if(!files[i].isDirectory( ) && files[i].getName( ).endsWith( ".txt" ))
							{
								BoardState s = pm.importPuzzle(dirName + files[i].getName( ));
								if(s != null)
								{
									//Save the puzzle
									SaveableBoardState.saveState(s,puzzleModuleName, dirName + files[i].getName( ).substring( 0, files[i].getName( ).length( ) - 4 ) + ".xml");
								}
								else
									JOptionPane.showMessageDialog(null,"Error loading puzzle " + files[i]);
							}
						}
					}
				}
				else
					JOptionPane.showMessageDialog(null,"Error loading puzzle module " + puzzleModuleName);
			}
		}
	}


	/**
	 * Sets the visibility of the normal editor
	 * @param b the value to set the editor's visibility to
	 */
	public void setEditorVisible(boolean b)
	{
		initialBoard.setVisible(b);
	}
}
