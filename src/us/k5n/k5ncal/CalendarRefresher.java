package us.k5n.k5ncal;

/**
 * Defines the interface of an object that will relaod a remote calendar (read
 * in from HTTP and save it locally)
 * 
 * @author Craig Knudsen, craig@k5n.us
 * @version $Id$
 */
public interface CalendarRefresher {

	/**
	 * Refresh the specified calendar by reloading it from its URL. Because this
	 * is likely to take a second or more in ideal circumstances (and much longer
	 * in many cases), we will use the SwingWorker class to execute this in a
	 * separate thread so we don't lock up the UI.
	 * 
	 * @param cal
	 *          The Calendar to refresh
	 */
	public abstract void refreshCalendar ( final Calendar cal );

}
