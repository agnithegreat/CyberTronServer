package com.toxicgames.cybertron.core;

import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.exceptions.ExceptionMessageComposer;
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
        personage.x = Math.round(Math.random() * field.getWidth());
        personage.y = Math.round(Math.random() * field.getHeight());
        personage.color = (int) (Math.random() * 0xFFFFFF);
		personage.lastRenderTime = System.currentTimeMillis();
		personages.put(ownerId, personage);

		sendPersonageData(personage);
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
        personage.deltaX = deltaX;
        personage.deltaY = deltaY;
    }

    public void rotatePersonage(int ownerId, float direction) {
        Personage personage = personages.get(ownerId);
        personage.direction = direction;

        savePersonagePosition(personage);
    }

    public void shotUser(int ownerId, float direction) {
        Personage personage = personages.get(ownerId);
        personage.direction = direction;

        savePersonagePosition(personage);

        ISFSObject settings = new SFSObject();
        settings.putFloat(UserProps.DIRECTION, direction);
        settings.putFloat(UserProps.SPEED, 200);
        settings.putUtfString(UserProps.WEAPON, "weapon");
        createBullet(ownerId, settings);
    }

    public void createBullet(int ownerId, ISFSObject settings) {
        Personage personage = personages.get(ownerId);

        Bullet bullet = new Bullet(ownerId, settings);
        bullet.x = personage.x + (float) Math.cos(personage.direction) * 10;
        bullet.y = personage.y + (float) Math.sin(personage.direction) * 10;
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
                    } else if (getDistance(personage, bullet) <= 5) {
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

        personage.x += personage.deltaX * delta;
        personage.x = Math.max(0, Math.min(personage.x, field.getWidth()));
        personage.y += personage.deltaY * delta;
        personage.y = Math.max(0, Math.min(personage.y, field.getHeight()));

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

    private float getDistance(GameItem simItem1, GameItem simItem2) {
        float dist_x = simItem1.x - simItem2.x;
        float dist_y = simItem1.y - simItem2.y;

        return (float) Math.sqrt(Math.pow(dist_x, 2) + Math.pow(dist_y, 2));
    }
}
