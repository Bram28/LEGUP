package edu.rpi.phil.legup.newgui;

import javax.swing.JInternalFrame;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.Dimension;

public class TutorFrame extends JInternalFrame
{
	private static final long serialVersionUID = 8572197337878587284L;

	private JTextArea tutorOutput = new JTextArea();
	private JScrollPane scrollPane = new JScrollPane();
	private TutorToolbarPanel tutorToolbar;

	public TutorFrame()
	{
		super("LEGUP Tutor");

		this.setLayout(new BorderLayout());

		//tutorOutput.setPreferredSize(new Dimension(200, 200));
		tutorOutput.setAutoscrolls(true);
		tutorOutput.setEditable(false);

		scrollPane = new JScrollPane(tutorOutput);
		this.getContentPane().setPreferredSize(new Dimension(200,200));
		this.add(scrollPane, BorderLayout.CENTER);

		tutorToolbar = new TutorToolbarPanel(this);
		this.add(tutorToolbar, BorderLayout.NORTH);

		this.pack();
	}

	public void tutorPrint(final String text)
	{
		tutorOutput.append( text );
		tutorOutput.setCaretPosition( tutorOutput.getDocument().getLength() );

		//Attempt at scrolling while only at the bottom
		/*
		JScrollBar vbar = scrollPane.getVerticalScrollBar();
		boolean autoScroll = ((vbar.getValue() + vbar.getVisibleAmount()) == vbar.getMaximum());
		tutorOutput.append( text );
		if( autoScroll )
		{
			tutorOutput.setCaretPosition( tutorOutput.getDocument().getLength() );
		}
		*/
	}

	public void tutorPrintln(String text)
	{
		tutorPrint(text + "\n");
	}
}
