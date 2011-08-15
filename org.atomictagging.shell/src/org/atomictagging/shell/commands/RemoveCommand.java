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

import org.atomictagging.core.accessors.DbRemover;
import org.atomictagging.shell.IShell;

/**
 * Command to delete atoms or molecules from the database.
 * 
 * @author Stephan Mann
 */
public class RemoveCommand extends AbstractModifyCommand {

	/**
	 * @param shell
	 */
	public RemoveCommand( IShell shell ) {
		super( shell );
	}


	@Override
	public String getCommandString() {
		return "rm";
	}


	@Override
	public String getHelpMessage() {
		return "rm <a/m> <ID>\t- Remove an atom or a molecule";
	}


	@Override
	protected int handleAtom( long id, PrintStream stdout ) {
		DbRemover.removeAtom( id );
		return 0;
	}


	@Override
	protected int handleMolecule( long id, PrintStream stdout ) {
		DbRemover.removeMolecule( id );
		return 0;
	}

}
