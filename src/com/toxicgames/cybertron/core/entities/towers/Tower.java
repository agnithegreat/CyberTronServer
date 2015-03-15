package com.toxicgames.cybertron.core.entities.towers;

import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSDataWrapper;
import com.toxicgames.cybertron.core.entities.GameItem;
import com.toxicgames.cybertron.core.entities.Weapon;
import com.toxicgames.cybertron.core.utils.CastUtil;

/**
 * Created by desktop on 14.03.2015.
 */
public class Tower extends GameItem {

    private int itemId;

    public Weapon weapon;

    public Tower(int id, ISFSObject settings) {
        super(0, settings);

        this.itemId = id;
    }

    public int getItemId() {
        return itemId;
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
