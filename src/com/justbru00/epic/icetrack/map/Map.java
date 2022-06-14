package com.justbru00.epic.icetrack.map;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import com.justbru00.epic.icetrack.enums.MapDifficulty;
import com.justbru00.epic.icetrack.enums.MapLength;

public class Map {
	
	public static final int UNLOCKED_DEFAULT = -1;

	private String internalName;
	private ItemStack guiItem;	
	private String creatorName;	
	private Location spawnLocation;
	private boolean requiresCheckpoint;
	
	public Map(String internalName) {
		this.internalName = internalName;
	}
	
	public Map(String internalName, ItemStack guiItem, MapDifficulty difficulty, MapLength length, String creatorName,
			int rewardAmount, int purchaseCost, Location startPlateLocation, Location endingPlateLocation, boolean requiresCheckpoint) {
		super();
		this.internalName = internalName;
		this.guiItem = guiItem;		
		this.creatorName = creatorName;		
		this.requiresCheckpoint = requiresCheckpoint;
	}	
	/**
	 * Gets the spawnpoint for this map
	 * @return
	 */
	public Location getSpawnLocation() {
		return spawnLocation;
	}

	public void setSpawnLocation(Location spawnLocation) {
		this.spawnLocation = spawnLocation;
	}

	public String getInternalName() {
		return internalName;
	}
	
	public void setInternalName(String internalName) {
		this.internalName = internalName;
	}
	
	public ItemStack getGuiItem() {
		return guiItem;
	}
	
	public void setGuiItem(ItemStack guiItem) {
		this.guiItem = guiItem;
	}
	
	public String getCreatorName() {
		return creatorName;
	}
	
	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}	
	
	public boolean doesRequiresCheckpoint() {
		return requiresCheckpoint;
	}

	public void setRequiresCheckpoint(boolean requiresCheckpoint) {
		this.requiresCheckpoint = requiresCheckpoint;
	}	
}
