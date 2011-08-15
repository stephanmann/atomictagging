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

import org.atomictagging.core.types.IMolecule;

/**
 * Interface for classes that can export a molecule to the file system.<br>
 * <br>
 * Note: Any implementation that is to be used in an environment must be registered with the
 * {@link MoleculeHandlerFactory}.
 * 
 * @author Stephan Mann
 */
public interface IMoleculeExporter extends IMoleculeHandler {

	/**
	 * Called by the {@link MoleculeHandlerFactory} to ask whether the given molecule can be handled by this exporter.
	 * 
	 * @param molecule
	 * @return true if the molecule can be handled, false otherwise
	 */
	public boolean canHandle( IMolecule molecule );


	/**
	 * Export the given molecule to the given file.
	 * 
	 * @param molecule
	 * @param file
	 */
	public void exportToFile( IMolecule molecule, File file );
}
