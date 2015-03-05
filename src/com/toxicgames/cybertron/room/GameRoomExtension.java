package com.toxicgames.cybertron.room;

import com.smartfoxserver.v2.SmartFoxServer;
import com.smartfoxserver.v2.api.ISFSGameApi;
import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.entities.variables.RoomVariable;
import com.smartfoxserver.v2.entities.variables.SFSRoomVariable;
import com.smartfoxserver.v2.entities.variables.SFSUserVariable;
import com.smartfoxserver.v2.entities.variables.UserVariable;
import com.smartfoxserver.v2.extensions.SFSExtension;
import com.smartfoxserver.v2.game.SFSGame;
import com.toxicgames.cybertron.core.Bullet;
import com.toxicgames.cybertron.enums.ClientRequest;
import com.toxicgames.cybertron.enums.UserProps;
import com.toxicgames.cybertron.handlers.UserDisconnectedEventHandler;
import com.toxicgames.cybertron.handlers.UserLeavedEventHandler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by mor on 01.03.2015.
 */
public class GameRoomExtension extends SFSExtension {

    private SmartFoxServer sfs;
    private ISFSGameApi gameApi;
    private SFSGame room;

    private GameController game;
    private ScheduledFuture<?> gameTask;

    //
    @Override
    public void init() {
        room = (SFSGame) this.getParentRoom();

        sfs = SmartFoxServer.getInstance();

        gameApi = sfs.getAPIManager().getGameApi();

        addEventHandler(SFSEventType.USER_JOIN_ROOM, UserJoinedGameEventHandler.class);
        addEventHandler(SFSEventType.USER_LEAVE_ROOM, UserLeavedEventHandler.class);
        addEventHandler(SFSEventType.USER_DISCONNECT, UserDisconnectedEventHandler.class);

        addRequestHandler(ClientRequest.REQ_CONTROL, ControlRequestHandler.class);

        ISFSObject settings = (ISFSObject) this.getParentZone().getExtension().handleInternalMessage("getFieldCfg", null);
        game = new GameController(this, settings);

        // Schedule task: 30ms is nearly 30 fps used by the Flash client;
        gameTask = sfs.getTaskScheduler().scheduleAtFixedRate(game, 0, 30, TimeUnit.MILLISECONDS);
    }

    @Override
    public void destroy()
    {
        gameTask.cancel(true);
    }

    public GameController getGame() {
        return game;
    }

    public void addPersonage(User user) {
        ISFSObject settings = new SFSObject();

        game.createPersonage(user.getId(), settings);
    }

    public void removePersonage(int userId)
    {
        game.removePersonage(userId);
    }



    public void setPersonageData(int userId, int x, int y, int color) {
        User user = room.getUserById(userId);

        if (user != null) {
            List<UserVariable> vars = new ArrayList<UserVariable>();
            vars.add(new SFSUserVariable(UserProps.POSX, x));
            vars.add(new SFSUserVariable(UserProps.POSY, y));
            vars.add(new SFSUserVariable(UserProps.COLOR, color));
            getApi().setUserVariables(user, vars);
        }
    }

    public void setPersonageState(int userId, int x, int y, float direction) {
        User user = room.getUserById(userId);

        if (user != null) {
            List<UserVariable> vars = new ArrayList<UserVariable>();
            vars.add(new SFSUserVariable(UserProps.POSX, x));
            vars.add(new SFSUserVariable(UserProps.POSY, y));
            vars.add(new SFSUserVariable(UserProps.DIRECTION, direction));
            getApi().setUserVariables(user, vars);
        }
    }

    public void setBulletsPositions(int userId, Map<Integer, Bullet> bullets) {
        User user = room.getUserById(userId);

        List<RoomVariable> vars = new ArrayList<RoomVariable>();

        SFSArray bulletsArray = new SFSArray();
        for (Iterator<Map.Entry<Integer, Bullet>> it = bullets.entrySet().iterator(); it.hasNext(); ) {
            Bullet bullet = it.next().getValue();

            SFSObject bulletData = new SFSObject();
            bulletData.putInt(UserProps.ID, bullet.getItemId());
            bulletData.putInt(UserProps.USER, bullet.getOwnerId());
            bulletData.putInt(UserProps.POSX, bullet.getX());
            bulletData.putInt(UserProps.POSY, bullet.getY());
            bulletData.putFloat(UserProps.DIRECTION, bullet.getDirection());
            bulletData.putFloat(UserProps.SPEED, bullet.getSpeed());

            bulletsArray.addSFSObject(bulletData);
        }
        vars.add(new SFSRoomVariable(UserProps.BULLETS, bulletsArray));

        getApi().setRoomVariables(user, room, vars);
    }
}
