package edu.rpi.phil.legup.saveable;

//import java.beans.XMLDecoder;
//import java.beans.XMLEncoder;
import java.awt.Point;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.Vector;
//import java.util.zip.GZIPInputStream;
//import java.util.zip.GZIPOutputStream;

import javax.swing.JOptionPane;

import edu.rpi.phil.legup.BoardState;

public class SaveableProof
{
	public SaveableProofState[] states;
	public Vector<SaveableProofTransition> transitions;
	public PrintWriter out;
	public File file;
	private SaveableProof(BoardState state)
	{
		states = new SaveableProofState[state.calcID()];
		transitions = new Vector<SaveableProofTransition>();
		state.makeSaveableProof(states, transitions);
	}
	
	private BoardState toBoardState()
	{
		BoardState[] bstates = new BoardState[states.length];
		for(int i = 0; i < states.length; ++i)
		{
			bstates[i] = BoardState.fromSaveableProofState(states[i]);
		}
		
		for(SaveableProofTransition t : transitions)
		{
			bstates[t.id1].addTransitionFrom(bstates[t.id2], t.justification, t.isCaseRule);
		}
		
		bstates[0].recalculateLocation();
		
		return bstates[0];
	}
	public SaveableProof(){}
/*LOAD XML	
	//XML functions
	
	public static BoardState loadProof(String filename)
    {
		SaveableProof rv = null;
    	
    	try
    	{
	    	XMLDecoder d = new XMLDecoder(
	                new BufferedInputStream(
	                		//new GZIPInputStream( //For compression
	                    new FileInputStream(filename)));
			Object result = d.readObject();
			d.close();
			
			rv = (SaveableProof)result;
    	}
    	catch (Exception e)
    	{
    		JOptionPane.showMessageDialog(null,"Error loading: " + e.toString());
    		rv = null;
    	}
    	
    	BoardState state = rv.toBoardState(); 
    	
    	if(state == null)
    		JOptionPane.showMessageDialog(null,"Board Load Failed");
    		
    	return state;
    }
*/
//NORMAL LOAD
	public static BoardState loadProof(String filename) throws IOException
	{
		System.out.println("Loading...\n");
		SaveableProof loadthis = new SaveableProof();
		Vector<SaveableProofState> loadme = new Vector<SaveableProofState>();
		loadme.add(new SaveableProofState());
		Scanner scan = new Scanner(new File(filename));
		String str;
		int in;
		Vector<Integer> labels = new Vector<Integer>();
		//iterate through file
		//puzzle
		str = scan.next();
		//System.out.println(str);
		str = scan.next();
		//str += scan.nextLine();
		System.out.println(str);
		loadme.get(0).puzzleName = str;
		//System.out.println("stupid loadme is working\n");
		//height
		while (!scan.hasNextInt()){ scan.next();}
		in = scan.nextInt();
		//System.out.println(in);
		loadme.get(0).height = in;
		//width
		while (!scan.hasNextInt()){ scan.next();}
		in = scan.nextInt();
		//System.out.println(in);
		loadme.get(0).width = in;
		//make board array
		loadme.get(0).boardCells = new int[loadme.get(0).height][loadme.get(0).width];
		//top labels
		str = scan.next();
		//System.out.println(str);
		str = scan.next();
		while (!str.equals("bottomLabels:"))
		{
			System.out.println(str);
			labels.add(Integer.parseInt(str));
			str = scan.next();
		}
		loadme.get(0).topLabels = new int[labels.size()];
		for (int x = 0; x < labels.size(); x++)
			loadme.get(0).topLabels[x] = labels.get(x);
		labels.clear();
		//bottom labels
		str = scan.next();
		while (!str.equals("leftLabels:"))
		{
			labels.add(Integer.parseInt(str));
			str = scan.next();
		}
		loadme.get(0).bottomLabels = new int[labels.size()];
		for (int x = 0; x < labels.size(); x++)
			loadme.get(0).bottomLabels[x] = labels.get(x);
		labels.clear();
		//left labels
		str = scan.next();
		while (!str.equals("rightLabels:"))
		{
			labels.add(Integer.parseInt(str));
			str = scan.next();
		}
		loadme.get(0).leftLabels = new int[labels.size()];
		for (int x = 0; x < labels.size(); x++)
			loadme.get(0).leftLabels[x] = labels.get(x);
		labels.clear();
		//right labels
		str = scan.next();
		while (!str.equals("hintCells:"))
		{
			labels.add(Integer.parseInt(str));
			str = scan.next();
		}
		loadme.get(0).rightLabels = new int[labels.size()];
		for (int x = 0; x < labels.size(); x++)
			loadme.get(0).rightLabels[x] = labels.get(x);
		labels.clear();
		//hint cells;
		str = scan.next();
		//extra data
		//str = scan.next();
		//board
		//scan.next();
		for (int i = 0; i < loadme.get(0).height; i++)
		{
			for (int j = 0; j < loadme.get(0).width; j++)
			{
				loadme.get(0).boardCells[i][j] = scan.nextInt();
				//System.out.println(loadme.get(0).boardCells[i][j]);
			}
		}
		//transitions
		str = scan.next();
		loadthis.transitions = new Vector<SaveableProofTransition>();
		while (scan.hasNextInt())
		{
			loadthis.transitions.add(new SaveableProofTransition());
			loadthis.transitions.lastElement().id1 = scan.nextInt();
			loadthis.transitions.lastElement().id2 = scan.nextInt();
			loadthis.transitions.lastElement().x = scan.nextInt();
			loadthis.transitions.lastElement().y = scan.nextInt();
			loadthis.transitions.lastElement().prev = scan.nextInt();
			loadthis.transitions.lastElement().newv = scan.nextInt();
			loadthis.transitions.lastElement().justification += scan.next();
			str = scan.next();
			while(!str.equals("correct:"))
			{
				loadthis.transitions.lastElement().justification += " ";
				loadthis.transitions.lastElement().justification += str;
				str = scan.next();
			}
			loadthis.transitions.lastElement().isCaseRule = scan.nextBoolean();
			//make new states, will not be needed once transitions actually exist
			loadme.add(loadme.lastElement());
			if (loadthis.transitions.lastElement().id2 > loadme.size())
				break;
			else
				loadme.lastElement().boardCells[loadthis.transitions.lastElement().y][loadthis.transitions.lastElement().x] = loadthis.transitions.lastElement().newv;
			
		}
		scan.close();
		System.out.println("File Closed...");
		System.out.flush();
		//convert to boardstate
		loadthis.states = new SaveableProofState[loadme.size()];
		for (int x = 0; x < loadme.size(); x++)
			loadthis.states[x] = loadme.get(x);
		BoardState state = loadthis.toBoardState();
		SaveableProof.saveProof(state, "battleships_test_load.proof");
		if(state == null)
    		JOptionPane.showMessageDialog(null,"Board Load Failed");
		System.out.println("...Done\n");
		return state;
	}
/*SAVE XML	
    public static boolean saveProof(BoardState state, String filename)
    {	
    	SaveableProof s = new SaveableProof(state);
    	
    	try
    	{
	    	 XMLEncoder e = new XMLEncoder(
	                 new BufferedOutputStream(
	                		 //new GZIPOutputStream( //For compression
	                     new FileOutputStream(filename)));
			e.writeObject(s);
			e.close();
    	}
    	catch (Exception e)
    	{
    		JOptionPane.showMessageDialog(null,"Error saving: " + e.getMessage());
    		return false;
    	}
    	
    	return true;
    }
*/
//NORMAL SAVE
	public static boolean saveProof(BoardState state, String filename) throws IOException
	{
		SaveableProof saveme = new SaveableProof(state);
		saveme.file = new File(filename);
		saveme.out = new PrintWriter(new FileWriter(filename));
		int[] extradata;
		//write "static" state data
		saveme.out.print("Puzzle: ");
		saveme.out.print(saveme.states[0].puzzleName);
		saveme.out.print("\nHeight: ");
		saveme.out.print(saveme.states[0].height);
		saveme.out.print("\nWidth: ");
		saveme.out.print(saveme.states[0].width);
		saveme.out.print("\ntopLabels:");
		for (int savelabel : saveme.states[0].topLabels)
		{
			saveme.out.print(' ');
			saveme.out.print(savelabel);
		}
		saveme.out.print("\nbottomLabels:");
		for (int savelabel : saveme.states[0].bottomLabels)
		{
			saveme.out.print(' ');
			saveme.out.print(savelabel);
		}
		saveme.out.print("\nleftLabels:");
		for (int savelabel : saveme.states[0].leftLabels)
		{
			saveme.out.print(' ');
			saveme.out.print(savelabel);
		}
		saveme.out.print("\nrightLabels:");
		for (int savelabel : saveme.states[0].rightLabels)
		{
			saveme.out.print(' ');
			saveme.out.print(savelabel);
		}
		saveme.out.print("\nhintCells:\n");
		for (Point savehints : saveme.states[0].hintCells)
		{
			//SAVE HINTCELLS
		}
		saveme.out.print("\nextraData:\n");
		for (Object extra : saveme.states[0].extraData)
		{
			//SAVE EXTRADATA, ie
			//extradata = new extra.tosavedata();
		}
		saveme.out.print("\nBoard:\n");
		for (int i = 0; i < saveme.states[0].height; i++)
		{
			for (int j = 0; j < saveme.states[0].width; j++)
			{
				saveme.out.print(saveme.states[0].boardCells[i][j]);
				saveme.out.print(' ');
			}
			saveme.out.print('\n');
		}
		//save transitions
		saveme.out.print("Transitions:\n");
		for (SaveableProofTransition transistme : saveme.transitions)
		{
			saveme.out.print(transistme.id1);
			saveme.out.print(' ');
			saveme.out.print(transistme.id2);
			saveme.out.print(' ');
			saveme.out.print(transistme.x);
			saveme.out.print(' ');
			saveme.out.print(transistme.y);
			saveme.out.print(' ');
			saveme.out.print(transistme.prev);
			saveme.out.print(' ');
			saveme.out.print(transistme.newv);
			saveme.out.print(' ');
			saveme.out.print(transistme.justification);
			saveme.out.print(' ');
			//java can't type cast to/from bools...
			extradata = new int[1];
			if (transistme.isCaseRule)
				extradata[0] = 1;
			else
				extradata[0] = 0;
			saveme.out.print(extradata[0]);
			saveme.out.print('\n');
		}
		saveme.out.close();
		return true;
	}
	public SaveableProofState[] getStates()
	{
		return states;
	}

	public void setStates(SaveableProofState[] states)
	{
		this.states = states;
	}

	public Vector<SaveableProofTransition> getTransitions()
	{
		return transitions;
	}

	public void setTransitions(Vector<SaveableProofTransition> transitions)
	{
		this.transitions = transitions;
	}
}
