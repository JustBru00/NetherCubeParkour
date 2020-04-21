package com.gmail.justbru00.nethercube.elytra.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.justbru00.nethercube.elytra.timer.PlayerTimer;
import com.gmail.justbru00.nethercube.elytra.utils.Messager;

public class ElytraLobbyCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if (command.getName().equalsIgnoreCase("elytralobby")) {
			if (sender.hasPermission("nethercubeelytra.elytralobby")) {
				if (sender instanceof Player) {
					Player p = (Player) sender;
					PlayerTimer.playerLeavingMap(p, true);
					Messager.msgSender("&6Teleported you to the elytra lobby.", sender);
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
