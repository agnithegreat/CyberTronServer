package com.toxicgames.cybertron.room;

import com.smartfoxserver.v2.SmartFoxServer;
import com.smartfoxserver.v2.api.ISFSGameApi;
import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.entities.variables.RoomVariable;
import com.smartfoxserver.v2.entities.variables.SFSUserVariable;
import com.smartfoxserver.v2.entities.variables.UserVariable;
import com.smartfoxserver.v2.extensions.SFSExtension;
import com.smartfoxserver.v2.game.SFSGame;
import com.toxicgames.cybertron.enums.ClientRequest;
import com.toxicgames.cybertron.enums.UserProps;
import com.toxicgames.cybertron.handlers.UserDisconnectedEventHandler;
import com.toxicgames.cybertron.handlers.UserLeavedEventHandler;

import java.util.ArrayList;
import java.util.List;
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

    public void shotUser(User user, float direction) {
        ISFSObject settings = new SFSObject();
        settings.putDouble(UserProps.DIRECTION, direction);
        game.createBullet(user.getId(), settings);
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

    public int addBullet(String weapon, float x, float y, float direction, float speed) {
        List<RoomVariable> vars = buildBulletRoomVars(x, y, direction, speed);
//        vars.add(new MMOItemVariable(IV_MODEL, model));
//        vars.add(new MMOItemVariable(IV_TYPE, ITYPE_WEAPON));

//        MMOItem item = new MMOItem(vars);
//
//        return item.getId();
        return 0;
    }

    public void removeBullet(int mmoItemId) {
//        BaseMMOItem item = room.getMMOItemById(mmoItemId);
//
//        gameApi.removeMMOItem(item);
    }

    public void setBulletPosition(int mmoItemId, float x, float y, float direction, float speed) {
//        BaseMMOItem item = room.getMMOItemById(mmoItemId);
//
//        List<RoomVariable> vars = buildBulletRoomVars(x, y, direction, speed);
//        gameApi.setMMOItemVariables(item, vars, false);
    }

    public List<Integer> getBulletList(float x, float y) {
        List<Integer> shots = new ArrayList<Integer>();

//        // Get MMOItems in proximity
//        int intX = (int)Math.round(x);
//        int intY = (int)Math.round(y);
//        Vec3D pos = new Vec3D(intX, intY, 0);
//
//        List<BaseMMOItem> items = room.getProximityItems(pos);
//
//        // Get all MMOItems of type "weapon"
//        for (BaseMMOItem item : items) {
////            boolean isWeapon = item.getVariable(IV_TYPE).getStringValue().equals(ITYPE_WEAPON);
////
////            if (isWeapon)
//                shots.add(item.getId());
//        }

        return shots;
    }




    private List<RoomVariable> buildBulletRoomVars(float x, float y, float direction, float speed)  {
        List<RoomVariable> vars = new ArrayList<RoomVariable>();
//        vars.add(new RoomVariable(UserProps.POSX, x));
//        vars.add(new RoomVariable(UserProps.POSY, y));
//        vars.add(new RoomVariable(UserProps.DIRECTION, direction));
//        vars.add(new RoomVariable(UserProps.SPEED, speed));
        return vars;
    }
}
