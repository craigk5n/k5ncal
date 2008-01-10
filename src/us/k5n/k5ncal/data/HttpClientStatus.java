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

/**
 * This class defines the object returned from HttpClient.
 * 
 * @author Craig Knudsen, craig@k5n.us
 * @version $Id$
 */
public class HttpClientStatus {
	public static final int HTTP_STATUS_SUCCESS = 0;
	public static final int HTTP_STATUS_AUTH_REQUIRED = 1;
	public static final int HTTP_STATUS_NOT_FOUND = 2;
	public static final int HTTP_STATUS_OTHER_ERROR = 3;
	String message;
	int status;
	File resultFile;
	Exception exception;

	public HttpClientStatus(int status, String message) {
		this ( status, message, null, null );
	}

	public HttpClientStatus(int status, String message, Exception exception) {
		this ( status, message, null, exception );
	}

	public HttpClientStatus(int status, File resultFile) {
		this ( status, null, resultFile, null );
	}

	public HttpClientStatus(int status, String message, File resultFile,
	    Exception exception) {
		this.status = status;
		this.message = message;
		this.resultFile = resultFile;
		this.exception = exception;
	}

	public String getMessage () {
		return message;
	}

	public int getStatus () {
		return status;
	}

	public File getResultFile () {
		return resultFile;
	}

	public Exception getException () {
		return exception;
	}

}
