package edu.rpi.phil.legup.saveable;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

import javax.swing.JOptionPane;

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
		f_in.close();
		if (obj instanceof BoardState)
		{
			// Cast object to a BoardState
			BoardState state = (BoardState) obj;
			String user = Legup.getInstance().getUser();
			String boardUser = state.getUser();
			user = (user != null) ? user : "null";
			boardUser = (boardUser != null) ? boardUser : "null";
			String admins[] = Legup.getInstance().getAdmins();
			if (!(boardUser.equals(user)) && !(Arrays.asList(admins).contains(user)))
			{
				JOptionPane.showMessageDialog(null, "You do not have permission to open files owned by "+boardUser+".");
				return null;
			}
			else
			{
				//Legup.getInstance().loadPuzzleModule(state.getPuzzleName());
				Legup.getInstance().getGui().repaint();
				return state;
			}
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
		
		f_out.close();
		
		return true;
	}
	
	public static BoardState bytesToState(byte[] bytes)// throws ClassNotFoundException, IOException
	{
		try
		{
			ByteArrayInputStream b_in = new ByteArrayInputStream(bytes);
			ObjectInputStream obj_in = new ObjectInputStream(b_in);
			Object obj = obj_in.readObject();
			obj_in.close();
			b_in.close();
			if(!(obj instanceof BoardState))return null;
			BoardState state = (BoardState)obj;
			//validation of user is omitted since this is intended to be used for temporary storage
			//within one run of the program (undo/redo stacks). use loadProof() and saveProof() for long-term
			//storage or storage that should require authentication.
			return state;
		}
		catch(Exception ex)
		{
		}
		return null;
	}
	
	public static byte[] stateToBytes(BoardState state)// throws IOException
	{
		try
		{
			ByteArrayOutputStream b_out = new ByteArrayOutputStream();
			ObjectOutputStream obj_out = new ObjectOutputStream(b_out);
			obj_out.writeObject(state);
			obj_out.close();
			byte[] bytes = b_out.toByteArray();
			b_out.close();
			return bytes;
		}
		catch(Exception ex)
		{
		}
		return null;
	}
}

