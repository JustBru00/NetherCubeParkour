package com.justbru00.epic.icetrack.data;

import java.io.File;
import java.util.ArrayList;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import com.justbru00.epic.icetrack.main.EpicIceTrack;
import com.justbru00.epic.icetrack.map.Map;
import com.justbru00.epic.icetrack.map.MapManager;
import com.justbru00.epic.icetrack.utils.Messager;
import com.justbru00.epic.icetrack.utils.PluginFile;

/**
 * @deprecated Replaced by {@link AsyncFlatFileManager}
 *
 */
public class PlayerDataManager {

	private static ArrayList<PlayerData> dataForOnlinePlayers;
	
	private static BukkitScheduler scheduler = Bukkit.getScheduler();
	
	/**
	 * 
	 * Call this method when the plugin starts and after any plugin reload.
	 */
	public static void init() {
		dataForOnlinePlayers = new ArrayList<PlayerData>();
		
		if (Bukkit.getOnlinePlayers().size() > 0) {
			// Refill the cache.
			for (Player online : Bukkit.getOnlinePlayers()) {
				getPlayerDataAsyncAndRunCallback(Bukkit.getOfflinePlayer(online.getUniqueId()), new PlayerDataCallback() {					
					@Override
					public void inSync(PlayerData playerData) {						
						dataForOnlinePlayers.add(playerData);						
					}
				});
			}
		}
	}	
	
	public static synchronized void savePlayerDataAsync(PlayerData pd) {
		// Save to disk async.
		scheduler.runTaskAsynchronously(EpicIceTrack.getInstance(), new Runnable() {
			
			@Override
			public void run() {				
				File playersDataFile = new File(EpicIceTrack.getInstance().getDataFolder().getPath() + 
						File.separator + "flatfilestorage" + File.separator + pd.getUuid().toString() + ".yml");
				
				// Personal Data File
				PluginFile pdf = new PluginFile(EpicIceTrack.getInstance(), playersDataFile);
				
				// We ignore the lastname value as we don't know if the player is online. `lastname` is more for confirming that an admin is editing the right file.
				pdf.set("currency", pd.getCurrency());
				// Map data storage keys are abbreviated to save storage space for large amounts of players.
				// u = unlocked, a = attempts, f = finishes, b = besttime					
				
				for (PlayerMapData pmd : pd.getMapData()) {					
					pdf.set("maps." + pmd.getInternalName() + ".u", pmd.isUnlocked());					
					pdf.set("maps." + pmd.getInternalName() + ".a", pmd.getAttempts());
					pdf.set("maps." + pmd.getInternalName() + ".f", pmd.getFinishes());
					pdf.set("maps." + pmd.getInternalName() + ".b", pmd.getBestTime());
				}
				pdf.save();
			}
		});		
	}
	
	/**
	 * This method attempts to get the requested PlayerData from the built in cache.
	 * If it isn't in the cache, then we load it in sync with the calling process
	 * @param p
	 * @return
	 */
	public static PlayerData getPlayerData(OfflinePlayer p) {
		PlayerData matchingData = null;
		for (PlayerData pd : dataForOnlinePlayers) {
			if (pd.getUuid().equals(p.getUniqueId())) {
				matchingData = pd;
			}
		}
		
		if (matchingData == null) {
			// The requested player isn't online.
			return getPlayerDataSync(p);
		}
		
		return matchingData;
	}
	
	/**
	 * Call this method when a player joins.
	 * @param player
	 */
	public static void onPlayerJoin(Player player) {
		getPlayerDataAsyncAndRunCallback(Bukkit.getOfflinePlayer(player.getUniqueId()), new PlayerDataCallback() {
			
			@Override
			public void inSync(PlayerData playerData) {
				dataForOnlinePlayers.add(playerData);				
			}
		});
	}
	
	/**
	 * Call this method every five minutes or so
	 * This method clears the cache of players that are no longer online.
	 */
	public static void runEveryFiveMinutes() {
		for (PlayerData pd : dataForOnlinePlayers) {
			if (!Bukkit.getOfflinePlayer(pd.getUuid()).isOnline()) {
				dataForOnlinePlayers.remove(pd);
			}
		}
	}
	
	private static void migrateV1ToV2(OfflinePlayer offlinePlayer) {
		// Check if this server still has an old data.yml file.
		// If it does we need to transfer the old data format to the new format.
		// If a players data has already been migrated the players UUID section in the data.yml file will simply be a string
		// with "Migrated To V2" Ex. 28f9bb08-b33c-4a7d-b098-ebf271383966: Migrated To V2
		File dataYml = new File(EpicIceTrack.getInstance().getDataFolder().getPath() + 
				File.separator + "data.yml");
		if (!dataYml.exists()) {			
			// No data.yml file, no data to migrate.
			return;
		}	
		
		PluginFile dataFile = new PluginFile(EpicIceTrack.getInstance(), dataYml);
			
		if (dataFile.getConfigurationSection(offlinePlayer.getUniqueId().toString()) != null) {
			// Section exists for this player
			if (!dataFile.getString(offlinePlayer.getUniqueId().toString()).equalsIgnoreCase("Migrated To V2")) {
				// We need to migrate this players data.
				Messager.msgConsole("&6[PlayerDataManager] Attempting to migrate player data for " + offlinePlayer.getUniqueId() + " from v1 to v2.");
				// Load this data from dataFile and save it to a new /NetherCubeParkour/flatfilestorage/uuid.yml file.
				String pathPre = offlinePlayer.getUniqueId().toString() + ".";
				
				int currency = dataFile.getInt(pathPre + "currency");
				
				ArrayList<PlayerMapData> mapDataList = new ArrayList<PlayerMapData>();
				
				for (String mapKey : dataFile.getConfigurationSection(pathPre + "maps").getKeys(false)) {
					boolean unlocked = dataFile.getBoolean(pathPre + "map." + mapKey + ".unlocked");
					int attempts = dataFile.getInt(pathPre + "map." + mapKey + ".attempts");
					int finishes = dataFile.getInt(pathPre + "map." + mapKey + ".finishes");
					long besttime = dataFile.getLong(pathPre + "map." + mapKey + ".besttime");
					
					PlayerMapData aMapsData = new PlayerMapData(mapKey, unlocked, attempts, finishes, besttime);
					mapDataList.add(aMapsData);
				}
				
				// Write to the new file format
				File playersDataFile = new File(EpicIceTrack.getInstance().getDataFolder().getPath() + 
						File.separator + "flatfilestorage" + File.separator + offlinePlayer.getUniqueId().toString() + ".yml");
				
				// Personal Data File
				PluginFile pdf = new PluginFile(EpicIceTrack.getInstance(), playersDataFile);
				
				pdf.set("lastname", offlinePlayer.getName());
				pdf.set("currency", currency);
				// Map data storage keys are abbreviated to save storage space for large amounts of players.
				// u = unlocked, a = attempts, f = finishes, b = besttime					
				
				for (PlayerMapData pmd : mapDataList) {					
					pdf.set("maps." + pmd.getInternalName() + ".u", pmd.isUnlocked());					
					pdf.set("maps." + pmd.getInternalName() + ".a", pmd.getAttempts());
					pdf.set("maps." + pmd.getInternalName() + ".f", pmd.getFinishes());
					pdf.set("maps." + pmd.getInternalName() + ".b", pmd.getBestTime());
				}
				pdf.save();
				
				dataFile.set(offlinePlayer.getUniqueId().toString(), "Migrated To V2");
				dataFile.save();
			}				
		}
		
	}
	
	private static PlayerData getPlayerDataSync(final OfflinePlayer offlinePlayer) {
		migrateV1ToV2(offlinePlayer);
		
		// Load or create the player data from yaml files located in /NetherCubeParkour/flatfilestorage/uuid.yml
		final PlayerData playerData = new PlayerData(offlinePlayer.getUniqueId());
		
		File playersDataFile = new File(EpicIceTrack.getInstance().getDataFolder().getPath() + 
				File.separator + "flatfilestorage" + File.separator + offlinePlayer.getUniqueId().toString() + ".yml");
		
		// Personal Data File
		PluginFile pdf = new PluginFile(EpicIceTrack.getInstance(), playersDataFile);
		
		// If the file is empty we need to fill it with default data.
		if (pdf.getString("lastname") == null) {
			// This file is empty and it needs to be filled.
			pdf.set("lastname", offlinePlayer.getName());
			pdf.set("currency", 0);
			// Map data storage keys are abbreviated to save storage space for large amounts of players.
			// u = unlocked
			// a = attempts
			// f = finishes
			// b = besttime					
			
			for (Map map : MapManager.getMaps()) {
				pdf.set("maps." + map.getInternalName() + ".u", true);				
				pdf.set("maps." + map.getInternalName() + ".a", 0);
				pdf.set("maps." + map.getInternalName() + ".f", 0);
				pdf.set("maps." + map.getInternalName() + ".b", (long) -1);
			}
			pdf.save();
		}
		
		// Load the values from the file now that we are sure at least something exists.
		playerData.setCurrency(pdf.getInt("currency"));
		
		Set<String> mapKeys = pdf.getConfigurationSection("maps").getKeys(false);
		
		ArrayList<PlayerMapData> playerMapData = new ArrayList<PlayerMapData>();
		
		for (Map map : MapManager.getMaps()) {
			if (mapKeys.contains(map.getInternalName())) {
				// Load the data for this map
				PlayerMapData mapData = new PlayerMapData(map.getInternalName());
				
				mapData.setUnlocked(pdf.getBoolean("maps." + map.getInternalName() + ".u"));
				mapData.setAttempts(pdf.getInt("maps." + map.getInternalName() + ".a"));
				mapData.setFinishes(pdf.getInt("maps." + map.getInternalName() + ".f"));
				mapData.setBestTime(pdf.getLong("maps." + map.getInternalName() + ".b"));
				
				playerMapData.add(mapData);
			} else {
				// Missing the section for this map. We need to add it.
				boolean unlocked;
				
				pdf.set("maps." + map.getInternalName() + ".u", true);
				unlocked = true;				
				pdf.set("maps." + map.getInternalName() + ".a", 0);
				pdf.set("maps." + map.getInternalName() + ".f", 0);
				pdf.set("maps." + map.getInternalName() + ".b", (long) -1);
				pdf.save();
				playerMapData.add(new PlayerMapData(map.getInternalName(), unlocked, 0, 0, -1));
			}						
		}
		
		playerData.setMapData(playerMapData);
		return playerData;
	}
	
	private static void getPlayerDataAsyncAndRunCallback(final OfflinePlayer offlinePlayer, final PlayerDataCallback callback) {
		scheduler.runTaskAsynchronously(EpicIceTrack.getInstance(), new Runnable() {			
			@Override
			public void run() {			
				
				
				// Pass the loaded PlayerData object to the callback.
				scheduler.runTask(EpicIceTrack.getInstance(), new Runnable() {
					PlayerData playerData = getPlayerDataSync(offlinePlayer);
					@Override
					public void run() {
						callback.inSync(playerData);						
					}
				});
			}
		});
	}
	
}
