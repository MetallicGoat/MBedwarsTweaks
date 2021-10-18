package me.metallicgoat.MBedwarsTweaks;

import de.marcely.bedwars.api.BedwarsAPI;
import me.metallicgoat.MBedwarsTweaks.tweaks.spawners.GenTiers;
import me.metallicgoat.MBedwarsTweaks.utils.Metrics;
import me.metallicgoat.MBedwarsTweaks.utils.ServerManager;
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
            GenTiers.startUpdatingTime();
            new Placeholders().register();
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