package com.gmail.justbru00.nethercube.parkour.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.justbru00.nethercube.parkour.timer.PlayerTimer;
import com.gmail.justbru00.nethercube.parkour.utils.Messager;

public class ParkourLobbyCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if (command.getName().equalsIgnoreCase("parkourlobby")) {
			if (sender.hasPermission("nethercubeparkour.parkourlobby")) {
				if (sender instanceof Player) {
					Player p = (Player) sender;
					PlayerTimer.playerLeavingMap(p, true);
					Messager.msgSender("&6Teleported you to the parkour lobby.", sender);
					return true;
				} else {
					Messager.msgSender("&cSorry that can only be used by players.", sender);
					return true;
				}
			} else {
				Messager.msgSender("&cSorry you don't have permission for that command.", sender);
				return true;
			}
		}
		
		return false;
	}

}
