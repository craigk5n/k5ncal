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

package us.k5n.k5ncal.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * 
 * @author Craig Knudsen, craig@k5n.us
 * @version $Id$
 */
public class FileUtils {
	public static void copyFile ( File in, File out ) throws Exception {
		FileInputStream fis = new FileInputStream ( in );
		FileOutputStream fos = new FileOutputStream ( out );
		try {
			byte[] buf = new byte[1024];
			int i = 0;
			while ( ( i = fis.read ( buf ) ) != -1 ) {
				fos.write ( buf, 0, i );
			}
		} catch ( Exception e ) {
			throw e;
		} finally {
			if ( fis != null )
				fis.close ();
			if ( fos != null )
				fos.close ();
		}
	}

	public static void main ( String args[] ) throws Exception {
		FileUtils.copyFile ( new File ( args[0] ), new File ( args[1] ) );
	}
}
