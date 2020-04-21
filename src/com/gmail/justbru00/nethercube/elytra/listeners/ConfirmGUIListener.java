package com.gmail.justbru00.nethercube.elytra.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import com.gmail.justbru00.nethercube.elytra.data.PlayerData;
import com.gmail.justbru00.nethercube.elytra.gui.GUIManager;
import com.gmail.justbru00.nethercube.elytra.map.Map;
import com.gmail.justbru00.nethercube.elytra.map.MapManager;
import com.gmail.justbru00.nethercube.elytra.utils.Messager;

public class ConfirmGUIListener implements Listener {

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		
		if (e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR) {
			
			if (e.getInventory().getName().equals(Messager.color("&cConfirm Purchase"))) {
				e.setCancelled(true);
				
				ItemStack mapItem = e.getInventory().getItem(22);
				
				if (mapItem == null || mapItem.getItemMeta() == null || mapItem.getItemMeta().getLore() == null) {
					Messager.debug("WHOA!!! Map item in the confirm GUI has a major problem.");
					return;
				}
				
				// Okay item
				if (e.getCurrentItem().equals(GUIManager.getOkay())) {
					// Player attempts to purchase this map
					
					// Check balance and remove the currency if the player can afford it
					PlayerData pd = PlayerData.getDataFor((Player) e.getWhoClicked()); 
					String mapName = ChatColor.stripColor(mapItem.getItemMeta().getLore().get(5));
					Map map = MapManager.getMap(mapName);
					
					if (pd.getCurrency() >= map.getPurchaseCost()) {
						// Player has more than the cost
						
						int balance = pd.getCurrency() - map.getPurchaseCost();
						
						pd.setCurrency(balance);
						pd.getMapData(mapName).setUnlocked(true);
						pd.save();
						
						Messager.debug(pd.getUuid().toString() + " has unlocked " + map.getInternalName() + " for " + map.getPurchaseCost());
						GUIManager.openMainGUI((Player) e.getWhoClicked());
						return;
					} else {
						// Player can't afford this 
						Integer[] okaySlots = {15,16,24,25,33,34,42,43};
						
						for (Integer slot : okaySlots) {
							e.getInventory().setItem(slot, GUIManager.getCannotAfford());
						}
						return;
					}					
				} else if (e.getCurrentItem().equals(GUIManager.getCancel())) {
					// Cancel item
					
					GUIManager.openMainGUI((Player) e.getWhoClicked()); 
				}			
			}
			
		}
		
	}
	
}
