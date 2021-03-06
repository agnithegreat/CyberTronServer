package com.toxicgames.cybertron.core.entities;

import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.toxicgames.cybertron.core.enums.BulletProps;

/**
 * Created by kirillvirich on 04.03.15.
 */
public class Bullet extends GameItem {

    private int itemId;
    public int getItemId() {
        return itemId;
    }

    private double direction;

    public Bullet(int ownerId, int bulletId, ISFSObject settings, double direction) {
        super(ownerId, settings);

        this.itemId = bulletId;
        this.direction = direction;
    }

    public double getDirection() {
        return direction;
    }
    public int getSpeed() {
        return settings.getInt("speed");
    }
    public int getDamage() {
        return settings.getInt("damage");
    }
    public String getWeapon() { return settings.getUtfString("name"); }

    public SFSObject getData() {
        SFSObject data = new SFSObject();
        data.putInt(BulletProps.ID, getItemId());
        data.putInt(BulletProps.USER, getOwnerId());
        data.putInt(BulletProps.POSX, getX());
        data.putInt(BulletProps.POSY, getY());
        data.putDouble(BulletProps.DIRECTION, getDirection());
//        data.putInt(BulletProps.SPEED, getSpeed());
        data.putUtfString(BulletProps.WEAPON, getWeapon());
        return data;
    }
}
