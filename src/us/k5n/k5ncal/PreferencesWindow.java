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
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import us.k5n.ical.Constants;
import us.k5n.k5ncal.data.Repository;

/**
 * Preferences window.
 * 
 * @author Craig Knudsen, craig@k5n.us
 * @version $Id$
 */
public class PreferencesWindow extends JDialog implements Constants,
    ComponentListener {
	JFrame parent;
	Repository repo;
	AppPreferences prefs;
	JComboBox displayCancelled;
	JComboBox displayTentative;

	class IntegerChoice {
		String label;
		int value;

		public IntegerChoice(String label, int value) {
			this.label = label;
			this.value = value;
		}

		public String toString () {
			return label;
		}
	}

	/**
	 * Create the Preferences window.
	 * 
	 * @param parent
	 */
	public PreferencesWindow(JFrame parent, Repository repo) {
		super ( parent );
		prefs = AppPreferences.getInstance ();
		super.setSize ( prefs.getPrefWindowWidth (), prefs.getPrefWindowHeight () );
		super.setLocation ( prefs.getPrefWindowX (), prefs.getPrefWindowY () );
		setModal ( true );
		setDefaultCloseOperation ( JDialog.DISPOSE_ON_CLOSE );

		this.parent = parent;
		this.repo = repo;

		createWindow ();
		updateUIFromPreferences ();
		setVisible ( true );
		this.addComponentListener ( this );
	}

	private void createWindow () {
		int[] props = { 50, 50 };
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
		JButton cancelButton = new JButton ( "Cancel" );
		cancelButton.addActionListener ( new ActionListener () {
			public void actionPerformed ( ActionEvent event ) {
				close ();
			}
		} );
		buttonPanel.add ( cancelButton );
		getContentPane ().add ( buttonPanel, BorderLayout.SOUTH );

		JTabbedPane tabbedPane = new JTabbedPane ();

		JPanel eventPanel = new JPanel ( new GridLayout ( 2, 1 ) );

		JPanel cancelledPanel = new JPanel ( new ProportionalLayout ( props,
		    ProportionalLayout.HORIZONTAL_LAYOUT ) );
		cancelledPanel.add ( new JLabel ( "Display cancelled: " ) );
		Vector choices = new Vector ();
		choices.addElement ( new IntegerChoice ( "No", 0 ) );
		choices.addElement ( new IntegerChoice ( "Yes", 1 ) );
		this.displayCancelled = new JComboBox ( choices );
		cancelledPanel.add ( this.displayCancelled );
		eventPanel.add ( cancelledPanel );

		JPanel tentativePanel = new JPanel ( new ProportionalLayout ( props,
		    ProportionalLayout.HORIZONTAL_LAYOUT ) );
		tentativePanel.add ( new JLabel ( "Display tentative: " ) );
		this.displayTentative = new JComboBox ( choices );
		tentativePanel.add ( this.displayTentative );
		eventPanel.add ( tentativePanel );

		JPanel eventParentPanel = new JPanel ( new BorderLayout () );
		eventParentPanel.add ( eventPanel, BorderLayout.NORTH );
		JScrollPane sp = new JScrollPane ( eventParentPanel );

		tabbedPane.addTab ( "Events", sp );

		getContentPane ().add ( tabbedPane, BorderLayout.CENTER );
	}

	void updateUIFromPreferences () {
		this.displayCancelled
		    .setSelectedIndex ( prefs.getEventDisplayCancelled () ? 1 : 0 );
		this.displayTentative
		    .setSelectedIndex ( prefs.getEventDisplayTentative () ? 1 : 0 );

	}

	void save () {
		prefs
		    .setEventDisplayCancelled ( this.displayCancelled.getSelectedIndex () > 0 );
		prefs
		    .setEventDisplayTentative ( this.displayTentative.getSelectedIndex () > 0 );
		repo.notifyDisplayPreferencesChange ();
		this.dispose ();
	}

	void close () {
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
		prefs.setPrefWindowWidth ( this.getWidth () );
		prefs.setPrefWindowHeight ( this.getHeight () );
		prefs.setPrefWindowX ( this.getX () );
		prefs.setPrefWindowY ( this.getY () );
	}

}
