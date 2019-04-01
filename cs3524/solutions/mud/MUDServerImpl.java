/*
* @author Haniel Rameshbabu
* Implementation of MUDServerInterface
*/

package cs3524.solutions.mud;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashMap;


public class MUDServerImpl implements MUDServerInterface {

  // Key => Value; name => MUD object
  private Map<String, MUD> servers = new HashMap<String, MUD>();

  // whereabouts of the User
	private	Map<String, String> userMap = new HashMap<String, String>();
  // records of all users positions
	private Map<String, String> userPosMap = new HashMap<String, String>();

	// server => Hash of all usernames logged
	private	Map<String, HashMap<String, UserInterface>> loggedUsers = new HashMap<String, HashMap<String, UserInterface>>();

	// holds records of all user inventories
  // @format InventpryName => InventoryItem
	private Map<String, ArrayList<String>> inventoryMap = new HashMap<String, ArrayList<String>>();

  // Create all servers at start; i.e 3;
  public MUDServerImpl() throws RemoteException {
    // Server 1 (Rosa)
    servers.put("Rosa", new MUD("MUDs/rosa/rosa.edg","MUDs/rosa/rosa.msg","MUDs/rosa/rosa.thg"));
    // Username => Object
    HashMap<String, UserInterface> RosaObj = new HashMap<String, UserInterface>();
		loggedUsers.put( "Rosa", RosaObj);

    // Server 2
    servers.put("Tros", new MUD("MUDs/tros/tros.edg","MUDs/tros/tros.msg","MUDs/tros/tros.thg"));
    // Username => Object
    HashMap<String, UserInterface> TrosObj = new HashMap<String, UserInterface>();
    loggedUsers.put( "Tros", TrosObj);

    // Server 3
    servers.put("Gina", new MUD("MUDs/gina/gina.edg","MUDs/gina/gina.msg","MUDs/gina/gina.thg"));
    // Username => Object
    HashMap<String, UserInterface> GinaObj = new HashMap<String, UserInterface>();
    loggedUsers.put( "Gina", GinaObj);

  }

  public List<String> listServers() throws RemoteException {
    Set<String> set = servers.keySet();
		return new ArrayList<String>(set);
  }

  public boolean joinServer(String servername, UserInterface user) throws RemoteException {
    MUD server = servers.get(servername);
    String userName = user.getName();
    HashMap<String, UserInterface> userObj;


    /*if(loggedUsers.get(name).size() >= maxPlayers) {	// check if maxPlayers is reached
      client.sendMessage( "Sorry maximum players reached. Try later or join another server" );
      return false;
    }

    if ( userMap.get( userName ) != null ) {	// check if userName already exist. It must be unique
      client.sendMessage( "Change name please! User already exist!" );
      return false;
    }*/

    // User
    userMap.put( userName, servername );
    userObj = loggedUsers.get( servername );

    userObj.put( userName, user);

    loggedUsers.put( servername, userObj );

    userPosMap.put( userName, server.startLocation() );

    inventoryMap.put( userName, new ArrayList<String>() );

    server.addThing( server.startLocation(), "User: "+userName );


    // prepare the message
    String message = ( "\n***** Welcome to " + servername + " Server *****\n" );
    message += "Current number of player on this server is "+loggedUsers.get( servername ).size()+"\n";
    message += "You are currently at "+userPosMap.get( userName )+" location\n";
    
    // send the message to the client
    user.sendMessage( message );
    return true;
  }
 
  // view what is at particular location
  public boolean view( String userName, String what ) throws RemoteException{ 
    String serverName = userMap.get( userName );
    MUD server = servers.get( serverName );
    HashMap<String, UserInterface> clientsMap = loggedUsers.get( serverName );
    UserInterface client = clientsMap.get( userName );
    String position = userPosMap.get( userName );
    String message = null;

    if ( what.equals("paths") )
    { // send info about possible paths
      message = server.locationPaths( position );
      client.sendMessage( message );
      return true;
    }

    if ( what.equals("things") )
    { // send info about things
      message = "There is:\n";
      List<String> things = server.locationThings( position );
      for ( String t : things )
      { // construct the message and send
        message += t + "\n";
      }
      client.sendMessage( message );
      return true;
    }
    return false;
  }

  public boolean moveUser(String userName, String position) throws RemoteException
  { // move the user
    String serverName = userMap.get( userName );
    MUD server = servers.get( serverName );
    HashMap<String, UserInterface> clientsMap = loggedUsers.get( serverName );
    UserInterface client = clientsMap.get( userName );
    String origin = userPosMap.get( userName );
    String message = "";
    // try to move the user and check response
    message = server.moveThing( origin, position, "User: "+userName );
    userPosMap.put( userName, message );
    if ( message.equals( origin ) )
    { // user is at the same place because there is no path
      client.sendMessage( "You cannot move there.\n" );
      return false;
    }
    client.sendMessage( "You moved to " + message + "\n");
    return true;
  }



  public boolean getThing( String userName, String thing) throws RemoteException
  { // client can take a thing but not a user

    String serverName = userMap.get( userName );
    MUD server = servers.get( serverName );
    HashMap<String, UserInterface> clientsMap = loggedUsers.get( serverName );
    UserInterface client = clientsMap.get( userName );
    ArrayList<String> inventory = inventoryMap.get( userName );
    List<String> things = server.locationThings( userPosMap.get( userName ) );

    for ( String t : things )
    { // iterate through things
      if ( thing.equals( t ) && !thing.contains("User:") )
      { // check if there is the thing client wants to take
        // && check of the thing is not user
        server.delThing( userPosMap.get( userName ), t );
        inventory.add( t );
        inventoryMap.put( userName, inventory );
        client.sendMessage( "You have: "+inventory.toString() );
        return true;
      }
    }
    // the thing was not there or it was a user
    client.sendMessage( "No!\nYou have: "+inventory.toString() );
    return false;

  }

  public boolean showInventory( String userName ) throws RemoteException
  { // list all the collected items
    String serverName = userMap.get( userName );
    MUD server = servers.get( serverName );
    HashMap<String, UserInterface> clientsMap = loggedUsers.get( serverName );
    UserInterface client = clientsMap.get( userName );
    List<String> inventory =  inventoryMap.get(userName);
    String message = "In your inventory is:\n";
    client.sendMessage( message+inventory.toString() );
    return true;
  }

  public boolean listUsers( String userName ) throws RemoteException
  { // list all the online users at the server where client is.
    String serverName = userMap.get( userName );
    MUD server = servers.get( serverName );
    HashMap<String, UserInterface> clientsMap = loggedUsers.get( serverName );
    UserInterface client = clientsMap.get( userName );
    String message = "\nThese users are online:\n";
    Set<String> clientsSet = clientsMap.keySet();
    for (String c : clientsSet )
    {
      message += c+"\n";
    }
    client.sendMessage( message );
    return true;
  }

  public boolean message( String userName, String to, String message ) throws RemoteException
  { // send a message to a user
    String serverName = userMap.get( userName );
    MUD server = servers.get( serverName );
    HashMap<String, UserInterface> clientsMap = loggedUsers.get( serverName );
    UserInterface fromClient = clientsMap.get( userName );
    UserInterface toClient = clientsMap.get( to );
    String formatedMessage = "Message from " + userName + ":\n" + message;
    toClient.sendMessage( formatedMessage );
    fromClient.sendMessage( "\nMessage sent\n" );
    return true;
  }


}
