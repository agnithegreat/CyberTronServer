package com.toxicgames.cybertron.core;

import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.toxicgames.cybertron.enums.UserProps;

/**
 * Created by kirillvirich on 04.03.15.
 */
public class Monster extends GameItem {

    static public int count = 0;

    private int itemId = count++;
    public int getItemId() {
        return itemId;
    }

    public float direction;

    public int hp;

    public long lastRenderTime;

    public Monster(ISFSObject settings) {
        super(0, settings);

        hp = settings.getInt("hp");
    }

    public int getHitRadius() {
        return settings.getInt("hitRadius");
    }

    public int getSpeed() {
        return settings.getInt("speed");
    }

    public SFSObject getData() {
        SFSObject data = new SFSObject();
        data.putInt(UserProps.ID, getItemId());
        data.putInt(UserProps.POSX, getX());
        data.putInt(UserProps.POSY, getY());
        data.putFloat(UserProps.DIRECTION, direction);
        data.putInt(UserProps.HP, hp);
//        data.putFloat(UserProps.SPEED, getSpeed());
        return data;
    }
}
