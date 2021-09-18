package me.metallicgoat.MBedwarsTweaks.utils;

import me.metallicgoat.MBedwarsTweaks.Main;
import me.metallicgoat.MBedwarsTweaks.tweaks.messages.FinalKill;
import me.metallicgoat.MBedwarsTweaks.tweaks.misc.*;
import me.metallicgoat.MBedwarsTweaks.tweaks.spawners.ScheduleBedBreak;
import me.metallicgoat.MBedwarsTweaks.tweaks.spawners.UnusedGens;
import me.metallicgoat.MBedwarsTweaks.tweaks.messages.TopKillers;
import me.metallicgoat.MBedwarsTweaks.tweaks.explotions.AutoIgnite;
import me.metallicgoat.MBedwarsTweaks.tweaks.explotions.FireballWhitelist;
import me.metallicgoat.MBedwarsTweaks.tweaks.spawners.GenTiers;
import me.metallicgoat.MBedwarsTweaks.tweaks.messages.BuyMessage;
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

    public static FileConfiguration getConfig(){
        return plugin().getConfig();
    }

    public static FileConfiguration getTiersConfig(){
        return tiersConfig;
    }

    public static void loadConfigs() {
        loadDefaultConfig();
        loadTiersConfig();
    }

    public static void registerEvents(){
        PluginManager manager = plugin().getServer().getPluginManager();
        manager.registerEvents(new EmptyBucket(), plugin());
        manager.registerEvents(new EmptyPotion(), plugin());
        manager.registerEvents(new BreakInvis(), plugin());
        manager.registerEvents(new FinalStrike(), plugin());
        manager.registerEvents(new BuyMessage(), plugin());
        manager.registerEvents(new FireballWhitelist(), plugin());
        manager.registerEvents(new AutoIgnite(), plugin());
        manager.registerEvents(new GenTiers(), plugin());
        manager.registerEvents(new WaterFlow(), plugin());
        manager.registerEvents(new HeightCap(), plugin());

        //manager.registerEvents(new test(), plugin());

        manager.registerEvents(new UnusedGens(), plugin());
        manager.registerEvents(new ScheduleBedBreak(), plugin());

        manager.registerEvents(new FinalKill(), plugin());


        manager.registerEvents(new TopKillers(), plugin());
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

    public static Main plugin(){
        return Main.getInstance();
    }
}
