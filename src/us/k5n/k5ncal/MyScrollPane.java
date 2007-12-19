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

import java.awt.Component;

import javax.swing.JScrollPane;

/**
 * Override the Swing JScrollPane class so that vertical scrollbars are always
 * visible on Mac OS X. (This is suggested by Apple's style guide.)
 * 
 * @author Craig Knudsen, craig@k5n.us
 * @version $Id$
 */
public class MyScrollPane extends JScrollPane {

	public MyScrollPane(Component comp) {
		super ( comp );
		if ( System.getProperty ( "mrj.version" ) != null )
			this.setVerticalScrollBarPolicy ( JScrollPane.VERTICAL_SCROLLBAR_ALWAYS );
	}

}
