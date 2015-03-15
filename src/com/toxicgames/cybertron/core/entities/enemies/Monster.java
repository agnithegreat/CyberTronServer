package com.toxicgames.cybertron.core.entities.enemies;

import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.toxicgames.cybertron.core.entities.GameItem;
import com.toxicgames.cybertron.core.enums.UserProps;
import pathfinder.GraphNode;

/**
 * Created by kirillvirich on 04.03.15.
 */
public class Monster extends GameItem {

    static public int count = 0;

    private int itemId = count++;
    public int getItemId() {
        return itemId;
    }

    public GraphNode[] path;
    public int node;
    public float direction;

    public int hp;

    public long lastRenderTime;

    public Monster(ISFSObject settings) {
        super(0, settings);

        hp = settings.getInt("hp");
    }

    public int getHitRadius() {
        return settings.getInt("hitradius");
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
