package com.toxicgames.cybertron.core;

import com.smartfoxserver.v2.entities.data.ISFSObject;

public class GameItem {

	protected int ownerId;

	protected ISFSObject settings;
	
	public double x;
	public double y;
	public long lastRenderTime;
	
	public GameItem(int ownerId, ISFSObject settings)
	{
		this.ownerId = ownerId;
		this.settings = settings;
	}

	public int getOwnerId()
	{
		return ownerId;
	}
	
//	public String getModel()
//	{
//		return settings.getUtfString("model");
//	}
}