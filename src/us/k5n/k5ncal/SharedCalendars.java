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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import edu.stanford.ejalbert.BrowserLauncher;

public class SharedCalendars {
	public static Vector<CalendarSite> sites;

	/**
	 * Populate the "Find Shared Calendars" submenu. Eventually, we will replace
	 * the hard-coded values with a web services (REST) call to obtain the list of
	 * shared calendars. But, for now, it's hard-coded :-)
	 * 
	 * @param menu
	 *          The "Find Shared Calendars" menu
	 */
	public static void updateSharedCalendars ( JMenu menu ) {
		sites = new Vector<CalendarSite> ();
		menu.removeAll ();
		initStaticData ();

		for ( int i = 0; i < sites.size (); i++ ) {
			CalendarSite site = sites.elementAt ( i );
			JMenu siteMenu = new JMenu ( site.name );
			menu.add ( siteMenu );
			for ( int j = 0; j < site.calendars.size (); j++ ) {
				SharedCalendar cal = site.calendars.elementAt ( j );
				JMenuItem item = new JMenuItem ( cal.name );
				siteMenu.add ( item );
				final String url = cal.url;
				item.addActionListener ( new ActionListener () {
					public void actionPerformed ( ActionEvent event ) {
						try {
							BrowserLauncher bl = new BrowserLauncher ();
							bl.openURLinBrowser ( url );
						} catch ( Exception e1 ) {
							System.err.println ( "Error starting web browser" + ": "
							    + e1.getMessage () );
							e1.printStackTrace ();
						}
					}
				} );
			}

		}

	}

	// This method to be replaced by a call to obtain this info dynamically.
	private static void initStaticData () {
		// Add iCalShare
		CalendarSite site = new CalendarSite ( "iCalShare" );
		site.addCalendar ( new SharedCalendar ( "Most Popular",
		    "http://icalshare.com/top.php" ) );
		site.addCalendar ( new SharedCalendar ( "Most Recent",
		    "http://icalshare.com/recent.php" ) );
		site.addCalendar ( new SharedCalendar ( "Holidays",
		    "http://icalshare.com/index.php?topic=holidays" ) );
		site.addCalendar ( new SharedCalendar ( "Sports: Football",
		    "http://icalshare.com/index.php?topic=football" ) );
		site.addCalendar ( new SharedCalendar ( "Sports: Soccer",
		    "http://icalshare.com/index.php?topic=soccer" ) );
		site.addCalendar ( new SharedCalendar ( "Sports: Hockey",
		    "http://icalshare.com/index.php?topic=hockey" ) );
		site.addCalendar ( new SharedCalendar ( "Sports: Baseball",
		    "http://icalshare.com/index.php?topic=baseball" ) );
		sites.add ( site );

		// Add iCal World
		site = new CalendarSite ( "iCal World" );
		site.addCalendar ( new SharedCalendar ( "Holidays",
		    "http://www.icalworld.com/holidays.html" ) );
		site.addCalendar ( new SharedCalendar ( "Sports",
		    "http://www.icalworld.com/sports.html" ) );
		site.addCalendar ( new SharedCalendar ( "Entertainment",
		    "http://www.icalworld.com/ent.html" ) );
		sites.add ( site );
	}

}

class CalendarSite {
	String name;
	Vector<SharedCalendar> calendars;

	public CalendarSite(String name) {
		this.name = name;
		this.calendars = new Vector<SharedCalendar> ();
	}

	public void addCalendar ( SharedCalendar calendar ) {
		this.calendars.addElement ( calendar );
	}
}

class SharedCalendar {
	public String url;
	public String name;

	public SharedCalendar(String name, String url) {
		this.url = url;
		this.name = name;
	}
}
