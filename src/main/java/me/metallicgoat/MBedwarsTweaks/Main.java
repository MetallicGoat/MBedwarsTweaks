package me.metallicgoat.MBedwarsTweaks;

import de.marcely.bedwars.api.BedwarsAPI;
import me.metallicgoat.MBedwarsTweaks.tweaks.spawners.GenTiers;
import me.metallicgoat.MBedwarsTweaks.tweaks.spawners.GenTiersV2;
import me.metallicgoat.MBedwarsTweaks.utils.Metrics;
import me.metallicgoat.MBedwarsTweaks.utils.ServerManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private static Main instance;

    public void onEnable() {
        int pluginId = 11928;
        Metrics metrics = new Metrics(this, pluginId);

        instance = this;
        ServerManager.loadConfigs();
        ServerManager.registerEvents();

        PluginDescriptionFile pdf = this.getDescription();

        log(
                "------------------------------",
                pdf.getName() + " For MBedwars",
                "By: " + pdf.getAuthors(),
                "Version: " + pdf.getVersion(),
                "------------------------------"
        );

        BedwarsAPI.onReady(() -> {
            if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                new Placeholders().register();
            }else{
                log("PlaceholderAPI Was not Found! Placeholders wont work!");
            }
            GenTiersV2.section = ServerManager.getTiersConfig().getConfigurationSection("Gen-Tiers");
        });
    }

    public static Main getInstance() {
        return instance;
    }


    private void log(String ...args) {
        for(String s : args)
            getLogger().info(s);
    }
}