package us.k5n.k5ncal;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;

public class ColorButton extends JButton {
	Color selColor = null;

	public ColorButton() {
		super ();
		final ColorButton b = this;
		this.addActionListener ( new ActionListener () {
			public void actionPerformed ( ActionEvent event ) {
				Color newColor = JColorChooser
				    .showDialog ( b, "Choose Color", selColor );
				selColor = newColor;
				if ( newColor != null ) {
					ImageIcon icon = Utils.buildColoredIcon ( newColor, Utils
					    .getForegroundColorForBackground ( newColor ) );
					b.setIcon ( icon );
				}
			}
		} );
	}

	public void setSelectedColor ( Color c ) {
		ImageIcon icon = Utils.buildColoredIcon ( c, Utils
		    .getForegroundColorForBackground ( c ) );
		this.setIcon ( icon );
	}

	public Color getSelectedColor () {
		return selColor;
	}

	public Dimension preferredSize () {
		return new Dimension ( 30, 30 );
	}

}
