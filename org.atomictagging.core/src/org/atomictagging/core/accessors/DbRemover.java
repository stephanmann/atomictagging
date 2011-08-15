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
package org.atomictagging.core.accessors;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.atomictagging.core.services.ATService;
import org.atomictagging.core.types.IAtom;
import org.atomictagging.core.types.IMolecule;

/**
 * A class that handles removing atoms or molecules.
 * 
 * @author Stephan Mann
 */
public class DbRemover {

	/**
	 * Remove the molecule specified by the given ID and all atoms that are not part of another molecule.
	 * 
	 * @param id
	 */
	public static void removeMolecule( long id ) {
		try {
			DB.CONN.setAutoCommit( false );

			IMolecule molecule = ATService.getMoleculeService().find( id );

			// Delete all atoms that are only linked by this one molecule.
			PreparedStatement checkAtom = DB.CONN
					.prepareStatement( "SELECT COUNT(*) AS amount FROM molecule_has_atoms WHERE atoms_atomid = ?" );
			PreparedStatement deleteAtom = DB.CONN.prepareStatement( "DELETE FROM atoms WHERE atomid = ?" );
			PreparedStatement deleteAtomTags = DB.CONN
					.prepareStatement( "DELETE FROM atom_has_tags WHERE atoms_atomid = ?" );

			for ( IAtom atom : molecule.getAtoms() ) {
				checkAtom.setLong( 1, atom.getId() );
				ResultSet result = checkAtom.executeQuery();
				result.next();
				int amount = result.getInt( "amount" );

				if ( amount == 0 ) {
					DB.CONN.rollback();
					throw new RuntimeException( "This can't be. There is no link to this atom." );
				}

				// Delete all atom links.
				// TODO This could be done more efficient by removing all links to this molecule with one query.
				// But this must be done prior to the atom deletion (foreign key) and after the checkAtom statement
				// (changes the result).
				PreparedStatement deleteAtomLink = DB.CONN
						.prepareStatement( "DELETE FROM molecule_has_atoms WHERE molecules_moleculeid = ? AND atoms_atomid = ?" );
				deleteAtomLink.setLong( 1, molecule.getId() );
				deleteAtomLink.setLong( 2, atom.getId() );
				deleteAtomLink.execute();

				if ( amount == 1 ) {
					deleteAtomTags.setLong( 1, atom.getId() );
					deleteAtomTags.execute();

					deleteAtom.setLong( 1, atom.getId() );
					deleteAtom.execute();
				}
			}

			// Delete molecule.
			PreparedStatement deleteMoleculeTags = DB.CONN
					.prepareStatement( "DELETE FROM molecule_has_tags WHERE molecules_moleculeid = ?" );
			deleteMoleculeTags.setLong( 1, molecule.getId() );
			deleteMoleculeTags.execute();

			PreparedStatement deleteMolecule = DB.CONN.prepareStatement( "DELETE FROM molecules WHERE moleculeid = ?" );
			deleteMolecule.setLong( 1, molecule.getId() );
			deleteMolecule.execute();

			DB.CONN.commit();
		} catch ( SQLException e ) {
			// TODO Auto-generated catch block
			try {
				DB.CONN.rollback();
			} catch ( SQLException e1 ) {
				// TODO Auto-generated catch block
				System.out.println( "rollback failed" );
				e1.printStackTrace();
			}
			e.printStackTrace();
		} finally {
			try {
				DB.CONN.setAutoCommit( true );
			} catch ( SQLException e ) {
				// TODO Auto-generated catch block
				System.out.println( "enabling auto commit failed" );
				e.printStackTrace();
			}
		}
		// TODO Delete tags that are only attached to these atoms
		// TODO Delete tags that are only attached to this molecule
		// TODO Delete binary files that are referenced by a x-fileref atom
	}


	/**
	 * Remove the atom specified by the given ID and all molecules that only consist this one atom.
	 * 
	 * @param id
	 */
	public static void removeAtom( long id ) {

	}

}
