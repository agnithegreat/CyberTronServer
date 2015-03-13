package com.toxicgames.cybertron.room;

import com.smartfoxserver.v2.SmartFoxServer;
import com.smartfoxserver.v2.api.ISFSGameApi;
import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.variables.RoomVariable;
import com.smartfoxserver.v2.entities.variables.SFSRoomVariable;
import com.smartfoxserver.v2.entities.variables.SFSUserVariable;
import com.smartfoxserver.v2.entities.variables.UserVariable;
import com.smartfoxserver.v2.extensions.SFSExtension;
import com.smartfoxserver.v2.game.SFSGame;
import com.toxicgames.cybertron.core.entities.Bullet;
import com.toxicgames.cybertron.core.GameController;
import com.toxicgames.cybertron.core.entities.Monster;
import com.toxicgames.cybertron.core.enums.ClientRequest;
import com.toxicgames.cybertron.core.enums.InternalCmd;
import com.toxicgames.cybertron.core.enums.RoomProps;
import com.toxicgames.cybertron.core.enums.UserProps;
import com.toxicgames.cybertron.handlers.ControlRequestHandler;
import com.toxicgames.cybertron.handlers.UserJoinedGameEventHandler;
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
        addEventHandler(SFSEventType.USER_DISCONNECT, UserLeavedEventHandler.class);

        addRequestHandler(ClientRequest.REQ_CONTROL, ControlRequestHandler.class);

        ISFSObject settings = (ISFSObject) this.getParentZone().getExtension().handleInternalMessage(InternalCmd.GET_CONFIGURATION, null);
        ISFSObject levels = (ISFSObject) this.getParentZone().getExtension().handleInternalMessage(InternalCmd.GET_LEVELS, null);
        game = new GameController(this, settings, levels);

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

    public void addHero(User user) {
        game.createHero(user.getId());
        game.updateForNewPlayer();
    }

    public void removeHero(int userId)
    {
        game.removeHero(userId);
    }



    public void setGameData(int userId, ISFSObject data) {
        User user = room.getUserById(userId);
        List<RoomVariable> vars = new ArrayList<RoomVariable>();
        vars.add(new SFSRoomVariable(RoomProps.CONFIG_DATA, data));
        getApi().setRoomVariables(user, room, vars);
    }

    public void setLevelData(int userId, ISFSObject data) {
        User user = room.getUserById(userId);
        List<RoomVariable> vars = new ArrayList<RoomVariable>();
        vars.add(new SFSRoomVariable(RoomProps.LEVEL_DATA, data));
        getApi().setRoomVariables(user, room, vars);
    }

    public void setHeroData(int userId, int x, int y, int color, String weapon, int reqId) {
        User user = room.getUserById(userId);

        if (user != null) {
            List<UserVariable> vars = new ArrayList<UserVariable>();
            vars.add(new SFSUserVariable(UserProps.REQ_ID, reqId));
            vars.add(new SFSUserVariable(UserProps.POSX, x));
            vars.add(new SFSUserVariable(UserProps.POSY, y));
            vars.add(new SFSUserVariable(UserProps.COLOR, color));
            vars.add(new SFSUserVariable(UserProps.WEAPON, weapon));
            getApi().setUserVariables(user, vars);
        }
    }

    public void setHeroState(int userId, int x, int y, float direction, int reqId) {
        User user = room.getUserById(userId);

        if (user != null) {
            List<UserVariable> vars = new ArrayList<UserVariable>();
            vars.add(new SFSUserVariable(UserProps.REQ_ID, reqId));
            vars.add(new SFSUserVariable(UserProps.POSX, x));
            vars.add(new SFSUserVariable(UserProps.POSY, y));
            vars.add(new SFSUserVariable(UserProps.DIRECTION, direction));
            getApi().setUserVariables(user, vars);
        }
    }

    public void setMonstersPositions(Map<Integer, Monster> monsters) {
        List<RoomVariable> vars = new ArrayList<RoomVariable>();

        SFSArray monstersArray = new SFSArray();
        for (Iterator<Map.Entry<Integer, Monster>> it = monsters.entrySet().iterator(); it.hasNext(); ) {
            Monster monster = it.next().getValue();
            monstersArray.addSFSObject(monster.getData());
        }
        vars.add(new SFSRoomVariable(RoomProps.MONSTERS, monstersArray));

        getApi().setRoomVariables(null, room, vars);

    }
    public void setBulletsPositions(Map<Integer, Bullet> bullets) {
        List<RoomVariable> vars = new ArrayList<RoomVariable>();

        SFSArray bulletsArray = new SFSArray();
        for (Iterator<Map.Entry<Integer, Bullet>> it = bullets.entrySet().iterator(); it.hasNext(); ) {
            Bullet bullet = it.next().getValue();
            bulletsArray.addSFSObject(bullet.getData());
        }
        vars.add(new SFSRoomVariable(RoomProps.BULLETS, bulletsArray));

        getApi().setRoomVariables(null, room, vars);
    }

    public void setBaseState(ISFSObject base) {
        List<RoomVariable> vars = new ArrayList<RoomVariable>();
        vars.add(new SFSRoomVariable(RoomProps.BASE, base));
        getApi().setRoomVariables(null, room, vars);
    }

    public void endGame(ISFSObject result) {
        List<RoomVariable> vars = new ArrayList<RoomVariable>();
        vars.add(new SFSRoomVariable(RoomProps.RESULT, result));
        getApi().setRoomVariables(null, room, vars);
    }
}
