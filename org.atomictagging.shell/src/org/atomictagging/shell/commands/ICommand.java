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
package org.atomictagging.shell.commands;

import java.io.PrintStream;

import org.atomictagging.shell.Shell;

/**
 * The interface for commands that can be executed in a Shell
 * 
 * @see Shell
 * @author Stephan Mann
 */
public interface ICommand {
	/**
	 * Gets the string that will call the command.
	 * 
	 * @return The string identifying the command
	 */
	String getCommandString();


	/**
	 * Executes the command.
	 * 
	 * @param input
	 * @param stdout
	 * @return 0 in case of success, an error code != 0 in case of failure
	 */
	int handleInput( String input, PrintStream stdout );


	/**
	 * Gets the help message for this command.
	 * 
	 * @return Help message
	 */
	String getHelpMessage();


	/**
	 * Gets a more verbose help message for this command.
	 * 
	 * @return Verbose help message
	 */
	String getVerboseHelpMessage();
}
