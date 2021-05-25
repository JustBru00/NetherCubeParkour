package com.gmail.justbru00.nethercube.parkour.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.gmail.justbru00.nethercube.parkour.map.MapManager;
import com.gmail.justbru00.nethercube.parkour.timer.PlayerTimer;
import com.gmail.justbru00.nethercube.parkour.utils.Messager;

public class ParkourStopCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		if (sender.hasPermission("parkour.parkourstop")) {
			if (args.length == 2) {
				String playerName = args[0];
				String mapInternalName = args[1];
				
				PlayerTimer.playerEndedMap(Bukkit.getPlayer(playerName), MapManager.getMap(mapInternalName));
				Messager.msgSender("&aAttempted to stop the player " + playerName + " on the map " + mapInternalName, sender);
			} else {
				Messager.msgSender("&cIncorrect command arguments... /parkourstop <playerName> <mapInternalName>", sender);
				return true;
			}
		} else {
			Messager.msgSender("&cSorry you don't have permission.", sender);
			return true;
		}
		
		
		return false;
	}

}
