package us.k5n.k5ncal;

import java.awt.Color;

import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Common functions.
 * 
 * @version $Id$
 * @author Craig Knudsen, craig@k5n.us
 */
public class Utils {

	/**
	 * * For tags such as <name>xxx</name>, get the "xxx" for the Node.
	 * 
	 * @param node
	 *          the XML Node object
	 * @return the String value
	 */
	public static String xmlNodeGetValue ( Node node ) {
		NodeList list = node.getChildNodes ();
		int len = list.getLength ();
		if ( len > 1 )
			System.err.println ( "  Error: length of node=" + len + " for tag <"
			    + node.getNodeName () + ">" );
		for ( int i = 0; i < len; i++ ) {
			Node n = list.item ( i );
			// System.out.println ( " " + i + "> name=" + n.getNodeName() + ", value="
			// +
			// n.getNodeValue () + ", type=" + n.getNodeType() );
			if ( n.getNodeType () == Node.TEXT_NODE ) {
				return ( n.getNodeValue () );
			}
		}
		return ( null ); // not found
	}

	/**
	 * For tags such as <name attr="xxx" />, get the "xxx".
	 */
	public static String xmlNodeGetAttribute ( Node node, String name ) {
		NamedNodeMap list = node.getAttributes ();
		if ( list == null )
			return null;
		int len = list.getLength ();
		if ( len == 0 )
			return null;
		for ( int i = 0; i < len; i++ ) {
			Node n = list.item ( i );
			// System.out.println ( " " + i + "> name=" + n.getNodeName() + ", value="
			// +
			// n.getNodeValue () + ", type=" + n.getNodeType() );
			if ( n.getNodeType () == Node.ATTRIBUTE_NODE ) {
				Attr attr = (Attr) n;
				if ( name.equalsIgnoreCase ( attr.getName () ) ) {
					return attr.getValue ();
				}
			}
		}
		return ( null ); // not found
	}

	/**
	 * Convert a hex value into an integer.
	 * 
	 * @param st
	 *          Two-digit ex value ("FF", "00", "A0", etc.)
	 */
	public static int hexValue ( String st ) {
		st = st.toUpperCase ();
		int ret = 0;

		char ch1 = st.charAt ( 0 );
		if ( ch1 >= '0' && ch1 <= '9' )
			ret += 16 * Integer.parseInt ( "" + ch1 );
		else {
			switch ( ch1 ) {
				case 'A':
					ret += 16 * 10;
					break;
				case 'B':
					ret += 16 * 11;
					break;
				case 'C':
					ret += 16 * 12;
					break;
				case 'D':
					ret += 16 * 13;
					break;
				case 'E':
					ret += 16 * 14;
					break;
				case 'F':
					ret += 16 * 15;
					break;
			}
		}

		char ch2 = st.charAt ( 1 );
		if ( ch2 >= '0' && ch2 <= '9' )
			ret += Integer.parseInt ( "" + ch2 );
		else {
			switch ( ch2 ) {
				case 'A':
					ret += 10;
					break;
				case 'B':
					ret += 11;
					break;
				case 'C':
					ret += 12;
					break;
				case 'D':
					ret += 13;
					break;
				case 'E':
					ret += 14;
					break;
				case 'F':
					ret += 15;
					break;
			}
		}

		return ret;
	}

	public static String intToHex ( int num ) {
		char[] chars = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A',
		    'B', 'C', 'D', 'E', 'F' };
		int i1 = num / 16;
		int i2 = num % 16;
		StringBuffer ret = new StringBuffer ();
		ret.append ( chars[i1] );
		ret.append ( chars[i2] );
		return ret.toString ();
	}

	/**
	 * Escape a String for use in XML output. Note: assumes UTF-8 or UTF-16 as
	 * encoding
	 */
	public static String escape ( String content ) {
		return escape ( content, false );
	}

	/**
	 * Escape a String for use in XML output. Note: assumes UTF-8 or UTF-16 as
	 * encoding
	 */
	public static String escape ( String content, boolean isAttribute ) {
		StringBuffer buffer = new StringBuffer ();
		for ( int i = 0; i < content.length (); i++ ) {
			char c = content.charAt ( i );
			if ( c == '<' )
				buffer.append ( "&lt;" );
			else if ( c == '>' )
				buffer.append ( "&gt;" );
			else if ( c == '&' )
				buffer.append ( "&amp;" );
			else if ( c == '"' && isAttribute )
				buffer.append ( "&quot;" );
			else if ( c == '\'' && isAttribute )
				buffer.append ( "&pos;" );
			else
				buffer.append ( c );
		}
		return buffer.toString ();
	}

	public static Color parseColor ( String colorStr ) {
		int r = 192, g = 192, b = 192;

		try {
			if ( colorStr == null ) {
				// ignore
			} else if ( colorStr.indexOf ( "," ) > 0 ) {
				String[] params = colorStr.split ( "," );
				if ( params.length == 3 ) {
					r = Integer.parseInt ( params[0] );
					g = Integer.parseInt ( params[1] );
					b = Integer.parseInt ( params[2] );
				}
			} else if ( colorStr.startsWith ( "#" ) && colorStr.length () == 7 ) {
				r = Utils.hexValue ( colorStr.substring ( 1, 3 ) );
				g = Utils.hexValue ( colorStr.substring ( 3, 5 ) );
				b = Utils.hexValue ( colorStr.substring ( 5, 7 ) );
			} else if ( colorStr.length () == 6 ) {
				r = Utils.hexValue ( colorStr.substring ( 0, 2 ) );
				g = Utils.hexValue ( colorStr.substring ( 2, 4 ) );
				b = Utils.hexValue ( colorStr.substring ( 4, 6 ) );
			} else {
				System.err.println ( "Invalid color specification: " + colorStr + "'" );
			}
		} catch ( Exception e ) {
			System.err
			    .println ( "Invalid color specification for: " + colorStr + "'" );
		}
		return new Color ( r, g, b );
	}

}
