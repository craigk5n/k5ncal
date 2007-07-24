package us.k5n.k5ncal;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import us.k5n.ui.calendar.CalendarDataRepository;
import us.k5n.ui.calendar.CalendarPanel;

/**
 * Extend the CalendarPanel class to draw with a gradient background.
 * 
 * @version $Id$
 * @author Craig Knudsen, craig@k5n.us
 */
public class MyCalendarPanel extends CalendarPanel {
	private boolean useGradientBackground = true;

	public MyCalendarPanel(CalendarDataRepository repo) {
		super ( repo );
	}

	/**
	 * Specify whether a gradient background should be used for background colors.
	 * If enabled, then the background will start at the lower right corner of
	 * each table cell with the specified background color. The color will slowly
	 * change towards the upper left corner, where the color will be the average
	 * of the background color and white.
	 * 
	 * @param useGradient
	 *          Should gradient backgrounds be used?
	 */
	public void setUseGradientBackground ( boolean useGradient ) {
		this.useGradientBackground = useGradient;
	}

	/**
	 * Override the default method so we can draw with a gradient background.
	 * 
	 * @param g
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param bottomColor
	 */
	public void drawDayOfMonthBackground ( Graphics g, int x, int y, int w,
	    int h, Color bottomColor ) {
		if ( this.useGradientBackground ) {
			Color topColor = new Color ( ( 255 + bottomColor.getRed () ) / 2,
			    ( 255 + bottomColor.getGreen () ) / 2,
			    ( 255 + bottomColor.getBlue () ) / 2 );
			Graphics2D g2 = (Graphics2D) g;
			GradientPaint gp = new GradientPaint ( x, y, topColor, x + w, y + h,
			    bottomColor );
			g2.setPaint ( gp );
			g2.fillRect ( x, y, w, h );
		} else {
			g.setColor ( bottomColor );
			g.fillRect ( x, y, w, h );
		}
	}
}
