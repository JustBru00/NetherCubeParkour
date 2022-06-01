package com.justbru00.epic.icetrack.map;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import com.justbru00.epic.icetrack.enums.MapDifficulty;
import com.justbru00.epic.icetrack.enums.MapLength;

public class Map {
	
	public static final int UNLOCKED_DEFAULT = -1;

	private String internalName;
	private ItemStack guiItem;
	private MapDifficulty difficulty;
	private MapLength length;
	private String creatorName;
	private int rewardAmount;
	private int purchaseCost;
	private Location startPlateLocation;
	private Location endingPlateLocation;
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
		this.difficulty = difficulty;
		this.length = length;
		this.creatorName = creatorName;
		this.rewardAmount = rewardAmount;
		this.purchaseCost = purchaseCost;
		this.startPlateLocation = startPlateLocation;
		this.endingPlateLocation = endingPlateLocation;
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
	public MapDifficulty getDifficulty() {
		return difficulty;
	}
	public void setDifficulty(MapDifficulty difficulty) {
		this.difficulty = difficulty;
	}
	public MapLength getLength() {
		return length;
	}
	public void setLength(MapLength length) {
		this.length = length;
	}
	public String getCreatorName() {
		return creatorName;
	}
	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}
	public int getRewardAmount() {
		return rewardAmount;
	}
	public void setRewardAmount(int rewardAmount) {
		this.rewardAmount = rewardAmount;
	}
	public int getPurchaseCost() {
		return purchaseCost;
	}
	public void setPurchaseCost(int purchaseCost) {
		this.purchaseCost = purchaseCost;
	}
	public Location getStartPlateLocation() {
		return startPlateLocation;
	}
	public void setStartPlateLocation(Location startPlateLocation) {
		this.startPlateLocation = startPlateLocation;
	}
	public Location getEndingPlateLocation() {
		return endingPlateLocation;
	}
	public void setEndingPlateLocation(Location endingPlateLocation) {
		this.endingPlateLocation = endingPlateLocation;
	}

	public boolean doesRequiresCheckpoint() {
		return requiresCheckpoint;
	}

	public void setRequiresCheckpoint(boolean requiresCheckpoint) {
		this.requiresCheckpoint = requiresCheckpoint;
	}	
}
