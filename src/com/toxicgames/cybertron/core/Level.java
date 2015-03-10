package com.toxicgames.cybertron.core;

import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.awt.Rectangle;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by kirillvirich on 10.03.15.
 */
public class Level {

    private ISFSObject data;

    private Rectangle base;

    private Map<Integer, Rectangle> heroes;
    private Map<Integer, Rectangle> enemies;

    public Level(ISFSObject data) {
        this.data = data;

        ISFSObject baseObj = data.getSFSObject("base");
        base = new Rectangle(baseObj.getInt("x"), baseObj.getInt("y"), baseObj.getInt("width"), baseObj.getInt("height"));

        heroes = new ConcurrentHashMap<Integer, Rectangle>();
        ISFSArray heroesArr = data.getSFSArray("heroes");
        for (int i = 0; i < heroesArr.size(); i++) {
            ISFSObject hero = heroesArr.getSFSObject(i);
            heroes.put(i, new Rectangle(hero.getInt("x"), hero.getInt("y"), hero.getInt("width"), hero.getInt("height")));
        }

        enemies = new ConcurrentHashMap<Integer, Rectangle>();
        ISFSArray enemiesArr = data.getSFSArray("enemies");
        for (int i = 0; i < enemiesArr.size(); i++) {
            ISFSObject enemy = enemiesArr.getSFSObject(i);
            enemies.put(i, new Rectangle(enemy.getInt("x"), enemy.getInt("y"), enemy.getInt("width"), enemy.getInt("height")));
        }
    }

    public int getId() {
        return data.getInt("id");
    }

    public Rectangle getBase() {
        return base;
    }

    public int getHeroSpawnCount() {
        return heroes.size();
    }

    public int getEnemySpawnCount() {
        return enemies.size();
    }

    public Rectangle getHeroSpawn(int id) {
        return heroes.get(id);
    }

    public Rectangle getEnemySpawn(int id) {
        return enemies.get(id);
    }
}
