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

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;

import org.atomictagging.core.moleculehandler.IMoleculeImporter;
import org.atomictagging.core.moleculehandler.MoleculeHandlerFactory;
import org.atomictagging.core.types.IMolecule;
import org.atomictagging.shell.IShell;

/**
 * Command to import files from the file system into Atomic Tagging
 * 
 * @author Stephan Mann
 */
public class ImportCommand extends AbstractCommand {

	/**
	 * @param shell
	 */
	public ImportCommand( IShell shell ) {
		super( shell );
	}


	@Override
	public String getCommandString() {
		return "import";
	}


	@Override
	public String getHelpMessage() {
		return "import <FILE>\t- imports a file from the file system";
	}


	@Override
	public String getVerboseHelpMessage() {
		return getHelpMessage() + "\n"
				+ "\t\t  Use \"import <REPO> <FILE>\" to import to a remote repository as specified in your config";
	}


	@Override
	public int handleInput( String input, PrintStream stdout ) {
		String fileName = input;
		String remoteName = null;
		String[] parts = input.trim().split( " ", 2 );

		if ( parts.length == 2 ) {
			fileName = parts[1];
			remoteName = parts[0];
		} else if ( parts.length == 1 ) {
			fileName = parts[0];
		} else {
			stdout.println( "Invalid parameter count." );
			return 1;
		}

		File file = new File( fileName );
		if ( !file.exists() || !file.canRead() ) {
			stdout.println( "Can't read from given file: " + file.getAbsolutePath() );
			return 1;
		}

		if ( file.isDirectory() ) {
			stdout.println( "Given path points to a directory. Importing directories is not yet supported." );
			return 2;
		}

		IMoleculeImporter importer = MoleculeHandlerFactory.getInstance().getImporter( file );

		if ( remoteName == null ) {
			importer.importFile( new ArrayList<IMolecule>(), file );
		} else {
			importer.importFile( new ArrayList<IMolecule>(), file, remoteName );
		}

		return 0;
	}
}
