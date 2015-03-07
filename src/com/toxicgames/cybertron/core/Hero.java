package com.toxicgames.cybertron.core;

import com.smartfoxserver.v2.entities.data.ISFSObject;

/**
 * Created by kirillvirich on 04.03.15.
 */
public class Hero extends GameItem {

    public int requestCounter = 0;

    public int color = 0;

    public int deltaX = 0;
    public int deltaY = 0;

    public float direction;

    public int ammo;
    public boolean isShooting;
    public float shotCooldown;

    public String weapon;

    public Hero(int ownerId, ISFSObject settings) {
        super(ownerId, settings);
    }

    public int getSpeed() {
        return settings.getInt("speed");
    }

    public int getHitRadius() {
        return settings.getInt("hitRadius");
    }
    public int getShotRadius() {
        return settings.getInt("shotRadius");
    }
}
