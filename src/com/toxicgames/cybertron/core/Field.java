package com.toxicgames.cybertron.core;

import com.smartfoxserver.v2.entities.data.ISFSObject;

/**
 * Created by desktop on 04.03.2015.
 */
public class Field {

    private ISFSObject settings;

    public Field(ISFSObject settings) {
        this.settings = settings;
    }

    public int getWidth() {
        return settings.getInt("width");
    }

    public int getHeight() { return settings.getInt("height"); }
}
