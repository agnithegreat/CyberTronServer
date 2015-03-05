package com.toxicgames.cybertron.core;

import com.smartfoxserver.v2.entities.data.ISFSObject;

/**
 * Created by kirillvirich on 04.03.15.
 */
public class Bullet extends GameItem {

    private int itemId;
    public void setItemId(int itemId) {
        this.itemId = itemId;
    }
    public int getItemId() {
        return itemId;
    }

    public Bullet(int ownerId, ISFSObject settings) {
        super(ownerId, settings);
    }

    public float getDirection() {
        return settings.getFloat("direction");
    }
    public float getSpeed() {
        return settings.getFloat("speed");
    }
    public String getWeapon() { return settings.getUtfString("weapon"); }
}
