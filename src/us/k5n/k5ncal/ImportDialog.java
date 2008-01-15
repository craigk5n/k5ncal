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
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import us.k5n.ical.CSVParser;
import us.k5n.ical.CalendarParser;
import us.k5n.ical.ICalendarParser;
import us.k5n.k5ncal.data.Calendar;
import us.k5n.k5ncal.data.Repository;

/**
 * The import dialog window: Creates a JDialog window that will import the
 * specified format (CSV, ICS).
 * 
 * @author Craig Knudsen
 * @version $Id$
 */
public class ImportDialog extends JDialog {
	public final static int IMPORT_ICS = 1;
	public final static int IMPORT_CSV = 2;
	static File lastImportDirectory = null;
	File dataDirectory;
	Repository dataRepository;
	int type;

	class CSVFileFilter extends FileFilter {
		public boolean accept ( File f ) {
			if ( f.isDirectory () )
				return true;
			String name = f.getName ();
			if ( name.toLowerCase ().endsWith ( ".csv" ) )
				return true;
			return false;
		}

		public String getDescription () {
			return "*.csv (Comma-Separated Values)";
		}
	}
	class ICSFileFilter extends FileFilter {
		public boolean accept ( File f ) {
			if ( f.isDirectory () )
				return true;
			String name = f.getName ();
			if ( name.toLowerCase ().endsWith ( ".ics" ) )
				return true;
			return false;
		}

		public String getDescription () {
			return "*.ics (iCalendar)";
		}
	}

	/**
	 * Create the ImportDialog window.
	 * 
	 * @param parent
	 *          The parent JFrame object
	 * @param type
	 *          The type of import (IMPORT_CSV, IMPORT_ICS)
	 * @param dataDirectory
	 *          The data directory where files are stored
	 * @param dataRepository
	 *          The Repository for calendar data.
	 */
	public ImportDialog(JFrame parent, int type, File dataDirectory,
	    Repository dataRepository) {
		super ( parent );
		this.type = type;
		this.dataDirectory = dataDirectory;
		this.dataRepository = dataRepository;

		createUI ( parent );
		this.setLocationRelativeTo ( null );
		this.setVisible ( true );
	}

	protected void createUI ( JFrame parent ) {
		final JTextField fileField = new JTextField ( 40 );
		final JTextField nameField = new JTextField ( 40 );
		final ColorButton colorField = new ColorButton ();
		int[] props = { 1, 3 };

		nameField.setText ( "iCalendar Import" );

		this.setTitle ( "Import iCalendar File" );
		this.setModal ( true );
		Container content = this.getContentPane ();
		content.setLayout ( new BorderLayout () );
		JPanel buttonPanel = new JPanel ();
		buttonPanel.setLayout ( new FlowLayout () );
		JButton cancel = new JButton ( "Cancel" );
		cancel.addActionListener ( new ActionListener () {
			public void actionPerformed ( ActionEvent event ) {
				dispose ();
			}
		} );
		buttonPanel.add ( cancel );
		JButton ok = new JButton ( "Import" );
		ok.addActionListener ( new ActionListener () {
			public void actionPerformed ( ActionEvent event ) {
				try {
					String fname = fileField.getText ();
					if ( fname == null || fname.trim ().length () == 0 ) {
						showError ( "You must provide a filename." );
						return;
					}
					File importFile = new File ( fname );
					if ( !importFile.exists () ) {
						showError ( "The specified file does not exist." );
						return;
					}
					String name = nameField.getText ();
					if ( name == null || name.trim ().length () == 0 ) {
						showError ( "You must provide a name." );
						return;
					}
					Color color = colorField.getSelectedColor ();
					CalendarParser parser = null;
					switch ( type ) {
						case IMPORT_ICS:
							parser = new ICalendarParser ( CalendarParser.PARSE_LOOSE );
							break;
						case IMPORT_CSV:
							parser = new CSVParser ( CalendarParser.PARSE_LOOSE );
							break;
					}
					FileReader reader = new FileReader ( importFile );
					parser.parse ( reader );
					// TODO: display/handle parse errors
					Calendar cal = new Calendar ( dataDirectory, name );
					cal.setBackgroundColor ( color );
					cal.setBorderColor ( getForegroundColorForBackground ( color ) );
					cal.setBackgroundColor ( cal.getBorderColor () );
					cal.setLastUpdated ( java.util.Calendar.getInstance ()
					    .getTimeInMillis () );
					File file = new File ( dataDirectory, cal.getFilename () );
					FileWriter writer = new FileWriter ( file );
					writer.write ( parser.toICalendar () );
					writer.close ();
					showMessage ( "New local calendar added for import: " + name
					    + "      " + "Events imported: "
					    + parser.getDataStoreAt ( 0 ).getAllEvents ().size () );
					dataRepository.addCalendar ( dataDirectory, cal, false );
				} catch ( Exception e1 ) {
					showError ( "Error writing calendar:" + "\n" + e1.getMessage () );
					return;
				}
				dispose ();
			}
		} );
		buttonPanel.add ( ok );
		content.add ( buttonPanel, BorderLayout.SOUTH );

		JPanel main = new JPanel ();
		switch ( type ) {
			case IMPORT_CSV:
				main
				    .setBorder ( BorderFactory.createTitledBorder ( "Import CSV File" ) );
				break;
			case IMPORT_ICS:
				main.setBorder ( BorderFactory
				    .createTitledBorder ( "Import iCalendar File" ) );
				break;
		}
		main.setLayout ( new GridLayout ( 3, 1 ) );

		JPanel filePanel = new JPanel ();
		filePanel.setLayout ( new ProportionalLayout ( props,
		    ProportionalLayout.HORIZONTAL_LAYOUT ) );
		switch ( type ) {
			case IMPORT_CSV:
				filePanel.add ( new JLabel ( "CSV File: " ) );
				break;
			case IMPORT_ICS:
				filePanel.add ( new JLabel ( "ICS File: " ) );
				break;
		}
		JPanel fileNamePanel = new JPanel ();
		fileNamePanel.setLayout ( new BorderLayout () );
		JButton browse = new JButton ( "..." );
		browse.addActionListener ( new ActionListener () {
			public void actionPerformed ( ActionEvent event ) {
				String selectedFile = browseForFile ();
				if ( selectedFile != null )
					fileField.setText ( selectedFile );
			}
		} );
		fileNamePanel.add ( browse, BorderLayout.EAST );
		fileNamePanel.add ( fileField, BorderLayout.CENTER );
		filePanel.add ( fileNamePanel );
		main.add ( filePanel );

		JPanel namePanel = new JPanel ();
		namePanel.setLayout ( new ProportionalLayout ( props,
		    ProportionalLayout.HORIZONTAL_LAYOUT ) );
		namePanel.add ( new JLabel ( "New Calendar Name: " ) );
		namePanel.add ( nameField );
		main.add ( namePanel );

		JPanel colorPanel = new JPanel ();
		colorPanel.setLayout ( new ProportionalLayout ( props,
		    ProportionalLayout.HORIZONTAL_LAYOUT ) );
		colorPanel.add ( new JLabel ( "Background Color: " ) );
		JPanel colorSub = new JPanel ();
		colorSub.setLayout ( new BorderLayout () );
		colorField.setBackground ( Color.blue );
		colorSub.add ( colorField, BorderLayout.WEST );
		colorPanel.add ( colorSub );
		main.add ( colorPanel );

		content.add ( main, BorderLayout.CENTER );

		this.pack ();
	}

	void showError ( String message ) {
		System.err.println ( "Error: " + message );
		JOptionPane.showMessageDialog ( this, message, "Import Error",
		    JOptionPane.ERROR_MESSAGE );
	}

	void showMessage ( String message ) {
		JOptionPane.showMessageDialog ( this, message, "Import Details",
		    JOptionPane.INFORMATION_MESSAGE );
	}

	// TODO: move this into a Utils.java or something since it is also
	// in Main.java.
	private Color getForegroundColorForBackground ( Color bg ) {
		Color ret = Color.white;
		if ( bg.getRed () > 128 && bg.getGreen () > 128 && bg.getRed () > 128 )
			ret = Color.black;
		return ret;
	}

	String browseForFile () {
		JFileChooser fileChooser;
		File outFile = null;

		if ( lastImportDirectory == null )
			fileChooser = new JFileChooser ();
		else
			fileChooser = new JFileChooser ( lastImportDirectory );
		fileChooser.setFileSelectionMode ( JFileChooser.FILES_ONLY );
		switch ( type ) {
			case IMPORT_CSV:
				fileChooser.setFileFilter ( new CSVFileFilter () );
				break;
			case IMPORT_ICS:
				fileChooser.setFileFilter ( new ICSFileFilter () );
				break;
		}
		fileChooser.setDialogTitle ( "Select Import File" );
		fileChooser.setApproveButtonText ( "Choose" );
		fileChooser.setApproveButtonToolTipText ( "Select file to import" );
		int ret = fileChooser.showOpenDialog ( this );
		if ( ret == JFileChooser.APPROVE_OPTION ) {
			outFile = fileChooser.getSelectedFile ();
		} else {
			// Cancel
			return null;
		}
		// If no file extension provided, use ".cvs"
		String basename = outFile.getName ();
		if ( basename.indexOf ( '.' ) < 0 ) {
			// No filename extension provided, so add ".csv" to it
			outFile = new File ( outFile.getParent (), basename + ".csv" );
		}
		System.out.println ( "Selected File: " + outFile.toString () );
		lastImportDirectory = outFile.getParentFile ();
		if ( outFile.exists () && !outFile.canWrite () ) {
			JOptionPane.showMessageDialog ( this,
			    "You do not have the proper\npermissions to write to:\n\n"
			        + outFile.toString () + "\n\n" + "Please select another file.",
			    "Permissions Error", JOptionPane.WARNING_MESSAGE );
			return null;
		}
		return outFile.getAbsolutePath ();
	}

}
