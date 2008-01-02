package us.k5n.k5ncal;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.colorchooser.ColorSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ColorButton extends JButton {
	Color selColor = null;
	JColorChooser chooser = null;
	JDialog otherModal = null;

	public ColorButton() {
		super ();
		this.addActionListener ( new ActionListener () {
			public void actionPerformed ( ActionEvent event ) {
				// ColorDialog dialog = new ColorDialog ( ColorButton.this,
				// "Color Chooser" );
				// dialog.setVisible ( true );
				Color newColor = JColorChooser.showDialog ( ColorButton.this,
				    "Choose Color", selColor );
				selColor = newColor;
				if ( newColor != null ) {
					ImageIcon icon = Utils.buildColoredIcon ( newColor, Utils
					    .getBorderColorForBackground ( newColor ) );
					ColorButton.this.setIcon ( icon );
				}
			}
		} );
	}

	public void setSelectedColor ( Color c ) {
		this.selColor = c;
		ImageIcon icon = Utils.buildColoredIcon ( c, Utils
		    .getBorderColorForBackground ( c ) );
		this.setIcon ( icon );
	}

	public Color getSelectedColor () {
		return selColor;
	}

	public Dimension preferredSize () {
		return new Dimension ( 30, 30 );
	}

}

class ColorDialog extends JDialog {
	JColorChooser chooser;

	public Color getSelectedColor () {
		return chooser.getColor ();
	}

	public static Color showDialog ( JComponent component, String title ) {
		ColorDialog d = new ColorDialog ( component, title );
		d.setVisible ( true );
		makeModal ( d );
		Color c = d.getSelectedColor ();
		d.dispose ();
		return c;
	}

	public ColorDialog(JComponent component, String title) {
		super ();
		// super ( JOptionPane.getFrameForComponent ( component ) );
		setTitle ( title );
		// this.setModal ( false );

		chooser = new JColorChooser ();

		this.getContentPane ().setLayout ( new BorderLayout () );
		JPanel buttonPanel = new JPanel ( new FlowLayout () );
		JButton okButton = new JButton ( "Ok" );
		buttonPanel.add ( okButton );
		JButton cancelButton = new JButton ( "Cancel" );
		buttonPanel.add ( cancelButton );
		this.getContentPane ().add ( buttonPanel, BorderLayout.SOUTH );
		okButton.addActionListener ( new ActionListener () {
			public void actionPerformed ( ActionEvent ae ) {
				ColorDialog.this.setVisible ( false );
			}
		} );
		cancelButton.addActionListener ( new ActionListener () {
			public void actionPerformed ( ActionEvent ae ) {
				ColorDialog.this.setVisible ( false );
			}
		} );
		JPanel middlePanel = new JPanel ( new BorderLayout () );
		JPanel samplePanel = new JPanel ( new FlowLayout () );
		MyPreviewPanel previewPanel = new MyPreviewPanel ( chooser );
		samplePanel.setBorder ( BorderFactory.createTitledBorder ( "Preview" ) );
		samplePanel.add ( previewPanel );
		chooser.setPreviewPanel ( previewPanel );
		middlePanel.add ( samplePanel, BorderLayout.SOUTH );
		middlePanel.add ( chooser, BorderLayout.CENTER );
		this.getContentPane ().add ( middlePanel, BorderLayout.CENTER );
		this.pack ();
		this.show ();
		makeModal ( this );
	}

	static void makeModal ( JDialog dialog ) {
		try {
			synchronized ( dialog ) {
				while ( dialog.isVisible () )
					dialog.wait ();
			}
		} catch ( InterruptedException e ) {
			// TODO: Should this be handled?
		}
	}
}

// This preview panel simply displays the currently selected color.
class MyPreviewPanel extends JComponent {
	// The currently selected color
	Color curColor;

	public MyPreviewPanel(JColorChooser chooser) {
		// Initialize the currently selected color
		curColor = chooser.getColor ();

		// Add listener on model to detect changes to selected color
		ColorSelectionModel model = chooser.getSelectionModel ();
		model.addChangeListener ( new ChangeListener () {
			public void stateChanged ( ChangeEvent evt ) {
				ColorSelectionModel model = (ColorSelectionModel) evt.getSource ();

				// Get the new color value
				curColor = model.getSelectedColor ();
			}
		} );

		// Set a preferred size
		setPreferredSize ( new Dimension ( 200, 30 ) );
	}

	// Paint current color
	public void paint ( Graphics g ) {
		System.out.println ( "Paint!" );
		int CELL_MARGIN = 2;
		int x = 0;
		int w = 200;
		int startY = 0;
		Color backgroundColor = curColor;
		Color borderColor = Utils.getBorderColorForBackground ( curColor );
		Color foregroundColor = Utils.getForegroundColorForBackground ( curColor );

		FontMetrics fm = g.getFontMetrics ();
		Rectangle r = new Rectangle ( x + CELL_MARGIN, startY, w
		    - ( 2 * CELL_MARGIN ), fm.getHeight () );
		// The following code was copied from CalendarPanel.java in the
		// java calendar tools package.
		g.setColor ( backgroundColor );
		int arclen = r.height;
		g.fillRoundRect ( r.x, r.y, r.width, r.height, arclen, arclen );
		g.setColor ( borderColor );
		g.drawRoundRect ( r.x, r.y, r.width, r.height, arclen, arclen );
		g.setClip ( r.x + 1, r.y + 1, r.width - 2, r.height - 3 );
		g.setColor ( foregroundColor );
		String text = "Color Sample";
		g.drawString ( text, r.x + 3, r.y + g.getFontMetrics ().getAscent () );
	}
}
