package edu.rpi.phil.legup.saveable;

//import java.beans.XMLDecoder;
//import java.beans.XMLEncoder;
import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.Vector;
//import java.util.zip.GZIPInputStream;
//import java.util.zip.GZIPOutputStream;

import javax.swing.JOptionPane;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.Legup;
import edu.rpi.phil.legup.newgui.TreePanel;

public class SaveableProof
{
	private SaveableProof(BoardState state)
	{}
	
	public static BoardState loadProof(String filename) throws ClassNotFoundException, IOException
	{
		FileInputStream f_in = new FileInputStream(filename);

		// Read object using ObjectInputStream
		ObjectInputStream obj_in = new ObjectInputStream (f_in);

		// Read an object
		Object obj = obj_in.readObject();

		if (obj instanceof BoardState)
		{
			// Cast object to a BoardState
			BoardState state = (BoardState) obj;
			Legup.getInstance().loadPuzzleModule(state.getPuzzleName());
			Legup.getInstance().getGui().repaint();
			return state;
		}
		return null;

	}
	
	public static boolean saveProof(BoardState state, String filename) throws IOException
	{
		// Write to disk with FileOutputStream
		FileOutputStream f_out = new FileOutputStream(filename);

		// Write object with ObjectOutputStream
		ObjectOutputStream obj_out = new ObjectOutputStream (f_out);

		// Write object out to disk
		obj_out.writeObject ( state );
		
		return true;
	}
}
