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

package us.k5n.k5ncal.data;

import java.awt.Color;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import us.k5n.k5ncal.Utils;

/**
 * The Calendar class represents a single user calendar, either local or remote.
 * 
 * @author Craig Knudsen, craig@k5n.us
 * @version $Id$
 * 
 */
public class Calendar implements Serializable {
	private static final long serialVersionUID = 1001L;
	public static final int UNKNOWN_CALENDAR = -1;
	public static final int LOCAL_CALENDAR = 1;
	public static final int REMOTE_ICAL_CALENDAR = 2;
	private String name = null;
	private String filename = null;
	/** LOCAL_CALENDAR, REMOTE_ICAL_CALENDAR */
	private int type = UNKNOWN_CALENDAR;
	private long lastUpdated = 0; // Time of last update (in ms since 1970) */
	private URL url = null;
	private int updateIntervalSecs = 0; // seconds between updates
	private int authType = AUTH_NONE;
	private String authUsername = null;
	private String authPassword = null;
	private boolean selected = true;
	private Color fg = Color.WHITE;
	private Color bg = Color.BLUE;
	private Color border = Color.BLACK;
	private static Random random = new Random ( ( new java.util.Date () )
	    .getTime () );
	public static final int AUTH_NONE = 0;
	public static final int AUTH_BASIC = 1;
	private boolean canWrite = false; // For remote calendars
	private boolean syncBeforePublish = false; // For remote calendars

	public Calendar(File dir, String name) {
		this.name = name;
		this.type = LOCAL_CALENDAR;
		this.lastUpdated = 0;
		// Generate a unique
		this.filename = generateFileName ( dir );
		this.url = null;
		updateIntervalSecs = 0;
	}

	public Calendar(File dir, String name, URL url, int updateIntervalHours) {
		this.name = name;
		this.type = REMOTE_ICAL_CALENDAR;
		this.url = url;
		this.lastUpdated = 0;
		// Generate a unique
		this.filename = generateFileName ( dir );
		this.updateIntervalSecs = updateIntervalHours * 3600;
		// kludge: should have made this a long since 30 days exceeds
		// the max int for ms.
		if ( this.updateIntervalSecs < 0 )
			this.updateIntervalSecs = 3600 * 14; // 14 days default
	}

	public Calendar(Node topNode) {
		NodeList list = topNode.getChildNodes ();
		int len = list.getLength ();

		this.type = -1;

		String calType = Utils.xmlNodeGetAttribute ( topNode, "type" );
		if ( calType != null && calType.equalsIgnoreCase ( "local" ) )
			this.type = LOCAL_CALENDAR;
		else if ( calType != null && calType.equalsIgnoreCase ( "remote-ical" ) )
			this.type = REMOTE_ICAL_CALENDAR;

		for ( int i = 0; i < len; i++ ) {
			Node n = list.item ( i );
			if ( n.getNodeType () == Node.ELEMENT_NODE ) {
				String nodeName = n.getNodeName ();
				if ( "name".equals ( nodeName ) ) {
					this.name = Utils.xmlNodeGetValue ( n );
				} else if ( "filename".equals ( nodeName ) ) {
					this.filename = Utils.xmlNodeGetValue ( n );
				} else if ( "lastUpdated".equals ( nodeName ) ) {
					this.lastUpdated = Long.parseLong ( Utils.xmlNodeGetValue ( n ) );
				} else if ( "url".equals ( nodeName ) ) {
					try {
						this.url = new URL ( Utils.xmlNodeGetValue ( n ) );
					} catch ( MalformedURLException e1 ) {
						System.err.println ( "Invalid URL in calendar data: "
						    + Utils.xmlNodeGetValue ( n ) );
					}
				} else if ( "updateIntervalSecs".equals ( nodeName ) ) {
					this.updateIntervalSecs = Integer.parseInt ( Utils
					    .xmlNodeGetValue ( n ) );
				} else if ( "authType".equals ( nodeName ) ) {
					String typeStr = Utils.xmlNodeGetValue ( n );
					if ( typeStr.equalsIgnoreCase ( "basic" ) )
						this.authType = AUTH_BASIC;
					else if ( typeStr.equalsIgnoreCase ( "none" ) )
						this.authType = AUTH_NONE;
					else
						System.err.println ( "Invalid auth type '" + typeStr
						    + "' found in calendar '" + this.name + "'." );
				} else if ( "authUsername".equals ( nodeName ) ) {
					this.authUsername = Utils.xmlNodeGetValue ( n );
				} else if ( "authPassword".equals ( nodeName ) ) {
					this.authPassword = Utils.xmlNodeGetValue ( n );
				} else if ( "selected".equals ( nodeName ) ) {
					String s = Utils.xmlNodeGetValue ( n );
					this.selected = s.toUpperCase ().startsWith ( "T" )
					    || s.toUpperCase ().startsWith ( "Y" );
				} else if ( "canWrite".equals ( nodeName ) ) {
					String s = Utils.xmlNodeGetValue ( n );
					this.canWrite = s.toUpperCase ().startsWith ( "T" )
					    || s.toUpperCase ().startsWith ( "Y" );
				} else if ( "syncBeforePublish".equals ( nodeName ) ) {
					String s = Utils.xmlNodeGetValue ( n );
					this.syncBeforePublish = s.toUpperCase ().startsWith ( "T" )
					    || s.toUpperCase ().startsWith ( "Y" );
				} else if ( "foregroundColor".equals ( nodeName ) ) {
					this.fg = Utils.parseColor ( Utils.xmlNodeGetValue ( n ) );
				} else if ( "backgroundColor".equals ( nodeName ) ) {
					this.bg = Utils.parseColor ( Utils.xmlNodeGetValue ( n ) );
				} else if ( "borderColor".equals ( nodeName ) ) {
					this.border = Utils.parseColor ( Utils.xmlNodeGetValue ( n ) );
				} else {
					System.err.println ( "Not sure what to do with <" + nodeName
					    + "> tag (ignoring) in <product>" );
				}
			}
		}

		if ( this.type == -1 ) {
			System.err.println ( "Error: no type attribute for calendar '"
			    + this.name + "'" );
			if ( this.url == null )
				this.type = LOCAL_CALENDAR;
			else
				this.type = REMOTE_ICAL_CALENDAR;
		}

		if ( filename == null ) {
			System.err.println ( "Error: no filename in <calendar> entry" );
		}
	}

	public String toString () {
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
		c2.setTimeInMillis ( this.lastUpdated + this.updateIntervalSecs * 1000 );
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

	/**
	 * Generate the XML representation of this Calendar object so that we may
	 * store it in an XML file.
	 * 
	 * @return
	 */
	public String toXML () {
		StringBuffer sb = new StringBuffer ();

		sb.append ( "  <calendar type=\"" );
		if ( this.type == LOCAL_CALENDAR )
			sb.append ( "local" );
		else if ( this.type == REMOTE_ICAL_CALENDAR )
			sb.append ( "remote-ical" );
		sb.append ( "\">\n" );
		sb.append ( "    <name>" );
		sb.append ( Utils.escape ( this.name ) );
		sb.append ( "</name>\n" );

		sb.append ( "    <filename>" );
		sb.append ( Utils.escape ( this.filename ) );
		sb.append ( "</filename>\n" );

		sb.append ( "    <lastUpdated>" );
		sb.append ( this.lastUpdated );
		sb.append ( "</lastUpdated>\n" );

		if ( this.url != null ) {
			sb.append ( "    <url>" );
			sb.append ( Utils.escape ( this.url.toString () ) );
			sb.append ( "</url>\n" );

			sb.append ( "    <canWrite>" );
			sb.append ( this.canWrite ? "true" : "false" );
			sb.append ( "</canWrite>\n" );

			sb.append ( "    <syncBeforePublish>" );
			sb.append ( this.syncBeforePublish ? "true" : "false" );
			sb.append ( "</syncBeforePublish>\n" );
		}

		sb.append ( "    <updateIntervalSecs>" );
		sb.append ( this.updateIntervalSecs );
		sb.append ( "</updateIntervalSecs>\n" );

		sb.append ( "    <selected>" );
		sb.append ( this.selected ? "true" : "false" );
		sb.append ( "</selected>\n" );

		if ( this.authType != AUTH_NONE ) {
			sb.append ( "    <authType>basic</authType>\n" );
			sb.append ( "    <authUsername>" );
			sb.append ( this.authUsername == null ? "" : Utils
			    .escape ( this.authUsername ) );
			sb.append ( "</authUsername>\n" );
			sb.append ( "    <authPassword>" );
			sb.append ( this.authPassword == null ? "" : Utils
			    .escape ( this.authPassword ) );
			sb.append ( "</authPassword>\n" );
		}

		sb.append ( "    <foregroundColor>#" );
		sb.append ( Utils.intToHex ( this.fg.getRed () ) );
		sb.append ( Utils.intToHex ( this.fg.getGreen () ) );
		sb.append ( Utils.intToHex ( this.fg.getBlue () ) );
		sb.append ( "</foregroundColor>\n" );

		sb.append ( "    <backgroundColor>#" );
		sb.append ( Utils.intToHex ( this.bg.getRed () ) );
		sb.append ( Utils.intToHex ( this.bg.getGreen () ) );
		sb.append ( Utils.intToHex ( this.bg.getBlue () ) );
		sb.append ( "</backgroundColor>\n" );

		sb.append ( "    <borderColor>#" );
		sb.append ( Utils.intToHex ( this.border.getRed () ) );
		sb.append ( Utils.intToHex ( this.border.getGreen () ) );
		sb.append ( Utils.intToHex ( this.border.getBlue () ) );
		sb.append ( "</borderColor>\n" );

		sb.append ( "  </calendar>\n" );
		return sb.toString ();
	}

	public static void writeCalendars ( File file, Vector<Calendar> calendars )
	    throws IOException {
		OutputStream os = new FileOutputStream ( file );
		os.write ( "<calendars>\n".getBytes () );
		for ( int i = 0; i < calendars.size (); i++ ) {
			Calendar c = calendars.elementAt ( i );
			os.write ( c.toXML ().getBytes () );
		}
		os.write ( "</calendars>\n".getBytes () );
		os.close ();
	}

	public static Vector<Calendar> readCalendars ( File file )
	    throws IOException, ParserConfigurationException, SAXException {
		Vector<Calendar> ret = new Vector<Calendar> ();
		InputStream is = new FileInputStream ( file );
		DataInputStream ds = new DataInputStream ( is );
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance ();
		DocumentBuilder builder = factory.newDocumentBuilder ();
		Document document = builder.parse ( ds );
		is.close ();

		NodeList list = document.getElementsByTagName ( "calendars" );
		int len = list.getLength ();
		if ( list.getLength () < 1 ) {
			System.err.println ( "Error: no <calendars> tag found in " + file );
			System.exit ( 1 );
		}
		Node topNode = list.item ( 0 );
		list = topNode.getChildNodes ();
		len = list.getLength ();

		for ( int i = 0; i < len; i++ ) {
			Node n = list.item ( i );

			if ( n.getNodeType () == Node.ELEMENT_NODE ) {
				String nodeName = n.getNodeName ();
				if ( "calendar".equals ( nodeName ) ) {
					Calendar c = new Calendar ( n );
					ret.addElement ( c );
				}
			}
		}

		return ret;
	}

	public String getName () {
		return name;
	}

	public void setName ( String name ) {
		this.name = name;
	}

	public String getFilename () {
		return filename;
	}

	public void setFilename ( String filename ) {
		this.filename = filename;
	}

	public int getType () {
		return type;
	}

	public void setType ( int type ) {
		this.type = type;
	}

	public long getLastUpdated () {
		return lastUpdated;
	}

	public void setLastUpdated ( long lastUpdated ) {
		this.lastUpdated = lastUpdated;
	}

	public void setLastUpdatedAsNow () {
		this.lastUpdated = java.util.Calendar.getInstance ().getTimeInMillis ();
	}

	public URL getUrl () {
		return url;
	}

	public void setUrl ( URL url ) {
		this.url = url;
	}

	public int getUpdateIntervalSecs () {
		return updateIntervalSecs;
	}

	public void setUpdateIntervalSecs ( int updateIntervalSecs ) {
		this.updateIntervalSecs = updateIntervalSecs;
	}

	public boolean isSelected () {
		return selected;
	}

	public void setSelected ( boolean selected ) {
		this.selected = selected;
	}

	public Color getForegroundColor () {
		return fg;
	}

	public void setForegroundColor ( Color fg ) {
		this.fg = fg;
	}

	public Color getBackgroundColor () {
		return bg;
	}

	public void setBackgroundColor ( Color bg ) {
		this.bg = bg;
	}

	public Color getBorderColor () {
		return border;
	}

	public void setBorderColor ( Color border ) {
		this.border = border;
	}

	public int getAuthType () {
		return authType;
	}

	public void setAuthType ( int authType ) {
		this.authType = authType;
	}

	public String getAuthUsername () {
		return authUsername;
	}

	public void setAuthUsername ( String authUsername ) {
		this.authUsername = authUsername;
	}

	public String getAuthPassword () {
		return authPassword;
	}

	public void setAuthPassword ( String authPassword ) {
		this.authPassword = authPassword;
	}

	public boolean getCanWrite () {
		return canWrite;
	}

	public void setCanWrite ( boolean canWrite ) {
		this.canWrite = canWrite;
	}

	public boolean getSyncBeforePublish () {
		return syncBeforePublish;
	}

	public void setSyncBeforePublish ( boolean syncBeforePublish ) {
		this.syncBeforePublish = syncBeforePublish;
	}

}
