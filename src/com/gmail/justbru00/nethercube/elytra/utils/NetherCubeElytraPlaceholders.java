package com.gmail.justbru00.nethercube.elytra.utils;

import org.bukkit.entity.Player;
import com.gmail.justbru00.nethercube.elytra.data.PlayerData;
import com.gmail.justbru00.nethercube.elytra.main.NetherCubeElytra;

import me.clip.placeholderapi.external.EZPlaceholderHook;

@SuppressWarnings("deprecation")
public class NetherCubeElytraPlaceholders extends EZPlaceholderHook {

	public NetherCubeElytraPlaceholders(NetherCubeElytra plugin) {
		super(plugin, "nethercubeelytra");		
	}

	@Override
	public String onPlaceholderRequest(Player p, String identifier) {
		
		if (p == null) {
			return "";
		}
		
		// placeholder: %nethercubeelytra_currency%
		if (identifier.equals("currency")) {
			return String.valueOf(PlayerData.getDataFor(p).getCurrency());
		}
		
		return null;
	}
	
	

}
