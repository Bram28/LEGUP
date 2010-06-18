package edu.rpi.phil.legup.newgui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Color;
import java.awt.Point;

//import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import javax.swing.BorderFactory; 
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.ImageIcon;

import javax.swing.JToolBar;

public class Console extends JToolBar
{
	private static final long serialVersionUID = 2811647197689569562L;

	private JTextArea output;
	private JScrollPane scrollPane;

	Console() {
		super("LEGUP");
		super.setLayout(new BorderLayout());

		output = new JTextArea("LEGUP Console v1.0\n");
		output.setAutoscrolls(true);
		output.setEditable(false);
		output.setWrapStyleWord(true);

		// optional console settings
		output.setFont( new Font("Monospaced", Font.PLAIN, 14) );
		//output.setBackground( Color.BLACK );
		//output.setForeground( Color.GREEN );

		scrollPane = new JScrollPane(output, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );
		this.add(scrollPane);
		
		//ImageIcon icon = new ImageIcon("images/bar.png");
		//Border titlebar= BorderFactory.createMatteBorder(24, 0, 0, 0, icon);
		TitledBorder title = BorderFactory.createTitledBorder( /*titlebar,*/ "Console");
		title.setTitleJustification(TitledBorder.CENTER);
		this.setBorder(title);

		this.setPreferredSize( new Dimension(800,100) );
		

		/*/ extremely hackish way to prevent orientation changes
		this.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
			public void propertyChange(java.beans.PropertyChangeEvent evt) {
				if ("orientation".equals(evt.getPropertyName())) {
					Integer i = (Integer)evt.getOldValue();
					if(i.intValue() == JToolBar.VERTICAL)
						fix();
				}
			}
		}); */

	}
	
	/*/ helper function to the orientation hack
	private void fix(){
		this.setOrientation(JToolBar.HORIZONTAL);
	}*/
	
	public void println( String text ){
		output.append(text + '\n');
		output.setCaretPosition( output.getDocument().getLength() );
	}

}
