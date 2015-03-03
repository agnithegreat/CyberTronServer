package com.toxicgames.cybertron;

import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.extensions.SFSExtension;
import com.toxicgames.cybertron.room.GameController;

/**
 * Created by mor on 25.02.2015.
 */
public class CyberTronExtension extends SFSExtension {

    private GameController gameController = null;

    @Override
    public void init() {

        // Event handler: join room
        addEventHandler(SFSEventType.USER_LOGIN, UserLoginEventHandler.class);

//        addEventHandler(SFSEventType.USER_JOIN_ROOM, UserJoinedEventHandler.class);

        // Event handler: user leave game room
//        addEventHandler(SFSEventType.USER_LEAVE_ROOM, UserLeavedEventHandler.class);

        // Event handler: user log out
        addEventHandler(SFSEventType.USER_LOGOUT, UserDisconnectedEventHandler.class);

        // Event handler: user disconnect
        addEventHandler(SFSEventType.USER_DISCONNECT, UserDisconnectedEventHandler.class);
    }

    @Override
    public void destroy() {
        super.destroy();
    }


}
