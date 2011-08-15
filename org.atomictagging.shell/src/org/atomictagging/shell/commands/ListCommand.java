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
import java.util.ArrayList;
import java.util.List;

import org.atomictagging.core.moleculehandler.IMoleculeViewer;
import org.atomictagging.core.moleculehandler.MoleculeHandlerFactory;
import org.atomictagging.core.moleculehandler.IMoleculeViewer.VERBOSITY;
import org.atomictagging.core.services.ATService;
import org.atomictagging.core.types.IMolecule;
import org.atomictagging.shell.IShell;

/**
 * The implementation of a list command that show molecules according to the given tags
 * 
 * @author Stephan Mann
 */
public class ListCommand extends AbstractCommand {

	/**
	 * @param shell
	 */
	public ListCommand( IShell shell ) {
		super( shell );
	}


	@Override
	public String getCommandString() {
		return "ls";
	}


	@Override
	public String getHelpMessage() {
		return "ls [tag1/tag2]\t- List all molecules (with the given tags)";
	}


	@Override
	public String getVerboseHelpMessage() {
		return getHelpMessage();
	}


	@Override
	public int handleInput( String input, PrintStream stdout ) {
		List<String> tags = new ArrayList<String>();

		String scope = shell.getEnvironment( "scope" );
		scope = ( scope == null ) ? "" : scope;

		if ( input != null && !input.isEmpty() ) {
			scope += "/" + input;
		}

		if ( !scope.isEmpty() ) {
			for ( String tag : scope.split( "/" ) ) {
				if ( !tag.isEmpty() ) {
					tags.add( tag );
				}
			}
		}

		List<IMolecule> molecules = ATService.getMoleculeService().find( tags );

		for ( IMolecule molecule : molecules ) {
			IMoleculeViewer viewer = MoleculeHandlerFactory.getInstance().getViewer( molecule );
			stdout.println( viewer.getTextRepresentation( molecule, MAX_LENGTH, VERBOSITY.DEFAULT ) );
		}
		return 0;
	}

}
