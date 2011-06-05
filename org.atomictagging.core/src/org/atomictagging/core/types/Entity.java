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
 * Base class for all entities that supplies better equals and hashCode methods than Object does. It also provides a
 * negative ID generator to identify entities that have not been persisted.
 */
public abstract class Entity implements IEntity {

	private static long	ID_GENERATOR	= -1;
	protected long		id;


	/**
	 * Create a new entity. The ID of this entity will be a session-unique negative long unless overwritten explicitly.
	 */
	public Entity() {
		this.id = ID_GENERATOR--;
	}


	@Override
	public long getId() {
		return id;
	}


	@Override
	public void setId( long id ) {
		this.id = id;
	}


	@Override
	public boolean isPersistent() {
		return getId() > 0;
	}


	@Override
	public int hashCode() {
		// Copied from java.lang.Long
		return (int) ( id ^ ( id >>> 32 ) );
	}


	@Override
	public boolean equals( Object obj ) {
		if ( this == obj ) {
			return true;
		}
		if ( obj == null ) {
			return false;
		}
		if ( getClass() != obj.getClass() ) {
			return false;
		}
		Entity other = (Entity) obj;
		if ( id != other.id ) {
			return false;
		}
		return true;
	}
}