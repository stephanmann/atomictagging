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
import java.util.List;

import org.atomictagging.utils.StringUtils;

/**
 * Some convenience methods that are common to all services.
 */
public abstract class AbstractService {

	protected long getAutoIncrementId( final PreparedStatement statement ) throws SQLException {
		final ResultSet resultSet = statement.getGeneratedKeys();

		if ( resultSet.next() ) {
			return resultSet.getLong( "GENERATED_KEY" );
		}

		return -1;
	}


	protected long getIdOfExistingEntity( final PreparedStatement statement, final String column ) throws SQLException {
		final ResultSet possiblyExisting = statement.getResultSet();

		if ( possiblyExisting.next() ) {
			return possiblyExisting.getLong( column );
		}

		return -1;
	}


	/**
	 * Builds a query string with the following syntax: <br>
	 * IN ( type0, type1, ..., typeN)
	 * 
	 * @param types
	 * @return IN ( type0, type1, ..., typeN)
	 */
	protected String in( final List<String> tags ) {
		return " IN ('" + StringUtils.join( tags, "', '" ) + "') ";
	}

}
