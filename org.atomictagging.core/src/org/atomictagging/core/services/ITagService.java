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
package org.atomictagging.core.services;

import java.util.List;

/**
 * Service to handle tags.
 */
public interface ITagService {

	/**
	 * Returns all tags in the order the database returns them. An empty list in case that there are no tags.
	 * 
	 * @return A list of tags, never null.
	 */
	List<String> getAll();


	/**
	 * Returns all tags in the order the database returns them. An empty array in case that there are no tags.
	 * 
	 * @return A list of tags, never null.
	 */
	String[] getAllAsArray();


	/**
	 * Returns all tags for a certain molecule.
	 * 
	 * @param id
	 * @return A list of tags, never null.
	 */
	public List<String> getForMolecule( final long id );


	/**
	 * Saves a tag and returns its ID, as defined by the DB. If the tag already exists in the DB, it will not be
	 * inserted again but rather the ID of the stored tag will be returned.
	 * 
	 * @param tag
	 * @return The ID of the tag as returned by the DB or <code>-1</code> if there was a DB error.
	 */
	long save( String tag );
}
