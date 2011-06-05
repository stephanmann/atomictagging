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
	public void addType( String type ) {
		types.add( type );
	}


	@Override
	public void removeType( String type ) {
		types.remove( type );
	}


	@Override
	public String toString() {
		return "Atom: id=" + getId() + "; data=" + getData() + "; types=" + types;
	}

}
