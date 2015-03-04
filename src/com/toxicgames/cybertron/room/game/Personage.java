package com.toxicgames.cybertron.room.game;

import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;

/**
 * Created by kirillvirich on 04.03.15.
 */
public class Personage {

    public User user;

    private Weapon weapon;

    private Double posX;
    private Double posY;

    public Personage(User user, Weapon weapon) {
        this.user = user;
        this.weapon = weapon;
    }

    public void place(Double x, Double y) {
        posX = x;
        posY = y;
    }

    public void move(Double deltaX, Double deltaY) {
        posX += deltaX;
        posY += deltaY;
    }

    public ISFSObject toSFSObject() {
        ISFSObject sfso = new SFSObject();
        sfso.putInt("userId", user.getId());
        sfso.putInt("posX", (int) Math.round(posX));
        sfso.putInt("posY", (int) Math.round(posY));
        return sfso;
    }
}
