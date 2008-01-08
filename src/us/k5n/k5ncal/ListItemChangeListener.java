package us.k5n.k5ncal;

import java.util.Vector;

/**
 * The ListItemChangeListener interface defines a class that is interested in
 * being notified if a ListItem changes its status.
 * 
 * @see ListItem
 * @author Craig Knudsen, craig@k5n.us
 * @version $Id: ListItemChangeListener.java,v 1.1 2007/12/12 19:32:24 cknudsen
 *          Exp $
 */
public interface ListItemChangeListener {

	public void itemSelected ( int ind );

	public void itemUnselected ( int ind );

	public void menuChoice ( int ind, String actionCommand );

	public Vector<ListItemMenuItem> getMenuChoicesForIndex ( int ind );
}
