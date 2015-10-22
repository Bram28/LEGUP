/**
 *  Legup.java
 **/

package edu.rpi.phil.legup;

import java.awt.Image;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

import javax.swing.JOptionPane;

import edu.rpi.phil.legup.editor.SaveableBoardState;
import edu.rpi.phil.legup.newgui.LEGUP_Gui;
import edu.rpi.phil.legup.newgui.Tree;
import edu.rpi.phil.legup.newgui.TreeSelectionListener;
import edu.rpi.phil.legup.newgui.BoardDataChangeListener;
import edu.rpi.phil.legup.newgui.TransitionChangeListener;
import edu.rpi.phil.legup.saveable.SaveableProof;


//TODO system l&f
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.UIManager;

/**
 * The <code>Legup</code> class is the main class for the Legup Application. It
 * provides access to all the major data structures and functionality necessary.
 *
 * @author Drew Housten & Stan Bak
 * @version 1.0
 */
public class Legup
{
	private static Legup instance = null;

	public static ArrayList<BoardDataChangeListener> boardDataChangeListeners = new ArrayList<BoardDataChangeListener>();
	public static ArrayList<TransitionChangeListener> transitionChangeListeners = new ArrayList<TransitionChangeListener>();

	public static void renewListeners()
	{
		Legup legup = getInstance();
		boardDataChangeListeners.clear();
		transitionChangeListeners.clear();
		legup.getSelections().clearTreeSelectionListeners();
		BoardState.addCellChangeListener(legup.getGui().getBoard());
		BoardState.addCellChangeListener(legup.getGui().getJustificationFrame());
		BoardState.addCellChangeListener(legup.getGui().getTree());
		BoardState.addTransitionChangeListener(legup.getGui().getTree());
		BoardState.addTransitionChangeListener(legup.getGui().getTree().treePanel);
		legup.getSelections().addTreeSelectionListener(legup.getGui().getJustificationFrame());
		legup.getSelections().addTreeSelectionListener(legup.getGui());
		legup.getSelections().addTreeSelectionListener(legup.getGui().getTree());
		legup.getSelections().addTreeSelectionListener(legup.getGui().getTree().treePanel);
	}
	/**
	 * Legup is a singleton. Get the instance of legup
	 *
	 * @return the Legup instance, creating if necessary
	 */
	public static Legup getInstance()
	{
		if (instance == null)
			instance = new Legup();

		return instance;
	}
	public LEGUP_Gui getGui()
	{
		return this.gui;
	}

	public static InputStream getResource(String name)
	{
		InputStream res = null;
		res = Legup.class.getResourceAsStream('/' + name);
		if(res == null)
		{
			try { res = new FileInputStream(new File(name)); }
			catch(IOException e) { throw new RuntimeException(e); }
		}
		return res;
	}

	private Config config = new Config(getResource("config.xml"));

	/**
	 * Returns the <code>Config</code> object so that the calling code can have
	 * access to any configuration information
	 *
	 * @return Configuration object
	 */
	public Config getConfig()
	{
		return config;
	}

/*
 * BoardState methods
 */

	private BoardState initialBoardState = null;

	/**
	 * Gets the initial board state. This is the start step of the proof.
	 *
	 * @return Initial Board State
	 */
	public BoardState getInitialBoardState()
	{
		return this.initialBoardState;
	}
	public void setInitialBoardState(BoardState state)
	{
		this.initialBoardState = state;
	}

	public static BoardState getCurrentState()
	{
		return Legup.getInstance().getSelections().getFirstSelection().getState();
	}

	public static void setCurrentState(BoardState b)
	{
		Legup.getInstance().getSelections().setSelection(new Selection(b,false));
	}

	public void loadRandomBoard(String puzzle)
	{
		Vector<String> allBoards = config.getBoardsForPuzzle(puzzle);
		Random random = new Random();
		String filename = allBoards.get(random.nextInt(allBoards.size()));
		System.out.println(filename + " selected");
		loadBoardFile(filename);
	}

	public void setWindowTitle(String filename, String puzzleName) {
		// Set window title to "puzzle_type - filename (w/o path or extension)"
		String fileNameNoExt = filename;
		if (filename.substring(filename.length()-4).equals(".xml")) {
			fileNameNoExt = filename.substring(0, filename.length()-4);
		} else if (filename.substring(filename.length()-6).equals(".proof")) {
			fileNameNoExt = filename.substring(0, filename.length()-6);
		}
		String[] filePath = fileNameNoExt.split("/");
		gui.setTitle(puzzleName + " - " + filePath[filePath.length-1]);
	}

	public void loadBoardFile(String filename)
	{
		BoardState b = null;
		System.out.println("Loading board: " + filename);

		//Try to load the file
		try
		{
			b = SaveableBoardState.loadState(filename);
		}
		catch (Exception e)
		{
			initialBoardState = null;
			errorMessage("Error loading board file:" + e.toString());
			return;
		}

		if(b != null)
		{
			String puzzle = b.getPuzzleName();
			System.out.println("Loading puzzle module: " + puzzle);

			if(loadPuzzleModule(puzzle)) {
				setWindowTitle(filename, puzzle);
				this.initialBoardState = b;
				setCurrentState(b);
				gui.reloadGui();
				getGui().getTree().treePanel.reset();
			}
		}
	}

	public void loadAssignmentFile(String filename)
	{
		//TODO: Assignment File loading
	}

	public void loadProofFile(String filename)
	{
		try
		{
			initialBoardState = SaveableProof.loadProof(filename);
			selections = new Selections();
			BoardState b = initialBoardState;
			if(b!=null)while(b.getChildren().size() > 0)b = b.getChildren().get(b.getChildren().size()-1);
			setCurrentState(b);
		}
		//catch (NullPointerException e1) {}
		catch (Exception e)
		{
			initialBoardState = null;
			errorMessage("Error loading proof: " + e.toString());
			return;
		}

		String puzzle = initialBoardState.getPuzzleName();
		if(puzzle == null)return;
		System.out.println("Loading puzzle module: " + puzzle);
		if(loadPuzzleModule(puzzle)) {
			setWindowTitle(filename, puzzle);
			gui.reloadGui();
			Tree.colorTransitions();
		}
	}

	/*
	 * PuzzleModule methods
	 */

	private PuzzleModule puzzleModule = null;

	/**
	 * Gets the active Puzzle Module. Each type of puzzle will have its own
	 * Puzzle Module which will provide support for puzzle-specific
	 * functionality
	 *
	 * @return The active Puzzle Module
	 */
	public PuzzleModule getPuzzleModule()
	{
		//if(puzzleModule == null){System.out.println("bad");}
		return this.puzzleModule;
	}

	/**
	 * @param moduleName
	 * @return a boolean indicating success (true means success)
	 */
	public boolean loadPuzzleModule(String moduleName)
	{
		renewListeners();
		// Load the puzzle
		try
		{
			String className = config.getPuzzleClassForName(moduleName);
			if(className == null)
			{
				errorMessage(String.format("Unsupported puzzle type: %s", moduleName));
				return false;
			}
			puzzleModule = (PuzzleModule) (Class.forName(className).newInstance());
			puzzleModule.name = moduleName;
		}
		catch(Exception e)
		{
			errorMessage("PuzzleModule load error: " + e.toString());
			return false;
		}
		Tree tree = getGui().getTree();
		tree.modifiedSinceSave = false;
		tree.modifiedSinceUndoPush = false;
		tree.undoStack.clear();
		tree.redoStack.clear();
		tree.tempSuppressUndoPushing = false;
		//tree.pushUndo();
		tree.origInitState = SaveableProof.stateToBytes(getInitialBoardState());
		return true;
	}

	/**
	 *	Method used for Puzzle Generators - no I/O interaction necessary
	 */
	public void initializeGeneratedPuzzle(PuzzleModule module, BoardState init)
	{
		puzzleModule = module;
		initialBoardState = init;
		selections.setSelection(new Selection(initialBoardState, false));
		gui.reloadGui();
	}

	private Selections selections = new Selections();

	public Selections getSelections()
	{
		return selections;
	}

	/**
	 * Prints an error message to a log, console, and GUI
	 * @param message The error to display
	 */
	public void errorMessage(String message)
	{
		if (gui != null)
		{
			gui.errorEncountered(message);
		}
		System.out.println(message);
	}

	// The GUI used - modified by Daniel for explicitness
	private LEGUP_Gui gui = null;
	private Login login = null;
	private String user = null;
	private String[] admins = {"heuveb"};

	public String getUser()
	{
		return user;
	}

	public void setUser(String user)
	{
		this.user = user;
	}

	public String[] getAdmins()
	{
		return this.admins;
	}

	public void refresh()
	{
		gui.repaintBoard();
	}

	/**
	 * Starts up the program and the GUI
	 */
	public static void main(String[] args)
	{
		GlobalPopupExceptionHandler.registerExceptionHandler();
		Legup legup = Legup.getInstance();

		// TODO system look & feel
		// Set System L&F
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch( Exception e ){}

		legup.login = new Login(legup);
		//legup.login.promptLogin();
		legup.gui = new LEGUP_Gui(legup);

		//legup.gui.promptPuzzle();

		// This is for the animation - Daniel P
		if (BoardDrawingHelper.ANIMATE_SPLIT_CASE) {
			legup.selections.addTreeSelectionListener(new TreeSelectionListener() {
				private Thread myThread = null;
				public void treeSelectionChanged(ArrayList<Selection> newSelections) {
					/*if (myThread != null)
					{
						myThread.stop();
						myThread = null;
					}*/
					if (newSelections.size() >= 1 && !newSelections.get(0).isState())
					{
						myThread = new Thread()
						{
							public void run()
							{
								while (true)
								{
									try { Thread.sleep(150); } catch (InterruptedException ie) { }
									Legup.getInstance().gui.repaintBoard();
								}
							}
						};
						myThread.start();
					}
				}
			});
		}
	}
}
