package com.justbru00.epic.icetrack.data;

import java.util.UUID;

import org.bukkit.Bukkit;

import com.justbru00.epic.icetrack.main.EpicIceTrack;
import com.justbru00.epic.icetrack.utils.Messager;

public class Migrator {

	@SuppressWarnings("deprecation")
	public static void migrateV1toV2() {
		// Migrate player data from version 1 data.yml to version 2 flat file.
		// Then empty data.yml
		
		for (String key : EpicIceTrack.dataFile.getKeys(false)) {
			UUID uuid = UUID.fromString(key);
			AsyncFlatFileManager.loadAndCacheFile(uuid);
			
			PlayerData oldPlayerData = PlayerData.getDataForV1(Bukkit.getOfflinePlayer(uuid));
			
			PlayerData newPlayerData = PlayerData.getDataFor(Bukkit.getOfflinePlayer(uuid));
			newPlayerData.setCurrency(oldPlayerData.getCurrency());
			newPlayerData.setMapData(oldPlayerData.getMapData());
			newPlayerData.save();
			
			Messager.msgConsole("&7[Migrator] Copied data for " + uuid.toString() + " from data.yml to flatfilestorage.");
		}
	}
	
}
