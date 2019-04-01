/*
* @author Haniel Rameshbabu
* Remote Interface for MUD game server
*/

package cs3524.solutions.mud;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;


public interface MUDServerInterface extends Remote
{
    
	public List<String> listServers() throws RemoteException;
	public boolean joinServer( String servername, UserInterface user ) throws RemoteException;
	//public boolean leaveServer( String userName ) throws RemoteException;
	public boolean view( String userName, String way ) throws RemoteException;
	public boolean moveUser( String userName, String position ) throws RemoteException;
	public boolean getThing( String userName, String thing ) throws RemoteException;
	public boolean showInventory( String userName ) throws RemoteException;
	public boolean listUsers( String userName ) throws RemoteException;
	public boolean message( String userName, String to, String message ) throws RemoteException;
}
