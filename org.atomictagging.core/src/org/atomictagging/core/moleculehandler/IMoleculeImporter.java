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
package org.atomictagging.core.moleculehandler;

import java.io.File;
import java.util.Collection;

import org.atomictagging.core.types.IMolecule;

/**
 * Interface for classes that can import a file or directory into the Atomic Tagging environment.<br>
 * <br>
 * Note: Any implementation that is to be used in an environment must be registered with the
 * {@link MoleculeHandlerFactory}.
 * 
 * @author Stephan Mann
 */
public interface IMoleculeImporter extends IMoleculeHandler {

	/**
	 * Called by the {@link MoleculeHandlerFactory} to ask whether the given file or directory can be handled by this
	 * importer. The {@link MoleculeHandlerFactory} will assure that the file or directory exists and is readable before
	 * asking.
	 * 
	 * @param file
	 *            A file or directory
	 * @return true if this file can be handled, false otherwise
	 */
	public boolean canHandle( File file );


	/**
	 * Read the given file or directory from the file system and create molecules representing the file(s). After return
	 * of this method, the file(s) has been copied into the Atomic Tagging environment and the molecule has been written
	 * to the database.
	 * 
	 * @param molecules
	 *            The given collection will be filed with whatever molecules where created
	 * @param file
	 *            Source to read from
	 */
	public void importFile( Collection<IMolecule> molecules, File file );


	/**
	 * Same as {@link #importFile(Collection, File)} with the possibility to specify a repository. The importer will
	 * treat an explicit specified repository as remote repository and attach the appropriate tags to the atoms.
	 * 
	 * @param molecules
	 * @param file
	 * @param repository
	 *            The repository the file will be copied to.
	 */
	public void importFile( Collection<IMolecule> molecules, File file, String repository );
}
