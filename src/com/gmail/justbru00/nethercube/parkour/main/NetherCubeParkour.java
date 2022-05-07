package com.gmail.justbru00.nethercube.parkour.main;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.justbru00.nethercube.parkour.commands.*;
import com.gmail.justbru00.nethercube.parkour.gui.GUIManager;
import com.gmail.justbru00.nethercube.parkour.leaderboards.LeaderboardManager;
import com.gmail.justbru00.nethercube.parkour.listeners.*;
import com.gmail.justbru00.nethercube.parkour.map.MapManager;
import com.gmail.justbru00.nethercube.parkour.timer.PlayerTimer;
import com.gmail.justbru00.nethercube.parkour.utils.Messager;
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
		
		
		// CHECK FOR HOLOGRAPHIC DISPLAYS
		if (!Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")) {
			Messager.msgConsole("&cWARNING HOLOGRAPHICDISPLAYS NOT INSTALLED OR ENABLED");
			Messager.msgConsole("&cDISABLING LEADERBOARDS.");
			enableLeaderboards = false;
		} else {
			LeaderboardManager.startUpdateTask();
		}
		
		// REGISTER COMMANDS
		getCommand("courses").setExecutor(new ParkourCommand());
		getCommand("parkouradmin").setExecutor(new ParkourAdminCommand());
		getCommand("parkourbalance").setExecutor(new ParkourBalanceCommand());
		getCommand("parkourlobby").setExecutor(new ParkourLobbyCommand());
		getCommand("parkourtp").setExecutor(new ParkourTpCommand());
		getCommand("parkourstart").setExecutor(new ParkourStartCommand());
		getCommand("parkourstop").setExecutor(new ParkourStopCommand());
		getCommand("boatkill").setExecutor(new BoatKillCommand());
		getCommand("parkourcheckpoint").setExecutor(new ParkourCheckpointCommand());
		getCommand("toptimes").setExecutor(new TopTimesCommand());
		
		// REGISTER LISTENERS
		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new MainGUIListener(), instance);
		pm.registerEvents(new ConfirmGUIListener(), instance);
		pm.registerEvents(new PressurePlateTriggerListener(), instance);
		pm.registerEvents(new IceTrackListener(), instance);
		
		// NOTIFY CONSOLE ABOUT CURRENT SETTINGS
		Messager.msgConsole("&6Teleport to lobby on join: " + getConfig().getBoolean("lobbylocation.onjoin"));
		Messager.msgConsole("&6Prevent inventory movement: " + getConfig().getBoolean("prevent_inventory_movement"));
		Messager.msgConsole("&6Clear player inventory on join: " + getConfig().getBoolean("clear_player_inventory_on_join"));
		Messager.msgConsole("&6Give players barrier block: " + getConfig().getBoolean("give_barrier_block_to_middle_slot_on_hotbar"));
	}
	
	public static NetherCubeParkour getInstance() {
		return instance;
	}

}
