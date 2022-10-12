package com.justbru00.epic.icetrack.pathrecorder;

import java.util.ArrayList;
import java.util.UUID;

/**
 * This class holds the data for path from a single run around a track. 
 * @author Justin Brubaker
 *
 */
public class Path {

	private UUID pathCreatorUuid;
	private String mapNamePathWasCreatedOn;
	private ArrayList<PathPoint> points = new ArrayList<PathPoint>();
	
	public ArrayList<PathPoint> getPoints() {
		return points;
	}

	public void setPoints(ArrayList<PathPoint> points) {
		this.points = points;
	}	
	
	public void addPoint(PathPoint point) {
		points.add(point);
	}

	public UUID getPathCreatorUuid() {
		return pathCreatorUuid;
	}

	public void setPathCreatorUuid(UUID pathCreatorUuid) {
		this.pathCreatorUuid = pathCreatorUuid;
	}

	public String getMapNamePathWasCreatedOn() {
		return mapNamePathWasCreatedOn;
	}

	public void setMapNamePathWasCreatedOn(String mapNamePathWasCreatedOn) {
		this.mapNamePathWasCreatedOn = mapNamePathWasCreatedOn;
	}	
		
}
