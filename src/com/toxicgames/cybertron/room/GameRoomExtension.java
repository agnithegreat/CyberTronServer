package com.toxicgames.cybertron.room;

import com.smartfoxserver.v2.SmartFoxServer;
import com.smartfoxserver.v2.api.ISFSMMOApi;
import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.extensions.SFSExtension;
import com.smartfoxserver.v2.game.SFSGame;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by mor on 01.03.2015.
 */
public class GameRoomExtension extends SFSExtension {

    private SFSGame room;
    private SmartFoxServer sfs;
    private GameController game;
    private ScheduledFuture<?> gameTask;
    private ISFSMMOApi mmoapi;

    public GameController getGame() {
        return game;
    }

    //
    @Override
    public void init()
    {
        room = (SFSGame) this.getParentRoom();

        sfs = SmartFoxServer.getInstance();

        mmoapi = sfs.getAPIManager().getMMOApi();

        addEventHandler(SFSEventType.USER_JOIN_ROOM, UserJoinedGameEventHandler.class);

        addRequestHandler(UserProps.REQ_CONTROL, ControlRequestHandler.class);

        game = new GameController(this);

        // Schedule task: 30ms is nearly 30 fps used by the Flash client;
        gameTask = sfs.getTaskScheduler().scheduleAtFixedRate(game, 0, 30, TimeUnit.MILLISECONDS);
//        gameTask = sfs.getTaskScheduler().scheduleAtFixedRate(game, 0, 20, TimeUnit.MILLISECONDS);
    }

}
