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

import us.k5n.k5ncal.data.Calendar;

/**
 * Defines the interface of an object that will relaod a remote calendar (read
 * in from HTTP and save it locally)
 * 
 * @author Craig Knudsen, craig@k5n.us
 * @version $Id$
 */
public interface CalendarRefresher {

	/**
	 * Refresh the specified calendar by reloading it from its URL. Because this
	 * is likely to take a second or more in ideal circumstances (and much longer
	 * in many cases), we will use the SwingWorker class to execute this in a
	 * separate thread so we don't lock up the UI.
	 * 
	 * @param cal
	 *          The Calendar to refresh
	 */
	public abstract void refreshCalendar ( final Calendar cal );

}
