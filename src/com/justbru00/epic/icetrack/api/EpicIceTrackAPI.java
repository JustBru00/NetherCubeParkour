package com.justbru00.epic.icetrack.api;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.OfflinePlayer;

import com.justbru00.epic.icetrack.data.PlayerData;
import com.justbru00.epic.icetrack.leaderboards.LeaderboardManager;
import com.justbru00.epic.icetrack.map.Map;
import com.justbru00.epic.icetrack.map.MapManager;

public class EpicIceTrackAPI {
	
	/**
	 * Gets the full list of all loaded maps.
	 * @return
	 */
	public static ArrayList<Map> getLoadedMaps() {
		return MapManager.getMaps();
	}
	
	/**
	 * Gets a specific map with the maps internal name.
	 * @param internalName The internal name of the map to get a {@link Map} object for.
	 * @return If the map can't be found. The Optional will be empty.
	 */
	public static Optional<Map> getMapByInternalName(String internalName) {
		return Optional.of(MapManager.getMap(internalName));
	}
	
	/**
	 * Gets the {@link PlayerData} for the given {@link OfflinePlayer}.
	 * To this method all players exist. If a player doesn't have any data saved, EpicIceTrack will create a new storage section for them.
	 * 
	 * If changes are made to the returned {@link PlayerData} object, you MUST run {@link PlayerData#save()} to save the updated data to the disk.
	 * 
	 * @param player The {@link OfflinePlayer} to get data for.
	 * @return
	 */
	public static PlayerData getPlayerData(OfflinePlayer player) {
		return PlayerData.getDataFor(player);
	}
	
	/**
	 * This method disables the action bar normally shown to players who are currently running in a map.
	 * @param playerUuid
	 */
	public static void disableTimeActionBar(UUID playerUuid) {
		// TODO
	}
	
	/**
	 * This method enables the action bar normally shown to players who are currently running in a map.
	 * @param playerUuid
	 */
	public static void enableTimeActionBar(UUID playerUuid) {
		// TODO
	}
	
	/**
	 * This method enables the action bar for ALL players. This action bar is normally shown to players who are currently running in a map.
	 */
	public static void enableTimeActionBarForAll() {
		// TODO
	}
	
	/**
	 * This method gets a {@link java.util.Map} of the top times for the given map internal name.
	 * @param mapInternalName The internal name for the map.
	 * @param limitListTo The limit on the returned {@link java.util.Map}.
	 * @return
	 */
	public static java.util.Map<UUID, Long> getFastestTimesForMap(String mapInternalName, int limitListTo) {
		return LeaderboardManager.getFastestTimesForMap(mapInternalName, limitListTo);
	}
	
}
