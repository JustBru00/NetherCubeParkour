package com.gmail.justbru00.nethercube.parkour.main;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.justbru00.nethercube.parkour.commands.ParkourAdminCommand;
import com.gmail.justbru00.nethercube.parkour.commands.ParkourBalanceCommand;
import com.gmail.justbru00.nethercube.parkour.commands.ParkourCommand;
import com.gmail.justbru00.nethercube.parkour.commands.ParkourLobbyCommand;
import com.gmail.justbru00.nethercube.parkour.commands.ParkourTpCommand;
import com.gmail.justbru00.nethercube.parkour.gui.GUIManager;
import com.gmail.justbru00.nethercube.parkour.leaderboards.LeaderboardManager;
import com.gmail.justbru00.nethercube.parkour.listeners.ConfirmGUIListener;
import com.gmail.justbru00.nethercube.parkour.listeners.MainGUIListener;
import com.gmail.justbru00.nethercube.parkour.listeners.PressurePlateTriggerListener;
import com.gmail.justbru00.nethercube.parkour.listeners.WorldLeaveListener;
import com.gmail.justbru00.nethercube.parkour.map.MapManager;
import com.gmail.justbru00.nethercube.parkour.timer.PlayerTimer;
import com.gmail.justbru00.nethercube.parkour.utils.Messager;
import com.gmail.justbru00.nethercube.parkour.utils.NetherCubeParkourPlaceholders;
import com.gmail.justbru00.nethercube.parkour.utils.PluginFile;

public class NetherCubeParkour extends JavaPlugin {
	
	public static ConsoleCommandSender console = Bukkit.getConsoleSender();
	public static Logger log = Bukkit.getLogger();
	public static String prefix = Messager.color("&8[&cNether&6Cube&fParkour&8] &6");
	private static NetherCubeParkour instance;
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
			new NetherCubeParkourPlaceholders(instance).hook();
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
		getCommand("parkour").setExecutor(new ParkourCommand());
		getCommand("parkouradmin").setExecutor(new ParkourAdminCommand());
		getCommand("parkourbalance").setExecutor(new ParkourBalanceCommand());
		getCommand("parkourlobby").setExecutor(new ParkourLobbyCommand());
		getCommand("parkourtp").setExecutor(new ParkourTpCommand());
		
		// REGISTER LISTENERS
		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new MainGUIListener(), instance);
		pm.registerEvents(new ConfirmGUIListener(), instance);
		pm.registerEvents(new PressurePlateTriggerListener(), instance);
		pm.registerEvents(new WorldLeaveListener(), instance);
		
	}
	
	public static NetherCubeParkour getInstance() {
		return instance;
	}

}
