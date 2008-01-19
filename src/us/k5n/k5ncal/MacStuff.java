package us.k5n.k5ncal;

import javax.swing.SwingUtilities;

import com.apple.mrj.MRJAboutHandler;
import com.apple.mrj.MRJApplicationUtils;
import com.apple.mrj.MRJPrefsHandler;
import com.apple.mrj.MRJQuitHandler;

/**
 * This class provides makes some custom Mac OS X calls. The classes called by
 * this class are only available on Mac OS X, so you shouldn't call this class
 * on a Windows machine, or you'll get a class not found exception. You can test
 * to see if you are running on a Mac with:
 * 
 * boolean isMac = System.getProperty ( "mrj.version" ) != null;
 * 
 * @author Craig Knudsen, craig@k5n.us
 * @version $Id$
 */
public class MacStuff implements MRJQuitHandler, MRJPrefsHandler,
    MRJAboutHandler {
	Main app;

	public MacStuff(Main theProgram) {
		app = theProgram;
		System.setProperty ( "com.apple.macos.useScreenMenubar", "true" );
		System.setProperty ( "apple.laf.useScreenMenuBar", "true" );
		System.setProperty ( "com.apple.mrj.application.apple.menu.about.name",
		    "k5nCal" );
		System.setProperty ( "com.apple.mrj.application.growbox.intrudes", "false" );
		System.setProperty ( "apple.awt.antialiasing", "true" );
		MRJApplicationUtils.registerAboutHandler ( this );
		MRJApplicationUtils.registerPrefsHandler ( this );
		MRJApplicationUtils.registerQuitHandler ( this );
	}

	public void handleAbout () {
		app.showAbout ();
	}

	public void handlePrefs () {
		app.showPreferences ();
	}

	public void handleQuit () {
		SwingUtilities.invokeLater ( new Runnable () {
			public void run () {
				app.quit ();
			}
		} );
		throw new IllegalStateException ( "Let the quit handler do it" );
	}
}
