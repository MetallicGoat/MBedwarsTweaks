package me.metallicgoat.MBedwarsTweaks.utils;

import me.metallicgoat.MBedwarsTweaks.Main;
import me.metallicgoat.MBedwarsTweaks.advancedswords.*;
import me.metallicgoat.MBedwarsTweaks.bedbreakeffects.*;
import me.metallicgoat.MBedwarsTweaks.tweaks.explotions.*;
import me.metallicgoat.MBedwarsTweaks.tweaks.messages.*;
import me.metallicgoat.MBedwarsTweaks.tweaks.misc.*;
import me.metallicgoat.MBedwarsTweaks.tweaks.spawners.*;
import me.metallicgoat.MBedwarsTweaks.utils.configupdater.ConfigUpdater;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

public class ServerManager {

    private static FileConfiguration tiersConfig;

    private static FileConfiguration swordsToolsConfig;

    public static FileConfiguration getConfig(){
        return plugin().getConfig();
    }

    public static FileConfiguration getTiersConfig(){
        return tiersConfig;
    }

    public static FileConfiguration getSwordsToolsConfig(){
        return swordsToolsConfig;
    }

    public static void loadConfigs() {
        loadDefaultConfig();
        loadTiersConfig();
        loadSwordsToolsConfig();
    }

    public static void registerEvents(){
        PluginManager manager = plugin().getServer().getPluginManager();

        //Tweaks - explosions
        manager.registerEvents(new AutoIgnite(), plugin());
        manager.registerEvents(new FireballWhitelist(), plugin());

        //Tweaks - messages
        manager.registerEvents(new BuyMessage(), plugin());
        manager.registerEvents(new FinalKill(), plugin());
        manager.registerEvents(new TeamEliminate(), plugin());
        manager.registerEvents(new TopKillers(), plugin());

        //Tweaks - misc
        manager.registerEvents(new BreakInvis(), plugin());
        manager.registerEvents(new EmptyBucket(), plugin());
        manager.registerEvents(new EmptyPotion(), plugin());
        manager.registerEvents(new FinalStrike(), plugin());
        manager.registerEvents(new FriendlyVillagers(), plugin());
        manager.registerEvents(new HeightCap(), plugin());
        manager.registerEvents(new PermanentEffects(), plugin());
        manager.registerEvents(new ShortenCountdown(), plugin());
        manager.registerEvents(new WaterFlow(), plugin());

        //Tweaks - spawners
        manager.registerEvents(new GenTiers(), plugin());
        manager.registerEvents(new ScheduleBedBreak(), plugin());
        manager.registerEvents(new UnusedGens(), plugin());


        //Advanced Swords
        manager.registerEvents(new AlwaysSword(), plugin());
        manager.registerEvents(new AntiChest(), plugin());
        manager.registerEvents(new AntiDrop(), plugin());
        manager.registerEvents(new DowngradeTools(), plugin());
        manager.registerEvents(new OrderedSwordBuy(), plugin());
        manager.registerEvents(new ReplaceSwordOnBuy(), plugin());
        manager.registerEvents(new SwordDrop(), plugin());
        manager.registerEvents(new ToolBuyV2(), plugin());

        //Break Effects
        manager.registerEvents(new BedDestroyListener(), plugin());


    }



    private static void loadDefaultConfig(){
        plugin().saveDefaultConfig();
        File configFile = new File(plugin().getDataFolder(), "config.yml");

        try {
            ConfigUpdater.update(plugin(), "config.yml", configFile, Collections.singletonList("nothing"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        plugin().reloadConfig();
    }

    private static void loadTiersConfig(){
        String ymlName = "gen-tiers.yml";

        File configFile = new File(plugin().getDataFolder(), ymlName);
        if (!configFile.exists()) {
            plugin().saveResource(ymlName, false);
        }

        try {
            ConfigUpdater.update(plugin(), ymlName, configFile, Collections.singletonList("Gen-Tiers"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        tiersConfig = new YamlConfiguration();
        try {
            tiersConfig.load(configFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    private static void loadSwordsToolsConfig(){
        String ymlName = "swords-tools.yml";

        File configFile = new File(plugin().getDataFolder(), ymlName);
        if (!configFile.exists()) {
            plugin().saveResource(ymlName, false);
        }

        try {
            ConfigUpdater.update(plugin(), ymlName, configFile, Collections.singletonList("Nothing"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        swordsToolsConfig = new YamlConfiguration();
        try {
            swordsToolsConfig.load(configFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public static Main plugin(){
        return Main.getInstance();
    }
}
