package com.gmail.justbru00.nethercube.parkour.listeners;

import java.util.Optional;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Gate;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import com.gmail.justbru00.nethercube.parkour.map.Map;
import com.gmail.justbru00.nethercube.parkour.timer.PlayerTimer;
import com.gmail.justbru00.nethercube.parkour.utils.ItemBuilder;
import com.gmail.justbru00.nethercube.parkour.utils.Messager;

public class IceTrackListener implements Listener {
	
	@EventHandler
	public void onDoorClick(PlayerInteractEvent e) {
		if (e.getHand() == EquipmentSlot.OFF_HAND) {
			return; // Ignore off hand events.
		}
				
		Player p = e.getPlayer();
		Block b = e.getClickedBlock();
		
		
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (p.getGameMode().equals(GameMode.CREATIVE)) {
				// Ignore Creative Players
				return;
			}
			
			if (b.getBlockData() instanceof Gate) {
				// Is a trapdoor
				e.setCancelled(true);
			}			
			
		}
		
	}

	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent e) {
		PlayerTimer.playerLeavingMap(e.getPlayer(), false);
		Messager.debug("&cPlayer left the game");
	}	
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		e.getPlayer().teleport(PlayerTimer.LOBBY_LOCATION, TeleportCause.PLUGIN);		
		e.getPlayer().getInventory().clear();
		e.getPlayer().getInventory().setItem(4, new ItemBuilder(Material.BARRIER).setName("&cRestart Map &7&o(Right Click)").build());
	}
	
	@EventHandler
	public void onPlayerInventoryClick(InventoryClickEvent e) {
		if (e.getWhoClicked().getGameMode().equals(GameMode.CREATIVE)) {
			return;
		}
		
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		if (e.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
			return;
		}
		
		if (e.getAction() == null) {
			return;
		}
		
		if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			ItemStack inHand = e.getPlayer().getInventory().getItemInMainHand();
			
			if (inHand == null || inHand.getType().equals(Material.AIR)) {
				return;
			}
			
			if (inHand.getItemMeta().getDisplayName() == null) {
				return;
			}
			
			if (inHand.getItemMeta().getDisplayName().startsWith(Messager.color("&cRestart Map "))) {
				Player p = e.getPlayer();
				// What map are we in?
				Optional<Map> possibleMap = PlayerTimer.getMapPlayerIsIn(p);
				if (possibleMap.isPresent()) {
					// Player is currently playing a map.
					PlayerTimer.playerLeavingMap(p, false);
					
					// Remove the boat the player might be in.
					Entity entity = p.getVehicle();
					
					if (entity != null) {
						entity.remove();
					}
					Map currentMap = possibleMap.get();
					
					p.teleport(currentMap.getSpawnLocation(), TeleportCause.PLUGIN);				
					return;
				} else {
					// Player isn't currently playing a map.
					// Calling this method shouldn't be necessary, but I am going to call it anyway, just to clean up anything that shouldn't be in the HashMaps. 
					PlayerTimer.playerLeavingMap(p, false);				
					
					// Remove the boat the player might be in.
					Entity entity = p.getVehicle();
					
					if (entity != null) {
						entity.remove();
					}
					
					p.teleport(PlayerTimer.LOBBY_LOCATION, TeleportCause.PLUGIN);
					return;
				}						
			} 
		}
	}	
}
