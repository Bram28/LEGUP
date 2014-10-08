package edu.rpi.phil.legup.newgui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.Point;
import java.lang.Double;
import java.util.TreeSet;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.ViewportLayout;

/**
 * @author Colin Kuebler
 */
public abstract class DynamicViewer extends JScrollPane {
	private static final long serialVersionUID = 24547340L;

	/*** FIELDS ***/
	// customized JComponent provides a scalable canvas for drawing
	private JComponent canvas = new JComponent(){
		private static final long serialVersionUID = -6592350784886799360L;
		public void paint( Graphics g ){
			Graphics2D g2d = (Graphics2D) g;
			g2d.scale(scale,scale);
			draw(g2d);
		}
	};
	private Dimension size = new Dimension();
	private Dimension zoomSize = new Dimension();
	private double levels[] = { 0.25, 1.0/3.0, 0.50, 2.0/3.0, 1.0, 2.0, 3.0, 4.0 };
	private TreeSet<Double> zoomLevels = new TreeSet<Double>();
	private double minScale = 0.25;
	private double maxScale = 4.0;
	private double scale = 1.0;
	private ZoomWidget widget;

	/*** MOUSE EVENTS ***/
	private MouseAdapter hci = new MouseAdapter(){
		int x, y;
		boolean pan = false;
		public void mousePressed( MouseEvent e ){
			if( e.getButton() == MouseEvent.BUTTON2 ){
				pan = true;
				setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
				x = e.getX();
				y = e.getY();
			} else {
				mousePressedAt( toDrawCoordinates(e.getPoint()), e );
			}
		}
		public void mouseDragged( MouseEvent e ){
			if( pan ){
				Point p = viewport.getViewPosition();
				p.x += (x - e.getX());
				p.y += (y - e.getY());
				viewport.setViewPosition(p);
				revalidate();
			} else {
				mouseDraggedAt( toDrawCoordinates(e.getPoint()), e );
			}
		}
		public void mouseReleased( MouseEvent e ){
			if( e.getButton() == MouseEvent.BUTTON2 ){
				pan = false;
				setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			} else {
				mouseReleasedAt( toDrawCoordinates(e.getPoint()), e );
			}
		}
		public void mouseWheelMoved( MouseWheelEvent e ){
			zoom( e.getWheelRotation(), e.getPoint() );
		}
		// extra mouse events for ZoomablePanel compatibility
		public void mouseEntered( MouseEvent e ){
			mouseEnteredAt( toDrawCoordinates(e.getPoint()), e );
		}
		public void mouseExited( MouseEvent e ){
			mouseExitedAt( toDrawCoordinates(e.getPoint()), e );
		}
		public void mouseMoved( MouseEvent e ){
			mouseMovedAt( toDrawCoordinates(e.getPoint()), e );
		}
	};

	/*** LAYOUT MANAGER ***/
	// override to return a customized viewport
	protected JViewport createViewport(){
		// customized viewport
		return new JViewport(){
			// override to return a customized layoutmanager
			private static final long serialVersionUID = -6592350784886799360L;
			protected LayoutManager createLayoutManager(){
				// customized layoutmanager
				return new ViewportLayout(){
					// positions the view within viewport
					private static final long serialVersionUID = -6592350784886799360L;
					public void layoutContainer(Container parent){
						Point p = viewport.getViewPosition();
						// determine the maximum x and y view positions
						int mx = canvas.getWidth() - viewport.getWidth();
						int my = canvas.getHeight() - viewport.getHeight();
						// obey edge boundaries
						if (p.x < 0)	p.x = 0;
						if (p.x > mx)	p.x = mx;
						if (p.y < 0)	p.y = 0;
						if (p.y > my)	p.y = my;
						// center margins
						if (mx < 0)	p.x = mx/2;
						if (my < 0)	p.y = my/2;
						viewport.setViewPosition(p);
					}
				};
				// end of customized layoutmanager
			}
		};
		// end of customized viewport
	}

	/*** CONSTRUCTORS ***/
	public DynamicViewer(){
		this( false );
	}
	
	public DynamicViewer( boolean usewidget ){
		// construct JScrollPane
		super( VERTICAL_SCROLLBAR_ALWAYS, HORIZONTAL_SCROLLBAR_ALWAYS );
		// setup viewport
		viewport.setView( canvas );
		
		// setup zoom levels
		for( Double level : levels )
			zoomLevels.add( level );
		
		// setup the zoom widget
		if( usewidget ){
			widget = new ZoomWidget(this);
			setCorner( JScrollPane.LOWER_RIGHT_CORNER, widget );
		}
		
		// setup mouse events
		setWheelScrollingEnabled(false);
		canvas.addMouseMotionListener(hci);
		canvas.addMouseListener(hci);
		viewport.addMouseWheelListener(hci);
	}

	/*** ZOOM HELPERS ***/
	// updates zoomSize and view size with the new scale
	private void updateSize(){
		zoomSize.setSize( (int) (size.width * scale), (int) (size.height * scale) );
		viewport.setViewSize(zoomSize);
	}

	// updates view position to account for zooming
	private void updatePosition( Point p, double mag ){
		Point m = viewport.getViewPosition();
		m.x = (int)( (double) (p.x + m.x) * mag - p.x + 0.0 );
		m.y = (int)( (double) (p.y + m.y) * mag - p.y + 0.0 );
		viewport.setViewPosition(m);
	}

	// converts canvas coordinates to draw coordinates
	private Point toDrawCoordinates( Point p ){
		return new Point( (int)( p.x / scale ), (int)( p.y / scale ) );
	}

	/*** ZOOM METHODS ***/
	public void zoom( int n, Point p ){
		// if no Point is given, keep current center
		if( p == null )
			p = new Point( viewport.getWidth()/2 + viewport.getX(), viewport.getHeight()/2 + viewport.getY() );
		// magnification level
		double mag = (double) n * 1.05;
		// zoom in
		if( n < 0 ){
			mag = -mag;
			// check zoom bounds
			if( scale * mag > maxScale )
				mag = maxScale / scale;
			// update
			scale *= mag;
			updateSize();
			updatePosition(p,mag);
		// zoom out
		} else {
			mag = 1 / mag;
			// check zoom bounds
			if( scale * mag < minScale )
				mag = minScale / scale;
			// update
			scale *= mag;
			updatePosition(p,mag);
			updateSize();
		}
		// update the scrollpane and subclass
		revalidate();
		zoomChanged();
	}

	public void zoomTo( double newscale ){
		// check zoom bounds
		if( newscale < minScale ) newscale = minScale;
		if( newscale > maxScale ) newscale = maxScale;
		if( newscale == scale ) return;
		// calculate the magnification and center point
		double mag = newscale / scale;
		Point p = new Point( viewport.getWidth()/2 + viewport.getX(), viewport.getHeight()/2 + viewport.getY() );
		// set scale directly
		scale = newscale;
		// zoom in
		if( mag > 1.0 ){
			updateSize();
			updatePosition(p,mag);
		// zoom out
		} else {
			updatePosition(p,mag);
			updateSize();
		}
		// update the scrollpane and subclass
		revalidate();
		zoomChanged();
	}

	public void zoomFit(){
		// find the ideal width and height scale
		double fitwidth = (viewport.getWidth()-8.0) / size.width;
		double fitheight = (viewport.getHeight()-8.0) / size.height;
		// choose the smaller of the two and zoom
		zoomTo( (fitwidth < fitheight) ? fitwidth : fitheight );
	}

	public void zoomIn(){
		// find the next valid zoom level
		Double newscale = zoomLevels.higher( new Double(scale) );
		if( newscale == null ) return;
		zoomTo( newscale.doubleValue() );
	}

	public void zoomOut(){
		// find the next valid zoom level
		Double newscale = zoomLevels.lower( new Double(scale) );
		if( newscale == null ) return;
		zoomTo( newscale.doubleValue() );
	}

	public int getZoom(){
		return (int)( scale * 100.0 );
	}

    public JViewport getViewport() { return viewport; }

	/*** CUSTOMIZATION METHODS ***/

	public void setBackground( Color c ){
		viewport.setBackground( c );
	}

	public void setSize( Dimension size ){
		this.size = size;
		updateSize();
	}

	/*** SUBCLASS METHODS ***/

	abstract protected void draw( Graphics2D g );

	// mouse events compatible with ZoomablePanel
	protected void mousePressedAt( Point p, MouseEvent e ){}
	protected void mouseDraggedAt( Point p, MouseEvent e ){}
	protected void mouseReleasedAt( Point p, MouseEvent e ){}
	protected void mouseEnteredAt( Point p, MouseEvent e ){}
	protected void mouseExitedAt( Point p, MouseEvent e ){}
	protected void mouseMovedAt( Point p, MouseEvent e ){}

	// zoom event
	protected void zoomChanged(){}

}

// references:
// http://www.java2s.com/Code/Java/Swing-JFC/GrabandDragimagescrolllabel.htm
// http://stackoverflow.com/questions/115103/how-do-you-implement-position-sensitive-zooming-inside-a-jscrollpane
