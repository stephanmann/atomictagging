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

import org.atomictagging.core.moleculehandler.IMoleculeViewer;
import org.atomictagging.core.moleculehandler.MoleculeHandlerFactory;
import org.atomictagging.core.moleculehandler.IMoleculeViewer.VERBOSITY;
import org.atomictagging.core.types.IMolecule;
import org.atomictagging.shell.IShell;

/**
 * Command to show the details of a molecule.
 * 
 * @author Stephan Mann
 */
public class ShowCommand extends AbstractCommand {

	/**
	 * @param shell
	 */
	public ShowCommand( IShell shell ) {
		super( shell );
	}


	@Override
	public String getCommandString() {
		return "show";
	}


	@Override
	public String getHelpMessage() {
		return "show <MID>\t- Shows a molecule with all its atoms";
	}


	@Override
	public int handleInput( String input, PrintStream stdout ) {
		long moleculeId = validateAndParseId( input, stdout );
		IMolecule molecule = validateAndLoad( moleculeId, stdout );

		if ( molecule == null ) {
			return 1;
		}

		IMoleculeViewer viewer = MoleculeHandlerFactory.getInstance().getViewer( molecule );
		stdout.println( viewer.getTextRepresentation( molecule, MAX_LENGTH, VERBOSITY.VERBOSE ) );
		return 0;
	}

}
