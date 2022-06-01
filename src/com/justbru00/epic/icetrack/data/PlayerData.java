package com.justbru00.epic.icetrack.data;

import static com.justbru00.epic.icetrack.main.EpicIceTrack.dataFile;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import com.justbru00.epic.icetrack.main.EpicIceTrack;
import com.justbru00.epic.icetrack.map.Map;
import com.justbru00.epic.icetrack.map.MapManager;
import com.justbru00.epic.icetrack.utils.Messager;
import com.justbru00.epic.icetrack.utils.PluginFile;

public class PlayerData {

	private UUID uuid;
	private int currency;
	private ArrayList<PlayerMapData> mapData = new ArrayList<PlayerMapData>();
	
	public static PlayerData getDataFor(OfflinePlayer p) {
		Optional<PlayerData> possiblePD = AsyncFlatFileManager.getPlayerData(p.getUniqueId());
		if (possiblePD.isEmpty()) {
			Messager.msgConsole("&c[PlayerData] The player data from AsyncFlatFileManager was empty. Something has gone badly.");
			return null;
		}
		
		return possiblePD.get();
	}
	
	/**
	 * @deprecated Replaced by {@link AsyncFlatFileManager}
	 * @param p
	 * @return
	 */
	public static PlayerData getDataForV1(OfflinePlayer p) {
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
	
	public void save() {
		Optional<AsyncCachedPluginFile> possible =  AsyncFlatFileManager.getCachedFile(uuid);
		
		if (possible.isEmpty()) {
			Messager.msgConsole("&cFailed to run PlayerData#save(). Something has gone terribly wrong.");
			return;
		}
		
		AsyncCachedPluginFile cachedFile = possible.get();
		PluginFile pdf = cachedFile.getFile();
		// Currency
		pdf.set(uuid.toString() + ".currency", currency);
				
		// Player's map data
		for (PlayerMapData pmd : mapData) {
			String prePath = "maps.";
			pdf.set(prePath + pmd.getInternalName() + ".u", pmd.isUnlocked());							
			pdf.set(prePath + pmd.getInternalName() + ".a", pmd.getAttempts());
			pdf.set(prePath + pmd.getInternalName() + ".f", pmd.getFinishes());
			pdf.set(prePath + pmd.getInternalName() + ".b", pmd.getBestTime());
		}
		cachedFile.setSaveNeeded(true);
	}
	
	/**
	 * @deprecated Replaced by {@link AsyncFlatFileManager}
	 * Saves this data to the config. This will actually write to disk.
	 * This is used after changing a value
	 */
	public void saveV1() {
		
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
		//dataFile.save();
		Bukkit.getScheduler().runTaskAsynchronously(EpicIceTrack.getInstance(), PlayerData::saveToDisk);
	}
	
	/**
	 * @deprecated Replaced by {@link AsyncFlatFileManager}
	 * This method is a syncronized method to make sure all data is saved and we don't lose anything.
	 * All changes are saved in order.
	 */
	private static synchronized void saveToDisk() {		
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
