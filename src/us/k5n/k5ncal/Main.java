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
import java.awt.Component;
import java.awt.Container;
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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import us.k5n.ical.BogusDataException;
import us.k5n.ical.Constants;
import us.k5n.ical.DataStore;
import us.k5n.ical.Date;
import us.k5n.ical.Event;
import us.k5n.ical.ICalendarParser;
import us.k5n.k5ncal.data.Calendar;
import us.k5n.k5ncal.data.HttpClient;
import us.k5n.k5ncal.data.HttpClientStatus;
import us.k5n.k5ncal.data.Repository;
import us.k5n.k5ncal.data.RepositoryChangeListener;
import us.k5n.k5ncal.data.SingleEvent;
import us.k5n.ui.AccordionPane;
import us.k5n.ui.calendar.CalendarPanel;
import us.k5n.ui.calendar.CalendarPanelSelectionListener;
import us.k5n.ui.calendar.EventInstance;
import edu.stanford.ejalbert.BrowserLauncher;

/**
 * Main class for k5nCal application. This application makes use of the k5n
 * iCalendar library (part of Java Calendar Tools) as well as many other 3rd
 * party libraries and tools. See the README.txt file for details. Please see
 * the License.html file for licensing details.
 * 
 * @author Craig Knudsen, craig@k5n.us
 * @version $Id$
 * 
 */
public class Main extends JFrame implements Constants, ComponentListener,
    PropertyChangeListener, RepositoryChangeListener,
    CalendarPanelSelectionListener, CalendarRefresher {
	public static final String DEFAULT_DIR_NAME = "k5nCal";
	public String version = null;;
	public static final String CALENDARS_XML_FILE = "calendars.xml";
	static final String APP_ICON = "images/k5nCal-128x128.png";
	static final String APP_URL = "http://www.k5n.us/k5ncal.php";
	static final String DONATE_URL = "https://sourceforge.net/donate/index.php?group_id=195315";
	static final String REPORT_BUG_URL = "https://sourceforge.net/tracker/?group_id=195315&atid=952950";
	static final String REQUEST_FEATURE_URL = "https://sourceforge.net/tracker/?group_id=195315&atid=952953";
	static final String SUPPORT_REQUEST_URL = "https://sourceforge.net/tracker/?group_id=195315&atid=952951";
	static final String LICENSE_FILE = "License.html";
	static ClassLoader cl = null;
	JFrame parent;
	EventViewPanel eventViewPanel;
	JButton newButton, editButton, deleteButton, largerButton, smallerButton;
	JLabel messageArea;
	Repository dataRepository;
	CalendarPanel calendarPanel;
	JSplitPane horizontalSplit = null, leftVerticalSplit = null;
	AccordionPane ap;
	JListWithCheckBoxes calendarJList;
	JList categoryJList;
	String searchText = null;
	private static File lastExportDirectory = null;
	PreferencesWindow preferencesWindow = null;
	AppPreferences prefs;
	File dataDir = null;
	static final String MENU_CALENDAR_EDIT = "Edit Calendar...";
	static final String MENU_CALENDAR_REFRESH = "Refresh Calendar";
	static final String MENU_CALENDAR_DELETE = "Delete Calendar";
	static final String MENU_CALENDAR_ADD_EVENT = "Add Event...";
	static final String MENU_CALENDAR_VIEW_ERRORS = "View Errors/Warnings...";
	static final String MAIN_WINDOW_HEIGHT = "MainWindow.height";
	static final String MAIN_WINDOW_WIDTH = "MainWindow.width";
	static final String MAIN_WINDOW_X = "MainWindow.x";
	static final String MAIN_WINDOW_Y = "MainWindow.y";
	static final String MAIN_WINDOW_VERTICAL_SPLIT_POSITION = "MainWindow.vSplitPanePosition";
	static final String MAIN_WINDOW_HORIZONTAL_SPLIT_POSITION = "MainWindow.hSplitPanePosition";
	private boolean fontsInitialized = false;
	static final String ADD_EVENT_LABEL = "New...";
	static final String EDIT_EVENT_LABEL = "Edit...";
	static final String DELETE_EVENT_LABEL = "Delete";
	static final String LARGER_FONT_LABEL = "Larger";
	static final String SMALLER_FONT_LABEL = "Smaller";

	public Main() {
		super ( "k5nCal" );
		setWindowsLAF ();
		this.parent = this;

		// Get version from ChangeLog
		this.getVersionFromChangeLog ();

		// TODO: save user's preferred size on exit and set here
		prefs = AppPreferences.getInstance ();

		setSize ( prefs.getMainWindowWidth (), prefs.getMainWindowHeight () );
		this.setLocation ( prefs.getMainWindowX (), prefs.getMainWindowY () );

		setDefaultCloseOperation ( JFrame.EXIT_ON_CLOSE );
		Container contentPane = getContentPane ();

		JLabel test = new JLabel ( "XXX" );
		// Font newFont = new Font ( currentFont.getFamily (),
		// currentFont.getStyle (), currentFont.getSize ()
		// + prefs.getDisplayFontSize () );

		// Load data
		File dataDir = getDataDirectory ();
		dataRepository = new Repository ( dataDir, loadCalendars ( dataDir ), false );
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

		ap = new AccordionPane ();
		ap.addPanel ( "Calendars", createCalendarSelectionPanel ( dataRepository
		    .getCalendars () ) );
		ap.addPanel ( "Categories", createCategorySelectionPanel ( dataRepository
		    .getCategories () ) );
		ap.setTooltipTextAt ( 0, "Manage Calendars" );
		ap.setTooltipTextAt ( 1, "Filter displayed events by category" );

		eventViewPanel = new EventViewPanel ();
		eventViewPanel.setBorder ( BorderFactory
		    .createTitledBorder ( "Event Details" ) );
		leftVerticalSplit = new JSplitPane ( JSplitPane.VERTICAL_SPLIT, ap,
		    eventViewPanel );
		leftVerticalSplit.setOneTouchExpandable ( true );
		leftVerticalSplit.setDividerLocation ( prefs
		    .getMainWindowLeftVerticalSplitPosition () );
		leftVerticalSplit.addPropertyChangeListener ( this );

		JPanel rightPanel = new JPanel ();
		rightPanel.setLayout ( new BorderLayout () );
		rightPanel.add ( createToolBar (), BorderLayout.NORTH );

		calendarPanel = new MyCalendarPanel ( dataRepository );
		calendarPanel.addSelectionListener ( this );
		calendarPanel.setShowTime ( prefs.getDisplayHourInMonthView () );
		rightPanel.add ( calendarPanel, BorderLayout.CENTER );

		horizontalSplit = new JSplitPane ( JSplitPane.HORIZONTAL_SPLIT,
		    leftVerticalSplit, rightPanel );
		horizontalSplit.setOneTouchExpandable ( true );
		horizontalSplit.setDividerLocation ( prefs
		    .getMainWindowHorizontalSplitPosition () );
		horizontalSplit.addPropertyChangeListener ( this );

		this.add ( horizontalSplit, BorderLayout.CENTER );

		this.addComponentListener ( this );
		updateToolbar ();
		this.setVisible ( true );

		// Start a thread to update the remote calendars as needed.
		RemoteCalendarUpdater updater = new RemoteCalendarUpdater (
		    this.dataRepository, this );
		updater.start ();
	}

	public void paint ( Graphics g ) {
		if ( !this.fontsInitialized ) {
			this.fontsInitialized = true;
			Font currentFont = g.getFont ();
			Font newFont = new Font ( currentFont.getFamily (), currentFont
			    .getStyle (), currentFont.getSize () + prefs.getDisplayFontSize () );
			eventViewPanel.setAllFonts ( newFont );
			calendarPanel.setFont ( newFont );
		}
		super.paint ( g );
	}

	public Vector<Calendar> loadCalendars ( File dir ) {
		Vector<Calendar> ret = new Vector<Calendar> ();
		File f = new File ( dir, CALENDARS_XML_FILE );
		if ( !f.exists () ) {
			String name = (String) System.getProperty ( "user.name" );
			if ( name == null )
				name = "Main";
			Calendar def = new Calendar ( dir, name );
			this.showMessage ( "A new calendar named \"" + name
			    + "\"\nwas created for you." );
			ret.addElement ( def );
		} else {
			try {
				ret = Calendar.readCalendars ( f );
			} catch ( Exception e1 ) {
				this.fatalError ( "Error reading calendar file\n" + f + "\n\nError:\n"
				    + e1.getMessage () );
				e1.printStackTrace ();
			}
		}
		return ret;
	}

	public void saveCalendars ( File dir ) {
		File f = new File ( dir, CALENDARS_XML_FILE );
		try {
			Calendar.writeCalendars ( f, dataRepository.getCalendars () );
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

		item = new JMenuItem ( "Preferences..." );
		item.addActionListener ( new ActionListener () {
			public void actionPerformed ( ActionEvent event ) {
				if ( preferencesWindow == null ) {
					preferencesWindow = new PreferencesWindow ( parent, dataRepository );
				} else {
					preferencesWindow.updateUIFromPreferences ();
					preferencesWindow.setVisible ( true );
				}
			}
		} );
		fileMenu.add ( item );

		JMenu importMenu = new JMenu ( "Import" );
		// exportMenu.setMnemonic ( 'X' );
		fileMenu.add ( importMenu );

		item = new JMenuItem ( "iCalendar File" );
		item.addActionListener ( new ActionListener () {
			public void actionPerformed ( ActionEvent event ) {
				importICalendar ();
			}
		} );
		importMenu.add ( item );
		item = new JMenuItem ( "CSV File" );
		item.addActionListener ( new ActionListener () {
			public void actionPerformed ( ActionEvent event ) {
				importCSV ();
			}
		} );
		importMenu.add ( item );

		fileMenu.addSeparator ();

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

		/* Mac users see "Quit", everyone else gets "Exit" */
		boolean isMac = System.getProperty ( "mrj.version" ) != null;
		item = new JMenuItem ( isMac ? "Quit k5nCal" : "Exit" );
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
		item = new JMenuItem ( "Subscribe to Remote..." );
		item.setAccelerator ( KeyStroke.getKeyStroke ( 'S', Toolkit
		    .getDefaultToolkit ().getMenuShortcutKeyMask () ) );
		item.addActionListener ( new ActionListener () {
			public void actionPerformed ( ActionEvent event ) {
				new EditRemoteCalendarWindow ( parent, dataRepository, null,
				    getDataDirectory () );
			}
		} );
		calMenu.add ( item );

		calMenu.addSeparator ();

		JMenu sharedCalMenu = new JMenu ( "Find Shared Calendars" );
		SharedCalendars.updateSharedCalendars ( sharedCalMenu );
		calMenu.add ( sharedCalMenu );

		bar.add ( calMenu );

		// Add help bar to right end of menubar
		bar.add ( Box.createHorizontalGlue () );

		JMenu helpMenu = new JMenu ( "Help" );

		item = new JMenuItem ( "About..." );
		item.setAccelerator ( KeyStroke.getKeyStroke ( 'A', Toolkit
		    .getDefaultToolkit ().getMenuShortcutKeyMask () ) );
		item.addActionListener ( new ActionListener () {
			public void actionPerformed ( ActionEvent event ) {
				// Get application icon
				URL url = getResource ( APP_ICON );
				ImageIcon icon = new ImageIcon ( url, "k5nCal" );
				// Get java version
				String javaVersion = System.getProperty ( "java.version" );
				if ( javaVersion == null )
					javaVersion = "Unknown";
				JOptionPane.showMessageDialog ( parent, "k5nCal "
				    + ( version == null ? "Unknown Version" : version )
				    + "\n\nJava Version: " + javaVersion
				    + "\n\nDeveloped by k5n.us\n\n" + "http://www.k5n.us",
				    "About k5nCal", JOptionPane.INFORMATION_MESSAGE, icon );
			}
		} );
		helpMenu.add ( item );

		item = new JMenuItem ( "View ChangeLog..." );
		item.setAccelerator ( KeyStroke.getKeyStroke ( 'C', Toolkit
		    .getDefaultToolkit ().getMenuShortcutKeyMask () ) );
		item.addActionListener ( new ActionListener () {
			public void actionPerformed ( ActionEvent event ) {
				viewChangeLog ();
			}
		} );
		helpMenu.add ( item );

		item = new JMenuItem ( "View License..." );
		item.addActionListener ( new ActionListener () {
			public void actionPerformed ( ActionEvent event ) {
				viewLicense ();
			}
		} );
		helpMenu.add ( item );

		item = new JMenuItem ( "Go to k5nCal Home Page..." );
		item.addActionListener ( new ActionListener () {
			public void actionPerformed ( ActionEvent event ) {
				try {
					BrowserLauncher bl = new BrowserLauncher ();
					bl.openURLinBrowser ( APP_URL );
				} catch ( Exception e1 ) {
					System.err.println ( "Error starting web browser: "
					    + e1.getMessage () );
					e1.printStackTrace ();
				}
			}
		} );
		helpMenu.add ( item );

		item = new JMenuItem ( "Donate to Support k5nCal..." );
		item.addActionListener ( new ActionListener () {
			public void actionPerformed ( ActionEvent event ) {
				try {
					BrowserLauncher bl = new BrowserLauncher ();
					bl.openURLinBrowser ( DONATE_URL );
				} catch ( Exception e1 ) {
					System.err.println ( "Error starting web browser: "
					    + e1.getMessage () );
					e1.printStackTrace ();
				}
			}
		} );
		helpMenu.add ( item );

		item = new JMenuItem ( "Report Bug..." );
		item.addActionListener ( new ActionListener () {
			public void actionPerformed ( ActionEvent event ) {
				try {
					BrowserLauncher bl = new BrowserLauncher ();
					bl.openURLinBrowser ( REPORT_BUG_URL );
				} catch ( Exception e1 ) {
					System.err.println ( "Error starting web browser: "
					    + e1.getMessage () );
					e1.printStackTrace ();
				}
			}
		} );
		helpMenu.add ( item );

		item = new JMenuItem ( "Get Support..." );
		item.addActionListener ( new ActionListener () {
			public void actionPerformed ( ActionEvent event ) {
				try {
					BrowserLauncher bl = new BrowserLauncher ();
					bl.openURLinBrowser ( SUPPORT_REQUEST_URL );
				} catch ( Exception e1 ) {
					System.err.println ( "Error starting web browser: "
					    + e1.getMessage () );
					e1.printStackTrace ();
				}
			}
		} );
		helpMenu.add ( item );

		item = new JMenuItem ( "Request Feature..." );
		item.addActionListener ( new ActionListener () {
			public void actionPerformed ( ActionEvent event ) {
				try {
					BrowserLauncher bl = new BrowserLauncher ();
					bl.openURLinBrowser ( REQUEST_FEATURE_URL );
				} catch ( Exception e1 ) {
					System.err.println ( "Error starting web browser: "
					    + e1.getMessage () );
					e1.printStackTrace ();
				}
			}
		} );
		helpMenu.add ( item );

		helpMenu.addSeparator ();

		item = new JMenuItem ( "3rd Party Components..." );
		item.addActionListener ( new ActionListener () {
			public void actionPerformed ( ActionEvent event ) {
				new ThirdPartyDialog ( parent );
			}
		} );
		helpMenu.add ( item );

		bar.add ( helpMenu );

		return bar;
	}

	JToolBar createToolBar () {
		JToolBar toolbar = new JToolBar ();
		newButton = makeNavigationButton ( "New24.gif", "new", "Add new event",
		    ADD_EVENT_LABEL );
		newButton.addActionListener ( new ActionListener () {
			public void actionPerformed ( ActionEvent event ) {
				// Make sure there is at least one local calendar.
				boolean foundLocal = false;
				for ( int i = 0; i < dataRepository.getCalendars ().size (); i++ ) {
					Calendar c = dataRepository.getCalendars ().elementAt ( i );
					if ( c.getType () == Calendar.LOCAL_CALENDAR )
						foundLocal = true;
				}
				if ( !foundLocal ) {
					showError ( "You must create a local\ncalendar to add a\nnew event." );
				} else {
					// See if they have selected a local calendar from the
					// calendar list
					Calendar selectedCalendar = null;
					int selCalInd = calendarJList.getSelectedIndex ();
					if ( selCalInd >= 0 ) {
						selectedCalendar = dataRepository.getCalendars ().elementAt (
						    selCalInd );
						if ( selectedCalendar.getType () != Calendar.LOCAL_CALENDAR )
							selectedCalendar = null; // don't allow adding to
						// remote cals
					}
					Date now = Date.getCurrentDateTime ( "DTSTART" );
					now.setMinute ( 0 );
					new EditEventWindow ( parent, dataRepository, now, selectedCalendar );
				}
			}
		} );
		toolbar.add ( newButton );

		editButton = makeNavigationButton ( "Edit24.gif", "edit",
		    "Edit selected event", EDIT_EVENT_LABEL );
		toolbar.add ( editButton );
		editButton.addActionListener ( new ActionListener () {
			public void actionPerformed ( ActionEvent event ) {
				// Get selected item and open edit window
				EventInstance eventInstance = calendarPanel.getSelectedEvent ();
				if ( eventInstance != null ) {
					// NOTE: edit window does not yet support complicated
					// recurrence
					// rules.
					SingleEvent se = (SingleEvent) eventInstance;
					if ( se.getEvent ().getRrule () != null ) {
						new EditEventWindow ( parent, dataRepository, se.getEvent (), se
						    .getCalendar () );
					} else {
						new EditEventWindow ( parent, dataRepository, se.getEvent (), se
						    .getCalendar () );
					}
				}
			}
		} );

		deleteButton = makeNavigationButton ( "Delete24.gif", "delete",
		    "Delete selected event", DELETE_EVENT_LABEL );
		toolbar.add ( deleteButton );
		deleteButton.addActionListener ( new ActionListener () {
			public void actionPerformed ( ActionEvent event ) {
				// Get selected item and open edit window
				EventInstance eventInstance = calendarPanel.getSelectedEvent ();
				if ( eventInstance != null ) {
					SingleEvent se = (SingleEvent) eventInstance;
					if ( se.getEvent ().getRrule () != null ) {
						// TODO: support deleting single occurrence, which will
						// add an
						// exception to the RRULE in the event.
						if ( JOptionPane.showConfirmDialog ( parent,
						    "Are you sure you want\nto delete all occurreces of the\n"
						        + "following repeating event?\n\n" + se.getTitle ()
						        + "\n\nThis will delete ALL events\nin this series.",
						    "Confirm Delete", JOptionPane.YES_NO_OPTION ) == 0 ) {
							try {
								dataRepository.deleteEvent ( se.getCalendar (), se.getEvent () );
							} catch ( IOException e1 ) {
								showError ( "Error deleting." );
								e1.printStackTrace ();
							}
						}
					} else {
						if ( JOptionPane.showConfirmDialog ( parent,
						    "Are you sure you want\nto delete the following event?\n\n"
						        + se.getTitle (), "Confirm Delete",
						    JOptionPane.YES_NO_OPTION ) == 0 ) {
							try {
								dataRepository.deleteEvent ( se.getCalendar (), se.getEvent () );
							} catch ( IOException e1 ) {
								showError ( "Error deleting." );
								e1.printStackTrace ();
							}
						}
					}
				}
			}
		} );

		toolbar.addSeparator ();

		largerButton = makeNavigationButton ( "LargerFont24.png",
		    "Increase Font Size", "Increase font size", LARGER_FONT_LABEL );
		toolbar.add ( largerButton );
		largerButton.addActionListener ( new ActionListener () {
			public void actionPerformed ( ActionEvent event ) {
				int oldOffset = prefs.getDisplayFontSize ();
				if ( oldOffset < 4 )
					prefs.setDisplayFontSize ( oldOffset + 2 );
				displaySettingsChanged ();
			}
		} );

		smallerButton = makeNavigationButton ( "SmallerFont24.png",
		    "Decrease Font Size", "Decrease font size", SMALLER_FONT_LABEL );
		toolbar.add ( smallerButton );
		smallerButton.addActionListener ( new ActionListener () {
			public void actionPerformed ( ActionEvent event ) {
				int oldOffset = prefs.getDisplayFontSize ();
				if ( oldOffset > -4 )
					prefs.setDisplayFontSize ( oldOffset - 2 );
				displaySettingsChanged ();
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
		boolean canEdit = false;
		boolean selected = false;
		EventInstance eventInstance = calendarPanel.getSelectedEvent ();
		selected = ( eventInstance != null );
		if ( selected && eventInstance instanceof SingleEvent ) {
			SingleEvent se = (SingleEvent) eventInstance;
			canEdit = ( se.getCalendar ().getType () == Calendar.LOCAL_CALENDAR )
			    || ( se.getCalendar ().getType () == Calendar.REMOTE_ICAL_CALENDAR && se
			        .getCalendar ().getCanWrite () );
		}
		editButton.setEnabled ( selected && canEdit );
		deleteButton.setEnabled ( selected && canEdit );
		smallerButton.setEnabled ( prefs.getDisplayFontSize () > -4 );
		largerButton.setEnabled ( prefs.getDisplayFontSize () < 4 );
		// Show text?
		if ( prefs.getToolbarIconText () ) {
			this.newButton.setText ( ADD_EVENT_LABEL );
			this.editButton.setText ( EDIT_EVENT_LABEL );
			this.deleteButton.setText ( DELETE_EVENT_LABEL );
			this.largerButton.setText ( LARGER_FONT_LABEL );
			this.smallerButton.setText ( SMALLER_FONT_LABEL );
		} else {
			this.newButton.setText ( null );
			this.editButton.setText ( null );
			this.deleteButton.setText ( null );
			this.largerButton.setText ( null );
			this.smallerButton.setText ( null );
		}
	}

	/**
	 * Create the file selection area on the top side of the window. This will
	 * include a split pane where the left will allow navigation and selection of
	 * dates and the right will allow the selection of a specific entry.
	 * 
	 * @return
	 */
	protected JPanel createCalendarSelectionPanel ( Vector calendars ) {
		JPanel topPanel = new JPanel ();
		topPanel.setLayout ( new BorderLayout () );

		this.calendarJList = new JListWithCheckBoxes ( new Vector<Object> () );
		updateCalendarJList ();
		this.calendarJList
		    .addListItemChangeListener ( new ListItemChangeListener () {
			    public void itemSelected ( int ind ) {
				    dataRepository.getCalendars ().elementAt ( ind )
				        .setSelected ( true );
				    handleCalendarFilterSelection ();
			    }

			    public void itemUnselected ( int ind ) {
				    dataRepository.getCalendars ().elementAt ( ind )
				        .setSelected ( true );
				    handleCalendarFilterSelection ();
			    }

			    public Vector<ListItemMenuItem> getMenuChoicesForIndex ( int ind ) {
				    Vector<ListItemMenuItem> ret = new Vector<ListItemMenuItem> ();
				    Calendar c = dataRepository.getCalendars ().elementAt ( ind );
				    ret.addElement ( new ListItemMenuItem ( MENU_CALENDAR_EDIT ) );
				    ret.addElement ( new ListItemMenuItem ( MENU_CALENDAR_REFRESH, c
				        .getType () != Calendar.LOCAL_CALENDAR ) );
				    ret.addElement ( new ListItemMenuItem ( MENU_CALENDAR_DELETE ) );
				    ret.addElement ( new ListItemMenuItem ( MENU_CALENDAR_ADD_EVENT, c
				        .getType () == Calendar.LOCAL_CALENDAR ) );
				    if ( c.getType () == Calendar.REMOTE_ICAL_CALENDAR ) {
					    // Does this calendar have errors?
					    // TODO: implement error viewer...
					    // Vector<ParseError> errors =
					    // dataRepository.getErrorsAt ( ind );
					    // if ( errors != null && errors.size () > 0 )
					    // ret.addElement ( MENU_CALENDAR_VIEW_ERRORS + " "
					    // + "("
					    // + errors.size () + ")" );
				    }
				    return ret;
			    }

			    public void menuChoice ( int ind, String actionCommand ) {
				    Calendar c = dataRepository.getCalendars ().elementAt ( ind );
				    if ( MENU_CALENDAR_EDIT.equals ( actionCommand ) ) {
					    editCalendar ( c );
				    } else if ( MENU_CALENDAR_REFRESH.equals ( actionCommand ) ) {
					    if ( c.getType () == Calendar.LOCAL_CALENDAR ) {
						    showError ( "You can only refresh\nremote/subscribed calendars" );
					    } else {
						    refreshCalendar ( c );
					    }
				    } else if ( MENU_CALENDAR_DELETE.equals ( actionCommand ) ) {
					    if ( JOptionPane.showConfirmDialog ( parent,
					        "Are you sure you want to\nDelete the following calendar?\n\n"
					            + c.toString (), "Confirm Delete",
					        JOptionPane.YES_NO_OPTION ) == 0 ) {
						    deleteCalendar ( c );
					    }
				    } else if ( MENU_CALENDAR_ADD_EVENT.equals ( actionCommand ) ) {
					    Date now = Date.getCurrentDateTime ( "DTSTART" );
					    now.setMinute ( 0 );
					    new EditEventWindow ( parent, dataRepository, now, c );
				    } else {
					    System.err.println ( "Unknown menu command: " + actionCommand );
				    }
			    }
		    } );

		for ( int i = 0; i < dataRepository.getCalendars ().size (); i++ ) {
			Calendar c = dataRepository.getCalendars ().elementAt ( i );
			ListItem item = this.calendarJList.getListItemAt ( i );
			item
			    .setState ( c.isSelected () ? ListItem.STATE_YES : ListItem.STATE_OFF );
		}

		topPanel
		    .add ( new MyScrollPane ( this.calendarJList ), BorderLayout.CENTER );

		return topPanel;
	}

	// Handle user selecting a calendar checkbox to display/hide a calendar
	void handleCalendarFilterSelection () {
		// Repaint the calendar view, which will reload the data
		for ( int i = 0; i < dataRepository.getCalendars ().size (); i++ ) {
			dataRepository
			    .getCalendars ()
			    .elementAt ( i )
			    .setSelected (
			        this.calendarJList.getListItemAt ( i ).getState () == ListItem.STATE_YES );
		}
		this.calendarPanel.clearSelection ();
		this.dataRepository.rebuild ();
		this.calendarPanel.repaint ();
	}

	public void editCalendar ( Calendar c ) {
		if ( c.getType () == Calendar.LOCAL_CALENDAR ) {
			editLocalCalendar ( c );
		} else {
			new EditRemoteCalendarWindow ( parent, dataRepository, c,
			    getDataDirectory () );
		}
	}

	/**
	 * Refresh the specified calendar by reloading it from its URL. Because this
	 * is likely to take a second or more in ideal circumstances (and much longer
	 * in many cases), we will use the SwingWorker class to execute this in a
	 * separate thread so we don't lock up the UI.
	 * 
	 * @param cal
	 *          The Calendar to refresh
	 */
	public void refreshCalendar ( final Calendar cal ) {
		// Before we get started, update the status bar to indicate we are
		// loading
		// the calendar.
		showStatusMessage ( "Refreshing calendar '" + cal.getName () + "' ..." );

		SwingWorker refreshWorker = new SwingWorker () {
			private String error = null;
			private String statusMsg = null;
			private File outputFile = null;

			public Object construct () {
				// Execute time-consuming task...
				// For now, we only support HTTP/HTTPS since 99.99% of all users
				// will
				// use it
				// instead of something like FTP.
				outputFile = new File ( dataDir, cal.getFilename () + ".new" );
				String username = null, password = null;
				if ( cal.getAuthType () == Calendar.AUTH_BASIC ) {
					username = cal.getAuthUsername ();
					password = cal.getAuthPassword ();
				}
				HttpClientStatus result = HttpClient.getRemoteCalendar ( cal.getUrl (),
				    username, password, outputFile );
				// We're not supposed to make UI calls from this thread. So,
				// when
				// we get an error, save it in the error variable for use in the
				// finished method.
				// TODO: implement a way to show these errors to the user.
				switch ( result.getStatus () ) {
					case HttpClientStatus.HTTP_STATUS_SUCCESS:
						statusMsg = "Calendar '" + cal.getName ()
						    + "' successfully refreshed.";
						break;
					case HttpClientStatus.HTTP_STATUS_AUTH_REQUIRED:
						error = "Authorization required.\nPlease provide a username\n"
						    + "and password.";
						return null;
					case HttpClientStatus.HTTP_STATUS_NOT_FOUND:
						error = "Invalid calendar URL (not found).\n\nServer response: "
						    + result.getMessage ();
						return null;
					default:
					case HttpClientStatus.HTTP_STATUS_OTHER_ERROR:
						error = "Error downloading calendar.\n\nServer response: "
						    + result.getMessage ();
						return null;
				}
				return null;
			}

			public void finished () {
				// Update UI
				if ( error != null )
					showError ( error );
				if ( this.statusMsg != null )
					showStatusMessage ( statusMsg );
				if ( error == null ) {
					// TODO: validate what we downloaded was ICS data rather
					// than an HTML
					// page
					// Rename file from ".ics.new" to ".ics"
					File file = new File ( dataDir, cal.getFilename () );
					// Delete old file first since renameTo may file if file
					// already
					// exists
					file.delete ();
					// Now rename
					if ( !outputFile.renameTo ( file ) ) {
						// Error renaming
						showError ( "Error renaming file" );
					} else {
						// System.out.println ( "Renamed " + outputFile + " to "
						// + file );
						// If no error, then save calendar update
						cal.setLastUpdatedAsNow ();
						saveCalendars ( dataDir );
						dataRepository.updateCalendar ( getDataDirectory (), cal );
					}
				}
			}
		};
		refreshWorker.start ();
	}

	public void deleteCalendar ( Calendar c ) {
		boolean found = false;
		for ( int i = 0; i < dataRepository.getCalendars ().size () && !found; i++ ) {
			Calendar c1 = (Calendar) dataRepository.getCalendars ().elementAt ( i );
			if ( c1.equals ( c ) ) {
				dataRepository.removeCalendar ( getDataDirectory (), c );
				found = true;
			}
		}
		if ( found ) {
			updateCalendarJList ();
			updateCategoryJList ();
			this.dataRepository.rebuild ();
			this.calendarPanel.repaint ();
			saveCalendars ( getDataDirectory () );
		} else {
			System.err.println ( "deleteCalendar: could not find calendar!" );
		}
	}

	/**
	 * Update the list of Calendars shown to the user
	 */
	public void updateCalendarJList () {
		this.calendarJList.setChoices ( dataRepository.getCalendars () );
		for ( int i = 0; i < dataRepository.getCalendars ().size (); i++ ) {
			Calendar cal = (Calendar) dataRepository.getCalendars ().elementAt ( i );
			ListItem item = this.calendarJList.getListItemAt ( i );
			item.setBackground ( cal.getBackgroundColor () );
			item.setForeground ( cal.getForegroundColor () );
			item.setState ( cal.isSelected () ? ListItem.STATE_YES
			    : ListItem.STATE_OFF );
		}
		this.calendarJList.validate ();
	}

	protected JPanel createCategorySelectionPanel ( Vector categories ) {
		JPanel panel = new JPanel ();
		panel.setLayout ( new BorderLayout () );
		this.categoryJList = new JList ( categories );
		final JList list = this.categoryJList;
		updateCategoryJList ();

		JPanel buttonPanel = new JPanel ();
		buttonPanel.setLayout ( new FlowLayout () );
		JButton allButton = new JButton ( "Select All" );
		allButton.addActionListener ( new ActionListener () {
			public void actionPerformed ( ActionEvent e ) {
				int len = list.getModel ().getSize ();
				if ( len > 0 )
					list.getSelectionModel ().setSelectionInterval ( 0, len - 1 );
			}
		} );
		buttonPanel.add ( allButton );
		JButton noneButton = new JButton ( "Clear" );
		noneButton.addActionListener ( new ActionListener () {
			public void actionPerformed ( ActionEvent e ) {
				list.clearSelection ();
			}
		} );
		buttonPanel.add ( noneButton );
		panel.add ( buttonPanel, BorderLayout.SOUTH );

		// Add handler for when user changes category selectios
		list.addListSelectionListener ( new ListSelectionListener () {
			public void valueChanged ( ListSelectionEvent e ) {
				handleCategoryFilterSelection ();
			}
		} );

		JScrollPane sp = new MyScrollPane ( this.categoryJList );
		panel.add ( sp, BorderLayout.CENTER );
		return panel;
	}

	// Handle user selecting a category to filter by
	void handleCategoryFilterSelection () {
		boolean uncategorized = this.categoryJList.getSelectionModel ()
		    .isSelectedIndex ( 0 );
		int[] selected = this.categoryJList.getSelectedIndices ();
		Vector selectedCats = new Vector<String> ();
		for ( int i = 0; i < selected.length; i++ ) {
			if ( selected[i] > 0 ) {
				String cat = (String) this.categoryJList.getModel ().getElementAt (
				    selected[i] );
				selectedCats.addElement ( cat );
			}
		}
		if ( selected == null || selected.length == 0 ) {
			ap.setTitleAt ( 1, "Categories" );
			this.dataRepository.clearCategoryFilter ();
		} else {
			ap.setTitleAt ( 1, "Categories [" + selected.length + "]" );
			this.dataRepository.setCategoryFilter ( uncategorized, selectedCats );
		}
		this.calendarPanel.clearSelection ();
		this.dataRepository.rebuild ();
		this.calendarPanel.repaint ();
	}

	/**
	 * Update the list of Calendars shown to the user
	 */
	public void updateCategoryJList () {
		// Get current selections so we can preserve
		Object[] oldSelections = this.categoryJList.getSelectedValues ();
		HashMap<String, String> old = new HashMap<String, String> ();
		for ( int i = 0; i < oldSelections.length; i++ ) {
			old.put ( oldSelections[i].toString (), oldSelections[i].toString () );
		}
		Vector cats = dataRepository.getCategories ();
		// Sort categories alphabetically
		Collections.sort ( cats );
		cats.insertElementAt ( "Uncategorized", 0 );
		this.categoryJList.setListData ( cats );
		int[] newSelections = new int[oldSelections.length];
		int j = 0;
		for ( int i = 0; i < dataRepository.getCategories ().size (); i++ ) {
			String cat = (String) dataRepository.getCategories ().elementAt ( i );
			if ( old.containsKey ( cat ) )
				newSelections[j++] = i + 1; // skip over "uncategorized"
		}
		int[] indices = new int[j];
		for ( int i = 0; i < j; i++ ) {
			indices[i] = newSelections[i];
		}
		this.categoryJList.setSelectedIndices ( indices );
		this.categoryJList.validate ();
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
		    JOptionPane.ERROR_MESSAGE );
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
			imageURL = getResource ( imgLocation );
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

	protected void editLocalCalendar ( final Calendar c ) {
		final JDialog addLocal = new JDialog ( this );
		addLocal.setLocationRelativeTo ( null );
		final JTextField nameField = new JTextField ( 30 );
		final ColorButton colorField = new ColorButton ();
		int[] props = { 1, 2 };

		if ( c != null ) {
			nameField.setText ( c.getName () );
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
					showStatusMessage ( "Creating calendar..." );
					Calendar cal = null;
					if ( c == null ) {
						cal = new Calendar ( getDataDirectory (), name );
					} else {
						cal = c;
					}
					cal.setBackgroundColor ( color );
					cal.setBorderColor ( getForegroundColorForBackground ( color ) );
					cal.setForegroundColor ( getForegroundColorForBackground ( color ) );
					cal.setLastUpdatedAsNow ();
					File file = new File ( getDataDirectory (), cal.getFilename () );
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
						dataRepository.addCalendar ( getDataDirectory (), cal, false );
						// This will call us back with calendarAdded (below)
					} else {
						// updating calendar...
						dataRepository.updateCalendar ( getDataDirectory (), cal );
						showStatusMessage ( "Updated local calendar \"" + name
						    + "\" updated" );
					}
					System.out.println ( "Created cal file: " + file );
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
		colorField.setSelectedColor ( c == null ? Color.blue : c
		    .getBackgroundColor () );
		colorSub.add ( colorField, BorderLayout.WEST );
		colorPanel.add ( colorSub );
		main.add ( colorPanel );

		content.add ( main, BorderLayout.CENTER );

		addLocal.pack ();
		addLocal.setVisible ( true );
	}

	protected void importCSV () {
		new ImportDialog ( this, ImportDialog.IMPORT_CSV, getDataDirectory (),
		    dataRepository );
	}

	protected void importICalendar () {
		new ImportDialog ( this, ImportDialog.IMPORT_ICS, getDataDirectory (),
		    dataRepository );
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
		int ret = fileChooser.showSaveDialog ( this );
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
		prefs.setMainWindowLeftVerticalSplitPosition ( leftVerticalSplit
		    .getDividerLocation () );
		prefs.setMainWindowHorizontalSplitPosition ( horizontalSplit
		    .getDividerLocation () );
	}

	public void eventAdded ( Event event ) {
		this.updateCategoryJList ();
		handleCalendarFilterSelection ();
		this.eventViewPanel.clear ();
	}

	public void eventUpdated ( Event event ) {
		this.updateCategoryJList ();
		handleCalendarFilterSelection ();
		this.eventViewPanel.clear ();
	}

	public void eventDeleted ( Event event ) {
		this.updateCategoryJList ();
		handleCalendarFilterSelection ();
		this.eventViewPanel.clear ();
	}

	public void eventSelected ( EventInstance eventInstance ) {
		SingleEvent se = (SingleEvent) eventInstance;
		Date eventDate = null;
		try {
			eventDate = new Date ( "DTSTART", eventInstance.getYear (), eventInstance
			    .getMonth (), eventInstance.getDayOfMonth () );
		} catch ( BogusDataException e1 ) {
			e1.printStackTrace ();
			return;
		}
		if ( eventInstance.hasTime () ) {
			eventDate.setDateOnly ( false );
			eventDate.setHour ( eventInstance.getHour () );
			eventDate.setMinute ( eventInstance.getMinute () );
			eventDate.setSecond ( eventInstance.getSecond () );
		} else {
			eventDate.setDateOnly ( true );
		}
		updateToolbar ();
		this.eventViewPanel.update ( eventDate, se.getEvent (), se.getCalendar () );
		// Select the calendar on the left that the selected event belongs to.
		if ( se.getCalendar () != null ) {
			int ind = -1;
			for ( int i = 0; i < this.dataRepository.getCalendars ().size ()
			    && ind < 0; i++ ) {
				Calendar c = this.dataRepository.getCalendars ().elementAt ( i );
				if ( c.equals ( se.getCalendar () ) )
					ind = i;
			}
			if ( ind >= 0 )
				this.calendarJList.setSelectedIndex ( ind );
		}
	}

	public void calendarAdded ( Calendar c ) {
		updateCalendarJList ();
		updateCategoryJList ();
		saveCalendars ( getDataDirectory () );
		this.dataRepository.rebuild ();
		this.calendarPanel.repaint ();
	}

	public void calendarUpdated ( Calendar c ) {
		updateCalendarJList ();
		updateCategoryJList ();
		saveCalendars ( getDataDirectory () );
		this.dataRepository.rebuild ();
		this.calendarPanel.repaint ();
	}

	public void calendarDeleted ( Calendar c ) {
		updateCalendarJList ();
		updateCategoryJList ();
		saveCalendars ( getDataDirectory () );
		this.dataRepository.rebuild ();
		this.calendarPanel.repaint ();
	}

	public void displaySettingsChanged () {
		this.calendarPanel.setShowTime ( prefs.getDisplayHourInMonthView () );
		Font oldFont = this.calendarPanel.getFont ();
		Font defaultFont = this.getFont ();
		Font newFont = new Font ( oldFont.getFamily (), oldFont.getStyle (),
		    defaultFont.getSize () + prefs.getDisplayFontSize () );
		this.calendarPanel.setFont ( newFont );
		this.dataRepository.rebuild ();
		this.calendarPanel.repaint ();
		this.eventViewPanel.setAllFonts ( newFont );
		updateToolbar ();
	}

	public void eventDoubleClicked ( EventInstance eventInstance ) {
		if ( eventInstance != null ) {
			SingleEvent se = (SingleEvent) eventInstance;
			boolean canEdit = ( se.getCalendar ().getType () == Calendar.LOCAL_CALENDAR )
			    || ( se.getCalendar ().getType () == Calendar.REMOTE_ICAL_CALENDAR && se
			        .getCalendar ().getCanWrite () );
			if ( !canEdit ) {
				showError ( "You cannot edit events\non the '"
				    + se.getCalendar ().getName () + "' calendar." );
			} else {
				new EditEventWindow ( parent, dataRepository, se.getEvent (), se
				    .getCalendar () );
			}
		}
	}

	public void dateDoubleClicked ( int year, int month, int dayOfMonth ) {
		try {
			Date d = new Date ( "DTSTART", year, month, dayOfMonth );
			new EditEventWindow ( parent, dataRepository, d, null );
		} catch ( BogusDataException e1 ) {
			e1.printStackTrace ();
		}
	}

	public void eventUnselected () {
		updateToolbar ();
		this.eventViewPanel.clear ();
	}

	URL getResource ( String name ) {
		return this.getClass ().getClassLoader ().getResource ( name );
	}

	void getVersionFromChangeLog () {
		if ( this.version != null )
			return;

		URL url = getResource ( "ChangeLog" );
		if ( url == null ) {
			System.err.println ( "Error: could not find ChangeLog in your CLASSPATH" );
			return;
		}
		try {
			InputStream is = url.openStream ();
			BufferedReader reader = new BufferedReader ( new InputStreamReader ( is ) );
			while ( this.version == null ) {
				String line = reader.readLine ();
				if ( line == null )
					break; // not found
				line = line.trim ();
				if ( line.toUpperCase ().startsWith ( "VERSION" ) ) {
					String[] args = line.split ( "-" );
					this.version = args[0].trim ();
				}
			}
			is.close ();
		} catch ( IOException e1 ) {
			e1.printStackTrace ();
			this.version = "Unknown Version (" + e1.getMessage () + ")";
		}
		return;
	}

	public void viewChangeLog () {
		URL url = getResource ( "ChangeLog" );
		try {
			InputStream is = url.openStream ();
			BufferedReader reader = new BufferedReader ( new InputStreamReader ( is ) );
			String line;
			StringBuffer sb = new StringBuffer ();
			while ( ( line = reader.readLine () ) != null ) {
				sb.append ( line );
				sb.append ( "\n" );
			}
			is.close ();
			final JDialog d = new JDialog ();
			d.getContentPane ().setLayout ( new BorderLayout () );
			d.setTitle ( "Change Log" );
			d.setSize ( 500, 400 );
			d.setLocationByPlatform ( true );
			JPanel buttonPanel = new JPanel ();
			buttonPanel.setLayout ( new FlowLayout () );
			JButton b = new JButton ( "Close" );
			b.addActionListener ( // Anonymous class as a listener.
			    new ActionListener () {
				    public void actionPerformed ( ActionEvent e ) {
					    d.dispose ();
				    }
			    } );
			buttonPanel.add ( b );
			d.getContentPane ().add ( buttonPanel, BorderLayout.SOUTH );
			JTextArea te = new JTextArea ( sb.toString () );
			Font f = new Font ( te.getFont ().getFamily (), Font.PLAIN, 10 );
			te.setFont ( f );
			te.setEditable ( false );
			JScrollPane sp = new MyScrollPane ( te );
			sp.getVerticalScrollBar ().setValue ( 0 );
			JPanel p = new JPanel ();
			p.setLayout ( new BorderLayout () );
			p.setBorder ( BorderFactory.createTitledBorder ( "Change Log" ) );
			p.add ( sp, BorderLayout.CENTER );
			d.getContentPane ().add ( p, BorderLayout.CENTER );
			d.show ();
		} catch ( Exception e1 ) {
			e1.printStackTrace ();
			showMessage ( "Error:\n" + e1.getMessage () );
		}
	}

	public void viewLicense () {
		URL url = getResource ( LICENSE_FILE );
		if ( url == null ) {
			System.err.println ( "Unable to find license file: " + LICENSE_FILE );
			return;
		}
		try {
			final JDialog d = new JDialog ();
			d.getContentPane ().setLayout ( new BorderLayout () );
			d.setTitle ( "k5nCal License" );
			d.setSize ( 500, 400 );
			d.setLocationByPlatform ( true );
			JPanel buttonPanel = new JPanel ();
			buttonPanel.setLayout ( new FlowLayout () );
			JButton b = new JButton ( "Close" );
			b.addActionListener ( // Anonymous class as a listener.
			    new ActionListener () {
				    public void actionPerformed ( ActionEvent e ) {
					    d.dispose ();
				    }
			    } );
			buttonPanel.add ( b );
			d.getContentPane ().add ( buttonPanel, BorderLayout.SOUTH );
			HelpPanel licenseText = new HelpPanel ( url );
			d.getContentPane ().add ( licenseText, BorderLayout.CENTER );
			d.show ();
		} catch ( Exception e1 ) {
			e1.printStackTrace ();
			showMessage ( "Error:\n" + e1.getMessage () );
		}
	}

	/**
	 * @param args
	 */
	public static void main ( String[] args ) {
		Vector<String> remoteNames = new Vector<String> ();
		Vector<String> remoteURLs = new Vector<String> ();

		// Check for command line options
		for ( int i = 0; i < args.length; i++ ) {
			if ( args[i].equals ( "-addcalendar" ) ) {
				if ( args.length >= i + 1 ) {
					String name = args[++i].trim ();
					String url = args[++i].trim ();
					if ( url.startsWith ( "http://" ) || url.startsWith ( "https://" ) ) {
						remoteNames.addElement ( name );
						remoteURLs.addElement ( url );
					} else {
						System.err.println ( "Ignoring invalid url: " + url );
					}
				} else {
					System.err
					    .println ( "Error: -addcalendar requires name and URL parameter" );
					System.exit ( 1 );
				}
			} else {
				System.err.println ( "Unknown command line option: " + args[i] );
			}

		}
		Main app = new Main ();
		// Add calendars if not there...
		for ( int i = 0; i < remoteURLs.size (); i++ ) {
			String name = remoteNames.elementAt ( i );
			String url = remoteURLs.elementAt ( i );
			if ( app.dataRepository.hasCalendarWithURL ( url ) ) {
				System.out
				    .println ( "Ignoring add calendar from duplicate URL: " + url );
			} else {
				// auto-add the calendar....
				app.addCalendarFromCommandLine ( name, url );
			}
		}
	}

	private void addCalendarFromCommandLine ( String name, String urlStr ) {
		URL url = null;
		try {
			url = new URL ( urlStr );
		} catch ( Exception e1 ) {
			showError ( "Invalid URL:\n" + e1.getMessage () );
			return;
		}
		showStatusMessage ( "Downloading calendar '" + name + "'" );
		final Calendar cal = new Calendar ( getDataDirectory (), name, url, 30 );
		cal.setBackgroundColor ( Color.blue );
		cal.setForegroundColor ( getForegroundColorForBackground ( Color.blue ) );
		cal.setBorderColor ( getForegroundColorForBackground ( Color.blue ) );
		cal.setLastUpdatedAsNow ();
		cal.setUrl ( url );

		SwingWorker addWorker = new SwingWorker () {
			private String error = null;
			private String statusMsg = null;

			public Object construct () {
				File file = new File ( getDataDirectory (), cal.getFilename () );
				HttpClientStatus result = HttpClient.getRemoteCalendar ( cal.getUrl (),
				    null, null, file );
				switch ( result.getStatus () ) {
					case HttpClientStatus.HTTP_STATUS_SUCCESS:
						break;
					case HttpClientStatus.HTTP_STATUS_AUTH_REQUIRED:
						showError ( "Authorization required.\nPlease provide a username\n"
						    + "and password." );
						return null;
					case HttpClientStatus.HTTP_STATUS_NOT_FOUND:
						showError ( "Invalid calendar URL (not found).\n\nServer response: "
						    + result.getMessage () );
						return null;
					default:
					case HttpClientStatus.HTTP_STATUS_OTHER_ERROR:
						showError ( "Error downloading calendar.\n\nServer response: "
						    + result.getMessage () );
						return null;
				}
				return null;
			}

			public void finished () {
				if ( error == null )
					dataRepository.addCalendar ( getDataDirectory (), cal, false );
				// Update UI
				if ( error != null )
					showError ( error );
				if ( this.statusMsg != null )
					showStatusMessage ( statusMsg );
				if ( error == null ) {
					// If no error, then save calendar update
					cal.setLastUpdatedAsNow ();
					saveCalendars ( dataDir );
					dataRepository.updateCalendar ( getDataDirectory (), cal );
				}
			}
		};
		addWorker.start ();
		// updating calendar...
		dataRepository.updateCalendar ( getDataDirectory (), cal );
		showStatusMessage ( "Calendar \"" + name + "\" added" );
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
