/*
 * Copyright (C) 2005-2007 Craig Knudsen
 *
 * k5nEvent is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 * 
 * A copy of the GNU Lesser General Public License can be found at www.gnu.org. 
 * To receive a hard copy, you can write to:
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307 USA.
 */
package us.k5n.k5ncal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import us.k5n.ical.Constants;
import us.k5n.ical.DataStore;
import us.k5n.ical.Event;
import us.k5n.ical.ICalendarParser;
import us.k5n.ical.ParseError;

/**
 * Extend the File class to include iCalendar data created from parsing the
 * file. Normally, the application will just store a single Event entry in each
 * file. However, if a user copies an ICS file into their directory, we don't
 * want to loose track of the original filename to avoid creating duplicates.
 * 
 * @author Craig Knudsen, craig@k5n.us
 * @version $Id$
 */
public class DataFile extends File implements Constants {
	ICalendarParser parser;
	DataStore dataStore;
	Calendar calendar;

	public DataFile(String filename, Calendar calendar) {
		this ( filename, calendar, false );
	}

	/**
	 * Create a DataFile object. If the specified filename exists, then it will be
	 * parsed and all entries loaded into the default DataStore. If the filename
	 * does not exists, then no parsing/loading will take place.
	 * 
	 * @param filename
	 *          The filename (YYYYMMDD.ics as in "19991231.ics")
	 * @param strictParsing
	 */
	public DataFile(String filename, Calendar calendar, boolean strictParsing) {
		super ( filename );
		this.calendar = calendar;
		parser = new ICalendarParser ( strictParsing ? PARSE_STRICT : PARSE_LOOSE );
		if ( this.exists () ) {
			BufferedReader reader = null;
			try {
				reader = new BufferedReader ( new FileReader ( this ) );
				parser.parse ( reader );
				reader.close ();
			} catch ( IOException e ) {
				System.err.println ( "Error opening " + toString () + ": " + e );
			}
		}
		dataStore = parser.getDataStoreAt ( 0 );
		// Store this DataFile object in the user data object of each
		// Event entry so we can get back to this object if the user
		// edits and saves a Event entry.
		for ( int i = 0; i < getEventCount (); i++ ) {
			Event e = eventEntryAt ( i );
			e.setUserData ( this );
		}
	}

	public void addEvent ( Event event ) {
		event.setUserData ( this );
		dataStore.storeEvent ( event );
	}

	private DataFile(ICalendarParser parser, String filename) {
		super ( filename );
		dataStore = parser.getDataStoreAt ( 0 );
		// Store this DataFile object in the user data object of each
		// Event entry so we can get back to this object if the user
		// edits and saves a Event entry.
		for ( int i = 0; i < getEventCount (); i++ ) {
			Event e = eventEntryAt ( i );
			e.setUserData ( this );
		}
	}

	/**
	 * Return the number of event entries in this file.
	 * 
	 * @return
	 */
	public int getEventCount () {
		return dataStore.getAllEvents ().size ();
	}

	/**
	 * Get the Event entry at the specified location.
	 * 
	 * @param ind
	 *          The index number (0 is first)
	 * @return
	 */
	public Event eventEntryAt ( int ind ) {
		return (Event) dataStore.getAllEvents ().elementAt ( ind );
	}

	/**
	 * Remove the Event object at the specified location in the Vector of entries.
	 * 
	 * @param ind
	 * @return true if found and deleted
	 */
	public boolean removeEvent ( Event event ) {
		return dataStore.getAllEvents ().remove ( event );
	}

	/**
	 * Get the number of parse errors found in the file.
	 * 
	 * @return
	 */
	public int getParseErrorCount () {
		return parser.getAllErrors ().size ();
	}

	/**
	 * Get the parse error at the specified location
	 * 
	 * @param ind
	 * @return
	 */
	public ParseError getParseErrorAt ( int ind ) {
		return (ParseError) parser.getAllErrors ().elementAt ( ind );
	}

	/**
	 * Write this DataFile object.
	 * 
	 * @throws IOException
	 */
	public void write () throws IOException {
		FileWriter writer = null;
		writer = new FileWriter ( this );
		writer.write ( parser.toICalendar () );
		writer.close ();
	}
}