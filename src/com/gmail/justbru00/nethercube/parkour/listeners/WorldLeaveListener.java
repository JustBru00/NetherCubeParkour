package com.gmail.justbru00.nethercube.parkour.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

import com.gmail.justbru00.nethercube.parkour.timer.PlayerTimer;
import com.gmail.justbru00.nethercube.parkour.utils.Messager;

public class WorldLeaveListener implements Listener {

	@EventHandler
	public void worldLeave(PlayerChangedWorldEvent e) {
		if (e.getFrom().getName().equalsIgnoreCase(PlayerTimer.LOBBY_LOCATION.getWorld().getName())) {
			PlayerTimer.playerLeavingMap(e.getPlayer(), false);
			Messager.debug("&cPlayer left lobby world");
		}
	}
	
}
