package com.gmail.justbru00.nethercube.elytra.map;

import org.bukkit.Location;

public class MapPlateDetails {

	private Location plateLocation;
	private boolean startingLocation;
	private Map mapPlateBelongsTo;	
	
	public MapPlateDetails(Location plateLocation, boolean startingLocation, Map mapPlateBelongsTo) {
		super();
		this.plateLocation = plateLocation;
		this.startingLocation = startingLocation;
		this.mapPlateBelongsTo = mapPlateBelongsTo;
	}
	public Location getPlateLocation() {
		return plateLocation;
	}
	public void setPlateLocation(Location plateLocation) {
		this.plateLocation = plateLocation;
	}
	public boolean isStartingLocation() {
		return startingLocation;
	}
	public void setStartingLocation(boolean startingLocation) {
		this.startingLocation = startingLocation;
	}
	public Map getMapPlateBelongsTo() {
		return mapPlateBelongsTo;
	}
	public void setMapPlateBelongsTo(Map mapPlateBelongsTo) {
		this.mapPlateBelongsTo = mapPlateBelongsTo;
	}
	
	
}
