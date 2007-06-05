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
	Vector checkboxes;
	Vector listeners;
	private JPanel innerPanel;
	JPopupMenu menu;
	String[] actionStrings = null;
	JCheckBox itemForMenu = null;

	public CheckBoxList(Vector choices) {
		super ();
		this.setLayout ( new BorderLayout () );
		this.choices = choices;
		this.checkboxes = new Vector ();
		this.listeners = new Vector ();

		JPanel panel = new JPanel ();
		panel.setLayout ( new BorderLayout () );

		innerPanel = new JPanel ();
		this.setChoices ( choices );
		panel.add ( innerPanel, BorderLayout.NORTH );
		JScrollPane scrollPane = new JScrollPane ( panel );
		this.add ( scrollPane, BorderLayout.CENTER );
	}

	public void setRightClickMenu ( String[] labels, String[] actionStrings ) {
		// create right-click menu
		this.actionStrings = actionStrings;
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
		System.out.println ( "menuSelected: " + actionStrings[menuItemInd] );
		String cmd = itemForMenu.getActionCommand ();
		if ( cmd.charAt ( 0 ) == '#' ) {
			int ind = Integer.parseInt ( cmd.substring ( 1 ) );
			Object selectedObject = this.choices.elementAt ( ind );
			for ( int i = 0; i < this.listeners.size (); i++ ) {
				CheckBoxListListener l = (CheckBoxListListener) this.listeners
				    .elementAt ( i );
				l.menuChoice ( choices.elementAt ( ind ), actionStrings[menuItemInd] );
			}
		}
		// Determine which
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
		if ( menu != null && e.isPopupTrigger ()
		    && e.getSource () instanceof JCheckBox ) {
			menu.show ( (Component) e.getSource (), e.getX (), e.getY () );
			itemForMenu = (JCheckBox) e.getSource ();
		}
	}

	public void mouseReleased ( MouseEvent e ) {
		if ( menu != null && e.isPopupTrigger ()
		    && e.getSource () instanceof JCheckBox ) {
			menu.show ( (Component) e.getSource (), e.getX (), e.getY () );
			itemForMenu = (JCheckBox) e.getSource ();
		}
	}

	public void mouseEntered ( MouseEvent e ) {
	}

	public void mouseClicked ( MouseEvent e ) {
	}

	public void mouseExited ( MouseEvent e ) {
	}

	public JCheckBox getCheckBoxAt ( int ind ) {
		return (JCheckBox) this.checkboxes.elementAt ( ind );
	}

	public Vector getSelectedItems () {
		Vector ret = new Vector ();
		for ( int i = 0; i < choices.size (); i++ ) {
			JCheckBox checkbox = (JCheckBox) checkboxes.elementAt ( i );
			if ( checkbox.isSelected () ) {
				ret.addElement ( choices.elementAt ( i ) );
			}
		}
		return ret;
	}

	public void addCheckBoxListListener ( CheckBoxListListener l ) {
		this.listeners.addElement ( l );
	}

	public void actionPerformed ( ActionEvent event ) {
		Object o = event.getSource ();
		if ( o instanceof JCheckBox ) {
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
