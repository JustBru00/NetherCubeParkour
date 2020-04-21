package com.gmail.justbru00.nethercube.elytra.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

import com.gmail.justbru00.nethercube.elytra.timer.PlayerTimer;
import com.gmail.justbru00.nethercube.elytra.utils.Messager;

public class WorldLeaveListener implements Listener {

	@EventHandler
	public void worldLeave(PlayerChangedWorldEvent e) {
		if (e.getFrom().getName().equalsIgnoreCase(PlayerTimer.LOBBY_LOCATION.getWorld().getName())) {
			PlayerTimer.playerLeavingMap(e.getPlayer(), false);
			Messager.debug("&cPlayer left lobby world");
		}
	}
	
}
