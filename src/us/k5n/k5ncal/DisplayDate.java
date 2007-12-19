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

import java.text.SimpleDateFormat;

import us.k5n.ical.Date;

/**
 * Repackage a us.k5n.ical.Date object so that we can format the date for
 * display using SimpleDateFormat.
 * 
 * @author Craig Knudsen, craig@k5n.us
 * @version $Id$
 * 
 */
public class DisplayDate implements Comparable {
	// Date formats are specified in the Java API doc.
	// TODO: allow setting this format in user preferences
	private static String dateOnlyFormat = "EEE, d MMM yyyy";
	private static String dateTimeFormat = "EEE, d MMM yyyy h:mm a";
	private java.util.Date javaDate;
	boolean hasTime;
	Object userData;

	public DisplayDate(Date d) {
		this ( d, null );
	}

	public DisplayDate(Date d, Object userData) {
		if ( d == null ) {
			javaDate = null;
			hasTime = false;
		} else {
			javaDate = d.toCalendar ().getTime ();
			hasTime = !d.isDateOnly ();
		}
		this.userData = userData;
	}

	public String toString () {
		SimpleDateFormat format = null;
		if ( javaDate == null )
			return "Unknown Date";
		if ( hasTime )
			format = new SimpleDateFormat ( dateTimeFormat );
		else
			format = new SimpleDateFormat ( dateOnlyFormat );
		return format.format ( javaDate );
	}

	public Object getUserData () {
		return this.userData;
	}

	public int compareTo ( Object arg0 ) {
		DisplayDate d2 = (DisplayDate) arg0;
		return javaDate.compareTo ( d2.javaDate );
	}

}
