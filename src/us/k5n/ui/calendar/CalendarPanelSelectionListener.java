package us.k5n.ui.calendar;


/**
 * This interface should be implements for any class that wants to receive
 * CalendarPanelSelection events from the CalendarPanel class.
 * 
 * @see CalendarPanel
 * @author Craig Knudsen, craig@k5n.us
 * @version $Id: CalendarPanelSelectionListener.java,v 1.1 2007/06/05 12:39:27
 *          cknudsen Exp $
 */
public interface CalendarPanelSelectionListener {

	public abstract void eventSelected ( EventInstance eventInstance );

	public abstract void eventUnselected ();

	public abstract void eventDoubleClicked ( EventInstance eventInstance );

	public abstract void dateDoubleClicked ( int year, int month, int dayOfMonth );

}
