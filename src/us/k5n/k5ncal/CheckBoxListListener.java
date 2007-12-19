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

import java.util.Vector;

/**
 * Interface to listen to item selections in the CheckBoxList class.
 * 
 * @author Craig Knudsen, craig@k5n.us
 * @version $Id: CheckBoxListListener.java,v 1.2 2007/06/30 01:36:32 cknudsen
 *          Exp $
 */
public interface CheckBoxListListener {

	public void itemSelected ( Object o );

	public void itemUnselected ( Object o );

	public void menuChoice ( Object o, String actionCommand );

	public Vector<String> getMenuChoicesForObject ( Object o );

}
