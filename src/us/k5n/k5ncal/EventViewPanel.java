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

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import us.k5n.ical.Date;
import us.k5n.ical.Event;

public class EventViewPanel extends JPanel {
	private JLabel date;
	private JLabel subject;
	private JLabel location;
	private JLabel calendar;
	private JLabel categories;
	private JTextArea text;

	public EventViewPanel() {
		super ();

		setLayout ( new BorderLayout () );

		JPanel topPanel = new JPanel ();
		topPanel.setLayout ( new GridLayout ( 5, 1 ) );
		topPanel.setBorder ( BorderFactory.createEmptyBorder ( 2, 4, 2, 4 ) );

		JPanel subpanel = new JPanel ();
		subpanel.setLayout ( new BorderLayout () );
		subpanel.add ( new JLabel ( "Date: " ), BorderLayout.WEST );
		date = new JLabel ();
		subpanel.add ( date, BorderLayout.CENTER );
		topPanel.add ( subpanel );

		subpanel = new JPanel ();
		subpanel.setLayout ( new BorderLayout () );
		subpanel.add ( new JLabel ( "Subject: " ), BorderLayout.WEST );
		subject = new JLabel ();
		subpanel.add ( subject, BorderLayout.CENTER );
		topPanel.add ( subpanel );

		subpanel = new JPanel ();
		subpanel.setLayout ( new BorderLayout () );
		subpanel.add ( new JLabel ( "Location: " ), BorderLayout.WEST );
		location = new JLabel ();
		subpanel.add ( location, BorderLayout.CENTER );
		topPanel.add ( subpanel );

		subpanel = new JPanel ();
		subpanel.setLayout ( new BorderLayout () );
		subpanel.add ( new JLabel ( "Calendar: " ), BorderLayout.WEST );
		calendar = new JLabel ();
		subpanel.add ( calendar, BorderLayout.CENTER );
		topPanel.add ( subpanel );

		subpanel = new JPanel ();
		subpanel.setLayout ( new BorderLayout () );
		subpanel.add ( new JLabel ( "Categories: " ), BorderLayout.WEST );
		categories = new JLabel ();
		subpanel.add ( categories, BorderLayout.CENTER );
		topPanel.add ( subpanel );

		add ( topPanel, BorderLayout.NORTH );

		text = new JTextArea ();
		text.setLineWrap ( true );
		text.setWrapStyleWord ( true );
		text.setEditable ( false );
		JScrollPane scrollPane = new JScrollPane ( text );
		scrollPane
		    .setVerticalScrollBarPolicy ( JScrollPane.VERTICAL_SCROLLBAR_ALWAYS );

		add ( scrollPane, BorderLayout.CENTER );
	}

	public void clear () {
		this.date.setText ( "" );
		this.subject.setText ( "" );
		this.location.setText ( "" );
		this.calendar.setText ( "" );
		this.categories.setText ( "" );
		this.text.setText ( "" );
	}

	public void update ( Date eventDate, Event event, Calendar calendar ) {
		if ( event == null ) {
			this.clear ();
			return;
		}
		DisplayDate dd = new DisplayDate ( eventDate );
		this.date.setText ( dd.toString () );

		if ( event.getSummary () != null )
			this.subject.setText ( event.getSummary ().getValue () );
		else
			this.subject.setText ( "" );

		if ( event.getLocation () != null )
			this.location.setText ( event.getLocation ().getValue () );
		else
			this.location.setText ( "" );

		if ( calendar != null ) {
			this.calendar.setText ( calendar.name );
		} else
			this.calendar.setText ( "" );

		if ( event.getCategories () != null )
			this.categories.setText ( event.getCategories ().getValue () );
		else
			this.categories.setText ( "" );

		if ( event.getDescription () != null )
			this.text.setText ( event.getDescription ().getValue () );
		else
			this.text.setText ( "" );
	}
}
