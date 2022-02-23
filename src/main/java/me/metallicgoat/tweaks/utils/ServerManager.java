package me.metallicgoat.tweaks.utils;

import me.metallicgoat.tweaks.MBedwarsTweaksPlugin;
import me.metallicgoat.tweaks.old.advancedswords.*;
import me.metallicgoat.tweaks.old.tweaks.explotions.FireballOutsideArena;
import me.metallicgoat.tweaks.old.tweaks.explotions.FireballWhitelist;
import me.metallicgoat.tweaks.old.tweaks.messages.BuyMessage;
import me.metallicgoat.tweaks.old.tweaks.messages.FinalKill;
import me.metallicgoat.tweaks.old.tweaks.messages.TeamEliminate;
import me.metallicgoat.tweaks.old.tweaks.messages.TopKillers;
import me.metallicgoat.tweaks.old.tweaks.misc.*;
import me.metallicgoat.tweaks.old.tweaks.spawners.GenTiers;
import me.metallicgoat.tweaks.old.tweaks.spawners.ScheduleBedBreak;
import me.metallicgoat.tweaks.old.tweaks.spawners.UnusedGens;
import me.metallicgoat.tweaks.utils.cmd.*;
import me.metallicgoat.tweaks.utils.configupdater.ConfigUpdater;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;

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
        registerEvents();
        loadConfigs();
    }

    public static void reload(){
        defaultConfig = reloadConfig("config.yml");
        swordsToolsConfig = reloadConfig("swords-tools.yml");
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
        tiersConfig = loadCustomConfig("gen-tiers.yml", Collections.singletonList("Gen-Tiers"));
        swordsToolsConfig = loadCustomConfig("swords-tools.yml", Collections.singletonList("Nothing"));
    }

    private static void registerEvents(){
        PluginManager manager = plugin.getServer().getPluginManager();

        //Tweaks - explosions
        manager.registerEvents(new FireballOutsideArena(), plugin);
        manager.registerEvents(new FireballWhitelist(), plugin);

        //Tweaks - messages
        manager.registerEvents(new BuyMessage(), plugin);
        manager.registerEvents(new FinalKill(), plugin);
        manager.registerEvents(new TeamEliminate(), plugin);
        manager.registerEvents(new TopKillers(), plugin);

        //Tweaks - misc
        manager.registerEvents(new ActionBar(), plugin);
        manager.registerEvents(new BreakInvis(), plugin);
        manager.registerEvents(new EmptyBucket(), plugin);
        manager.registerEvents(new EmptyPotion(), plugin);
        manager.registerEvents(new FinalStrike(), plugin);
        manager.registerEvents(new FriendlyVillagers(), plugin);
        manager.registerEvents(new HeightCap(), plugin);
        manager.registerEvents(new LockTeamChest(), plugin);
        manager.registerEvents(new PermanentEffects(), plugin);
        manager.registerEvents(new PersonalChests(), plugin);
        manager.registerEvents(new PlayerLimitBypass(), plugin);
        manager.registerEvents(new ShortenCountdown(), plugin);
        manager.registerEvents(new SpongeParticles(), plugin);
        manager.registerEvents(new WaterFlow(), plugin);

        //Tweaks - spawners
        manager.registerEvents(new GenTiers(), plugin);
        manager.registerEvents(new ScheduleBedBreak(), plugin);
        manager.registerEvents(new UnusedGens(), plugin);

        //Advanced Swords
        manager.registerEvents(new AlwaysSword(), plugin);
        manager.registerEvents(new AntiChest(), plugin);
        manager.registerEvents(new AntiDrop(), plugin);
        manager.registerEvents(new DowngradeTools(), plugin);
        manager.registerEvents(new OneSlotTools(), plugin);
        manager.registerEvents(new OrderedSwordBuy(), plugin);
        manager.registerEvents(new ReplaceSwordOnBuy(), plugin);
        manager.registerEvents(new SwordDrop(), plugin);
        manager.registerEvents(new ToolBuy(), plugin);
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
