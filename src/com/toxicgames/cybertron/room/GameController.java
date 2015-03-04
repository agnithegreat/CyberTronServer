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
            for (Iterator<User> iter = usersList.iterator(); iter.hasNext();) {

//                List = extension.getParentRoom().

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

    public void moveUser(int id, Integer deltaX, Integer deltaY) {

        User roomUser = extension.getParentRoom().getUserById(id);

        extension.trace(deltaX, deltaY);

        roomUser.setProperty(UserPropsEnum.X_DIRECTION, deltaX);
        roomUser.setProperty(UserPropsEnum.Y_DIRECTION, deltaY);

//        int posX = (Integer) roomUser.getProperty(UserPropsEnum.POSX) + deltaX;
//        int posY = (Integer) roomUser.getProperty(UserPropsEnum.POSY) + deltaY;
//        roomUser.getProperty(UserPropsEnum.X_DIRECTION)

    }
}
