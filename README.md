# k5nCal

|Version:      |0.9.9-SNAPSHOT|
|--------------|----------------------------|
|URL:          |http://www.k5n.us/k5ncal.php|
|Author:       |Craig Knudsen, &#99;&#114;&#97;&#105;&#103;&#64;&#107;&#53;&#110;&#46;&#117;&#115;|
|License:      |GNU GPL|
|Requires:     |Java 1.8 or later|

## About
The k5nCal is a desktop calendar application written in Java.  You can
maintain a local calendar and subscribe to multiple iCalendar-based
calendars online.


## Status
This is a beta quality release.  There are surely bugs, and there are
some features not yet implemented.

Features not yet implemented:
 - Advanced recurrence rules (every other week, specific weekdays, etc.)
   You will be able to properly view events with advanced recurrence
   rules if they are imported or part of a subscribed calendar, but you
   cannot edit or create these type of events from k5nCal yet.
 - Modify in any way events from a subscribed calendar
 - Biews other than the 5-week month view
 - Many others... see the app home page and click on "Plans" to
   see what else will eventually be added.


## Building from source
To build the source, you will need to use ant with the provided build.xml
file.

To build with ant:
```
ant
```

This build process will create the following jar file:
```
dist/k5nCal-0.9.9-SNAPSHOT.jar
```

## Running the App

To run the k5nCal application, you can double-click on the file
in your file browser (Windows Explorer, Mac OS X Finder, etc.), or you
can start it from the command line:
```
java -jar k5nCal-0.9.9-SNAPSHOT.jar
```

## License
This application and all associated tools and applications are licensed under
the GNU General Public License.

For information about this license: http://www.gnu.org/licenses/gpl.html

## 3rd Party Packages

This package makes use of the following packages.  The class files from
these packages are bundled into the k5nCal jar file, so you do NOT need
to download them separately.

Package|License
-------|-------
[k5n Java Calendar Tools](http://www.k5n.us/javacaltools.php)|[GNU GPL](http://www.fsf.org/licensing/licenses/gpl.html)
[Joda Time](http://joda-time.sourceforge.net/index.html)|[Apache License 2.0](http://joda-time.sourceforge.net/license.html)
[k5n AccordionPane](http://www.k5n.us/k5naccordion.php)|[GNU GPL](http://www.fsf.org/licensing/licenses/gpl.html)
[Google RFC2445](http://code.google.com/p/google-rfc-2445/)|[Apache License 2.0](http://www.apache.org/licenses/)
[Java CSV Library](http://sourceforge.net/projects/javacsv/)|[LGPL](http://www.gnu.org/licenses/lgpl.html)
[JCalendar](http://www.toedter.com/en/jcalendar/index.html)|[LGPL](http://www.toedter.com/en/jcalendar/license.html)

