package com.justbru00.epic.icetrack.pathrecorder;

import org.bukkit.Location;

/**
 * This class hold the details for a single recorded point.
 * @author Justin Brubaker
 *
 */
public class PathPoint {
	
	private long time;
	private Location location;	
	
	public PathPoint(long time, Location location) {
		super();
		this.time = time;
		this.location = location;
	}
	
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public Location getLocation() {
		return location;
	}
	public void setLocation(Location location) {
		this.location = location;
	}
	
	
}
