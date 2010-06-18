package edu.rpi.phil.legup.newgui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

import javax.swing.SwingConstants;

import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.Legup;
import edu.rpi.phil.legup.PuzzleModule;
import edu.rpi.phil.legup.PuzzleGeneration;
import edu.rpi.phil.legup.Selection;
import edu.rpi.phil.legup.saveable.SaveableProof;
import edu.rpi.phil.legup.ILegupGui;

//import edu.rpi.phil.legup.newgui.TreeFrame;
import javax.swing.plaf.ToolBarUI;
import javax.swing.plaf.basic.BasicToolBarUI;
import java.awt.Color;
import java.awt.Point;

public class LEGUP_Gui extends JFrame implements ActionListener, InternalFrameListener, TreeSelectionListener, ILegupGui
{
	private static final long serialVersionUID = -2304281047341398965L;

	/**
	 *	Daniel Ploch - Added 09/25/2009
	 * Integrated variables for different Proof Modes in LEGUP
	 *	The PROOF_CONFIG environment variable stores the settings as bitwise flags
	 *
	 *	AllOW_JUST:		Allows the user to use the Justification Panel to verify answers.
	 *	ALLOW_HINTS:	Allow the user to query the tutor for hints (no "Oops - I gave you the answer" step)
	 *	ALLOW_DEFAPP:	Allow the user to use default-applications (have the AI auto-infer parts of the solution)
	 *	ALLOW_FULLAI:	Gives user full access to the AI menu, including use of the AI solving algorithm (includes "Oops" tutor step).
	 *	REQ_STEP_JUST:	Requires the user to justify (correct not necessary) the latest transition before making a new one (safety/training device)
	 *	IMD_FEEDBACK:	Shows green and red arrows in Proof-Tree for correct/incorrect justifications in real-time.
	 *	INTERN_RO:		Internal nodes (in the Proof-Tree) are Read-Only, only leaf nodes can be modified.  Ideal safety feature
	 *	AUTO_JUST:		AI automatically justifies moves as you make them.
	 */
	private static int CONFIG_INDEX = 0;
	public static final int ALLOW_HINTS = 1,
							ALLOW_DEFAPP = 2,
							ALLOW_FULLAI = 4,
							ALLOW_JUST = 8,
							REQ_STEP_JUST = 16,
							IMD_FEEDBACK = 32,
							INTERN_RO = 64,
							AUTO_JUST = 128;
	public static boolean profFlag(int flag)
	{
		return !((PROF_FLAGS[CONFIG_INDEX] & flag) == 0);
	}

	private static final String[] PROFILES = {
		"No Assistance",
		"Rigorous Proof",
		"Casual Proof",
		"Assisted Proof",
		"Guided Proof",
		"Training-Wheels Proof",
		"No Restrictions"};
	private static final int[] PROF_FLAGS =  {
		0,
		ALLOW_JUST | REQ_STEP_JUST,
		ALLOW_JUST,
		ALLOW_HINTS | ALLOW_JUST | AUTO_JUST,
		ALLOW_HINTS | ALLOW_JUST | REQ_STEP_JUST,
		ALLOW_HINTS | ALLOW_DEFAPP | ALLOW_JUST | IMD_FEEDBACK | INTERN_RO,
		ALLOW_HINTS | ALLOW_DEFAPP | ALLOW_FULLAI | ALLOW_JUST};

	private static final int BOARD = 0;
	private static final int JUSTIFICATION = 1;
	private static final int TREE = 2;

	private static final int TOOLBAR_NEW = 0;
	private static final int TOOLBAR_OPEN = 1;
	private static final int TOOLBAR_SAVE = 2;

	private static final int TOOLBAR_UNDO = 3;
	private static final int TOOLBAR_REDO = 4;

	private static final int TOOLBAR_CONSOLE = 5;
	private static final int TOOLBAR_HINT = 6;
	private static final int TOOLBAR_CHECK = 7;
	
	/*private static final int TOOLBAR_BOARD = 5;
	private static final int TOOLBAR_JUSTIFICATION = 6;
	private static final int TOOLBAR_TREE = 7;*/
	

	PickGameDialog pgd = null;
	Legup legupMain = null;

	private JDesktopPane mdiPane = new JDesktopPane();

	private JMenuBar bar = new JMenuBar();

	private JMenu file = new JMenu("File");
		private JMenuItem newPuzzle = new JMenuItem("New Puzzle");
		private JMenuItem genPuzzle = new JMenuItem("Puzzle Generators");
		private JMenuItem openProof = new JMenuItem("Open LEGUP Proof");
		private JMenuItem saveProof = new JMenuItem("Save LEGUP Proof");
		private JMenuItem exit = new JMenuItem("Exit");
	private JMenu edit = new JMenu("Edit");
		private JMenuItem undo = new JMenuItem("Undo");
		private JMenuItem redo = new JMenuItem("Redo");
	private JMenu view = new JMenu("View");
	private JMenu proof = new JMenu("Proof");
	//added by Jacob
	private JMenu AI = new JMenu("AI");
		private JMenuItem Run = new JMenuItem("Run AI to completion");
		private JMenuItem Step = new JMenuItem("Run AI one Step");
		private JMenuItem Test = new JMenuItem("Test AI!");
		private JMenuItem hint = new JMenuItem("Hint");

	private edu.rpi.phil.legup.AI myAI = new edu.rpi.phil.legup.AI();
	//end additions
	private JMenu help = new JMenu("Help");
	

	private final FileDialog fileChooser;

	final static String[] toolBarNames =
	{
		"New",
		"Open",
		"Save",
		"Undo",
		"Redo",
		"Console",
		"Hint",
		"Check"
	};

	AbstractButton[] toolBarButtons =
	{
		new JButton(toolBarNames[0], new ImageIcon("images/" + toolBarNames[0] + ".png")),
		new JButton(toolBarNames[1], new ImageIcon("images/" + toolBarNames[1] + ".png")),
		new JButton(toolBarNames[2], new ImageIcon("images/" + toolBarNames[2] + ".png")),
		new JButton(toolBarNames[3], new ImageIcon("images/" + toolBarNames[3] + ".png")),
		new JButton(toolBarNames[4], new ImageIcon("images/" + toolBarNames[4] + ".png")),
		new JButton(toolBarNames[5], new ImageIcon("images/" + toolBarNames[5] + ".png")),
		new JButton(toolBarNames[6], new ImageIcon("images/" + toolBarNames[6] + ".png")),
		new JButton(toolBarNames[7], new ImageIcon("images/" + toolBarNames[7] + ".png"))
		/*new JToggleButton(new ImageIcon("images/" + toolBarNames[5] + ".png")),
		new JToggleButton(new ImageIcon("images/" + toolBarNames[6] + ".png")),
		new JToggleButton(new ImageIcon("images/" + toolBarNames[7] + ".png")),
		new JToggleButton(new ImageIcon("images/" + toolBarNames[8] + ".png"))*/
	};

	final static int[] toolbarSeperatorBefore =
	{
		3, 5
	};

	private JToolBar toolBar;
	
	// TODO
	JToolBar treet;
	TreePanel treep;
	//private TreeToolbarPanel treeb = new TreeToolbarPanel(this);

	private final static String[] types =
	{
			"Board",
			"Justification",
			"Tree"
	};

	private JCheckBoxMenuItem[] viewItem =
	{
			new JCheckBoxMenuItem(types[0],false),
			new JCheckBoxMenuItem(types[1],false),
			new JCheckBoxMenuItem(types[2],false)
	};

	private JustificationFrame justificationFrame;

	private JInternalFrame[] frames =
	{
			new JInternalFrame(types[0]),
			null, // justification is initialized in construtor
			null // tree is initialized in constructor
	};

	private JCheckBoxMenuItem allowDefault =
		new JCheckBoxMenuItem("Allow Default Rule Applications",false);
	private JMenu proofMode = new JMenu("Proof Mode");
	private JCheckBoxMenuItem[] proofModeItems = new JCheckBoxMenuItem[PROF_FLAGS.length];

	// Modified for access - Daniel P
	private BoardPanel boardPanel = null;
	public void repaintBoard()
	{
		if (boardPanel != null) boardPanel.boardDataChanged(null);
	}

	private Console console;
	public LEGUP_Gui(Legup legupMain)
	{
		
		this.setLayout(new BorderLayout());
		this.add(mdiPane, BorderLayout.CENTER);
		
		mdiPane.setPreferredSize(new Dimension(800,600));
		
		// TODO Console
		console = new Console();
		this.add(console,BorderLayout.SOUTH);
		
		this.legupMain = legupMain;
		legupMain.getSelections().addTreeSelectionListener(this);
		setTitle("LEGUP");

		setupMenu();
		setupToolBar();
		setupFrames();
		pack();

		//Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		/*setLocation(100,50);
		setSize(800,600);*/

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setVisible(true);

		// TODO experimental floating toolbar
		((BasicToolBarUI) console.getUI()).setFloatingLocation(500,500);
		((BasicToolBarUI) console.getUI()).setFloating(true, new Point(500,500));
		((BasicToolBarUI) console.getUI()).setFloatingColor(Color.black);
		((BasicToolBarUI) console.getUI()).setDockingColor(Color.red);

		fileChooser = new FileDialog(this);
	}

	private void setupToolBar()
	{
		toolBar = new JToolBar();
		toolBar.setFloatable( false );
		toolBar.setRollover( true );

		for (int x = 0; x < toolBarButtons.length; ++x){

			for (int y = 0; y < toolbarSeperatorBefore.length; ++y){
				if (x == toolbarSeperatorBefore[y]){
					toolBar.addSeparator();
				}
			}

			toolBar.add(toolBarButtons[x]);
			toolBarButtons[x].addActionListener(this);
			toolBarButtons[x].setToolTipText(toolBarNames[x]);
			// TODO text under icons
			toolBarButtons[x].setVerticalTextPosition( SwingConstants.BOTTOM );
			toolBarButtons[x].setHorizontalTextPosition( SwingConstants.CENTER );
		}

		// TODO disable buttons
		toolBarButtons[TOOLBAR_SAVE].setEnabled(false);
		toolBarButtons[TOOLBAR_UNDO].setEnabled(false);
		toolBarButtons[TOOLBAR_REDO].setEnabled(false);
		toolBarButtons[TOOLBAR_HINT].setEnabled(false);
		toolBarButtons[TOOLBAR_CHECK].setEnabled(false);

		this.add(toolBar, BorderLayout.NORTH);
	}

	private void setupFrames()
	{
		// initialize
		justificationFrame = new JustificationFrame(types[1],this);
		frames[JUSTIFICATION] = justificationFrame;

		frames[TREE] = new TreeFrame(types[TREE],legupMain,this);
		// TODO
		//this.add(new TreePanel(), BorderLayout.EAST);
		
		// TODO float
		treet = new JToolBar("Tree");
		treet.setLayout(new BorderLayout());
		treet.setPreferredSize(new Dimension(150,500));
		
		treep = new TreePanel();
		//treet.add(treeb,BorderLayout.NORTH);
		treet.add(treep);
		
		
		
		
		// end of TODO

		boardPanel = new BoardPanel(this);

		// gui
		//frames[BOARD].setLayout(new BorderLayout());
		//frames[BOARD].add(boardPanel,BorderLayout.CENTER);
		frames[BOARD].add(boardPanel);

		//boardPanel.setLayout(new FlowLayout());

//		 window listeners
		for (int x = 0; x < frames.length; ++x)
		{
			frames[x].addInternalFrameListener(this);
			frames[x].setResizable(true);
			frames[x].setClosable(true);
			frames[x].setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);
			mdiPane.add(frames[x]);
		}
		//mdiPane.setSize(frames[JUSTIFICATION].getWidth(), frames[TREE].getHeight());
	}

	private void setupMenu()
	{
		bar.add(file);
			file.add(newPuzzle);
				newPuzzle.addActionListener(this);
				newPuzzle.setAccelerator(KeyStroke.getKeyStroke('N',2));
			file.add(genPuzzle);
				genPuzzle.addActionListener(this);
			file.addSeparator();
			file.add(openProof);
				openProof.addActionListener(this);
				openProof.setAccelerator(KeyStroke.getKeyStroke('O',2));
			file.add(saveProof);
				saveProof.addActionListener(this);
				saveProof.setAccelerator(KeyStroke.getKeyStroke('S',2));
			file.addSeparator();
			file.add(exit);
				exit.addActionListener(this);

		bar.add(edit);
			edit.add(undo);
				undo.addActionListener(this);
				undo.setAccelerator(KeyStroke.getKeyStroke('Z',2));
			edit.add(redo);
				redo.addActionListener(this);
				redo.setAccelerator(KeyStroke.getKeyStroke('Y',2));

		bar.add(view);
			for (int x = 0; x < viewItem.length; ++x)
			{
				view.add(viewItem[x]);
				viewItem[x].addActionListener(this);
			}

		bar.add(proof);
			proof.add(allowDefault);
				allowDefault.addActionListener(this);
			proof.add(proofMode);
			for (int i = 0; i < PROF_FLAGS.length; i++)
			{
				proofModeItems[i] = new JCheckBoxMenuItem(PROFILES[i], i == CONFIG_INDEX);
				proofModeItems[i].addActionListener(this);
				proofMode.add(proofModeItems[i]);
			}

		bar.add(AI);
			AI.add(Step);
				Step.addActionListener(this);
				Step.setAccelerator(KeyStroke.getKeyStroke("F9"));
			AI.add(Run);
				Run.addActionListener(this);
				Run.setAccelerator(KeyStroke.getKeyStroke("F10"));
			AI.add(Test);
				Test.addActionListener(this);
			AI.add(hint);
				hint.addActionListener(this);
				hint.setAccelerator(KeyStroke.getKeyStroke('h'));

		bar.add(help);

		setJMenuBar(bar);
	}

	private void selectNewPuzzle()
	{
		pgd.setVisible(true);

		if (pgd.okPressed)
		{
			legupMain.loadBoardFile(pgd.getPuzzle());

			PuzzleModule pm = legupMain.getPuzzleModule();

			if (pm != null)
			{
				justificationFrame.setJustifications(pm);

				// AI setup
				myAI.setBoard(pm);
			}

			// show them all
			showAll();
		}
	}

	private void openProof()
	{
		fileChooser.setMode(FileDialog.LOAD);
		fileChooser.setTitle("Select Proof");
		fileChooser.setVisible(true);
		String filename = fileChooser.getFile();

		if (filename != null) // user didn't pressed cancel
		{
			filename = fileChooser.getDirectory() + filename;
			if (!filename.toLowerCase().endsWith(".proof"))
				filename += ".proof";
			Legup.getInstance().loadProofFile(filename);
		}

	}

	private void saveProof()
	{
		BoardState root = legupMain.getInitialBoardState();
		fileChooser.setMode(FileDialog.SAVE);
		fileChooser.setTitle("Select Proof");
		fileChooser.setVisible(true);
		String filename = fileChooser.getFile();

		if (filename != null) // user didn't pressed cancel
		{
			filename = fileChooser.getDirectory() + filename;

			if (!filename.toLowerCase().endsWith(".proof"))
	    		filename = filename + ".proof";

		    SaveableProof.saveProof(root, filename);
		}

	}

	private void checkProof()
	{
		BoardState root = legupMain.getInitialBoardState();
		root.evalDelayStatus();
		repaintAll();

		PuzzleModule pm = legupMain.getPuzzleModule();
		if (pm.checkProof(root))
			JOptionPane.showMessageDialog(null, "Your proof is correct.");
		else
			JOptionPane.showMessageDialog(null, "Your proof is INCORRECT.");
	}



	/*private int buttonIndexToFrameIndex(int buttonIndex)
	{
		return buttonIndex - TOOLBAR_BOARD;
	}

	private int frameIndexToButtonIndex(int frameIndex)
	{
		return TOOLBAR_BOARD + frameIndex;
	}*/

	/**
	 * Show the JFrame cooresponding to the ID (like BOARD or TREE)
	 * @param ID the frame type to show, static member of LEGUP_Gui
	 */
	private void showFrame(int ID)
	{
		if (ID == BOARD)
		{ // location is under menuBar
			boardPanel.initSize();
			frames[ID].setLocation(0,frames[JUSTIFICATION].getHeight());
		}
		else if (ID == JUSTIFICATION)
		{
			frames[ID].setLocation(0,0);
		}
		else if (ID == TREE)
		{
			frames[ID].setLocation(frames[BOARD].getX() + frames[BOARD].getWidth(),frames[BOARD].getY());
		}

		frames[ID].pack();
		frames[ID].setVisible(true);

	}

	private void setFrameState(int index, boolean show)
	{
		viewItem[index].setState(show);

		if (show)
			showFrame(index);
		else
			frames[index].setVisible(false);
	}

	private void showAll() {
		showFrame(JUSTIFICATION);
		showFrame(BOARD);
		showFrame(TREE);
		// TODO disable buttons
		toolBarButtons[TOOLBAR_SAVE].setEnabled(true);
		toolBarButtons[TOOLBAR_UNDO].setEnabled(true);
		toolBarButtons[TOOLBAR_REDO].setEnabled(true);
		toolBarButtons[TOOLBAR_HINT].setEnabled(true);
		toolBarButtons[TOOLBAR_CHECK].setEnabled(true);
		///
		this.add( treet, BorderLayout.EAST);
		this.pack();
	}

	private void repaintAll()
	{
		for (int x = 0; x < frames.length; ++x)
		{
			frames[x].repaint();
		}
	}


	/*
	 * ILegupGui interface methods
	 * @see edu.rpi.phil.legup.ILegupGui
	 */

	public void showStatus(String status)
	{
		justificationFrame.setStatus(false,status);
	}

	public void errorEncountered(String error)
	{
		JOptionPane.showMessageDialog(null,error);
	}

	public void promptPuzzle()
	{
		pgd = new PickGameDialog(this,legupMain,true);
		selectNewPuzzle();

		mdiPane.setPreferredSize(new Dimension(800, 600));
		pack();
	}

	public void reloadGui()
	{
		justificationFrame.setJustifications(Legup.getInstance().getPuzzleModule());

		// AI setup
		myAI.setBoard(Legup.getInstance().getPuzzleModule());

		// show them all
		showAll();
	}


	/*
	 * Events
	 */

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == newPuzzle || e.getSource() == toolBarButtons[TOOLBAR_NEW])
		{
			selectNewPuzzle();
		}
		else if (e.getSource() == openProof || e.getSource() == toolBarButtons[TOOLBAR_OPEN])
		{
			openProof();
		}
		else if (e.getSource() == saveProof || e.getSource() == toolBarButtons[TOOLBAR_SAVE])
		{
			saveProof();
		}
		else if (e.getSource() == genPuzzle)
		{
			PuzzleGeneratorDialog pgd = new PuzzleGeneratorDialog(this);
			pgd.setVisible(true);

			if (pgd.getChoice() == PuzzleGeneratorDialog.PUZZLE_CHOSEN)
			{
				PuzzleModule module = PuzzleGeneration.getModule(pgd.puzzleChosen());
				BoardState puzzle = PuzzleGeneration.makePuzzle(pgd.puzzleChosen(), pgd.difficultyChosen(), this);
				legupMain.initializeGeneratedPuzzle(module, puzzle);

				justificationFrame.setJustifications(module);

				// AI setup
				myAI.setBoard(module);

				// show them all
				showAll();
			}
		}
		else if (e.getSource() == exit)
		{
			System.exit(0);
		}
		else if (e.getSource() == undo || e.getSource() == toolBarButtons[TOOLBAR_UNDO])
		{
			System.out.println("Undo!");
		}
		else if (e.getSource() == redo || e.getSource() == toolBarButtons[TOOLBAR_REDO])
		{
			System.out.println("Redo!");
		}
		else if (e.getSource() == toolBarButtons[TOOLBAR_CONSOLE])
		{
			console.setVisible(!console.isVisible());
			pack();
		}
		else if (e.getSource() == toolBarButtons[TOOLBAR_CHECK])
		{
			checkProof();
		}
		else if (e.getSource() == hint || e.getSource() == toolBarButtons[TOOLBAR_HINT])
		{
			myAI.setBoard(Legup.getInstance().getPuzzleModule());
			String text = new String( myAI.findRuleApplication(Legup.getInstance().getSelections().getFirstSelection().getState()) );
			// TODO console
			console.println("Tutor: " + text);
		}
		else if (e.getSource() == allowDefault)
		{
			//Change default applications on, nothing, checks menu checked state everywhere
		}
		else if (e.getSource() == Step)
		{
			if (myAI.loaded()) {
				BoardState current = legupMain.getSelections().getFirstSelection().getState();
				myAI.step(current);
			}
		}
		else if (e.getSource() == Run)
		{
			if (myAI.loaded()) {
				BoardState current = legupMain.getSelections().getFirstSelection().getState();
				myAI.stepToCompletion(current);
			}
		}
		else if (e.getSource() == Test)
		{
			if (myAI.loaded()) {
				//PuzzleModule current = legupMain.getPuzzleModule();
				//myAI.test(current);
				BoardState current = legupMain.getSelections().getFirstSelection().getState();
				myAI.findRuleApplication(current);
			}
		}
		else
		{
			for (int x = 0; x < viewItem.length;++x)
			{
				if (e.getSource() == viewItem[x]) setFrameState(x,viewItem[x].isSelected());
			}
			for (int x = 0; x < PROF_FLAGS.length; ++x)
			{
				if (e.getSource() == proofModeItems[x]) processConfig(x);
			}
		}
	}

	public void internalFrameClosing(InternalFrameEvent e)
	{
		for (int x = 0; x < frames.length; ++x)
			if (e.getSource() == frames[x])
				viewItem[x].setState(false);
	}

	public void internalFrameOpened(InternalFrameEvent e)
	{
		for (int x = 0; x < frames.length; ++x)
			if (e.getSource() == frames[x])
				viewItem[x].setState(true);
	}

	public void internalFrameClosed(InternalFrameEvent e) { }
	public void internalFrameIconified(InternalFrameEvent e) { }
	public void internalFrameDeiconified(InternalFrameEvent e) { }
	public void internalFrameActivated(InternalFrameEvent e) { }//e.getInternalFrame().requestFocusInWindow(); }
	public void internalFrameDeactivated(InternalFrameEvent e) { }

	public void treeSelectionChanged(ArrayList <Selection> s)
	{
		repaintAll();
	}

 	public void processConfig(int index)
	{
		proofModeItems[CONFIG_INDEX].setState(false);
		CONFIG_INDEX = index;
		proofModeItems[CONFIG_INDEX].setState(true);

		int flags = PROF_FLAGS[index];
		if (!profFlag(ALLOW_DEFAPP)) allowDefault.setState(false);
		allowDefault.setEnabled(profFlag(ALLOW_DEFAPP));

		AI.setEnabled(profFlag(ALLOW_FULLAI));

		justificationFrame.setStatus(true, "Proof mode "+PROFILES[index]+" has been activated");
	}

}
