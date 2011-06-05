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
package org.atomictagging.core.internal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/** */
public class Activator implements BundleActivator {

	private static BundleContext	context;


	static BundleContext getContext() {
		return context;
	}


	@Override
	public void start( BundleContext bundleContext ) throws Exception {
		Activator.context = bundleContext;
	}


	@Override
	public void stop( BundleContext bundleContext ) throws Exception {
		Activator.context = null;
	}

}
