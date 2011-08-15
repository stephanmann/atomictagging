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
import org.atomictagging.core.types.CoreTypes;
import org.atomictagging.core.types.IMolecule;
import org.atomictagging.shell.IShell;

/**
 * The implementation of a command that opens binary files
 * 
 * @author Stephan Mann
 */
public class OpenCommand extends AbstractCommand {

	/**
	 * @param shell
	 */
	public OpenCommand( final IShell shell ) {
		super( shell );
	}


	@Override
	public String getCommandString() {
		return "open";
	}


	@Override
	public String getHelpMessage() {
		return "open <MID>\t- Opens a " + CoreTypes.FILEREF
				+ " atom from the molecule in the desktops default application";
	}


	@Override
	public String getVerboseHelpMessage() {
		return getHelpMessage() + "\n" + "\t\tThis command looks for a atom with the tag \"" + CoreTypes.FILEREF
				+ "\" in the specified molecule\n"
				+ "\t\tand opens the file this atom points to in the desktops default application.";
	}


	@Override
	public int handleInput( final String input, final PrintStream stdout ) {
		final long moleculeId = validateAndParseId( input, stdout );
		final IMolecule molecule = validateAndLoad( moleculeId, stdout );

		if ( molecule == null ) {
			return 1;
		}

		final IMoleculeViewer viewer = MoleculeHandlerFactory.getInstance().getViewer( molecule );
		viewer.showMolecule( molecule );
		return 0;
	}

}
