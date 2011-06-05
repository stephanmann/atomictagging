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

/**
 * @author tokei
 * 
 */
public interface IEntity {

	/**
	 * Returns the entities ID. The ID is negative if it was generated automatically. If it is positive, the entity is
	 * already persistent in the data base.
	 * 
	 * @return Entity ID
	 */
	long getId();


	/**
	 * Set the ID of the entity.
	 * 
	 * @param id
	 */
	void setId( long id );


	/**
	 * Returns whether this entity has been persisted. This is decided solely on its ID.
	 * 
	 * @return Persistence state
	 */
	boolean isPersistent();

}