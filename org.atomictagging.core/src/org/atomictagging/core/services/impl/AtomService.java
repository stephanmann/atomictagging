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
import java.util.List;

import org.apache.commons.lang.NotImplementedException;
import org.atomictagging.core.accessors.DB;
import org.atomictagging.core.services.ATService;
import org.atomictagging.core.services.IAtomService;
import org.atomictagging.core.types.Atom;
import org.atomictagging.core.types.CoreTypes;
import org.atomictagging.core.types.IAtom;
import org.eclipse.core.runtime.Assert;

/**
 * 
 */
public class AtomService extends AbstractService implements IAtomService {

	private final static String			ID				= "atomid";
	private final static String			DATA			= "data";
	private final static String			TYPE			= "type";

	private final static String			SELECT_ALL		= "SELECT " + ID + ", " + DATA + ", " + TYPE + " ";
	private final static String			FROM_JOIN_WHERE	= " FROM atoms JOIN atom_has_types JOIN types "
																+ "WHERE atomid = atoms_atomid AND types_typeid = typeid ";

	private static PreparedStatement	checkAtom;
	private static PreparedStatement	readAtom;
	private static PreparedStatement	insertAtom;
	private static PreparedStatement	checkAtomTypes;
	private static PreparedStatement	insertAtomTypes;

	static {
		try {
			checkAtom = DB.CONN.prepareStatement( "SELECT atomid FROM atoms WHERE data = ?" );
			readAtom = DB.CONN.prepareStatement( SELECT_ALL + FROM_JOIN_WHERE
					+ " AND types_typeid = typeid AND atomid = ?" );
			insertAtom = DB.CONN.prepareStatement( "INSERT INTO atoms (data) VALUES (?)",
					Statement.RETURN_GENERATED_KEYS );
			checkAtomTypes = DB.CONN
					.prepareStatement( "SELECT atoms_atomid FROM atom_has_types WHERE atoms_atomid = ? AND types_typeid = ?" );
			insertAtomTypes = DB.CONN
					.prepareStatement( "INSERT INTO atom_has_types (atoms_atomid, types_typeid) VALUES (?, ?)" );
		} catch ( final SQLException e ) {
			e.printStackTrace();
		}
	}


	@Override
	public IAtom create( final String type, final String data ) {
		return create( Arrays.asList( type ), data );
	}


	@Override
	public IAtom create( final List<String> types, final String data ) {
		final Atom atom = new Atom();
		atom.setTypes( types );
		atom.setData( data );
		return atom;
	}


	@Override
	public IAtom find( final long atomId ) {
		try {
			readAtom.setLong( 1, atomId );
			final ResultSet atomResult = readAtom.executeQuery();

			final List<IAtom> atoms = readFromResultSet( atomResult );
			Assert.isTrue( atoms.size() <= 1 );

			if ( atoms.size() == 1 ) {
				return atoms.get( 0 );
			}
		} catch ( final SQLException e ) {
			e.printStackTrace();
		}

		return null;
	}


	@Override
	public List<IAtom> find( final List<String> inclusiveTypes ) {
		return find( inclusiveTypes, Filter.INCLUDE );
	}


	@Override
	public List<IAtom> find( final List<String> types, final Filter filter ) {
		List<IAtom> atoms = new ArrayList<IAtom>();

		try {
			String is = "";
			if ( Filter.EXCLUDE == filter ) {
				is = " NOT ";
			}

			// FIXME Types need to be escaped
			final String subQuery = "SELECT " + ID + FROM_JOIN_WHERE + " AND type " + is + in( types );
			final String query = SELECT_ALL + FROM_JOIN_WHERE + " AND " + ID + " IN (" + subQuery + " ) ORDER BY " + ID;

			final Statement stmt = DB.CONN.createStatement();
			final ResultSet atomsResult = stmt.executeQuery( query );
			atoms = readFromResultSet( atomsResult );
		} catch ( final SQLException e ) {
			e.printStackTrace();
		}

		return atoms;
	}


	@Override
	public List<IAtom> findUserAtoms() {
		return find( CoreTypes.asList(), Filter.EXCLUDE );
	}


	@Override
	public List<String> getDomain() {
		final List<String> domain = new ArrayList<String>();

		final String sql = "SELECT DISTINCT a.data FROM atoms a JOIN atom_has_types at "
				+ "ON a.atomid = at.atoms_atomid JOIN types t ON at.types_typeid = t.typeid " + "WHERE t.type NOT "
				+ in( CoreTypes.asList() ) + " ORDER BY data";

		try {
			final PreparedStatement readMolecules = DB.CONN.prepareStatement( sql );

			final ResultSet resultSet = readMolecules.executeQuery();

			while ( resultSet.next() ) {
				domain.add( resultSet.getString( 1 ) );
			}

		} catch ( final SQLException e ) {
			e.printStackTrace();
		}

		return domain;
	}


	@Override
	public String[] getDomainAsArray() {
		final List<String> domain = getDomain();
		return domain.toArray( new String[domain.size()] );
	}


	@Override
	public IAtom findByData( final String data ) {
		IAtom atom = null;

		try {
			final String query = SELECT_ALL + FROM_JOIN_WHERE + " AND types_typeid = typeid AND data = '" + data + "'";
			final Statement stmt = DB.CONN.createStatement();
			final ResultSet atomsResult = stmt.executeQuery( query );
			final List<IAtom> resultSet = readFromResultSet( atomsResult );
			if ( !resultSet.isEmpty() ) {
				atom = resultSet.get( 0 );
			}
		} catch ( final SQLException e ) {
			e.printStackTrace();
		}

		return atom;
	}


	@Override
	public long save( final IAtom atom ) {
		try {
			final List<Long> ids = save( Arrays.asList( atom ) );
			Assert.isTrue( ids.size() == 1 );
			return ids.get( 0 );
		} catch ( final SQLException e ) {
			e.printStackTrace();
		}
		return -1;
	}


	@Override
	public List<Long> save( final List<IAtom> atoms ) throws SQLException {
		final List<Long> atomIds = new ArrayList<Long>();

		for ( final IAtom atom : atoms ) {
			checkAtom.setString( 1, atom.getData() );
			checkAtom.execute();
			long atomId = getIdOfExistingEntity( checkAtom, "atomid" );

			if ( atomId == -1 ) {
				insertAtom.setString( 1, atom.getData() );
				insertAtom.execute();
				atomId = getAutoIncrementId( insertAtom );
			}

			for ( final String type : atom.getTypes() ) {
				final long typeId = ATService.getTypeService().save( type );

				checkAtomTypes.setLong( 1, atomId );
				checkAtomTypes.setLong( 2, typeId );
				checkAtomTypes.execute();
				final long typeCheck = getIdOfExistingEntity( checkAtomTypes, "atoms_atomid" );

				if ( typeCheck == -1 ) {
					insertAtomTypes.setLong( 1, atomId );
					insertAtomTypes.setLong( 2, typeId );
					insertAtomTypes.execute();
				}
			}

			atomIds.add( atomId );
		}

		return atomIds;
	}


	@Override
	public void delete( final IAtom atom ) {
		throw new NotImplementedException( "Not yet implemented. Sorry." );
	}


	private List<IAtom> readFromResultSet( final ResultSet atomsResult ) throws SQLException {
		final List<IAtom> atoms = new ArrayList<IAtom>();

		try {
			// The result set contains atoms multiple times, as often as they have types.
			// That's why the next() call is around the type retrieval. If it was in the
			// while loop, we would loose atoms or at least types.
			atomsResult.next();

			while ( !atomsResult.isAfterLast() ) {
				final long atomId = atomsResult.getLong( ID );
				final String data = atomsResult.getString( DATA );
				final String type = atomsResult.getString( TYPE );

				final ArrayList<String> types = new ArrayList<String>();
				types.add( type );

				final Atom atom = (Atom) create( types, data );
				atom.setId( atomId );

				while ( atomsResult.next() && atomsResult.getLong( ID ) == atomId ) {
					atom.addType( atomsResult.getString( TYPE ) );
				}

				atoms.add( atom );
			}
		} finally {
			atomsResult.close();
		}

		return atoms;
	}

}
