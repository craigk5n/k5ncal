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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import us.k5n.ical.BogusDataException;
import us.k5n.ical.Categories;
import us.k5n.ical.Date;
import us.k5n.ical.Event;
import us.k5n.ical.Utils;
import us.k5n.ui.calendar.CalendarDataRepository;

/**
 * The Repository class manages all loading and saving of data files. All
 * methods are intended to work with just Event objects. However, if an
 * iCalendar file is loaded with non-Event objects, they should be preserved in
 * the data if it is written back out.
 * 
 * @author Craig Knudsen, craig@k5n.us
 * @version $Id$
 */
public class Repository implements CalendarDataRepository {
	File directory;
	Vector<Calendar> calendars;
	Vector<DataFile> dataFiles;
	HashMap<String, DataFile> dataFileHash;
	HashMap<Calendar, DataFile> dataFileCalendarHash;
	int parseErrorCount = 0;
	int eventCount = 0;
	private HashMap<String, String> categoryFilter = null;
	private boolean categoryFilterIncludeUncat = false;
	private boolean categoryFilterEnabled = false;
	private HashMap<String, Vector> cachedEvents;
	private Vector<RepositoryChangeListener> changeListeners;
	private Vector<String> categories; // Vector of String categories
	private boolean needsRebuilding = true;
	private static boolean looseCategoryHandling = true;

	public Repository(File dir, Vector<Calendar> calendars, boolean strictParsing) {
		this.directory = dir;
		this.calendars = calendars;
		this.dataFiles = new Vector<DataFile> ();
		this.dataFileHash = new HashMap<String, DataFile> ();
		this.dataFileCalendarHash = new HashMap<Calendar, DataFile> ();
		this.cachedEvents = new HashMap<String, Vector> ();
		this.changeListeners = new Vector<RepositoryChangeListener> ();
		this.categories = new Vector<String> ();

		// Load all calendars
		for ( int i = 0; calendars != null && i < calendars.size (); i++ ) {
			Calendar c = calendars.elementAt ( i );
			File file = new File ( dir, c.getFilename () );
			DataFile f = new DataFile ( file.getAbsolutePath (), c, strictParsing );
			if ( f != null ) {
				this.dataFileCalendarHash.put ( c, f );
				this.addDataFile ( f, c );
			}
		}

		this.needsRebuilding = true;
		rebuildPrivateData ();
	}

	public void addCalendar ( File dir, Calendar c, boolean strictParsing ) {
		// make sure we don't already have it
		boolean found = false;
		for ( int i = 0; i < this.calendars.size (); i++ ) {
			Calendar c1 = this.calendars.elementAt ( i );
			if ( c1.equals ( c ) ) {
				// already have it. Probably added by the caller to the shared Vector
				found = true;
			}
		}
		File file = new File ( dir, c.getFilename () );
		DataFile f = new DataFile ( file.getAbsolutePath (), c, strictParsing );
		if ( f != null ) {
			this.addDataFile ( f, c );
		}
		if ( !found ) {
			this.calendars.addElement ( c );
			for ( int i = 0; i < this.changeListeners.size (); i++ ) {
				RepositoryChangeListener l = this.changeListeners.elementAt ( i );
				l.calendarAdded ( c );
			}
		}
		needsRebuilding = true;
	}

	// Call this when you have updated the calendar name or other
	// attributes.
	public void updateCalendar ( File dir, Calendar c ) {
		boolean found = false;
		for ( int i = 0; i < this.calendars.size (); i++ ) {
			Calendar c1 = this.calendars.elementAt ( i );
			if ( c1.equals ( c ) ) {
				// found it
				found = true;
				this.calendars.setElementAt ( c, i );
				DataFile df = this.dataFiles.elementAt ( i );
				df.refresh ();
			}
		}
		if ( found ) {
			for ( int i = 0; i < this.changeListeners.size (); i++ ) {
				RepositoryChangeListener l = this.changeListeners.elementAt ( i );
				l.calendarUpdated ( c );
			}
		}
		needsRebuilding = true;
	}

	public void removeCalendar ( File dir, Calendar c ) {
		boolean found = false;
		for ( int i = 0; i < this.calendars.size () && !found; i++ ) {
			Calendar c1 = this.calendars.elementAt ( i );
			if ( c1.equals ( c ) ) {
				// found
				this.calendars.remove ( i );
				found = true;
			}
		}

		// Now remove DataFile
		found = false;
		for ( int i = 0; i < this.dataFiles.size () && !found; i++ ) {
			DataFile df = this.dataFiles.elementAt ( i );
			if ( df.calendar.equals ( c ) ) {
				File f = new File ( dir, df.calendar.getFilename () );
				removeDataFile ( df );
				found = true;
				f.delete ();
			}
		}
		if ( !found ) {
			System.out.println ( "removeCalendar: not found " + c );
		} else {
			needsRebuilding = true;
			for ( int i = 0; i < this.changeListeners.size (); i++ ) {
				RepositoryChangeListener l = this.changeListeners.elementAt ( i );
				l.calendarDeleted ( c );
			}
		}
	}

	/**
	 * Get a Vector of Calendar objects for all calendars.
	 * 
	 * @return
	 */
	public Vector<Calendar> getCalendars () {
		return calendars;
	}

	public DataFile getDataFileForCalendar ( Calendar c ) {
		for ( int i = 0; i < this.calendars.size (); i++ ) {
			Calendar c1 = this.calendars.elementAt ( i );
			if ( c1.equals ( c ) ) {
				// found it
				return this.dataFiles.elementAt ( i );
			}
		}
		// not found
		return null;
	}

	private void removeDataFile ( DataFile f ) {
		for ( int i = 0; i < this.dataFiles.size (); i++ ) {
			DataFile df = this.dataFiles.elementAt ( i );
			if ( df.equals ( f ) ) {
				eventCount -= f.getEventCount ();
				parseErrorCount -= f.getParseErrorCount ();
				this.dataFileHash.remove ( f.getName ().toLowerCase () );
				this.dataFiles.remove ( i );
				this.needsRebuilding = true;
				return;
			}
		}
		// Not found
		System.err.println ( "removeDataFile: not found" );
	}

	private void addDataFile ( DataFile f, Calendar c ) {
		boolean found = false;
		for ( int i = 0; i < this.dataFiles.size (); i++ ) {
			DataFile df = this.dataFiles.elementAt ( i );
			if ( df.equals ( f ) )
				found = true;
		}
		if ( found ) {
			System.out.println ( "addDataFile: not adding duplicate" );
		} else {
			this.dataFiles.addElement ( f );
			eventCount += f.getEventCount ();
			parseErrorCount += f.getParseErrorCount ();
			// Store in HashMap using just the filename (19991231.ics)
			// as the key
			this.dataFileHash.put ( f.getName ().toLowerCase (), f );
			this.dataFileCalendarHash.put ( c, f );
			this.needsRebuilding = true;
		}
	}

	/**
	 * Get all Event objects.
	 * 
	 * @return
	 */
	public Vector<Event> getAllEntries () {
		Vector<Event> ret = new Vector<Event> ();
		for ( int i = 0; i < dataFiles.size (); i++ ) {
			DataFile df = dataFiles.elementAt ( i );
			for ( int j = 0; j < df.getEventCount (); j++ ) {
				Event event = df.eventEntryAt ( j );
				ret.addElement ( event );
			}
		}
		return ret;
	}

	/**
	 * Get all Event objects from calendars that are currently visible.
	 * 
	 * @return
	 */
	public Vector<Event> getVisibleEntries () {
		Vector<Event> ret = new Vector<Event> ();
		for ( int i = 0; i < dataFiles.size (); i++ ) {
			DataFile df = dataFiles.elementAt ( i );
			if ( df.calendar.isSelected () ) {
				for ( int j = 0; j < df.getEventCount (); j++ ) {
					Event event = df.eventEntryAt ( j );
					ret.addElement ( event );
				}
			}
		}
		return ret;
	}

	public void clearCategoryFilter () {
		this.categoryFilterEnabled = false;
		this.categoryFilter = null;
	}

	public void setCategoryFilter ( boolean includeUncategorized,
	    Vector<String> cats ) {
		this.categoryFilterEnabled = true;
		this.categoryFilterIncludeUncat = includeUncategorized;
		this.categoryFilter = new HashMap<String, String> ();
		for ( int i = 0; i < cats.size (); i++ ) {
			String cat = cats.elementAt ( i ).toUpperCase ().trim ();
			this.categoryFilter.put ( cat, cat );
		}
	}

	/**
	 * Rebuild internal cached data after one or more calendar
	 */
	public void rebuild () {
		this.needsRebuilding = true;
	}

	/**
	 * Rebuild internal cached data after one or more calendar. Update the
	 * EventInstance objects array. Update the Vector of existing categories. The
	 * following objects will be updated: categories, cachedEvents
	 */
	private void rebuildPrivateData () {
		if ( !needsRebuilding )
			return;
		this.categories = new Vector<String> ();
		this.cachedEvents = new HashMap<String, Vector> ();
		HashMap<String, String> catH = new HashMap<String, String> ();
		for ( int i = 0; i < dataFiles.size (); i++ ) {
			DataFile df = (DataFile) dataFiles.elementAt ( i );
			System.out.println ( "DataFile#"
			    + i
			    + ": "
			    + df.toString ()
			    + ( this.getCalendars ().elementAt ( i ).isSelected () ? "(selected)"
			        : "" ) );
			// System.out.println ( " df.getEventCount () =" + df.getEventCount ()
			// );
			for ( int j = 0; j < df.getEventCount (); j++ ) {
				Event event = df.eventEntryAt ( j );
				if ( event.getStartDate () != null ) {
					boolean matchesCategoryFilter = false;
					if ( this.categoryFilterEnabled ) {
						// Does category match filter?
						if ( event.getCategories () == null
						    && this.categoryFilterIncludeUncat ) {
							matchesCategoryFilter = true;
						} else if ( event.getCategories () == null ) {
							matchesCategoryFilter = false;
						} else {
							String[] cats = splitCategories ( event.getCategories ()
							    .getValue () );
							for ( int k = 0; k < cats.length && !matchesCategoryFilter; k++ ) {
								if ( this.categoryFilter.containsKey ( cats[k].toUpperCase ()
								    .trim () ) )
									matchesCategoryFilter = true;
							}
						}
					} else {
						matchesCategoryFilter = true;
					}
					if ( df.calendar.isSelected () && matchesCategoryFilter ) {
						SingleEvent se = null;
						if ( event.isValid () && event.getStartDate () != null ) {
							Date startDate = event.getStartDate ();
							String title = event.getSummary ().getValue ();
							String description = event.getDescription () != null ? event
							    .getDescription ().getValue () : title;
							if ( startDate.isDateOnly () ) {
								se = new SingleEvent ( title, description,
								    startDate.getYear (), startDate.getMonth (), startDate
								        .getDay () );
							} else {
								se = new SingleEvent ( title, description,
								    startDate.getYear (), startDate.getMonth (), startDate
								        .getDay (), startDate.getHour (), startDate
								        .getMinute (), startDate.getSecond () );
							}
							se.setEvent ( event );
							se.setCalendar ( df.calendar );
							se.bg = df.calendar.getBackgroundColor ();
							se.border = df.calendar.getBorderColor ();
							se.fg = df.calendar.getForegroundColor ();
							String YMD = Utils.DateToYYYYMMDD ( startDate );
							Vector dateVector = null;
							if ( cachedEvents.containsKey ( YMD ) ) {
								dateVector = (Vector) cachedEvents.get ( YMD );
							} else {
								dateVector = new Vector ();
								cachedEvents.put ( YMD, dateVector );
							}
							dateVector.addElement ( se );
							// Add recurrance events
							Vector more = event.getRecurranceDates ();
							for ( int k = 0; more != null && k < more.size (); k++ ) {
								Date d2 = (Date) more.elementAt ( k );
								if ( startDate.isDateOnly () ) {
									se = new SingleEvent ( title, description, d2.getYear (), d2
									    .getMonth (), d2.getDay () );
								} else {
									se = new SingleEvent ( title, description, d2.getYear (), d2
									    .getMonth (), d2.getDay (), d2.getHour (), d2
									    .getMinute (), d2.getSecond () );
								}
								se.setEvent ( event );
								se.setCalendar ( df.calendar );
								se.bg = df.calendar.getBackgroundColor ();
								se.border = df.calendar.getBorderColor ();
								se.fg = df.calendar.getForegroundColor ();
								YMD = Utils.DateToYYYYMMDD ( d2 );
								dateVector = null;
								if ( cachedEvents.containsKey ( YMD ) ) {
									dateVector = (Vector) cachedEvents.get ( YMD );
								} else {
									dateVector = new Vector ();
									cachedEvents.put ( YMD, dateVector );
								}
								dateVector.addElement ( se );
							}
						}
					}
				}
				Categories cats = event.getCategories ();
				if ( cats != null && cats.getValue () != null ) {
					String[] catArray = splitCategories ( cats.getValue () );
					for ( int k = 0; catArray != null && k < catArray.length; k++ ) {
						String c1 = catArray[k].trim ();
						if ( c1.length () > 0 ) {
							String c1up = c1.toUpperCase ();
							if ( !catH.containsKey ( c1up ) ) {
								this.categories.addElement ( c1 );
								catH.put ( c1up, c1up );
							}
						}
					}
				}
			}
		}
		this.needsRebuilding = false;
	}

	public Vector getEventInstancesForDate ( int year, int month, int day ) {
		if ( needsRebuilding )
			this.rebuildPrivateData ();
		try {
			Date date = new Date ( "DTSTART", year, month, day );
			String YMD = Utils.DateToYYYYMMDD ( date );
			return (Vector) cachedEvents.get ( YMD );
		} catch ( BogusDataException e1 ) {
			e1.printStackTrace ();
			return null;
		}
	}

	/**
	 * Save the specified Event object. If the Event is part of an existing
	 * iCalendar file, the entire file will be written out. If this Event object
	 * is new, then a new iCalendar file will be created. Note: It is up to the
	 * caller to update the Sequence object each time a Event entry is saved. The
	 * "LAST-MODIFIED" setting will be updated automatically.
	 * 
	 * @param j
	 * @throws IOException
	 */
	public void saveEvent ( Calendar calendar, Event event ) throws IOException {
		boolean added = false;

		DataFile dataFile = (DataFile) event.getUserData ();
		if ( dataFile == null ) {
			// New event.
			// Add this event entry to the calendar's file
			dataFile = dataFileCalendarHash.get ( calendar );
			if ( dataFile == null ) {
				System.err.println ( "Error: could not find file for calendar "
				    + calendar );
				return;
			}
			System.out.println ( "Added event '" + event.getSummary ().getValue ()
			    + "' to data file: " + dataFile );
			dataFile.addEvent ( event );
			added = true;
		}
		event.setLastModified ( Date.getCurrentDateTime ( "LAST-MODIFIED" ) );
		event.setUserData ( dataFile );
		dataFile.write ();

		this.needsRebuilding = true;

		if ( added ) {
			for ( int i = 0; this.changeListeners != null
			    && i < this.changeListeners.size (); i++ ) {
				RepositoryChangeListener l = this.changeListeners.elementAt ( i );
				l.eventAdded ( event );
			}
		} else {
			// If we are updating, then the Event to be updated should
			// already be updated in the DataStore.
			for ( int i = 0; this.changeListeners != null
			    && i < this.changeListeners.size (); i++ ) {
				RepositoryChangeListener l = this.changeListeners.elementAt ( i );
				l.eventUpdated ( event );
			}
		}
	}

	/**
	 * Delete the specified Event object.
	 * 
	 * @param j
	 * @throws IOException
	 */
	public boolean deleteEvent ( Calendar calendar, Event e ) throws IOException {
		boolean deleted = false;
		DataFile dataFile = (DataFile) e.getUserData ();
		if ( dataFile == null ) {
			// New event. Nothing to do...
			System.err.println ( "Not found..." );
		} else {
			// Event to be deleted should be in the DataStore.
			if ( dataFile.removeEvent ( e ) ) {
				deleted = true;
				dataFile.write ();
				this.needsRebuilding = true;
				for ( int i = 0; this.changeListeners != null
				    && i < this.changeListeners.size (); i++ ) {
					RepositoryChangeListener l = (RepositoryChangeListener) this.changeListeners
					    .elementAt ( i );
					l.eventDeleted ( e );
				}
			} else {
				// System.out.println ( "Not deleted" );
			}
		}
		return deleted;
	}

	/**
	 * Is there a calendar with the specified URL already in the repository?
	 * 
	 * @param url
	 *          The URL to check for
	 * @return
	 */
	public boolean hasCalendarWithURL ( String url ) {
		for ( int i = 0; i < this.calendars.size (); i++ ) {
			Calendar c = this.calendars.elementAt ( i );
			if ( c.getUrl () == null )
				continue;
			String urlS = c.getUrl ().toString ();
			if ( url.equals ( urlS ) ) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Ask to be notified when changes are made to the Repository.
	 * 
	 * @param l
	 */
	public void addChangeListener ( RepositoryChangeListener l ) {
		if ( this.changeListeners == null )
			this.changeListeners = new Vector<RepositoryChangeListener> ();
		this.changeListeners.addElement ( l );
	}

	public Vector<String> getCategories () {
		if ( needsRebuilding )
			this.rebuildPrivateData ();
		return this.categories;
	}

	private static String[] splitCategories ( String categories ) {
		if ( looseCategoryHandling && categories.indexOf ( ',' ) < 0
		    && categories.indexOf ( ' ' ) > 0 ) {
			return categories.trim ().split ( "[, ]" );
		} else {
			return categories.trim ().split ( "," );
		}
	}

}
