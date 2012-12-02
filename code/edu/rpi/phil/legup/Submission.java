package edu.rpi.phil.legup;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;


public class Submission {
	public Submission(BoardState state, Boolean correct)
	{
		String url = "https://legup.herokuapp.com/submit/";
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

		URLConnection connection = null;
		try {
			connection = new URL(url).openConnection();
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		connection.setDoOutput(true);
		connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
		PrintWriter writer = null;
		
		try {
		    OutputStream output = connection.getOutputStream();
		    writer = new PrintWriter(new OutputStreamWriter(output, charset), true); // true = autoFlush, important!

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

		    // Send binary file.
		    java.util.Date now = new java.util.Date();
		    writer.append("--" + boundary).append(CRLF);
		    writer.append("Content-Disposition: form-data; name=\"binaryFile\"; filename=\"" + user + "_" + state.getPuzzleName() + "_" + now.toString() + "\"").append(CRLF);
		    writer.append("Content-Type: application/legup").append(CRLF);
		    writer.append("Content-Transfer-Encoding: binary").append(CRLF);
		    writer.append(CRLF).flush();
		    InputStream input = null;
		    try {
		        input = new ByteArrayInputStream(out_stream.toByteArray());
		        byte[] buffer = new byte[1024];
		        for (int length = 0; (length = input.read(buffer)) > 0;) {
		            output.write(buffer, 0, length);
		        }
		        output.flush(); // Important! Output cannot be closed. Close of writer will close output as well.
		    } catch (IOException e) {
			} finally {
		        if (input != null) try { input.close(); } catch (IOException logOrIgnore) {}
		    }
		    writer.append(CRLF).flush(); // CRLF is important! It indicates end of binary boundary.

		    // End of multipart/form-data.
		    writer.append("--" + boundary + "--").append(CRLF);
		} catch (IOException e) {
		} finally {
		    if (writer != null) writer.close();
		}
	}
}
