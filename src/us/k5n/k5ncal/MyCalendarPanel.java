/*
 * k5nCal - Java Swing Desktop Calendar App
 * Copyright (C) 2005-2007 Craig Knudsen, craig@k5n.us
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
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
