package com.toxicgames.cybertron.core;

import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.toxicgames.cybertron.enums.BulletProps;

/**
 * Created by kirillvirich on 04.03.15.
 */
public class Bullet extends GameItem {

    static public int count = 0;

    private int itemId = count++;
    public int getItemId() {
        return itemId;
    }

    private float direction;

    public Bullet(int ownerId, ISFSObject settings, float direction) {
        super(ownerId, settings);

        this.direction = direction;
    }

    public float getDirection() {
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
        data.putFloat(BulletProps.DIRECTION, getDirection());
//        data.putInt(BulletProps.SPEED, getSpeed());
        data.putUtfString(BulletProps.WEAPON, getWeapon());
        return data;
    }
}
