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
package org.atomictagging.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * Some helper methods for handling files
 * 
 * TODO Check whether this is still appropriate or a switch to Apache commons would be in order.
 */
public class FileUtils {

	/**
	 * Copy a file
	 * 
	 * @param source
	 * @param target
	 */
	public static void copyFile( final File source, final File target ) {
		if ( !source.exists() || !source.canRead() ) {
			throw new IllegalArgumentException( "Can't read from given source file: " + source.getAbsolutePath() );
		}
		if ( !target.exists() ) {
			try {
				if ( !target.createNewFile() ) {
					throw new IllegalArgumentException( "Can't write to given target file: " + target.getAbsolutePath() );
				}
			} catch ( final IOException e ) {
				throw new IllegalArgumentException( "Can't write to given target file: " + target.getAbsolutePath(), e );
			}
		}

		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			fis = new FileInputStream( source );
			fos = new FileOutputStream( target );
		} catch ( final FileNotFoundException ignore ) {
			// Was checked previously.
		}

		if ( fis == null || fos == null ) {
			throw new RuntimeException( "Failed to create file streams." );
		}

		try {
			final byte[] buf = new byte[1024];
			int i = 0;
			while ( ( i = fis.read( buf ) ) != -1 ) {
				fos.write( buf, 0, i );
			}
		} catch ( final IOException e ) {
			// FIXME
			throw new RuntimeException( "Failed to copy file.", e );
		} finally {
			try {
				fis.close();
				fos.close();
			} catch ( final IOException e ) {
				// Nothing we can do, or is there?
				e.printStackTrace();
			}
		}
	}


	/**
	 * save an array of bytes
	 * 
	 * @param bytes
	 * @param target
	 */
	public static void saveFile( final byte[] bytes, final File target ) {
		if ( !target.exists() ) {
			try {
				if ( !target.createNewFile() ) {
					throw new IllegalArgumentException( "Can't write to given target file: " + target.getAbsolutePath() );
				}
			} catch ( final IOException e ) {
				throw new IllegalArgumentException( "Can't write to given target file: " + target.getAbsolutePath(), e );
			}
		}

		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream( target );
		} catch ( final FileNotFoundException ignore ) {
			// Was checked previously.
		}

		if ( fos == null ) {
			throw new RuntimeException( "Failed to create file streams." );
		}

		try {
			fos.write( bytes, 0, bytes.length );

		} catch ( final IOException e ) {
			// FIXME
			throw new RuntimeException( "Failed to copy file.", e );
		} finally {
			try {
				fos.close();
			} catch ( final IOException e ) {
				// Nothing we can do, or is there?
				e.printStackTrace();
			}
		}
	}


	public static String getHashSum( final File file ) {
		String hash = null;
		try {
			final FileInputStream fis = new FileInputStream( file );
			hash = DigestUtils.md5Hex( fis );
			fis.close();
		} catch ( final FileNotFoundException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch ( final IOException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return hash;
	}


	public static byte[] loadImage( final File file ) throws IOException {
		byte[] bytes = null;

		final FileInputStream fileInputStream = new FileInputStream( file );
		bytes = new byte[(int) file.length()];
		fileInputStream.read( bytes );
		fileInputStream.close();

		return bytes;
	}
}
