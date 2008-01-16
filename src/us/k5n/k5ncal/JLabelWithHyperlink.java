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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;

import edu.stanford.ejalbert.BrowserLauncher;

/**
 * Override the JLabel class with a hyperlink so users can click on it to open
 * up their preferred browser to the URL in the JLabel.
 * 
 * @author Craig Knudsen, craig@k5n.us
 * @version $Id$
 */
public class JLabelWithHyperlink extends JLabel {
	private static Cursor handCursor = null, defaultCursor = null;

	public JLabelWithHyperlink(String label) {
		super ( label );

		this.setForeground ( Color.blue );
		if ( defaultCursor == null ) {
			defaultCursor = this.getCursor ();
			handCursor = Cursor.getPredefinedCursor ( Cursor.HAND_CURSOR );
		}

		this.addMouseListener ( new MouseListener () {
			public void mouseEntered ( MouseEvent e1 ) {
				setCursor ( handCursor );
			}

			public void mouseExited ( MouseEvent e1 ) {
				setCursor ( defaultCursor );
			}

			public void mouseClicked ( MouseEvent e1 ) {
			}

			public void mousePressed ( MouseEvent e1 ) {
			}

			public void mouseReleased ( MouseEvent e1 ) {
				String urlStr = getText ();
				if ( urlStr != null && urlStr.trim ().startsWith ( "http" ) ) {
					try {
						BrowserLauncher bl = new BrowserLauncher ();
						bl.openURLinBrowser ( urlStr );
					} catch ( Exception e ) {
						System.err.println ( "Error starting web browser" + ": "
						    + e.getMessage () );
						e.printStackTrace ();
					}
				}
			}
		} );
	}

}
