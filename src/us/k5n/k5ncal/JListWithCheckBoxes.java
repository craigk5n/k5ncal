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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.ListSelectionModel;

/**
 * Extend the JList Swing class by providing a checkbox at the left side of each
 * item in the list that can be toggled between on and off independent of the
 * selection taking place in the JList class.
 * 
 * @author Craig Knudsen, craig@k5n.us
 * @version $Id$
 */
public class JListWithCheckBoxes extends JList implements MouseListener {
	private Vector objects;
	private Vector<ListItem> listItems;
	Vector<ListItemChangeListener> listeners;
	JPopupMenu menu;
	int menuItemIndex = -1;
	boolean isPopupTrigger = false;
	private Vector<ListItemMenuItem> menuItems = null;

	/**
	 * Create a JList where each item in this list also has a checkbox on the left
	 * that can be toggled on and off.
	 * 
	 * @param objects
	 */
	public JListWithCheckBoxes(Vector<Object> objects) {
		super ();
		this.setChoices ( objects );
		this.listeners = new Vector<ListItemChangeListener> ();
		this.addMouseListener ( this );
		this.setCellRenderer ( new ListItemRenderer () );
		this.setSelectionMode ( ListSelectionModel.SINGLE_SELECTION );
	}

	/**
	 * Get the object from the list at the specified location.
	 * 
	 * @param i
	 *          The location of the objecet
	 * @return
	 */
	public Object getObjectAt ( int i ) {
		return objects.elementAt ( i );
	}

	/**
	 * Get the ListItem at the specified location.
	 * 
	 * @param i
	 *          The location (0 = first)
	 * @return
	 */
	public ListItem getListItemAt ( int i ) {
		return (ListItem) super.getModel ().getElementAt ( i );
	}

	private void setRightClickMenu ( Vector<ListItemMenuItem> menuItems ) {
		// create right-click menu
		this.menuItems = menuItems;
		menu = new JPopupMenu ();
		for ( int i = 0; i < menuItems.size (); i++ ) {
			ListItemMenuItem menuItem = menuItems.elementAt ( i );
			JMenuItem item = new JMenuItem ( menuItem.toString () );
			item.setEnabled ( menuItem.isEnabled () );
			final int ind = i;
			item.addActionListener ( new ActionListener () {
				public void actionPerformed ( ActionEvent event ) {
					menuSelected ( ind );
				}
			} );
			menu.add ( item );
		}
	}

	public void menuSelected ( int menuOptionNumber ) {
		if ( menuItemIndex >= 0 ) {
			for ( int i = 0; i < this.listeners.size (); i++ ) {
				ListItemChangeListener l = this.listeners.elementAt ( i );
				l.menuChoice ( this.menuItemIndex, menuItems.elementAt (
				    menuOptionNumber ).getAction () );
			}
		}
	}

	/**
	 * Set the objects in the JList.
	 * 
	 * @param choices
	 *          The new Vector
	 */
	public void setChoices ( Vector choices ) {
		this.objects = choices;
		this.listItems = new Vector<ListItem> ();
		for ( int i = 0; i < choices.size (); i++ ) {
			Object o = this.objects.elementAt ( i );
			ListItem item = new ListItem ( o );
			this.listItems.addElement ( item );
		}
		super.setListData ( this.listItems );
		super.repaint ();
	}

	/**
	 * Set the state of the checkbox at the specified location.
	 * 
	 * @param ind
	 * @param state
	 *          the new state (ListItem.STATE_OFF, ListItem.STATE_YES)
	 */
	public void setStateAt ( int ind, int state ) {
		ListItem item = this.listItems.elementAt ( ind );
		item.setState ( state );
	}

	/**
	 * Handle the mousePressed event so that we can possibly show the popup menu.
	 */
	public void mousePressed ( MouseEvent e ) {
		// We need to check isPopupTrigger here and in mouseReleased. It wil be
		// true here for Mac (false for Windows). In mouseReleased, it will be
		// true for Windows and false for Mac.
		this.isPopupTrigger = e.isPopupTrigger ();

		// Determine which ListItem was selected.
		int selectedInd = this.locationToIndex ( e.getPoint () );
		if ( selectedInd < 0 || selectedInd >= this.objects.size () ) {
			// they may have selected below last item in list
			System.err.println ( "Could not find ListItem for " + e.getSource () );
			return;
		}
		this.menuItemIndex = selectedInd;
		Vector<ListItemMenuItem> menuItems = new Vector<ListItemMenuItem> ();
		for ( int i = 0; i < this.listeners.size (); i++ ) {
			ListItemChangeListener l = this.listeners.elementAt ( i );
			menuItems.addAll ( l.getMenuChoicesForIndex ( this.menuItemIndex ) );
		}
		setRightClickMenu ( menuItems );
	}

	/**
	 * Check to see if this event should create a popup menu. In Windows,
	 * isPopupTrigger gets set to true here, while it gets set to true in
	 * mousePressed on Mac OS X (just to keep us programmers on our toes).
	 */
	public void mouseReleased ( MouseEvent e ) {
		if ( e.isPopupTrigger () )
			this.isPopupTrigger = true;
		if ( menu != null && this.isPopupTrigger ) {
			int ind = this.locationToIndex ( e.getPoint () );
			this.menuItemIndex = ind;
			if ( ind >= 0 ) {
				menu.show ( (Component) e.getSource (), e.getX (), e.getY () );
			}
		} else if ( !this.isPopupTrigger ) {
			// check to see if user click on the icon
			if ( e.getX () <= ListItemIconFactory.getIconWidth () ) {
				int ind = this.locationToIndex ( e.getPoint () );
				if ( ind >= 0 ) {
					ListItem item = this.getListItemAt ( ind );
					item.nextState ();
					// Call listeners
					for ( int i = 0; i < this.listeners.size (); i++ ) {
						ListItemChangeListener l = this.listeners.elementAt ( i );
						if ( item.state == ListItem.STATE_YES )
							l.itemSelected ( ind );
						else
							l.itemUnselected ( ind );
					}
					this.repaint ();
				}
			}
		}
		this.isPopupTrigger = false;
	}

	public void mouseEntered ( MouseEvent e ) {
	}

	public void mouseClicked ( MouseEvent e ) {
	}

	public void mouseExited ( MouseEvent e ) {
	}

	public void addListItemChangeListener ( ListItemChangeListener l ) {
		this.listeners.addElement ( l );
	}

}
