/**
The MIT License (MIT)

Copyright (c) 2016 Justin "JustBru00" Brubaker

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
**/ 
package com.gmail.justbru00.nethercube.elytra.utils;

import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.gmail.justbru00.nethercube.elytra.main.NetherCubeElytra;

import net.minecraft.server.v1_12_R1.ChatMessageType;
import net.minecraft.server.v1_12_R1.IChatBaseComponent;
import net.minecraft.server.v1_12_R1.PacketPlayOutChat;
import net.minecraft.server.v1_12_R1.IChatBaseComponent.ChatSerializer;


public class Messager {
	
	public static String color(String uncolored){			
		return ChatColor.translateAlternateColorCodes('&', uncolored);		
	}
	
	/**
	 * "Stolen" from EpicBattleDomeV2
	 * @param msg
	 * @param player
	 */
	public static void sendActionBar(String msg, Player player) {
		msg = Messager.color(msg);
		IChatBaseComponent cbc = ChatSerializer.a("{\"text\": \"" + msg + "\"}");
        PacketPlayOutChat ppoc = new PacketPlayOutChat(cbc, ChatMessageType.GAME_INFO);       
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(ppoc);
	}
	
	public static void msgConsole(String msg) {		
		//msg = msg.replace("{char}", Integer.toString(CharLimit.getCharLimit()));
		
		if (NetherCubeElytra.console != null) {
		NetherCubeElytra.console.sendMessage(NetherCubeElytra.prefix + Messager.color(msg));		
		} else {
			NetherCubeElytra.log.info(ChatColor.stripColor(Messager.color(msg)));
		}
	}
	
	public static void msgPlayer(String msg, Player player) {
		player.sendMessage(Messager.color(NetherCubeElytra.prefix + msg));
	}
	
	public static void msgSender(String msg, CommandSender sender) {
		//msg = msg.replace("{char}", Integer.toString(CharLimit.getCharLimit()));
		sender.sendMessage(NetherCubeElytra.prefix + Messager.color(msg));
	}	
	
	public static void sendBC(String msg) {
		Bukkit.broadcastMessage(Messager.color(NetherCubeElytra.prefix + msg));
	}
	
	public static void debug(String msg) {
		if (NetherCubeElytra.debug) {
			Bukkit.broadcastMessage(Messager.color(NetherCubeElytra.prefix + " &8[&c&lDEBUG&8] &c" + msg));
		}
	}
	
	/**
	 * Returns a time formated with HH:MM:SS:mm
	 * @param timeInMillis
	 * @return
	 */
	public static String formatAsTime(long timeInMillis) {
		String toReturn;
		
		long hours = TimeUnit.MILLISECONDS.toHours(timeInMillis);
		long minutes = TimeUnit.MILLISECONDS.toMinutes(timeInMillis) % TimeUnit.HOURS.toMinutes(1);
		long seconds = TimeUnit.MILLISECONDS.toSeconds(timeInMillis) % TimeUnit.MINUTES.toSeconds(1);
		String milis = String.format("%03d", timeInMillis % 1000);
		
		if (hours == 0 && minutes == 0) {
			toReturn = String.format("%02d", seconds) + "." + milis;			
		} else if (hours == 0) {
			toReturn = String.format("%02d:%02d", minutes, seconds) + "." + milis;
		} else {
			toReturn = String.format("%d:%02d:%02d", hours, minutes, seconds) + "." + milis;
		}
		return Messager.color(toReturn);
	}
}
