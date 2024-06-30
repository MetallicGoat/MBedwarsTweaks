package me.metallicgoat.tweaksaddon.config;

import de.marcely.bedwars.api.configuration.ConfigurationAPI;
import de.marcely.bedwars.api.event.ConfigsLoadEvent;
import de.marcely.bedwars.tools.Helper;
import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;
import me.metallicgoat.tweaksaddon.config.ConfigManager.FileType;
import me.metallicgoat.tweaksaddon.gentiers.GenTierLevel;
import me.metallicgoat.tweaksaddon.gentiers.TierAction;
import me.metallicgoat.tweaksaddon.utils.Console;
import me.metallicgoat.tweaksaddon.utils.Util;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ConfigLoader implements Listener {

  @EventHandler
  public void onConfigLoad(ConfigsLoadEvent event) {
    if (!event.isStartup()) {
      MBedwarsTweaksPlugin.getInstance().loadTweaks(); // Reload tweaks
    }
  }

  public static void loadTweaksConfigs(MBedwarsTweaksPlugin plugin) {
    final long start = System.currentTimeMillis();

    final boolean defaultDropTypesExist = (
        Util.getDropType("iron") != null
            && Util.getDropType("gold") != null
            && Util.getDropType("diamond") != null
            && Util.getDropType("emerald") != null
    );

    // Replace with these values if we know defaults are still setup
    if (defaultDropTypesExist) {
      MainConfig.gen_tiers_start_spawners = new ArrayList<>(Arrays.asList(
          Util.getDropType("emerald"),
          Util.getDropType("diamond")
      ));

      MainConfig.disable_empty_generators_spawners = new ArrayList<>(Arrays.asList(
          Util.getDropType("iron"),
          Util.getDropType("gold")
      ));
    }

    GenTiersConfig.gen_tier_levels = getDefaultGenTiers();

    ConfigManager.load(plugin, MainConfig.class, FileType.MAIN);
    ConfigManager.load(plugin, SwordsToolsConfig.class, FileType.SWORDS_TOOLS);
    GenTiersConfig.load(); // We load gen tiers a special way

    overrideMBedwarsConfigs();

    final long end = System.currentTimeMillis();

    Console.printInfo("Configs loaded in " + (end - start) + "ms.");
  }

  public static HashMap<Integer, GenTierLevel> getDefaultGenTiers() {
    return new HashMap<Integer, GenTierLevel>() {{
      put(1, new GenTierLevel(
          1,
          "Diamond II", "&eTier &cII",
          "diamond",
          TierAction.GEN_UPGRADE, 6, 30D, null,
          "&bDiamond Generators &ehave been upgraded to Tier &4II",
          null
      ));
      put(2, new GenTierLevel(
          2,
          "Emerald II", "&eTier &cII",
          "emerald",
          TierAction.GEN_UPGRADE, 6, 40D, null,
          "&aEmerald Generators &ehave been upgraded to Tier &4II",
          null
      ));
      put(3, new GenTierLevel(
          3,
          "Diamond III", "&eTier &cIII",
          "diamond",
          TierAction.GEN_UPGRADE, 6, 20D, null,
          "&bDiamond Generators &ehave been upgraded to Tier &4III",
          null
      ));
      put(4, new GenTierLevel(
          4,
          "Emerald III", "&eTier &cIII",
          "emerald",
          TierAction.GEN_UPGRADE, 6, 30D, null,
          "&aEmerald Generators &ehave been upgraded to Tier &4III",
          null
      ));
      put(5, new GenTierLevel(5, "Bed Destroy", TierAction.BED_DESTROY, 5, null, null));
      put(6, new GenTierLevel(6, "Sudden Death", TierAction.SUDDEN_DEATH, 10, null, null));
      put(7, new GenTierLevel(7, "Game Over", TierAction.GAME_OVER, 10, null, null));
    }};
  }

  private static void overrideMBedwarsConfigs() {
    // Configure MBedwars to work like our old feature did
    if (MainConfig.personal_ender_chests_enabled) {
      try {
        final boolean teamchestEnabled = (boolean) ConfigurationAPI.get().getValue("teamchest-enabled");
        final Material teamchestBlock = (Material) ConfigurationAPI.get().getValue("teamchest-block");

        if (teamchestEnabled && teamchestBlock == Helper.get().getMaterialByName("ENDER_CHEST")) {
          ConfigurationAPI.get().setValue("teamchest-enabled", false);
          Console.printWarn(
              "WARNING: You have \"personal-ender-chests-enabled\" enabled. This setting will be removed in the future, as it is already possible in MBedwars.",
              "Open your MBedwars config.yml, and either set \"teamchest-enabled\" to false, or set \"teamchest-block\" to CHEST.",
              "Currently, we are using the MBedwars configuration api to override these values for you, but we may not do this in the future."
          );
        }

      } catch (IllegalArgumentException e) {
        Console.printWarn("Failed to apply personal ender chests. Try updating MBedwars, or disabling \"personal-ender-chests-enabled\"");
      }
    }
  }
}
