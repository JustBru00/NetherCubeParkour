package com.gmail.justbru00.nethercube.elytra.data;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import org.bukkit.OfflinePlayer;

import com.gmail.justbru00.nethercube.elytra.map.Map;
import com.gmail.justbru00.nethercube.elytra.map.MapManager;

import static com.gmail.justbru00.nethercube.elytra.main.NetherCubeElytra.dataFile;

public class PlayerData {

	private UUID uuid;
	private int currency;
	private ArrayList<PlayerMapData> mapData = new ArrayList<PlayerMapData>();
	
	public static PlayerData getDataFor(OfflinePlayer p) {
		PlayerData pd = new PlayerData(p.getUniqueId());
		
		if (dataFile.getConfigurationSection(p.getUniqueId().toString()) == null) {
			// Make the section as it does not exist.
			String pathPre = p.getUniqueId().toString() + ".";
			
			dataFile.set(pathPre + "currency", 0);
			
			pathPre = p.getUniqueId().toString() + ".maps.";
			
			for (Map map : MapManager.getMaps()) {
				if (map.getPurchaseCost() == Map.UNLOCKED_DEFAULT) {
					dataFile.set(pathPre + map.getInternalName() + ".unlocked", true);
				} else {
					dataFile.set(pathPre + map.getInternalName() + ".unlocked", false);
				}
				
				dataFile.set(pathPre + map.getInternalName() + ".attempts", 0);
				dataFile.set(pathPre + map.getInternalName() + ".finishes", 0);
				dataFile.set(pathPre + map.getInternalName() + ".besttime", (long) -1);
			}	
			dataFile.save();
		}
		
		// Get all data from config.
		
		String prePath = p.getUniqueId().toString() + ".maps.";
		
		pd.setCurrency(dataFile.getInt(p.getUniqueId().toString() + ".currency"));
		
		Set<String> mapsFromData = dataFile.getConfigurationSection(p.getUniqueId().toString() + ".maps").getKeys(false);
		
		ArrayList<PlayerMapData> playerMapData = new ArrayList<PlayerMapData>();
		
		for (Map map : MapManager.getMaps()) {
			if (mapsFromData.contains(map.getInternalName())) {
				// Get data for this map
				PlayerMapData mapData = new PlayerMapData(map.getInternalName());
				
				mapData.setUnlocked(dataFile.getBoolean(prePath + map.getInternalName() + ".unlocked"));
				mapData.setAttempts(dataFile.getInt(prePath + map.getInternalName() + ".attempts"));
				mapData.setFinishes(dataFile.getInt(prePath + map.getInternalName() + ".finishes"));
				mapData.setBestTime(dataFile.getLong(prePath + map.getInternalName() + ".besttime"));
				
				playerMapData.add(mapData);
			} else {
				// Create new section for this map
				boolean unlocked;
				if (map.getPurchaseCost() == Map.UNLOCKED_DEFAULT) {
					dataFile.set(prePath + map.getInternalName() + ".unlocked", true);
					unlocked = true;
				} else {
					dataFile.set(prePath + map.getInternalName() + ".unlocked", false);
					unlocked = false;
				}
				
				dataFile.set(prePath + map.getInternalName() + ".attempts", 0);
				dataFile.set(prePath + map.getInternalName() + ".finishes", 0);
				dataFile.set(prePath + map.getInternalName() + ".besttime", (long) -1);
				dataFile.save();
				playerMapData.add(new PlayerMapData(map.getInternalName(), unlocked, 0, 0, -1));
			}
		}
		
		pd.setMapData(playerMapData);		
		return pd;
	}
	
	/**
	 * Saves this data to the config. This will actually write to disk.
	 * This is used after changing a value
	 */
	public void save() {
		
		// Currency
		dataFile.set(uuid.toString() + ".currency", currency);
		
		// Player's map data
		for (PlayerMapData pmd : mapData) {
			String prePath = uuid.toString() + ".maps.";
			dataFile.set(prePath + pmd.getInternalName() + ".unlocked", pmd.isUnlocked());							
			dataFile.set(prePath + pmd.getInternalName() + ".attempts", pmd.getAttempts());
			dataFile.set(prePath + pmd.getInternalName() + ".finishes", pmd.getFinishes());
			dataFile.set(prePath + pmd.getInternalName() + ".besttime", pmd.getBestTime());
		}
		dataFile.save();
	}
	
	public PlayerMapData getMapData(String internalName) {
		for (PlayerMapData data : mapData) {
			if (data.getInternalName().equalsIgnoreCase(internalName)) {
				return data;
			}
		}
		
		return null;
	}
	
	public PlayerData(UUID playerId) {
		uuid = playerId;
	}

	public UUID getUuid() {
		return uuid;
	}

	public PlayerData setUuid(UUID uuid) {
		this.uuid = uuid;
		return this;
	}

	public int getCurrency() {
		return currency;
	}

	public PlayerData setCurrency(int currency) {
		this.currency = currency;
		return this;
	}

	public ArrayList<PlayerMapData> getMapData() {
		return mapData;
	}

	public PlayerData setMapData(ArrayList<PlayerMapData> mapData) {
		this.mapData = mapData;
		return this;
	}
	
	
	
}
