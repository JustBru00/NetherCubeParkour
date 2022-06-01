package com.justbru00.epic.icetrack.listeners;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;

import com.justbru00.epic.icetrack.gui.GUIManager;
import com.justbru00.epic.icetrack.map.MapManager;
import com.justbru00.epic.icetrack.timer.PlayerTimer;
import com.justbru00.epic.icetrack.utils.Messager;

public class MainGUIListener implements Listener {

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		
		if (e.getInventory() != null) {
			if (e.getView().getTitle() != null) {
				//if (e.getView().getTitle().startsWith(Messager.color("&6Currency: "))) {
				if (e.getView().getTitle().startsWith(Messager.color("&6Courses: "))) {
					// Is the main GUI 
					e.setCancelled(true);
					
					if (e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR) {
						ItemStack item = e.getCurrentItem();				
						
						if (item.getItemMeta() == null || item.getItemMeta().getLore() == null)  {
							// Item doesn't have lore
							return;
						}
						
						List<String> lore = item.getItemMeta().getLore();
						
						if (lore.get(0).contains(Messager.color("UNLOCKED:"))) {
							// Map is unlocked - Teleport them to the start
							if (e.getClick() == ClickType.LEFT || e.getClick() == ClickType.SHIFT_LEFT) {
								// Ensure that the player is not in a map
								PlayerTimer.playerLeavingMap((Player) e.getWhoClicked(), false);
								String mapName = ChatColor.stripColor(item.getItemMeta().getLore().get(5));								
								e.getWhoClicked().teleport(MapManager.getMap(mapName).getSpawnLocation(), TeleportCause.PLUGIN);
							}
							return;
						} 
						
						// Not unlocked
						
						//if (e.getView().getTitle().startsWith(Messager.color("&6Currency: "))) {
						if (e.getView().getTitle().startsWith(Messager.color("&6Courses: "))) {							
							// Make sure the item clicked is from the mainGUI
							if (!e.getCurrentItem().equals(GUIManager.getBorderGlass())) {
								// Item is not the border glass
								if (e.getClick() == ClickType.RIGHT || e.getClick() == ClickType.SHIFT_RIGHT) {
									Messager.debug("opening the confirm GUI");								
									GUIManager.openConfirmGUI((Player) e.getWhoClicked(), e.getCurrentItem());
								}
							}
						}					
						
					}
				}
			}
		}
		
	}
	
}
