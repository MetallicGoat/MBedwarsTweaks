package me.metallicgoat.tweaksaddon;

import de.marcely.bedwars.api.event.ConfigsLoadEvent;
import me.metallicgoat.tweaksaddon.config.ConfigValue;
import me.metallicgoat.tweaksaddon.config.GenTiersConfig;
import me.metallicgoat.tweaksaddon.config.MainConfig;
import me.metallicgoat.tweaksaddon.config.SwordsToolsConfig;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class LoadConfigs implements Listener {

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

        ConfigValue.defaultDropTypesExist = (
                Util.getDropType("iron") != null
                        && Util.getDropType("gold") != null
                        && Util.getDropType("diamond") != null
                        && Util.getDropType("emerald") != null
        );

        if(!ConfigValue.defaultDropTypesExist)
            Console.printConfigInfo("Not all default DropTypes are present. This causes issues if you are generating configs for the first time. If you already have your configs setup, ignore this.", "gen-tiers");

        MainConfig.load();
        SwordsToolsConfig.load();
        GenTiersConfig.load();

        isLoading = false;
        lastLoad = System.currentTimeMillis();

        Console.printInfo("Configs loaded in " + (lastLoad - start) + "ms.");
    }
}
