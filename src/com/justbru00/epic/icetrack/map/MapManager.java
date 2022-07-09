package com.justbru00.epic.icetrack.map;

import java.util.ArrayList;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import com.justbru00.epic.icetrack.main.EpicIceTrack;
import com.justbru00.epic.icetrack.utils.ItemBuilder;
import com.justbru00.epic.icetrack.utils.Messager;

/**
 * This class handles importing maps from the config.
 * @author Justin Brubaker
 *
 */
public class MapManager {
	
	private static ArrayList<Map> maps = new ArrayList<Map>();
	
	public static void init() {	
		maps = new ArrayList<Map>();
		FileConfiguration c = EpicIceTrack.getInstance().getConfig();
		Set<String> mapKeys = c.getConfigurationSection("maps").getKeys(false);		
		
		for (String mapKey : mapKeys) {
			Map m = new Map(mapKey);
			
			String prePath = "maps." + mapKey + ".";
			
			try {
			m.setGuiItem(new ItemBuilder(Material.valueOf(c.getString("maps." + mapKey + ".item.material"))).setDataValue(c.getInt("maps." + mapKey + ".item.data")).setName(c.getString("maps." + mapKey + ".item.displayname")).build());
			m.setCreatorName(c.getString("maps." + mapKey + ".creatorname"));
			m.setInternalName(mapKey);
			
			boolean checkpoint = c.getBoolean(prePath + "requirescheckpoint");
			if (!checkpoint) {
				Messager.msgConsole("&6[MapManager] Map '" + mapKey + "' doesn't have checkpoints enabled. If you want to change this please add 'requirescheckpoint: true` to the configuration section for this map.");
			}
			m.setRequiresCheckpoint(checkpoint);
			
			Location spawnpoint;				
			
			spawnpoint = new Location(Bukkit.getWorld(c.getString(prePath + "spawnlocation.world")), c.getDouble(prePath + "spawnlocation.x"), c.getDouble(prePath + "spawnlocation.y"),
					c.getDouble(prePath + "spawnlocation.z"), c.getInt(prePath + "spawnlocation.yaw"), c.getInt(prePath + "spawnlocation.pitch"));
			m.setSpawnLocation(spawnpoint);
			
			maps.add(m);
			Messager.msgConsole("&aLoaded Map " + m.getInternalName() + " by " + m.getCreatorName() + " successfully.");
			} catch (Exception e) {
				Messager.msgConsole("&cAttempt to load map " + mapKey + " FAILED. The stack trace follows this message:");
				e.printStackTrace();
			}			
		}
	}	
	
	public static Map getMap(String internalName) {
		for (Map map : maps) {
			if (map.getInternalName().equalsIgnoreCase(internalName)) {
				return map;
			}
		}
		return null;
	}
	
	public static ArrayList<Map> getMaps() {
		return maps;
	}

	public static int getNumberOfMaps() {
		return maps.size();
	}	

}
