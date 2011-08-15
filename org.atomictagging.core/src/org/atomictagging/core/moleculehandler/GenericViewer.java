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

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.atomictagging.core.configuration.Configuration;
import org.atomictagging.core.types.CoreTypes;
import org.atomictagging.core.types.IAtom;
import org.atomictagging.core.types.IMolecule;
import org.atomictagging.utils.StringUtils;

/**
 * Views any given molecule.<br>
 * <br>
 * This is the generic "catch all" viewer for Atomic Tagging. It therefore will always say yes if asked whether it can
 * view a molecule (see {@link #canHandle(IMolecule)} and it will put itself at the end of the
 * {@link MoleculeHandlerFactory} viewer chain (see {@link #getOrdinal()}). It is expected that there will be a number
 * of better viewers for any given file but if all else fails, this viewer will try to give an informative
 * representation of the molecule.
 */
public class GenericViewer implements IMoleculeViewer {

	@Override
	public boolean canHandle( final IMolecule molecule ) {
		return true;
	}


	@Override
	public int getOrdinal() {
		return Integer.MAX_VALUE;
	}


	@Override
	public String getUniqueId() {
		return "atomictagging-genericviewer";
	}

	/**
	 * To handle molecules in a generic way, we need to specify some sort of order for what atom we want to display
	 * first.
	 */
	private static final List<String>	DEFAULT_TAGS	= new ArrayList<String>();
	static {
		DEFAULT_TAGS.add( "title" );
		DEFAULT_TAGS.add( "filename" );
		DEFAULT_TAGS.add( "name" );
	}

	// Default length for the data columns.
	private final int					ID_LENGTH		= 6;
	private final int					TAG_LENGTH		= 32;
	private final int					DATA_MIN_LENGTH	= 10;


	@Override
	public String getTextRepresentation( final IMolecule molecule, final int length, final VERBOSITY verbosity ) {
		int remainingLength = length - ID_LENGTH - TAG_LENGTH - 3; // white spaces

		if ( remainingLength < DATA_MIN_LENGTH ) {
			remainingLength = DATA_MIN_LENGTH;
		}

		final String format = " %" + ID_LENGTH + "d %-" + TAG_LENGTH + "s %-" + remainingLength + "s";

		switch ( verbosity ) {
		case DEFAULT:
			final String tags = StringUtils.cut( molecule.getTags().toString(), TAG_LENGTH );
			final String data = StringUtils.cut( defaultVerbosity( molecule ), remainingLength );
			return String.format( format, molecule.getId(), tags,
					data.replace( System.getProperty( "line.separator" ), " " ) );

		case VERBOSE:
			final StringBuilder builder = new StringBuilder();

			final String mTags = StringUtils.cut( molecule.getTags().toString(), length - ID_LENGTH - 2 );
			builder.append( String.format( " %" + ID_LENGTH + "d %s\n", molecule.getId(), mTags ) );
			builder.append( " " + StringUtils.repeat( "-", length - 1 ) + System.getProperty( "line.separator" ) );

			for ( final IAtom atom : molecule.getAtoms() ) {
				final String aTags = StringUtils.cut( atom.getTypes().toString(), TAG_LENGTH );
				final String aData = StringUtils.cut( atom.getData(), remainingLength );

				builder.append( String.format( format, atom.getId(), aTags,
						aData.replace( System.getProperty( "line.separator" ), " " ) ) );
				builder.append( System.getProperty( "line.separator" ) );
			}

			builder.deleteCharAt( builder.length() - 1 );
			return builder.toString();
		default:
			return "Unhandled verbosity level.";
		}
	}


	private String defaultVerbosity( final IMolecule molecule ) {
		String data = null;

		// Check whether the molecule contains a atom with a tag that we know to be important.
		for ( final String defaultTag : DEFAULT_TAGS ) {
			for ( final IAtom atom : molecule.getAtoms() ) {
				for ( final String type : atom.getTypes() ) {
					if ( defaultTag.equals( type ) ) {
						data = atom.getData();
					}
				}
			}
		}

		// Fallback
		if ( data == null ) {
			final List<String> dataList = new ArrayList<String>();
			for ( final IAtom atom : molecule.getAtoms() ) {
				dataList.add( atom.getData() );
			}
			data = StringUtils.join( dataList, "; " );
		}

		return data;
	}


	@Override
	public void showMolecule( final IMolecule molecule ) {
		for ( final IAtom atom : molecule.getAtoms() ) {
			if ( atom.getTypes().contains( CoreTypes.FILEREF ) ) {
				try {
					final Desktop dt = Desktop.getDesktop();
					dt.open( new File( Configuration.get().getString( "base.dir" ) + atom.getData() ) );
				} catch ( final IOException e ) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
