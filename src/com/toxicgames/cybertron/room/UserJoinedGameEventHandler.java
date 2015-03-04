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
//		UserVariable xdirVar = new SFSUserVariable("x_dir", Math.random() > .5);
//		UserVariable ydirVar = new SFSUserVariable("y_dir", Math.random() > .5);

		getApi().setUserVariables(user, Arrays.asList(posXVar, posYVar, colorVar));

//		GameRoomExtension game = (GameRoomExtension) getParentExtension();
//		game.saveUserPosition(user, new Vec3D((float) Math.random() * 300, (float) Math.random() * 300));
//
//		if(room != null)
//		{
//			trace("*** CT ***: USER JOINED",room.getName());
//		}
//		else
//		{
//			user.addJoinedRoom(SmartFoxServer.getInsta);
//		}
//
//		if (room.isGame()) {
//
//			// Get user that joined

//			trace("CyberTron: user " + user.getName() + " entered the game room '" + room.getName() + "' - game room id: " + room.getId());
//
//			// Check if game already exist. If not, you have just entered a new room: create new game
//			GameBean currGame = (GameBean) ((BattleFarmExtension) getParentExtension()).getGames().get(room.getId());
//
//			if (currGame == null)
//			{
//				/*
//				 * Note: global room variables cannot be set by the client; so we receive an idMap room variable that has been created
//				 * by the client and remap it on a global room variable created server-side in order to be sure that all clients will receive this information
//				 */
//				int mapId = ((Integer) room.getVariable("map").getValue()).intValue();
//				ArrayList<RoomVariable> roomVariables = new ArrayList<RoomVariable>();
//				RoomVariable map = new SFSRoomVariable("idMap",mapId,true,false,true);
//				roomVariables.add(map);
//				this.getApi().setRoomVariables(user, room, roomVariables);
//
//				GameMapBean gameMapBean = ((BattleFarmExtension) (getParentExtension())).getGameMapsInfoBean().getMaps().get(mapId);
//
//				currGame = new GameBean(gameMapBean,room.getId());
//				((BattleFarmExtension) (getParentExtension())).getGames().put(room.getId(),currGame);
//				trace("BattleFarm: a new match was generated for room name '" + room.getName() + "' room id: " + room.getId() + "; selected map is '" + gameMapBean.getId());
//
//			}
//
//			if (currGame != null)
//			{
//				// Retrieve game map data
//				GameMapBean gameMapBean = currGame.getBaseGameMapBean();
//
//				// Create player with starting coordinates
//				if (currGame.getMaster() == null)
//				{
//					PlayerBean p1 = new PlayerBean(user.getId(), gameMapBean.getP1x(), gameMapBean.getP1y(),user);
//					p1.setMaster(true);
//					p1.setIdGame(currGame.getId());
//
//					currGame.getPlayers().put(user.getId(), p1);
//					currGame.setMaster(user.getId());
//				}
//				else
//				{
//					PlayerBean p2 = new PlayerBean(user.getId(), gameMapBean.getP2x(), gameMapBean.getP2y(),user);
//					p2.setIdGame(currGame.getId());
//
//					currGame.getPlayers().put(user.getId(), p2);
//					currGame.setSlave(user.getId());
//				}
//			}
//		}
	}
}