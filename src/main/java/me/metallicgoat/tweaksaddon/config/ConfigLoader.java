package me.metallicgoat.tweaksaddon.config;

import de.marcely.bedwars.api.arena.Team;
import java.util.HashMap;
import java.util.Map.Entry;
import me.metallicgoat.tweaksaddon.Console;
import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;
import me.metallicgoat.tweaksaddon.Util;
import me.metallicgoat.tweaksaddon.config.ConfigManager.FileType;
import me.metallicgoat.tweaksaddon.tweaks.spawners.GenTierLevel;
import me.metallicgoat.tweaksaddon.tweaks.spawners.TierAction;
import net.md_5.bungee.api.ChatColor;

public class ConfigLoader {

  public static void loadTweaksConfigs(MBedwarsTweaksPlugin plugin) {
    final long start = System.currentTimeMillis();

    MainConfig.defaultDropTypesExist = (
        Util.getDropType("iron") != null
            && Util.getDropType("gold") != null
            && Util.getDropType("diamond") != null
            && Util.getDropType("emerald") != null
    );

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
          "Diamond II", "&eTier &cII",
          "diamond",
          TierAction.GEN_UPGRADE, 6, 30D, null,
          "&bDiamond Generators &ehave been upgraded to Tier &4II",
          null
      ));
      put(2, new GenTierLevel(
          "Emerald II", "&eTier &cII",
          "emerald",
          TierAction.GEN_UPGRADE, 6, 40D, null,
          "&aEmerald Generators &ehave been upgraded to Tier &4II",
          null
      ));
      put(3, new GenTierLevel(
          "Diamond III", "&eTier &cIII",
          "diamond",
          TierAction.GEN_UPGRADE, 6, 20D, null,
          "&bDiamond Generators &ehave been upgraded to Tier &4III",
          null
      ));
      put(4, new GenTierLevel(
          "Emerald III", "&eTier &cIII",
          "emerald",
          TierAction.GEN_UPGRADE, 6, 30D, null,
          "&aEmerald Generators &ehave been upgraded to Tier &4III",
          null
      ));
      put(5, new GenTierLevel("Auto-Break", "Bed Destroy", TierAction.BED_DESTROY, 7, "All beds have been broken", null));
      put(6, new GenTierLevel("Game-Over", "Game Over", TierAction.GAME_OVER, 10, "Game Ended", null));
    }};
  }

  private static void applyCustomTeamColors() {
    if (!MainConfig.custom_team_colors_enabled)
      return;

    for(Entry<Team, ChatColor> entry : MainConfig.custom_team_colors.entrySet())
      entry.getKey().setBungeeChatColor(entry.getValue());
  }
}
