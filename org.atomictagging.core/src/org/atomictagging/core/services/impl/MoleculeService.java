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
package org.atomictagging.core.services.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.atomictagging.core.accessors.DB;
import org.atomictagging.core.services.ATService;
import org.atomictagging.core.services.IMoleculeService;
import org.atomictagging.core.types.IAtom;
import org.atomictagging.core.types.IMolecule;
import org.atomictagging.core.types.Molecule;
import org.atomictagging.utils.StringUtils;

/**
 * 
 */
public class MoleculeService extends AbstractService implements IMoleculeService {

	private static PreparedStatement	readMolecule;
	private static PreparedStatement	readMoleculeTags;
	static {
		try {
			readMolecule = DB.CONN
					.prepareStatement( "SELECT molecules_moleculeid, atoms_atomid FROM molecule_has_atoms WHERE molecules_moleculeid = ?" );
			readMoleculeTags = DB.CONN
					.prepareStatement( "SELECT tagid, tag FROM tags JOIN molecule_has_tags WHERE tagid = tags_tagid AND molecules_moleculeid = ?" );
		} catch ( final SQLException e ) {
			e.printStackTrace();
		}
	}


	@Override
	public IMolecule find( final long moleculeId ) {
		if ( moleculeId <= 0 ) {
			throw new IllegalArgumentException( "Invalid molecule ID." );
		}

		try {
			readMolecule.setLong( 1, moleculeId );
			final ResultSet moleculeResult = readMolecule.executeQuery();
			boolean first = true;
			final IMolecule molecule = new Molecule();
			final List<IAtom> atoms = new ArrayList<IAtom>();

			while ( moleculeResult.next() ) {
				if ( first ) {
					molecule.setId( moleculeResult.getLong( "molecules_moleculeid" ) );
					first = false;
				}

				final long atomId = moleculeResult.getLong( "atoms_atomid" );
				atoms.add( ATService.getAtomService().find( atomId ) );
			}

			molecule.setAtoms( atoms );

			readMoleculeTags.setLong( 1, moleculeId );
			final ResultSet moleculeTagResult = readMoleculeTags.executeQuery();
			final List<String> tags = new ArrayList<String>();

			while ( moleculeTagResult.next() ) {
				tags.add( moleculeTagResult.getString( "tag" ) );
			}

			molecule.setTags( tags );

			// No molecule found
			if ( first ) {
				return null;
			}

			return molecule;
		} catch ( final SQLException e ) {
			e.printStackTrace();
		}

		return null;
	}


	@Override
	public List<IMolecule> findByAtomData( final String data ) {
		final List<IMolecule> result = new ArrayList<IMolecule>();

		final String sql = "SELECT ma.molecules_moleculeid moleculeid, a.atomid FROM molecules m "
				+ "JOIN molecule_has_atoms ma on m.moleculeid=ma.molecules_moleculeid "
				+ "JOIN atoms a on ma.atoms_atomid=a.atomid "
				+ "WHERE moleculeid IN( "
				+ "SELECT ma.molecules_moleculeid moleculeid FROM molecules m join molecule_has_atoms ma on m.moleculeid=ma.molecules_moleculeid join atoms a on ma.atoms_atomid=a.atomid where a.data='"
				+ data + "' )";

		try {
			final PreparedStatement readMolecules = DB.CONN.prepareStatement( sql );

			// ResultSet = moleculeid, atomid
			final ResultSet moleculeResult = readMolecules.executeQuery();
			long moleculeId = -1;
			Molecule molecule = null;
			IAtom atom = null;
			final Map<Long, IAtom> atoms = new HashMap<Long, IAtom>();

			while ( moleculeResult.next() ) {
				final long moleculeIdNext = moleculeResult.getLong( "moleculeid" );

				if ( moleculeIdNext != moleculeId ) {
					moleculeId = moleculeIdNext;

					molecule = new Molecule();
					molecule.setId( moleculeId );
					molecule.setTags( ATService.getTagService().getForMolecule( moleculeId ) );

					result.add( molecule );
				}

				final long atomId = moleculeResult.getLong( "atomid" );

				// FIXME What is this supposed to accomplish? There is no write to this map so
				// this if will always be false.
				if ( !atoms.containsKey( atomId ) ) {
					atom = ATService.getAtomService().find( atomId );
				}

				molecule.getAtoms().add( atom );
			}

		} catch ( final SQLException e ) {
			e.printStackTrace();
		}

		return result;
	}


	@Override
	public List<IMolecule> find( final List<String> tags ) {
		final List<IMolecule> result = new ArrayList<IMolecule>();

		try {
			final String tagFilter = StringUtils.join( tags, "' OR tag = '" );
			String moleculeSQL;

			// FIXME SQL injection! Fix this!!
			if ( !tagFilter.isEmpty() ) {
				moleculeSQL =
				// TODO Select molecules with all their tags
				"SELECT mt.molecules_moleculeid AS moleculeid "
						+ "FROM tags JOIN molecule_has_tags AS mt ON (tagid = mt.tags_tagid) " + "WHERE tag = '"
						+ tagFilter + "' ORDER BY moleculeid;";

				throw new RuntimeException( "not yet implemented, sorry." );
			} else {
				moleculeSQL = "SELECT molecules_moleculeid AS moleculeid, tag " //
						+ "FROM molecule_has_tags AS mt JOIN tags ON (mt.tags_tagid = tagid)";
			}

			final PreparedStatement readMolecules = DB.CONN.prepareStatement( moleculeSQL );

			final String atomSQL = "SELECT atoms_atomid AS atomid "
					+ "FROM molecule_has_atoms WHERE molecules_moleculeid = ? GROUP BY atoms_atomid";
			final PreparedStatement readAtoms = DB.CONN.prepareStatement( atomSQL );

			final ResultSet moleculeResult = readMolecules.executeQuery();
			IMolecule molecule = new Molecule();
			long moleculeId = 0;

			// Build molecules with all their atoms
			while ( moleculeResult.next() ) {
				if ( moleculeId != moleculeResult.getLong( "moleculeid" ) ) {

					// Whenever the molecule ID changes, but not in first iteration
					if ( moleculeId != 0 ) {
						result.add( molecule );
						molecule = new Molecule();
					}

					moleculeId = moleculeResult.getLong( "moleculeid" );
					molecule.setId( moleculeId );

					readAtoms.setLong( 1, moleculeId );
					final ResultSet atomResult = readAtoms.executeQuery();
					final List<IAtom> atoms = new ArrayList<IAtom>();

					while ( atomResult.next() ) {
						final long atomId = atomResult.getLong( "atomid" );
						atoms.add( ATService.getAtomService().find( atomId ) );
					}
				}

				molecule.setTags( Arrays.asList( moleculeResult.getString( "tag" ) ) );
			}

			// Only if there was at least one molecule
			if ( moleculeId != 0 ) {
				result.add( molecule );
			}

		} catch ( final SQLException e ) {
			e.printStackTrace();
		}

		return result;
	}


	@Override
	public long save( final IMolecule molecule ) {
		long moleculeId = 0;

		try {
			DB.CONN.setAutoCommit( false );

			// Write atoms
			final List<Long> atomIds = ATService.getAtomService().save( molecule.getAtoms() );

			// Write molecule
			final PreparedStatement insertMolecule = DB.CONN.prepareStatement( "INSERT INTO molecules VALUES ()",
					Statement.RETURN_GENERATED_KEYS );
			insertMolecule.execute();
			moleculeId = getAutoIncrementId( insertMolecule );

			// Write molecule tags
			final PreparedStatement insertTags = DB.CONN
					.prepareStatement( "INSERT INTO molecule_has_tags (molecules_moleculeid, tags_tagid) VALUES (?, ?)" );
			insertTags.setLong( 1, moleculeId );

			for ( final String tag : molecule.getTags() ) {
				final long tagId = ATService.getTagService().save( tag );
				insertTags.setLong( 2, tagId );
				insertTags.execute();
			}

			// Write links between atoms and molecules
			final PreparedStatement insertLinks = DB.CONN
					.prepareStatement( "INSERT INTO molecule_has_atoms (molecules_moleculeid, atoms_atomid) VALUES (?, ?)" );
			insertLinks.setLong( 1, moleculeId );

			for ( final long atomId : atomIds ) {
				insertLinks.setLong( 2, atomId );
				insertLinks.execute();
			}

			DB.CONN.commit();
			DB.CONN.setAutoCommit( true );
		} catch ( final SQLException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return moleculeId;
	}

}
