package com.toxicgames.cybertron.handlers;

import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.exceptions.SFSException;
import com.smartfoxserver.v2.extensions.BaseServerEventHandler;
import com.toxicgames.cybertron.room.GameRoomExtension;

/**
 * UserLeavedEventHandler: manager user leaved events (user exiting a game room)
 * 
 * @author Ing. Ignazio Locatelli
 * @version 1.0
 */
public class UserLeavedEventHandler extends BaseServerEventHandler
{
	/**
	 * Handle user leaving a room
	 * 
	 * @param event The received event
	 */
	@Override
	public void handleServerEvent(ISFSEvent event) throws SFSException {
		User user = (User) event.getParameter(SFSEventParam.USER);
		((GameRoomExtension) this.getParentExtension()).removeHero(user.getId());
	}
}