package com.justbru00.epic.icetrack.main;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.justbru00.epic.icetrack.bstats.Metrics;
import com.justbru00.epic.icetrack.commands.*;
import com.justbru00.epic.icetrack.data.AsyncFlatFileManager;
import com.justbru00.epic.icetrack.gui.GUIManager;
import com.justbru00.epic.icetrack.leaderboards.LeaderboardManager;
import com.justbru00.epic.icetrack.listeners.*;
import com.justbru00.epic.icetrack.map.MapManager;
import com.justbru00.epic.icetrack.timer.PlayerTimer;
import com.justbru00.epic.icetrack.utils.Messager;
import com.justbru00.epic.icetrack.utils.PluginFile;

public class EpicIceTrack extends JavaPlugin {
	
	public static ConsoleCommandSender console = Bukkit.getConsoleSender();
	public static Logger log = Bukkit.getLogger();
	public static String prefix = Messager.color("&8[&bEpic&fIceTrack&8] &6");
	private static EpicIceTrack instance;
	public static PluginFile dataFile = null;
	public static boolean debug = true;
	public static boolean enableLeaderboards = true;
	private static final int BSTATS_PLUGIN_ID = 15450;
  public static Instant currentTime = Instant.now();

	@Override
	public void onDisable() {
		AsyncFlatFileManager.onDisable();
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
		
		AsyncFlatFileManager.init();	
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
		
		Metrics bstats = new Metrics(instance, BSTATS_PLUGIN_ID);
		bstats.addCustomChart(new Metrics.SimplePie("number_of_courses", () -> String.valueOf(MapManager.getNumberOfMaps())));
	}
	
	public static EpicIceTrack getInstance() {
		return instance;
	}

}
