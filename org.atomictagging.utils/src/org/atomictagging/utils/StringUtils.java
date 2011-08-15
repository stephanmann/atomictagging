/**
 * This file is part of Atomic Tagging.
 * 
 * Atomic Tagging is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * Atomic Tagging is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with Atomic Tagging. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package org.atomictagging.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Some helper methods for handling strings
 * 
 * TODO Check whether this is still appropriate or a switch to Apache commons would be in order.
 */
public class StringUtils {

	/**
	 * Joins a list of strings to one string
	 * 
	 * @param strings
	 * @param delimiter
	 * @return Joined string
	 */
	public static String join( final List<String> strings, final String delimiter ) {
		assert strings != null;

		if ( strings.isEmpty() ) {
			return "";
		}

		boolean first = true;
		final StringBuilder builder = new StringBuilder();

		for ( final String string : strings ) {
			if ( !first ) {
				builder.append( delimiter );
			}
			first = false;
			builder.append( string );
		}

		return builder.toString();
	}


	/**
	 * Cut a string that is longer than the given length an append three dots "..."
	 * 
	 * @param string
	 * @param length
	 * @return The given string or a cut version, if the string was longer than the given length
	 */
	public static String cut( final String string, final int length ) {
		return ( string.length() > length ) ? string.substring( 0, length - 3 ) + "..." : string;
	}


	/**
	 * Repeats a given string as many times as requested and returns the result as a string.
	 * 
	 * @param string
	 * @param times
	 * @return The string repeated as many times as requested
	 */
	public static String repeat( final String string, final int times ) {
		final StringBuilder builder = new StringBuilder();

		for ( int i = 0; i < times; i++ ) {
			builder.append( string );
		}

		return builder.toString();
	}


	public static List<String> breakCommaSeparatedString( final String text ) {
		final List<String> list = new ArrayList<String>();

		if ( text != null && !text.equals( "" ) ) {
			final String[] split = text.split( "," );
			for ( int i = 0; i < split.length; i++ ) {
				list.add( split[i].trim() );
			}
		}

		return list;
	}
}
