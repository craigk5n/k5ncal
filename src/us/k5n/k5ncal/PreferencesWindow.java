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
import javax.swing.UIManager;

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
	JComboBox displayHourInMonthView;
	JComboBox displayFontSize;
	JComboBox appearanceLAF;
	JComboBox iconStyle;

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

	class LAFChoice {
		UIManager.LookAndFeelInfo laf;
		String label;

		public LAFChoice(UIManager.LookAndFeelInfo laf) {
			this.laf = laf;
			this.label = laf.getName ();
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

		JPanel displayPanel = new JPanel ( new GridLayout ( 5, 1 ) );

		UIManager.LookAndFeelInfo[] info = UIManager.getInstalledLookAndFeels ();
		JPanel lafPanel = new JPanel ( new ProportionalLayout ( props,
		    ProportionalLayout.HORIZONTAL_LAYOUT ) );
		lafPanel.add ( new JLabel ( "Look and Feel: " ) );
		Vector<LAFChoice> lafChoices = new Vector<LAFChoice> ();
		for ( int i = 0; i < info.length; i++ ) {
			lafChoices.addElement ( new LAFChoice ( info[i] ) );
		}
		this.appearanceLAF = new JComboBox ( lafChoices );
		JPanel lafSubPanel = new JPanel ( new BorderLayout () );
		lafSubPanel.add ( this.appearanceLAF, BorderLayout.WEST );
		lafPanel.add ( lafSubPanel );
		displayPanel.add ( lafPanel );

		JPanel cancelledPanel = new JPanel ( new ProportionalLayout ( props,
		    ProportionalLayout.HORIZONTAL_LAYOUT ) );
		cancelledPanel.add ( new JLabel ( "Display cancelled events" + ": " ) );
		Vector<IntegerChoice> choices = new Vector<IntegerChoice> ();
		choices.addElement ( new IntegerChoice ( "No", 0 ) );
		choices.addElement ( new IntegerChoice ( "Yes", 1 ) );
		this.displayCancelled = new JComboBox ( choices );
		JPanel aPanel = new JPanel ( new BorderLayout () );
		aPanel.add ( this.displayCancelled, BorderLayout.WEST );
		cancelledPanel.add ( aPanel );
		displayPanel.add ( cancelledPanel );

		JPanel tentativePanel = new JPanel ( new ProportionalLayout ( props,
		    ProportionalLayout.HORIZONTAL_LAYOUT ) );
		tentativePanel.add ( new JLabel ( "Display tentative events" + ": " ) );
		this.displayTentative = new JComboBox ( choices );
		aPanel = new JPanel ( new BorderLayout () );
		aPanel.add ( this.displayTentative, BorderLayout.WEST );
		tentativePanel.add ( aPanel );
		displayPanel.add ( tentativePanel );

		JPanel showHourInMonthPanel = new JPanel ( new ProportionalLayout ( props,
		    ProportionalLayout.HORIZONTAL_LAYOUT ) );
		showHourInMonthPanel.add ( new JLabel ( "Show hour in month view" + ": " ) );
		this.displayHourInMonthView = new JComboBox ( choices );
		aPanel = new JPanel ( new BorderLayout () );
		aPanel.add ( this.displayHourInMonthView, BorderLayout.WEST );
		showHourInMonthPanel.add ( aPanel );
		displayPanel.add ( showHourInMonthPanel );

		JPanel fontSizePanel = new JPanel ( new ProportionalLayout ( props,
		    ProportionalLayout.HORIZONTAL_LAYOUT ) );
		fontSizePanel.add ( new JLabel ( "Font size" + ": " ) );
		choices = new Vector<IntegerChoice> ();
		choices.addElement ( new IntegerChoice ( "Smallest", -4 ) );
		choices.addElement ( new IntegerChoice ( "Small", -2 ) );
		choices.addElement ( new IntegerChoice ( "Default", 0 ) );
		choices.addElement ( new IntegerChoice ( "Large", 2 ) );
		choices.addElement ( new IntegerChoice ( "Largest", 4 ) );
		this.displayFontSize = new JComboBox ( choices );
		aPanel = new JPanel ( new BorderLayout () );
		aPanel.add ( this.displayFontSize, BorderLayout.WEST );
		fontSizePanel.add ( aPanel );
		displayPanel.add ( fontSizePanel );

		JPanel eventParentPanel = new JPanel ( new BorderLayout () );
		eventParentPanel.add ( displayPanel, BorderLayout.NORTH );
		JScrollPane sp = new JScrollPane ( eventParentPanel );

		tabbedPane.addTab ( "Display", sp );

		JPanel toolbarPanel = new JPanel ( new BorderLayout () );
		JPanel toolbarSubPanel = new JPanel ( new ProportionalLayout ( props,
		    ProportionalLayout.HORIZONTAL_LAYOUT ) );
		toolbarPanel.add ( toolbarSubPanel, BorderLayout.NORTH );
		toolbarSubPanel.add ( new JLabel ( "Icon Style" + ": " ) );
		JPanel iconStylePanel = new JPanel ( new BorderLayout () );
		choices = new Vector<IntegerChoice> ();
		choices.addElement ( new IntegerChoice ( "Text & Icon", 0 ) );
		choices.addElement ( new IntegerChoice ( "Icon", 1 ) );
		this.iconStyle = new JComboBox ( choices );
		iconStylePanel.add ( this.iconStyle, BorderLayout.WEST );
		toolbarSubPanel.add ( iconStylePanel );

		tabbedPane.addTab ( "Toolbar", toolbarPanel );

		getContentPane ().add ( tabbedPane, BorderLayout.CENTER );
	}

	void updateUIFromPreferences () {
		this.displayCancelled
		    .setSelectedIndex ( prefs.getDisplayCancelledEvents () ? 1 : 0 );
		this.displayTentative
		    .setSelectedIndex ( prefs.getDisplayTentativeEvents () ? 1 : 0 );
		this.displayHourInMonthView.setSelectedIndex ( prefs
		    .getDisplayHourInMonthView () ? 1 : 0 );
		switch ( prefs.getDisplayFontSize () ) {
			case -4:
				this.displayFontSize.setSelectedIndex ( 0 );
				break;
			case -2:
				this.displayFontSize.setSelectedIndex ( 1 );
				break;
			case 2:
				this.displayFontSize.setSelectedIndex ( 3 );
				break;
			case 4:
				this.displayFontSize.setSelectedIndex ( 4 );
				break;
			case 0:
			default:
				this.displayFontSize.setSelectedIndex ( 2 );
				break;
		}
		String laf = prefs.getAppearanceLookAndFeel ();
		for ( int i = 0; i < this.appearanceLAF.getItemCount (); i++ ) {
			LAFChoice lafChoice = (LAFChoice) this.appearanceLAF.getItemAt ( i );
			if ( lafChoice.laf.getClassName ().equals ( laf ) )
				this.appearanceLAF.setSelectedIndex ( i );
		}
		this.iconStyle.setSelectedIndex ( prefs.getToolbarIconText () ? 0 : 1 );
	}

	void save () {
		prefs
		    .setDisplayCancelledEvents ( this.displayCancelled.getSelectedIndex () > 0 );
		prefs
		    .setDisplayTentativeEvents ( this.displayTentative.getSelectedIndex () > 0 );
		prefs.setDisplayHourInMonthView ( this.displayHourInMonthView
		    .getSelectedIndex () > 0 );
		IntegerChoice ic = (IntegerChoice) this.displayFontSize.getSelectedItem ();
		if ( ic != null )
			prefs.setDisplayFontSize ( ic.value );
		ic = (IntegerChoice) this.iconStyle.getSelectedItem ();
		if ( ic != null )
			prefs.setToolbarIconText ( ic.value == 0 );
		LAFChoice laf = (LAFChoice) this.appearanceLAF.getSelectedItem ();
		prefs.setAppearanceLookAndFeel ( laf.laf.getClassName () );
		
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
