package com.toxicgames.cybertron.core;

import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.exceptions.ExceptionMessageComposer;
import com.toxicgames.cybertron.enums.BulletProps;
import com.toxicgames.cybertron.enums.UserProps;
import com.toxicgames.cybertron.room.GameRoomExtension;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by mor on 25.02.2015.
 */
public class GameController extends Thread {

    private GameRoomExtension extension;

    private ISFSObject settings;
    public ISFSObject getWeapon(String name) {
        return settings.getSFSObject("weapons").getSFSObject(name);
    }

    private ISFSObject levels;
    public ISFSObject getLevel(String id) {
        return settings.getSFSObject(id);
    }

    private Field field;

    private Map<Integer, Personage> personages;
    private Map<Integer, Bullet> bullets;

    public GameController(GameRoomExtension extension, ISFSObject settings, ISFSObject levels) {
        this.extension = extension;
        this.settings = settings;
        this.levels = levels;
        this.field = new Field(settings.getSFSObject("field"));
        this.personages = new ConcurrentHashMap<Integer, Personage>();
        this.bullets = new ConcurrentHashMap<Integer, Bullet>();
    }

    public void createPersonage(int ownerId) {
        Personage personage = new Personage(ownerId, settings.getSFSObject("player"));
        personage.x = Math.round(Math.random() * field.getWidth());
        personage.y = Math.round(Math.random() * field.getHeight());
        personage.color = (int) (Math.random() * 0xFFFFFF);
		personage.lastRenderTime = System.currentTimeMillis();
		personages.put(ownerId, personage);

		sendPersonageData(personage);

        extension.setGameData(ownerId, settings);
    }

    public void updatePersonages() {
        for (Iterator<Map.Entry<Integer, Personage>> it = personages.entrySet().iterator(); it.hasNext(); ) {
            Personage personage = it.next().getValue();
            sendPersonageData(personage);
        }
    }

    public void removePersonage(int ownerId) {
		personages.remove(ownerId);
	}

    public void movePersonage(int ownerId, int deltaX, int deltaY) {
        Personage personage = personages.get(ownerId);
        personage.deltaX = deltaX == 0 ? 0 : (deltaX > 0 ? 1 : -1);
        personage.deltaY = deltaY == 0 ? 0 : (deltaY > 0 ? 1 : -1);
    }

    public void rotatePersonage(int ownerId, float direction) {
        Personage personage = personages.get(ownerId);
        personage.direction = direction;

        savePersonagePosition(personage);
    }

    public void shotUser(int ownerId, boolean shoot) {
        Personage personage = personages.get(ownerId);
        personage.isShooting = shoot;
    }

    public void createBullet(int ownerId, ISFSObject settings, float direction) {
        Personage personage = personages.get(ownerId);

        Bullet bullet = new Bullet(ownerId, settings, direction);
        bullet.x = personage.x + (float) Math.cos(personage.direction) * personage.getShotRadius();
        bullet.y = personage.y + (float) Math.sin(personage.direction) * personage.getShotRadius();
		bullet.lastRenderTime = System.currentTimeMillis();

		bullets.put(bullet.getItemId(), bullet);
    }

    @Override
    public void run() {
        try {
            for (Iterator<Map.Entry<Integer, Bullet>> it = bullets.entrySet().iterator(); it.hasNext(); ) {
                Bullet bullet = it.next().getValue();
                renderBullet(bullet);
            }

            for (Iterator<Map.Entry<Integer, Personage>> it = personages.entrySet().iterator(); it.hasNext(); ) {
                Personage personage = it.next().getValue();

                renderPersonage(personage);

                if (personage.deltaX != 0 || personage.deltaY != 0) {
                    savePersonagePosition(personage);
                }

                boolean hit = false;

                for (Iterator<Map.Entry<Integer, Bullet>> it2 = bullets.entrySet().iterator(); it2.hasNext(); ) {
                    Bullet bullet = it2.next().getValue();

                    if (bullet.x < 0 || bullet.x > field.getWidth() || bullet.y < 0 || bullet.y > field.getHeight()) {
                        bullets.remove(bullet.getItemId());
                    } else if (getDistance(personage, bullet) <= personage.getHitRadius()) {
                        bullets.remove(bullet.getItemId());

                        hit = true;
                    }
                }

                saveBulletsData(personage, bullets);
            }
        }
        catch (Exception e) {
            ExceptionMessageComposer emc = new ExceptionMessageComposer(e);
            extension.trace(emc.toString());
        }
    }

    private void renderPersonage(Personage personage) {
        long now = System.currentTimeMillis();
        double delta = (now - personage.lastRenderTime) / 1000.0;

        int mod = Math.abs(personage.deltaX) + Math.abs(personage.deltaY);
        double speed = mod != 0 ? personage.getSpeed() / Math.sqrt(mod) : personage.getSpeed();

        personage.x += personage.deltaX * speed * delta;
        personage.x = Math.max(0, Math.min(personage.x, field.getWidth()));
        personage.y += personage.deltaY * speed * delta;
        personage.y = Math.max(0, Math.min(personage.y, field.getHeight()));

        personage.shotCooldown -= delta;
        if (personage.isShooting && personage.shotCooldown <= 0) {
            ISFSObject weapon = getWeapon(personage.getWeapon());
            if (personage.ammo <= 0) {
                personage.ammo = weapon.getInt("ammo");
            }
            if (personage.ammo > 0) {
                personage.shotCooldown = weapon.getFloat("cooldown");
                personage.ammo--;

                if (personage.ammo <= 0) {
                    personage.shotCooldown = weapon.getFloat("reload");
                }

                createBullet(personage.getOwnerId(), weapon, personage.direction);
            }
        }

        personage.lastRenderTime = now;
    }

    private void renderBullet(Bullet bullet) {
        long now = System.currentTimeMillis();
        double delta = (now - bullet.lastRenderTime) / 1000.0;

        bullet.x += Math.cos(bullet.getDirection()) * bullet.getSpeed() * delta;
        bullet.y += Math.sin(bullet.getDirection()) * bullet.getSpeed() * delta;
        bullet.lastRenderTime = now;
    }

    private void sendPersonageData(Personage personage) {
		extension.setPersonageData(personage.getOwnerId(), personage.getX(), personage.getY(), personage.color);
	}

    private void savePersonagePosition(Personage personage) {
		extension.setPersonageState(personage.getOwnerId(), personage.getX(), personage.getY(), personage.direction);
	}

    private void saveBulletsData(Personage personage, Map<Integer, Bullet> bullets) {
		extension.setBulletsPositions(personage.getOwnerId(), bullets);
	}

    private double getDistance(GameItem simItem1, GameItem simItem2) {
        double dist_x = simItem1.x - simItem2.x;
        double dist_y = simItem1.y - simItem2.y;

        return Math.sqrt(Math.pow(dist_x, 2) + Math.pow(dist_y, 2));
    }
}
