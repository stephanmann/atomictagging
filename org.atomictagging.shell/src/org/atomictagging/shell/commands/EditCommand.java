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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.atomictagging.core.accessors.DbModifier;
import org.atomictagging.core.configuration.Configuration;
import org.atomictagging.core.services.ATService;
import org.atomictagging.core.types.IAtom;
import org.atomictagging.core.types.IMolecule;
import org.atomictagging.core.types.Molecule.MoleculeBuilder;
import org.atomictagging.shell.IShell;
import org.atomictagging.shell.swingeditor.AtomicTaggingEditor;
import org.atomictagging.utils.StringUtils;

/**
 * A command to edit atoms and molecules.
 * 
 * @author Stephan Mann
 */
public class EditCommand extends AbstractModifyCommand {

	/**
	 * @param shell
	 */
	public EditCommand( final IShell shell ) {
		super( shell );
	}


	@Override
	public String getCommandString() {
		return "edit";
	}


	@Override
	public String getHelpMessage() {
		return "edit <a/m> <ID>\t- Edit an atom or a molecule";
	}


	@Override
	public String getVerboseHelpMessage() {
		return getHelpMessage() + "\n" + "\t\t  Example 'edit atom 2' to edit the atom with ID 2.";
	}


	@Override
	protected int handleAtom( final long id, final PrintStream stdout ) {
		final IAtom atom = ATService.getAtomService().find( id );

		if ( atom == null ) {
			stdout.println( "No atom found with the given ID " + id );
			return 1;
		}

		final File temp = writeAtomTempFile( atom );

		// This can't happen, can it?
		if ( temp == null ) {
			stdout.println( "Failed to create temporarry file." );
			return 1;
		}

		openEditorAndWait( temp );

		final IAtom a = getAtomFromFile( atom, temp );
		DbModifier.modify( a );

		temp.delete();
		return 0;
	}


	/**
	 * @param atom
	 * @param temp
	 * @return
	 */
	static IAtom getAtomFromFile( final IAtom atom, final File temp ) {
		final StringBuilder data = new StringBuilder();
		final List<String> types = new ArrayList<String>();

		try {
			final BufferedReader reader = new BufferedReader( new FileReader( temp ) );
			String line = null;
			boolean startData = false;

			while ( ( line = reader.readLine() ) != null ) {
				if ( line.startsWith( "Tags:" ) ) {
					final String[] newTags = line.substring( 5 ).split( "," );
					for ( final String tag : newTags ) {
						types.add( tag.trim() );
					}
					continue;
				}

				if ( !startData && line.startsWith( "Data:" ) ) {
					startData = true;
					if ( !"".equals( line.substring( 5 ).trim() ) ) {
						data.append( line.substring( 5 ) );
						data.append( System.getProperty( "line.separator" ) );
					}
					continue;
				}

				if ( startData ) {
					data.append( line );
					data.append( System.getProperty( "line.separator" ) );
				}
			}
		} catch ( final FileNotFoundException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch ( final IOException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		atom.setData( data.toString() );
		atom.setTypes( types );
		return atom;
	}


	/**
	 * @param temp
	 */
	static void openEditorAndWait( final File temp ) {
		final String editor = Configuration.get().getString( "base.editor" );
		if ( editor == null || editor.isEmpty() ) {
			openBuildInEditor( temp );
		} else {
			openExternalEditor( editor, temp );
		}
	}


	private static void openBuildInEditor( final File temp ) {
		final CountDownLatch loginSignal = new CountDownLatch( 1 );
		new AtomicTaggingEditor( temp, loginSignal );
		try {
			loginSignal.await();
		} catch ( final InterruptedException e1 ) {
			e1.printStackTrace();
		}
	}


	private static void openExternalEditor( final String executable, final File temp ) {
		final ProcessBuilder pb = new ProcessBuilder( executable, temp.getAbsolutePath() );
		try {
			final Process p = pb.start();
			p.waitFor();
		} catch ( final IOException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch ( final InterruptedException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	private File writeAtomTempFile( final IAtom atom ) {
		return writeAtomTempFile( atom.getTypes(), atom.getData() );
	}


	/**
	 * @param atom
	 * @param temp
	 * @return
	 */
	static File writeAtomTempFile( final List<String> tags, final String data ) {
		File temp = null;
		BufferedWriter writer = null;
		try {
			temp = File.createTempFile( "atomictagging", ".tmp" );
			writer = new BufferedWriter( new FileWriter( temp ) );
			writer.write( "Tags: " + StringUtils.join( tags, ", " ) );
			writer.write( "\nData:\n" + data );
		} catch ( final IOException x ) {
			System.err.println( x );
		} finally {
			if ( writer != null ) {
				try {
					writer.flush();
					writer.close();
				} catch ( final IOException ignore ) {
				}
			}

		}
		return temp;
	}


	@Override
	protected int handleMolecule( final long id, final PrintStream stdout ) {
		IMolecule molecule = null;
		molecule = ATService.getMoleculeService().find( id );

		if ( molecule == null ) {
			stdout.println( "No molecule found with the given ID " + id );
			return 1;
		}

		File temp = null;
		BufferedWriter writer = null;
		try {
			temp = File.createTempFile( "atomictagging", ".tmp" );
			writer = new BufferedWriter( new FileWriter( temp ) );
			writer.write( "Tags: " + StringUtils.join( molecule.getTags(), ", " ) );
			writer.write( "\nAtoms: " );

			final List<String> atomIds = new ArrayList<String>();
			for ( final IAtom atom : molecule.getAtoms() ) {
				atomIds.add( String.valueOf( atom.getId() ) );
			}

			writer.write( StringUtils.join( atomIds, ", " ) );
		} catch ( final IOException x ) {
			System.err.println( x );
		} finally {
			if ( writer != null ) {
				try {
					writer.flush();
					writer.close();
				} catch ( final IOException ignore ) {
				}
			}

		}

		// This can't happen, can it?
		if ( temp == null ) {
			stdout.println( "Failed to create temporarry file." );
			return 1;
		}

		openEditorAndWait( temp );

		final List<String> tags = new ArrayList<String>();
		final List<String> atoms = new ArrayList<String>();

		try {
			final BufferedReader reader = new BufferedReader( new FileReader( temp ) );
			String line = null;

			while ( ( line = reader.readLine() ) != null ) {
				if ( line.startsWith( "Tags:" ) ) {
					final String[] newTags = line.substring( 5 ).split( "," );
					for ( final String tag : newTags ) {
						tags.add( tag.trim() );
					}
					continue;
				}

				if ( line.startsWith( "Atoms:" ) ) {
					final String[] newAtoms = line.substring( 6 ).split( "," );
					for ( final String atom : newAtoms ) {
						atoms.add( atom.trim() );
					}
					continue;
				}
			}
		} catch ( final FileNotFoundException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch ( final IOException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		final MoleculeBuilder mBuilder = molecule.modify();
		mBuilder.replaceTags( tags );
		mBuilder.deleteAtoms();

		for ( final String atomId : atoms ) {
			try {
				final IAtom atom = ATService.getAtomService().find( Long.parseLong( atomId ) );

				if ( atom == null ) {
					stdout.println( "Unknown atom with ID: " + atomId );
					return 1;
				}

				mBuilder.withAtom( atom );
			} catch ( final NumberFormatException e ) {
				e.printStackTrace();
			} finally {
				temp.delete();
			}
		}

		DbModifier.modify( mBuilder.buildWithAtomsAndTags() );

		return 0;
	}

}
