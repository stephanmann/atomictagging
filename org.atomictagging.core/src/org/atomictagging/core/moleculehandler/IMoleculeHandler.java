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

/**
 * The base interface for all molecule handlers.<br>
 * <br>
 * The idea is, that there are specialized viewers, importers and exporters for any given molecule type. These special
 * handlers shall be implemented using one of the children of this interface. This way, it should be easy to change the
 * handling of a given molecule type and allow applications to be customized by loading or unloading specific handlers.
 * 
 * @author Stephan Mann
 */
public interface IMoleculeHandler {

	/**
	 * Returns a human readable unique ID for this molecule handler. This ID is not used for management of the handlers
	 * and should only be used to identify the handler when presented to a user.
	 * 
	 * @return A human readable unique ID
	 */
	String getUniqueId();


	/**
	 * Returns an ordinal for this handler. It will be used by the {@link MoleculeHandlerFactory} to decide which
	 * handler will be used if multiple handlers can handle the same type of molecule. The handler with the lower number
	 * will be asked first and thus wins.<br>
	 * <br>
	 * <b>IMPORTANT: Integer.MAX_VALUE is reserved and won't be accepted by the {@link MoleculeHandlerFactory}</b>
	 * 
	 * @return An ordinal for this handler
	 */
	int getOrdinal();

}
