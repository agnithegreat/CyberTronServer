package com.toxicgames.cybertron.core.entities;

import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;

/**
 * Created by desktop on 11.03.2015.
 */
public class Base extends GameItem {

    public int hp;

    public Base(ISFSObject settings) {
        super(0, settings);

        this.hp = settings.getInt("hp");
    }

    public ISFSObject getData() {
        ISFSObject data = new SFSObject();
        data.putInt("hp", hp);
        return data;
    }
}
