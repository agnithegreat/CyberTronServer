package com.toxicgames.cybertron;

import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.extensions.SFSExtension;
import com.toxicgames.cybertron.handlers.UserLoginEventHandler;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by mor on 25.02.2015.
 */
public class CyberTronExtension extends SFSExtension {

    private static final String CFG_FIELD_KEY = "field";

    private ISFSObject configuration;

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
        if (cmdName.equals("getFieldCfg")) {
            return getFieldCfg();
        }
        return null;
    }

    public ISFSObject getFieldCfg() {
        return configuration.getSFSObject(CFG_FIELD_KEY);
    }

    private void setupGame() throws IOException  {
        String cfgData = FileUtils.readFileToString(new File(this.getCurrentFolder() + "CyberTron.json"));

        ISFSObject tempCfg = SFSObject.newFromJsonData(cfgData);

        ISFSObject field = tempCfg.getSFSObject(CFG_FIELD_KEY);

        configuration = new SFSObject();
        configuration.putSFSObject(CFG_FIELD_KEY, field);
    }
}
