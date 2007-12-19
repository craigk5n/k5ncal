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
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;

/**
 * The CheckBoxList class presents a list of JCheckBox in a JScrollPane for the
 * user to select. It functions much like a JList with multiple selection
 * enabled, but without requiring users to use CTRL or SHIFT to make multiple
 * selections.
 * 
 * @author Craig Knudsen
 * @version $Id$
 * 
 */
public class CheckBoxList extends JPanel implements ActionListener,
    MouseListener {
	Vector choices;
	Vector<JCheckBox> checkboxes;
	Vector<CheckBoxListListener> listeners;
	private JPanel innerPanel;
	JPopupMenu menu;
	boolean isPopupTrigger = false;
	JCheckBox itemForMenu = null;
	private String[] menuLabels = null;

	public CheckBoxList(Vector choices) {
		super ();
		this.setLayout ( new BorderLayout () );
		this.choices = choices;
		this.checkboxes = new Vector<JCheckBox> ();
		this.listeners = new Vector<CheckBoxListListener> ();

		JPanel panel = new JPanel ();
		panel.setLayout ( new BorderLayout () );

		innerPanel = new JPanel ();
		this.setChoices ( choices );
		panel.add ( innerPanel, BorderLayout.NORTH );
		JScrollPane scrollPane = new MyScrollPane ( panel );
		this.add ( scrollPane, BorderLayout.CENTER );
	}

	private void setRightClickMenu ( String[] labels ) {
		// create right-click menu
		this.menuLabels = labels;
		menu = new JPopupMenu ();
		for ( int i = 0; i < labels.length; i++ ) {
			JMenuItem item = new JMenuItem ( labels[i] );
			final int ind = i;
			item.addActionListener ( new ActionListener () {
				public void actionPerformed ( ActionEvent event ) {
					menuSelected ( ind );
				}
			} );
			menu.add ( item );
		}
	}

	public void menuSelected ( int menuItemInd ) {
		// System.out.println ( "menuSelected: " + menuLabels[menuItemInd] );
		String cmd = itemForMenu.getActionCommand ();
		if ( cmd.charAt ( 0 ) == '#' ) {
			int ind = Integer.parseInt ( cmd.substring ( 1 ) );
			for ( int i = 0; i < this.listeners.size (); i++ ) {
				CheckBoxListListener l = (CheckBoxListListener) this.listeners
				    .elementAt ( i );
				l.menuChoice ( choices.elementAt ( ind ), menuLabels[menuItemInd] );
			}
		}
	}

	public void setChoices ( Vector choices ) {
		this.choices = choices;
		this.checkboxes.removeAllElements ();
		innerPanel.removeAll ();
		innerPanel.setLayout ( new GridLayout ( choices.size (), 1 ) );
		for ( int i = 0; i < choices.size (); i++ ) {
			Object o = choices.elementAt ( i );
			JCheckBox checkbox = new JCheckBox ( o.toString () );
			// TODO: save status from last run
			checkbox.setSelected ( true );
			checkbox.addActionListener ( this );
			checkbox.setActionCommand ( "#" + i );
			checkbox.addMouseListener ( this );
			innerPanel.add ( checkbox );
			this.checkboxes.addElement ( checkbox );
		}
		innerPanel.repaint ();
	}

	public void mousePressed ( MouseEvent e ) {
		if ( e.getSource () instanceof JCheckBox ) {
			// We need to check isPopupTrigger here and in mouseReleased. It wil be
			// true here for Mac (false for Windows). In mouseReleased, it will be
			// true for Windows and false for Mac.
			this.isPopupTrigger = e.isPopupTrigger ();
			// Update menu for this choice
			Object choice = null;
			for ( int i = 0; i < choices.size () && choice == null; i++ ) {
				JCheckBox checkbox = checkboxes.elementAt ( i );
				if ( checkbox.equals ( e.getSource () ) ) {
					// found it
					choice = choices.elementAt ( i );
				}
			}
			if ( choice == null ) {
				System.err.println ( "Could not find choice for " + e.getSource () );
				return;
			}
			Vector<String> menuLabels = new Vector<String> ();
			for ( int i = 0; i < this.listeners.size (); i++ ) {
				CheckBoxListListener l = this.listeners.elementAt ( i );
				menuLabels.addAll ( l.getMenuChoicesForObject ( choice ) );
			}
			String[] menuChoices = new String[menuLabels.size ()];
			for ( int i = 0; i < menuLabels.size (); i++ ) {
				menuChoices[i] = (String) menuLabels.elementAt ( i );
			}
			setRightClickMenu ( menuChoices );
			itemForMenu = (JCheckBox) e.getSource ();
		}
	}

	/**
	 * Check to see if this event should create a popup menu. In Windows,
	 * isPopupTrigger gets set to true here, while it gets set to true in
	 * mousePressed on Mac OS X (just to keep us programmers on our toes).
	 */
	public void mouseReleased ( MouseEvent e ) {
		if ( e.isPopupTrigger () )
			this.isPopupTrigger = true;
		if ( menu != null && this.isPopupTrigger
		    && e.getSource () instanceof JCheckBox ) {
			menu.show ( (Component) e.getSource (), e.getX (), e.getY () );
			itemForMenu = (JCheckBox) e.getSource ();
		}
		this.isPopupTrigger = false;
	}

	public void mouseEntered ( MouseEvent e ) {
	}

	public void mouseClicked ( MouseEvent e ) {
	}

	public void mouseExited ( MouseEvent e ) {
	}

	public JCheckBox getCheckBoxAt ( int ind ) {
		return this.checkboxes.elementAt ( ind );
	}

	public Vector getSelectedItems () {
		Vector ret = new Vector ();
		for ( int i = 0; i < choices.size (); i++ ) {
			JCheckBox checkbox = checkboxes.elementAt ( i );
			if ( checkbox.isSelected () ) {
				ret.addElement ( choices.elementAt ( i ) );
			}
		}
		return ret;
	}

	public void addCheckBoxListListener ( CheckBoxListListener l ) {
		this.listeners.addElement ( l );
	}

	/**
	 * Select/unselect an item, but only if they clicked on the icon rather than
	 * the text.
	 */
	public void actionPerformed ( ActionEvent event ) {
		Object o = event.getSource ();
		System.out.println ( "isPopupTrigger = " + this.isPopupTrigger );
		if ( o instanceof JCheckBox && this.isPopupTrigger == false ) {
			JCheckBox checkbox = (JCheckBox) o;
			String cmd = event.getActionCommand ();
			if ( cmd.charAt ( 0 ) == '#' ) {
				int ind = Integer.parseInt ( cmd.substring ( 1 ) );
				Object selectedObject = this.choices.elementAt ( ind );
				for ( int i = 0; i < this.listeners.size (); i++ ) {
					CheckBoxListListener l = (CheckBoxListListener) this.listeners
					    .elementAt ( i );
					if ( checkbox.isSelected () )
						l.itemSelected ( selectedObject );
					else
						l.itemUnselected ( selectedObject );
				}
			}
		}
	}
}
