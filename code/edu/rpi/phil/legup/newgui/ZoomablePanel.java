package edu.rpi.phil.legup.newgui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;
import java.util.Date;

import javax.swing.JPanel;

/**
 * Represents a scalable and moveable drawing panel
 * @author Stan Bak
 *
 */
public abstract class ZoomablePanel extends JPanel implements MouseWheelListener,
MouseListener, MouseMotionListener
{
	private static final long serialVersionUID = -2304281047341398965L;

//	 negative = zoom in, positive = zoom out
	private double desZoomFactor = 0;
	private double curZoomFactor = 0;
	protected double moveX = 0, moveY = 0;
	private double defaultMoveX = 0, defaultMoveY = 0;
	private int lastX = -1, lastY = -1;

	private static RoundRectangle2D.Double up = new RoundRectangle2D.Double(30,5,20,20,15,15);
	private static RoundRectangle2D.Double down = new RoundRectangle2D.Double(30,55,20,20,15,15);
	private static RoundRectangle2D.Double left = new RoundRectangle2D.Double(5,30,20,20,15,15);
	private static RoundRectangle2D.Double right = new RoundRectangle2D.Double(55,30,20,20,15,15);
	private static RoundRectangle2D.Double in = new RoundRectangle2D.Double(30,30,10,20,15,15);
	private static RoundRectangle2D.Double out = new RoundRectangle2D.Double(40,30,10,20,15,15);

	private static RoundRectangle2D.Double rects[] = {
		up, down, left, right, in, out
	};

	//Sets up polygons for drawing the black arrows
	private static int[] xUp = {40, 35, 45 };
	private static int[] yUp = {10, 20, 20 };

	private static int[] xDown = xUp;
	private static int[] yDown = {70, 60, 60 };

	private static int[] xLeft = yUp;
	private static int[] yLeft = xUp;

	private static int[] xRight = yDown;
	private static int[] yRight = xUp;

	private static Polygon arrows[] = {
		new Polygon( xUp , yUp, 3),
		new Polygon( xDown , yDown, 3),
		new Polygon( xLeft , yLeft, 3),
		new Polygon( xRight , yRight, 3),
		null, null
	};

	private static RoundRectangle2D.Double toggle = new RoundRectangle2D.Double(5,5,20,20,15,15);

	private boolean moveOn = false;
	private boolean mouseDown = false;
	private Point mousePoint = null;
	private static Stroke med = new BasicStroke(2);
	private static Stroke thin = new BasicStroke(1);

	/**
	 * Creates an instance of ZoomablePanel
	 */
	public ZoomablePanel() {
		addMouseWheelListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);

		//Handles smooth zooming in separate thread
		PaintThread pt = new PaintThread();
		pt.start();

		//Handles smooth scrolling in separate thread
		MoveThread mt = new MoveThread();
		mt.start();
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D)g;

		setupDrawing(g2d);

		AffineTransform a = g2d.getTransform();
		preDraw(g2d);
		draw(g2d);
		g2d.setTransform(a);

		g2d.setStroke(med);

		drawMove(g2d);
		drawToggle(g2d);
	}

	private void setupDrawing(Graphics2D g) {
		// Enable Anti-Aliasing
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	}

	private void preDraw(Graphics2D g) {
		Dimension size = getSize();
		g.setColor(Color.white);
		g.fillRect(0,0,size.width,size.height);

		double scale = getScale();
		g.scale(scale,scale);

		// move
		g.translate(moveX,moveY);
	}

	protected abstract void draw(Graphics2D g);

	/**
	 * @param g the graphics object to use
	 */
	private void drawToggle(Graphics2D g) {
		final int o = 5;
		g.setColor(Color.white);
		g.fill(toggle);
		g.setColor(moveOn ? Color.red : Color.black);
		g.draw(toggle);
		
		// draw minus
		g.drawLine((int)toggle.x + o,(int)toggle.y + (int)toggle.height / 2, (int)toggle.x + (int)toggle.width - o, (int)toggle.y + (int)toggle.height / 2);
		// draw plus
		if (!moveOn)
			g.drawLine((int)toggle.x + (int)toggle.width / 2,(int)toggle.y + o, (int)toggle.x + (int)toggle.width / 2, (int)toggle.y + (int)toggle.height - o);

	}

	private void drawMove(Graphics2D g) {
		if (moveOn) {
			for (int x = 0; x < rects.length; ++x) {
				g.setColor(Color.white);
				g.fill(rects[x]);

				g.setColor(Color.black);

				g.draw(rects[x]);

				if (x > 3) {
					Point middle = new Point(40,40);
					final int o = 2;
					g.setStroke(thin);
					if (x == 4) {
						middle = new Point(middle.x-5,middle.y);
						Point top = new Point(middle.x,middle.y-o);
						Point bottom = new Point(middle.x,middle.y+o);
						Point left = new Point(middle.x-o,middle.y);
						Point right = new Point(middle.x+o,middle.y);

						g.drawLine(top.x,top.y,bottom.x,bottom.y);
						g.drawLine(left.x,left.y,right.x,right.y);
					}
					else if (x == 5) {
						middle = new Point(middle.x+5,middle.y);

						Point left = new Point(middle.x-o,middle.y);
						Point right = new Point(middle.x+o,middle.y);

						g.drawLine(left.x,left.y,right.x,right.y);
					}

					g.setStroke(med);
				}
				else if (arrows[x] != null) {
						g.fill(arrows[x]);
				}
			}
		}
	}

	/**
	 * Sets the default position for this panel and moves to it.
	 */
	public void setDefaultPosition(double x, double y) {
		moveX = defaultMoveX = -x;
		moveY = defaultMoveY = -y;
		desZoomFactor = curZoomFactor = 0;
	}

	public void scrollDownToFit(double x, double y, double scroll) {
		double scale = getScale();
		double w = getWidth() / scale / 2;
		double h = getHeight() / scale / 2;
		moveX = x + w;
		moveY = h-y;
	}

	private double minimumScale = 0.25;
	public double getMinimumScale() {
		return minimumScale;
	}
	public void setMinimumScale(double minimumScale) {
		if(minimumScale < maximumScale)
			this.minimumScale = minimumScale;
	}

	private double maximumScale = 4;
	public double getMaximumScale() {
		return maximumScale;
	}
	public void setMaximumScale(double maximumScale) {
		if(maximumScale > minimumScale)
			this.maximumScale = maximumScale;
	}

	/**
	 * Gets the current scaling factor
	 * @return A double corresponding to the current scale
	 */
	protected double getScale() {
		//Find our min/max zoom exponents
		double minFactor = (Math.log(minimumScale) / Math.log(2)) * 10;
		double maxFactor = (Math.log(maximumScale) / Math.log(2)) * 10;

		//Check to see if we're out of bounds and if we are, halt
		if(curZoomFactor < minFactor)
			desZoomFactor = curZoomFactor = minFactor;

		else if(curZoomFactor > maxFactor)
			desZoomFactor = curZoomFactor = maxFactor;

		//Calculate the scale
		double scale = Math.pow(2, curZoomFactor / 10.0);

		return scale;
	}

	/**
	 * Converts the screen coordinate to the canvas coordinate
	 * @param p Screen point
	 * @return Corresponding canvase point
	 */
	private Point toRealCoords(Point p) {
		double scale = getScale();

		return new Point((int)(p.x/scale - moveX) ,(int)(p.y/scale - moveY) );
	}

	//*************************
	//MouseWheelListener Method
	//*************************
	public void mouseWheelMoved(MouseWheelEvent e) {
		System.out.println("wheel");
		int num = e.getWheelRotation();

		//desZoomFactor -= num;
		changeZoom(-num);

		repaint();
	}

	//*************************
	//MouseListener Methods
	//*************************
	public void mouseClicked(MouseEvent arg0){	}
	public void mousePressed(MouseEvent e) {
		System.out.println("pressed");
		boolean handled = false;

		if (e.getButton() == MouseEvent.BUTTON2) {
			if (e.getClickCount() == 2) {
				moveX = defaultMoveX;

				moveY = defaultMoveY;

				desZoomFactor = curZoomFactor = 0;
				repaint();
			}

			Point p = e.getPoint();

			lastX = p.x;
			lastY = p.y;

			handled = true;
		}
		else if (e.getButton() == MouseEvent.BUTTON1) {
			Point p = e.getPoint();

			if (toggle.contains(p)) {
				moveOn = !moveOn;
				repaint();
				handled = true;
			}
			else if (moveOn) {
				if (up.contains(p) || down.contains(p) || left.contains(p) || right.contains(p) ||
						in.contains(p) || out.contains(p)) {
					// set mouseDown
					mouseDown = true;
					mousePoint = p;
					handled = true;
				}
			}
		}

		if (!handled) {
			mousePressedAt(toRealCoords(e.getPoint()), e);
		}
	}

	public void mouseReleased(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON2) {
			Point p = e.getPoint();

			double dx = lastX - p.x;
			double dy = p.y - lastY;
			double scale = getScale();

			moveX += dx/scale;
			moveY += dy/scale;

			lastX = lastY = -1;

			repaint();
		}
		if (e.getButton() == MouseEvent.BUTTON1)
			mouseDown = false;

		mouseReleasedAt(toRealCoords(e.getPoint()), e);
	}

	public void mouseDragged(MouseEvent e) {  
		if (lastX != -1 && lastY != -1) {
			Point p = e.getPoint();

			double dx = p.x - lastX;
			double dy = p.y - lastY;
			double scale = getScale();

			moveX += dx/scale;
			moveY += dy/scale;

			lastX = p.x;
			lastY = p.y;

			repaint();
		}

		mouseDraggedAt(toRealCoords(e.getPoint()), e);
	}

	public void mouseEntered(MouseEvent e) {
		mouseEnteredAt(toRealCoords(e.getPoint()), e);
	}

	public void mouseExited(MouseEvent e) {
		mouseDown = false;

		mouseExitedAt(toRealCoords(e.getPoint()), e);
	}

	public void mouseMoved(MouseEvent e) {
		mouseMovedAt(toRealCoords(e.getPoint()), e);
	}


	//***********************************
	//Inherited Class mouse event methods
	//***********************************
	protected void mousePressedAt(Point p, MouseEvent e) {}
	protected void mouseDraggedAt(Point realPoint, MouseEvent e) {}
	protected void mouseReleasedAt(Point realPoint, MouseEvent e) {}
	protected void mouseEnteredAt(Point realPoint, MouseEvent e) {}
	protected void mouseExitedAt(Point realPoint, MouseEvent e) {}
	protected void mouseMovedAt(Point realPoint, MouseEvent e) {}

	private void changeZoom(double exp) {
		if((curZoomFactor < desZoomFactor && exp < 0) || curZoomFactor > desZoomFactor && exp > 0)
			desZoomFactor = curZoomFactor + exp;
		else
			desZoomFactor += exp;
	}

	//**********************
	//Display update threads
	//**********************

	class MoveThread extends Thread {
		public void run() {
			long lastTime = -1;

			while (true) {
				if (mouseDown) {
					long time  = new Date().getTime();
					double scale = getScale();
					double w = getWidth() / scale;
					double h = getHeight() / scale;
					long dif = time - lastTime;

					if (lastTime != -1) {
						double change = dif / 100.0;

						if (out.contains(mousePoint))
							curZoomFactor -= change;
						else if (in.contains(mousePoint))
							curZoomFactor += change;
						else if (up.contains(mousePoint))
							moveY += (int)(change * 50);
						else if (down.contains(mousePoint))
							moveY -= (int)(change * 50);
						else if (left.contains(mousePoint))
							moveX += (int)(change * 50);
						else if (right.contains(mousePoint))
							moveX -= (int)(change * 50);
					}

					double scaleAfter = getScale();
					double wAfter = getWidth() / scaleAfter;
					double hAfter = getHeight() / scaleAfter;

					double xGained = (wAfter - w);
					double yGained = (hAfter - h);

					double dx = xGained/2;
					double dy = yGained/2;

					moveX += dx;
					moveY += dy;

					desZoomFactor = curZoomFactor;
					repaint();
					lastTime = time;
				}
				else
					lastTime = -1;

				try {
					Thread.sleep(10);
				}
				catch (Exception e) { }
			}
		}
	}

	class PaintThread extends Thread {
		public void run() {
			System.out.println("paint start");
			long lastTime = -1;

			while (true) {
				System.out.println("repaint");
				if (Math.abs(curZoomFactor-desZoomFactor) > 0.5) {
					long time  = new Date().getTime();
					double scale = getScale();
					double w = getWidth() / scale;
					double h = getHeight() / scale;
					long dif = time - lastTime;

					if (lastTime != -1) {
						double change = dif / 100.0;

						if (curZoomFactor < desZoomFactor)
							curZoomFactor += change;
						else
							curZoomFactor -= change;
					}


					double scaleAfter = getScale();
					double wAfter = getWidth() / scaleAfter;
					double hAfter = getHeight() / scaleAfter;

					double xGained = (wAfter - w);
					double yGained = (hAfter - h);

					double dx = xGained/2;
					double dy = yGained/2;

					moveX += dx;
					moveY += dy;

					repaint();
					lastTime = time;
				}
				else
					lastTime = -1;

				try {
					Thread.sleep(100);
				}
				catch (Exception e) { }
			}
		}
	}
}
