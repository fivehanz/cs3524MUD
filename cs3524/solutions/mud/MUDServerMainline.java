/*
* @author Haniel Rameshbabu
* @title Server Mainline for MUD game server
* @source Adapted Practicals 1 code
*/

package cs3524.solutions.mud;

import java.io.InputStreamReader;
import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.server.UnicastRemoteObject;

public class MUDServerMainline {



	public static void main(String args[]){

		if (args.length < 2) {
			System.err.println("Usage: \njava MUDServerMainline <registryport> <serverport>");
			return;
		}

		try {
			String hostname = (InetAddress.getLocalHost()).getCanonicalHostName();
			int registryport = Integer.parseInt(args[0]);
			int serverport = Integer.parseInt(args[1]);

			// Security Policy
			System.setProperty("java.security.policy", "mud.policy");
			System.setSecurityManager( new SecurityManager());

			// Generate remote Objects
			MUDServerImpl server = new MUDServerImpl();
			MUDServerInterface stub = (MUDServerInterface)UnicastRemoteObject.exportObject(server, serverport);
			
			String regURL = "rmi://" + hostname + ":" + registryport + "/MUDServer";
			System.out.println("Registering " + regURL);
			Naming.rebind(regURL, stub);

		}

		// Error Catching
		catch(java.net.UnknownHostException e) {
		    System.err.println( "Cannot determine localhost name." );
		    System.err.println( e.getMessage() );
		}
		catch (java.io.IOException e) {
            System.err.println( "Failed to register!" );
		    System.err.println( e.getMessage() );
	    }

		
	}

}
