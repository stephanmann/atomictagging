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
package org.atomictagging.core.types;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Types that are added and interpreted by the system.
 */
public final class CoreTypes {

	/**
	 * The type that gets attached to all atoms pointing to a file on the file system.
	 */
	public final static String	FILEREF					= "x-fileref";

	/**
	 * The type that gets attached to all atoms pointing to a file not on the local file system. These files might not
	 * be available at all times and therefore need special treatment.
	 */
	public final static String	FILEREF_REMOTE			= "x-remotefile";

	/**
	 * The type that gets attached to atoms that point to a remote location specified by the configuration.
	 */
	public final static String	FILEREF_REMOTE_LOCATION	= "x-remotelocation";

	/**
	 * Generic type for files which type is unknown.
	 */
	public final static String	FILETYPE_UNKNOWN		= "x-filetype-unknown";

	/**
	 * Type for video files.
	 */
	public final static String	FILETYPE_VIDEO			= "x-filetype-video";

	/**
	 * Type for image files.
	 */
	public final static String	FILETYPE_IMAGE			= "x-filetype-image";


	/**
	 * Returns all core types as a list.
	 * 
	 * @return List of core types.
	 */
	public static List<String> asList() {
		final List<String> coreTypes = new ArrayList<String>();

		try {
			for ( final Field field : CoreTypes.class.getDeclaredFields() ) {
				coreTypes.add( (String) field.get( CoreTypes.class ) );
			}
		} catch ( final IllegalArgumentException e ) {
			e.printStackTrace();
		} catch ( final IllegalAccessException e ) {
			e.printStackTrace();
		}

		return coreTypes;
	}
}
