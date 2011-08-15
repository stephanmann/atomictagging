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
package org.atomictagging.shell.commands;

import java.io.PrintStream;

import org.atomictagging.shell.IShell;

/**
 * Base for all commands that need to differentiate atoms and molecules by the command arguments.
 * 
 * @author Stephan Mann
 */
public abstract class AbstractModifyCommand extends AbstractCommand {

	/**
	 * @param shell
	 */
	public AbstractModifyCommand( IShell shell ) {
		super( shell );
	}


	@Override
	public int handleInput( String input, PrintStream stdout ) {
		String[] parts = input.trim().split( " ", 2 );
		if ( parts.length != 2 ) {
			stdout.println( "Please specify type (atom, molecule) and ID." );
			return 1;
		}

		String type = parts[0];
		if ( !type.equals( "atom" ) && !type.equals( "molecule" ) ) {
			stdout.println( "Please specify valid type: atom or molecule." );
			return 1;
		}

		long id;
		try {
			id = Long.parseLong( parts[1].trim() );
		} catch ( NumberFormatException e ) {
			stdout.println( "Invalid ID: " + parts[1] );
			return 1;
		}

		if ( type.equals( "atom" ) ) {
			return handleAtom( id, stdout );
		}
		return handleMolecule( id, stdout );
	}


	/**
	 * @param id
	 * @param stdout
	 * @return
	 */
	protected abstract int handleAtom( long id, PrintStream stdout );


	/**
	 * @param id
	 * @param stdout
	 * @return
	 */
	protected abstract int handleMolecule( long id, PrintStream stdout );

}
