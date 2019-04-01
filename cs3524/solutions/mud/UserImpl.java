/*
* @author Haniel Rameshbabu
*/

package cs3524.solutions.mud;

//import java.util.List;
//import java.util.ArrayList;

public class UserImpl implements UserInterface {
	private String userName;

	public UserImpl( String name ) {
    		userName = name;
	}

  	public String getName()	{
		    return userName;
  	}

  	public void sendMessage(String message) {
    		System.out.println(message);
  	}
}
