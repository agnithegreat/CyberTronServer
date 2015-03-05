package com.toxicgames.cybertron.handlers;

import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.exceptions.SFSException;
import com.smartfoxserver.v2.extensions.BaseServerEventHandler;
import com.toxicgames.cybertron.core.GameRoomExtension;

/**
 * UserJoinedEventHandler: manage a user joined event
 * 
 * @author Ing. Ignazio Locatelli
 * @version 1.0
 */
public class UserJoinedGameEventHandler extends BaseServerEventHandler
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

		// Add user starship to game
		((GameRoomExtension) this.getParentExtension()).addPersonage(user);
	}
}