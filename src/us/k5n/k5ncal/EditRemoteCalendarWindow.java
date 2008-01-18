package us.k5n.k5ncal;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import us.k5n.k5ncal.data.Calendar;
import us.k5n.k5ncal.data.HttpClient;
import us.k5n.k5ncal.data.HttpClientStatus;
import us.k5n.k5ncal.data.Repository;

public class EditRemoteCalendarWindow extends JDialog {
	JFrame parent;
	Repository dataRepository;
	Calendar c;
	File dataDir;
	JTextField nameField;
	JTextField urlField;
	ColorButton colorField;
	String[] updateIntervalChoices = { "12 Hours", "1 Day", "3 Days", "7 Days",
	    "14 Days", "30 Days", "90 Days", "1 Year", "Never" };
	int[] updateIntervalChoiceValues = { 12, 24, 24 * 3, 24 * 7, 24 * 14,
	    24 * 30, 24 * 90, 24 * 365, 0 };
	int defChoice = 5;
	JComboBox updateField;
	JComboBox modeField;
	String[] modeChoices = { "Read-Only", "Read/Publish" };
	JPanel syncBeforePanel;
	JCheckBox syncBeforePublishField;
	JComboBox authField;
	String[] authChoices = { "None", "Basic" };
	int[] authChoiceValues = { Calendar.AUTH_NONE, Calendar.AUTH_BASIC };
	JTextField usernameField;
	JTextField passwordField;
	Vector<JComponent> visibleAuthEnabled;

	public EditRemoteCalendarWindow(JFrame parent,
	    final Repository dataRepository, final Calendar c, final File dataDir) {
		super ( parent );
		this.dataRepository = dataRepository;
		this.c = c;
		this.dataDir = dataDir;
		this.parent = parent;
		this.setLocationByPlatform ( true );
		this.visibleAuthEnabled = new Vector<JComponent> ();

		nameField = new JTextField ( 40 );
		urlField = new JTextField ( 40 );
		colorField = new ColorButton ();
		authField = new JComboBox ( authChoices );
		usernameField = new JTextField ( 25 );
		passwordField = new JPasswordField ( 25 );
		updateField = new JComboBox ( updateIntervalChoices );
		modeField = new JComboBox ( modeChoices );
		syncBeforePanel = new JPanel ();
		syncBeforePublishField = new JCheckBox ();
		int[] props = { 1, 2 };

		if ( c != null ) {
			for ( int i = 0; i < updateIntervalChoiceValues.length; i++ ) {
				if ( c.getUpdateIntervalSecs () == updateIntervalChoiceValues[i] * 3600 )
					defChoice = i;
			}
			nameField.setText ( c.getName () );
			// Don't allow changing of URL. Must delete and add new
			urlField.setText ( c.getUrl ().toString () );
			urlField.setEditable ( false );
			modeField.setSelectedIndex ( c.getCanWrite () ? 1 : 0 );
			syncBeforePanel.setVisible ( c.getCanWrite () );
			syncBeforePublishField.setSelected ( c.getSyncBeforePublish () );
			switch ( c.getAuthType () ) {
				case Calendar.AUTH_NONE:
					authField.setSelectedIndex ( 0 );
					break;
				case Calendar.AUTH_BASIC:
					authField.setSelectedIndex ( 1 );
					usernameField.setText ( c.getAuthUsername () );
					passwordField.setText ( c.getAuthPassword () );
					break;
			}
		} else {
			// default settings
			syncBeforePanel.setVisible ( false );
		}

		this.setTitle ( c != null ? "Edit Remote Calendar" : "Add Remote Calendar" );
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
		JButton ok = new JButton ( c == null ? "Add" : "Save" );
		ok.addActionListener ( new ActionListener () {
			public void actionPerformed ( ActionEvent event ) {
				okHandler ();
			}
		} );
		buttonPanel.add ( ok );
		content.add ( buttonPanel, BorderLayout.SOUTH );

		JPanel main = new JPanel ();
		main
		    .setBorder ( BorderFactory.createTitledBorder ( "New Remote Calendar" ) );
		main.setLayout ( new GridLayout ( 9, 1 ) );
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

		JPanel updatePanel = new JPanel ();
		updatePanel.setLayout ( new ProportionalLayout ( props,
		    ProportionalLayout.HORIZONTAL_LAYOUT ) );
		updatePanel.add ( new JLabel ( "Update Interval: " ) );
		if ( defChoice < updateIntervalChoices.length )
			updateField.setSelectedIndex ( defChoice );
		JPanel updateSubPanel = new JPanel ( new BorderLayout () );
		updateSubPanel.add ( updateField, BorderLayout.WEST );
		updatePanel.add ( updateSubPanel );
		main.add ( updatePanel );

		JPanel modePanel = new JPanel ();
		modePanel.setLayout ( new ProportionalLayout ( props,
		    ProportionalLayout.HORIZONTAL_LAYOUT ) );
		modePanel.add ( new JLabel ( "Mode: " ) );
		JPanel modePanelSubPanel = new JPanel ( new BorderLayout () );
		modePanelSubPanel.add ( modeField, BorderLayout.WEST );
		modePanel.add ( modePanelSubPanel );
		main.add ( modePanel );

		syncBeforePanel.setLayout ( new ProportionalLayout ( props,
		    ProportionalLayout.HORIZONTAL_LAYOUT ) );
		syncBeforePanel.add ( new JLabel ( "   " + "Sync before publish:" ) );
		JPanel autoPublishSubPanel = new JPanel ( new BorderLayout () );
		autoPublishSubPanel.add ( syncBeforePublishField, BorderLayout.WEST );
		syncBeforePanel.add ( autoPublishSubPanel );
		main.add ( syncBeforePanel );

		JPanel authPanel = new JPanel ( new ProportionalLayout ( props,
		    ProportionalLayout.HORIZONTAL_LAYOUT ) );
		authPanel.add ( new JLabel ( "Authentication:" ) );
		JPanel authSubPanel = new JPanel ( new BorderLayout () );
		authSubPanel.add ( authField, BorderLayout.WEST );
		authPanel.add ( authSubPanel );
		main.add ( authPanel );

		JPanel usernamePanel = new JPanel ( new ProportionalLayout ( props,
		    ProportionalLayout.HORIZONTAL_LAYOUT ) );
		usernamePanel.add ( new JLabel ( "   " + "Username:" ) );
		JPanel usernameSubPanel = new JPanel ( new BorderLayout () );
		usernameSubPanel.add ( usernameField, BorderLayout.WEST );
		usernamePanel.add ( usernameSubPanel );
		main.add ( usernamePanel );
		this.visibleAuthEnabled.addElement ( usernamePanel );

		JPanel passwordPanel = new JPanel ( new ProportionalLayout ( props,
		    ProportionalLayout.HORIZONTAL_LAYOUT ) );
		passwordPanel.add ( new JLabel ( "   " + "Password:" ) );
		JPanel passwordSubPanel = new JPanel ( new BorderLayout () );
		passwordSubPanel.add ( passwordField, BorderLayout.WEST );
		passwordPanel.add ( passwordSubPanel );
		main.add ( passwordPanel );
		this.visibleAuthEnabled.addElement ( passwordPanel );

		authField.addActionListener ( new ActionListener () {
			public void actionPerformed ( ActionEvent e1 ) {
				authChangeHandler ();
			}
		} );

		modeField.addActionListener ( new ActionListener () {
			public void actionPerformed ( ActionEvent e1 ) {
				modeChangeHandler ();
			}
		} );

		content.add ( main, BorderLayout.CENTER );

		this.authChangeHandler ();

		this.pack ();
		this.setVisible ( true );
	}

	void authChangeHandler () {
		int authType = this.authChoiceValues[this.authField.getSelectedIndex ()];
		boolean authVisible = ( authType != Calendar.AUTH_NONE );
		for ( int i = 0; i < this.visibleAuthEnabled.size (); i++ ) {
			JComponent c = this.visibleAuthEnabled.elementAt ( i );
			c.setVisible ( authVisible );
		}
	}

	void modeChangeHandler () {
		boolean modeVisible = ( this.modeField.getSelectedIndex () > 0 );
		this.syncBeforePanel.setVisible ( modeVisible );
	}

	void okHandler () {
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
		// Only allow HTTP
		if ( !urlStr.toUpperCase ().startsWith ( "HTTP://" )
		    && !urlStr.toUpperCase ().startsWith ( "HTTPS://" ) ) {
			showError ( "Invalid URL.\n\nOnly the HTTP and HTTPS protocols\nare currently supported." );
			return;
		}
		URL url = null;
		try {
			url = new URL ( urlStr );
		} catch ( Exception e1 ) {
			showError ( "Invalid URL:" + "\n" + e1.getMessage () );
			return;
		}
		int updSel = updateField.getSelectedIndex ();
		int updateInterval = updateIntervalChoiceValues[updSel];
		Color color = colorField.getSelectedColor ();
		Calendar cal = null;
		if ( c == null ) {
			cal = new Calendar ( dataDir, name, url, updateInterval );
		} else {
			cal = c;
		}
		int authType = authChoiceValues[authField.getSelectedIndex ()];
		if ( this.modeField.getSelectedIndex () == 0 ) {
			cal.setCanWrite ( false );
			cal.setSyncBeforePublish ( false );
		} else {
			cal.setCanWrite ( true );
			cal.setSyncBeforePublish ( this.syncBeforePublishField.isSelected () );
		}
		String username = null;
		String password = null;
		if ( authType == Calendar.AUTH_BASIC ) {
			username = usernameField.getText ();
			password = passwordField.getText ();
		}
		File outputFile = new File ( dataDir, cal.getFilename () + ".new" );
		HttpClientStatus result = HttpClient.getRemoteCalendar ( url, username,
		    password, outputFile );
		switch ( result.getStatus () ) {
			case HttpClientStatus.HTTP_STATUS_SUCCESS:
				break;
			case HttpClientStatus.HTTP_STATUS_AUTH_REQUIRED:
				showError ( "Authorization required.\nPlease provide a username\nand password." );
				authField.setSelectedIndex ( 1 );
				return;
			case HttpClientStatus.HTTP_STATUS_NOT_FOUND:
				showError ( "Invalid calendar URL (not found)." + "\n\n"
				    + "Server response: " + result.getMessage () );
				return;
			default:
			case HttpClientStatus.HTTP_STATUS_OTHER_ERROR:
				showError ( "Error downloading calendar." + "\n\n"
				    + "Server response: " + result.getMessage () );
				return;
		}
		// TODO: validate the contents of the file

		// Delete old file and move/rename newly downloaded file to the correct
		// name.
		File file = new File ( dataDir, cal.getFilename () );
		if ( file.exists () )
			file.delete ();
		if ( !outputFile.renameTo ( file ) ) {
			// Rename/move failed
			showError ( "Unable to rename calendar file" );
			return;
		}

		cal.setBackgroundColor ( color );
		cal.setBorderColor ( Utils.getBorderColorForBackground ( color ) );
		cal.setForegroundColor ( Utils.getForegroundColorForBackground ( color ) );
		cal.setLastUpdatedAsNow ();
		cal.setUpdateIntervalSecs ( updateInterval * 3600 );
		if ( this.modeField.getSelectedIndex () == 0 ) {
			cal.setCanWrite ( false );
			cal.setSyncBeforePublish ( false );
		} else {
			cal.setCanWrite ( true );
			cal.setSyncBeforePublish ( this.syncBeforePublishField.isSelected () );
		}
		cal.setAuthType ( authType );
		cal.setAuthUsername ( username );
		cal.setAuthPassword ( password );

		if ( c == null ) {
			// TODO: update status msg on main window
			dataRepository.addCalendar ( dataDir, cal, false );
			// This will call us back with calendarAdded (below)
		} else {
			// TODO: update status msg on main window
			// updating calendar...
			dataRepository.updateCalendar ( dataDir, cal );
		}
		dispose ();
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

}
