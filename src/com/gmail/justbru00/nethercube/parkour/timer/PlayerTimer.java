package com.gmail.justbru00.nethercube.parkour.timer;

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

import com.gmail.justbru00.nethercube.parkour.data.PlayerData;
import com.gmail.justbru00.nethercube.parkour.data.PlayerMapData;
import com.gmail.justbru00.nethercube.parkour.leaderboards.LeaderboardManager;
import com.gmail.justbru00.nethercube.parkour.main.NetherCubeParkour;
import com.gmail.justbru00.nethercube.parkour.map.Map;
import com.gmail.justbru00.nethercube.parkour.utils.Messager;

import java.util.Optional;

public class PlayerTimer {

	private static HashMap<UUID, Map> playersInMaps = new HashMap<UUID, Map>();
	private static HashMap<UUID, Instant> playerMapStartTime = new HashMap<UUID, Instant>();
	public static HashMap<UUID, UUID> playersInMapsBoatUuids = new HashMap<UUID, UUID>();
	private static HashMap<UUID, Integer> playersCheckpointScore = new HashMap<UUID, Integer>();
	
	public static Location LOBBY_LOCATION;
		
	public static void init() {
		playersInMaps = new HashMap<UUID, Map>();
		playerMapStartTime = new HashMap<UUID, Instant>();
		playersInMapsBoatUuids = new HashMap<UUID, UUID>();
		playersCheckpointScore = new HashMap<UUID, Integer>();
		
		FileConfiguration config = NetherCubeParkour.getInstance().getConfig();
		// Load lobby location
		Location loc = new Location(Bukkit.getWorld(config.getString("lobbylocation.world")), 
				config.getDouble("lobbylocation.x"), config.getDouble("lobbylocation.y"), config.getDouble("lobbylocation.z"));
		LOBBY_LOCATION = loc;
		
		
		// Time display repeating task
		Bukkit.getScheduler().scheduleSyncRepeatingTask(NetherCubeParkour.getInstance(), new Runnable() {
			
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
		}, 5, 5);
	}
	
	/**
	 * This method will return a map if the player is currently in one.
	 * @param p
	 * @return
	 */
	public static Optional<Map> getMapPlayerIsIn(Player p) {
		if (playersInMaps.containsKey(p.getUniqueId())) {
			return Optional.of(playersInMaps.get(p.getUniqueId()));
		}
		
		return Optional.empty();
	}
	
	/**
	 * Call this when a player leaves the current map for some reason.
	 * Used by /elytralobby and the WorldLeaveListener
	 * @param p
	 */
	public static void playerLeavingMap(Player p, boolean teleportToLobby) {
		playerMapStartTime.remove(p.getUniqueId());
		playersInMaps.remove(p.getUniqueId());
		playersInMapsBoatUuids.remove(p.getUniqueId());
		playersCheckpointScore.remove(p.getUniqueId()); 
		
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
		if (m == null) {
			Messager.debug("A NULL map was provided to the #playerStartingMap()");
			return;
		} else if (!p.isOnline()) {
			Messager.debug("A player was provided to MapManager#playerStartingMap() that isn't online. How did that happen?");
			return;
		}
		
		Player online = p.getPlayer();
		if (online.getVehicle() == null) {
			Messager.debug("Player was not in a boat. Can't start time");
			return;
		}
		UUID boatUuid = online.getVehicle().getUniqueId();
		
		playerMapStartTime.put(p.getUniqueId(), Instant.now());
		playersInMaps.put(p.getUniqueId(), m);
		playersInMapsBoatUuids.put(p.getUniqueId(), boatUuid);
		playersCheckpointScore.put(p.getUniqueId(), 0);
		
		// Add one attempt to the stats 
		PlayerData pd = PlayerData.getDataFor(p);
		PlayerMapData pmd = pd.getMapData(m.getInternalName());
		
		pmd.setAttempts(pmd.getAttempts() + 1);
		pd.save();
	}
	
	public static void playerCheckpointMap(OfflinePlayer p, Map m) {
		if (m == null) {
			Messager.debug("A NULL map was provided to the #playerCheckpointMap()");
			return;
		} else if (!p.isOnline()) {
			Messager.debug("A player was provided to MapManager#playerCheckpointMap() that isn't online. How did that happen?");
			return;
		}
		
		Player online = p.getPlayer();
		if (online.getVehicle() == null) {
			Messager.debug("Player was not in a boat. Can't give checkpoint.");
			return;
		}

		UUID startingBoatUuid = playersInMapsBoatUuids.get(p.getUniqueId());
		
		if (startingBoatUuid == null) {
			Messager.debug("Player doesn't have starting boat uuid.");
			// Remove player from the HashMaps
			playersInMaps.remove(p.getUniqueId());
			playerMapStartTime.remove(p.getUniqueId());
			playersInMapsBoatUuids.remove(p.getUniqueId());
			playersCheckpointScore.remove(p.getUniqueId());
			return;
		}
		
		playersCheckpointScore.put(p.getUniqueId(), 1);
		Messager.debug("&6[PlayerTimer] Activated hidden checkpoint for " + p.getName());
		return;
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
		
		if (p.getVehicle() == null) {
			Messager.debug("Player wasn't in a boat");
			// Remove player from the HashMaps
			playersInMaps.remove(p.getUniqueId());
			playerMapStartTime.remove(p.getUniqueId());
			playersInMapsBoatUuids.remove(p.getUniqueId());
			playersCheckpointScore.remove(p.getUniqueId());
			return;
		}
		
		UUID startingBoatUuid = playersInMapsBoatUuids.get(p.getUniqueId());
		if (startingBoatUuid == null) {
			Messager.debug("Player doesn't have starting boat uuid.");
			// Remove player from the HashMaps
			playersInMaps.remove(p.getUniqueId());
			playerMapStartTime.remove(p.getUniqueId());
			playersInMapsBoatUuids.remove(p.getUniqueId());
			playersCheckpointScore.remove(p.getUniqueId());
			return;
		}
		
		if (p.getVehicle().getUniqueId() != startingBoatUuid) {
			Messager.msgPlayer("&cYou finished the map in a different boat than you started in. I can't accept your time because of this.", p);
			Messager.msgConsole(String.format("&c%s attempted to exploit the timing system by finishing in a different boat than they started in.", p.getName()));
			// Remove player from the HashMaps
			playersInMaps.remove(p.getUniqueId());
			playerMapStartTime.remove(p.getUniqueId());
			playersInMapsBoatUuids.remove(p.getUniqueId());
			playersCheckpointScore.remove(p.getUniqueId());
			return;
		}		
		
		if (m.doesRequiresCheckpoint()) {
			if (playersCheckpointScore.get(p.getUniqueId()) == null || playersCheckpointScore.get(p.getUniqueId()) != 1) {
				Messager.msgPlayer("&cYou skipped some of the map. I can't accept your time because of this.", p);
				Messager.msgConsole(String.format("&c%s attempted to exploit the timing system by finishing without passing the hidden checkpoint.", p.getName()));
				// Remove player from the HashMaps
				playersInMaps.remove(p.getUniqueId());
				playerMapStartTime.remove(p.getUniqueId());
				playersInMapsBoatUuids.remove(p.getUniqueId());
				playersCheckpointScore.remove(p.getUniqueId());
				return;
			}		
		}
		
		PlayerData pd = PlayerData.getDataFor(p);
		PlayerMapData pmd = pd.getMapData(m.getInternalName());		
		
		long mapTime = Duration.between(playerMapStartTime.get(p.getUniqueId()), endTime).toMillis();
		
		if (pmd.getBestTime() == -1) {
			// The default value is still saved. This is the new best time.
			Messager.msgPlayer("&6You finished the map in &a&l&n" + Messager.formatAsTime(mapTime) + "&6. That is your new personal best!", p);
			// Save new best time
			pmd.setBestTime(mapTime);			
		} else if (mapTime < pmd.getBestTime()) {
			// This is the new best time
			Messager.msgPlayer("&6You finished the map in &a&l&n" + Messager.formatAsTime(mapTime) + "&6. That is your new personal best! You beat your previous best time of &c"
					+ Messager.formatAsTime(pmd.getBestTime()) + "&6.", p);
			pmd.setBestTime(mapTime);			
		} else {
			// Not the new best time
			int placement = LeaderboardManager.getCachedLeaderboardPositionOnMap(m.getInternalName(), p.getUniqueId());
			if (placement > 0) {
				Messager.msgPlayer("&6You finished the map in &c&l&n" + Messager.formatAsTime(mapTime) + "&6. "
						+ "You failed to beat your personal best of &a" + Messager.formatAsTime(pmd.getBestTime()) + " [#"+ placement + "]&6.", p);
			} else {
				Messager.msgPlayer("&6You finished the map in &c&l&n" + Messager.formatAsTime(mapTime) + "&6. "
						+ "You failed to beat your personal best of &a" + Messager.formatAsTime(pmd.getBestTime()) + "&6.", p);
			}			
		}	
		
		// Add one finish to the stats
		pmd.setFinishes(pmd.getFinishes() + 1);			
	
		pd.save();		
		
		// Remove player from the HashMaps
		playersInMaps.remove(p.getUniqueId());
		playerMapStartTime.remove(p.getUniqueId());
		playersInMapsBoatUuids.remove(p.getUniqueId());
		playersCheckpointScore.remove(p.getUniqueId());		
	}
	
}
