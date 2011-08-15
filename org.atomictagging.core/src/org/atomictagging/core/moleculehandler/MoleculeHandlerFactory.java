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
import java.util.Map;
import java.util.TreeMap;

import org.atomictagging.core.types.IMolecule;

/**
 * Factory to register and retrieve molecule handlers.<br>
 * <br>
 * This is the molecule handler store where molecule handlers are managed. Molecule handlers can be registered with the
 * factory using their own build in ordinal or a custom ordinal depending on the method used. Upon request, the factory
 * will ask all registered handlers in order of their ordinal whether or not it can handle a molecule. It will return a
 * handler that says it can. Since there are generic catch all handlers provided by the library, there will always be a
 * handler returned.
 * 
 * @author Stephan Mann
 */
public class MoleculeHandlerFactory {

	private final Map<Integer, IMoleculeViewer>		viewers		= new TreeMap<Integer, IMoleculeViewer>();
	private final Map<Integer, IMoleculeExporter>	exporters	= new TreeMap<Integer, IMoleculeExporter>();
	private final Map<Integer, IMoleculeImporter>	importers	= new TreeMap<Integer, IMoleculeImporter>();

	private static MoleculeHandlerFactory			instance	= null;


	private MoleculeHandlerFactory() {
		// Register default molecule handler so the factories getters will always have something to return.
		importers.put( Integer.MAX_VALUE, new GenericImporter() );
		viewers.put( Integer.MAX_VALUE, new GenericViewer() );
	}


	/**
	 * Singleton
	 * 
	 * @return The instance of the handler factory
	 */
	public static MoleculeHandlerFactory getInstance() {
		if ( instance == null ) {
			instance = new MoleculeHandlerFactory();
		}
		return instance;
	}


	/**
	 * Get a viewer that is capable of handling the given molecule.<br>
	 * <br>
	 * The factory will not decide this on its own but ask all registered viewers in order of the ordinal they were
	 * registered under whether they can handle the given molecule.
	 * 
	 * @param molecule
	 * @return A viewer which can handle the given molecule
	 */
	public IMoleculeViewer getViewer( IMolecule molecule ) {
		IMoleculeViewer result = null;

		for ( IMoleculeViewer viewer : viewers.values() ) {
			if ( viewer.canHandle( molecule ) ) {
				result = viewer;
				break;
			}
		}

		return result;
	}


	/**
	 * Get a viewer that is capable of handling the given molecule but ignore the viewer that was given as second
	 * parameter.
	 * 
	 * @param molecule
	 * @param notThisViewer
	 * @return A viewer which can handle the given molecule
	 */
	public IMoleculeViewer getNextViewer( final IMolecule molecule, final IMoleculeViewer notThisViewer ) {
		IMoleculeViewer result = null;

		for ( IMoleculeViewer viewer : viewers.values() ) {
			if ( viewer != notThisViewer && viewer.canHandle( molecule ) ) {
				result = viewer;
				break;
			}
		}

		return result;
	}


	/**
	 * Get an exporter that is capable of handling the given molecule.<br>
	 * <br>
	 * The factory will not decide this on its own but ask all registered exporters in order of the ordinal they were
	 * registered under whether they can handle the given molecule.
	 * 
	 * @param molecule
	 * @return An exporter which can handle the given molecule
	 */
	public IMoleculeExporter getExporter( IMolecule molecule ) {
		IMoleculeExporter result = null;

		for ( IMoleculeExporter exporter : exporters.values() ) {
			if ( exporter.canHandle( molecule ) ) {
				result = exporter;
				break;
			}
		}

		return result;
	}


	/**
	 * Get an importer that is capable of handling the given file.<br>
	 * <br>
	 * The factory will not decide this on its own but ask all registered importers in order of the ordinal they were
	 * registered under whether they can handle the given file. The factory will however assure that the given file
	 * exists and is readable before asking the importers.
	 * 
	 * @param file
	 * @return An importer which can handle the given file
	 */
	public IMoleculeImporter getImporter( File file ) {
		if ( !file.exists() || !file.canRead() ) {
			throw new IllegalArgumentException( "Given file <" + file.getAbsolutePath()
					+ "> doesn't exist or is not readable." );
		}

		IMoleculeImporter result = null;

		for ( IMoleculeImporter importer : importers.values() ) {
			if ( importer.canHandle( file ) ) {
				result = importer;
				break;
			}
		}

		return result;
	}


	/**
	 * Get an importer that is capable of handling the given file but ignore the importer given as a second parameter.
	 * 
	 * @param file
	 * @param notThisImporter
	 * @return An importer which can handle the given file
	 * @see #getImporter(File)
	 */
	public IMoleculeImporter getNextImporter( File file, IMoleculeImporter notThisImporter ) {
		if ( !file.exists() || !file.canRead() ) {
			throw new IllegalArgumentException( "Given file <" + file.getAbsolutePath()
					+ "> doesn't exist or is not readable." );
		}

		IMoleculeImporter result = null;

		for ( IMoleculeImporter importer : importers.values() ) {
			if ( importer != notThisImporter && importer.canHandle( file ) ) {
				result = importer;
				break;
			}
		}

		return result;
	}


	/**
	 * Register a molecule viewer with the factory. The viewers ordinal will be used.
	 * 
	 * @param viewer
	 */
	public void registerViewer( IMoleculeViewer viewer ) {
		validateOrdinal( viewer.getOrdinal() );
		viewers.put( viewer.getOrdinal(), viewer );
	}


	/**
	 * Register a molecule viewer with the factory. The provided ordinal will be used in preference of the viewers own
	 * ordinal.
	 * 
	 * @param viewer
	 * @param ordinal
	 */
	public void registerViewer( IMoleculeViewer viewer, int ordinal ) {
		validateOrdinal( ordinal );
		viewers.put( ordinal, viewer );
	}


	/**
	 * Register a molecule exporter with the factory. The viewers ordinal will be used.
	 * 
	 * @param exporter
	 */
	public void registerExporter( IMoleculeExporter exporter ) {
		validateOrdinal( exporter.getOrdinal() );
		exporters.put( exporter.getOrdinal(), exporter );
	}


	/**
	 * Register a molecule exporter with the factory. The provided ordinal will be used in preference of the exporters
	 * own ordinal.
	 * 
	 * @param exporter
	 * @param ordinal
	 */
	public void registerExporter( IMoleculeExporter exporter, int ordinal ) {
		validateOrdinal( ordinal );
		exporters.put( ordinal, exporter );
	}


	/**
	 * Register a molecule importer with the factory. The viewers ordinal will be used.
	 * 
	 * @param importer
	 */
	public void registerImporter( IMoleculeImporter importer ) {
		validateOrdinal( importer.getOrdinal() );
		importers.put( importer.getOrdinal(), importer );
	}


	/**
	 * Register a molecule importer with the factory. The provided ordinal will be used in preference of the importers
	 * own ordinal.
	 * 
	 * @param importer
	 * @param ordinal
	 */
	public void registerImporter( IMoleculeImporter importer, int ordinal ) {
		validateOrdinal( ordinal );
		importers.put( ordinal, importer );
	}


	private void validateOrdinal( int ordinal ) {
		if ( ordinal == Integer.MAX_VALUE ) {
			throw new IllegalArgumentException(
					"INTEGER.MAX_VALUE is reserved for the default handlers and must not be used." );
		}
	}
}
