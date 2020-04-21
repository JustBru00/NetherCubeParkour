package com.gmail.justbru00.nethercube.parkour.utils;

import org.bukkit.entity.Player;

import com.gmail.justbru00.nethercube.parkour.data.PlayerData;
import com.gmail.justbru00.nethercube.parkour.main.NetherCubeParkour;

import me.clip.placeholderapi.external.EZPlaceholderHook;

@SuppressWarnings("deprecation")
public class NetherCubeParkourPlaceholders extends EZPlaceholderHook {

	public NetherCubeParkourPlaceholders(NetherCubeParkour plugin) {
		super(plugin, "nethercubeparkour");		
	}

	@Override
	public String onPlaceholderRequest(Player p, String identifier) {
		
		if (p == null) {
			return "";
		}
		
		// placeholder: %nethercubeparkour_currency%
		if (identifier.equals("currency")) {
			return String.valueOf(PlayerData.getDataFor(p).getCurrency());
		}
		
		return null;
	}
	
	

}
