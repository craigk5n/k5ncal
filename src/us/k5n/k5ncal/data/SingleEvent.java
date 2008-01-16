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

import java.awt.Color;

import us.k5n.ical.Event;
import us.k5n.ui.calendar.EventInstance;

/**
 * k5nCal implementation of the EventInstance interface.
 * 
 * @author Craig Knudsen, craig@k5n.us
 * @version $Id$
 */
public class SingleEvent implements EventInstance, Comparable {
	String title, description, location;
	int Y, M, D, h, m, s;
	boolean hasTime, allDay;
	Color fg, bg, border;
	// The Event that this SingleEvent is derived from
	protected Event event;
	protected Calendar calendar;

	// TODO: duration

	public SingleEvent(String title, String description, int Y, int M, int D) {
		this ( title, description, Y, M, D, 0, 0, 0, false, false );
	}

	public SingleEvent(String title, String description, int Y, int M, int D,
	    int h, int m, int s) {
		this ( title, description, Y, M, D, h, m, s, true, false );
	}

	public SingleEvent(String title, String description, int Y, int M, int D,
	    int h, int m, int s, boolean hasTime, boolean allDay) {
		this.title = title;
		this.description = description;
		this.Y = Y;
		this.M = M;
		this.D = D;
		this.h = h;
		this.m = m;
		this.s = s;
		this.hasTime = hasTime;
		this.allDay = allDay;
		// Set default color to blue
		this.bg = Color.blue;
		this.fg = Color.white;
		this.border = Color.white;
	}

	public String getTitle () {
		return title;
	}

	public String getDescription () {
		return description;
	}

	public String getLocation () {
		if ( this.event != null && this.event.getLocation () != null )
			return this.event.getLocation ().getValue ();
		return null;
	}

	public boolean isAllDay () {
		return allDay;
	}

	public boolean hasTime () {
		return hasTime;
	}

	public int getYear () {
		return Y;
	}

	public int getMonth () {
		return M;
	}

	public int getDayOfMonth () {
		return D;
	}

	public int getHour () {
		return h;
	}

	public int getMinute () {
		return m;
	}

	public int getSecond () {
		return s;
	}

	public boolean hasDuration () {
		return false;
	}

	public int getDurationSeconds () {
		return 0;
	}

	public Color getForegroundColor () {
		return fg;
	}

	public Color getBackgroundColor () {
		return bg;
	}

	public Color getBorderColor () {
		return border;
	}

	public void setBackgrounColor ( Color bg ) {
		this.bg = bg;
	}

	public void setBorderColor ( Color border ) {
		this.border = border;
	}

	public void setDayOfMonth ( int d ) {
		D = d;
	}

	public void setForegroundColor ( Color fg ) {
		this.fg = fg;
	}

	public void setHour ( int h ) {
		this.h = h;
	}

	public void setHasTime ( boolean hasTime ) {
		this.hasTime = hasTime;
	}

	public void setMinute ( int m ) {
		this.m = m;
	}

	public void setSecond ( int s ) {
		this.s = s;
	}

	public void setYear ( int y ) {
		Y = y;
	}

	public void setAllDay ( boolean allDay ) {
		this.allDay = allDay;
	}

	public void setDescription ( String description ) {
		this.description = description;
	}

	public void setTitle ( String title ) {
		this.title = title;
	}

	/** Implement the Comparable interface so events can be sorted */
	public int compareTo ( Object o ) {
		EventInstance e2 = (EventInstance) o;
		if ( this.getYear () < e2.getYear () )
			return -1;
		else if ( this.getYear () > e2.getYear () )
			return 1;
		if ( this.getMonth () < e2.getMonth () )
			return -1;
		else if ( this.getMonth () > e2.getMonth () )
			return 1;
		if ( this.getDayOfMonth () < e2.getDayOfMonth () )
			return -1;
		else if ( this.getDayOfMonth () > e2.getDayOfMonth () )
			return 1;
		if ( !this.hasTime && e2.hasTime () )
			return -1;
		else if ( this.hasTime () && !e2.hasTime () )
			return 1;
		else if ( !this.hasTime && !e2.hasTime () )
			return 0;
		if ( this.isAllDay () && !e2.isAllDay () )
			return -1;
		if ( !this.isAllDay () && e2.isAllDay () )
			return 1;
		if ( this.isAllDay () && e2.isAllDay () )
			return 0;
		// both events have a time
		if ( this.getHour () < e2.getHour () )
			return -1;
		else if ( this.getHour () > e2.getHour () )
			return 1;
		if ( this.getMinute () < e2.getMinute () )
			return -1;
		else if ( this.getMinute () > e2.getMinute () )
			return 1;
		if ( this.getSecond () < e2.getSecond () )
			return -1;
		else if ( this.getSecond () > e2.getSecond () )
			return 1;

		return 0;
	}

	public Event getEvent () {
		return event;
	}

	public void setEvent ( Event event ) {
		this.event = event;
	}

	public Calendar getCalendar () {
		return calendar;
	}

	public void setCalendar ( Calendar calendar ) {
		this.calendar = calendar;
	}

}
