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

/**
 * A helper thread class to periodically check to see if we should update a
 * remote calendar. When we find a calendar needs updating, we call the
 * CalendarRefresher class specified in the constructor.
 * 
 * @author Craig Knudsen, craig@k5n.us
 * @version $Id: RemoteCalendarUpdater.java,v 1.1 2007/12/19 01:58:13 cknudsen
 *          Exp $
 */
public class RemoteCalendarUpdater extends Thread {
	Repository repo;
	CalendarRefresher refresher;
	/** How long should we sleep between checking again? */
	public static int SLEEP_DURATION = 5000;

	/**
	 * Create a RemoteCalendarUpdater object. The caller should call
	 * <code>run</code> after creating the object.
	 * 
	 * @param repo
	 *          The Repository object
	 * @param refresher
	 *          The class to call to do the actual update if a calendar is found
	 *          to be in need of updating
	 */
	public RemoteCalendarUpdater(Repository repo, CalendarRefresher refresher) {
		super ();
		this.repo = repo;
		this.refresher = refresher;
	}

	public void run () {
		while ( true ) {
			for ( int i = 0; i < repo.getCalendars ().size (); i++ ) {
				Calendar c = repo.getCalendars ().elementAt ( i );
				if ( c.url != null ) {
					updateCalendarIfNeeded ( c );
				}
			}
			// Sleep for three seconds...
			try {
				sleep ( SLEEP_DURATION );
			} catch ( InterruptedException e1 ) {
				e1.printStackTrace ();
			}
		}
	}

	public void updateCalendarIfNeeded ( Calendar c ) {
		java.util.Calendar time = java.util.Calendar.getInstance ();
		time.setTimeInMillis ( c.lastUpdated );

		// Kludge: oops... 30 days in ms is larger than MAXINT (so it appears as
		// a negative).
		if ( c.updateIntervalMS <= 0 )
			c.updateIntervalMS = 1000 * 3600 * 14; // change to 14 days
		//System.out.println ( "Calendar: " + c.name );
		long currentTimeMS = java.util.Calendar.getInstance ().getTimeInMillis ();
		long updateTimeMS = c.lastUpdated + c.updateIntervalMS;
		//System.out.println ( " update interval: "
		//    + ( c.updateIntervalMS / ( 3600 * 1000 ) ) + " hours" );

		time.setTimeInMillis ( c.lastUpdated );
		// System.out.println ( " last updated: " + Utils.CalendarToYYYYMMDD ( time
		// ) );
		time.setTimeInMillis ( updateTimeMS );
		// System.out.println ( " next update: " + Utils.CalendarToYYYYMMDD ( time )
		// );

		if ( currentTimeMS >= updateTimeMS ) {
			// We need to update this calendar!
			// Calling refreshCalendar will start another thread, so it should
			// return immediately, so we may be refreshing multiple calendars
			// at the same time.
			// System.out.println ( "Updating calendar '" + c.name + "'" );
			refresher.refreshCalendar ( c );
		} else {
			/*
			 * long updateSecs = ( updateTimeMS - currentTimeMS ) / 1000; long mins =
			 * updateSecs / 60; long secs = updateSecs % 60; long hours = mins / 60;
			 * mins %= 60; System.out.println ( "Will need to update '" + c.name + "'
			 * in " + hours + " hrs, " + mins + " mins, " + secs + " secs" );
			 */
		}
	}
}
