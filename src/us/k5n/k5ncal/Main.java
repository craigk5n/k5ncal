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
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;

import us.k5n.ical.BogusDataException;
import us.k5n.ical.Constants;
import us.k5n.ical.DataStore;
import us.k5n.ical.Date;
import us.k5n.ical.Event;
import us.k5n.ical.ICalendarParser;
import us.k5n.ui.calendar.CalendarPanel;
import us.k5n.ui.calendar.CalendarPanelSelectionListener;
import us.k5n.ui.calendar.EventInstance;

/**
 * Main class for k5nCal application. This application makes use of the k5n
 * iCalendar library (part of Java Calendar Tools).
 * 
 * @author Craig Knudsen, craig@k5n.us
 * @version $Id$
 * 
 */
public class Main extends JFrame implements Constants, ComponentListener,
    PropertyChangeListener, RepositoryChangeListener,
    CalendarPanelSelectionListener {
	public static final String DEFAULT_DIR_NAME = "k5nCal";
	public static final String VERSION = "0.9.0 (01 Jun 2007)";
	public static final String CALENDARS_FILE = "calendars.dat";
	JFrame parent;
	EventViewPanel eventView;
	JButton newButton, editButton, deleteButton;
	JLabel messageArea;
	Vector<Calendar> calendars;
	Repository dataRepository;
	CalendarPanel eventViewPanel;
	JSplitPane horizontalSplit = null;
	CheckBoxList calendarCheckboxes;
	String searchText = null;
	private static File lastExportDirectory = null;
	AppPreferences prefs;
	File dataDir = null;
	static final String MAIN_WINDOW_HEIGHT = "MainWindow.height";
	static final String MAIN_WINDOW_WIDTH = "MainWindow.width";
	static final String MAIN_WINDOW_X = "MainWindow.x";
	static final String MAIN_WINDOW_Y = "MainWindow.y";
	static final String MAIN_WINDOW_VERTICAL_SPLIT_POSITION = "MainWindow.vSplitPanePosition";
	static final String MAIN_WINDOW_HORIZONTAL_SPLIT_POSITION = "MainWindow.hSplitPanePosition";

	public Main() {
		super ( "k5nCal" );
		setWindowsLAF ();
		this.parent = this;

		// TODO: save user's preferred size on exit and set here
		prefs = AppPreferences.getInstance ();

		setSize ( prefs.getMainWindowWidth (), prefs.getMainWindowHeight () );
		this.setLocation ( prefs.getMainWindowX (), prefs.getMainWindowY () );

		setDefaultCloseOperation ( JFrame.EXIT_ON_CLOSE );
		Container contentPane = getContentPane ();

		// Load data
		File dataDir = getDataDirectory ();
		loadCalendars ( dataDir );
		dataRepository = new Repository ( dataDir, calendars, false );
		// Ask to be notified when the repository changes (user adds/edits
		// an entry)
		dataRepository.addChangeListener ( this );

		// Create a menu bar
		setJMenuBar ( createMenu () );

		contentPane.setLayout ( new BorderLayout () );

		// Add message/status bar at bottom
		JPanel messagePanel = new JPanel ();
		messagePanel.setLayout ( new BorderLayout () );
		messagePanel.setBorder ( BorderFactory.createEmptyBorder ( 2, 4, 2, 4 ) );
		messageArea = new JLabel ( "Welcome to k5nCal..." );
		messagePanel.add ( messageArea, BorderLayout.CENTER );
		contentPane.add ( messagePanel, BorderLayout.SOUTH );

		JPanel navArea = createCalendarSelectionPanel ( calendars );

		JPanel rightPanel = new JPanel ();
		rightPanel.setLayout ( new BorderLayout () );
		rightPanel.add ( createToolBar (), BorderLayout.NORTH );

		eventViewPanel = new CalendarPanel ( dataRepository );
		eventViewPanel.addSelectionListener ( this );
		rightPanel.add ( eventViewPanel, BorderLayout.CENTER );

		horizontalSplit = new JSplitPane ( JSplitPane.HORIZONTAL_SPLIT, navArea,
		    rightPanel );
		horizontalSplit.setOneTouchExpandable ( true );
		horizontalSplit.setDividerLocation ( prefs
		    .getMainWindowHorizontalSplitPosition () );
		horizontalSplit.addPropertyChangeListener ( this );

		this.add ( horizontalSplit, BorderLayout.CENTER );

		this.addComponentListener ( this );
		updateToolbar ();
		this.setVisible ( true );
	}

	public void loadCalendars ( File dir ) {
		this.calendars = new Vector<Calendar> ();
		File f = new File ( dir, CALENDARS_FILE );
		if ( !f.exists () ) {
			String name = (String) System.getProperty ( "user.name" );
			if ( name == null )
				name = "Main";
			Calendar def = new Calendar ( dir, name );
			this.showMessage ( "A new calendar named \"" + name
			    + "\"\nwas created for you." );
			this.calendars.addElement ( def );
		} else {
			try {
				FileInputStream f_in = new FileInputStream ( f );
				ObjectInputStream obj_in = new ObjectInputStream ( f_in );
				Object obj = obj_in.readObject ();
				if ( obj instanceof Vector ) {
					this.calendars = (Vector) obj;
				}
			} catch ( IOException e1 ) {
				fatalError ( "Error reading calendar data:\n" + e1.getMessage () );
				e1.printStackTrace ();
			} catch ( Exception e1 ) {
				fatalError ( "Error reading calendar data:\n" + e1.getMessage () );
				e1.printStackTrace ();
			}
		}
	}

	public void saveCalendars ( File dir ) {
		File f = new File ( dir, CALENDARS_FILE );
		try {
			FileOutputStream f_out = new FileOutputStream ( f );
			ObjectOutputStream obj_out = new ObjectOutputStream ( f_out );
			obj_out.writeObject ( this.calendars );
		} catch ( IOException e1 ) {
			this.showError ( "Error writing calendars:\n" + e1.getMessage () );
			e1.printStackTrace ();
		}
	}

	public void setMessage ( String msg ) {
		this.messageArea.setText ( msg );
	}

	public JMenuBar createMenu () {
		JMenuItem item;

		JMenuBar bar = new JMenuBar ();

		JMenu fileMenu = new JMenu ( "File" );

		JMenu exportMenu = new JMenu ( "Export" );
		// exportMenu.setMnemonic ( 'X' );
		fileMenu.add ( exportMenu );

		item = new JMenuItem ( "All Calendars" );
		item.addActionListener ( new ActionListener () {
			public void actionPerformed ( ActionEvent event ) {
				exportAll ();
			}
		} );
		exportMenu.add ( item );
		item = new JMenuItem ( "Selected Calendars" );
		item.addActionListener ( new ActionListener () {
			public void actionPerformed ( ActionEvent event ) {
				exportVisible ();
			}
		} );
		exportMenu.add ( item );

		fileMenu.addSeparator ();

		item = new JMenuItem ( "Exit" );
		item.setAccelerator ( KeyStroke.getKeyStroke ( 'Q', Toolkit
		    .getDefaultToolkit ().getMenuShortcutKeyMask () ) );
		item.addActionListener ( new ActionListener () {
			public void actionPerformed ( ActionEvent event ) {
				saveCalendars ( getDataDirectory () );
				System.exit ( 0 );
			}
		} );
		fileMenu.add ( item );

		bar.add ( fileMenu );

		JMenu calMenu = new JMenu ( "Calendar" );

		item = new JMenuItem ( "New Local..." );
		item.setAccelerator ( KeyStroke.getKeyStroke ( 'L', Toolkit
		    .getDefaultToolkit ().getMenuShortcutKeyMask () ) );
		item.addActionListener ( new ActionListener () {
			public void actionPerformed ( ActionEvent event ) {
				editLocalCalendar ( null );
			}
		} );
		calMenu.add ( item );
		item = new JMenuItem ( "Subscribe..." );
		item.setAccelerator ( KeyStroke.getKeyStroke ( 'S', Toolkit
		    .getDefaultToolkit ().getMenuShortcutKeyMask () ) );
		item.addActionListener ( new ActionListener () {
			public void actionPerformed ( ActionEvent event ) {
				editRemoteCalendar ( null );
			}
		} );
		calMenu.add ( item );

		bar.add ( calMenu );

		// Add help bar to right end of menubar
		bar.add ( Box.createHorizontalGlue () );

		JMenu helpMenu = new JMenu ( "Help" );

		item = new JMenuItem ( "About..." );
		item.setAccelerator ( KeyStroke.getKeyStroke ( 'A', Toolkit
		    .getDefaultToolkit ().getMenuShortcutKeyMask () ) );
		item.addActionListener ( new ActionListener () {
			public void actionPerformed ( ActionEvent event ) {
				// TODO: add logo, etc...
				JOptionPane
				    .showMessageDialog ( parent, "k5nCal\nVersion " + VERSION
				        + "\n\nDeveloped by k5n.us\n\n"
				        + "Go to www.k5n.us for more info." );
			}
		} );
		helpMenu.add ( item );

		bar.add ( helpMenu );

		return bar;
	}

	JToolBar createToolBar () {
		JToolBar toolbar = new JToolBar ();
		newButton = makeNavigationButton ( "New24.gif", "new", "Add new entry",
		    "New..." );
		newButton.addActionListener ( new ActionListener () {
			public void actionPerformed ( ActionEvent event ) {
				// Make sure there is at least one local calendar.
				boolean foundLocal = false;
				for ( int i = 0; i < calendars.size (); i++ ) {
					Calendar c = calendars.elementAt ( i );
					if ( c.url == null )
						foundLocal = true;
				}
				if ( !foundLocal ) {
					showError ( "You must create a local\ncalendar to add a\nnew event." );
				} else {
					Date now = Date.getCurrentDateTime ( "DTSTART" );
					now.setMinute ( 0 );
					new EditWindow ( parent, dataRepository, now, null );
				}
			}
		} );
		toolbar.add ( newButton );

		editButton = makeNavigationButton ( "Edit24.gif", "edit", "Edit entry",
		    "Edit..." );
		toolbar.add ( editButton );
		editButton.addActionListener ( new ActionListener () {
			public void actionPerformed ( ActionEvent event ) {
				// Get selected item and open edit window
				EventInstance eventInstance = eventViewPanel.getSelectedEvent ();
				if ( eventInstance != null ) {
					// TODO: support editing events with recurrance.
					SingleEvent se = (SingleEvent) eventInstance;
					if ( se.event.getRrule () != null ) {
						showError ( "Editing events with recurrance\nnot yet supported." );
					} else {
						new EditWindow ( parent, dataRepository, se.event, se.calendar );
					}
				}
			}
		} );

		deleteButton = makeNavigationButton ( "Delete24.gif", "delete",
		    "Delete entry", "Delete" );
		toolbar.add ( deleteButton );
		deleteButton.addActionListener ( new ActionListener () {
			public void actionPerformed ( ActionEvent event ) {
				// Get selected item and open edit window
				EventInstance eventInstance = eventViewPanel.getSelectedEvent ();
				if ( eventInstance != null ) {
					// TODO: support deleting events with recurrance.
					SingleEvent se = (SingleEvent) eventInstance;
					if ( se.event.getRrule () != null ) {
						showError ( "Deleting events with recurrance\nnot yet supported." );
					} else {
						if ( JOptionPane.showConfirmDialog ( parent,
						    "Are you sure you want\nto delete the following entry?\n\n"
						        + se.getTitle (), "Confirm Delete",
						    JOptionPane.YES_NO_OPTION ) == 0 ) {
							try {
								dataRepository.deleteEvent ( se.calendar, se.event );
							} catch ( IOException e1 ) {
								showError ( "Error deleting." );
								e1.printStackTrace ();
							}
						}
					}
				}
			}
		} );

		return toolbar;
	}

	/**
	 * Update the toolbar. You can only modify local calendars (for now, at
	 * least), so unless an event from a local calendar was selected, the edit and
	 * delete buttons should be disabled.
	 */
	void updateToolbar () {
		boolean isLocal = false;
		boolean selected = false;
		EventInstance eventInstance = eventViewPanel.getSelectedEvent ();
		selected = ( eventInstance != null );
		if ( selected && eventInstance instanceof SingleEvent ) {
			SingleEvent se = (SingleEvent) eventInstance;
			isLocal = se.calendar.url == null;
		}
		editButton.setEnabled ( selected && isLocal );
		deleteButton.setEnabled ( selected && isLocal );
	}

	/**
	 * Create the file selection area on the top side of the window. This will
	 * include a split pane where the left will allow navigation and selection of
	 * dates and the right will allow the selection of a specific entry.
	 * 
	 * @return
	 */
	protected JPanel createCalendarSelectionPanel ( Vector calendars ) {
		String[] menuLabels = { "Edit...", "Delete" };
		String[] menuActions = { "EDIT", "DELETE" };
		JPanel topPanel = new JPanel ();
		topPanel.setLayout ( new BorderLayout () );
		topPanel.setBorder ( BorderFactory.createTitledBorder ( "Calendars" ) );

		this.calendarCheckboxes = new CheckBoxList ( new Vector () );
		updateCalendarCheckboxes ();
		this.calendarCheckboxes
		    .addCheckBoxListListener ( new CheckBoxListListener () {
			    public void itemSelected ( Object item ) {
				    handleCalendarFilterSelection ();
			    }

			    public void itemUnselected ( Object item ) {
				    handleCalendarFilterSelection ();
			    }

			    public void menuChoice ( Object item, String actionCommand ) {
				    Calendar c = (Calendar) item;
				    if ( "EDIT".equals ( actionCommand ) ) {
					    editCalendar ( c );
				    } else if ( "DELETE".equals ( actionCommand ) ) {
					    System.out.println ( "Delete calendar: " + c );
					    if ( JOptionPane.showConfirmDialog ( parent,
					        "Are you sure you want to\nDelete the following calendar?\n\n"
					            + c.toString (), "Confirm Delete",
					        JOptionPane.YES_NO_OPTION ) == 0 ) {
						    deleteCalendar ( c );
					    }
					    ;
				    } else {
					    System.err.println ( "Unknown menu command: " + actionCommand );
				    }
			    }
		    } );
		this.calendarCheckboxes.setRightClickMenu ( menuLabels, menuActions );

		for ( int i = 0; i < this.calendars.size (); i++ ) {
			Calendar c = this.calendars.elementAt ( i );
			JCheckBox cb = this.calendarCheckboxes.getCheckBoxAt ( i );
			cb.setSelected ( c.selected );
		}

		topPanel.add ( this.calendarCheckboxes, BorderLayout.CENTER );

		return topPanel;
	}

	// Handle user selecting a calendar checkbox to display/hide a calendar
	void handleCalendarFilterSelection () {
		// Repaint the calendar view, which will reload the data
		Vector sel = this.calendarCheckboxes.getSelectedItems ();
		for ( int i = 0; i < calendars.size (); i++ ) {
			Calendar c = (Calendar) calendars.elementAt ( i );
			boolean selected = false;
			for ( int j = 0; j < sel.size () && !selected; j++ ) {
				Calendar selCal = (Calendar) sel.elementAt ( j );
				if ( c.equals ( selCal ) ) {
					selected = true;
				}
			}
			c.selected = selected;
		}
		this.eventViewPanel.clearSelection ();
		this.dataRepository.rebuild ();
		this.eventViewPanel.repaint ();
	}

	public void editCalendar ( Calendar c ) {
		if ( c.url == null ) {
			editLocalCalendar ( c );
		} else
			editRemoteCalendar ( c );

	}

	public void deleteCalendar ( Calendar c ) {
		boolean found = false;
		for ( int i = 0; i < calendars.size () && !found; i++ ) {
			Calendar c1 = (Calendar) calendars.elementAt ( i );
			if ( c1.equals ( c ) ) {
				calendars.remove ( i );
				dataRepository.removeCalendar ( getDataDirectory (), c );
				found = true;
			}
		}
		if ( found ) {
			updateCalendarCheckboxes ();
			this.dataRepository.rebuild ();
			this.eventViewPanel.repaint ();
			saveCalendars ( getDataDirectory () );
		} else {
			System.err.println ( "deleteCalendar: could not find calendar!" );
		}
	}

	/**
	 * Update the list of Calendars shown to the user
	 */
	public void updateCalendarCheckboxes () {
		this.calendarCheckboxes.setChoices ( calendars );
		for ( int i = 0; i < calendars.size (); i++ ) {
			Calendar cal = (Calendar) calendars.elementAt ( i );
			JCheckBox cb = this.calendarCheckboxes.getCheckBoxAt ( i );
			cb.setBackground ( cal.bg );
			cb.setForeground ( cal.fg );
			if ( cal.url != null )
				cb.setToolTipText ( cal.url.toString () );
			else
				cb.setToolTipText ( "Local calendar" );
		}
		this.calendarCheckboxes.validate ();
	}

	/**
	 * Get the data directory that data files for this application will be stored
	 * in.
	 * 
	 * @return
	 */
	// TODO: allow user preferences to override this setting
	File getDataDirectory () {
		if ( dataDir != null )
			return dataDir;
		String s = (String) System.getProperty ( "user.home" );
		if ( s == null ) {
			System.err.println ( "Could not find user.home setting." );
			System.err.println ( "Using current directory instead." );
			s = ".";
		}
		File f = new File ( s );
		if ( f == null )
			fatalError ( "Invalid user.home value '" + s + "'" );
		if ( !f.exists () )
			fatalError ( "Home directory '" + f + "' does not exist." );
		if ( !f.isDirectory () )
			fatalError ( "Home directory '" + f + "'is not a directory" );
		// Use the home directory as the base. Data files will
		// be stored in a subdirectory.
		File dir = new File ( f, DEFAULT_DIR_NAME );
		if ( !dir.exists () ) {
			if ( !dir.mkdirs () )
				fatalError ( "Unable to create data directory: " + dir );
			showMessage ( "The following directory was created\n"
			    + "to store data files:\n\n" + dir );
		}
		if ( !dir.isDirectory () )
			fatalError ( "Not a directory: " + dir );
		dataDir = dir;
		return dir;
	}

	void showStatusMessage ( String string ) {
		this.messageArea.setText ( string );
	}

	void showMessage ( String message ) {
		JOptionPane.showMessageDialog ( parent, message, "Notice",
		    JOptionPane.INFORMATION_MESSAGE );
	}

	void showError ( String message ) {
		System.err.println ( "Error: " + message );
		JOptionPane.showMessageDialog ( parent, message, "Error",
		    JOptionPane.ERROR_MESSAGE );
	}

	void fatalError ( String message ) {
		System.err.println ( "Fatal error: " + message );
		JOptionPane.showMessageDialog ( parent, message, "Fatal Error",
		    JOptionPane.ERROR );
		System.exit ( 1 );
	}

	protected JButton makeNavigationButton ( String imageName,
	    String actionCommand, String toolTipText, String altText ) {
		JButton button;
		String imgLocation = null;
		URL imageURL = null;

		// Look for the image.
		imgLocation = "images/" + imageName;
		if ( imageName != null ) {
			imgLocation = "images/" + imageName;
			imageURL = this.getClass ().getClassLoader ().getResource ( imgLocation );
		}

		if ( imageURL != null ) { // image found
			button = new JButton ( altText );
			button.setIcon ( new ImageIcon ( imageURL, altText ) );
		} else {
			// no image found
			button = new JButton ( altText );
			if ( imageName != null )
				System.err.println ( "Resource not found: " + imgLocation );
		}

		button.setVerticalTextPosition ( JButton.BOTTOM );
		button.setHorizontalTextPosition ( JButton.CENTER );
		button.setActionCommand ( actionCommand );
		button.setToolTipText ( toolTipText );

		// Decrease font size by 2 if we have an icon
		if ( imageURL != null ) {
			Font f = button.getFont ();
			Font newFont = new Font ( f.getFamily (), Font.PLAIN, f.getSize () - 2 );
			button.setFont ( newFont );
		}

		return button;
	}

	/**
	 * Set the Look and Feel to be Windows.
	 */
	public static void setWindowsLAF () {
		try {
			UIManager
			    .setLookAndFeel ( "com.sun.java.swing.plaf.windows.WindowsLookAndFeel" );
		} catch ( Exception e ) {
			System.out.println ( "Unable to load Windows UI: " + e.toString () );
		}
	}

	public void selectLookAndFeel ( Component toplevel, Frame dialogParent ) {
		LookAndFeel lafCurrent = UIManager.getLookAndFeel ();
		System.out.println ( "Current L&F: " + lafCurrent );
		UIManager.LookAndFeelInfo[] info = UIManager.getInstalledLookAndFeels ();
		String[] choices = new String[info.length];
		int sel = 0;
		for ( int i = 0; i < info.length; i++ ) {
			System.out.println ( "  " + info[i].toString () );
			choices[i] = info[i].getClassName ();
			if ( info[i].getClassName ().equals ( lafCurrent.getClass ().getName () ) )
				sel = i;
		}
		Object uiSelection = JOptionPane.showInputDialog ( dialogParent,
		    "Select Look and Feel", "Look and Feel",
		    JOptionPane.INFORMATION_MESSAGE, null, choices, choices[sel] );
		UIManager.LookAndFeelInfo selectedLAFInfo = null;
		for ( int i = 0; i < info.length; i++ ) {
			if ( uiSelection.equals ( choices[i] ) )
				selectedLAFInfo = info[i];
		}
		if ( selectedLAFInfo != null ) {
			try {
				System.out.println ( "Changing L&F: " + selectedLAFInfo );
				UIManager.setLookAndFeel ( selectedLAFInfo.getClassName () );
				// SwingUtilities.updateComponentTreeUI ( parent );
				// parent.pack ();
			} catch ( Exception e ) {
				System.err.println ( "Unabled to load L&F: " + e.toString () );
			}
		} else {
			System.err.println ( "No L&F selected" );
		}
	}

	private Color getForegroundColorForBackground ( Color bg ) {
		Color ret = Color.white;
		if ( bg.getRed () > 128 && bg.getGreen () > 128 && bg.getRed () > 128 )
			ret = Color.black;
		return ret;
	}

	protected void editRemoteCalendar ( final Calendar c ) {
		final JDialog addRemote = new JDialog ( this );
		final JTextField nameField = new JTextField ( 50 );
		final JTextField urlField = new JTextField ( 50 );
		final String[] choices = { "12 Hours", "1 Day", "3 Days", "7 Days",
		    "14 Days", "30 Days", "90 Days", "1 Year" };
		final int[] choiceValues = { 12, 24, 24 * 3, 24 * 7, 24 * 14, 24 * 30,
		    24 * 90, 24 * 365 };
		int defChoice = 5;
		final JComboBox updateField = new JComboBox ( choices );
		final ColorButton colorField = new ColorButton ();
		int[] props = { 2, 3 };

		if ( c != null ) {
			for ( int i = 0; i < choiceValues.length; i++ ) {
				if ( c.updateIntervalMS == choiceValues[i] * 1000 * 3600 )
					defChoice = i;
			}
			nameField.setText ( c.name );
			// Don't allow changing of URL. Must delete and add new
			urlField.setText ( c.url.toString () );
			urlField.setEditable ( false );
		}

		addRemote.setTitle ( c != null ? "Edit Remote Calendar"
		    : "Add Remote Calendar" );
		addRemote.setModal ( true );
		Container content = addRemote.getContentPane ();
		content.setLayout ( new BorderLayout () );
		JPanel buttonPanel = new JPanel ();
		buttonPanel.setLayout ( new FlowLayout () );
		JButton cancel = new JButton ( "Cancel" );
		cancel.addActionListener ( new ActionListener () {
			public void actionPerformed ( ActionEvent event ) {
				addRemote.dispose ();
			}
		} );
		buttonPanel.add ( cancel );
		JButton ok = new JButton ( c == null ? "Add" : "Save" );
		ok.addActionListener ( new ActionListener () {
			public void actionPerformed ( ActionEvent event ) {
				try {
					String name = nameField.getText ();
					if ( name == null || name.trim ().length () == 0 ) {
						showError ( "You must provide a name" );
						return;
					}
					String urlStr = urlField.getText ();
					if ( urlStr == null || urlStr.trim ().length () == 0 ) {
						showError ( "You must provide a URL" );
						return;
					}
					// convert "webcal://" to "http://";
					if ( urlStr.startsWith ( "webcal:" ) )
						urlStr = urlStr.replaceFirst ( "webcal:", "http:" );
					URL url = null;
					try {
						url = new URL ( urlStr );
					} catch ( Exception e1 ) {
						showError ( "Invalid URL:\n" + e1.getMessage () );
						return;
					}
					int updSel = updateField.getSelectedIndex ();
					int updateInterval = choiceValues[updSel];
					Color color = colorField.getSelectedColor ();
					// download
					showStatusMessage ( "Downloading calendar..." );
					Calendar cal = new Calendar ( getDataDirectory (), name, url,
					    updateInterval );
					cal.bg = color;
					cal.border = cal.fg = getForegroundColorForBackground ( color );
					cal.lastUpdated = java.util.Calendar.getInstance ()
					    .getTimeInMillis ();
					InputStream is = url.openStream ();
					File file = new File ( getDataDirectory (), cal.filename );
					OutputStream os = new FileOutputStream ( file );
					DataInputStream dis = new DataInputStream ( new BufferedInputStream (
					    is ) );
					byte[] buf = new byte[4 * 1024]; // 4K buffer
					int bytesRead;
					int totalRead = 0;
					while ( ( bytesRead = dis.read ( buf ) ) != -1 ) {
						os.write ( buf, 0, bytesRead );
						totalRead += bytesRead;
					}
					os.close ();
					dis.close ();
					// Delete old calendar
					if ( c == null ) {
						showStatusMessage ( "New remote calendar \"" + name + "\" added ("
						    + totalRead + " bytes)" );
					} else {
						deleteCalendar ( c );
						showStatusMessage ( "Remote calendar \"" + name + "\" updated ("
						    + totalRead + " bytes)" );
					}
					calendars.addElement ( cal );
					dataRepository.addCalendar ( getDataDirectory (), cal, false );
					updateCalendarCheckboxes ();
					saveCalendars ( getDataDirectory () );
					dataRepository.rebuild ();
				} catch ( Exception e1 ) {
					showError ( "Error downloading calendar:\n" + e1.getMessage () );
					return;
				}
				addRemote.dispose ();
			}
		} );
		buttonPanel.add ( ok );
		content.add ( buttonPanel, BorderLayout.SOUTH );

		JPanel main = new JPanel ();
		main
		    .setBorder ( BorderFactory.createTitledBorder ( "New Remote Calendar" ) );
		main.setLayout ( new GridLayout ( 4, 1 ) );
		JPanel namePanel = new JPanel ();
		namePanel.setLayout ( new ProportionalLayout ( props,
		    ProportionalLayout.HORIZONTAL_LAYOUT ) );
		namePanel.add ( new JLabel ( "Name: " ) );
		namePanel.add ( nameField );
		main.add ( namePanel );
		JPanel urlPanel = new JPanel ();
		urlPanel.setLayout ( new ProportionalLayout ( props,
		    ProportionalLayout.HORIZONTAL_LAYOUT ) );
		urlPanel.add ( new JLabel ( "URL: " ) );
		urlPanel.add ( urlField );
		main.add ( urlPanel );
		JPanel updatePanel = new JPanel ();
		updatePanel.setLayout ( new ProportionalLayout ( props,
		    ProportionalLayout.HORIZONTAL_LAYOUT ) );
		updatePanel.add ( new JLabel ( "Update Interval: " ) );
		updateField.setSelectedIndex ( defChoice );
		updatePanel.add ( updateField );
		main.add ( updatePanel );

		JPanel colorPanel = new JPanel ();
		colorPanel.setLayout ( new ProportionalLayout ( props,
		    ProportionalLayout.HORIZONTAL_LAYOUT ) );
		colorPanel.add ( new JLabel ( "Background Color: " ) );
		JPanel colorSub = new JPanel ();
		colorSub.setLayout ( new BorderLayout () );
		colorField.setBackground ( c == null ? Color.blue : c.bg );
		colorSub.add ( colorField, BorderLayout.WEST );
		colorPanel.add ( colorSub );
		main.add ( colorPanel );

		content.add ( main, BorderLayout.CENTER );

		addRemote.pack ();
		addRemote.setVisible ( true );
	}

	protected void editLocalCalendar ( final Calendar c ) {
		final JDialog addLocal = new JDialog ( this );
		final JTextField nameField = new JTextField ( 50 );
		final ColorButton colorField = new ColorButton ();
		int[] props = { 2, 3 };

		if ( c != null ) {
			nameField.setText ( c.name );
		}

		addLocal.setTitle ( c != null ? "Edit Local Calendar"
		    : "Add Local Calendar" );
		addLocal.setModal ( true );
		Container content = addLocal.getContentPane ();
		content.setLayout ( new BorderLayout () );
		JPanel buttonPanel = new JPanel ();
		buttonPanel.setLayout ( new FlowLayout () );
		JButton cancel = new JButton ( "Cancel" );
		cancel.addActionListener ( new ActionListener () {
			public void actionPerformed ( ActionEvent event ) {
				addLocal.dispose ();
			}
		} );
		buttonPanel.add ( cancel );
		JButton ok = new JButton ( c == null ? "Add" : "Save" );
		ok.addActionListener ( new ActionListener () {
			public void actionPerformed ( ActionEvent event ) {
				try {
					String name = nameField.getText ();
					if ( name == null || name.trim ().length () == 0 ) {
						showError ( "You must provide a name" );
						return;
					}
					Color color = colorField.getSelectedColor ();
					// download
					showStatusMessage ( "Downloading calendar..." );
					Calendar cal = new Calendar ( getDataDirectory (), name );
					cal.bg = color;
					cal.border = cal.fg = getForegroundColorForBackground ( color );
					cal.lastUpdated = java.util.Calendar.getInstance ()
					    .getTimeInMillis ();
					File file = new File ( getDataDirectory (), cal.filename );
					if ( c == null ) {
						// Create empty iCalendar file
						FileWriter writer = new FileWriter ( file );
						ICalendarParser icalParser = new ICalendarParser (
						    ICalendarParser.PARSE_STRICT );
						icalParser.toICalendar ();
						writer.write ( icalParser.toICalendar () );
						writer.close ();
					}
					if ( c == null ) {
						showStatusMessage ( "New local calendar \"" + name + "\" added" );
					} else {
						deleteCalendar ( c );
						showStatusMessage ( "Updated local calendar \"" + name
						    + "\" updated" );
					}
					calendars.addElement ( cal );
					dataRepository.addCalendar ( getDataDirectory (), cal, false );
					updateCalendarCheckboxes ();
					saveCalendars ( getDataDirectory () );
					dataRepository.rebuild ();
				} catch ( Exception e1 ) {
					showError ( "Error writing calendar:\n" + e1.getMessage () );
					return;
				}
				addLocal.dispose ();
			}
		} );
		buttonPanel.add ( ok );
		content.add ( buttonPanel, BorderLayout.SOUTH );

		JPanel main = new JPanel ();
		main.setBorder ( BorderFactory
		    .createTitledBorder ( c == null ? "New Local Calendar"
		        : "Edit Local Calendar" ) );
		main.setLayout ( new GridLayout ( 2, 1 ) );
		JPanel namePanel = new JPanel ();
		namePanel.setLayout ( new ProportionalLayout ( props,
		    ProportionalLayout.HORIZONTAL_LAYOUT ) );
		namePanel.add ( new JLabel ( "Name: " ) );
		namePanel.add ( nameField );
		main.add ( namePanel );

		JPanel colorPanel = new JPanel ();
		colorPanel.setLayout ( new ProportionalLayout ( props,
		    ProportionalLayout.HORIZONTAL_LAYOUT ) );
		colorPanel.add ( new JLabel ( "Background Color: " ) );
		JPanel colorSub = new JPanel ();
		colorSub.setLayout ( new BorderLayout () );
		colorField.setBackground ( c == null ? Color.blue : c.bg );
		colorSub.add ( colorField, BorderLayout.WEST );
		colorPanel.add ( colorSub );
		main.add ( colorPanel );

		content.add ( main, BorderLayout.CENTER );

		addLocal.pack ();
		addLocal.setVisible ( true );
	}

	protected void exportAll () {
		export ( "Export All", dataRepository.getAllEntries () );
	}

	protected void exportVisible () {
		export ( "Export Visible", dataRepository.getVisibleEntries () );
	}

	private void export ( String title, Vector eventEntries ) {
		JFileChooser fileChooser;
		File outFile = null;

		if ( lastExportDirectory == null )
			fileChooser = new JFileChooser ();
		else
			fileChooser = new JFileChooser ( lastExportDirectory );
		fileChooser.setFileSelectionMode ( JFileChooser.FILES_ONLY );
		fileChooser.setFileFilter ( new ICSFileChooserFilter () );
		fileChooser.setDialogTitle ( "Select Output File for " + title );
		fileChooser.setApproveButtonText ( "Save as ICS File" );
		fileChooser
		    .setApproveButtonToolTipText ( "Export entries to iCalendar file" );
		int ret = fileChooser.showOpenDialog ( this );
		if ( ret == JFileChooser.APPROVE_OPTION ) {
			outFile = fileChooser.getSelectedFile ();
		} else {
			// Cancel
			return;
		}
		// If no file extension provided, use ".ics
		String basename = outFile.getName ();
		if ( basename.indexOf ( '.' ) < 0 ) {
			// No filename extension provided, so add ".csv" to it
			outFile = new File ( outFile.getParent (), basename + ".ics" );
		}
		System.out.println ( "Selected File: " + outFile.toString () );
		lastExportDirectory = outFile.getParentFile ();
		if ( outFile.exists () && !outFile.canWrite () ) {
			JOptionPane.showMessageDialog ( parent,
			    "You do not have the proper\npermissions to write to:\n\n"
			        + outFile.toString () + "\n\nPlease select another file.",
			    "Save Error", JOptionPane.PLAIN_MESSAGE );
			return;
		}
		if ( outFile.exists () ) {
			if ( JOptionPane.showConfirmDialog ( parent,
			    "Overwrite existing file?\n\n" + outFile.toString (),
			    "Overwrite Confirm", JOptionPane.YES_NO_OPTION ) != 0 ) {
				JOptionPane.showMessageDialog ( parent, "Export canceled.",
				    "Export canceled", JOptionPane.PLAIN_MESSAGE );
				return;
			}
		}
		try {
			PrintWriter writer = new PrintWriter ( new FileWriter ( outFile ) );
			// Now write!
			ICalendarParser p = new ICalendarParser ( PARSE_LOOSE );
			DataStore dataStore = p.getDataStoreAt ( 0 );
			for ( int i = 0; i < eventEntries.size (); i++ ) {
				Event j = (Event) eventEntries.elementAt ( i );
				dataStore.storeEvent ( j );
			}
			writer.write ( p.toICalendar () );
			writer.close ();
			JOptionPane.showMessageDialog ( parent, "Exported to:\n\n"
			    + outFile.toString (), "Export", JOptionPane.PLAIN_MESSAGE );
		} catch ( IOException e ) {
			JOptionPane.showMessageDialog ( parent,
			    "An error was encountered\nwriting to the file:\n\n"
			        + e.getMessage (), "Save Error", JOptionPane.PLAIN_MESSAGE );
			e.printStackTrace ();
		}
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

	public void propertyChange ( PropertyChangeEvent pce ) {
		// System.out.println ( "property Change: " + pce );
		if ( pce.getPropertyName ().equals ( JSplitPane.DIVIDER_LOCATION_PROPERTY ) ) {
			saveWindowPreferences ();
		}
	}

	/**
	 * Save current window width, height so we can restore on next run.
	 */
	public void saveWindowPreferences () {
		prefs.setMainWindowX ( this.getX () );
		prefs.setMainWindowY ( this.getY () );
		prefs.setMainWindowWidth ( this.getWidth () );
		prefs.setMainWindowHeight ( this.getHeight () );
		prefs.setMainWindowHorizontalSplitPosition ( horizontalSplit
		    .getDividerLocation () );
	}

	public void eventAdded ( Event event ) {
		handleCalendarFilterSelection ();
	}

	public void eventUpdated ( Event event ) {
		handleCalendarFilterSelection ();
	}

	public void eventDeleted ( Event event ) {
		handleCalendarFilterSelection ();
	}

	public void eventSelected ( EventInstance eventInstance ) {
		updateToolbar ();
	}

	public void eventDoubleClicked ( EventInstance eventInstance ) {
		if ( eventInstance != null ) {
			// TODO: support editing events with recurrance.
			SingleEvent se = (SingleEvent) eventInstance;
			if ( se.calendar.url != null ) {
				showError ( "You cannot edit events\non remote/subscribed calendars." );
			} else if ( se.event.getRrule () != null ) {
				showError ( "Editing events with recurrance\nnot yet supported." );
			} else {
				new EditWindow ( parent, dataRepository, se.event, se.calendar );
			}
		}
	}

	public void dateDoubleClicked ( int year, int month, int dayOfMonth ) {
		try {
			Date d = new Date ( "DTSTART", year, month, dayOfMonth );
			new EditWindow ( parent, dataRepository, d, null );
		} catch ( BogusDataException e1 ) {
			e1.printStackTrace ();
		}
	}

	public void eventUnselected () {
		updateToolbar ();
	}

	/**
	 * @param args
	 */
	public static void main ( String[] args ) {
		new Main ();
	}

}

/**
 * Create a class to use as a file filter for exporting to ics.
 */

class ICSFileChooserFilter extends javax.swing.filechooser.FileFilter {
	public boolean accept ( File f ) {
		if ( f.isDirectory () )
			return true;
		String name = f.getName ();
		if ( name.toLowerCase ().endsWith ( ".ics" ) )
			return true;
		return false;
	}

	public String getDescription () {
		return "*.ics (iCalendar Files)";
	}
}

class ColorButton extends JButton {
	public ColorButton() {
		super ();
		final ColorButton b = this;
		this.addActionListener ( new ActionListener () {
			public void actionPerformed ( ActionEvent event ) {
				Color newColor = JColorChooser.showDialog ( b, "Choose Color", b
				    .getBackground () );
				if ( newColor != null ) {
					b.setBackground ( newColor );
				}
			}
		} );
	}

	public void setSelectedColor ( Color c ) {
		this.setBackground ( c );
	}

	public Color getSelectedColor () {
		return this.getBackground ();
	}

	public void paint ( Graphics g ) {
		super.paint ( g );
		g.setColor ( super.getBackground () );
		g.fill3DRect ( 4, 4, getWidth () - 8, getHeight () - 8, true );
	}

	public Dimension preferredSize () {
		return new Dimension ( 30, 30 );
	}
}
