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

/**
 * Defines a menu option available as right mouse click (or CTRL-click on Mac)
 * from the JListWithCheckBoxes class.
 * 
 * @author Craig Knudsen, craig@k5n.us
 * @version $Id$
 */
public class ListItemMenuItem {
	private String label;
	private boolean enabled;
	private String action;

	/**
	 * Construct a new menu item for use in the JListWithCheckBoxes class.
	 * 
	 * @param label
	 * @param enabled
	 * @param action
	 */
	public ListItemMenuItem(String label, boolean enabled, String action) {
		this.label = label;
		this.enabled = enabled;
		this.action = action;
	}

	/**
	 * Construct a new menu item for use in the JListWithCheckBoxes class.
	 * 
	 * @param label
	 * @param enabled
	 * @param action
	 */
	public ListItemMenuItem(String label, boolean enabled) {
		this.label = label;
		this.enabled = enabled;
		this.action = label;
	}

	/**
	 * Construct a new menu item for use in the JListWithCheckBoxes class.
	 * 
	 * @param label
	 * @param action
	 */
	public ListItemMenuItem(String label, String action) {
		this.label = label;
		this.enabled = true;
		this.action = action;
	}

	/**
	 * Construct a new menu item for use in the JListWithCheckBoxes class.
	 * 
	 * @param label
	 * @param action
	 */
	public ListItemMenuItem(String label) {
		this.label = label;
		this.enabled = true;
		this.action = label;
	}

	/**
	 * Get the String representation of this menu item.
	 */
	public String toString () {
		return label;
	}

	/**
	 * Get the label for this menu item.
	 * 
	 * @return
	 */
	public String getLabel () {
		return label;
	}

	/**
	 * Set the label for this menu item.
	 * 
	 * @param label
	 *          The new label
	 */
	public void setLabel ( String label ) {
		this.label = label;
	}

	/**
	 * Is this menu item enabled?
	 * 
	 * @return
	 */
	public boolean isEnabled () {
		return enabled;
	}

	/**
	 * Set the enabled status of this menu item.
	 * 
	 * @param enabled
	 */
	public void setEnabled ( boolean enabled ) {
		this.enabled = enabled;
	}

	/**
	 * Get the action String for this menu item.
	 * 
	 * @return
	 */
	public String getAction () {
		return action;
	}

	/**
	 * Set the action String for this menu item.
	 * 
	 * @param action
	 *          The new action String.
	 */
	public void setAction ( String action ) {
		this.action = action;
	}

}
