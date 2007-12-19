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
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.util.HashMap;

import javax.swing.ImageIcon;

/**
 * This class generates the two checkbox icons (selected and unselected)
 * customized to specific colors. It makes the assumption that the base icons
 * (selectedImageBytes and unselectedImageBytes below) have values of white for
 * the area to be filled with the calendar color, and red for the opposing color
 * (the contrasting color that represents the check mark).
 * 
 * @author Craig Knudsen, craig@k5n.us
 * @version $Id$
 */
public class ListItemIconFactory {
	private static HashMap<Color, ImageIcon> selectedIcons = null;
	private static HashMap<Color, ImageIcon> unselectedIcons = null;
	// The following is GIF image binary data encoded using a helper utility.
	private static byte[] selectedImageBytes = { 71, 73, 70, 56, 57, 97, 14, 0,
	    13, 0, -29, 9, 0, 4, 4, 4, -1, 0, 0, 124, 124, 124, -128, -128, -128,
	    -68, -68, -68, -64, -64, -64, -36, -36, -36, -4, -4, -4, -1, -1, -1, 0,
	    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 44, 0, 0, 0,
	    0, 14, 0, 13, 0, 0, 4, 62, -112, 12, 65, -85, 29, -90, 8, -64, -69, 47,
	    -122, 4, 28, 100, 73, 6, -96, 54, -102, 39, 26, 14, -85, 25, 4, 72, -70,
	    29, 65, 57, -25, -96, -120, -25, 59, -110, 109, -124, -40, -27, -124,
	    -81, 85, -80, 52, -44, -79, 82, 48, -106, 9, 90, -88, 90, -81, 61, -125,
	    118, -53, -35, 70, 0, 0, 59 };
	// The following is GIF image binary data encoded using a helper utility.
	private static byte[] unselectedImageBytes = { 71, 73, 70, 56, 57, 97, 14, 0,
	    13, 0, -62, 7, 0, 4, 4, 4, 124, 124, 124, -128, -128, -128, -68, -68,
	    -68, -64, -64, -64, -36, -36, -36, -1, -1, -1, 0, 0, 0, 33, -2, 17, 67,
	    114, 101, 97, 116, 101, 100, 32, 119, 105, 116, 104, 32, 71, 73, 77, 80,
	    0, 44, 0, 0, 0, 0, 14, 0, 13, 0, 0, 3, 47, 56, 18, -36, 46, -123, 4, 64,
	    -85, 37, 69, 1, -61, 123, -57, -46, -26, 121, -104, 54, -110, -47, 116,
	    126, -103, 32, -82, -96, -70, 26, -91, 59, -45, -23, 123, -126, -10, -52,
	    19, -64, -96, -80, 84, 40, 26, -113, -58, 4, 0, 59 };
	private static int maxWidth = 0;

	public static ImageIcon getSelectedIcon ( Color fillColor, Color checkColor ) {
		init ();
		ImageIcon icon = selectedIcons.get ( fillColor );
		if ( icon == null ) {
			icon = new ImageIcon ( selectedImageBytes );
			// Colorize icon
			icon = generateIcon ( icon.getImage (), icon.getIconWidth (), icon
			    .getIconHeight (), fillColor, checkColor );
			selectedIcons.put ( fillColor, icon );
			if ( icon.getIconWidth () > maxWidth )
				maxWidth = icon.getIconWidth ();
		}
		return icon;
	}

	public static ImageIcon getUnselectedIcon ( Color color ) {
		init ();
		ImageIcon icon = unselectedIcons.get ( color );
		if ( icon == null ) {
			icon = new ImageIcon ( unselectedImageBytes );
			// colorize icon
			icon = generateIcon ( icon.getImage (), icon.getIconWidth (), icon
			    .getIconHeight (), color, null );
			unselectedIcons.put ( color, icon );
			if ( icon.getIconWidth () > maxWidth )
				maxWidth = icon.getIconWidth ();
		}
		return icon;
	}

	public static int getIconWidth () {
		return maxWidth;
	}

	public static ImageIcon generateIcon ( Image templateImage, int width,
	    int height, Color fillColor, Color checkColor ) {
		int[] pixels = new int[width * height];
		PixelGrabber pg = new PixelGrabber ( templateImage, 0, 0, width, height,
		    pixels, 0, width );
		try {
			pg.grabPixels ( 0 );
		} catch ( InterruptedException e ) {
			System.err.println ( e.toString () );
			e.printStackTrace ();
		}
		BufferedImage bufimage = new BufferedImage ( width, height,
		    BufferedImage.TYPE_INT_ARGB );
		Graphics g = bufimage.getGraphics ();
		for ( int row = 0; row < height; row++ ) {
			for ( int col = 0; col < width; col++ ) {
				int pixel = pixels[ ( row * width ) + col];
				int alpha = ( pixel >> 24 ) & 0xff;
				int red = ( pixel >> 16 ) & 0xff;
				int green = ( pixel >> 8 ) & 0xff;
				int blue = ( pixel ) & 0xff;

				// System.out.println ( "(" + row + "," + col + ")=" + red + "," + green
				// + "," + blue );
				if ( red >= 250 && green >= 250 && blue >= 250 ) {
					// Found the fill area
					// System.out.println ( "Replacing (" + row + "," + col + ") with "
					// + fillColor );
					g.setColor ( fillColor );
					g.drawLine ( col, row, col, row );
				} else if ( red >= 250 && green <= 50 && blue <= 50
				    && checkColor != null ) {
					// Found the checkmark
					// System.out.println ( "Replacing (" + row + "," + col + ") with "
					// + checkColor );
					g.setColor ( checkColor );
					g.drawLine ( col, row, col, row );
				} else {
					Color origColor = new Color ( red, green, blue );
					g.setColor ( origColor );
					g.drawLine ( col, row, col, row );
				}
			}
		}
		g.dispose ();
		return new ImageIcon ( bufimage );
	}

	private static void init () {
		if ( selectedIcons == null )
			selectedIcons = new HashMap<Color, ImageIcon> ();
		if ( unselectedIcons == null )
			unselectedIcons = new HashMap<Color, ImageIcon> ();
	}

}
