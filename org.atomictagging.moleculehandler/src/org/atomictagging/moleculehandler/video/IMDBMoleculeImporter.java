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
package org.atomictagging.moleculehandler.video;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.atomictagging.core.configuration.Configuration;
import org.atomictagging.core.moleculehandler.GenericImporter;
import org.atomictagging.core.moleculehandler.IMoleculeImporter;
import org.atomictagging.core.moleculehandler.MoleculeHandlerFactory;
import org.atomictagging.core.services.ATService;
import org.atomictagging.core.types.Atom;
import org.atomictagging.core.types.Atom.AtomBuilder;
import org.atomictagging.core.types.CoreTypes;
import org.atomictagging.core.types.IAtom;
import org.atomictagging.core.types.IMolecule;
import org.atomictagging.core.types.Molecule;
import org.atomictagging.core.types.Molecule.MoleculeBuilder;

/**
 * A PoC to show how to write additional importers. This one should be able to lookup movies in IMDB.
 * 
 * @author Stephan Mann
 */
public class IMDBMoleculeImporter implements IMoleculeImporter {

	private final DeanClatworthyParser	PARSER	= new DeanClatworthyParser();


	@Override
	public boolean canHandle( final File file ) {
		final String fileName = file.getName();
		if ( fileName.endsWith( ".avi" ) || fileName.endsWith( ".mpg" ) ) {
			return true;
		}
		return false;
	}


	@Override
	public void importFile( final Collection<IMolecule> molecules, final File file ) {
		importFile( molecules, file, null );
	}


	@Override
	public void importFile( final Collection<IMolecule> molecules, final File file, final String repository ) {
		final IMDBEntry entry = lookUpIMDB( file );
		if ( entry == null ) {
			System.out
					.println( "Failed to retrieve an IMDB entry for this file. Do you want to import this file anyway? [y/N]" );
			if ( askUserYesNo() ) {
				final IMoleculeImporter importer = MoleculeHandlerFactory.getInstance().getNextImporter( file, this );
				importer.importFile( molecules, file );
			}
			return;
		}

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

		final String fileName = GenericImporter.copyFile( file, targetDirName );
		if ( fileName == null ) {
			System.out.println( "Error. No file imported." );
			return;
		}

		final IAtom title = Atom.build().withData( entry.getTitle() ).withType( "title" ).buildWithDataAndType();
		final AtomBuilder binRefBuilder = Atom.build().withData( "/" + fileName ).withType( CoreTypes.FILEREF )
				.withType( CoreTypes.FILETYPE_VIDEO );

		if ( isRemote ) {
			binRefBuilder.withType( CoreTypes.FILEREF_REMOTE );
		}

		final MoleculeBuilder mBuilder = Molecule.build().withAtom( title )
				.withAtom( binRefBuilder.buildWithDataAndType() ).withTag( "video" );

		if ( entry.isSeries() ) {
			mBuilder.withTag( "series" );
		} else {
			mBuilder.withTag( "movie" );
		}

		if ( isRemote ) {
			final IAtom remote = Atom.build().withData( repository ).withType( CoreTypes.FILEREF_REMOTE_LOCATION )
					.buildWithDataAndType();
			mBuilder.withAtom( remote );
		}

		if ( entry.getImdbRating() != null ) {
			final IAtom rating = Atom.build().withData( entry.getImdbRating() ).withType( "IMDB-rating" )
					.buildWithDataAndType();
			mBuilder.withAtom( rating );
		}
		if ( entry.getYear() != null ) {
			final IAtom year = Atom.build().withData( entry.getYear() ).withType( "year" ).buildWithDataAndType();
			mBuilder.withAtom( year );
		}
		if ( entry.getImdbUrl() != null ) {
			final IAtom url = Atom.build().withData( entry.getImdbUrl() ).withType( "url" ).buildWithDataAndType();
			mBuilder.withAtom( url );
		}

		final IMolecule molecule = mBuilder.buildWithAtomsAndTags();
		ATService.getMoleculeService().save( molecule );
		molecules.add( molecule );
	}


	/**
	 * @param file
	 */
	private IMDBEntry lookUpIMDB( final File file ) {
		boolean tryAgain = true;
		String searchString = file.getName();
		IMDBEntry entry = null;

		System.out.println( "Trying to retrieve IMDB entry..." );

		while ( tryAgain ) {
			try {
				entry = PARSER.find( searchString );

				if ( entry.getTitle() == null ) {
					System.out.println( "Searched for \"" + searchString
							+ "\" but didn't find anything. Try again with different string? [y/N]" );
					if ( !askUserYesNo() ) {
						System.out.println( "File was not imported!" );
						return null;
					}

					System.out.println( "Insert new search string to search for: " );
					searchString = readInput();
				} else {
					System.out.println( "Found entry " + entry.getTitle() + " " + entry.getGenres() + "." );
					System.out.println( " Do you want to use that? [y/N]" );

					if ( askUserYesNo() ) {
						tryAgain = false;
					} else {
						System.out.println( "Insert new search string to search for: " );
						searchString = readInput();
					}
				}
			} catch ( final Exception e ) {
				System.out.println( "Can't reach web service to ask for IMDB data (" + e.getClass()
						+ "). Try again? [y/N]" );
				if ( !askUserYesNo() ) {
					System.out.println( "File was not imported!" );
					return null;
				}
			}
		}

		return entry;
	}


	@Override
	public int getOrdinal() {
		return 1;
	}


	@Override
	public String getUniqueId() {
		return "atomictagging-imdbimporter";
	}


	// FIXME: This is copy&paste from the Shell. Find a better solution!!
	private static String readInput() {
		final BufferedReader br = new BufferedReader( new InputStreamReader( System.in ) );
		String input = null;

		try {
			input = br.readLine();
		} catch ( final IOException ioe ) {
			System.err.println( "IO error trying to read user input." );
			System.exit( 1 );
		}

		return input;
	}


	private static boolean askUserYesNo() {
		final String input = readInput();
		if ( !input.equals( "y" ) && !input.equals( "Y" ) ) {
			return false;
		}
		return true;
	}

	private class DeanClatworthyParser {
		private static final String	SERVICE_URL		= "http://www.deanclatworthy.com/imdb/?type=text&q=";

		private static final String	ENTRY_TITLE		= "title";
		private static final String	ENTRY_URL		= "imdburl";
		private static final String	ENTRY_GENRES	= "genres";
		private static final String	ENTRY_RATING	= "rating";
		private static final String	ENTRY_YEAR		= "year";
		private static final String	ENTRY_SERIES	= "series";


		public IMDBEntry find( final String name ) throws Exception {
			IMDBEntry entry = null;
			final String search = name.replace( " ", "+" );
			final BufferedReader reader = read( SERVICE_URL + search );
			entry = parse( reader );
			return entry;
		}


		private BufferedReader read( final String url ) throws Exception {
			return new BufferedReader( new InputStreamReader( new URL( url ).openStream() ) );
		}


		private IMDBEntry parse( final BufferedReader reader ) throws IOException {
			final IMDBEntry entry = new IMDBEntry();

			String line = reader.readLine();
			while ( line != null ) {
				if ( line.startsWith( ENTRY_TITLE ) ) {
					entry.setTitle( getValue( line ) );
				} else if ( line.startsWith( ENTRY_URL ) ) {
					entry.setImdbUrl( getValue( line ) );
				} else if ( line.startsWith( ENTRY_GENRES ) ) {
					final String genres = getValue( line );
					final List<String> genreList = Arrays.asList( genres.split( "," ) );
					entry.setGenres( genreList );
				} else if ( line.startsWith( ENTRY_RATING ) ) {
					entry.setImdbRating( getValue( line ) );
				} else if ( line.startsWith( ENTRY_YEAR ) ) {
					entry.setYear( getValue( line ) );
				} else if ( line.startsWith( ENTRY_SERIES ) ) {
					entry.setSeries( Boolean.parseBoolean( getValue( line ) ) );
				}

				line = reader.readLine();
			}
			return entry;
		}


		private String getValue( final String line ) {
			final int index = line.indexOf( "|" );
			return line.substring( index + 1 );
		}

	}

	/**
	 * Just a bean to encapsulate the IMDB result.
	 */
	private class IMDBEntry {
		private String			title;
		private String			imdbUrl;
		private List<String>	genres;
		private String			imdbRating;
		private String			year;
		private boolean			series;


		/**
		 * @return title
		 */
		public String getTitle() {
			return title;
		}


		/**
		 * @param title
		 */
		public void setTitle( final String title ) {
			this.title = title;
		}


		/**
		 * @return IMDB link
		 */
		public String getImdbUrl() {
			return imdbUrl;
		}


		/**
		 * @param imdbUrl
		 */
		public void setImdbUrl( final String imdbUrl ) {
			this.imdbUrl = imdbUrl;
		}


		/**
		 * @return genres
		 */
		public List<String> getGenres() {
			return genres;
		}


		/**
		 * @param genres
		 */
		public void setGenres( final List<String> genres ) {
			this.genres = genres;
		}


		/**
		 * @return IMDB rating
		 */
		public String getImdbRating() {
			return imdbRating;
		}


		/**
		 * @param imdbRating
		 */
		public void setImdbRating( final String imdbRating ) {
			this.imdbRating = imdbRating;
		}


		/**
		 * @return year
		 */
		public String getYear() {
			return year;
		}


		/**
		 * @param year
		 */
		public void setYear( final String year ) {
			this.year = year;
		}


		/**
		 * @return whether this is a series
		 */
		public boolean isSeries() {
			return series;
		}


		/**
		 * @param series
		 */
		public void setSeries( final boolean series ) {
			this.series = series;
		}
	}
}
