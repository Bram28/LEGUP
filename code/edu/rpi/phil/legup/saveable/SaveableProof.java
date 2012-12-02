package edu.rpi.phil.legup.saveable;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.Legup;

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
		
		obj_in.close();

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
		
		obj_out.close();
		
		return true;
	}
}
