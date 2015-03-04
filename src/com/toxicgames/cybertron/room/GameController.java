package com.toxicgames.cybertron.room;

import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.exceptions.ExceptionMessageComposer;
import com.toxicgames.cybertron.core.Bullet;
import com.toxicgames.cybertron.core.Field;
import com.toxicgames.cybertron.core.GameItem;
import com.toxicgames.cybertron.core.Personage;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by mor on 25.02.2015.
 */
public class GameController extends Thread {

    private GameRoomExtension extension;

    private Field field;

    private Map<Integer, Personage> personages;
    private Map<Integer, Bullet> bullets;

    public GameController(GameRoomExtension extension, ISFSObject settings) {
        this.extension = extension;
        this.field = new Field(settings);
        this.personages = new ConcurrentHashMap<Integer, Personage>();
        this.bullets = new ConcurrentHashMap<Integer, Bullet>();
    }

    public void createPersonage(int ownerId, ISFSObject settings) {
        Personage personage = new Personage(ownerId, settings);
        personage.x = Math.random() * field.getWidth();
        personage.y = Math.random() * field.getHeight();
        personage.color = (int) (Math.random() * 0xFFFFFF);
		personage.lastRenderTime = System.currentTimeMillis();
		personages.put(ownerId, personage);

		savePersonagePosition(personage, true);
    }

    public void removePersonage(int ownerId) {
		personages.remove(ownerId);
	}

    public void movePersonage(int ownerId, int deltaX, int deltaY) {
        Personage personage = personages.get(ownerId);
        personage.deltaX = deltaX;
        personage.deltaY = deltaY;

        savePersonagePosition(personage, true);
    }

    public void createBullet(int ownerId, ISFSObject settings) {
        Personage personage = personages.get(ownerId);

        Bullet bullet = new Bullet(ownerId, settings);
        bullet.x = personage.x + Math.cos(personage.direction) * 1;
        bullet.y = personage.y + Math.sin(personage.direction) * 1;
		bullet.lastRenderTime = System.currentTimeMillis();

        int id = extension.addBullet(bullet.getWeapon(), bullet.x, bullet.y, bullet.getDirection(), bullet.getSpeed());
        bullet.setItemId(id);

		bullets.put(id, bullet);
    }

    @Override
    public void run() {
        try {
            for (Iterator<Map.Entry<Integer, Bullet>> it = bullets.entrySet().iterator(); it.hasNext(); ) {
                Bullet bullet = it.next().getValue();
                renderBullet(bullet);
                saveBulletPosition(bullet);
            }

            for (Personage personage : personages.values()) {
                renderPersonage(personage);

                // Retrieve list of MMOItems in proximity to check the collision
                List<Integer> bulletIDs = extension.getBulletList(personage.x, personage.y);

                boolean hit = false;

                for (int i = 0; i < bulletIDs.size(); i++) {
                    int bulletID = bulletIDs.get(i);
                    Bullet bullet = bullets.get(bulletID);

                    // Check collision
                    if (getDistance(personage, bullet) <= 10) {
                        bullets.remove(bulletID);

                        removeBullet(bullet);

                        hit = true;
                    }
                }

                savePersonagePosition(personage, hit);
            }
        }
        catch (Exception e) {
            ExceptionMessageComposer emc = new ExceptionMessageComposer(e);
            extension.trace(emc.toString());
        }
    }

    private void renderPersonage(Personage personage) {
        long now = System.currentTimeMillis();
        long elapsed = now - personage.lastRenderTime;

        for (long i = 0; i < elapsed; i++) {
            personage.x += personage.deltaX * elapsed;
            personage.y += personage.deltaY * elapsed;
        }
        personage.lastRenderTime = now;
    }

    private void renderBullet(Bullet bullet) {
        long now = System.currentTimeMillis();
        long elapsed = now - bullet.lastRenderTime;

        for (long i = 0; i < elapsed; i++) {
            bullet.x += Math.cos(bullet.getDirection()) * bullet.getSpeed() * elapsed;
            bullet.y += Math.sin(bullet.getDirection()) * bullet.getSpeed() * elapsed;
        }
        bullet.lastRenderTime = now;
    }

    private void savePersonagePosition(Personage personage, boolean doUpdateClients) {
		extension.setPersonageState(personage.getOwnerId(), personage.x, personage.y, personage.color, doUpdateClients);
	}

    private void saveBulletPosition(Bullet bullet) {
		extension.setBulletPosition(bullet.getOwnerId(), bullet.x, bullet.y, bullet.getDirection(), bullet.getSpeed());
	}

    private void removeBullet(Bullet bullet) {
        extension.removeBullet(bullet.getItemId());
	}


    private double getDistance(GameItem simItem1, GameItem simItem2) {
        double dist_x = simItem1.x - simItem2.x;
        double dist_y = simItem1.y - simItem2.y;

        return Math.sqrt(Math.pow(dist_x, 2) + Math.pow(dist_y, 2));
    }
}
