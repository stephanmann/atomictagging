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
package org.atomictagging.core.services;

import org.atomictagging.core.services.impl.AtomService;
import org.atomictagging.core.services.impl.MoleculeService;
import org.atomictagging.core.services.impl.TagService;
import org.atomictagging.core.services.impl.TypeService;

/**
 * Since we seem to be unable to get OSGI services to run the way we want them to, we'll be using singletons for the
 * time being. As long as this factory is used exclusively to access these singletons, we can easily switch the
 * "service" from single instance to multiple instance if we need to. Also, we can switch easily to OSGI services should
 * we ever figure out how to use them properly.
 */
public final class ATService {

	private static ITagService		tagService		= null;
	private static ITypeService		typeService		= null;
	private static IAtomService		atomService		= null;
	private static IMoleculeService	moleculeService	= null;


	/**
	 * Get an instance that implements the {@link ITagService} interface.
	 * <p>
	 * <b>Note:</b> You should not make any assumptions whether the returned instance is a singleon or not. It is, at
	 * the moment, but it might change.
	 * 
	 * @return An instance that implements {@link ITagService}
	 */
	public static ITagService getTagService() {
		if ( tagService == null ) {
			tagService = new TagService();
		}
		return tagService;
	}


	/**
	 * Get an instance that implements the {@link ITypeService} interface.
	 * <p>
	 * <b>Note:</b> You should not make any assumptions whether the returned instance is a singleon or not. It is, at
	 * the moment, but it might change.
	 * 
	 * @return An instance that implements {@link ITypeService}
	 */
	public static ITypeService getTypeService() {
		if ( typeService == null ) {
			typeService = new TypeService();
		}
		return typeService;
	}


	/**
	 * Get an instance that implements the {@link IAtomService} interface.
	 * <p>
	 * <b>Note:</b> You should not make any assumptions whether the returned instance is a singleon or not. It is, at
	 * the moment, but it might change.
	 * 
	 * @return An instance that implements {@link IAtomService}
	 */
	public static IAtomService getAtomService() {
		if ( atomService == null ) {
			atomService = new AtomService();
		}
		return atomService;
	}


	/**
	 * Get an instance that implements the {@link IMoleculeService} interface.
	 * <p>
	 * <b>Note:</b> You should not make any assumptions whether the returned instance is a singleon or not. It is, at
	 * the moment, but it might change.
	 * 
	 * @return An instance that implements {@link IMoleculeService}
	 */
	public static IMoleculeService getMoleculeService() {
		if ( moleculeService == null ) {
			moleculeService = new MoleculeService();
		}
		return moleculeService;
	}

}