package edu.rpi.phil.legup.saveable;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Vector;
//import java.util.zip.GZIPInputStream;
//import java.util.zip.GZIPOutputStream;

import javax.swing.JOptionPane;

import edu.rpi.phil.legup.BoardState;

public class SaveableProof
{
	public SaveableProofState[] states;
	public Vector<SaveableProofTransition> transitions;
	
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
	
	
	//XML functions
	public SaveableProof(){}
	
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
