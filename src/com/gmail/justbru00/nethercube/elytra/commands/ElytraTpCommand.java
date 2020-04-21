package com.gmail.justbru00.nethercube.elytra.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.gmail.justbru00.nethercube.elytra.data.PlayerData;
import com.gmail.justbru00.nethercube.elytra.map.Map;
import com.gmail.justbru00.nethercube.elytra.map.MapManager;
import com.gmail.justbru00.nethercube.elytra.utils.Messager;

public class ElytraTpCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		// /elytatp <map> <player>
		if (command.getName().equalsIgnoreCase("elytratp")) {
			if (sender.hasPermission("nethercubeelytra.elytratp")) {
				if (args.length == 2) {
					Map map = MapManager.getMap(args[0]);
					String playerName = args[1];

					if (map != null) {
						Player target;
						try {
							target = Bukkit.getPlayer(playerName);
						} catch (Exception e) {
							Messager.msgSender("&cProvided player is not online.", sender);
							return true;
						}
						if (target == null) {
							Messager.msgSender("&cProvided player is not online.", sender);
							return true;
						}

						PlayerData pd = PlayerData.getDataFor(target);
						// Does the player have this map unlocked?
						if (pd.getMapData(map.getInternalName()).isUnlocked()) {
							// TELEPORT PLAYER
							target.teleport(map.getSpawnLocation(), TeleportCause.PLUGIN);
							Messager.msgSender("&6Teleported " + target.getName() + " to the start of map "
									+ map.getInternalName() + ".", sender);
							return true;
						} else {
							Messager.msgSender("&c" + target.getName() + " doesn't have that map unlocked.", sender);
							Messager.msgPlayer("&cSorry you don't have the map " + map.getInternalName() + " unlocked.",
									target);
							return true;
						}
					} else {
						Messager.msgSender("&cThe map '" + args[0] + "' could not be found. Is it spelled correctly?",
								sender);
						return true;
					}
				} else {
					Messager.msgSender("&cPlease provide correct arguments. /elytratp <map> <player>", sender);
					return true;
				}
			} else {
				Messager.msgSender("&cYou don't have the permission to use that command.", sender);
				return true;
			}
		}

		return false;
	}

}
