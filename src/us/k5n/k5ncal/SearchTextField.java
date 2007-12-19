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
import java.awt.Cursor;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.ImageIcon;
import javax.swing.JTextField;

public class SearchTextField extends JTextField implements MouseListener,
    MouseMotionListener {
	public static ImageIcon clearImage = null;
	private static byte[] clearImageBytes = { 71, 73, 70, 56, 57, 97, 14, 0, 14,
	    0, -124, 26, 0, -60, 124, 124, -58, -126, -126, -58, -125, -125, -57,
	    -125, -125, -54, -118, -118, -54, -117, -117, -52, -115, -115, -52, -114,
	    -114, -50, -109, -109, -50, -108, -108, -49, -108, -108, -47, -103, -103,
	    -43, -94, -94, -43, -93, -93, -38, -83, -83, -36, -78, -78, -36, -77,
	    -77, -35, -77, -77, -33, -71, -71, -31, -67, -67, -26, -55, -55, -5, -8,
	    -8, -4, -8, -8, -4, -6, -6, -3, -5, -5, -2, -3, -3, -1, -1, -1, -1, -1,
	    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 33, -2, 21, 67, 114,
	    101, 97, 116, 101, 100, 32, 119, 105, 116, 104, 32, 84, 104, 101, 32, 71,
	    73, 77, 80, 0, 44, 0, 0, 0, 0, 14, 0, 14, 0, 0, 5, 80, -32, -11, 12, 64,
	    105, -106, 2, 36, -98, -84, 9, -111, -91, 113, 18, 38, 12, 72, 24, 83,
	    46, -43, -60, 30, 24, 77, -114, -89, -55, 40, 88, -115, 32, -58, 82, 116,
	    -76, 0, 73, 77, -13, 9, 88, 48, -123, -70, 22, 49, -93, -52, -102, 10,
	    -107, 102, 20, -125, 96, 77, 50, 78, 40, -122, 98, 10, -104, -114, -90,
	    -124, 73, -16, -96, -98, 84, 16, -9, 51, 16, -71 };
	int buttonX = -1, buttonY = -1;
	boolean iconVisible = false;
	boolean cursorChanged = false;
	private Cursor defaultCursor = null;
	private Cursor buttonCursor = null;
	private Color hintColor = null;
	public static String HINT = "Enter search text";

	public SearchTextField() {
		super ();
		this.addMouseListener ( this );
		this.addMouseMotionListener ( this );
	}

	public void paint ( Graphics g ) {
		int x, y;
		super.paint ( g );
		if ( clearImage == null ) {
			clearImage = new ImageIcon ( clearImageBytes );
		}
		if ( hintColor == null ) {
			// Make hint color a very light grey
			// TODO: derive this from current colors
			hintColor = new Color ( 200, 200, 200 );
		}

		if ( this.getText ().length () > 0 ) {
			x = this.getWidth () - clearImage.getIconWidth () - 5;
			y = ( this.getHeight () - clearImage.getIconHeight () ) / 2;
			clearImage.paintIcon ( this, g, x, y );
			this.buttonX = x;
			this.buttonY = y;
			this.iconVisible = true;
		} else {
			this.iconVisible = false;
			// No text. Draw hint
			FontMetrics fm = g.getFontMetrics ();
			y = ( this.getHeight () - fm.getHeight () ) / 2 + fm.getAscent ();
			x = ( this.getWidth () - fm.stringWidth ( HINT ) ) / 2;
			g.setColor ( hintColor );
			g.drawString ( HINT, x, y );
		}
	}

	public void mouseEntered ( MouseEvent e ) {
	}

	public void mouseReleased ( MouseEvent e ) {
	}

	public void mouseClicked ( MouseEvent e ) {
	}

	public void mouseExited ( MouseEvent e ) {
	}

	private boolean isOverClearButton ( Point p ) {
		return ( this.iconVisible && p.x >= this.buttonX
		    && p.x <= this.buttonX + clearImage.getIconWidth ()
		    && p.y >= this.buttonY && p.y <= this.buttonY
		    + clearImage.getIconHeight () );
	}

	public void mousePressed ( MouseEvent e ) {
		if ( this.isOverClearButton ( e.getPoint () ) ) {
			this.setText ( "" );
			this.fireActionPerformed ();
			this.repaint ();
		}
	}

	public void mouseMoved ( MouseEvent e ) {
		if ( this.isOverClearButton ( e.getPoint () ) ) {
			if ( !this.cursorChanged ) {
				// change cursor
				if ( this.defaultCursor == null ) {
					this.defaultCursor = this.getCursor ();
				}
				if ( this.buttonCursor == null ) {
					this.buttonCursor = new Cursor ( Cursor.DEFAULT_CURSOR );
				}
				this.setCursor ( this.buttonCursor );
				this.cursorChanged = true;
			}
		} else {
			if ( this.cursorChanged ) {
				this.setCursor ( this.defaultCursor );
				this.cursorChanged = false;
			}
		}
	}

	public void mouseDragged ( MouseEvent e ) {
	}

}
