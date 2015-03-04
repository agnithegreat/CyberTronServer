package com.toxicgames.cybertron.room;

import com.smartfoxserver.v2.SmartFoxServer;
import com.smartfoxserver.v2.api.ISFSMMOApi;
import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.entities.variables.SFSUserVariable;
import com.smartfoxserver.v2.entities.variables.UserVariable;
import com.smartfoxserver.v2.extensions.SFSExtension;
import com.smartfoxserver.v2.mmo.*;
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
    private ISFSMMOApi mmoApi;
    private MMORoom room;

    private GameController game;
    private ScheduledFuture<?> gameTask;

    //
    @Override
    public void init() {
        room = (MMORoom) this.getParentRoom();

        sfs = SmartFoxServer.getInstance();

        mmoApi = sfs.getAPIManager().getMMOApi();

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

    public void shotUser(User user, double direction) {
        ISFSObject settings = new SFSObject();
        settings.putDouble(UserProps.DIRECTION, direction);
        game.createBullet(user.getId(), settings);
    }





    public void setPersonageState(int userId, double x, double y, int color, boolean fireClientEvent) {
        User user = room.getUserById(userId);

        if (user != null) {
            List<UserVariable> vars = new ArrayList<UserVariable>();
            vars.add(new SFSUserVariable(UserProps.POSX, x));
            vars.add(new SFSUserVariable(UserProps.POSY, y));
            vars.add(new SFSUserVariable(UserProps.COLOR, color));
            getApi().setUserVariables(user, vars, fireClientEvent, false);

            int intX = (int)Math.round(x);
            int intY = (int)Math.round(y);
            Vec3D pos = new Vec3D(intX, intY, 0);
            mmoApi.setUserPosition(user, pos, this.getParentRoom());
        }
    }

    public int addBullet(String weapon, double x, double y, double direction, double speed) {
        List<IMMOItemVariable> vars = buildWeaponShotMMOItemVars(x, y, direction, speed);
//        vars.add(new MMOItemVariable(IV_MODEL, model));
//        vars.add(new MMOItemVariable(IV_TYPE, ITYPE_WEAPON));

        MMOItem item = new MMOItem(vars);

        setMMOItemPosition(item, x, y);

        return item.getId();
    }

    public void removeBullet(int mmoItemId) {
        BaseMMOItem item = room.getMMOItemById(mmoItemId);

        mmoApi.removeMMOItem(item);
    }

    public void setBulletPosition(int mmoItemId, double x, double y, double direction, double speed) {
        BaseMMOItem item = room.getMMOItemById(mmoItemId);

        List<IMMOItemVariable> vars = buildWeaponShotMMOItemVars(x, y, direction, speed);
        mmoApi.setMMOItemVariables(item, vars, false);

        setMMOItemPosition(item, x, y);
    }

    public List<Integer> getBulletList(double x, double y) {
        List<Integer> shots = new ArrayList<Integer>();

        // Get MMOItems in proximity
        int intX = (int)Math.round(x);
        int intY = (int)Math.round(y);
        Vec3D pos = new Vec3D(intX, intY, 0);

        List<BaseMMOItem> items = room.getProximityItems(pos);

        // Get all MMOItems of type "weapon"
        for (BaseMMOItem item : items) {
//            boolean isWeapon = item.getVariable(IV_TYPE).getStringValue().equals(ITYPE_WEAPON);
//
//            if (isWeapon)
                shots.add(item.getId());
        }

        return shots;
    }




    private List<IMMOItemVariable> buildWeaponShotMMOItemVars(double x, double y, double direction, double speed)  {
        List<IMMOItemVariable> vars = new ArrayList<IMMOItemVariable>();
        vars.add(new MMOItemVariable(UserProps.POSX, x));
        vars.add(new MMOItemVariable(UserProps.POSY, y));
        vars.add(new MMOItemVariable(UserProps.DIRECTION, direction));
        vars.add(new MMOItemVariable(UserProps.SPEED, speed));
        return vars;
    }

    private void setMMOItemPosition(BaseMMOItem item, double x, double y) {
        int intX = (int)Math.round(x);
        int intY = (int)Math.round(y);
        Vec3D pos = new Vec3D(intX, intY, 0);
        mmoApi.setMMOItemPosition(item, pos, this.getParentRoom());
    }
}
