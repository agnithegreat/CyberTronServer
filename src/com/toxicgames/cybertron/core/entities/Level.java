package com.toxicgames.cybertron.core.entities;

import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;

import java.awt.Rectangle;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by kirillvirich on 10.03.15.
 */
public class Level {

    private ISFSObject data;

    public int cellWidth;
    public int cellHeight;

    private Rectangle base;

    private Map<Integer, Rectangle> walls;
    private Map<Integer, Rectangle> towers;
    private Map<Integer, Rectangle> heroes;
    private Map<Integer, Rectangle> enemies;

    public Level(ISFSObject data) {
        this.data = data;

        ISFSObject cellSize = data.getSFSObject("cellSize");
        cellWidth = cellSize.getInt("width");
        cellHeight = cellSize.getInt("height");

        base = convertToRect(data.getSFSObject("base"));

        walls = convertToRectMap(data.getSFSArray("walls"));
        towers = convertToRectMap(data.getSFSArray("towers"));
        heroes = convertToRectMap(data.getSFSArray("heroes"));
        enemies = convertToRectMap(data.getSFSArray("enemies"));
    }

    public ISFSObject getData() {
        return data;
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

    public Map<Integer, Rectangle> getWalls() {
        return walls;
    }
    public Map<Integer, Rectangle> getTowers() {
        return towers;
    }

    public Rectangle getHeroSpawn(int id) {
        return heroes.get(id);
    }

    public Rectangle getEnemySpawn(int id) {
        return enemies.get(id);
    }


    private static Map<Integer, Rectangle> convertToRectMap(ISFSArray arr) {
        Map<Integer, Rectangle> map = new ConcurrentHashMap<Integer, Rectangle>();
        int l = arr.size();
        for (int i = 0; i < l; i++) {
            map.put(i, convertToRect(arr.getSFSObject(i)));
        }
        return map;
    }

    private static Rectangle convertToRect(ISFSObject data) {
        return new Rectangle(data.getInt("x"), data.getInt("y"), data.getInt("width"), data.getInt("height"));
    }
}
