// Stan Bak
// 11-05
// Image Chooser Dialog
// Allows the user to choose quickly from a set of images

package edu.rpi.phil.legup.editor;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;

import edu.rpi.phil.legup.BoardImage;

public class ImageChooserDialog extends JDialog implements ActionListener
{
	private static final long serialVersionUID = -2304281047341398965L;
	
	private JButton[] buttons = null;
	public Image selected = null;
	public int selectedIndex = -1;
	private Image[] allImages = null;
	
	private ImageChooserDialog(Image[] set)
	{
		super(new Frame(), true);
		setTitle("Choose Image");
		
		allImages = set;
		int w = (int)Math.sqrt(set.length);
		
		setLayout(new GridLayout(w,w));
		
		buttons = new JButton[set.length];
		
		for (int x = 0; x < set.length; ++x)
		{
			buttons[x] = new JButton(new ImageIcon(set[x]));
			buttons[x].addActionListener(this);
			add(buttons[x]);
		}
		
		pack();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(screenSize.width / 2 - getWidth() / 2, screenSize.height / 2 - getHeight() / 2);
		
		setVisible(true);
	}

	public static Image chooseImage(Image[] set)
	{
		ImageChooserDialog i = new ImageChooserDialog(set);
		
		return i.selected;
	}
	
	public static int chooseImage(BoardImage[] set)
	{
		Image ims[] = new Image[set.length];
		
		for (int x = 0; x < set.length; ++x)
		{
			ims[x] = set[x].i;
		}
		
		ImageChooserDialog i = new ImageChooserDialog(ims);
		
		return i.selectedIndex;
	}
	
	public void actionPerformed(ActionEvent e)
	{
		for (int x = 0; x < buttons.length; ++x)
		{
			if (e.getSource() == buttons[x])
			{
				selectedIndex = x;
				selected = allImages[x];
				setVisible(false);
				break;
			}
		}
	}
}
