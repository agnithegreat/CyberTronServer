package com.toxicgames.cybertron.room;

import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.variables.SFSUserVariable;
import com.smartfoxserver.v2.entities.variables.UserVariable;
import com.smartfoxserver.v2.exceptions.ExceptionMessageComposer;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by mor on 25.02.2015.
 */
public class GameController extends Thread {

    private GameRoomExtension extension = null;

    public GameController(GameRoomExtension extension) {
        this.extension = extension;
    }

    @Override
    public void run() {

        try {
            List<User> usersList = extension.getParentRoom().getUserList();
            int count = 0;
            for (Iterator<User> iter = usersList.iterator(); iter.hasNext();) {

//                List = extension.getParentRoom().

                User roomUser = iter.next();

                int deltaX = (Boolean) roomUser.getProperty(UserProps.X_DIRECTION) ? 1 : -1;
                int deltaY = (Boolean) roomUser.getProperty(UserProps.Y_DIRECTION) ? 1 : -1;

                int posX = (Integer) roomUser.getProperty(UserProps.POSX) + deltaX;
                int posY = (Integer) roomUser.getProperty(UserProps.POSY) + deltaY;
//                posX = posX + deltaX;
//                pos = posX + deltaX;
//                Vec3D newPos = new Vec3D(pos.floatX() + deltaX, pos.floatY() + deltaY);

                if(posX >= 300 || posX <= 0)
                {
                    roomUser.setProperty(UserProps.X_DIRECTION, deltaX * -1 > 0);
                }

                if(posY >= 300 || posY <= 0)
                {
                    roomUser.setProperty(UserProps.Y_DIRECTION, deltaY * -1 > 0);
                }

                roomUser.setProperty(UserProps.POSX, posX);
                roomUser.setProperty(UserProps.POSY, posY);

                UserVariable posXVar = new SFSUserVariable("posX", posX);
                UserVariable posYVar = new SFSUserVariable("posY", posY);
//                UserVariable colorVar = new SFSUserVariable("color", (int) Math.random() * 0xFFFFFF);

                extension.getApi().setUserVariables(roomUser, Arrays.asList(posXVar, posYVar), true, false);


            }

        } catch (Exception e) {
            // In case of exceptions this try-catch prevents the task to stop running
            ExceptionMessageComposer emc = new ExceptionMessageComposer(e);
            extension.trace(emc.toString());
        }
    }
}
