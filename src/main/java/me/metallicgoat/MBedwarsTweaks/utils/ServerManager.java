package me.metallicgoat.MBedwarsTweaks.utils;

import me.metallicgoat.MBedwarsTweaks.Main;
import me.metallicgoat.MBedwarsTweaks.tweaks.disablegens.UnusedGens;
import me.metallicgoat.MBedwarsTweaks.tweaks.topkillers.OnRoundEnd;
import me.metallicgoat.MBedwarsTweaks.tweaks.explotions.AutoIgnite;
import me.metallicgoat.MBedwarsTweaks.tweaks.explotions.Whitelist;
import me.metallicgoat.MBedwarsTweaks.tweaks.finalkill.FinalKillMessage;
import me.metallicgoat.MBedwarsTweaks.tweaks.finalkill.FinalStrike;
import me.metallicgoat.MBedwarsTweaks.tweaks.genupdater.GenTiers;
import me.metallicgoat.MBedwarsTweaks.tweaks.invis.BreakInvis;
import me.metallicgoat.MBedwarsTweaks.tweaks.shopmessage.OnBuy;
import me.metallicgoat.MBedwarsTweaks.tweaks.useditems.EmptyBucket;
import me.metallicgoat.MBedwarsTweaks.tweaks.useditems.EmptyPotion;
import me.metallicgoat.MBedwarsTweaks.tweaks.waterflow.WaterFlow;
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
        manager.registerEvents(new OnBuy(), plugin());
        manager.registerEvents(new Whitelist(), plugin());
        manager.registerEvents(new AutoIgnite(), plugin());
        manager.registerEvents(new GenTiers(), plugin());
        manager.registerEvents(new WaterFlow(), plugin());

        manager.registerEvents(new UnusedGens(), plugin());

        manager.registerEvents(new FinalKillMessage(), plugin());


        manager.registerEvents(new OnRoundEnd(), plugin());
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
