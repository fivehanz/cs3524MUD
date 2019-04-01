/*
* @author Haniel Rameshbabu
*/

package cs3524.solutions.mud;

//import java.util.List;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface UserInterface extends Remote
{
	public String getName() throws RemoteException;
	public void sendMessage( String message ) throws RemoteException;

}
