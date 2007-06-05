package us.k5n.ui.calendar;

import java.util.Vector;

/**
 * The CalendarDataRepository defines the interface that the CalendarPanel will
 * use to get event data. It is up to the calling application to implement this
 * interface. Note that the CalendarPanel does not cache any event data, so the
 * implementation of this interface should keep performance in mind.
 * 
 * @author Craig Knudsen, craig@k5n.us
 * @version $Id$
 * 
 */
public interface CalendarDataRepository {

	/**
	 * Return a Vector of EventInstance objects for the specified date.
	 * 
	 * @param year
	 *          The year in YYYY format
	 * @param month
	 *          The month (Jan = 1)
	 * @param day
	 *          The day of the month
	 * @return Vector of EventInstance objects
	 */
	public abstract Vector getEventInstancesForDate ( int year, int month, int day );

}
