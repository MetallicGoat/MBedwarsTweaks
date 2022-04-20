package me.metallicgoat.tweaksaddon.AA_old.utils;

import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;
import me.metallicgoat.tweaksaddon.AA_old.advancedswords.*;
import me.metallicgoat.tweaksaddon.tweaks.advancedswords.*;
import me.metallicgoat.tweaksaddon.tweaks.cosmetic.ActionBar;
import me.metallicgoat.tweaksaddon.tweaks.mechanics.FireballBlockBreakWhitelist;
import me.metallicgoat.tweaksaddon.tweaks.cosmetic.FinalStrike;
import me.metallicgoat.tweaksaddon.tweaks.cosmetic.SpongeParticles;
import me.metallicgoat.tweaksaddon.tweaks.gameplay.LockTeamChest;
import me.metallicgoat.tweaksaddon.tweaks.gameplay.PermanentEffects;
import me.metallicgoat.tweaksaddon.tweaks.gameplay.PersonalChests;
import me.metallicgoat.tweaksaddon.tweaks.mechanics.DisableFireballOutsideArena;
import me.metallicgoat.tweaksaddon.tweaks.mechanics.PreventLiquidBuildUp;
import me.metallicgoat.tweaksaddon.tweaks.messages.BuyMessage;
import me.metallicgoat.tweaksaddon.tweaks.messages.FinalKillSuffix;
import me.metallicgoat.tweaksaddon.tweaks.messages.TeamEliminate;
import me.metallicgoat.tweaksaddon.AA_old.tweaks.messages.TopKillers;
import me.metallicgoat.tweaksaddon.AA_old.tweaks.misc.*;
import me.metallicgoat.tweaksaddon.AA_old.tweaks.spawners.GenTiers;
import me.metallicgoat.tweaksaddon.AA_old.tweaks.spawners.ScheduleBedBreak;
import me.metallicgoat.tweaksaddon.AA_old.utils.cmd.*;
import me.metallicgoat.tweaksaddon.AA_old.utils.configupdater.ConfigUpdater;
import me.metallicgoat.tweaksaddon.tweaks.server.PlayerLimitBypass;
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

    private static void registerEvents(){
        PluginManager manager = plugin.getServer().getPluginManager();

        //Tweaks - explosions
        manager.registerEvents(new DisableFireballOutsideArena(), plugin);
        manager.registerEvents(new FireballBlockBreakWhitelist(), plugin);

        //Tweaks - messages
        manager.registerEvents(new BuyMessage(), plugin);
        manager.registerEvents(new FinalKillSuffix(), plugin);
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
        manager.registerEvents(new PreventLiquidBuildUp(), plugin);

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
