package us.k5n.k5ncal;

import java.awt.Color;
import java.io.File;
import java.io.Serializable;
import java.net.URL;
import java.util.Random;

/**
 * The Calendar class represents a single user calendar, either local or remote.
 * 
 * @author Craig Knudsen, craig@k5n.us
 * @version $Id$
 * 
 */
public class Calendar implements Serializable {
	private static final long serialVersionUID = 1000L;
	String name;
	String filename;
	long lastUpdated; // Time of last update (in ms since 1970) */
	URL url;
	int updateIntervalMS; // ms between updates
	boolean selected = true;
	Color fg = Color.WHITE;
	Color bg = Color.BLUE;
	Color border = Color.BLACK;
	private static Random random = new Random ( ( new java.util.Date () )
	    .getTime () );

	public Calendar(File dir, String name) {
		this.name = name;
		this.lastUpdated = 0;
		// Generate a unique
		this.filename = generateFileName ( dir );
		this.url = null;
		updateIntervalMS = 0;
	}

	public Calendar(File dir, String name, URL url, int updateIntervalHours) {
		this.name = name;
		this.url = url;
		this.lastUpdated = 0;
		// Generate a unique
		this.filename = generateFileName ( dir );
		this.updateIntervalMS = updateIntervalHours * 1000 * 3600;
	}
	
	public String toString ()
	{
		return name;
	}

	public void setUpdated () {
		java.util.Calendar c = java.util.Calendar.getInstance ();
		this.lastUpdated = c.getTimeInMillis ();
	}

	public boolean needsUpdating () {
		if ( url == null )
			return false;
		if ( lastUpdated == 0 )
			return true;
		java.util.Calendar c = java.util.Calendar.getInstance ();
		java.util.Calendar c2 = java.util.Calendar.getInstance ();
		c2.setTimeInMillis ( this.lastUpdated + this.updateIntervalMS );
		return ( c.after ( c2 ) );
	}

	/**
	 * Generate a unique filename
	 * 
	 * @param dir
	 * @return
	 */
	protected String generateFileName ( File dir ) {
		for ( ;; ) {
			String name = "cal_" + Math.abs ( random.nextInt () ) + ".ics";
			if ( ! ( new File ( dir, name ) ).exists () )
				return name;
		}
	}
}
