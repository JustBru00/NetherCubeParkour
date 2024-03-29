package com.justbru00.epic.icetrack.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class PluginFile extends YamlConfiguration {   
   
    private File file;
    private String defaults;
    private JavaPlugin plugin;
   
    /**
     * Creates new PluginFile, without defaults
     * @param plugin - Your plugin
     * @param fileName - Name of the file
     */
    public PluginFile(JavaPlugin plugin, String fileName) {
        this(plugin, fileName, null);
    }
   
    /**
     * Creates new PluginFile, with defaults
     * @param plugin - Your plugin
     * @param fileName - Name of the file
     * @param defaultsName - Name of the defaults
     */
    public PluginFile(JavaPlugin plugin, String fileName, String defaultsName) {
        this.plugin = plugin;
        this.defaults = defaultsName;
        this.file = new File(plugin.getDataFolder(), fileName);
        reload();
    }
    
    /**
     * My addition to the PluginFile class. 
     * This method allows you to pass a {@link File} instead of a file name.
     * Make sure your path is inside the server directory or you might have issues.
     * @param plugin
     * @param file
     */
    public PluginFile(JavaPlugin plugin, File file) {
    	this.plugin = plugin;
    	this.defaults = null;
    	this.file = file;
    	reload();
    }
   
    /**
     * Reload configuration
     */
    public void reload() {
       
        if (!file.exists()) {
           
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
               
            } catch (IOException exception) {
                exception.printStackTrace();
                plugin.getLogger().severe("Error while creating file " + file.getName());
            }
           
        }
       
        try {
            load(file);
           
            if (defaults != null) {
                InputStreamReader reader = new InputStreamReader(plugin.getResource(defaults));
                FileConfiguration defaultsConfig = YamlConfiguration.loadConfiguration(reader);       
               
                setDefaults(defaultsConfig);
                options().copyDefaults(true);
               
                reader.close();
                save();
            }
       
        } catch (IOException exception) {
            exception.printStackTrace();
            plugin.getLogger().severe("Error while loading file " + file.getName());
           
        } catch (InvalidConfigurationException exception) {
            exception.printStackTrace();
            plugin.getLogger().severe("Error while loading file " + file.getName());
           
        }
       
    }
   
    /**
     * Save configuration
     */
    public void save() {
       
        try {
            options().indent(2);
            save(file);
           
        } catch (IOException exception) {
            exception.printStackTrace();
            plugin.getLogger().severe("Error while saving file " + file.getName());
        }
       
    }
   
}