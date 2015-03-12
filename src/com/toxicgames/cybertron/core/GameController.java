package com.toxicgames.cybertron.core;

import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.exceptions.ExceptionMessageComposer;
import com.toxicgames.cybertron.room.GameRoomExtension;

import java.awt.*;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by mor on 25.02.2015.
 */
public class GameController extends Thread {

    private final double spawnCooldown = 1.5;

    private long lastRenderTime;
    private GameRoomExtension extension;

    private ISFSObject settings;
    public ISFSObject getWeapon(String name) {
        return settings.getSFSObject("weapons").getSFSObject(name);
    }
    public ISFSObject getEnemy(String name) {
        return settings.getSFSObject("enemies").getSFSObject(name);
    }

    private ISFSObject levels;
    public ISFSObject getLevel(String id) {
        return levels.getSFSObject(id);
    }

    private Level level;

    private Field field;

    private Base base;

    private Map<Integer, Hero> heroes;
    private Map<Integer, Bullet> bullets;
    private Map<Integer, Monster> monsters;
    private float spawnTimeleft = 1;
    private int monstersLeft = 50;

    public GameController(GameRoomExtension extension, ISFSObject settings, ISFSObject levels) {
        this.extension = extension;
        this.settings = settings;
        this.levels = levels;

        this.field = new Field(settings.getSFSObject("field"));

        this.level = new Level(getLevel("1"));

        this.base = new Base(settings.getSFSObject("base"));
        base.x = level.getBase().getCenterX();
        base.y = level.getBase().getCenterY();
        base.width = level.getBase().width;
        base.height = level.getBase().height;

        this.heroes = new ConcurrentHashMap<Integer, Hero>();
        this.bullets = new ConcurrentHashMap<Integer, Bullet>();
        this.monsters = new ConcurrentHashMap<Integer, Monster>();
        this.lastRenderTime = System.currentTimeMillis();
    }

    public void createHero(int ownerId) {
        int id = heroes.size();
        Rectangle spawn = level.getHeroSpawn(id);

        Hero hero = new Hero(ownerId, settings.getSFSObject("hero"));
        hero.x = spawn.getCenterX();
        hero.y = spawn.getCenterY();
        hero.width = hero.getHitRadius();
        hero.height = hero.getHitRadius();
        hero.color = (int) (Math.random() * 0xFFFFFF);

        // TODO: remove randomized weapon
        hero.weapon = new Weapon(getWeapon(Math.random() < 0.5 ? "m4" : "shotgun"));
		hero.lastRenderTime = System.currentTimeMillis();
		heroes.put(ownerId, hero);

		sendHeroData(hero);

        extension.setGameData(ownerId, settings);
        extension.setLevelData(ownerId, level.getData());
    }

    public void updateForNewPlayer() {
        updateHeroes();
        saveBaseState();
    }

    public void updateHeroes() {
        for (Iterator<Map.Entry<Integer, Hero>> it = heroes.entrySet().iterator(); it.hasNext(); ) {
            Hero hero = it.next().getValue();
            sendHeroData(hero);
        }
    }

    public void removeHero(int ownerId) {
		heroes.remove(ownerId);
	}

    public void moveHero(int ownerId, int deltaX, int deltaY, int reqId) {
        Hero hero = heroes.get(ownerId);
        hero.requestCounter = reqId;
        hero.deltaX = deltaX == 0 ? 0 : (deltaX > 0 ? 1 : -1);
        hero.deltaY = deltaY == 0 ? 0 : (deltaY > 0 ? 1 : -1);
    }

    public void rotateHero(int ownerId, float direction, int reqId) {
        Hero hero = heroes.get(ownerId);
        hero.requestCounter = reqId;
        hero.direction = direction;

        saveHeroPosition(hero);
    }

    public void shotUser(int ownerId, boolean shoot, int reqId) {
        Hero hero = heroes.get(ownerId);
        hero.requestCounter = reqId;
        hero.weapon.isShooting = shoot;
    }

    public void createMonster() {
        int id = (int) (Math.random() * level.getEnemySpawnCount());
        Rectangle spawn = level.getEnemySpawn(id);

        Monster monster = new Monster(getEnemy("monster"));
        monster.x = spawn.getCenterX();
        monster.y = spawn.getCenterY();
        monster.width = monster.getHitRadius();
        monster.height = monster.getHitRadius();
        monster.direction = (float) (Math.random() * Math.PI * 2);
        monster.lastRenderTime = System.currentTimeMillis();
        monsters.put(monster.getItemId(), monster);
    }

    public void createBullet(int ownerId, ISFSObject settings, double direction) {
        Hero hero = heroes.get(ownerId);

        Bullet bullet = new Bullet(ownerId, settings, direction);
        bullet.x = hero.x + (float) Math.cos(hero.direction) * hero.getShotRadius();
        bullet.y = hero.y + (float) Math.sin(hero.direction) * hero.getShotRadius();
		bullet.lastRenderTime = System.currentTimeMillis();

		bullets.put(bullet.getItemId(), bullet);
    }

    @Override
    public void run() {
        try {
            long now = System.currentTimeMillis();
            double delta = (now - lastRenderTime) / 1000.0;

            for (Iterator<Map.Entry<Integer, Hero>> it = heroes.entrySet().iterator(); it.hasNext(); ) {
                Hero hero = it.next().getValue();
                hero.requestCounter++;
                renderHero(hero);

                if (hero.deltaX != 0 || hero.deltaY != 0) {
                    saveHeroPosition(hero);
                }
            }

            for (Iterator<Map.Entry<Integer, Monster>> mon = monsters.entrySet().iterator(); mon.hasNext(); ) {
                Monster monster = mon.next().getValue();
                renderMonster(monster);

                if (monster.hp <= 0) {
                    monsters.remove(monster.getItemId());
                } else {
                    if (base.getBounds().intersects(monster.getBounds())) {
                        monsters.remove(monster.getItemId());
                        base.hp--;

                        saveBaseState();
                    }
                }
            }

            spawnTimeleft -= delta;
            if (monstersLeft > 0 && spawnTimeleft <= 0) {
                spawnTimeleft += spawnCooldown;
                monstersLeft--;

                createMonster();
            }

            for (Iterator<Map.Entry<Integer, Bullet>> it = bullets.entrySet().iterator(); it.hasNext(); ) {
                Bullet bullet = it.next().getValue();
                renderBullet(bullet);

                if (bullet.x < 0 || bullet.x > field.getWidth() || bullet.y < 0 || bullet.y > field.getHeight()) {
                    bullets.remove(bullet.getItemId());
                }

                // ! ignore hero obstacles !
//                for (Iterator<Map.Entry<Integer, Hero>> it2 = heroes.entrySet().iterator(); it2.hasNext(); ) {
//                    Hero hero = it2.next().getValue();
//                    if (getDistance(hero, bullet) <= hero.getHitRadius()) {
//                        bullets.remove(bullet.getItemId());
//                    }
//                }

                for (Iterator<Map.Entry<Integer, Monster>> it2 = monsters.entrySet().iterator(); it2.hasNext(); ) {
                    Monster monster = it2.next().getValue();
                    if (getDistance(monster, bullet) <= monster.getHitRadius()) {
                        bullets.remove(bullet.getItemId());

                        monster.hp -= bullet.getDamage();
                    }
                }
            }

            saveBulletsData();

            saveMonstersData();

            if (base.hp <= 0) {
                endGame(false);
            } else if (monsters.size() == 0 && monstersLeft == 0) {
                endGame(true);
            }

            lastRenderTime = now;
        }
        catch (Exception e) {
            ExceptionMessageComposer emc = new ExceptionMessageComposer(e);
            extension.trace(emc.toString());
        }
    }

    private void renderHero(Hero hero) {
        long now = System.currentTimeMillis();
        double delta = (now - hero.lastRenderTime) / 1000.0;

        int mod = Math.abs(hero.deltaX) + Math.abs(hero.deltaY);
        double speed = mod != 0 ? hero.getSpeed() / Math.sqrt(mod) : hero.getSpeed();

        hero.x += hero.deltaX * speed * delta;
        hero.x = Math.max(0, Math.min(hero.x, field.getWidth()));
        hero.y += hero.deltaY * speed * delta;
        hero.y = Math.max(0, Math.min(hero.y, field.getHeight()));

        hero.weapon.cooldown -= delta;
        if (hero.weapon.isShooting && hero.weapon.cooldown <= 0) {
            if (hero.weapon.ammo <= 0) {
                hero.weapon.reload();
            }
            if (hero.weapon.ammo > 0) {
                hero.weapon.shot();

                int amount = hero.weapon.getShotAmount();
                double angle = hero.weapon.getSpread() / amount;
                for (int i = 0; i < amount; i++) {
                    double direction = hero.direction + (i - amount/2) * angle;
                    createBullet(hero.getOwnerId(), hero.weapon.getData(), direction);
                }
            }
        }

        hero.lastRenderTime = now;
    }

    private void renderMonster(Monster monster) {
        long now = System.currentTimeMillis();
        double delta = (now - monster.lastRenderTime) / 1000.0;

        monster.x += Math.cos(monster.direction) * monster.getSpeed() * delta;
        monster.x = Math.max(0, Math.min(monster.x, field.getWidth()));
        monster.y += Math.sin(monster.direction) * monster.getSpeed() * delta;
        monster.y = Math.max(0, Math.min(monster.y, field.getHeight()));

        double dx = base.x - monster.x;
        double dy = base.y - monster.y;
        monster.direction = (float) Math.atan2(dy, dx);

        monster.lastRenderTime = now;
    }

    private void renderBullet(Bullet bullet) {
        long now = System.currentTimeMillis();
        double delta = (now - bullet.lastRenderTime) / 1000.0;

        bullet.x += Math.cos(bullet.getDirection()) * bullet.getSpeed() * delta;
        bullet.y += Math.sin(bullet.getDirection()) * bullet.getSpeed() * delta;
        bullet.lastRenderTime = now;
    }

    private void sendHeroData(Hero hero) {
		extension.setHeroData(hero.getOwnerId(), hero.getX(), hero.getY(), hero.color, hero.requestCounter);
	}

    private void saveHeroPosition(Hero hero) {
		extension.setHeroState(hero.getOwnerId(), hero.getX(), hero.getY(), hero.direction, hero.requestCounter);
	}

    private void saveBulletsData() {
        extension.setBulletsPositions(bullets);
	}

    private void saveMonstersData() {
        extension.setMonstersPositions(monsters);
    }

    private void saveBaseState() {
        extension.setBaseState(base.getData());
    }

    private void endGame(boolean win) {
        ISFSObject result = new SFSObject();
        result.putBool("win", win);
        extension.endGame(result);
    }

    private double getDistance(GameItem simItem1, GameItem simItem2) {
        double dist_x = simItem1.x - simItem2.x;
        double dist_y = simItem1.y - simItem2.y;

        return Math.sqrt(Math.pow(dist_x, 2) + Math.pow(dist_y, 2));
    }
}
