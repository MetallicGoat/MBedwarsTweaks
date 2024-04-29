package me.metallicgoat.tweaksaddon.config;

import de.marcely.bedwars.api.arena.Team;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;
import me.metallicgoat.tweaksaddon.utils.Console;
import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;
import me.metallicgoat.tweaksaddon.utils.Util;
import me.metallicgoat.tweaksaddon.config.ConfigManager.FileType;
import me.metallicgoat.tweaksaddon.gentiers.GenTierLevel;
import me.metallicgoat.tweaksaddon.gentiers.TierAction;
import net.md_5.bungee.api.ChatColor;

public class ConfigLoader {

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

    applyCustomTeamColors();

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

  private static void applyCustomTeamColors() {
    if (!MainConfig.custom_team_colors_enabled)
      return;

    for(Entry<Team, ChatColor> entry : MainConfig.custom_team_colors.entrySet())
      entry.getKey().setBungeeChatColor(entry.getValue());
  }
}
