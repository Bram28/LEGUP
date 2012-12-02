package edu.rpi.phil.legup;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;

import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

import edu.rpi.phil.legup.newgui.Console;

public class Login {
	private Legup legupMain = null;
    private JTextField rcsInput;
    private JPasswordField passwordInput;
    private JFrame frame;
    private JLabel error;

	public Login(Legup legupMain)
	{
		this.legupMain = legupMain;
		this.promptLogin();
	}
	
	private void promptLogin()
	{
        frame = new JFrame("Sign in");
        frame.setSize(300,135);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Creating the grid
        JPanel panel = new JPanel(new GridBagLayout());
        frame.getContentPane().add(panel, BorderLayout.NORTH);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(0, 0, 0, 0);

        // Create some elements
        error = new JLabel(" ");
        error.setForeground(Color.red);
        c.gridx = 1;
        c.gridy = 1;
        c.gridwidth = 2;
        panel.add(error, c);
        
        JLabel rcsLabel = new JLabel("RCSID");
        c.gridx = 1;
        c.gridy = 2;
        c.gridwidth = 1;
        panel.add(rcsLabel, c);
        
        rcsInput = new JTextField(10);
        c.gridx = 2;
        c.gridy = 2;
        panel.add(rcsInput, c);
        rcsInput.addKeyListener(new LoginEnter());
        
        JLabel passwordLabel = new JLabel("Password");
        c.gridx = 1;
        c.gridy = 3;
        panel.add(passwordLabel, c);

        passwordInput = new JPasswordField(10);
        c.gridx = 2;
        c.gridy = 3;
        panel.add(passwordInput, c);
        passwordInput.addKeyListener(new LoginEnter());

        JButton loginInput = new JButton("Sign in");
        c.gridx = 1;
        c.gridy = 4;
        c.gridwidth = 2;
        panel.add(loginInput, c);
        loginInput.addActionListener(new LoginButton());
        
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
	}
	
	private void login ()
	{
        String rcsid = rcsInput.getText();
        String password = new String(passwordInput.getPassword());
        
        String urlParameters = "username="+rcsid;
        urlParameters += "&password="+password;
        urlParameters += "&lt=e1s1";
        urlParameters += "&_eventId=submit";
        		
        try {
			URL url_session = new URL("https://cas-auth.rpi.edu/cas/login?service=http%3A%2F%2Flocalhost%3A8000%2Faccounts%2Flogin%2F%3Fnext%3D%252F");
	        HttpURLConnection conn = (HttpURLConnection)url_session.openConnection();
	        
	        String session_html = new String();
	        String result_html = new String();
	        
	        conn.setDoOutput(true);
	        
	        OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
	        
	        String line;
	        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	        
	        while ((line = reader.readLine()) != null) {
	            session_html += line;
	        }
	        writer.close();
	        reader.close(); 
	        
	        String[] session_html_tokens = session_html.split("[;=\\\"]");
	        int index = Arrays.asList(session_html_tokens).indexOf("jsessionid");
	        String jsessionid = session_html_tokens[index+1];
			URL url_result = new URL("https://cas-auth.rpi.edu/cas/login;jsessionid="+jsessionid+"?service=http%3A%2F%2Flocalhost%3A8000%2Faccounts%2Flogin%2F%3Fnext%3D%252F");
	        
	        conn = (HttpURLConnection)url_result.openConnection();
	        conn.setDoOutput(true);
	        
	        writer =  new OutputStreamWriter(conn.getOutputStream());
	        
	        writer.write(urlParameters);
	        writer.flush();
	        
	        int response = conn.getResponseCode();
	        if (response == 302)
	        {
	        	legupMain.setUser(rcsid);
		        frame.setVisible(false);
		        legupMain.getGui().setVisible(true);
	        }
	        else
	        {
	        	error.setText("Incorrect username or password.");
	        }	        
        } catch (IOException e) {
        	e.printStackTrace();
        }
	}
	
    class LoginButton implements ActionListener {

        public void actionPerformed(ActionEvent e)
        {
        	login();
        }
        
    }
    
    class LoginEnter implements KeyListener {
    	
    	public void keyPressed (KeyEvent e)
    	{
    		if (e.getKeyCode() == 10)
    		{
    			login();
    		}
    	}

		public void keyReleased(KeyEvent e)
		{}

		public void keyTyped(KeyEvent e)
		{}
    	
    }

	
}
