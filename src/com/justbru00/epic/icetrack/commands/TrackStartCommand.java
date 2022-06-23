package com.justbru00.epic.icetrack.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.justbru00.epic.icetrack.map.MapManager;
import com.justbru00.epic.icetrack.timer.PlayerTimer;
import com.justbru00.epic.icetrack.utils.Messager;

public class TrackStartCommand implements CommandExecutor {

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		
		if (sender.hasPermission("epicicetrack.trackstart") || sender.hasPermission("parkour.parkourstart")) {
			if (args.length == 2) {
				String playerName = args[0];
				String mapInternalName = args[1];
				
				PlayerTimer.playerStartingMap(Bukkit.getOfflinePlayer(playerName), MapManager.getMap(mapInternalName));
				Messager.msgSender("&aAttempted to start the player " + playerName + " on the map " + mapInternalName, sender);
			} else {
				Messager.msgSender("&cIncorrect command arguments... /trackstart <playerName> <mapInternalName>", sender);
				return true;
			}
		} else {
			Messager.msgSender("&cSorry you don't have permission.", sender);
			return true;
		}
		
		return false;
	}

}
