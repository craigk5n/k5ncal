package us.k5n.k5ncal;

import java.awt.Component;

import javax.swing.JScrollPane;

public class MyScrollPane extends JScrollPane {

	public MyScrollPane(Component comp) {
		super ( comp );
		if ( System.getProperty ( "mrj.version" ) != null )
			this.setVerticalScrollBarPolicy ( JScrollPane.VERTICAL_SCROLLBAR_ALWAYS );
	}

}
