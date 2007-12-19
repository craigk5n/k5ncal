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

import java.awt.Color;
import java.util.Vector;

import javax.swing.JList;

/**
 * A simple class that represents a dual-state list item. Because any item that
 * supports toString() can be a JList item, this is pretty simple class.
 * 
 * @author Craig Knudsen, craig@k5n.us
 * @version $Id$
 */
// TODO: add support for tooltips for each item
public class ListItem {
	/** nothing selected state */
	public static final int STATE_OFF = 1;
	/** positive match state */
	public static final int STATE_YES = 2;
	/** the current state of the item */
	public int state;
	Object o;
	/** Vector of JList objects that this ListItem is a member of */
	private Vector jlists = null;
	Color background = null, foreground = null;

	/**
	 * Construct a ListItem for the specified DataFieldDescription
	 * 
	 * @param dfd
	 *          The DataFieldDescription to represent as a ListItem
	 */
	public ListItem(Object o) {
		this.o = o;
		this.state = STATE_OFF;
		setState ( STATE_OFF );
		jlists = new Vector ();
	}

	/**
	 * Cycle the item to its next state. This should be called each time the user
	 * generates a mouse click on this item.
	 */
	public void nextState () {
		switch ( state ) {
			case STATE_OFF:
				state = STATE_YES;
				break;
			case STATE_YES:
				state = STATE_OFF;
				break;
		}
		setState ( state );
	}

	/**
	 * Set the current state of the ListItem
	 * 
	 * @param state
	 *          The new state (STATE_YES or STATE_OFF)
	 */
	public void setState ( final int state ) {
		this.state = state;
	}

	/**
	 * Get the current state (STATE_YES or STATE_OFF)
	 * 
	 * @return the current state (STATE_YES or STATE_OFF)
	 */
	public int getState () {
		return state;
	}

	/**
	 * Get the String representation of this ListItem. This will be the name of
	 * DataFieldDescription since JList uses the toString method to generate
	 * labels for items in a JList.
	 */
	public String toString () {
		return o.toString ();
	}

	public Object getObject () {
		return o;
	}

	public Color getBackground () {
		return background;
	}

	public void setBackground ( Color background ) {
		this.background = background;
	}

	public Color getForeground () {
		return foreground;
	}

	public void setForeground ( Color foreground ) {
		this.foreground = foreground;
	}

	/**
	 * Add a JList that this ListItem is a member of.
	 * 
	 * @param list
	 *          The JList
	 */
	public void addJList ( JList list ) {
		jlists.addElement ( list );
	}

	/**
	 * Send a repaint request to all JList components that this ListItem is a
	 * member of.
	 */
	public void repaintLists () {
		for ( int i = 0; i < jlists.size (); i++ ) {
			JList list = (JList) jlists.elementAt ( i );
			list.repaint ();
		}
	}
}
