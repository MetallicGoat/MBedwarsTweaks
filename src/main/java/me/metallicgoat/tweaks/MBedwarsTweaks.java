package me.metallicgoat.tweaks;

import de.marcely.bedwars.api.BedwarsAPI;
import me.metallicgoat.tweaks.tweaks.spawners.GenTiers;
import me.metallicgoat.tweaks.utils.Metrics;
import me.metallicgoat.tweaks.utils.ServerManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class MBedwarsTweaks extends JavaPlugin {

    private static MBedwarsTweaks instance;
    public static boolean papiEnabled = false;

    public void onEnable() {
        int pluginId = 11928;
        Metrics metrics = new Metrics(this, pluginId);

        instance = this;
        ServerManager.load();

        PluginDescriptionFile pdf = this.getDescription();

        log(
                "------------------------------",
                pdf.getName() + " For MBedwars",
                "By: " + pdf.getAuthors(),
                "Version: " + pdf.getVersion(),
                "------------------------------"
        );

        if(Bukkit.getServer().getPluginManager().isPluginEnabled("MBedwars") &&
                BedwarsAPI.getAPIVersion() >= 7) {
            BedwarsAPI.onReady(() -> {
                if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                    new Placeholders().register();
                    papiEnabled = true;
                } else {
                    log("PlaceholderAPI Was not Found! Placeholders wont work!");
                }
                GenTiers.section = ServerManager.getTiersConfig().getConfigurationSection("Gen-Tiers");
            });
        }else{
            log("This extension requires MBedwars v5.0.5 or later! Disabling Addon...");
            this.setEnabled(false);
        }
    }

    public static MBedwarsTweaks getInstance() {
        return instance;
    }

    private void log(String ...args) {
        for(String s : args)
            getLogger().info(s);
    }
}