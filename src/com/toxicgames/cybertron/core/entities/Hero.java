package com.toxicgames.cybertron.core.entities;

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

    public Weapon weapon;

    public Hero(int ownerId, ISFSObject settings) {
        super(ownerId, settings);
    }

    public int getSpeed() {
        return settings.getInt("speed");
    }

    public int getHitRadius() {
        return settings.getInt("hitradius");
    }
    public int getShotRadius() {
        return settings.getInt("shotradius");
    }
}
