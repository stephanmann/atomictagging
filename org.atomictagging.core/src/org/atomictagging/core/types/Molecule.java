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
 * Implementation of a molecule.
 */
public class Molecule extends Entity implements IMolecule {

	private List<IAtom>		atoms;
	private List<String>	tags;


	/**
	 * Create a new empty molecule.
	 */
	public Molecule() {
		atoms = new ArrayList<IAtom>();
		tags = new ArrayList<String>();
	}


	@Override
	public List<IAtom> getAtoms() {
		return atoms;
	}


	@Override
	public void setAtoms( final List<IAtom> atoms ) {
		this.atoms = atoms;
	}


	@Override
	public List<String> getTags() {
		return tags;
	}


	@Override
	public void setTags( final List<String> tags ) {
		this.tags = tags;
	}


	@Override
	public List<String> getAtomTags() {
		final ArrayList<String> result = new ArrayList<String>();

		for ( final IAtom atom : atoms ) {
			result.addAll( atom.getTypes() );
		}

		return result;
	}


	@Override
	public List<IAtom> getAtomsOfType( final String searchTag ) {
		final List<IAtom> list = new ArrayList<IAtom>();

		for ( final IAtom atom : atoms ) {
			for ( final String tag : atom.getTypes() ) {
				if ( searchTag.equals( tag ) ) {
					list.add( atom );
					break;
				}
			}
		}

		return list;
	}


	@Override
	public String toString() {
		return "Molecule: id=" + id + "; tags=" + tags + "; atom#=" + atoms.size();
	}

}
