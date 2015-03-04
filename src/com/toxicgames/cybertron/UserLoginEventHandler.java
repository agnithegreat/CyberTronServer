package com.toxicgames.cybertron;

import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.match.MatchExpression;
import com.smartfoxserver.v2.entities.match.StringMatch;
import com.smartfoxserver.v2.exceptions.SFSException;
import com.smartfoxserver.v2.extensions.BaseServerEventHandler;

import java.util.Collection;

/**
 * UserJoinedEventHandler: manage a user joined event
 * 
 * @author Ing. Ignazio Locatelli
 * @version 1.0
 */
public class UserLoginEventHandler extends BaseServerEventHandler
{
	/**
	 * Handle user event when user joins a game room 
	 * 
	 * @param event The received event
	 */
	@Override
	public void handleServerEvent(ISFSEvent event) throws SFSException
	{
		User user = (User) event.getParameter(SFSEventParam.USER);

		MatchExpression exp = new MatchExpression("name", StringMatch.EQUALS, "Lobby");

		Collection<Room> lobbyRooms = null;
		getApi().findRooms(lobbyRooms, exp, 1);

		if(user.getJoinedRooms().isEmpty() && !lobbyRooms.isEmpty())
		{
			Room room = (Room) lobbyRooms.toArray()[0];
			room.addUser(user);
		}

	}
}