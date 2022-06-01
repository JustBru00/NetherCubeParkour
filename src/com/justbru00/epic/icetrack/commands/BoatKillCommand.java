package com.justbru00.epic.icetrack.commands;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import com.justbru00.epic.icetrack.utils.Messager;

public class BoatKillCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] arg3) {

		if (sender.hasPermission("parkour.boatkill")) {
			long numberOfBoats = 0;
			
			for (World w : Bukkit.getWorlds()) {
				for (Entity e : w.getEntities()) {
					if (e.getType().equals(EntityType.BOAT)) {
						if (e.isEmpty()) {
							// No passengers
							e.remove();
							numberOfBoats++;
						}
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
