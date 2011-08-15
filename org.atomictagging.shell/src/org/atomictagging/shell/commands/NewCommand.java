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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.atomictagging.core.accessors.DbModifier;
import org.atomictagging.core.services.ATService;
import org.atomictagging.core.types.Atom;
import org.atomictagging.core.types.IAtom;
import org.atomictagging.core.types.IMolecule;
import org.atomictagging.core.types.Molecule;
import org.atomictagging.core.types.Molecule.MoleculeBuilder;
import org.atomictagging.shell.IShell;

/**
 * @author tokei
 * 
 */
public class NewCommand extends AbstractModifyCommand {

	private final List<IAtom>	atoms	= new ArrayList<IAtom>();


	/**
	 * @param shell
	 */
	public NewCommand( final IShell shell ) {
		super( shell );
	}


	@Override
	public String getCommandString() {
		return "new";
	}


	@Override
	public String getHelpMessage() {
		return "new <a/m>\t- Create new atoms and join them into a new molecule";
	}


	@Override
	public int handleInput( final String input, final PrintStream stdout ) {
		final String[] parts = input.trim().split( " ", 2 );
		String type = null;
		long id = 0;

		if ( parts.length == 2 ) {
			type = parts[0];
			id = Integer.parseInt( parts[1] );
		} else if ( parts.length == 1 ) {
			type = parts[0];
		} else {
			stdout.println( "Please specify type (atom, molecule)." );
			return 1;
		}

		if ( !type.equals( "atom" ) && !type.equals( "molecule" ) ) {
			stdout.println( "Please specify valid type: atom or molecule." );
			return 1;
		}

		if ( type.equals( "atom" ) ) {
			// FIXME Misuse of the id parameter to pass the *molecule* id!
			return handleAtom( id, stdout );
		}
		return handleMolecule( 0, stdout );
	}


	@Override
	protected int handleAtom( final long id, final PrintStream stdout ) {
		IMolecule molecule = null;

		if ( id != 0 ) {
			molecule = ATService.getMoleculeService().find( id );

			if ( molecule == null ) {
				stdout.println( "No molecule found with the given ID " + id );
				return 1;
			}
		}

		final File temp = EditCommand.writeAtomTempFile( Arrays.asList( "orphan" ), "" );
		EditCommand.openEditorAndWait( temp );
		final IAtom atom = EditCommand.getAtomFromFile( new Atom(), temp );
		temp.delete();

		if ( molecule != null ) {
			// IAtom atom = builder.buildWithDataAndTag();
			try {
				final List<Long> ids = ATService.getAtomService().save( Arrays.asList( atom ) );
				// atom = builder.withId( ids.get( 0 ) ).buildWithDataAndTag();
				atom.setId( ids.get( 0 ) );
				DbModifier.modify( molecule.modify().withAtom( atom ).buildWithAtomsAndTags() );
			} catch ( final SQLException e ) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			atoms.add( atom );
			stdout.println( atoms.size()
					+ " new atom(s) cached. Use \"new molecule\" to create a molecule containing them." );
		}
		return 0;
	}


	@Override
	protected int handleMolecule( final long id, final PrintStream stdout ) {
		if ( atoms.size() == 0 ) {
			stdout.println( "Create atoms first via \"new atom\"." );
			return 1;
		}

		final MoleculeBuilder builder = Molecule.build().withAtoms( atoms ).withTag( "created-via-shell" );
		ATService.getMoleculeService().save( builder.buildWithAtomsAndTags() );
		atoms.clear();
		return 0;
	}

}
