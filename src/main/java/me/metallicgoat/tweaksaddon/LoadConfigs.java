package me.metallicgoat.tweaksaddon;

import de.marcely.bedwars.api.event.ConfigsLoadEvent;
import me.metallicgoat.tweaksaddon.config.ConfigValue;
import me.metallicgoat.tweaksaddon.config.GenTiersConfig;
import me.metallicgoat.tweaksaddon.config.MainConfig;
import me.metallicgoat.tweaksaddon.config.SwordsToolsConfig;
import me.metallicgoat.tweaksaddon.tweaks.gentiers.GenTierLevel;
import me.metallicgoat.tweaksaddon.tweaks.gentiers.TierAction;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;

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
        else
            ConfigValue.gen_tier_levels = getDefaultGenTiers();

        MainConfig.load();
        SwordsToolsConfig.load();
        GenTiersConfig.load();

        isLoading = false;
        lastLoad = System.currentTimeMillis();

        Console.printInfo("Configs loaded in " + (lastLoad - start) + "ms.");
    }

    public static HashMap<Integer, GenTierLevel> getDefaultGenTiers() {
        return new HashMap<Integer, GenTierLevel>() {{
            put(1, new GenTierLevel(
                    "Diamond II", "&eTier &cII",
                    Util.getDropType("diamond"),
                    TierAction.GEN_UPGRADE, 6, 30,
                    "&bDiamond Generators &ehave been upgraded to Tier &4II",
                    null
            ));
            put(2, new GenTierLevel(
                    "Emerald II", "&eTier &cII",
                    Util.getDropType("emerald"),
                    TierAction.GEN_UPGRADE, 6, 40,
                    "&aEmerald Generators &ehave been upgraded to Tier &4II",
                    null
            ));
            put(3, new GenTierLevel(
                    "Diamond III", "&eTier &cIII",
                    Util.getDropType("diamond"),
                    TierAction.GEN_UPGRADE, 6, 20,
                    "&bDiamond Generators &ehave been upgraded to Tier &4III",
                    null
            ));
            put(4, new GenTierLevel(
                    "Emerald III", "&eTier &cIII",
                    Util.getDropType("emerald"),
                    TierAction.GEN_UPGRADE, 6, 30,
                    "&aEmerald Generators &ehave been upgraded to Tier &4III",
                    null
            ));
            put(5, new GenTierLevel("Auto-Break", "Bed Destroy", TierAction.BED_DESTROY, 7, "All beds have been broken", null));
            put(6, new GenTierLevel("Game-Over", "Game Over", TierAction.GAME_OVER, 10, "Game Ended", null));
        }};
    }
}
