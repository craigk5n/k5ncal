package us.k5n.k5ncal;

import java.util.Vector;

/**
 * Interface to listen to item selections in the CheckBoxList class.
 * 
 * @author Craig Knudsen, craig@k5n.us
 * @version $Id$
 */
public interface CheckBoxListListener {

	public void itemSelected ( Object o );

	public void itemUnselected ( Object o );

	public void menuChoice ( Object o, String actionCommand );
	
	public Vector<String> getMenuChoicesForObject ( Object o );

}
