package edu.rpi.phil.legup.newgui;

import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.awt.Image;
import javax.swing.ImageIcon;

/*** ZOOM WIDGET ***/
/**
 * @author Colin Kuebler
 */
public class ZoomWidget extends JLabel {
	private static final long serialVersionUID = -6945943362868642035L;
	private DynamicViewer parent;
	private class PopupSlider extends JPopupMenu implements ChangeListener {
		private static final long serialVersionUID = 8225019381200459814L;
		public JSlider slider;
		PopupSlider(){
			slider = new JSlider( SwingConstants.VERTICAL, 25, 400, 100 );
			// setup slider labels
			//Create the label table
			//Hashtable labels = new Hashtable<Integer,JLabel>();
			/*for( double zoom : levels ){
				Integer level = new Integer( (int)( zoom * 100.0 ) );
				String label = new String( Integer.toString(level) + "% " );
				System.out.println(label);
				labels.put( level, new JLabel(label) );
			}*/
			//labels.put( new Integer(25), new JLabel("25%") );
			//labels.put( new Integer(50), new JLabel("50%") );
			//labels.put( new Integer(100), new JLabel("100%") );
			//labels.put( new Integer(200), new JLabel("200%") );
			//labels.put( new Integer(400), new JLabel("400%") );
			//slider.setLabelTable( labels );
			//slider.setPaintLabels(true);
			slider.setMajorTickSpacing( 25 );
			//slider.setSnapToTicks( true );
			slider.setPaintTicks( true );
			//setPreferredSize( slider.getPreferredSize() );
			add( slider );
			slider.addChangeListener(this);
		}
		public void stateChanged( ChangeEvent e ){
			if( slider.getValueIsAdjusting() ){
				parent.zoomTo( (double) slider.getValue() / 100.0 );
			}
		}
	}
	private MouseAdapter open = new MouseAdapter(){
		public void mouseClicked( MouseEvent e ){
			palette.slider.setValue( parent.getZoom() );
			palette.show( e.getComponent(),  0,0);//e.getX(), e.getY() );
		}
	};
	private PopupSlider palette = new PopupSlider();
	public ZoomWidget( DynamicViewer parent ){
		super( new ImageIcon("zoom.png") );
		this.parent = parent;
		addMouseListener(open);
	}
}
