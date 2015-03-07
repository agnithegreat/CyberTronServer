package com.toxicgames.cybertron.handlers;

import com.smartfoxserver.v2.annotations.MultiHandler;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;
import com.smartfoxserver.v2.extensions.SFSExtension;
import com.toxicgames.cybertron.enums.ClientRequest;
import com.toxicgames.cybertron.enums.UserProps;
import com.toxicgames.cybertron.core.GameController;
import com.toxicgames.cybertron.room.GameRoomExtension;

/**
 * UserJoinedEventHandler: manage a user joined event
 *
 * @author Ing. Ignazio Locatelli
 * @version 1.0
 */

@MultiHandler
public class ControlRequestHandler extends BaseClientRequestHandler {

    @Override
    public void handleClientRequest(User sender, ISFSObject params) {
        String requestId = params.getUtfString(SFSExtension.MULTIHANDLER_REQUEST_ID);
        trace(requestId);

        GameController game = ((GameRoomExtension) this.getParentExtension()).getGame();

        if (requestId.equals(ClientRequest.REQ_MOVE)) {
            game.moveHero(sender.getId(), params.getInt(UserProps.DELTAX), params.getInt(UserProps.DELTAY), params.getInt(UserProps.REQ_ID));
        } else if (requestId.equals(ClientRequest.REQ_ROTATE)) {
            game.rotateHero(sender.getId(), params.getFloat(UserProps.DIRECTION), params.getInt(UserProps.REQ_ID));
        } else if (requestId.equals(ClientRequest.REQ_SHOT)) {
            game.shotUser(sender.getId(), params.getBool(UserProps.SHOOT), params.getInt(UserProps.REQ_ID));
        }
    }
}