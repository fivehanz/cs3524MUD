/*
* @author Haniel Rameshbabu
*/

package cs3524.solutions.mud;

import java.rmi.Naming;
import java.lang.SecurityManager;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Scanner;

public class Client {

	public static void main(String args[]) {
		if (args.length < 3) {
			System.err.println( "Usage:\njava Client <registryhostname> <registryport> <callbackport>" ) ;
			return;
		}

		try {

			String hostname = args[0];
			int registryport = Integer.parseInt( args[1] );
			int callbackport = Integer.parseInt( args[2] );

			System.setProperty( "java.security.policy", "mud.policy" ) ;
			System.setSecurityManager( new SecurityManager() ) ;

			// User Instance
			String userName = System.console().readLine("Username: ").trim();
			UserImpl user = new UserImpl( userName );
			UserInterface userstub = (UserInterface)UnicastRemoteObject.exportObject( user, callbackport );


			// Stub
			String regURL = "rmi://" + hostname + ":" + registryport + "/MUDServer";
			System.out.println("Looking up " + regURL );
			MUDServerInterface serverstub = (MUDServerInterface)Naming.lookup( regURL );

			
			List<String> servers = serverstub.listServers();
			Integer i = 1;
			for( String srv : servers )
			{
				System.out.println("("+i+") "+srv);
				++i;
			}
			
			//choose a server or create your own
			String chosenServerString = null;
			boolean response = false;
			while(chosenServerString == null)
			{
				chosenServerString = System.console().readLine("Connect to server number: ").trim();
				if (Integer.parseInt(chosenServerString) <= servers.size())
				{	// you have chosen one of the existing servers
					Integer chosenServerInt = Integer.parseInt(chosenServerString);
					--chosenServerInt;	//decrement the value to match index
					response = serverstub.joinServer(servers.get(chosenServerInt), userstub);
					if ( response == false ){System.exit(0);}
				}
				else {	// invalid input
					chosenServerString = null;
					System.out.println("Invalid choice! Try again.");
				}
			}


			// Main interface
			System.out.println( "Type 'help' for controls/commands" );
			String input;
			while (true) {
				input = System.console().readLine("\nEnter command:\n\n").trim();

				// Basic commands sys out
				if ( input.equals("help")  ) {
					System.out.println( "\nview \nmove \ntake \nshow inventory \nonline users \nmessage \nexit\n" );
				}

				else if ( input.equals( "view" ) )
				{	//view waht you have around you
					serverstub.view( userName, "paths" );
					serverstub.view( userName, "things" );
				}
				
				else if ( input.equals( "move" ) )
				{	// move somewhere
					System.out.println( "You can move:\n" );
					serverstub.view( userName, "paths" );
					input = System.console().readLine( "Where do you want to move?\n" ).trim();
					if ( input.equals("north") || input.equals("east") || input.equals("south") || input.equals("west") )
					{
						serverstub.moveUser( userName, input );
					}
				}
				
				else if ( input.equals( "take" ) )
				{	// take item around you
					serverstub.view( userName, "things" );
					input = System.console().readLine( "What would you like to take?\n" ).trim();
					serverstub.getThing( userName, input );
				}
				
				else if ( input.equals( "show inventory" ) )
				{	//show your items in inventory
					serverstub.showInventory( userName );
				}
				
				else if ( input.equals( "online users" ) )
				{	// show all online users on the server
					serverstub.listUsers( userName );
				}
				
				else if ( input.equals( "message" ) )
				{	// send a message to online user
					System.out.println( "You can message to:\n" );
					serverstub.listUsers( userName );
					String to = System.console().readLine( "Write the name:\n" ).trim();
					String message = System.console().readLine( "Write the message:\n" ).trim();
					serverstub.message(userName, to, message );
				}



				// Exit
				else if ( input.equals( "exit" ) ) {
					//serverStub.leaveServer( userName );
					System.exit(0);
				}

				// Invalid command
				else {
					System.out.println("Please enter valid command.\n");
				}
			}


		}
		catch(java.rmi.NotBoundException e) {
			//System.err.println( "Can't find the auctioneer in the registry." );
			System.err.println( "Error: server NotBoundException" );
		}
		catch (java.io.IOException e) {
			System.out.println( "Failed to register." );
		}
	}

}
