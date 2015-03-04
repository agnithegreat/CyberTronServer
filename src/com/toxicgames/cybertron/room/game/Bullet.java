package com.toxicgames.cybertron.room.game;

import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.toxicgames.cybertron.room.UserProps;

/**
 * Created by kirillvirich on 04.03.15.
 */
public class Bullet {

    private int id;

    private Weapon weapon;

    private Double posX;
    private Double posY;

    private Double direction;
    private Double speed;

    public Bullet(int id, Weapon weapon, Double direction) {
        this.id = id;
        this.weapon = weapon;

        this.posX = 1.0 * weapon.owner.user.getVariable(UserProps.POSX).getIntValue();
        this.posY = 1.0 * weapon.owner.user.getVariable(UserProps.POSY).getIntValue();

        this.direction = direction;
        this.speed = direction;
    }

    public void tick(Double time) {
        posX += Math.cos(direction) * speed * time;
        posY += Math.sin(direction) * speed * time;
    }

    public ISFSObject toSFSObject() {
        ISFSObject sfso = new SFSObject();
        sfso.putInt("bulletId", id);
        sfso.putInt("posX", (int) Math.round(posX));
        sfso.putInt("posY", (int) Math.round(posY));
        return sfso;
    }
}
