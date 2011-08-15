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
package org.atomictagging.shell.swingeditor;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * A build-in platform agnostic editor to fall back to if the user doesn't provide an editor via the configuration.
 * 
 * @author Oros
 */
public class AtomicTaggingEditor extends JFrame {
	private static final long		serialVersionUID	= 1L;

	private JTextArea				editArea			= null;

	private final Action			saveAction			= new SaveAction();
	private final Action			exitAction			= new ExitAction();

	private final File				file;
	private final CountDownLatch	latch;


	/**
	 * Creates and starts a new editor.
	 * 
	 * @param file
	 *            The file the editor shall display
	 * @param latch
	 *            A latch the application can wait for as it will be counted down once during editor shutdown.
	 */
	public AtomicTaggingEditor( File file, CountDownLatch latch ) {
		this.file = file;
		this.latch = latch;
		init();
	}


	private void init() {
		editArea = new JTextArea( 15, 80 );
		editArea.setBorder( BorderFactory.createEmptyBorder( 2, 2, 2, 2 ) );
		editArea.setFont( new Font( "monospaced", Font.PLAIN, 14 ) );
		JScrollPane scrollingText = new JScrollPane( editArea );

		JPanel content = new JPanel();
		content.setLayout( new BorderLayout() );
		content.add( scrollingText, BorderLayout.CENTER );

		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = menuBar.add( new JMenu( "File" ) );
		fileMenu.setMnemonic( 'F' );
		fileMenu.add( saveAction );
		fileMenu.addSeparator();
		fileMenu.add( exitAction );

		setContentPane( content );
		setJMenuBar( menuBar );

		setTitle( "AT Editor" );
		pack();
		setLocationRelativeTo( null );
		setVisible( true );

		if ( file != null ) {
			openFile();
		}
	}


	private void openFile() {
		try {
			FileReader reader = new FileReader( file );
			editArea.read( reader, "" );
		} catch ( IOException ioex ) {
			System.out.println( ioex );
		}
	}


	private void close() {
		latch.countDown();
		dispose();
	}

	private class SaveAction extends AbstractAction {
		private static final long	serialVersionUID	= 1L;


		SaveAction() {
			super( "Save..." );
			putValue( MNEMONIC_KEY, new Integer( 'S' ) );
		}


		@Override
		public void actionPerformed( ActionEvent e ) {
			try {
				FileWriter writer = new FileWriter( file );
				editArea.write( writer );
				writer.flush();
				writer.close();
			} catch ( IOException ioex ) {
				JOptionPane.showMessageDialog( AtomicTaggingEditor.this, ioex );
				latch.countDown();
				close();
			}
		}
	}

	private class ExitAction extends AbstractAction {
		private static final long	serialVersionUID	= 1L;


		public ExitAction() {
			super( "Exit" );
			putValue( MNEMONIC_KEY, new Integer( 'X' ) );
		}


		@Override
		public void actionPerformed( ActionEvent e ) {
			close();
		}
	}
}