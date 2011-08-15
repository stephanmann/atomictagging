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
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import org.atomictagging.shell.IShell;
import org.atomictagging.utils.StringUtils;

/**
 * Command to set the tag scope. Same as "cd" on file systems.
 * 
 * @author Stephan Mann
 */
public class SetScopeCommand extends AbstractCommand {

	/**
	 * @param shell
	 */
	public SetScopeCommand( IShell shell ) {
		super( shell );
	}


	@Override
	public String getCommandString() {
		return "cd";
	}


	@Override
	public String getHelpMessage() {
		return "cd <tag>\t- Set the scope of all following ls commands";
	}


	@Override
	public String getVerboseHelpMessage() {
		String msg = getHelpMessage();
		msg += "\n\t\t  Use \"..\" to remove the last item and \"/\" to return to root.";
		msg += "\n\t\t  Just like \"change directory\" on any file system.";
		return msg;
	}

	private final Stack<String>	scope	= new Stack<String>();


	@Override
	public int handleInput( final String input, PrintStream stdout ) {

		if ( input.isEmpty() ) {
			return 1;
		}

		if ( input.equals( "/" ) ) {
			scope.clear();
			shell.setEnvironment( "scope", "" );
			return 0;
		}

		String in = input;
		if ( input.startsWith( "/" ) ) {
			scope.clear();
			in = input.substring( 1 );
		}

		for ( String part : in.split( "/" ) ) {
			if ( part.equals( ".." ) ) {
				if ( scope.size() > 0 ) {
					scope.pop();
				}
			} else {
				scope.push( part );
			}
		}

		List<String> result = Arrays.asList( scope.toArray( new String[0] ) );
		shell.setEnvironment( "scope", StringUtils.join( result, "/" ) );
		return 0;
	}
}
