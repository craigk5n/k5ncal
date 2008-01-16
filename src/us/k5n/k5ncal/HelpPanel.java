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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

/**
 * The HelpPalenl displays application help for the user.
 * 
 * @author Craig Knudsen, craig@k5n.us
 * @version $Id$
 */
public class HelpPanel extends JPanel implements HyperlinkListener,
    ActionListener {
	// private JTextField urlField;
	private JEditorPane htmlPane;
	private String initialURL;

	public HelpPanel(URL url) {
		super ();
		this.setLayout ( new BorderLayout () );
		this.initialURL = url.toString ();
		// addWindowListener(new ExitListener());
		// WindowUtilities.setNativeLookAndFeel();
		try {
			htmlPane = new JEditorPane ( initialURL );
			htmlPane.setEditable ( false );
			htmlPane.addHyperlinkListener ( this );
			JScrollPane scrollPane = new MyScrollPane ( htmlPane );
			add ( scrollPane, BorderLayout.CENTER );
		} catch ( IOException ioe ) {
			warnUser ( "Can't build HTML pane for URL" + ":\n\n" + initialURL
			    + "\n\n" + ioe );
		}
	}

	public void actionPerformed ( ActionEvent event ) {
		String url;
		// if (event.getSource() == urlField)
		// url = urlField.getText();
		// else // Clicked "home" button instead of entering URL
		url = initialURL;
		try {
			htmlPane.setPage ( new URL ( url ) );
			// urlField.setText(url);
		} catch ( IOException ioe ) {
			warnUser ( "Can't follow link to URL" + ":\n\n" + url + "\n\n" + ioe );
		}
	}

	public void hyperlinkUpdate ( HyperlinkEvent event ) {
		if ( event.getEventType () == HyperlinkEvent.EventType.ACTIVATED ) {
			try {
				htmlPane.setPage ( event.getURL () );
				// urlField.setText(event.getURL().toExternalForm());
			} catch ( IOException ioe ) {
				warnUser ( "Can't follow link to URL" + ":\n\n"
				    + event.getURL ().toExternalForm () + "\n\n" + ioe );
			}
		}
	}

	private void warnUser ( String message ) {
		JOptionPane.showMessageDialog ( this, message, "Error",
		    JOptionPane.ERROR_MESSAGE );
	}
}
