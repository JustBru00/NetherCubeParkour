package com.gmail.justbru00.nethercube.parkour.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import com.gmail.justbru00.nethercube.parkour.utils.Messager;

public class BoatKillCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] arg3) {

		if (sender.hasPermission("parkour.boatkill")) {
			long numberOfBoats = 0;
			
			for (Entity e : Bukkit.getWorld("world").getEntities()) {
				if (e.getType().equals(EntityType.BOAT)) {
					if (e.isEmpty()) {
						// No passengers
						e.remove();
						numberOfBoats++;
					}
				}				
			}
			
			Messager.msgSender("&aRemoved " + numberOfBoats + " boats.", sender);
			
		} else {
			Messager.msgSender("&cSorry you don't have permission.", sender);
			return true;
		}
		
		return false;
	}

}
