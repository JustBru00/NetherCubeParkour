package com.gmail.justbru00.nethercube.parkour.commands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.gmail.justbru00.nethercube.parkour.data.PlayerData;
import com.gmail.justbru00.nethercube.parkour.gui.GUIManager;
import com.gmail.justbru00.nethercube.parkour.leaderboards.LeaderboardManager;
import com.gmail.justbru00.nethercube.parkour.main.NetherCubeParkour;
import com.gmail.justbru00.nethercube.parkour.map.Map;
import com.gmail.justbru00.nethercube.parkour.map.MapManager;
import com.gmail.justbru00.nethercube.parkour.timer.PlayerTimer;
import com.gmail.justbru00.nethercube.parkour.utils.Messager;

public class ParkourAdminCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if (command.getName().equalsIgnoreCase("parkouradmin")) {
			
			if (!sender.hasPermission("nethercubeparkour.parkouradmin")) {
				Messager.msgSender("&cYou don't have a cool enough prefix to use this command.", sender);
				return true;
			}
			
			if (args.length == 0) {
				Messager.msgSender("&cUhh... you &omight &r&cneed to provide an argment after this command. Use /parkouradmin help for a list of arguments.", sender);
				return true;
			}
			
			if (args.length >= 1) {
				if (args[0].equalsIgnoreCase("help")) {
					Messager.msgSender("&6/parkouradmin currency <set,get,add,subtract> <playerName,UUID> (amount)", sender);
					Messager.msgSender("&6/parkouradmin maps <list,tp> (player)", sender);
					Messager.msgSender("&6/parkouradmin updateleaderboards", sender);
					Messager.msgSender("&6/parkouradmin reload", sender);
					return true;
				} else if (args[0].equalsIgnoreCase("resetbesttime")) {
					// ISSUE #1
					// /parkouradmin resetbesttime <playerUuid> <map>
					if (args.length != 3) {
						Messager.msgSender("&cSorry you didn't provide the correct arguments. /parkouradmin resetbesttime <playerUuid> <mapName>", sender);
						return true;
					} 
					
					String potentialUuid = args[1];
					String potentialMapName = args[2];
					
					if (potentialUuid.length() != 36) {
						Messager.msgSender("&cUhh... " + potentialUuid + " doesn't appear to be properly formatted UUID string. Fix that please. It really helps my sanity.", sender);
						return true;
					}
					
					UUID id = null;
					try {
						id = UUID.fromString(potentialUuid);
					} catch (IllegalArgumentException e) {
						Messager.msgSender("&cUhh... " + potentialUuid+ " doesn't appear to be properly formatted UUID string. Fix that please. It really helps my sanity.", sender);
						return true;
					}
					
					PlayerData pd = PlayerData.getDataFor(Bukkit.getOfflinePlayer(id));
					
					if (pd.getMapData(potentialMapName) == null) {
						Messager.msgSender("&cI can't find that map in the given players userdata. Are you sure you typed it correctly?", sender);
						return true;
					}
					
					pd.getMapData(potentialMapName).setBestTime(-1L);
					pd.save();
					Messager.msgSender("&aSuccessfully reset the best time for " + id.toString() + " on the map " + potentialMapName + ".", sender);
					
					return true;
				} else if (args[0].equals("currency") || args[0].equalsIgnoreCase("cur")) {
					if (args.length >= 3) {
					
					OfflinePlayer offline = null;
					
					if (args[2].length() > 16) { // Is UUID Maybe?
						UUID id = null;
						try {
							id = UUID.fromString(args[2]);
						} catch (IllegalArgumentException e) {
							Messager.msgSender("&cUhh... " + args[2] + " doesn't appear to be properly formatted UUID string. Fix that please. It really helps my sanity.", sender);
							return true;
						}
						
						try {
							offline = Bukkit.getOfflinePlayer(id);
						} catch (Exception e) {
							return true;
						}
					} else {
						try {
							offline = Bukkit.getPlayer(args[2]);
						} catch (Exception e) {
							Messager.msgSender("&cCannot find player with the username " + args[2] + " online. If the player is offline, please provide their UUID instead.", sender);
							return true;
						}
						
						if (offline == null) {
							Messager.msgSender("&cCannot find player with the username " + args[2] + " online. If the player is offline, please provide their UUID instead.", sender);
							return true;
						}
					}
					
					PlayerData pd = PlayerData.getDataFor(offline);
					
					if (args[1].equalsIgnoreCase("get")) {
						if (offline.getName() == null) {
							Messager.msgSender("&a" + offline.getUniqueId().toString() + " has " + pd.getCurrency() + " currency.", sender);
						} else {
							Messager.msgSender("&a" + offline.getName() + " has " + pd.getCurrency() + " currency.", sender);
						}
						
						return true;
					}
					
					int amount = 0;
					
					try {
						amount = Integer.parseInt(args[3]);
					} catch (NumberFormatException e) {
						Messager.msgSender("&cThe value '" + args[3] + "' cannot be parsed as an Integer. Make sure is is a number without a decimal point.", sender);
						return true;
					}
					
					if (args[1].equalsIgnoreCase("set")) {
						pd.setCurrency(amount).save();
						if (offline.getName() == null) {
							Messager.msgSender("&aSet " + offline.getUniqueId().toString() + "'s currency balance to " + amount + ".", sender);
						} else {
							Messager.msgSender("&aSet " + offline.getName() + "'s currency balance to " + amount + ".", sender);
						}
						return true;
					} else if (args[1].equalsIgnoreCase("add")) {
						pd.setCurrency(pd.getCurrency() + amount).save();
						if (offline.getName() == null) {
							Messager.msgSender("&aAdded " + amount + " to " + offline.getUniqueId().toString() + "'s balance. They now have a total of " + pd.getCurrency() + ".", sender);
						} else {
							Messager.msgSender("&aAdded " + amount + " to " + offline.getName() + "'s balance. They now have a total of " + pd.getCurrency() + ".", sender);
						}
						return true;
					} else if (args[1].equalsIgnoreCase("subtract")) {
						if (pd.getCurrency() - amount < 0) {
							pd.setCurrency(0).save();
							if (offline.getName() == null) {
								Messager.msgSender("&aSubtracted " + amount + " from " + offline.getUniqueId().toString() + "'s balance. They now have a total of " + pd.getCurrency() + ".", sender);
							} else {
								Messager.msgSender("&aSubtracted " + amount + " from " + offline.getName() + "'s balance. They now have a total of " + pd.getCurrency() + ".", sender);
							}
						} else {
							pd.setCurrency(pd.getCurrency() - amount).save();
							if (offline.getName() == null) {
								Messager.msgSender("&aSubtracted " + amount + " from " + offline.getUniqueId().toString() + "'s balance. They now have a total of " + pd.getCurrency() + ".", sender);
							} else {
								Messager.msgSender("&aSubtracted " + amount + " from " + offline.getName() + "'s balance. They now have a total of " + pd.getCurrency() + ".", sender);
							}
						}
						return true;
					} else {
						Messager.msgSender("&cPlease provide the correct arguments. /parkouradmin currency <set,get,add,subtract> <player,UUID> (amount)", sender);
						return true;
					}
					
					} else {
						Messager.msgSender("&cPlease provide the correct arguments. /parkouradmin currency <set,get,add,subtract> <player,UUID> (amount)", sender);
						return true;
					}
						
				} else if (args[0].equalsIgnoreCase("maps")) {
					if (args.length >= 2) {
						if (args[1].equalsIgnoreCase("list")) {
							for (Map map : MapManager.getMaps()) {
								Messager.msgSender("&6" + map.getInternalName(), sender);
							}
							return true;
						} else {
							// Try for TP
							if (args[1].equalsIgnoreCase("tp")) {
								if (args.length == 4) {
									Player target;
									try {
										target = Bukkit.getPlayer(args[2]);
									} catch (Exception e) {
										Messager.msgSender("&cProvided player is not online.", sender);
										return true;
									}
									if (target == null) {
										Messager.msgSender("&cProvided player is not online.", sender);
										return true;
									}
									
									Map map = MapManager.getMap(args[3]);
									if (map != null) {
										// TELEPORT PLAYER
										target.teleport(map.getSpawnLocation(), TeleportCause.PLUGIN);
										Messager.msgSender("&6Teleported " + target.getName() + " to the start of map " + map.getInternalName() + ".", sender);
										return true;
									} else {
										Messager.msgSender("&cThat map does not exist. Get a list of maps with /parkouradmin maps list", sender);
										return true;
									}
								} else {
									Messager.msgSender("&cNot enough arguments. /parkouradmin maps tp <player> <mapname>", sender);
									return true;
								}
							} else {
								Messager.msgSender("&cNot enough arguments. /parkouradmin maps tp <player> <mapname>", sender);
								return true;
							}
						}
					} else {
						Messager.msgSender("&cNot enough arguments. /parkouradmin maps <list, tp>", sender);
						return true;
					}
				} else if (args[0].equalsIgnoreCase("testgui")) {
					Messager.msgSender("&aOpening the GUI.", sender);
					
					if (sender instanceof Player) {
						Player p = (Player) sender;
						GUIManager.openMainGUI(p);
						Messager.msgPlayer("&aGUI has been opened", p);
						return true;
					} 
					Messager.msgSender("&cUhhh... GUIs can only be opened by a player. You knew that right?", sender);
					return true;
				} else if (args[0].equalsIgnoreCase("reload")) {
					NetherCubeParkour.getInstance().reloadConfig();
					NetherCubeParkour.dataFile.reload();
					Messager.msgSender("&aReloaded config.yml and data.yml.", sender);
					MapManager.init();
					GUIManager.init();
					PlayerTimer.init();
					LeaderboardManager.loadLeaderboardLines();
					Messager.msgSender("&aReinitialized LeaderboardManager, MapManager, GUIManager, and PlayerTimer.", sender);
					return true;
				} else if (args[0].equalsIgnoreCase("updateleaderboards")) {
					Bukkit.getScheduler().runTaskAsynchronously(NetherCubeParkour.getInstance(), new Runnable() {
						
						@Override
						public void run() {
							LeaderboardManager.updateAllFastestTimeLeaderboard(sender);
							LeaderboardManager.updateBalanceLeaderboard(sender);
						}
					});
					Messager.msgSender("&aAttempting to update leaderboards. You should see a success message soon.", sender);
					return true;
				} else {
					Messager.msgSender("&cSorry that the argument " + args[0] + " is not correct. Use /parkouradmin help for a list of arguments.", sender);
					return true;
				}
			}
			
			return true;
		}
		
		return false;
	}

}
