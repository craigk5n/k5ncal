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
import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * Define a class that will override the JLabel class to add the checkbox
 * functionality (yes/off) we need in a JList.
 * 
 * @version $Id$
 * @author Craig Knudsen, craig@k5n.us
 * 
 */
public class ListItemRenderer extends JLabel implements ListCellRenderer {
	/** Font to use when selected */
	public static Font selectedFont = null;
	/** Font to use when not selected */
	public static Font notSelectedFont = null;
	/** Background color when selected */
	public static Color selectedColor = null;
	/** Background color for not selected */
	public static Color unselectedColor = null;

	// This is the only method defined by ListCellRenderer.
	// We just reconfigure the JLabel each time we're called.
	/**
	 * @param list
	 *          The JList
	 * @param value
	 *          The selected object (ListItem)
	 * @param index
	 *          cell index
	 * @param isSelected
	 *          Is the list item selected (in Swing context, not in the context of
	 *          our app
	 * @param cellHasFocus
	 *          The list and the cell have focus
	 */
	public Component getListCellRendererComponent ( JList list, Object value,
	    int index, boolean isSelected, boolean cellHasFocus ) {
		String s = value.toString ();
		ListItem listItem = null;
		setText ( s );

		if ( value instanceof ListItem ) {
			listItem = (ListItem) value;
			// setText ( s + "(" + listItem.dfd.getMinVal () + ","
			// + listItem.dfd.getMaxVal () + ")" );

			switch ( listItem.getState () ) {
				case ListItem.STATE_YES:
					setIcon ( ListItemIconFactory.getSelectedIcon ( listItem
					    .getBackground (), listItem.getForeground () ) );
					break;
				case ListItem.STATE_OFF:
					setIcon ( ListItemIconFactory.getUnselectedIcon ( listItem
					    .getBackground () ) );
					break;
				default:
					setIcon ( null );
					break;
			}
		} else {
			setIcon ( null );
		}

		setBackground ( isSelected ? list.getSelectionBackground () : list
		    .getBackground () );
		setForeground ( isSelected ? list.getSelectionForeground () : list
		    .getForeground () );
		setFont ( list.getFont () );
		setEnabled ( list.isEnabled () );
		setFont ( list.getFont () );
		setOpaque ( true );
		// if (value != null)
		// System.out.println ( "Value=" + value );
		// System.out.println ( "List selected" );
		return this;
	}
}
