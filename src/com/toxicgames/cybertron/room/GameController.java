package com.toxicgames.cybertron.room;

import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.variables.SFSUserVariable;
import com.smartfoxserver.v2.entities.variables.UserVariable;
import com.smartfoxserver.v2.exceptions.ExceptionMessageComposer;
import com.toxicgames.cybertron.core.Bullet;
import com.toxicgames.cybertron.core.Personage;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by mor on 25.02.2015.
 */
public class GameController extends Thread {

    private GameRoomExtension extension;
    private Map<Integer, Personage> personages;
    private Map<Integer, Bullet> bullets;

    public GameController(GameRoomExtension extension) {
        this.extension = extension;
        this.personages = new ConcurrentHashMap<Integer, Personage>();
        this.bullets = new ConcurrentHashMap<Integer, Bullet>();
    }

    public void createPersonage(int ownerId, ISFSObject settings) {
        Personage personage = new Personage(ownerId, settings);
        personage.x = Math.random() * 600;
        personage.y = Math.random() * 600;
		personage.lastRenderTime = System.currentTimeMillis();
		personages.put(ownerId, personage);

		savePersonagePosition(personage, true);
    }

    public void removePersonage(int ownerId) {
		personages.remove(ownerId);
	}

//    public void moveUser(int ownerId, Integer deltaX, Integer deltaY) {
//        Personage personage = personages.get(ownerId);
//        personage.deltaX = deltaX;
//        personage.deltaY = deltaY;
//
//        if (deltaX != 0 && deltaY != 0) {
////            extension.setStarshipMovement(ownerId, deltaX, deltaY);
//        } else {
//            savePersonagePosition(personage, true);
//        }
//    }

    public void createBullet(int ownerId, ISFSObject settings) {
        Personage personage = personages.get(ownerId);

        Bullet bullet = new Bullet(ownerId, settings);
        bullet.x = personage.x + Math.cos(personage.direction) * 5;
        bullet.y = personage.y + Math.sin(personage.direction) * 5;
		bullet.lastRenderTime = System.currentTimeMillis();

//        int id = extension.addBullet(bullet.getWeapon(), bullet.x, bullet.y, bullet.getDirection(), bullet.getSpeed());
//        bullet.setItemId(id);
//
//		bullets.put(id, bullet);
    }

    public void moveUser(int id, Integer deltaX, Integer deltaY) {
        User roomUser = extension.getParentRoom().getUserById(id);

        extension.trace(deltaX, deltaY);

        roomUser.setProperty(UserPropsEnum.X_DIRECTION, deltaX);
        roomUser.setProperty(UserPropsEnum.Y_DIRECTION, deltaY);
    }

    public void shotUser(int id, Float direction, int speed) {

        User roomUser = extension.getParentRoom().getUserById(id);

        extension.trace(direction);

        roomUser.setProperty(UserPropsEnum.DIRECTION, direction);
        roomUser.setProperty(UserPropsEnum.SPEED, speed);
    }

    @Override
    public void run() {

        try {
            List<User> usersList = extension.getParentRoom().getUserList();
            for (Iterator<User> iter = usersList.iterator(); iter.hasNext();) {

                User roomUser = iter.next();

                int deltaX = (Integer) roomUser.getProperty(UserPropsEnum.X_DIRECTION);
                int deltaY = (Integer) roomUser.getProperty(UserPropsEnum.Y_DIRECTION);

                int posX = (Integer) roomUser.getProperty(UserPropsEnum.POSX);
                int posY = (Integer) roomUser.getProperty(UserPropsEnum.POSY);



                if(deltaX != 0 && posX + deltaX <= 300 && posX + deltaX >= 0)
                {
                    roomUser.setProperty(UserPropsEnum.POSX, posX + deltaX);
                    posX = posX + deltaX;
                }

                if(deltaY != 0 && posY + deltaY <= 300 && posY + deltaY >= 0)
                {
                    roomUser.setProperty(UserPropsEnum.POSY, posY + deltaY);
                    posY = posY + deltaY;
                }


                UserVariable posXVar = new SFSUserVariable(UserProps.POSX, posX);
                UserVariable posYVar = new SFSUserVariable(UserProps.POSY, posY);
                if(deltaX != 0 || deltaY != 0)
                {
                    extension.getApi().setUserVariables(roomUser, Arrays.asList(posXVar, posYVar), true, false);
                }
            }

        } catch (Exception e) {
            // In case of exceptions this try-catch prevents the task to stop running
            ExceptionMessageComposer emc = new ExceptionMessageComposer(e);
            extension.trace(emc.toString());
        }
    }

    private void savePersonagePosition(Personage personage, boolean doUpdateClients) {
//		extension.setPersonageState(personage.getOwnerId(), personage.x, personage.y, personage.direction, doUpdateClients);
	}

    private void saveBulletPosition(Bullet bullet, boolean doUpdateClients) {
//		extension.setBulletPosition(bullet.getOwnerId(), bullet.x, bullet.y, bullet.getDirection(), bullet.getSpeed(), doUpdateClients);
	}

    private void removeBullet(Bullet bullet) {
//        extension.removeBullet(bullet.getItemId());
	}
}
