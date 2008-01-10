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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;

/**
 * Define a simple class for handling HTTP downloads. We may move to the more
 * robust Apache HttpClient class down the road. For now, this much leaner class
 * will serve our purposes.
 * 
 * @author Craig Knudsen, craig@k5n.us
 * @version $Id$
 * 
 */
public class HttpClient {

	public static HttpClientStatus getRemoteCalendar ( URL url,
	    final String username, final String password, File outputFile ) {
		if ( username != null && password != null ) {
			Authenticator.setDefault ( new Authenticator () {
				protected PasswordAuthentication getPasswordAuthentication () {
					return new PasswordAuthentication ( username, password.toCharArray () );
				}
			} );
		} else {
			Authenticator.setDefault ( new Authenticator () {
				protected PasswordAuthentication getPasswordAuthentication () {
					return null;
				}
			} );
		}
		HttpURLConnection urlC = null;
		int totalRead = 0;
		try {
			urlC = (HttpURLConnection) url.openConnection ();
			InputStream is = urlC.getInputStream ();
			OutputStream os = new FileOutputStream ( outputFile );
			DataInputStream dis = new DataInputStream ( new BufferedInputStream ( is ) );
			byte[] buf = new byte[4 * 1024]; // 4K buffer
			int bytesRead;
			while ( ( bytesRead = dis.read ( buf ) ) != -1 ) {
				os.write ( buf, 0, bytesRead );
				totalRead += bytesRead;
			}
			os.close ();
			dis.close ();
			urlC.disconnect ();
			// Handle the HTTP status code
			if ( urlC.getResponseCode () == HttpURLConnection.HTTP_NOT_FOUND ) {
				return new HttpClientStatus ( HttpClientStatus.HTTP_STATUS_NOT_FOUND,
				    "File not found on server" );
			} else if ( urlC.getResponseCode () == HttpURLConnection.HTTP_UNAUTHORIZED ) {
				return new HttpClientStatus (
				    HttpClientStatus.HTTP_STATUS_AUTH_REQUIRED, "Authorizaton required" );
			} else if ( urlC.getResponseCode () != HttpURLConnection.HTTP_OK ) {
				return new HttpClientStatus ( HttpClientStatus.HTTP_STATUS_OTHER_ERROR,
				    "HTTP Error " + urlC.getResponseCode () + ": "
				        + urlC.getResponseMessage () );
			}
		} catch ( IOException e1 ) {
			// Checking the response code can generate another IOException
			try {
				// Handle the HTTP status code
				if ( urlC.getResponseCode () == HttpURLConnection.HTTP_NOT_FOUND ) {
					// Handle this one individually since it will be the most common.
					return new HttpClientStatus ( HttpClientStatus.HTTP_STATUS_NOT_FOUND,
					    "File not found on server" );
				} else if ( urlC.getResponseCode () == HttpURLConnection.HTTP_UNAUTHORIZED ) {
					return new HttpClientStatus (
					    HttpClientStatus.HTTP_STATUS_AUTH_REQUIRED,
					    "Authorizaton required" );
				} else if ( urlC.getResponseCode () != HttpURLConnection.HTTP_OK ) {
					return new HttpClientStatus (
					    HttpClientStatus.HTTP_STATUS_OTHER_ERROR, "HTTP Error "
					        + urlC.getResponseCode () + ": " + urlC.getResponseMessage () );
				} else {
					return new HttpClientStatus (
					    HttpClientStatus.HTTP_STATUS_OTHER_ERROR, "HTTP I/O Exception:",
					    e1 );
				}
			} catch ( IOException e2 ) {
				// Print the stack trace on this one since it happened while we were
				// handling another exception...
				e2.printStackTrace ();
				return new HttpClientStatus ( HttpClientStatus.HTTP_STATUS_OTHER_ERROR,
				    "HTTP I/O Exception:", e1 );
			}
		}

		// Success
		return new HttpClientStatus ( HttpClientStatus.HTTP_STATUS_SUCCESS,
		    outputFile );
	}

	public static HttpClientStatus putRemoteCalendar ( URL url,
	    final String username, final String password, File inputFile ) {
		if ( !inputFile.exists () || inputFile.length () <= 0 ) {
			return new HttpClientStatus ( HttpClientStatus.HTTP_STATUS_NOT_FOUND,
			    "No such file: " + inputFile );
		}
		if ( username != null && password != null ) {
			Authenticator.setDefault ( new Authenticator () {
				protected PasswordAuthentication getPasswordAuthentication () {
					return new PasswordAuthentication ( username, password.toCharArray () );
				}
			} );
		} else {
			Authenticator.setDefault ( new Authenticator () {
				protected PasswordAuthentication getPasswordAuthentication () {
					return null;
				}
			} );
		}
		HttpURLConnection urlC = null;
		int totalRead = 0;
		try {
			urlC = (HttpURLConnection) url.openConnection ();
			urlC.setDoInput ( true );
			urlC.setDoOutput ( true );
			urlC.setUseCaches ( false );
			urlC.setDefaultUseCaches ( false );
			urlC.setAllowUserInteraction ( true );
			urlC.setRequestMethod ( "PUT" );
			urlC.setRequestProperty ( "Content-Length", "" + inputFile.length () );

			InputStream is = urlC.getInputStream ();
			OutputStream os = urlC.getOutputStream ();
			FileInputStream fis = new FileInputStream ( inputFile );
			DataInputStream dis = new DataInputStream (
			    new BufferedInputStream ( fis ) );
			DataOutputStream dos = new DataOutputStream ( new BufferedOutputStream (
			    os ) );
			byte[] buf = new byte[4 * 1024]; // 4K buffer
			int bytesRead;
			while ( ( bytesRead = dis.read ( buf ) ) != -1 ) {
				dos.write ( buf, 0, bytesRead );
				totalRead += bytesRead;
			}
			// TODO: should we read back the HTTP response data?
			os.close ();
			dos.close ();
			is.close ();
			dis.close ();
			urlC.disconnect ();
			// Handle the HTTP status code
			if ( urlC.getResponseCode () == HttpURLConnection.HTTP_NOT_FOUND ) {
				return new HttpClientStatus ( HttpClientStatus.HTTP_STATUS_NOT_FOUND,
				    "File not found on server" );
			} else if ( urlC.getResponseCode () == HttpURLConnection.HTTP_UNAUTHORIZED ) {
				return new HttpClientStatus (
				    HttpClientStatus.HTTP_STATUS_AUTH_REQUIRED, "Authorizaton required" );
			} else if ( urlC.getResponseCode () != HttpURLConnection.HTTP_OK ) {
				return new HttpClientStatus ( HttpClientStatus.HTTP_STATUS_OTHER_ERROR,
				    "HTTP Error " + urlC.getResponseCode () + ": "
				        + urlC.getResponseMessage () );
			}
		} catch ( IOException e1 ) {
			// Checking the response code can generate another IOException
			try {
				// Handle the HTTP status code
				if ( urlC.getResponseCode () == HttpURLConnection.HTTP_NOT_FOUND ) {
					// Handle this one individually since it will be the most common.
					return new HttpClientStatus ( HttpClientStatus.HTTP_STATUS_NOT_FOUND,
					    "File not found on server" );
				} else if ( urlC.getResponseCode () == HttpURLConnection.HTTP_UNAUTHORIZED ) {
					return new HttpClientStatus (
					    HttpClientStatus.HTTP_STATUS_AUTH_REQUIRED,
					    "Authorizaton required" );
				} else if ( urlC.getResponseCode () != HttpURLConnection.HTTP_OK ) {
					return new HttpClientStatus (
					    HttpClientStatus.HTTP_STATUS_OTHER_ERROR, "HTTP Error "
					        + urlC.getResponseCode () + ": " + urlC.getResponseMessage () );
				} else {
					return new HttpClientStatus (
					    HttpClientStatus.HTTP_STATUS_OTHER_ERROR, "HTTP I/O Exception:",
					    e1 );
				}
			} catch ( IOException e2 ) {
				// Print the stack trace on this one since it happened while we were
				// handling another exception...
				e2.printStackTrace ();
				return new HttpClientStatus ( HttpClientStatus.HTTP_STATUS_OTHER_ERROR,
				    "HTTP I/O Exception:", e1 );
			}
		}

		// Success
		return new HttpClientStatus ( HttpClientStatus.HTTP_STATUS_SUCCESS,
		    "File successfully uploaded" );
	}
}
