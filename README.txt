                           k5nCal
****************************************************************************

Version:      0.9.5
URL:          http://k5ncal.sourceforge.net
Author:       Craig Knudsen, craig [< at >] k5n.us
License:      GNU GPL
Requires:     Java 1.5 or later

---------------------------------------------------------------------------
                         ABOUT
---------------------------------------------------------------------------
The k5nCal is a desktop calendar application written in Java.  You can
maintain a local calendar and subscribe to multiple iCalendar-based
calendars online.

---------------------------------------------------------------------------
                         STATUS
---------------------------------------------------------------------------
This is an alpha quality release.  There are surely bugs, and there are
many features not yet implemented.

Features not yet implemented:
 - automatically update/refresh remote/subscribed calendars
 - add/edit/delete of events with recurrance
 - modify in any way events from a subscribed calendar
 - views other than the 5-week month view
 - view event details (categories, status, etc.)
 - many others... see the app home page and click on "Plans" to
   see what else will eventually be added.

---------------------------------------------------------------------------
                         BUILDING
---------------------------------------------------------------------------
To build the source, you will need to use ant with the provided build.xml
file.  (Ant 1.6 or later is required.)

To build with ant:

	ant

This build process will create the following jar file:

	dist/k5nCal-0.9.4.jar

---------------------------------------------------------------------------
                         RUNNING THE APP
---------------------------------------------------------------------------

To run the k5nCal application, you can double-click on the file
in your file browser (Windows Explorer, Mac OS X Finder, etc.), or you
can start it from the command line:

java -jar k5nCal-0.9.4.jar

---------------------------------------------------------------------------
                         LICENSE
---------------------------------------------------------------------------

This application and all associated tools and applications are licensed under
the GNU General Public License.

For information about this license:

	http://www.gnu.org/licenses/gpl.html
	

---------------------------------------------------------------------------
                         3RD PARTY PACKAGES
---------------------------------------------------------------------------
	

This package makes use of the following packages.  The class files from
these packages are bundled into the k5nCal jar file, so you do not need
to download them separately.

k5n Java Calendar Tools:
  URL: http://www.k5n.us/javacaltools.php
  License: GNU GPL
  License URL: http://www.fsf.org/licensing/licenses/gpl.html

Joda Time
  URL: http://joda-time.sourceforge.net/index.html
  License: Apache License 2.0
  License URL: http://joda-time.sourceforge.net/license.html
  
Google RFC2445
  URL: http://code.google.com/p/google-rfc-2445/
  License: Apache License 2.0
  License URL: http://www.apache.org/licenses/
  
Java CSV Library:
  URL: http://sourceforge.net/projects/javacsv/
  License: LGPL
  License URL: http://www.gnu.org/licenses/lgpl.html

JCalendar:
  URL: http://www.toedter.com/en/jcalendar/index.html
  License: LGPL
  Licence URL: http://www.toedter.com/en/jcalendar/license.html
