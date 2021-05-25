package com.gmail.justbru00.nethercube.parkour.listeners;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.gmail.justbru00.nethercube.parkour.map.MapManager;
import com.gmail.justbru00.nethercube.parkour.map.MapPlateDetails;
import com.gmail.justbru00.nethercube.parkour.timer.PlayerTimer;

public class PressurePlateTriggerListener implements Listener {

	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if(e.getAction().equals(Action.PHYSICAL)) {
			if (e.getClickedBlock().getType() == Material.STONE_PRESSURE_PLATE) {
				Player p = e.getPlayer();
				BlockState state = e.getClickedBlock().getState();
				
				MapPlateDetails plate = MapManager.getPlateDetails(state.getLocation());
				
				if (plate != null) {
					if (plate.isStartingLocation()) {
						// Starting plate
						PlayerTimer.playerStartingMap(p, plate.getMapPlateBelongsTo());
					} else {
						// Ending plate
						PlayerTimer.playerEndedMap(p, plate.getMapPlateBelongsTo());
					}
				} else {
					// Do nothing /shrug
				}
			}
		}
	}
	
}
