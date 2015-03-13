package com.toxicgames.cybertron.core;

import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSDataWrapper;

/**
 * Created by kirillvirich on 12.03.15.
 */
public class Weapon {

    private ISFSObject settings;

    public int ammo = 0;

    public boolean isShooting;
    public double cooldown;

    public Weapon(ISFSObject settings) {
        this.settings = settings;
    }

    public void reload() {
        ammo = settings.getInt("ammo");
    }

    public void shot() {
        ammo--;

        if (ammo <= 0) {
            cooldown = getReload();
        } else {
            cooldown = getCooldown();
        }
    }

    public ISFSObject getData() {
        return settings;
    }

    public String getName() {
        return settings.getUtfString("name");
    }

    public int getSpeed() {
        return settings.getInt("speed");
    }

    public int getDamage() {
        return settings.getInt("damage");
    }

    public double getSpread() {
        SFSDataWrapper data = settings.get("spread");
        return Utils.extractDouble(data.getObject());
//        if (data.getTypeId() == SFSDataType.INT) {
//            return (Integer) data.getObject() * 1.0;
//        }

//        Double.valueOf((String) data.getObject());

//        return Double.valueOf(data.getObject().toString());
//        return (Double) data.getObject();
    }

    public int getShotAmount() {
        return settings.getInt("shotamount");
    }

    public double getCooldown() {
        SFSDataWrapper data = settings.get("cooldown");
        return Utils.extractDouble(data.getObject());
//        if (data.getTypeId() == SFSDataType.INT) {
//            return (Integer) data.getObject() * 1.0;
//        }
//        return Double.valueOf(data.getObject().toString());
//        return (Double) data.getObject();
    }

    public double getReload() {
        SFSDataWrapper data = settings.get("reload");
        return Utils.extractDouble(data.getObject());

//        if (data.getTypeId() == SFSDataType.INT) {
//            return (Integer) data.getObject() * 1.0;
//        }
//        return Double.valueOf(data.getObject().toString());
//        return (Double) data.getObject();
    }
}
