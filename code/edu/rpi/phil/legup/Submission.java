package edu.rpi.phil.legup;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.JOptionPane;

public class Submission {
	public Submission(BoardState state, Boolean correct)
	{
		String url = "http://localhost:8000/submit/";
		String charset = "UTF-8";
		String user = Legup.getInstance().getUser();
				
		ByteArrayOutputStream out_stream = new ByteArrayOutputStream();
		ObjectOutputStream obj_out;
		try {
			obj_out = new ObjectOutputStream(out_stream);
			obj_out.writeObject(state);
			obj_out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		out_stream.toByteArray();
		
		String boundary = Long.toHexString(System.currentTimeMillis()); // Just generate some unique random value.
		String CRLF = "\r\n"; // Line separator required by multipart/form-data.

		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection)new URL(url).openConnection();
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
			JOptionPane.showMessageDialog(null, "FAILED to submit proof!");
		} catch (IOException e1) {
			e1.printStackTrace();
			JOptionPane.showMessageDialog(null, "FAILED to submit proof!");
		}
		connection.setDoOutput(true);
		connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
		PrintWriter writer = null;
		
		System.out.println("About to enter Submission block");
		try {
		    OutputStream output = connection.getOutputStream();
		    writer = new PrintWriter(new OutputStreamWriter(output, charset), true); // true = autoFlush, important!

		    // Send binary file.
		    writer.append("--" + boundary).append(CRLF);
		    writer.append("Content-Disposition: form-data; name=\"proof\"; filename=\"" + user + "_" + state.getPuzzleName() + ".proof\"").append(CRLF);
		    writer.append("Content-Type: application/legup").append(CRLF);
		    writer.append("Content-Transfer-Encoding: binary").append(CRLF);
		    writer.append(CRLF).flush();
		    InputStream input = null;
		    try {
		        input = new ByteArrayInputStream(out_stream.toByteArray());
		        byte[] buffer = new byte[1024];
		        for (int length = 0; (length = input.read(buffer)) > 0;) {
		            output.write(buffer, 0, length);
		            System.out.println("busy writing file");
		        }
		        output.flush(); // Important! Output cannot be closed. Close of writer will close output as well.
		    } catch (IOException e) {
		    	e.printStackTrace();
				JOptionPane.showMessageDialog(null, "FAILED to submit proof!");
			} finally {
		        if (input != null) try { input.close(); } catch (IOException logOrIgnore) {}
		    }
		    writer.append(CRLF).flush(); // CRLF is important! It indicates end of binary boundary.
		    
		    // Send username.
		    writer.append("--" + boundary).append(CRLF);
		    writer.append("Content-Disposition: form-data; name=\"user\"").append(CRLF);
		    writer.append("Content-Type: text/plain; charset=" + charset).append(CRLF);
		    writer.append(CRLF);
		    writer.append(user).append(CRLF).flush();
		    
		    // Send correctness.
		    writer.append("--" + boundary).append(CRLF);
		    writer.append("Content-Disposition: form-data; name=\"correct\"").append(CRLF);
		    writer.append("Content-Type: text/plain; charset=" + charset).append(CRLF);
		    writer.append(CRLF);
		    writer.append(correct.toString()).append(CRLF).flush();
		    
		    // Send puzzle type.
		    writer.append("--" + boundary).append(CRLF);
		    writer.append("Content-Disposition: form-data; name=\"puzzle\"").append(CRLF);
		    writer.append("Content-Type: text/plain; charset=" + charset).append(CRLF);
		    writer.append(CRLF);
		    writer.append(state.getPuzzleName()).append(CRLF).flush();

		    // End of multipart/form-data.
		    writer.append("--" + boundary + "--").append(CRLF);
		    
		    StringBuffer response = new StringBuffer();
		    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		    String line;
		    while ((line = reader.readLine()) != null)
		    {
		    	response.append(line);
		    }
		    if (response.toString().equals("success"))
		    {
				JOptionPane.showMessageDialog(null, "Thank you. Your proof was submitted successfully.");
		    }
		    else
		    {
				JOptionPane.showMessageDialog(null, "FAILED to submit proof!");
		    }
		} catch (IOException e) {
			e.printStackTrace();
			InputStream errorStream = connection.getErrorStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(errorStream));
			try {
				String line = null;
				while((line = in.readLine()) != null) {
				  System.out.println(line);
				}
			} catch (IOException e1) {
			}
			JOptionPane.showMessageDialog(null, "FAILED to submit proof!");
		} finally {
		    if (writer != null) writer.close();
		}
	}
}
