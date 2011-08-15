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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.atomictagging.core.configuration.Configuration;
import org.atomictagging.core.services.ATService;
import org.atomictagging.core.types.Atom;
import org.atomictagging.core.types.Atom.AtomBuilder;
import org.atomictagging.core.types.CoreTypes;
import org.atomictagging.core.types.IAtom;
import org.atomictagging.core.types.IMolecule;
import org.atomictagging.core.types.Molecule;
import org.atomictagging.core.types.Molecule.MoleculeBuilder;
import org.atomictagging.utils.FileUtils;
import org.atomictagging.utils.StringUtils;

/**
 * Imports any file into a molecule.<br>
 * <br>
 * This is the generic "catch all" importer for Atomic Tagging. It therefore will always say yes if asked whether it can
 * import a file (see {@link #canHandle(File)} and it will put itself at the end of the {@link MoleculeHandlerFactory}
 * importer chain (see {@link #getOrdinal()}). It is expected that there will be a number of better importers for any
 * given file but if all else fails, this importer will just create a basic molecule with whatever information it can
 * get from the file system.
 */
public class GenericImporter implements IMoleculeImporter {

	@Override
	public boolean canHandle( final File file ) {
		return true;
	}


	@Override
	public int getOrdinal() {
		return Integer.MAX_VALUE;
	}


	@Override
	public String getUniqueId() {
		return "atomictagging-genericimporter";
	}


	@Override
	public void importFile( final Collection<IMolecule> molecules, final File file ) {
		importFile( molecules, file, null );
	}


	@Override
	public void importFile( final Collection<IMolecule> molecules, final File file, final String repository ) {
		boolean isRemote = true;
		String targetDirName = Configuration.getRepository( repository );

		if ( repository != null && targetDirName == null ) {
			System.out.println( "Unkown remote location \"" + repository + "\". Check your config." );
			return;
		}

		if ( targetDirName == null ) {
			targetDirName = Configuration.get().getString( "base.dir" );
			isRemote = false;
		}

		final String fileName = copyFile( file, targetDirName );
		if ( fileName == null ) {
			System.out.println( "Error. No file imported." );
			return;
		}

		final IAtom filename = Atom.build().withData( file.getName() ).withType( "filename" ).buildWithDataAndType();
		final AtomBuilder binRefBuilder = Atom.build().withData( "/" + fileName ).withType( CoreTypes.FILEREF )
				.withType( CoreTypes.FILETYPE_UNKNOWN );

		if ( isRemote ) {
			binRefBuilder.withType( CoreTypes.FILEREF_REMOTE );
		}

		final MoleculeBuilder mBuilder = Molecule.build().withAtom( filename )
				.withAtom( binRefBuilder.buildWithDataAndType() ).withTag( "generic-file" );

		if ( isRemote ) {
			final IAtom remote = Atom.build().withData( repository ).withType( CoreTypes.FILEREF_REMOTE_LOCATION )
					.buildWithDataAndType();
			mBuilder.withAtom( remote );
		}

		final IMolecule molecule = mBuilder.buildWithAtomsAndTags();
		ATService.getMoleculeService().save( molecule );
		molecules.add( molecule );
	}


	/**
	 * @param bytes
	 * @param targetDirName
	 * @return
	 */
	public static String saveFile( final byte[] bytes, final String targetDirName ) {
		System.out.println( "Saving file..." );

		final String hash = DigestUtils.md5Hex( bytes );
		final List<String> pathArray = new ArrayList<String>( 3 );
		pathArray.add( hash.substring( 0, 2 ) );
		pathArray.add( hash.substring( 2, 4 ) );

		final File targetDir = new File( targetDirName + "/" + StringUtils.join( pathArray, "/" ) );

		pathArray.add( hash.substring( 4 ) );

		final File target = new File( targetDirName + "/" + StringUtils.join( pathArray, "/" ) );
		try {
			targetDir.mkdirs();
			target.createNewFile();

			FileUtils.saveFile( bytes, target );

		} catch ( final Exception e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		System.out.println( "Created file: " + target.getAbsolutePath() );
		return StringUtils.join( pathArray, "/" );
	}


	/**
	 * @param file
	 * @param targetDirName
	 * @return The resulting file name the file was copied to without the repository directory
	 */
	public static String copyFile( final File file, final String targetDirName ) {
		System.out.println( "Copying file..." );
		final String hash = FileUtils.getHashSum( file );
		final List<String> pathArray = new ArrayList<String>( 3 );
		pathArray.add( hash.substring( 0, 2 ) );
		pathArray.add( hash.substring( 2, 4 ) );

		final File targetDir = new File( targetDirName + "/" + StringUtils.join( pathArray, "/" ) );

		pathArray.add( hash.substring( 4 ) );

		final File target = new File( targetDirName + "/" + StringUtils.join( pathArray, "/" ) );
		try {
			targetDir.mkdirs();
			target.createNewFile();
			FileUtils.copyFile( file, target );
		} catch ( final Exception e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		System.out.println( "Created file: " + target.getAbsolutePath() );
		return StringUtils.join( pathArray, "/" );
	}

}
