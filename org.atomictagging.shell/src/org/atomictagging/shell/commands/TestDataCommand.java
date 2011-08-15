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
import java.util.Arrays;

import org.atomictagging.core.services.ATService;
import org.atomictagging.core.services.IMoleculeService;
import org.atomictagging.core.types.Atom;
import org.atomictagging.core.types.IAtom;
import org.atomictagging.core.types.Molecule;
import org.atomictagging.shell.IShell;

/**
 * A command that inserts some test data to play with into the database.
 * 
 * @author Stephan Mann
 */
public class TestDataCommand extends AbstractCommand {

	/**
	 * @param shell
	 */
	public TestDataCommand( final IShell shell ) {
		super( shell );
	}


	@Override
	public String getCommandString() {
		return "testdata";
	}


	@Override
	public String getHelpMessage() {
		return "testdata\t- Write a set of test data to the database";
	}


	@Override
	public int handleInput( final String input, final PrintStream stdout ) {
		final IMoleculeService service = ATService.getMoleculeService();

		IAtom artist = Atom.build().withData( "A Perfect Circle" ).withType( "artist" ).buildWithDataAndType();
		IAtom title = Atom.build().withData( "The Noose" ).withType( "title" ).buildWithDataAndType();
		IAtom album = Atom.build().withData( "13th Step" ).withType( "album" ).buildWithDataAndType();
		final IAtom rock = Atom.build().withData( "Rock" ).withType( "genre" ).buildWithDataAndType();
		final IAtom metal = Atom.build().withData( "Metal" ).withType( "genre" ).buildWithDataAndType();

		service.save( Molecule.build().withAtoms( Arrays.asList( artist, title, album, rock, metal ) )
				.withTag( "audio" ).withTag( "favorite" ).buildWithAtomsAndTags() );

		title = Atom.build().withData( "The Outsider" ).withType( "title" ).buildWithDataAndType();
		service.save( Molecule.build().withAtoms( Arrays.asList( artist, title, album, rock, metal ) )
				.withTag( "audio" ).buildWithAtomsAndTags() );

		artist = Atom.build().withData( "Led Zeppelin" ).withType( "artist" ).buildWithDataAndType();
		title = Atom.build().withData( "The Battle of Evermore" ).withType( "title" ).buildWithDataAndType();
		album = Atom.build().withData( "Led Zeppelin IV" ).withType( "album" ).buildWithDataAndType();
		service.save( Molecule.build().withAtoms( Arrays.asList( artist, title, album, rock ) ).withTag( "audio" )
				.withTag( "favorite" ).buildWithAtomsAndTags() );

		title = Atom.build().withData( "Stairway to Heaven" ).withType( "title" ).buildWithDataAndType();
		service.save( Molecule.build().withAtoms( Arrays.asList( artist, title, album, rock ) ).withTag( "audio" )
				.buildWithAtomsAndTags() );

		IAtom author1 = Atom.build().withData( "Erich Gamma" ).withType( "author" ).buildWithDataAndType();
		final IAtom author2 = Atom.build().withData( "Richard Helm" ).withType( "author" ).buildWithDataAndType();
		final IAtom author3 = Atom.build().withData( "Ralph Johnson" ).withType( "author" ).buildWithDataAndType();
		final IAtom author4 = Atom.build().withData( "John Vlissides" ).withType( "author" ).buildWithDataAndType();
		IAtom date = Atom.build().withData( "1995" ).withType( "release-year" ).buildWithDataAndType();
		IAtom bookTitle = Atom.build().withData( "Design Patterns: Elements of Reusable Object-Oriented Software" )
				.withType( "title" ).buildWithDataAndType();
		service.save( Molecule.build().withAtoms( Arrays.asList( author1, author2, author3, author4, bookTitle, date ) )
				.withTags( Arrays.asList( "document", "book", "software", "favorite" ) ).buildWithAtomsAndTags() );

		author1 = Atom.build().withData( "Mark Pilgrim" ).withType( "author" ).buildWithDataAndType();
		date = Atom.build().withData( "2004" ).withType( "release-year" ).buildWithDataAndType();
		bookTitle = Atom.build().withData( "Dive Into Python - Python from novice to pro" ).withType( "title" )
				.buildWithDataAndType();
		service.save( Molecule.build().withAtoms( Arrays.asList( author1, date, bookTitle ) )
				.withTags( Arrays.asList( "document", "book", "python" ) ).buildWithAtomsAndTags() );

		return 0;
	}

}
