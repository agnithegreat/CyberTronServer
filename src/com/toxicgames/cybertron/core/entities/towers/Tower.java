package com.toxicgames.cybertron.core.entities.towers;

import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSDataWrapper;
import com.toxicgames.cybertron.core.entities.GameItem;
import com.toxicgames.cybertron.core.utils.CastUtil;

/**
 * Created by desktop on 14.03.2015.
 */
public class Tower extends GameItem {

    public double cooldown;

    public Tower(ISFSObject settings) {
        super(0, settings);
    }

    public ISFSObject getData() {
        return settings;
    }

    public String getName() {
        return settings.getUtfString("name");
    }

    public int getDamage() {
        return settings.getInt("damage");
    }

    public double getCooldown() {
        SFSDataWrapper data = settings.get("cooldown");
        return CastUtil.extractDouble(data.getObject());
    }

    public int getRadius() {
        return settings.getInt("radius");
    }
}
