package com.toxicgames.cybertron.core.entities;

import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.awt.*;

/**
 * Created by desktop on 11.03.2015.
 */
public class Base extends GameItem {

    public int hp;

    public Base(Rectangle rect, ISFSObject settings) {
        super(0, settings);

        x = rect.getCenterX();
        y = rect.getCenterY();
        width = rect.width;
        height = rect.height;

        this.hp = settings.getInt("hp");
    }

    public ISFSObject getData() {
        ISFSObject data = new SFSObject();
        data.putInt("hp", hp);
        return data;
    }
}
