package com.justbru00.epic.icetrack.gui;


import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.justbru00.epic.icetrack.data.PlayerData;
import com.justbru00.epic.icetrack.data.PlayerMapData;
import com.justbru00.epic.icetrack.leaderboards.LeaderboardManager;
import com.justbru00.epic.icetrack.map.Map;
import com.justbru00.epic.icetrack.map.MapManager;
import com.justbru00.epic.icetrack.utils.ItemBuilder;
import com.justbru00.epic.icetrack.utils.Messager;

public class GUIManager {

	private static ItemStack borderGlass;
	private static ItemStack okay;
	private static ItemStack cancel;
	private static ItemStack cannotAffordMap;
	
	public static void init() {
		
		borderGlass = new ItemBuilder(Material.BLUE_STAINED_GLASS_PANE).setName("&r").build();
		okay = new ItemBuilder(Material.EMERALD_BLOCK).setName("&a&lOkay").build();
		cancel = new ItemBuilder(Material.REDSTONE_BLOCK).setName("&c&lCancel").build();
		cannotAffordMap = new ItemBuilder(Material.BARRIER).setName("&c&lYou can't afford that map").build();
	}
	
	public static ItemStack getBorderGlass() {
		return borderGlass;
	}
	
	public static ItemStack getOkay() {
		return okay;
	}
	
	public static ItemStack getCancel() {
		return cancel;
	}
	
	public static ItemStack getCannotAfford() {
		return cannotAffordMap;
	}
		
	/**
	 * Opens the main GUI for map selection, unlocking, and instant teleporting.
	 * Players can left click to teleport to the start of unlocked maps
	 * Players can right click to purchase locked maps
	 * @param p
	 */
	public static void openMainGUI(Player p) {
		
		//PlayerData pd = PlayerData.getDataFor(p);
		
		//Inventory inv = Bukkit.createInventory(null, 54, Messager.color("&6Currency: " + pd.getCurrency()));
		Inventory inv = Bukkit.createInventory(null, 54, Messager.color("&6Courses: "));
		
		// Fill the maps in
		for (Map map : MapManager.getMaps()) {
			inv.addItem(getMapItemFor(p, map.getInternalName()));
		}
		
		p.openInventory(inv);		
	}
	
	/**
	 * Fills the lore for each map icon for the specified player.
	 * @param p
	 * @param mapInternalName
	 * @return
	 */
	private static ItemStack getMapItemFor(Player p, String mapInternalName) {
		
		PlayerData pd = PlayerData.getDataFor(p);
		PlayerMapData pmd = pd.getMapData(mapInternalName);
		Map map = MapManager.getMap(mapInternalName);
		
		ItemStack toReturn = map.getGuiItem().clone();
		
		List<String> loreToSet = new ArrayList<String>();
		
		// First line - Unlock status and price if not unlocked
		String firstLine;
		if (pmd.isUnlocked()) {
			// Unlocked 
			firstLine = Messager.color("&a&lUNLOCKED: Left click to teleport to start");
		} else {
			firstLine = Messager.color("&cLOCKED: This map is currently locked by the server admins.");
		}
		// End First Line
		
		// Second Line - Map Placement Issue #15
		String secondLine;
		
		int placement = LeaderboardManager.getCachedLeaderboardPositionOnMap(mapInternalName, p.getUniqueId());
		String placementMsg = Messager.color("&cAn unknown error occured.");
		if (placement == -1) {
			placementMsg = "Lower than the top 100";
		} else if (placement == -2) {
			placementMsg = "Lower than the top 100";
		} else if (pmd.getFinishes() == -3) {
			placementMsg = "Not yet cached";
		} else {
			placementMsg = "#" + placement;
		}
		
		secondLine = Messager.color("&7Placement: &e" + placementMsg);
		
		// End Second Line
		
		// Third Line - Players best time for this map
		String thirdLine;
		
		Messager.debug("Best time is: " + pmd.getBestTime());
		if (pmd.getBestTime() == PlayerMapData.NO_BEST_TIME) {
			thirdLine = Messager.color("&7Your best time: &eNever Finished");
		} else {
			thirdLine = Messager.color("&7Your best time: &e" + Messager.formatAsTime(pmd.getBestTime()));
		}
		
		// End Third Line
		
		// Fourth Line - Times Finished for the player
		String fourthLine = Messager.color("&7Times finished: &e" + pmd.getFinishes());
		// End fourth line
		
		// Fifth line
		String fifthLine = Messager.color("&7Attempts: &e" + pmd.getAttempts());
		// End fifthLine
		
		// Sixth Line - This maps internal name. Important for the Confirm GUI.
		String sixthLine = Messager.color("&7" + map.getInternalName());
		// End Sixth line
		
		loreToSet.add(firstLine);
		loreToSet.add(secondLine);
		loreToSet.add(thirdLine);
		loreToSet.add(fourthLine);
		loreToSet.add(fifthLine);		
		loreToSet.add(sixthLine);
		
		if (pmd.isUnlocked()) {
			Messager.debug("Map is unlocked | GLOWING");
				if (toReturn.getType() == Material.FISHING_ROD) {		
					Messager.debug("ITEM IS FISHING ROD");
					toReturn.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 4341);
					ItemMeta meta = toReturn.getItemMeta();
					meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);		
					toReturn.setItemMeta(meta);
				} else {		
					Messager.debug("ITEM IS NOT FISHING ROD");
					toReturn.addUnsafeEnchantment(Enchantment.LURE, 4341);				
					ItemMeta meta = toReturn.getItemMeta();
					meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);		
					toReturn.setItemMeta(meta);
				}
		}
		
		ItemMeta im = toReturn.getItemMeta();		
		im.setLore(loreToSet);
		toReturn.setItemMeta(im);
		
		return toReturn;
	}
	
}
