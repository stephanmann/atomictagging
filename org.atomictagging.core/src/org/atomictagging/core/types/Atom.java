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

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of an atom
 */
public class Atom extends Entity implements IAtom {

	private String			data;
	private List<String>	types;


	/**
	 * Create a new empty atom.
	 */
	public Atom() {
		types = new ArrayList<String>();
	}


	private Atom( final AtomBuilder builder ) {
		if ( builder.atomId > 0 ) {
			this.id = builder.atomId;
		}
		this.data = builder.atomData;
		this.types = builder.atomTypes;
	}


	/**
	 * Start building an atom.
	 * 
	 * @return A builder instance
	 */
	public static AtomBuilder build() {
		return new AtomBuilder();
	}


	@Override
	public String getData() {
		return data;
	}


	@Override
	public void setData( final String data ) {
		this.data = data;
	}


	@Override
	public List<String> getTypes() {
		return types;
	}


	@Override
	public void setTypes( final List<String> types ) {
		this.types = types;
	}


	@Override
	public void addType( final String type ) {
		types.add( type );
	}


	@Override
	public void removeType( final String type ) {
		types.remove( type );
	}


	@Override
	public String toString() {
		return "Atom: id=" + getId() + "; data=" + getData() + "; types=" + types;
	}

	/**
	 * Build a consistent atom while regarding all constraints.
	 */
	public static class AtomBuilder {
		private long			atomId;
		private String			atomData;
		private List<String>	atomTypes	= new ArrayList<String>();


		/**
		 * Set the ID for the atom.
		 * 
		 * @param id
		 * @return The builder
		 */
		public AtomBuilder withId( final long id ) {
			if ( id < 0 ) {
				throw new IllegalArgumentException( "ID of an atom must be a number greater 0." );
			}

			atomId = id;
			return this;
		}


		/**
		 * Set the data for the atom.
		 * 
		 * @param data
		 * @return The builder
		 */
		public AtomBuilder withData( final String data ) {
			if ( data == null || data.isEmpty() ) {
				throw new IllegalArgumentException( "Data in an atom must not be NULL or empty." );
			}

			atomData = data;
			return this;
		}


		/**
		 * Set a tag for the atom. Multiple calls will result in multiple tags.
		 * 
		 * @param type
		 * @return The builder
		 */
		public AtomBuilder withType( final String type ) {
			if ( type == null || type.isEmpty() ) {
				throw new IllegalArgumentException( "A type must not be NULL or empty." );
			}

			if ( atomTypes.contains( type ) ) {
				throw new IllegalArgumentException( "Duplicate type <" + type + "> for atom." );
			}

			atomTypes.add( type );
			return this;
		}


		/**
		 * Set a list of types for the atom. Will not overwrite previously added types.
		 * 
		 * @param types
		 * @return The builder
		 */
		public AtomBuilder withTypes( final List<String> types ) {
			// Don't add the list at once because we need to
			// enforce the rules specified by withType(String).
			for ( final String type : types ) {
				withType( type );
			}
			return this;
		}


		/**
		 * Replace whatever types have been set so far with the types provided.
		 * 
		 * @param types
		 * @return The builder
		 */
		public AtomBuilder replaceTypes( final List<String> types ) {
			this.atomTypes = new ArrayList<String>();
			withTypes( types );
			return this;
		}


		/**
		 * Create the atom from this builder instance.
		 * 
		 * @return A consistent atom instance
		 */
		public Atom buildWithDataAndType() {
			if ( atomData == null || atomData.isEmpty() ) {
				throw new IllegalArgumentException( "Data in an atom must not be NULL or empty." );
			}

			if ( atomTypes.size() == 0 ) {
				throw new IllegalArgumentException( "An atom must have at least one type." );
			}

			return new Atom( this );
		}
	}

}
