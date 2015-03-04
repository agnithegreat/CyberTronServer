package com.toxicgames.cybertron.room;

import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.variables.SFSUserVariable;
import com.smartfoxserver.v2.entities.variables.UserVariable;
import com.smartfoxserver.v2.exceptions.SFSException;
import com.smartfoxserver.v2.extensions.BaseServerEventHandler;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

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
		Room room = (Room) event.getParameter(SFSEventParam.ROOM);	
		User user = (User) event.getParameter(SFSEventParam.USER);

		int posX = (int) (Math.random() * 300);
		int posY = (int) (Math.random() * 300);

		user.setProperty(UserPropsEnum.POSX, posX);
		user.setProperty(UserPropsEnum.POSY, posY);

		int color = (int) (Math.random() * 0xFFFFFF);
		user.setProperty(UserPropsEnum.COLOR, color);

		user.setProperty(UserPropsEnum.X_DIRECTION, 0);
		user.setProperty(UserPropsEnum.Y_DIRECTION, 0);

		UserVariable posXVar = new SFSUserVariable(UserProps.POSX, posX);
		UserVariable posYVar = new SFSUserVariable(UserProps.POSY, posY);
		UserVariable colorVar = new SFSUserVariable(UserProps.COLOR, color);

		getApi().setUserVariables(user, Arrays.asList(posXVar, posYVar, colorVar));

		/**
		 * send previous users data to the new one
		 */
		List<User> usersList = room.getUserList();
		for (Iterator<User> iter = usersList.iterator(); iter.hasNext();)
		{

			User roomUser = iter.next();
//			if(roomUser.getId() == user.getId())
//			{
//				continue;
//			}

			posXVar = new SFSUserVariable(UserProps.POSX, roomUser.getProperty(UserPropsEnum.POSX));
			posYVar = new SFSUserVariable(UserProps.POSY, roomUser.getProperty(UserPropsEnum.POSY));
			colorVar = new SFSUserVariable(UserProps.COLOR, roomUser.getProperty(UserPropsEnum.COLOR));
			getApi().setUserVariables(roomUser, Arrays.asList(posXVar, posYVar, colorVar), true, false);

		}


	}
}