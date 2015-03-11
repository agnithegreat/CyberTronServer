package com.toxicgames.cybertron.core;

import com.smartfoxserver.v2.entities.data.ISFSObject;

import java.awt.*;

public class GameItem {

	protected int ownerId;

	protected ISFSObject settings;
	
	public double x;
	public double y;

	public int width = 0;
	public int height = 0;

	private Rectangle bounds = new Rectangle();

	public long lastRenderTime;
	
	public GameItem(int ownerId, ISFSObject settings) {
		this.ownerId = ownerId;
		this.settings = settings;
	}

	public int getOwnerId()
	{
		return ownerId;
	}

    public int getX() {
        return (int) Math.round(x);
    }
    public int getY() {
        return (int) Math.round(y);
    }

	public Rectangle getBounds() {
		bounds.x = (int) (x - width/2);
		bounds.y = (int) (y - height/2);
		bounds.width = width;
		bounds.height = height;
		return bounds;
	}
}
