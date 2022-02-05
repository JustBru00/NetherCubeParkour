package com.gmail.justbru00.nethercube.parkour.commands;

import java.util.UUID;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.gmail.justbru00.nethercube.parkour.leaderboards.LeaderboardManager;
import com.gmail.justbru00.nethercube.parkour.map.Map;
import com.gmail.justbru00.nethercube.parkour.map.MapManager;
import com.gmail.justbru00.nethercube.parkour.utils.Messager;

public class TopTimesCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (command.getName().equalsIgnoreCase("toptimes")) {

			if (!sender.hasPermission("nethercubeparkour.toptimes")) {
				Messager.msgSender("&cYou don't have permission.", sender);
				return true;
			}

			if (args.length == 0) {
				Messager.msgSender("&cUhh... you &omight &r&cneed to provide an argument after this command. /toptimes <mapName> <totalNumberOfPlayers>", sender);
				return true;
			}

			if (args.length != 2) {
				Messager.msgSender("&cSorry you didn't provide the correct arguments. /toptimes <mapName> <totalNumberOfPlayers>", sender);
				return true;
			}

			Map map = MapManager.getMap(args[0]);

			if (map == null) {
				Messager.msgSender("&cSorry I can't find the a map with the name " + args[0] + ".", sender);
				return true;
			}

			String potentialLimit = args[1];
			int limit = -1;

			try {
				limit = Integer.parseInt(potentialLimit);
			} catch (NumberFormatException e) {
				Messager.msgSender("&cSorry, I can't parse a limit number from that argument.", sender);
				return true;
			}

			java.util.Map<UUID, Long> fastestTimes = LeaderboardManager.getFastestTimesForMap(map.getInternalName(),
					limit);

			int placement = 1;
			for (Entry<UUID, Long> entry : fastestTimes.entrySet()) {
				Messager.msgSender(String.format("#%s - %s | %s", placement + "",
						Bukkit.getOfflinePlayer(entry.getKey()).getName(), Messager.formatAsTime(entry.getValue())),
						sender);
				placement++;
			}

			Messager.msgSender("&aFinished listing fastest times", sender);
			return true;
		}

		return false;
	}

}
