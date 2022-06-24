package me.metallicgoat.tweaksaddon.AA_old.utils;

import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;
import me.metallicgoat.tweaksaddon.config.updater.ConfigUpdater;
import me.metallicgoat.tweaksaddon.AA_old.utils.cmd.*;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class ServerManager {
    
    private static MBedwarsTweaksPlugin plugin;
    private static FileConfiguration defaultConfig;
    private static FileConfiguration tiersConfig;
    private static FileConfiguration swordsToolsConfig;

    public static void load(){
        plugin = MBedwarsTweaksPlugin.getInstance();
        registerCommands();
        loadConfigs();
    }

    public static void reload(){
        defaultConfig = reloadConfig("config.yml");
        swordsToolsConfig = reloadConfig("Old Files/swords-tools.yml");
    }

    private static void registerCommands(){
        PluginCommand command = plugin.getCommand("MBedwarsTweaks");
        if(command != null) {
            command.setExecutor(new Commands());
            command.setTabCompleter(new TabComp());
        }
    }

    private static void loadConfigs() {
        defaultConfig = loadCustomConfig("config.yml", Collections.singletonList("Nothing"));
        tiersConfig = loadCustomConfig("Old Files/gen-tiers.yml", Collections.singletonList("Gen-Tiers"));
        swordsToolsConfig = loadCustomConfig("Old Files/swords-tools.yml", Collections.singletonList("Nothing"));
    }

    private static YamlConfiguration loadCustomConfig(String ymlName, List<String> ignore){
        //Save file
        File configFile = new File(plugin.getDataFolder(), ymlName);
        if (!configFile.exists()) {
            plugin.saveResource(ymlName, false);
        }

        //Run config updater
        try {
            ConfigUpdater.update(plugin, ymlName, configFile, ignore);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Load config
        YamlConfiguration configuration = new YamlConfiguration();
        try {
            configuration.load(configFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        return configuration;
    }

    private static YamlConfiguration reloadConfig(String ymlName){
        File configFile = new File(plugin.getDataFolder(), ymlName);
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(configFile);
        } catch (IOException | InvalidConfigurationException ex) {
            ex.printStackTrace();
        }
        return config;
    }

    public static FileConfiguration getConfig(){
        return defaultConfig;
    }

    public static FileConfiguration getTiersConfig(){
        return tiersConfig;
    }

    public static FileConfiguration getSwordsToolsConfig(){
        return swordsToolsConfig;
    }
}
