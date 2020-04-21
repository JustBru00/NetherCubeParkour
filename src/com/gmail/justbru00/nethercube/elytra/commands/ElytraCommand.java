package com.gmail.justbru00.nethercube.elytra.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.justbru00.nethercube.elytra.gui.GUIManager;
import com.gmail.justbru00.nethercube.elytra.utils.Messager;

public class ElytraCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if (command.getName().equalsIgnoreCase("elytra")) {
			if (!sender.hasPermission("nethercubeelytra.elytra")) {
				Messager.msgSender("&cSorry you don't have permission to use that command.", sender);
				return true;
			}
			
			if (sender instanceof Player) {
				// OPEN GUI			
				Player p = (Player) sender;
				GUIManager.openMainGUI(p);
				return true;
			} else {
				Messager.msgSender("&cOnly players can use this command.", sender);
				return true;
			}
		}
		
		return false;
	}
	
}
