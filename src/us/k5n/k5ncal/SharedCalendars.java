package us.k5n.k5ncal;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

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
							System.err.println ( "Error starting web browser: "
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
		// Add Apple calendars
		CalendarSite site = new CalendarSite ( "Apple" );
		site.addCalendar ( new SharedCalendar ( "Most Popular",
		    "http://www.apple.com/downloads/macosx/calendars/index_top.html" ) );
		site.addCalendar ( new SharedCalendar ( "Most Recent",
		    "http://www.apple.com/downloads/macosx/calendars/index.html" ) );
		sites.add ( site );

		// Add iCalShare
		site = new CalendarSite ( "iCalShare" );
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
