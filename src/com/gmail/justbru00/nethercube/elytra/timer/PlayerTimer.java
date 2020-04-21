package com.gmail.justbru00.nethercube.elytra.timer;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.gmail.justbru00.nethercube.elytra.data.PlayerData;
import com.gmail.justbru00.nethercube.elytra.data.PlayerMapData;
import com.gmail.justbru00.nethercube.elytra.main.NetherCubeElytra;
import com.gmail.justbru00.nethercube.elytra.map.Map;
import com.gmail.justbru00.nethercube.elytra.map.MapManager;
import com.gmail.justbru00.nethercube.elytra.utils.Messager;

public class PlayerTimer {

	private static HashMap<UUID, Map> playersInMaps = new HashMap<UUID, Map>();
	private static HashMap<UUID, Instant> playerMapStartTime = new HashMap<UUID, Instant>();
	public static Location LOBBY_LOCATION;
	
	public static void init() {
		playersInMaps = new HashMap<UUID, Map>();
		playerMapStartTime = new HashMap<UUID, Instant>();
		
		FileConfiguration config = NetherCubeElytra.getInstance().getConfig();
		// Load lobby location
		Location loc = new Location(Bukkit.getWorld(config.getString("lobbylocation.world")), 
				config.getDouble("lobbylocation.x"), config.getDouble("lobbylocation.y"), config.getDouble("lobbylocation.z"));
		LOBBY_LOCATION = loc;
		
		
		// Time display repeating task
		Bukkit.getScheduler().scheduleSyncRepeatingTask(NetherCubeElytra.getInstance(), new Runnable() {
			
			@Override
			public void run() {
				
				for (Player p : Bukkit.getOnlinePlayers()) {
					if (PlayerTimer.isPlayerInMap(p)) {
						PlayerData pd = PlayerData.getDataFor(p);
						PlayerMapData pmd = pd.getMapData(playersInMaps.get(p.getUniqueId()).getInternalName());
						
						long mapTime = Duration.between(playerMapStartTime.get(p.getUniqueId()), Instant.now()).toMillis();
						
						if (pmd.getBestTime() == -1) {
							// The default value is still saved. This is currently their best time
							Messager.sendActionBar("&a" + Messager.formatAsTime(mapTime), p);
						} else if (mapTime < pmd.getBestTime()) {
							// This is the currently the new best time
							Messager.sendActionBar("&a" + Messager.formatAsTime(mapTime), p);
						} else {
							// Not currently the new best time
							Messager.sendActionBar("&c" + Messager.formatAsTime(mapTime), p);
						}
					}
				}
				
			}
		}, 20, 20);
	}
	
	/**
	 * Call this when a player leaves the current map for some reason.
	 * Used by /elytralobby and the WorldLeaveListener
	 * @param p
	 */
	public static void playerLeavingMap(Player p, boolean teleportToLobby) {
		playerMapStartTime.remove(p.getUniqueId());
		playersInMaps.remove(p.getUniqueId());
		
		if (teleportToLobby) {
			// Teleport the player to the elytra lobby
			p.teleport(LOBBY_LOCATION, TeleportCause.PLUGIN);
		}
	}
	
	public static boolean isPlayerInMap(Player p) {
		return playersInMaps.containsKey(p.getUniqueId());
	}
	
	/**
	 * Call this method to start the player on the map again.
	 * This will reset any current time for the player.
	 * @param p
	 * @param m
	 */
	public static void playerStartingMap(OfflinePlayer p, Map m) {
		playerMapStartTime.put(p.getUniqueId(), Instant.now());
		playersInMaps.put(p.getUniqueId(), m);
		
		// Add one attempt to the stats
		PlayerData pd = PlayerData.getDataFor(p);
		PlayerMapData pmd = pd.getMapData(m.getInternalName());
		
		pmd.setAttempts(pmd.getAttempts() + 1);
		pd.save();
	}
	/**
	 * Gets the current time for a player
	 * Useful for displaying the current time
	 * @param p
	 * @return
	 */
	public static long getCurrentTimeInMap(Player p) {
		return Duration.between(playerMapStartTime.get(p.getUniqueId()), Instant.now()).toMillis();
	}
	
	/**
	 * This method is called when the map is completed.
	 * This will save the time to the data file if it is the best.
	 * It will tell the player their final time and if they broke their record
	 * @param p
	 * @param m
	 */
	public static void playerEndedMap(Player p, Map m) {		
		Instant endTime = Instant.now();
		
		if (!playersInMaps.containsKey(p.getUniqueId())) {
			Messager.debug("&cFINISHED BEFORE STARTING");
			return;
		}
		
		PlayerData pd = PlayerData.getDataFor(p);
		PlayerMapData pmd = pd.getMapData(m.getInternalName());
		
		
		
		long mapTime = Duration.between(playerMapStartTime.get(p.getUniqueId()), endTime).toMillis();
		
		if (pmd.getBestTime() == -1) {
			// The default value is still saved. This is the new best time.
			Messager.msgPlayer("&6You finished the map in &a" + Messager.formatAsTime(mapTime) + "&6. That is your new personal best!", p);
			// Save new best time
			pmd.setBestTime(mapTime);
			pd.save();
		} else if (mapTime < pmd.getBestTime()) {
			// This is the new best time
			Messager.msgPlayer("&6You finished the map in &a" + Messager.formatAsTime(mapTime) + "&6. That is your new personal best! You beat your previous best time of &c"
					+ Messager.formatAsTime(pmd.getBestTime()) + "&6.", p);
			pmd.setBestTime(mapTime);
			pd.save();
		} else {
			// Not the new best time
			Messager.msgPlayer("&6You finished the map in &c" + Messager.formatAsTime(mapTime) + "&6. "
					+ "You failed to beat your personal best of &a" + Messager.formatAsTime(pmd.getBestTime()) + "&6.", p);
		}
		
		// GIVE REWARD FOR THIS MAP AND TELL PLAYER HOW MUCH IT WAS
		int reward = 0;
		Map map = MapManager.getMap(pmd.getInternalName());
		if (pmd.getFinishes() == 0) {
			reward = map.getRewardAmount();
		} else if (pmd.getFinishes() == 1) {
			reward = (int) (.75 * map.getRewardAmount());
		} else if (pmd.getFinishes() == 2) {
			reward = (int) (.50 * map.getRewardAmount());
		} else if (pmd.getFinishes() == 3) {
			reward = (int) (.25 * map.getRewardAmount());
		} else {
			// over 4 times finished
			reward = 0;
		}
			
		if (reward <= 0) {
			reward = 0;
		}
		
		// Add one finish to the stats
		pmd.setFinishes(pmd.getFinishes() + 1);		
		
		pd.setCurrency(pd.getCurrency() + reward);
		pd.save();
		Messager.msgPlayer("&6You received &a" + reward + " &6currency for completing the map.", p);
		
		// Remove player from the HashMaps
		playersInMaps.remove(p.getUniqueId());
		playerMapStartTime.remove(p.getUniqueId());
		
		// Teleport the player to the elytra lobby
		p.teleport(LOBBY_LOCATION, TeleportCause.PLUGIN);
	}
	
}
