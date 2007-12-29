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
package us.k5n.k5ncal.data;

import us.k5n.ical.Event;

/**
 * Interface for receiving updates from Repository.
 * 
 * @author Craig Knudsen, craig@k5n.us
 * @version $Id: RepositoryChangeListener.java,v 1.1 2007/06/05 12:40:26
 *          cknudsen Exp $
 */
public interface RepositoryChangeListener {

	public abstract void eventAdded ( Event event );

	public abstract void eventUpdated ( Event event );

	public abstract void eventDeleted ( Event event );

	public abstract void calendarAdded ( Calendar c );

	public abstract void calendarUpdated ( Calendar c );

	public abstract void calendarDeleted ( Calendar c );

	public abstract void displaySettingsChanged ();

}
