package com.toxicgames.cybertron;

import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.extensions.SFSExtension;
import com.toxicgames.cybertron.enums.InternalCmd;
import com.toxicgames.cybertron.handlers.UserLoginEventHandler;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * Created by mor on 25.02.2015.
 */
public class CyberTronExtension extends SFSExtension {

    private ISFSObject configuration;
    private ISFSObject levels;

    @Override
    public void init() {
        try {
            setupGame();

            addEventHandler(SFSEventType.USER_LOGIN, UserLoginEventHandler.class);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object handleInternalMessage(String cmdName, Object params) {
        if (cmdName.equals(InternalCmd.GET_CONFIGURATION)) {
            return configuration;
        }
        if (cmdName.equals(InternalCmd.GET_LEVELS)) {
            return levels;
        }
        return null;
    }

    private void setupGame() throws IOException  {
        String cfgData = FileUtils.readFileToString(new File(this.getCurrentFolder() + "CyberTron.json"));
        configuration = SFSObject.newFromJsonData(cfgData);


        String[] extensions = new String[] { "json" };
        List<File> levelFiles = (List<File>) FileUtils.listFiles(new File(this.getCurrentFolder() + "levels/"), extensions, false);

        levels = new SFSObject();
        for (Iterator iterator = levelFiles.iterator(); iterator.hasNext();) {
            File file = (File) iterator.next();
            String levelData = FileUtils.readFileToString(file);
            ISFSObject level = SFSObject.newFromJsonData(levelData);
            trace(level.getInt("id"), level.getInt("id").toString());
            levels.putSFSObject(level.getInt("id").toString(), level);
        }
    }
}
