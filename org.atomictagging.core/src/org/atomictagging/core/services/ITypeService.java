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
public interface ITypeService {

	/**
	 * Returns all types in the order the database returns them. An empty list in case that there are no types.
	 * 
	 * @return A list of types, never null.
	 */
	List<String> getAll();


	/**
	 * Returns all types in the order the database returns them. An empty array in case that there are no types.
	 * 
	 * @return A list of types, never null.
	 */
	String[] getAllAsArray();


	/**
	 * Returns all types for a certain atom.
	 * 
	 * @param id
	 * @return A list of types, never null.
	 */
	public List<String> getForAtom( final long id );


	/**
	 * Saves a type and returns its ID, as defined by the DB. If the type already exists in the DB, it will not be
	 * inserted again but rather the ID of the stored type will be returned.
	 * 
	 * @param type
	 * @return The ID of the type as returned by the DB or <code>-1</code> if there was a DB error.
	 */
	long save( String type );
}
