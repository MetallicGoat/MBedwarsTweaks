package me.metallicgoat.tweaksaddon;

import de.marcely.bedwars.api.event.ConfigsLoadEvent;
import me.metallicgoat.tweaksaddon.config.GenTiersConfig;
import me.metallicgoat.tweaksaddon.config.MainConfig;
import me.metallicgoat.tweaksaddon.config.SwordsToolsConfig;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class LoadConfigs implements Listener {

    //TODO make this better, maybe with a reload event?

    private static boolean isLoading = false;
    private static long lastLoad = 0;

    @EventHandler
    public void onConfigLoad(ConfigsLoadEvent event) {
        loadTweaksConfigs();
    }

    public static void loadTweaksConfigs() {

        if(isLoading || (System.currentTimeMillis() - lastLoad) < 2000)
            return;

        isLoading = true;

        final long start = System.currentTimeMillis();

        MainConfig.load();
        SwordsToolsConfig.load();
        GenTiersConfig.load();

        isLoading = false;
        lastLoad = System.currentTimeMillis();

        // TODO console log class
        System.out.println("Configs loaded in " + (lastLoad - start) + "ms.");
    }
}
