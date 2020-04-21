package com.gmail.justbru00.nethercube.elytra.commands;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.justbru00.nethercube.elytra.data.PlayerData;
import com.gmail.justbru00.nethercube.elytra.main.NetherCubeElytra;
import com.gmail.justbru00.nethercube.elytra.utils.Messager;

public class ElytraBalanceCommand implements CommandExecutor {

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if (command.getName().equalsIgnoreCase("elytrabalance")) {
			if (sender.hasPermission("nethercubeelytra.elytrabalance")) {
				if (sender instanceof Player) {
					Player p = (Player) sender;
					
					if (args.length == 1) {
						// Trying to use /elybal <player>
						if (sender.hasPermission("nethercubeelytra.elytrabalance.others")) {
							
							OfflinePlayer target = p;
							try {
								target = Bukkit.getOfflinePlayer(args[0]);
							} catch (Exception e) {
								Messager.msgSender("&cUhh... Something went wrong. Make sure that you have supplied a valid player name.", sender);
								return true;
							}
							
							Set<String> keys = NetherCubeElytra.dataFile.getKeys(false);
							
							for (String key : keys) {
								if (target.getUniqueId().toString().equalsIgnoreCase(key)) {
									PlayerData pd = PlayerData.getDataFor(target);
									Messager.msgSender("&6The player " + target.getName() + " has a balance of &a" + pd.getCurrency() + "&6." , sender);
									return true;
								}
							}
							// Not a player that has joined before
							Messager.msgSender("&cSorry that player doesn't appear to have a balance yet.", sender);
							return true;
						} else {
							Messager.msgSender("&cSorry you don't have permission to view the balance of others.", sender);
							return true;
						}
					} else {
						// Not /elybal <player>
						PlayerData pd = PlayerData.getDataFor(p);
						Messager.msgSender("&6You have &a" + pd.getCurrency() + " &6currency in your balance.", sender);
						return true;
					}	
				} else {
					Messager.msgSender("&cSorry only players can use this command.", sender);
					return true;
				}
			} else {
				Messager.msgSender("&cSorry just don't have permission for that command.", sender);
				return true;
			}
		}
		
		return false;
	}

}
