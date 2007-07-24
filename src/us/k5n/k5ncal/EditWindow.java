/*
 * Copyright (C) 2005-2007 Craig Knudsen
 *
 * k5nEvent is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 * 
 * A copy of the GNU Lesser General Public License can be found at www.gnu.org. 
 * To receive a hard copy, you can write to:
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307 USA.
 */

package us.k5n.k5ncal;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.IOException;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import us.k5n.ical.Categories;
import us.k5n.ical.Date;
import us.k5n.ical.Description;
import us.k5n.ical.Event;
import us.k5n.ical.Location;
import us.k5n.ical.Sequence;
import us.k5n.ical.Summary;

/**
 * Create a Event entry edit window.
 * 
 * @author Craig Knudsen, craig@k5n.us
 * @version $Id$
 */
public class EditWindow extends JDialog implements ComponentListener {
	Repository repo;
	Event event;
	Calendar selectedCalendar;
	Sequence seq = null;
	JFrame parent;
	JTextField subject;
	JTextField categories;
	JTextField location;
	JComboBox calendar;
	JLabel startDate;
	JTextArea description;
	AppPreferences prefs;
	private boolean newEvent = true;

	/**
	 * Create a new event window for the specified date.
	 * 
	 * @param parent
	 * @param repo
	 * @param date
	 * @param selectedCalendar
	 */
	public EditWindow(JFrame parent, Repository repo, Date date,
	    Calendar selectedCalendar) {
		this ( parent, repo, null, date, selectedCalendar );
	}

	/**
	 * Edit the specified event.
	 * 
	 * @param parent
	 * @param repo
	 * @param event
	 * @param selectedCalendar
	 */
	public EditWindow(JFrame parent, Repository repo, Event event,
	    Calendar selectedCalendar) {
		this ( parent, repo, event, null, selectedCalendar );
	}

	private EditWindow(JFrame parent, Repository repo, Event event, Date date,
	    Calendar selectedCalendar) {
		super ( parent );
		prefs = AppPreferences.getInstance ();
		super.setSize ( prefs.getEditWindowWidth (), prefs.getEditWindowHeight () );
		super.setLocation ( prefs.getEditWindowX (), prefs.getEditWindowY () );
		// TODO: don't make this modal once we add code to check
		// things like deleting this entry in the main window, etc.
		// super.setModal ( true );
		setDefaultCloseOperation ( JDialog.DISPOSE_ON_CLOSE );

		this.parent = parent;
		this.repo = repo;
		this.event = event;
		;
		this.selectedCalendar = selectedCalendar;

		if ( this.event == null ) {
			newEvent = true;
			if ( date == null )
				this.event = new Event ( "", "", Date.getCurrentDateTime ( "DTSTART" ) );
			else {
				date.setName ( "DTSTART" );
				this.event = new Event ( "", "", date );
			}
		} else {
			newEvent = false;
			// Create an updated sequence number for use only if we save
			// (So don't put it in the original Event object yet)
			if ( this.event.getSequence () == null )
				seq = new Sequence ( 1 );
			else
				seq = new Sequence ( this.event.getSequence ().getNum () + 1 );
		}
		// Make sure there is a Summary and Description
		if ( this.event.getSummary () == null )
			this.event.setSummary ( new Summary () );
		if ( this.event.getDescription () == null )
			this.event.setDescription ( new Description () );
		if ( this.event.getCategories () == null )
			this.event.setCategories ( new Categories () );
		if ( this.event.getLocation () == null )
			this.event.setLocation ( new Location () );

		createWindow ();
		setVisible ( true );
		this.addComponentListener ( this );
	}

	private void createWindow () {
		this.getContentPane ().setLayout ( new BorderLayout () );

		JPanel buttonPanel = new JPanel ();
		buttonPanel.setLayout ( new FlowLayout () );
		JButton saveButton = new JButton ( "Save" );
		saveButton.addActionListener ( new ActionListener () {
			public void actionPerformed ( ActionEvent event ) {
				// Save (write file)
				save ();
			}
		} );
		buttonPanel.add ( saveButton );
		JButton closeButton = new JButton ( "Close" );
		closeButton.addActionListener ( new ActionListener () {
			public void actionPerformed ( ActionEvent event ) {
				close ();
			}
		} );
		buttonPanel.add ( closeButton );
		getContentPane ().add ( buttonPanel, BorderLayout.SOUTH );

		JPanel allButButtons = new JPanel ();
		allButButtons.setLayout ( new BorderLayout () );
		allButButtons.setBorder ( BorderFactory.createEmptyBorder ( 5, 5, 5, 5 ) );

		JPanel upperPanel = new JPanel ();
		upperPanel.setBorder ( BorderFactory.createEtchedBorder () );
		GridLayout grid = new GridLayout ( 5, 1 );
		grid.setHgap ( 15 );
		grid.setVgap ( 5 );
		upperPanel.setLayout ( grid );
		int[] proportions = { 20, 80 };

		JPanel subjectPanel = new JPanel ();
		subjectPanel.setLayout ( new ProportionalLayout ( proportions,
		    ProportionalLayout.HORIZONTAL_LAYOUT ) );
		JLabel prompt = new JLabel ( "Subject: " );
		prompt.setHorizontalAlignment ( SwingConstants.RIGHT );
		subjectPanel.add ( prompt );
		subject = new JTextField ();
		if ( event != null && event.getSummary () != null )
			subject.setText ( event.getSummary ().getValue () );
		subjectPanel.add ( subject );
		upperPanel.add ( subjectPanel );

		JPanel datePanel = new JPanel ();
		datePanel.setLayout ( new ProportionalLayout ( proportions,
		    ProportionalLayout.HORIZONTAL_LAYOUT ) );
		prompt = new JLabel ( "Date: " );
		prompt.setHorizontalAlignment ( SwingConstants.RIGHT );
		datePanel.add ( prompt );
		JPanel subDatePanel = new JPanel ();
		FlowLayout flow = new FlowLayout ();
		flow.setAlignment ( FlowLayout.LEFT );
		subDatePanel.setLayout ( flow );
		startDate = new JLabel ();
		DisplayDate d = new DisplayDate ( event.getStartDate () );
		startDate.setText ( d.toString () );
		subDatePanel.add ( startDate );
		JButton dateSel = new JButton ( "..." );
		dateSel.addActionListener ( new ActionListener () {
			public void actionPerformed ( ActionEvent ae ) {
				Date newDate = DateTimeSelectionDialog.showDateTimeSelectionDialog (
				    parent, event.getStartDate () );
				if ( newDate != null ) {
					event.setStartDate ( newDate );
					DisplayDate d = new DisplayDate ( event.getStartDate () );
					startDate.setText ( d.toString () );
				}
			}
		} );
		dateSel.setMargin ( new Insets ( 0, 5, 0, 5 ) );
		subDatePanel.add ( dateSel );
		datePanel.add ( subDatePanel );
		upperPanel.add ( datePanel );

		JPanel locPanel = new JPanel ();
		locPanel.setLayout ( new ProportionalLayout ( proportions,
		    ProportionalLayout.HORIZONTAL_LAYOUT ) );
		prompt = new JLabel ( "Location: " );
		prompt.setHorizontalAlignment ( SwingConstants.RIGHT );
		locPanel.add ( prompt );
		location = new JTextField ();
		location.setText ( this.event.getLocation ().getValue () );
		locPanel.add ( location );
		upperPanel.add ( locPanel );

		JPanel calPanel = new JPanel ();
		calPanel.setLayout ( new ProportionalLayout ( proportions,
		    ProportionalLayout.HORIZONTAL_LAYOUT ) );
		prompt = new JLabel ( "Calendar: " );
		prompt.setHorizontalAlignment ( SwingConstants.RIGHT );
		calPanel.add ( prompt );
		Vector<Calendar> localCalendars = new Vector<Calendar> ();
		for ( int i = 0; i < this.repo.calendars.size (); i++ ) {
			Calendar c = this.repo.calendars.elementAt ( i );
			if ( c.url == null ) {
				localCalendars.addElement ( c );
			}
		}
		// TODO: show error if no local calendars found
		calendar = new JComboBox ( localCalendars );
		calendar.setSelectedItem ( selectedCalendar );
		calPanel.add ( calendar );
		upperPanel.add ( calPanel );

		JPanel catPanel = new JPanel ();
		catPanel.setLayout ( new ProportionalLayout ( proportions,
		    ProportionalLayout.HORIZONTAL_LAYOUT ) );
		prompt = new JLabel ( "Categories: " );
		prompt.setHorizontalAlignment ( SwingConstants.RIGHT );
		catPanel.add ( prompt );
		categories = new JTextField ();
		if ( event != null && event.getCategories () != null )
			categories.setText ( event.getCategories ().getValue () );
		catPanel.add ( categories );
		upperPanel.add ( catPanel );

		allButButtons.add ( upperPanel, BorderLayout.NORTH );

		// TODO: eventually add some edit buttons/icons here when
		// we support more than plain text.
		JPanel descrPanel = new JPanel ();
		descrPanel.setLayout ( new BorderLayout () );
		description = new JTextArea ();
		description.setLineWrap ( true );
		description.setWrapStyleWord ( true );
		if ( event != null && event.getDescription () != null )
			description.setText ( event.getDescription ().getValue () );
		description.setCaretPosition ( 0 );
		JScrollPane scrollPane = new JScrollPane ( description );
		descrPanel.add ( scrollPane, BorderLayout.CENTER );
		allButButtons.add ( descrPanel, BorderLayout.CENTER );

		getContentPane ().add ( allButButtons, BorderLayout.CENTER );
	}

	void save () {
		// Verify a calendar was selected
		Calendar c = (Calendar) this.calendar.getSelectedItem ();
		if ( c == null ) {
			JOptionPane.showMessageDialog ( parent, "You must select a calendar.",
			    "Error", JOptionPane.ERROR_MESSAGE );
			return;
		}

		// Note: LAST-MODIFIED gets updated by call to saveEvent
		if ( seq != null ) {
			// TODO: some have suggested that the sequence number should
			// only change if the date/time or location is modified.
			event.setSequence ( seq );
			seq = null;
		}
		try {
			this.event.getDescription ().setValue ( description.getText () );
			this.event.getSummary ().setValue ( subject.getText ().trim () );
			this.event.getCategories ().setValue ( categories.getText ().trim () );
			this.event.getLocation ().setValue ( location.getText ().trim () );
			// Did the event move from one calendar to another?
			if ( c.equals ( this.selectedCalendar ) ) {
				repo.saveEvent ( c, this.event );
			} else {
				// New event?
				if ( !this.newEvent ) {
					// Calendar moved from one calendar to another.
					// Delete from old calendar
					repo.deleteEvent ( this.selectedCalendar, this.event );
					// Clear out the user data for the event (where the calendar
					// info is stored.)
					this.event.setUserData ( null );
				}
				// Add to new calendar
				repo.saveEvent ( c, this.event );
			}
		} catch ( IOException e2 ) {
			// TODO: add error handler that pops up a window here
			e2.printStackTrace ();
		}
		this.dispose ();
	}

	void chooseDate () {
		DateTimeSelectionDialog dts = new DateTimeSelectionDialog ( parent, event
		    .getStartDate () );
	}

	void close () {
		// TODO: check for unsaved changes
		this.dispose ();
	}

	public void componentHidden ( ComponentEvent ce ) {
	}

	public void componentShown ( ComponentEvent ce ) {
	}

	// Handle moving of main window
	public void componentMoved ( ComponentEvent ce ) {
		saveWindowPreferences ();
	}

	public void componentResized ( ComponentEvent ce ) {
		saveWindowPreferences ();
	}

	/**
	 * Save current window width, height so we can restore on next run.
	 */
	public void saveWindowPreferences () {
		prefs.setEditWindowWidth ( this.getWidth () );
		prefs.setEditWindowHeight ( this.getHeight () );
		prefs.setEditWindowX ( this.getX () );
		prefs.setEditWindowY ( this.getY () );
	}

}
