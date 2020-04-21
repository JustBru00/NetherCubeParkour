package com.gmail.justbru00.nethercube.elytra.leaderboards;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.justbru00.nethercube.elytra.data.PlayerData;
import com.gmail.justbru00.nethercube.elytra.main.NetherCubeElytra;

import com.gmail.justbru00.nethercube.elytra.map.MapManager;
import com.gmail.justbru00.nethercube.elytra.utils.Messager;

public class LeaderboardManager {
	
	private static List<String> balanceLeaderBoardLines = new ArrayList<String>();
	private static List<String> fastestTimeBoardLines = new ArrayList<String>();
	private static HashMap<String,Location> fastestTimeBoardLocations = new HashMap<String,Location>();
	private static Location balanceLeaderBoardLocation;
	private static HashMap<String, Hologram> fastestHolograms = new HashMap<String, Hologram>();
	private static Hologram balanceHologram;

	public static void updateBalanceLeaderboard() {
		if (!NetherCubeElytra.enableLeaderboards) {
			return;
		}
		Location loc = balanceLeaderBoardLocation;
		
		ArrayList<PlayerData> allTheData = new ArrayList<PlayerData>();
		
		for (String key : NetherCubeElytra.dataFile.getKeys(false)) {
			try {
				allTheData.add(PlayerData.getDataFor(Bukkit.getOfflinePlayer(UUID.fromString(key))));
			} catch (Exception e) {
				Messager.debug("&cFailed to get data for uuid: " + key);
			}
		}
		
		HashMap<UUID, Integer> dataMap = new HashMap<UUID, Integer>();
		
		for (PlayerData v : allTheData) {
			dataMap.put(v.getUuid(), v.getCurrency());
		}
		// Get the top ten
		Map<UUID, Integer> topTen = dataMap.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
				.limit(10).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
		
		
		List<String> textLines = new ArrayList<String>();
		
		ArrayList<UUID> orderedIds = new ArrayList<UUID>();
		
		for (Entry<UUID, Integer> entry : topTen.entrySet()) {
			orderedIds.add(entry.getKey());		
		}
		
		for (String line : balanceLeaderBoardLines) {
			// Replace names
			for (int i = 1; i <= 10; i++) {
				String name;				
				try {
					name = Bukkit.getOfflinePlayer(orderedIds.get(i-1)).getName();
				} catch (IndexOutOfBoundsException e) {
					name = "Empty";
				}
				
				if (name == null) {
					name = "Empty";
				}
				
				line = line.replace("{name" + i +"}", name);
			}
			
			// Replace Balance
			for (int i = 1; i <= 10; i++) {
				String currency;				
				try {
					currency = String.valueOf(topTen.get(orderedIds.get(i-1)));
				} catch (IndexOutOfBoundsException e) {
					currency = "none";
				}
				
				line = line.replace("{bal" + i +"}", currency);
			}
			
			textLines.add(line);
		}
		Bukkit.getScheduler().runTask(NetherCubeElytra.getInstance(), new Runnable() {
			
			@Override
			public void run() {
				// Update actual hologram
				Hologram holo;
				if (balanceHologram == null) {
					holo = HologramsAPI.createHologram(NetherCubeElytra.getInstance(), loc);
					balanceHologram = holo;
				} else {
					holo = balanceHologram;
				}		
				
				holo.clearLines();
				
				for (String line : textLines) {
					holo.appendTextLine(Messager.color(line));
				}
				Messager.debug("[LeaderManager] Finished updating balance leaderboard.");				
			}
		});
	}
	
	public static void updateBalanceLeaderboard(CommandSender toNotify) {
		updateBalanceLeaderboard();
		Messager.msgSender("&aUpdated the balance leaderboard successfully.", toNotify);
	}
	
	
	public static void updateFastestTimeLeaderboard(String mapInternalName) {
		if (!NetherCubeElytra.enableLeaderboards) {
			return;
		}
		Location loc = fastestTimeBoardLocations.get(mapInternalName);
		
		com.gmail.justbru00.nethercube.elytra.map.Map map = MapManager.getMap(mapInternalName);
		
		ArrayList<PlayerData> allTheData = new ArrayList<PlayerData>();
		
		for (String key : NetherCubeElytra.dataFile.getKeys(false)) {
			try {
				allTheData.add(PlayerData.getDataFor(Bukkit.getOfflinePlayer(UUID.fromString(key))));
			} catch (Exception e) {
				Messager.debug("&cFailed to get data for uuid: " + key);
			}
		}
		
		HashMap<UUID, Long> dataMap = new HashMap<UUID, Long>();
		
		for (PlayerData v : allTheData) {
			dataMap.put(v.getUuid(), v.getMapData(mapInternalName).getBestTime());
		}
		// Get the top ten
		Map<UUID, Long> topTen = dataMap.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.naturalOrder()))
				.limit(10).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
		
		
		List<String> textLines = new ArrayList<String>();
		
		ArrayList<UUID> orderedIds = new ArrayList<UUID>();
		
		for (Entry<UUID, Long> entry : topTen.entrySet()) {
			orderedIds.add(entry.getKey());		
		}
		
		for (String line : fastestTimeBoardLines) {
			// Replace the map name
			line = line.replace("{mapname}", MapManager.getMap(mapInternalName).getGuiItem().getItemMeta().getDisplayName());
			
			// Replace names
			for (int i = 1; i <= 10; i++) {
				String name;				
				try {
					name = Bukkit.getOfflinePlayer(orderedIds.get(i-1)).getName();
				} catch (IndexOutOfBoundsException e) {
					name = "Empty";
				}
				
				line = line.replace("{name" + i +"}", name);
			}
			
			// Replace Times
			for (int i = 1; i <= 10; i++) {
				String time;				
				try {
					time = Messager.formatAsTime(topTen.get(orderedIds.get(i-1)));
				} catch (IndexOutOfBoundsException e) {
					time = "none";
				}
				
				line = line.replace("{time" + i +"}", time);
			}
			
			textLines.add(line);
		}
		Bukkit.getScheduler().runTask(NetherCubeElytra.getInstance(), new Runnable() {
			
			@Override
			public void run() {
				// Update actual hologram
				// Hologram naming method: fastest_mapinternalname - If I could name them lol
				Hologram holo;
				if (fastestHolograms.get(mapInternalName) == null) {
					holo = HologramsAPI.createHologram(NetherCubeElytra.getInstance(), loc);
					fastestHolograms.put(mapInternalName, holo);
				} else {
					holo = fastestHolograms.get(mapInternalName);
				}		
				
				holo.clearLines();
				
				for (String line : textLines) {
					holo.appendTextLine(Messager.color(line));
				}
				Messager.debug("[LeaderManager] Finished updating fastest time leaderboard for " + map.getInternalName() + ".");				
			}
		});
		
	}
	
	/**
	 * Updates all fastest time leaderboards by calling {@link #updateFastestTimeLeaderboard(String)}
	 * for every map in the MapManager
	 */
	public static void updateAllFastestTimeLeaderboard() {
		if (!NetherCubeElytra.enableLeaderboards) {
			return;
		}
		for (com.gmail.justbru00.nethercube.elytra.map.Map m : MapManager.getMaps()) {
			updateFastestTimeLeaderboard(m.getInternalName());
		}
	}
	
	/**
	 * Updates all fastest time leaderboards by calling {@link #updateFastestTimeLeaderboard(String)}
	 * for every map in the MapManager
	 * @param toNotify The {@link CommandSender} that should be notified of this finishing
	 */
	public static void updateAllFastestTimeLeaderboard(CommandSender toNotify) {
		if (!NetherCubeElytra.enableLeaderboards) {
			return;
		}
		for (com.gmail.justbru00.nethercube.elytra.map.Map m : MapManager.getMaps()) {
			updateFastestTimeLeaderboard(m.getInternalName());
			Messager.msgSender("&aUpdated the fastest time leaderboard for: " + m.getInternalName(), toNotify);
		}
		Messager.msgSender("&aFinished updating all of the fastest time leaderboards.", toNotify);
	}
	/**
	 * This will not be reloaded with /elyadmin reload
	 */
	public static void startUpdateTask() {
		int ticksBetweenUpdates = NetherCubeElytra.getInstance().getConfig().getInt("leaderboards.update_every_x_ticks");
		Bukkit.getScheduler().runTaskTimerAsynchronously(NetherCubeElytra.getInstance(), new Runnable() {			
			@Override
			public void run() {
				Messager.debug("Starting auto leaderboard update.");
				updateAllFastestTimeLeaderboard();		
				updateBalanceLeaderboard();
				Messager.debug("Finished auto leaderboard update.");
			}
		}, 30*20, ticksBetweenUpdates);
	}
	
	public static void loadLeaderboardLines() {
		FileConfiguration config = NetherCubeElytra.getInstance().getConfig();
		
		// Clear to allow for reloading
		fastestTimeBoardLocations.clear();
		
		// Load balance leaderboard location
		balanceLeaderBoardLocation = new Location(Bukkit.getWorld(config.getString("leaderboards.topbalance.location.world")),
				config.getDouble("leaderboards.topbalance.location.x"),
				config.getDouble("leaderboards.topbalance.location.y"), 
				config.getDouble("leaderboards.topbalance.location.z"));
		
		// Load balance leaderboard lines
		balanceLeaderBoardLines = config.getStringList("leaderboards.topbalance.lines");
		
		// Load Fastest Time leaderboard lines
		fastestTimeBoardLines = config.getStringList("leaderboards.fastesttime.lines");
		
		for (String key : config.getConfigurationSection("leaderboards.fastesttime.locations").getKeys(false)) {
			String prefix = "leaderboards.fastesttime.locations." + key +".";
			fastestTimeBoardLocations.put(key, new Location(Bukkit.getWorld(config.getString(prefix + "world")),
					config.getDouble(prefix + "x"), config.getDouble(prefix + "y"), config.getDouble(prefix + "z")));
		}
		
	}
	
}
