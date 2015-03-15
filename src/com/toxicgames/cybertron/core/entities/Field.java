package com.toxicgames.cybertron.core.entities;

import com.smartfoxserver.v2.entities.data.ISFSObject;

/**
 * Created by desktop on 04.03.2015.
 */
public class Field {

    private ISFSObject settings;

    public int width;
    public int height;

    public Field(ISFSObject settings) {
        this.settings = settings;
    }

    public int getCellId(int x, int y) {
        return y * width + x;
    }

    public int getWidth() {
        return settings.getInt("width");
    }

    public int getHeight() { return settings.getInt("height"); }
}
