package com.toxicgames.cybertron.core;

import com.smartfoxserver.v2.entities.data.ISFSObject;

/**
 * Created by kirillvirich on 04.03.15.
 */
public class Monster extends GameItem {

    static public int count = 0;

    private int itemId = count++;
    public int getItemId() {
        return itemId;
    }

    public long lastRenderTime;

    public Monster(ISFSObject settings) {
        super(0, settings);
    }

    public float getDirection() {
        return settings.getFloat("direction");
    }
    public float getSpeed() {
        return settings.getFloat("speed");
    }
}
