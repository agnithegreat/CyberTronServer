package com.toxicgames.cybertron.core;

/**
 * Created by kirillvirich on 04.03.15.
 */
public class Weapon {

    public Personage owner;

    public int id;

    private Double speed;
    private int damage;

    public Weapon(int id, Personage owner, Double speed, int damage) {
        this.id = id;
        this.owner = owner;
        this.speed = speed;
        this.damage = damage;
    }
}
