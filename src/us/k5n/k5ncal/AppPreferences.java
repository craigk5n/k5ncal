/*
 * Copyright (C) 2005-2007 Craig Knudsen
 *
 * k5nJournal is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 * 
 * A copy of the GNU Lesser General Public License can be found at www.gnu.org. 
 * To receive a hard copy, you can write to:
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307 USA.
 */

package us.k5n.k5ncal;

/**
 * A convenience class for using the java Preferences class. All application
 * preferences will be stored using the java.util.prefs.Preferences class. This
 * class abstracts out the details of where the info saved (Windows registry,
 * etc.)
 * 
 * @version $Id$
 * @author Craig Knudsen, craig
 * @k5n.us
 */
public class AppPreferences {
	java.util.prefs.Preferences prefs = null;
	static final String MAIN_WINDOW_WIDTH = "MainWindow.width";
	static final String MAIN_WINDOW_HEIGHT = "MainWindow.height";
	static final String MAIN_WINDOW_X = "MainWindow.x";
	static final String MAIN_WINDOW_Y = "MainWindow.y";
	static final String MAIN_WINDOW_VERTICAL_SPLIT_POSITION = "MainWindow.vSplitPanePosition";
	static final String MAIN_WINDOW_HORIZONTAL_SPLIT_POSITION = "MainWindow.hSplitPanePosition";

	static final String EDIT_WINDOW_WIDTH = "EditWindow.width";
	static final String EDIT_WINDOW_HEIGHT = "EditWindow.height";
	static final String EDIT_WINDOW_X = "EditWindow.x";
	static final String EDIT_WINDOW_Y = "EditWindow.y";

	private static AppPreferences instance = null;

	public AppPreferences() {
		this.prefs = java.util.prefs.Preferences.userNodeForPackage ( this
		    .getClass () );
	}

	public static AppPreferences getInstance () {
		if ( instance == null )
			instance = new AppPreferences ();
		return instance;
	}

	/**
	 * Get height of main window
	 * 
	 * @return
	 */
	public int getMainWindowHeight () {
		return prefs.getInt ( MAIN_WINDOW_HEIGHT, 600 );
	}

	/**
	 * Set height of main window
	 * 
	 * @param mainWindowHeight
	 *          height of main window (pixels)
	 */
	public void setMainWindowHeight ( int mainWindowHeight ) {
		prefs.putInt ( MAIN_WINDOW_HEIGHT, mainWindowHeight );
	}

	/**
	 * Get divider location for horizontally divided JSplitPane. This value is in
	 * pixels.
	 * 
	 * @return
	 */
	public int getMainWindowHorizontalSplitPosition () {
		return prefs.getInt ( MAIN_WINDOW_HORIZONTAL_SPLIT_POSITION, 185 );
	}

	/**
	 * Set divider location for horizontally divided JSplitPane. This value is in
	 * pixels.
	 * 
	 * @param mainWindowHorizontalSplitPosition
	 *          The new divider location (in pixels)
	 * @return
	 */
	public void setMainWindowHorizontalSplitPosition (
	    int mainWindowHorizontalSplitPosition ) {
		prefs.putInt ( MAIN_WINDOW_HORIZONTAL_SPLIT_POSITION,
		    mainWindowHorizontalSplitPosition );
	}

	/**
	 * Get divider location for vertically divided JSplitPane. This value is in
	 * pixels.
	 * 
	 * @return
	 */
	public int getMainWindowVerticalSplitPosition () {
		return prefs.getInt ( MAIN_WINDOW_VERTICAL_SPLIT_POSITION, 200 );
	}

	/**
	 * Set divider location for vertically divided JSplitPane. This value is in
	 * pixels.
	 * 
	 * @param mainWindowVerticalSplitPosition
	 *          The new divider location (in pixels)
	 * @return
	 */
	public void setMainWindowVerticalSplitPosition (
	    int mainWindowVerticalSplitPosition ) {
		prefs.putInt ( MAIN_WINDOW_VERTICAL_SPLIT_POSITION,
		    mainWindowVerticalSplitPosition );
	}

	/**
	 * Get main window width
	 * 
	 * @return
	 */
	public int getMainWindowWidth () {
		return prefs.getInt ( MAIN_WINDOW_WIDTH, 800 );
	}

	/**
	 * Set main window width
	 * 
	 * @param mainWindowWidth
	 *          width of main window (in pixels)
	 */
	public void setMainWindowWidth ( int mainWindowWidth ) {
		prefs.putInt ( MAIN_WINDOW_WIDTH, mainWindowWidth );
	}

	/**
	 * Get the main window X position
	 * 
	 * @return
	 */
	public int getMainWindowX () {
		return prefs.getInt ( MAIN_WINDOW_X, 15 );
	}

	/**
	 * Set the main window X position
	 * 
	 * @param mainWindowX
	 *          The X position of the main window
	 */
	public void setMainWindowX ( int mainWindowX ) {
		prefs.putInt ( MAIN_WINDOW_X, mainWindowX );
	}

	/**
	 * Get the main window Y position
	 * 
	 * @return
	 */
	public int getMainWindowY () {
		return prefs.getInt ( MAIN_WINDOW_Y, 15 );
	}

	/**
	 * Set the main window Y position
	 * 
	 * @param mainWindowY
	 *          The main window Y position
	 */
	public void setMainWindowY ( int mainWindowY ) {
		prefs.putInt ( MAIN_WINDOW_Y, mainWindowY );
	}

	/**
	 * Get edit window width
	 * 
	 * @return
	 */
	public int getEditWindowWidth () {
		return prefs.getInt ( EDIT_WINDOW_WIDTH, 600 );
	}

	/**
	 * Set edit window width
	 * 
	 * @param editWindowWidth
	 *          width of edit window (in pixels)
	 */
	public void setEditWindowWidth ( int editWindowWidth ) {
		prefs.putInt ( EDIT_WINDOW_WIDTH, editWindowWidth );
	}

	/**
	 * Get edit window height
	 * 
	 * @return
	 */
	public int getEditWindowHeight () {
		return prefs.getInt ( EDIT_WINDOW_HEIGHT, 600 );
	}

	/**
	 * Set edit window width
	 * 
	 * @param editWindowWidth
	 *          width of edit window (in pixels)
	 */
	public void setEditWindowHeight ( int editWindowHeight ) {
		prefs.putInt ( EDIT_WINDOW_HEIGHT, editWindowHeight );
	}

	/**
	 * Get the edit window X position
	 * 
	 * @return
	 */
	public int getEditWindowX () {
		return prefs.getInt ( EDIT_WINDOW_X, 15 );
	}

	/**
	 * Set the edit window X position
	 * 
	 * @param editWindowX
	 *          The X position of the main window
	 */
	public void setEditWindowX ( int editWindowX ) {
		prefs.putInt ( EDIT_WINDOW_X, editWindowX );
	}

	/**
	 * Get the edit window y position
	 * 
	 * @return
	 */
	public int getEditWindowY () {
		return prefs.getInt ( EDIT_WINDOW_Y, 15 );
	}

	/**
	 * Set the edit window Y position
	 * 
	 * @param editWindowY
	 *          The Y position of the main window
	 */
	public void setEditWindowY ( int editWindowY ) {
		prefs.putInt ( EDIT_WINDOW_Y, editWindowY );
	}

}
