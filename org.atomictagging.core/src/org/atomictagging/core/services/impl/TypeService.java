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
import java.util.List;

import org.atomictagging.core.accessors.DB;
import org.atomictagging.core.services.ITypeService;
import org.eclipse.core.runtime.Assert;

/**
 * Service to handle types.
 */
public class TypeService extends AbstractService implements ITypeService {

	private static PreparedStatement	allTypes;
	private static PreparedStatement	checkType;
	private static PreparedStatement	insertType;
	private static PreparedStatement	allTypesForAtom;

	static {
		try {
			allTypes = DB.CONN.prepareStatement( "SELECT type FROM types" );
			checkType = DB.CONN.prepareStatement( "SELECT typeid FROM types WHERE type = ?" );
			insertType = DB.CONN.prepareStatement( "INSERT INTO types (type) VALUES (?)",
					Statement.RETURN_GENERATED_KEYS );
			allTypesForAtom = DB.CONN
					.prepareStatement( "SELECT type FROM atom_has_types at JOIN types t ON at.types_typeid=t.typeid WHERE at.atoms_atomid=?" );
		} catch ( final SQLException e ) {
			e.printStackTrace();
		}
	}


	@Override
	public List<String> getAll() {
		final List<String> tags = new ArrayList<String>();

		try {
			final ResultSet tagResult = allTypes.executeQuery();

			while ( tagResult.next() ) {
				tags.add( tagResult.getString( "type" ) );
			}
		} catch ( final SQLException e ) {
			e.printStackTrace();
		}

		return tags;
	}


	@Override
	public String[] getAllAsArray() {
		final List<String> list = getAll();

		final String[] tags = list.toArray( new String[list.size()] );

		return tags;
	}


	@Override
	public List<String> getForAtom( final long id ) {
		final List<String> types = new ArrayList<String>();

		try {
			allTypesForAtom.setLong( 1, id );
			final ResultSet typeResult = allTypesForAtom.executeQuery();

			while ( typeResult.next() ) {
				types.add( typeResult.getString( "type" ) );
			}
		} catch ( final SQLException e ) {
			e.printStackTrace();
		}

		return types;
	}


	@Override
	public long save( final String type ) {
		Assert.isTrue( type != null && !type.isEmpty() );

		long typeId = -1;
		try {
			checkType.setString( 1, type );
			checkType.execute();
			typeId = getIdOfExistingEntity( checkType, "typeid" );

			if ( typeId == -1 ) {
				insertType.setString( 1, type );
				insertType.execute();
				typeId = getAutoIncrementId( insertType );
			}
		} catch ( final SQLException e ) {
			e.printStackTrace();
		}

		return typeId;
	}

}
