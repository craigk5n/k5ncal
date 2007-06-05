package us.k5n.ui.calendar;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.CellRendererPane;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JToolTip;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicToolTipUI;

/**
 * Multiline ToolTip. The standard JToolTip Swing component can only display one
 * line of text in a tool tip. This class allows tooltips to be more than one
 * line by using a JLabel to display the tooltip text. If the tooltip text
 * starts with "<html>", the text will be rendered as HTML (like all JLabel
 * objects).
 * 
 * @version $Id$
 * @author Craig Knudsen, craig@k5n.us
 */
public class JMToolTip extends JToolTip {
	private static final long serialVersionUID = 1000L;
	protected int columns = 0;
	protected int fixedwidth = 0;
	String tipText;
	JComponent component;

	public JMToolTip() {
		updateUI ();
	}

	public void updateUI () {
		setUI ( MultiLineToolTipUI.createUI ( this ) );
	}

	public void setColumns ( int columns ) {
		this.columns = columns;
		this.fixedwidth = 0;
	}

	public int getColumns () {
		return columns;
	}

	public void setFixedWidth ( int width ) {
		this.fixedwidth = width;
		this.columns = 0;
	}

	public int getFixedWidth () {
		return fixedwidth;
	}

}

class MultiLineToolTipUI extends BasicToolTipUI {
	static MultiLineToolTipUI sharedInstance = new MultiLineToolTipUI ();
	static JToolTip tip;
	Font smallFont;

	protected CellRendererPane rendererPane;

	private static JLabel htmlDisplay;

	public static ComponentUI createUI ( JComponent c ) {
		return sharedInstance;
	}

	public MultiLineToolTipUI() {
		super ();
	}

	public void installUI ( JComponent c ) {
		super.installUI ( c );
		tip = (JToolTip) c;
		rendererPane = new CellRendererPane ();
		c.add ( rendererPane );
	}

	public void uninstallUI ( JComponent c ) {
		super.uninstallUI ( c );
		c.remove ( rendererPane );
		rendererPane = null;
	}

	public void paint ( Graphics g, JComponent c ) {
		Dimension size = c.getSize ();
		htmlDisplay.setBackground ( c.getBackground () );
		htmlDisplay.setBackground ( Color.WHITE );
		g.setColor ( Color.BLACK );
		rendererPane.paintComponent ( g, htmlDisplay, c, 1, 1, size.width - 1,
		    size.height - 1, true );
	}

	public Dimension getPreferredSize ( JComponent c ) {
		String tipText = ( (JToolTip) c ).getTipText ();
		if ( tipText == null )
			return new Dimension ( 0, 0 );
		htmlDisplay = new JLabel ();
		htmlDisplay.setText ( tipText );
		rendererPane.removeAll ();
		rendererPane.add ( htmlDisplay );
		// textArea.setWrapStyleWord ( true );
		int width = ( (JMToolTip) c ).getFixedWidth ();
		int columns = ( (JMToolTip) c ).getColumns ();

		if ( columns > 0 ) {
			htmlDisplay.setSize ( htmlDisplay.getPreferredSize () );
		} else if ( width > 0 ) {
			Dimension d = htmlDisplay.getPreferredSize ();
			d.width = width;
			d.height++;
			htmlDisplay.setSize ( d );
		}

		Dimension dim = htmlDisplay.getPreferredSize ();

		dim.height += 1;
		dim.width += 1;
		return dim;
	}

	public Dimension getMinimumSize ( JComponent c ) {
		return getPreferredSize ( c );
	}

	public Dimension getMaximumSize ( JComponent c ) {
		return getPreferredSize ( c );
	}
}
