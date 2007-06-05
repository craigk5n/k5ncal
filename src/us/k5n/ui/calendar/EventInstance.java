package us.k5n.ui.calendar;

import java.awt.Color;

/**
 * The EventInstance interface defines how event information will be obtained by
 * the CalendarPanel class. An EventInstance object will contain information
 * about an event on a single occurrence. If an event repeats, there will be a
 * separate EventInstance object for each occurrence.
 * 
 * @see CalendarDataRepository
 * @see CalendarPanel
 * @author Craig Knudsen
 * @version $Id$
 * 
 */
public interface EventInstance extends Comparable {

	public abstract String getTitle ();

	public abstract String getDescription ();

	/** Is the event an all-day event? */
	public abstract boolean isAllDay ();

	/** Does the event have a time of day specified? */
	public abstract boolean hasTime ();

	/** Get the event year (YYYY format) */
	public abstract int getYear ();

	/** Get the event month (Jan=1) */
	public abstract int getMonth ();

	public abstract int getDayOfMonth ();

	public abstract int getHour ();

	public abstract int getMinute ();

	public abstract int getSecond ();

	/** Does the event have a duration? */
	public abstract boolean hasDuration ();
	
	public abstract String getLocation ();

	/** Get the event duration (in seconds) */
	public abstract int getDurationSeconds ();

	/** Get the text color */
	public Color getForegroundColor ();

	public Color getBackgroundColor ();

	public Color getBorderColor ();

}
