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
package org.atomictagging.moleculehandler.image;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Collection;

import org.atomictagging.core.configuration.Configuration;
import org.atomictagging.core.moleculehandler.GenericImporter;
import org.atomictagging.core.moleculehandler.IMoleculeImporter;
import org.atomictagging.core.services.ATService;
import org.atomictagging.core.types.Atom;
import org.atomictagging.core.types.CoreTypes;
import org.atomictagging.core.types.IAtom;
import org.atomictagging.core.types.IMolecule;
import org.atomictagging.core.types.Molecule;
import org.atomictagging.core.types.Molecule.MoleculeBuilder;

/**
 * @author Alexander Oros
 */
public class ImageMoleculeImporter implements IMoleculeImporter {

	@Override
	public String getUniqueId() {
		return "atomictagging-imageimporter";
	}


	@Override
	public int getOrdinal() {
		return 1;
	}


	@Override
	public boolean canHandle( final File file ) {
		final String fileName = file.getName();
		if ( fileName.endsWith( ".jpg" ) || fileName.endsWith( ".gif" ) ) {
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

		// targetDirName = repository
		// fileName = 79/8b/498c975f328ec67ec3f76d7d423b
		final String fileNameIamge = GenericImporter.copyFile( file, targetDirName );
		if ( fileNameIamge == null ) {
			System.out.println( "Error. No file imported." );
			return;
		}

		String fileNameImageThumb = "";
		try {
			final byte[] thumb = transform( file, 200, 200 );
			fileNameImageThumb = GenericImporter.saveFile( thumb, targetDirName );
		} catch ( final Exception e ) {
			e.printStackTrace();
		}

		final IAtom atomImage = Atom.build().withData( "/" + fileNameIamge ).withType( CoreTypes.FILETYPE_IMAGE )
				.buildWithDataAndType();
		final IAtom atomImageThumb = Atom.build().withData( "/" + fileNameImageThumb )
				.withType( CoreTypes.FILETYPE_IMAGE ).withType( "thumb" ).buildWithDataAndType();

		final MoleculeBuilder mBuilder = Molecule.build().withAtom( atomImage ).withAtom( atomImageThumb );
		mBuilder.withTag( "generic-file" );

		final IMolecule molecule = mBuilder.buildWithAtomsAndTags();
		ATService.getMoleculeService().save( molecule );
		molecules.add( molecule );
	}


	private static byte[] transform( final File originalFile, int thumbWidth, int thumbHeight ) throws Exception {
		final BufferedImage image = javax.imageio.ImageIO.read( originalFile );

		final double thumbRatio = (double) thumbWidth / (double) thumbHeight;
		final int imageWidth = image.getWidth( null );
		final int imageHeight = image.getHeight( null );
		final double imageRatio = (double) imageWidth / (double) imageHeight;
		if ( thumbRatio < imageRatio ) {
			thumbHeight = (int) ( thumbWidth / imageRatio );
		} else {
			thumbWidth = (int) ( thumbHeight * imageRatio );
		}

		if ( imageWidth < thumbWidth && imageHeight < thumbHeight ) {
			thumbWidth = imageWidth;
			thumbHeight = imageHeight;
		} else if ( imageWidth < thumbWidth ) {
			thumbWidth = imageWidth;
		} else if ( imageHeight < thumbHeight ) {
			thumbHeight = imageHeight;
		}

		final BufferedImage thumbImage = new BufferedImage( thumbWidth, thumbHeight, BufferedImage.TYPE_INT_RGB );
		final Graphics2D graphics2D = thumbImage.createGraphics();
		graphics2D.setBackground( Color.WHITE );
		graphics2D.setPaint( Color.WHITE );
		graphics2D.fillRect( 0, 0, thumbWidth, thumbHeight );
		graphics2D.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR );
		graphics2D.drawImage( image, 0, 0, thumbWidth, thumbHeight, null );

		final ByteArrayOutputStream baos = new ByteArrayOutputStream();

		javax.imageio.ImageIO.write( thumbImage, "JPG", baos );

		return baos.toByteArray();
	}
}
