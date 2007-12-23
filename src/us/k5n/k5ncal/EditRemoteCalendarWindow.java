package us.k5n.k5ncal;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class EditRemoteCalendarWindow extends JDialog {
	JFrame parent;
	Repository dataRepository;
	Calendar c;
	File dataDir;
	JTextField nameField;
	JTextField urlField;
	JCheckBox authRequired;
	JTextField loginField;
	JTextField passwordField;
	String[] choices = { "12 Hours", "1 Day", "3 Days", "7 Days", "14 Days",
	    "30 Days", "90 Days", "1 Year", "Never" };
	int[] choiceValues = { 12, 24, 24 * 3, 24 * 7, 24 * 14, 24 * 30, 24 * 90,
	    24 * 365, 0 };
	int defChoice = 5;
	JComboBox updateField;
	ColorButton colorField;;

	public EditRemoteCalendarWindow(JFrame parent,
	    final Repository dataRepository, final Calendar c, final File dataDir) {
		super ( parent );
		this.dataRepository = dataRepository;
		this.c = c;
		this.dataDir = dataDir;
		this.parent = parent;
		this.setLocationByPlatform ( true );

		nameField = new JTextField ( 40 );
		urlField = new JTextField ( 40 );
		authRequired = new JCheckBox ( "Requires Authentication" );
		loginField = new JTextField ( 25 );
		passwordField = new JTextField ( 25 );
		updateField = new JComboBox ( choices );
		colorField = new ColorButton ();
		int[] props = { 1, 3 };

		if ( c != null ) {
			for ( int i = 0; i < choiceValues.length; i++ ) {
				if ( c.getUpdateIntervalSecs () == choiceValues[i] * 3600 )
					defChoice = i;
			}
			nameField.setText ( c.getName () );
			// Don't allow changing of URL. Must delete and add new
			urlField.setText ( c.getUrl ().toString () );
			urlField.setEditable ( false );
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
		main.setLayout ( new GridLayout ( 4, 1 ) );
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
		if ( defChoice < choices.length )
			updateField.setSelectedIndex ( defChoice );
		JPanel updateSubPanel = new JPanel ( new BorderLayout () );
		updateSubPanel.add ( updateField, BorderLayout.WEST );
		updatePanel.add ( updateSubPanel );
		main.add ( updatePanel );

		JPanel authPanel = new JPanel ( new ProportionalLayout ( props,
		    ProportionalLayout.HORIZONTAL_LAYOUT ) );
		authPanel.add ( new JLabel ( "Requires Auth: " ) );
		authPanel.add ( authRequired );

		content.add ( main, BorderLayout.CENTER );

		this.pack ();
		this.setVisible ( true );
	}

	private void okHandler () {
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
			// Only allow HTTP
			if ( !urlStr.toUpperCase ().startsWith ( "HTTP://" ) ) {
				showError ( "Invalid URL.\n\nOnly the HTTP protocol\nis supported." );
				return;
			}
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
			Calendar cal = null;
			if ( c == null ) {
				cal = new Calendar ( dataDir, name, url, updateInterval );
			} else {
				cal = c;
			}
			cal.setBackgroundColor ( color );
			cal.setBorderColor ( Utils.getForegroundColorForBackground ( color ) );
			cal.setForegroundColor ( Utils.getForegroundColorForBackground ( color ) );
			cal.setLastUpdatedAsNow ();
			cal.setUpdateIntervalSecs ( updateInterval * 3600 );
			HttpURLConnection urlC = (HttpURLConnection) url.openConnection ();
			int totalRead = 0;
			try {
				InputStream is = urlC.getInputStream ();
				File file = new File ( dataDir, cal.getFilename () );
				OutputStream os = new FileOutputStream ( file );
				DataInputStream dis = new DataInputStream ( new BufferedInputStream (
				    is ) );
				byte[] buf = new byte[4 * 1024]; // 4K buffer
				int bytesRead;
				while ( ( bytesRead = dis.read ( buf ) ) != -1 ) {
					os.write ( buf, 0, bytesRead );
					totalRead += bytesRead;
				}
				os.close ();
				dis.close ();
				urlC.disconnect ();
				// Handle the HTTP status code
				if ( urlC.getResponseCode () == HttpURLConnection.HTTP_NOT_FOUND ) {
					// Handle this one individually since it will be the most common.
					showError ( "Invalid calendar URL (not found).\n\nServer response: "
					    + urlC.getResponseMessage () );
					return;
				} else if ( urlC.getResponseCode () == HttpURLConnection.HTTP_UNAUTHORIZED ) {
					showError ( "Authorization required.\nPlease provide a username\n"
					    + "and password." );
					return;
				} else if ( urlC.getResponseCode () != HttpURLConnection.HTTP_OK ) {
					showError ( "Invalid calendar URL.\n\nServer response: "
					    + urlC.getResponseMessage () );
					return;
				}
			} catch ( IOException e1 ) {
				// Handle the HTTP status code
				if ( urlC.getResponseCode () == HttpURLConnection.HTTP_NOT_FOUND ) {
					// Handle this one individually since it will be the most common.
					showError ( "Invalid calendar URL (not found).\n\nServer response: "
					    + urlC.getResponseMessage () );
				} else if ( urlC.getResponseCode () == HttpURLConnection.HTTP_UNAUTHORIZED ) {
					showError ( "Authorization required.\nPlease provide a username\n"
					    + "and password." );
				} else if ( urlC.getResponseCode () != HttpURLConnection.HTTP_OK ) {
					showError ( "Invalid calendar URL.\n\nServer response: "
					    + urlC.getResponseMessage () );
				} else {
					showError ( "Error downloading calendar:\n" + e1.getMessage () );
					e1.printStackTrace ();
				}
				return;
			}
			// Delete old calendar
			if ( c == null ) {
				// TODO: update status msg on main window
				dataRepository.addCalendar ( dataDir, cal, false );
				// This will call us back with calendarAdded (below)
			} else {
				// TODO: update status msg on main window
				// updating calendar...
				dataRepository.updateCalendar ( dataDir, cal );
			}
		} catch ( FileNotFoundException e1 ) {
			showError ( "Invalid URL (not found)" );
			return;
		} catch ( Exception e2 ) {
			showError ( "Error downloading calendar:\n" + e2.getMessage () );
			e2.printStackTrace ();
			return;
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
