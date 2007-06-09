package us.k5n.ui.calendar;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.util.Vector;

import javax.swing.JFrame;

/**
 * Simple test/demo class for CalendarPanel that illustrates how to use
 * the CalendarPanel class and can be used to view the appearance of
 * the CalendarPanel calss.
 * 
 * @author Craig Knudsen, craig@k5n.us
 * @version $Id$
 * 
 */
public class CalendarPanelTest extends JFrame implements CalendarDataRepository {
	private static final long serialVersionUID = 1000L;
	CalendarPanel cpanel;

	public CalendarPanelTest() {
		super ( "Calendar Test" );
		setSize ( 600, 600 );
		setDefaultCloseOperation ( JFrame.EXIT_ON_CLOSE );
		Container contentPane = getContentPane ();
		contentPane.setLayout ( new BorderLayout () );

		cpanel = new CalendarPanel ( this );
		contentPane.add ( cpanel, BorderLayout.CENTER );
		this.setVisible ( true );
	}

	/**
	 * Get events for the specified date. This method implements the
	 * CalendarDataRepository interface. The CalendarPanel class does not cache
	 * this info, so this method should be fast and implement its own caching.
	 * 
	 * @see CalendarDataRepository
	 * @return Vector of EventInstance objects.
	 */
	public Vector getEventInstancesForDate ( int year, int month, int day ) {
		Vector ret = new Vector ();
		ret.addElement ( new Event ( "Test event",
		    "This is a test event.\nTest description", year, month, day ) );
		if ( day % 3 == 0 ) {
			ret
			    .addElement ( new Event ( "Test 9:15",
			        "This is a test event.\nTest description", year, month, day, 9,
			        15, 0 ) );
		} else if ( day % 3 == 2 ) {
			ret
			    .addElement ( new Event ( "Test 12:30",
			        "This is a test event.\nTest description", year, month, day, 0,
			        30, 0 ) );
			ret.addElement ( new Event ( "Test 3:30pm",
			    "This is a test event.\nTest description", year, month, day, 15, 15,
			    0 ) );
		}
		if ( day % 5 == 0 ) {
			ret
			    .addElement ( new Event ( "Test 8:15 event",
			        "This is a test event.\nTest description", year, month, day, 8,
			        15, 0 ) );
		}
		return ret;
	}

	/**
	 * Main for test app
	 */
	public static void main ( String[] args ) {
		new CalendarPanelTest ();
	}

	/**
	 * Inner class that implements the EventInstance interface.
	 */
	class Event implements EventInstance {
		String title, description;
		int Y, M, D, h, m, s;
		boolean hasTime, allDay;
		Color fg, bg, border;
		Color[] colors = { Color.blue, Color.red, Color.orange, Color.pink,
		    Color.gray, Color.green, Color.yellow, Color.cyan, Color.magenta };

		public Event(String title, String description, int Y, int M, int D) {
			this ( title, description, Y, M, D, 0, 0, 0, false, false );
		}

		public Event(String title, String description, int Y, int M, int D, int h,
		    int m, int s) {
			this ( title, description, Y, M, D, h, m, s, true, false );
		}

		public Event(String title, String description, int Y, int M, int D, int h,
		    int m, int s, boolean hasTime, boolean allDay) {
			this.title = title;
			this.description = description;
			this.Y = Y;
			this.M = M;
			this.D = D;
			this.h = h;
			this.m = m;
			this.s = s;
			this.hasTime = hasTime;
			this.allDay = allDay;
			// this.fg = new Color ( D * 8 % 256, h * 10 % 256, m * 4 % 256 );
			this.bg = colors[ ( Y + M + D + h + m + s ) % colors.length];
			this.fg = new Color ( this.bg.getRed () / 2, this.bg.getGreen () / 2,
			    this.bg.getBlue () / 2 );
			this.border = this.fg;
		}

		public String getTitle () {
			return title;
		}

		public String getDescription () {
			return description;
		}

		public boolean isAllDay () {
			return allDay;
		}

		public boolean hasTime () {
			return hasTime;
		}

		public int getYear () {
			return Y;
		}

		public int getMonth () {
			return M;
		}

		public int getDayOfMonth () {
			return D;
		}

		public int getHour () {
			return h;
		}

		public int getMinute () {
			return m;
		}

		public int getSecond () {
			return s;
		}

		public boolean hasDuration () {
			return false;
		}
		
		public String getLocation ()
		{
			return null;
		}

		public int getDurationSeconds () {
			return 0;
		}

		public Color getForegroundColor () {
			return fg;
		}

		public Color getBackgroundColor () {
			return bg;
		}

		public Color getBorderColor () {
			return border;
		}

		/** Implement the Comparable interface so events can be sorted */
		public int compareTo ( Object o ) {
			EventInstance e2 = (EventInstance) o;
			if ( this.getYear () < e2.getYear () )
				return -1;
			else if ( this.getYear () > e2.getYear () )
				return 1;
			if ( this.getMonth () < e2.getMonth () )
				return -1;
			else if ( this.getMonth () > e2.getMonth () )
				return 1;
			if ( this.getDayOfMonth () < e2.getDayOfMonth () )
				return -1;
			else if ( this.getDayOfMonth () > e2.getDayOfMonth () )
				return 1;
			if ( !this.hasTime && e2.hasTime () )
				return -1;
			else if ( this.hasTime () && !e2.hasTime () )
				return 1;
			else if ( !this.hasTime && !e2.hasTime () )
				return 0;
			if ( this.isAllDay () && !e2.isAllDay () )
				return -1;
			if ( !this.isAllDay () && e2.isAllDay () )
				return 1;
			if ( this.isAllDay () && e2.isAllDay () )
				return 0;
			// both events have a time
			if ( this.getHour () < e2.getHour () )
				return -1;
			else if ( this.getHour () > e2.getHour () )
				return 1;
			if ( this.getMinute () < e2.getMinute () )
				return -1;
			else if ( this.getMinute () > e2.getMinute () )
				return 1;
			if ( this.getSecond () < e2.getSecond () )
				return -1;
			else if ( this.getSecond () > e2.getSecond () )
				return 1;

			return 0;
		}
	}

}
