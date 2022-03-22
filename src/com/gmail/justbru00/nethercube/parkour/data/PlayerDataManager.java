package com.gmail.justbru00.nethercube.parkour.data;

import java.io.File;
import java.util.ArrayList;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import com.gmail.justbru00.nethercube.parkour.main.NetherCubeParkour;
import com.gmail.justbru00.nethercube.parkour.map.Map;
import com.gmail.justbru00.nethercube.parkour.map.MapManager;
import com.gmail.justbru00.nethercube.parkour.utils.Messager;
import com.gmail.justbru00.nethercube.parkour.utils.PluginFile;

public class PlayerDataManager {

	private static ArrayList<PlayerData> dataForOnlinePlayers;
	
	private static BukkitScheduler scheduler = Bukkit.getScheduler();
	
	/**
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
	
	private static void migrateV1ToV2(OfflinePlayer offlinePlayer) {
		// TODO Check if this server still has an old data.yml file.
		// If it does we need to transfer the old data format to the new format.
		// If a players data has already been migrated the players UUID section in the data.yml file will simply be a string
		// with "Migrated To V2" Ex. 28f9bb08-b33c-4a7d-b098-ebf271383966: Migrated To V2
		File dataYml = new File(NetherCubeParkour.getInstance().getDataFolder().getPath() + 
				File.separator + "data.yml");
		if (dataYml.exists()) {			
			PluginFile dataFile = new PluginFile(NetherCubeParkour.getInstance(), dataYml);
			
			if (dataFile.getConfigurationSection(offlinePlayer.getUniqueId().toString()) != null) {
				// Section exists for this player
				if (!dataFile.getString(offlinePlayer.getUniqueId().toString()).equalsIgnoreCase("Migrated To V2")) {
					// We need to migrate this players data.
					Messager.msgConsole("&6[PlayerDataManager] Attempting to migrate player data for " + offlinePlayer.getUniqueId() + " from v1 to v2.");
					// TODO Load this data from dataFile and save it to a new /NetherCubeParkour/flatfilestorage/uuid.yml file.
				}				
			}
		}
	}
	
	private static void getPlayerDataAsyncAndRunCallback(final OfflinePlayer offlinePlayer, final PlayerDataCallback callback) {
		scheduler.runTaskAsynchronously(NetherCubeParkour.getInstance(), new Runnable() {			
			@Override
			public void run() {				
				migrateV1ToV2(offlinePlayer);
				
				// Load or create the player data from yaml files located in /NetherCubeParkour/flatfilestorage/uuid.yml
				final PlayerData playerData = new PlayerData(offlinePlayer.getUniqueId());
				
				File playersDataFile = new File(NetherCubeParkour.getInstance().getDataFolder().getPath() + 
						File.separator + "flatfilestorage" + File.separator + offlinePlayer.getUniqueId() + ".yml");
				
				// Personal Data File
				PluginFile pdf = new PluginFile(NetherCubeParkour.getInstance(), playersDataFile);
				
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
						if (map.getPurchaseCost() == Map.UNLOCKED_DEFAULT) {
							pdf.set("maps." + map.getInternalName() + ".u", true);
							unlocked = true;
						} else {
							pdf.set("maps." + map.getInternalName() + ".u", false);
							unlocked = false;
						}
						
						pdf.set("maps." + map.getInternalName() + ".a", 0);
						pdf.set("maps." + map.getInternalName() + ".f", 0);
						pdf.set("maps." + map.getInternalName() + ".b", (long) -1);
						pdf.save();
						playerMapData.add(new PlayerMapData(map.getInternalName(), unlocked, 0, 0, -1));
					}						
				}
				
				playerData.setMapData(playerMapData);
				
				// Pass the loaded PlayerData object to the callback.
				scheduler.runTask(NetherCubeParkour.getInstance(), new Runnable() {
					
					@Override
					public void run() {
						callback.inSync(playerData);						
					}
				});
			}
		});
	}
	
}
