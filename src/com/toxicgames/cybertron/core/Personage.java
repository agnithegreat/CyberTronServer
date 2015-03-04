package com.toxicgames.cybertron.core;

import com.smartfoxserver.v2.entities.data.ISFSObject;

/**
 * Created by kirillvirich on 04.03.15.
 */
public class Personage extends GameItem {

    public double deltaX = 0;
    public double deltaY = 0;

    public double direction;

    public Personage(int ownerId, ISFSObject settings) {
        super(ownerId, settings);
    }

    public double getShotSpeed() {
        return settings.getDouble("shotSpeed");
    }
}
