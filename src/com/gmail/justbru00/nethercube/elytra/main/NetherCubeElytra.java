package com.gmail.justbru00.nethercube.elytra.main;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.justbru00.nethercube.elytra.commands.ElytraAdminCommand;
import com.gmail.justbru00.nethercube.elytra.commands.ElytraBalanceCommand;
import com.gmail.justbru00.nethercube.elytra.commands.ElytraCommand;
import com.gmail.justbru00.nethercube.elytra.commands.ElytraLobbyCommand;
import com.gmail.justbru00.nethercube.elytra.commands.ElytraTpCommand;
import com.gmail.justbru00.nethercube.elytra.gui.GUIManager;
import com.gmail.justbru00.nethercube.elytra.leaderboards.LeaderboardManager;
import com.gmail.justbru00.nethercube.elytra.listeners.ConfirmGUIListener;
import com.gmail.justbru00.nethercube.elytra.listeners.MainGUIListener;
import com.gmail.justbru00.nethercube.elytra.listeners.PressurePlateTriggerListener;
import com.gmail.justbru00.nethercube.elytra.listeners.WorldLeaveListener;
import com.gmail.justbru00.nethercube.elytra.map.MapManager;
import com.gmail.justbru00.nethercube.elytra.timer.PlayerTimer;
import com.gmail.justbru00.nethercube.elytra.utils.Messager;
import com.gmail.justbru00.nethercube.elytra.utils.NetherCubeElytraPlaceholders;
import com.gmail.justbru00.nethercube.elytra.utils.PluginFile;

public class NetherCubeElytra extends JavaPlugin {
	
	public static ConsoleCommandSender console = Bukkit.getConsoleSender();
	public static Logger log = Bukkit.getLogger();
	public static String prefix = Messager.color("&8[&cNether&6Cube&fElytra&8] &6");
	private static  NetherCubeElytra instance;
	public static PluginFile dataFile = null;
	public static boolean debug = true;
	public static boolean enableLeaderboards = true;

	@Override
	public void onDisable() {
		Messager.msgConsole("&cThe plugin is disabled.");
		instance = null;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onEnable() {
		instance = this;
		
		Messager.msgConsole("&aEnabling plugin...");
		
		// INIT STUFF
		saveDefaultConfig();
		debug = getConfig().getBoolean("debug");		
		MapManager.init();
		dataFile = new PluginFile(this, "data.yml", "data.yml");		
		GUIManager.init();
		PlayerTimer.init();
		LeaderboardManager.loadLeaderboardLines();
		prefix = Messager.color(getConfig().getString("prefix"));
		
		// ADD PLACEHOLDERS
		if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
			// Register placeholder hook
			new NetherCubeElytraPlaceholders(instance).hook();
		}
		
		
		// CHECK FOR HOLOGRAPHIC DISPLAYS
		if (!Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")) {
			Messager.msgConsole("&cWARNING HOLOGRAPHICDISPLAYS NOT INSTALLED OR ENABLED");
			Messager.msgConsole("&cDISABLING LEADERBOARDS.");
			enableLeaderboards = false;
		} else {
			LeaderboardManager.startUpdateTask();
		}
		
		// REGISTER COMMANDS
		getCommand("elytra").setExecutor(new ElytraCommand());
		getCommand("elytraadmin").setExecutor(new ElytraAdminCommand());
		getCommand("elytrabalance").setExecutor(new ElytraBalanceCommand());
		getCommand("elytralobby").setExecutor(new ElytraLobbyCommand());
		getCommand("elytratp").setExecutor(new ElytraTpCommand());
		
		// REGISTER LISTENERS
		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new MainGUIListener(), instance);
		pm.registerEvents(new ConfirmGUIListener(), instance);
		pm.registerEvents(new PressurePlateTriggerListener(), instance);
		pm.registerEvents(new WorldLeaveListener(), instance);
		
	}
	
	public static NetherCubeElytra getInstance() {
		return instance;
	}

}
