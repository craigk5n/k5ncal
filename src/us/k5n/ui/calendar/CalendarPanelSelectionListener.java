package us.k5n.ui.calendar;

import us.k5n.ical.Date;

/**
 * 
 * @author Craig Knudsen, craig@k5n.us
 * @version $Id$
 */
public interface CalendarPanelSelectionListener {

	public abstract void eventSelected ( EventInstance eventInstance );

	public abstract void eventUnselected ();
	
	public abstract void eventDoubleClicked ( EventInstance eventInstance );
	
	public abstract void dateDoubleClicked ( Date date );

}
