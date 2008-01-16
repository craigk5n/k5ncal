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
import java.awt.CardLayout;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import us.k5n.ical.Date;
import us.k5n.ical.Event;
import us.k5n.k5ncal.data.Calendar;

public class EventViewPanel extends JPanel {
	private JPanel eventDetailPanel, blankPanel;
	private CardLayout cardLayout;
	private JLabel date;
	private JLabel subject;
	private JLabel location;
	private JLabel url;
	private JLabel calendar;
	private JLabel categories;
	private JTextArea text;
	private static Cursor handCursor = null, defaultCursor = null;
	Vector<JComponent> textComponents;

	public EventViewPanel() {
		super ();

		this.textComponents = new Vector<JComponent> ();
		Font font = getFont ();
		font = new Font ( font.getFamily (), Font.PLAIN, font.getSize () - 2 );

		if ( EventViewPanel.defaultCursor == null ) {
			defaultCursor = this.getCursor ();
			EventViewPanel.handCursor = Cursor
			    .getPredefinedCursor ( Cursor.HAND_CURSOR );
		}
		setLayout ( this.cardLayout = new CardLayout () );
		this.eventDetailPanel = new JPanel ( new BorderLayout () );

		JPanel topPanel = new JPanel ();
		topPanel.setLayout ( new GridLayout ( 6, 1 ) );
		topPanel.setBorder ( BorderFactory.createEmptyBorder ( 2, 4, 2, 4 ) );

		JPanel subpanel = new JPanel ();
		subpanel.setLayout ( new BorderLayout () );
		subpanel.add ( makeLabel ( "Date" + ":" ), BorderLayout.WEST );
		date = makeLabel ( "" );
		this.textComponents.addElement ( date );
		subpanel.add ( date, BorderLayout.CENTER );
		topPanel.add ( subpanel );

		subpanel = new JPanel ();
		subpanel.setLayout ( new BorderLayout () );
		subpanel.add ( makeLabel ( "Subject" + ":" ), BorderLayout.WEST );
		subject = makeLabel ( "" );
		this.textComponents.addElement ( subject );
		subpanel.add ( subject, BorderLayout.CENTER );
		topPanel.add ( subpanel );

		subpanel = new JPanel ();
		subpanel.setLayout ( new BorderLayout () );
		subpanel.add ( makeLabel ( "Location" + ":" ), BorderLayout.WEST );
		location = makeLabel ( "" );
		this.textComponents.addElement ( location );
		subpanel.add ( location, BorderLayout.CENTER );
		topPanel.add ( subpanel );

		subpanel = new JPanel ();
		subpanel.setLayout ( new BorderLayout () );
		subpanel.add ( makeLabel ( "URL" + ":" ), BorderLayout.WEST );
		url = new JLabelWithHyperlink ( "" );
		this.textComponents.addElement ( url );
		JPanel subSubPanel = new JPanel ( new BorderLayout () );
		subSubPanel.add ( url, BorderLayout.WEST );
		subpanel.add ( subSubPanel, BorderLayout.CENTER );
		topPanel.add ( subpanel );

		subpanel = new JPanel ();
		subpanel.setLayout ( new BorderLayout () );
		subpanel.add ( makeLabel ( "Calendar" + ":" ), BorderLayout.WEST );
		calendar = makeLabel ( "" );
		subpanel.add ( calendar, BorderLayout.CENTER );
		topPanel.add ( subpanel );

		subpanel = new JPanel ();
		subpanel.setLayout ( new BorderLayout () );
		subpanel.add ( makeLabel ( "Categories" + ":" ), BorderLayout.WEST );
		categories = makeLabel ( "" );
		subpanel.add ( categories, BorderLayout.CENTER );
		topPanel.add ( subpanel );

		eventDetailPanel.add ( topPanel, BorderLayout.NORTH );

		text = new JTextArea ();
		text.setLineWrap ( true );
		text.setWrapStyleWord ( true );
		text.setEditable ( false );
		this.textComponents.addElement ( text );
		JScrollPane scrollPane = new JScrollPane ( text );

		eventDetailPanel.add ( scrollPane, BorderLayout.CENTER );

		this.blankPanel = new JPanel ( new BorderLayout () );
		this.blankPanel.add ( new JLabel (
		    "<html>Click on an event<br>to view details</html>", JLabel.CENTER ),
		    BorderLayout.CENTER );

		add ( this.eventDetailPanel, "details_panel" );
		add ( this.blankPanel, "blank_panel" );

		this.cardLayout.first ( this );

		if ( font != null )
			this.setAllFonts ( font );

		clear ();
	}

	JLabel makeLabel ( String s ) {
		JLabel l = new JLabel ( s );
		this.textComponents.addElement ( l );
		return l;
	}

	public void clear () {
		this.date.setText ( "" );
		this.subject.setText ( "" );
		this.location.setText ( "" );
		this.url.setText ( "" );
		this.calendar.setText ( "" );
		this.categories.setText ( "" );
		this.text.setText ( "" );
		this.cardLayout.last ( this );
	}

	public void setAllFonts ( Font font ) {
		super.setFont ( font );
		for ( int i = 0; i < this.textComponents.size (); i++ ) {
			JComponent o = this.textComponents.elementAt ( i );
			o.setFont ( font );
		}
	}

	public void update ( Date eventDate, Event event, Calendar calendar ) {
		if ( event == null ) {
			this.clear ();
			this.cardLayout.last ( this );
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

		if ( event.getUrl () != null )
			this.url.setText ( event.getUrl ().getValue () );
		else
			this.url.setText ( "" );

		if ( calendar != null ) {
			this.calendar.setText ( calendar.getName () );
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
		this.text.setCaretPosition ( 0 );
		this.cardLayout.first ( this );
	}
}
