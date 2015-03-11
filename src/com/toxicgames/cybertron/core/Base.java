package com.toxicgames.cybertron.core;

import com.smartfoxserver.v2.entities.data.ISFSObject;

/**
 * Created by desktop on 11.03.2015.
 */
public class Base extends GameItem {

    public int hp;

    public Base(ISFSObject settings) {
        super(0, settings);

        this.hp = settings.getInt("hp");
    }
}
