package org.atomictagging.client.explorer.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.part.ViewPart;

public class ExplorerView extends ViewPart {

	@Override
	public void createPartControl( Composite parent ) {
		// Text text = new Text(parent, SWT.BORDER);
		// text.setText("Imagine a fantastic user interface here");

		createContents( parent );

	}


	@Override
	public void setFocus() {
	}


	protected Control createContents( Composite parent ) {
		Composite composite = new Composite( parent, SWT.NONE );
		composite.setLayout( new GridLayout( 1, false ) );

		// Add a checkbox to toggle whether the labels preserve case
		Button preserveCase = new Button( composite, SWT.CHECK );
		preserveCase.setText( "&Preserve case" );

		// Create the tree viewer to display the file tree
		final TreeViewer tv = new TreeViewer( composite );
		tv.getTree().setLayoutData( new GridData( GridData.FILL_BOTH ) );
		tv.setContentProvider( new FileTreeContentProvider() );
		tv.setLabelProvider( new FileTreeLabelProvider() );
		tv.setInput( "root" ); // pass a non-null that will be ignored

		// When user checks the checkbox, toggle the preserve case attribute
		// of the label provider
		preserveCase.addSelectionListener( new SelectionAdapter() {
			@Override
			public void widgetSelected( SelectionEvent event ) {
				boolean preserveCase = ( (Button) event.widget ).getSelection();
				FileTreeLabelProvider ftlp = (FileTreeLabelProvider) tv.getLabelProvider();
				ftlp.setPreserveCase( preserveCase );
			}
		} );
		return composite;
	}

	class FileTreeContentProvider implements ITreeContentProvider {
		/**
		 * Gets the children of the specified object
		 * 
		 * @param arg0
		 *            the parent object
		 * @return Object[]
		 */
		@Override
		public Object[] getChildren( Object arg0 ) {
			// Return the files and subdirectories in this directory
			return ( (File) arg0 ).listFiles();
		}


		/**
		 * Gets the parent of the specified object
		 * 
		 * @param arg0
		 *            the object
		 * @return Object
		 */
		@Override
		public Object getParent( Object arg0 ) {
			// Return this file's parent file
			return ( (File) arg0 ).getParentFile();
		}


		/**
		 * Returns whether the passed object has children
		 * 
		 * @param arg0
		 *            the parent object
		 * @return boolean
		 */
		@Override
		public boolean hasChildren( Object arg0 ) {
			// Get the children
			Object[] obj = getChildren( arg0 );

			// Return whether the parent has children
			return obj == null ? false : obj.length > 0;
		}


		/**
		 * Gets the root element(s) of the tree
		 * 
		 * @param arg0
		 *            the input data
		 * @return Object[]
		 */
		@Override
		public Object[] getElements( Object arg0 ) {
			// These are the root elements of the tree
			// We don't care what arg0 is, because we just want all
			// the root nodes in the file system
			return File.listRoots();
		}


		/**
		 * Disposes any created resources
		 */
		@Override
		public void dispose() {
			// Nothing to dispose
		}


		/**
		 * Called when the input changes
		 * 
		 * @param arg0
		 *            the viewer
		 * @param arg1
		 *            the old input
		 * @param arg2
		 *            the new input
		 */
		@Override
		public void inputChanged( Viewer arg0, Object arg1, Object arg2 ) {
			// Nothing to change
		}
	}

	/**
	 * This class provides the labels for the file tree
	 */

	class FileTreeLabelProvider implements ILabelProvider {
		// The listeners
		private final List	listeners;

		// Images for tree nodes
		private Image		file;

		private Image		dir;

		// Label provider state: preserve case of file names/directories
		boolean				preserveCase;


		/**
		 * Constructs a FileTreeLabelProvider
		 */
		public FileTreeLabelProvider() {
			// Create the list to hold the listeners
			listeners = new ArrayList();

			// Create the images
			try {
				file = new Image( null, new FileInputStream( "images/file.gif" ) );
				dir = new Image( null, new FileInputStream( "images/directory.gif" ) );
			} catch ( FileNotFoundException e ) {
				// Swallow it; we'll do without images
			}
		}


		/**
		 * Sets the preserve case attribute
		 * 
		 * @param preserveCase
		 *            the preserve case attribute
		 */
		public void setPreserveCase( boolean preserveCase ) {
			this.preserveCase = preserveCase;

			// Since this attribute affects how the labels are computed,
			// notify all the listeners of the change.
			LabelProviderChangedEvent event = new LabelProviderChangedEvent( this );
			for ( int i = 0, n = listeners.size(); i < n; i++ ) {
				ILabelProviderListener ilpl = (ILabelProviderListener) listeners.get( i );
				ilpl.labelProviderChanged( event );
			}
		}


		/**
		 * Gets the image to display for a node in the tree
		 * 
		 * @param arg0
		 *            the node
		 * @return Image
		 */
		@Override
		public Image getImage( Object arg0 ) {
			// If the node represents a directory, return the directory image.
			// Otherwise, return the file image.
			return ( (File) arg0 ).isDirectory() ? dir : file;
		}


		/**
		 * Gets the text to display for a node in the tree
		 * 
		 * @param arg0
		 *            the node
		 * @return String
		 */
		@Override
		public String getText( Object arg0 ) {
			// Get the name of the file
			String text = ( (File) arg0 ).getName();

			// If name is blank, get the path
			if ( text.length() == 0 ) {
				text = ( (File) arg0 ).getPath();
			}

			// Check the case settings before returning the text
			return preserveCase ? text : text.toUpperCase();
		}


		/**
		 * Adds a listener to this label provider
		 * 
		 * @param arg0
		 *            the listener
		 */
		@Override
		public void addListener( ILabelProviderListener arg0 ) {
			listeners.add( arg0 );
		}


		/**
		 * Called when this LabelProvider is being disposed
		 */
		@Override
		public void dispose() {
			// Dispose the images
			if ( dir != null )
				dir.dispose();
			if ( file != null )
				file.dispose();
		}


		/**
		 * Returns whether changes to the specified property on the specified element would affect the label for the
		 * element
		 * 
		 * @param arg0
		 *            the element
		 * @param arg1
		 *            the property
		 * @return boolean
		 */
		@Override
		public boolean isLabelProperty( Object arg0, String arg1 ) {
			return false;
		}


		/**
		 * Removes the listener
		 * 
		 * @param arg0
		 *            the listener to remove
		 */
		@Override
		public void removeListener( ILabelProviderListener arg0 ) {
			listeners.remove( arg0 );
		}
	}

}
