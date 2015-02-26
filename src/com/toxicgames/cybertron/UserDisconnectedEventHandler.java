package com.toxicgames.cybertron;

import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.exceptions.SFSException;
import com.smartfoxserver.v2.extensions.BaseServerEventHandler;

/**
 * UserDisconnectedEventHandler: manager disconnected user events
 *
 * @author Ing. Ignazio Locatelli
 * @version 1.0
 */
public class UserDisconnectedEventHandler extends BaseServerEventHandler {
    /**
     * Handle user disconnected event
     *
     * @param event The received event
     */
    @SuppressWarnings("unchecked")
    @Override
    public void handleServerEvent(ISFSEvent event) throws SFSException {
//        // Get room
//        Room room = null;
//        List<Room> joinedRooms = (List<Room>) event.getParameter(SFSEventParam.JOINED_ROOMS);
//        if (joinedRooms.size() > 0) room = joinedRooms.get(0);  // User can be only in 1 room at a time
//
//        // Get user that leaved
        User user = (User) event.getParameter(SFSEventParam.USER);
        trace("BattleFarm: user " + user.getName() + " disconnected.");
//
//        // Process remove user from game
//        if (room != null)
//            GameBsn.removeUserFromGame(user, room, (BattleFarmExtension) getParentExtension());
    }
}