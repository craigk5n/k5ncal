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

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;

/**
 * Create a dialog window that displays info about 3rd party tools included in
 * this application.
 * 
 * @author Craig Knudsen, craig@k5n.us
 * @version $Id$
 */
public class ThirdPartyDialog extends JDialog {
	JFrame parent;
	static final String[] components = { "k5n Java Calendar Tools",
	    "http://www.k5n.us/javacaltools.php", "GNU GPL",
	    "http://www.fsf.org/licensing/licenses/gpl.html",

	    "Joda Time", "http://joda-time.sourceforge.net/index.html",
	    "Apache License 2.0", "http://joda-time.sourceforge.net/license.html",

	    "k5n AccordionPane", "http://www.k5n.us/k5naccordion.php", "GNU GPL",
	    "http://www.fsf.org/licensing/licenses/gpl.html",

	    "Google RFC2445", "http://code.google.com/p/google-rfc-2445/",
	    "Apache License 2.0", "http://www.apache.org/licenses/",

	    "Java CSV Library", "http://sourceforge.net/projects/javacsv/",
	    "GNU LGPL", "http://www.gnu.org/licenses/lgpl.html",

	    "JCalendar", "http://www.toedter.com/en/jcalendar/index.html",
	    "GNU LGPL", "http://www.toedter.com/en/jcalendar/license.html",

	    "BrowserLauncher2", "http://browserlaunch2.sourceforge.net/", "GNU LGPL",
	    "http://www.gnu.org/licenses/lgpl.html", };

	/**
	 * Create a new window.
	 * 
	 * @param parent
	 * @param repo
	 * @param date
	 * @param selectedCalendar
	 */
	public ThirdPartyDialog(JFrame parent) {
		super ( parent );

		super.setTitle ( "3rd Party Components in k5nCal" );
		super.setSize ( 400, 400 );
		// Center window
		super.setLocationRelativeTo ( null );
		setDefaultCloseOperation ( JDialog.DISPOSE_ON_CLOSE );

		createWindow ();
		setVisible ( true );
	}

	private void createWindow () {
		this.getContentPane ().setLayout ( new BorderLayout () );

		JPanel topPanel = new JPanel ( new BorderLayout () );
		topPanel.add ( new JLabel (
		    "The following components are included in k5nCal" ), BorderLayout.WEST );
		this.getContentPane ().add ( topPanel, BorderLayout.NORTH );

		JPanel buttonPanel = new JPanel ();
		buttonPanel.setLayout ( new FlowLayout () );
		JButton closeButton = new JButton ( "Close" );
		closeButton.addActionListener ( new ActionListener () {
			public void actionPerformed ( ActionEvent event ) {
				dispose ();
			}
		} );
		buttonPanel.add ( closeButton );

		getContentPane ().add ( buttonPanel, BorderLayout.SOUTH );

		int numEntries = components.length / 4;
		System.out.println ( "Num Entries: " + numEntries );
		JPanel panel = new JPanel ( new GridLayout ( numEntries, 1 ) );

		for ( int i = 0; i < components.length; i += 4 ) {
			String name = components[i];
			String url = components[i + 1];
			String license = components[i + 2];
			String licenseUrl = components[i + 3];
			JPanel p = new JPanel ( new GridLayout ( 4, 1 ) );
			p.setBorder ( BorderFactory.createBevelBorder ( BevelBorder.RAISED ) );
			p.add ( new JLabel ( name ) );
			p.add ( new JLabelWithHyperlink ( url ) );
			p.add ( new JLabel ( "License: " + license ) );
			p.add ( new JLabelWithHyperlink ( licenseUrl ) );
			panel.add ( p );
		}

		JScrollPane sp = new MyScrollPane ( panel );
		sp.setBorder ( BorderFactory.createEmptyBorder ( 5, 5, 5, 5 ) );
		this.getContentPane ().add ( sp, BorderLayout.CENTER );
	}
}
