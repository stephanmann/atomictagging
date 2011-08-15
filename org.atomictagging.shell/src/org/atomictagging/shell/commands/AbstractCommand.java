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

import org.atomictagging.core.services.ATService;
import org.atomictagging.core.types.IMolecule;
import org.atomictagging.shell.IShell;

/**
 * Default implementation of ICommand
 * 
 * @author Stephan Mann
 */
public abstract class AbstractCommand implements ICommand {

	/**
	 * A reference to the shell the command is registered to.
	 */
	protected IShell	shell;
	/**
	 * The max length the output will take.
	 * 
	 * TODO This should of course be dynamic depending on the terminals width
	 */
	protected final int	MAX_LENGTH	= 120;


	/**
	 * Default constructor
	 * 
	 * @param shell
	 */
	public AbstractCommand( IShell shell ) {
		this.shell = shell;
	}


	@Override
	public String getVerboseHelpMessage() {
		return getHelpMessage();
	}


	protected static long validateAndParseId( String input, PrintStream stdout ) {
		if ( input.trim().isEmpty() ) {
			stdout.println( "Please specifiy a molecule ID." );
			return 0;
		}

		return Long.parseLong( input );
	}


	protected static IMolecule validateAndLoad( long moleculeId, PrintStream stdout ) {
		if ( moleculeId == 0 ) {
			stdout.println( "No valid molecule ID given." );
			return null;
		}

		IMolecule molecule = ATService.getMoleculeService().find( moleculeId );

		if ( molecule == null ) {
			stdout.println( "No molecule found with given ID " + moleculeId );
			return null;
		}

		return molecule;
	}

}
