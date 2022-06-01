package com.justbru00.epic.icetrack.data;

import java.io.File;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import com.justbru00.epic.icetrack.main.EpicIceTrack;
import com.justbru00.epic.icetrack.map.Map;
import com.justbru00.epic.icetrack.map.MapManager;
import com.justbru00.epic.icetrack.utils.Messager;
import com.justbru00.epic.icetrack.utils.PluginFile;

public class AsyncFlatFileManager {

	private static ArrayList<AsyncCachedPluginFile> cachedFiles = new ArrayList<AsyncCachedPluginFile>();
	private static int repeatingTaskId = 0;
	
	public static void init() {		
		if (repeatingTaskId == 0) {
			BukkitTask task = Bukkit.getScheduler().runTaskTimerAsynchronously(EpicIceTrack.getInstance(), new Runnable() {
				
				@Override
				public void run() {
					runEvery30SecondsAsync();					
				}
			}, 30*20, 30*20);
			repeatingTaskId = task.getTaskId();
		}	
	}
	
	public static void onDisable() {
		Bukkit.getScheduler().cancelTask(repeatingTaskId);
		repeatingTaskId = 0;
		runEvery30SecondsAsync();
	}
	
	/**
	 * This method should be run async as it both reads and possibly saves a file which is a blocking operation.
	 * @param p
	 */
	public static void onPlayerJoin(UUID playerUuid) {
		// Check if file exists and that it has some data.
		// If it doesn't we need to put some default data in it.
		loadAndCacheFile(playerUuid);
		
		// Check if the file is empty
		// Personal Data File
		Optional<AsyncCachedPluginFile> possiblePdf = getCachedFile(playerUuid);
		
		if (possiblePdf.isEmpty()) {
			// This shouldn't happen ever
			Messager.debug("[AsyncFlatFileManager#onPlayerJoin] We couldn't find the cached file for this player. That shouldn't be possible as we have just created one.");
			return;
		}
		
		// Personal Data File
		PluginFile pdf = possiblePdf.get().getFile();
		
		// If the file is empty we need to fill it with default data.
		if (pdf.getString("lastname") == null) {
			// This file is empty and it needs to be filled.
			pdf.set("lastname", Bukkit.getOfflinePlayer(playerUuid).getName());
			pdf.set("currency", 0);
					// Map data storage keys are abbreviated to save storage space for large amounts of players.
					// u = unlocked
					// a = attempts
					// f = finishes
					// b = besttime					
					
					for (Map map : MapManager.getMaps()) {
						if (map.getPurchaseCost() == Map.UNLOCKED_DEFAULT) {
							pdf.set("maps." + map.getInternalName() + ".u", true);
						} else {
							pdf.set("maps." + map.getInternalName() + ".u", false);
						}
						
						pdf.set("maps." + map.getInternalName() + ".a", 0);
						pdf.set("maps." + map.getInternalName() + ".f", 0);
						pdf.set("maps." + map.getInternalName() + ".b", (long) -1);
					}
					pdf.save();
				}
	}
	
	/**
	 * This method needs to be run async every 30 seconds.
	 */
	private static void runEvery30SecondsAsync() {
		// save all files that are marked as needing saved.		
		for (AsyncCachedPluginFile f : cachedFiles) {
			if (f.isSaveNeeded()) {
				f.getFile().save();
				Messager.msgConsole("[AsyncFlatFileManager] &7Saved player data to disk for " + f.getPlayerUuid() + ".");
			}
		}
	}
	
	public static Optional<PlayerData> getPlayerData(UUID playerUuid) {		
		return getPlayerDataFromCachedFile(playerUuid);
	}
	
	public static Optional<AsyncCachedPluginFile> getCachedFile(UUID playerUuid) {
		for (AsyncCachedPluginFile f : cachedFiles) {
			if (f.getPlayerUuid().equals(playerUuid)) {
				return Optional.of(f);
			}
		}
		return Optional.empty();
	}
	
	private static Optional<PlayerData> getPlayerDataFromCachedFile(UUID playerUuid) {
		PluginFile pdf = null;
		
		Optional<AsyncCachedPluginFile> cachedFile = getCachedFile(playerUuid);			
		
		if (cachedFile.isEmpty()) {
			// This shouldn't happen ever
			Messager.debug("[AsyncFlatFileManager#getPlayerDataFromCachedFile] We couldn't find the cached file for this player."
					+ " This shouldn't be able to happen. If you are attempting to preform a leaderboard function please use #loadAndCacheFile() first.");
			return Optional.empty();
		}
		
		pdf = cachedFile.get().getFile();	
		
		PlayerData playerData = new PlayerData(playerUuid);
		
		// Load the values from the file 
		
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
			}					
		}
		
		playerData.setMapData(playerMapData);
		return Optional.of(playerData);		
	}
	
	/**
	 * This method is blocking while it reads the file. Make sure to run it async.
	 * This method won't read the file from disk if it is already cached.
	 * @param playerUuid
	 */
	public static void loadAndCacheFile(UUID playerUuid) {
		if (getCachedFile(playerUuid).isPresent()) {
			return;
		}
		
		File playersDataFile = new File(EpicIceTrack.getInstance().getDataFolder().getPath() + 
				File.separator + "flatfilestorage" + File.separator + playerUuid.toString() + ".yml");
		
		// Personal Data File
		PluginFile pdf = new PluginFile(EpicIceTrack.getInstance(), playersDataFile);
		
		AsyncCachedPluginFile cacheFile = new AsyncCachedPluginFile(pdf, false, playerUuid);
		cachedFiles.add(cacheFile);
	}
	
	
}
