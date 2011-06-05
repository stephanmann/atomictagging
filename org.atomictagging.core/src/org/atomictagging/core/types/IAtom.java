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

import java.util.List;

/**
 * Representation of an atom
 */
public interface IAtom extends IEntity {

	/**
	 * Return the data contained in this atom.
	 * 
	 * @return The atoms data
	 */
	public String getData();


	/**
	 * Set the data of this atom.
	 * 
	 * @param data
	 */
	void setData( String data );


	/**
	 * Return all types of this atom.
	 * 
	 * @return The atoms types
	 */
	public List<String> getTypes();


	/**
	 * Override the types of this atom.
	 * 
	 * @param types
	 */
	void setTypes( List<String> types );


	/**
	 * Add a single type to the atoms types.
	 * 
	 * @param type
	 */
	void addType( String type );


	/**
	 * Remove a single type from the atoms types.
	 * 
	 * @param type
	 */
	void removeType( String type );

}
