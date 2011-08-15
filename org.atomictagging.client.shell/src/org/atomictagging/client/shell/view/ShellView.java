package org.atomictagging.client.shell.view;

import org.atomictagging.shell.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

public class ShellView extends ViewPart {

	@Override
	public void createPartControl( final Composite parent ) {
		final Text text = new Text( parent, SWT.MULTI );

		text.addKeyListener( new KeyListener() {
			@Override
			public void keyReleased( final KeyEvent e ) {
				// TODO Auto-generated method stub

			}


			@Override
			public void keyPressed( final KeyEvent e ) {
				if ( e.keyCode == SWT.CR ) {
					text.setText( text.getText() + "\n> " );
					final int pos = text.getText().length();
					text.setSelection( pos, pos );
					e.doit = false;
				}
			}
		} );

		text.addFocusListener( new FocusListener() {

			private boolean	init	= false;


			@Override
			public void focusLost( final FocusEvent e ) {
				// TODO Auto-generated method stub

			}


			@Override
			public void focusGained( final FocusEvent e ) {
				if ( init ) {
					return;
				}

				// System.setOut( new PrintStream( new OutputStream() {
				// @Override
				// public void write( final int b ) throws IOException {
				// text.setText( text.getText() + String.valueOf( (char) b ) );
				// }
				// } ) );

				final Shell shell = new Shell();
				shell.run();

				init = true;
			}
		} );
	}


	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
