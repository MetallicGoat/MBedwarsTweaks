package me.metallicgoat.tweaksaddon.gentiers.handlers;

import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.game.spawner.Spawner;
import de.marcely.bedwars.api.game.spawner.SpawnerDurationModifier;
import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;
import me.metallicgoat.tweaksaddon.config.MainConfig;
import me.metallicgoat.tweaksaddon.gentiers.GenTierLevel;
import me.metallicgoat.tweaksaddon.gentiers.GenTiers;

public class SpawnerUpgradeHandler extends GenTierHandler {
  @Override
  public void run(GenTierLevel level, Arena arena) {
    // For all spawners
    for (Spawner spawner : arena.getSpawners()) {
      if (level.getType() != null && spawner.getDropType() == level.getType()) {
        // Set drop time
        if (level.getSpeed() != null)
          spawner.addDropDurationModifier("GEN_UPGRADE", MBedwarsTweaksPlugin.getInstance(), SpawnerDurationModifier.Operation.SET, level.getSpeed());

        // Set new limit
        if (level.getLimit() != null)
          spawner.setMaxNearbyItems(level.getLimit());

        // Add custom Holo tiles
        if (MainConfig.gen_tiers_custom_holo_enabled)
          GenTiers.formatHoloTiles(level.getHoloName(), spawner);

      }
    }
  }
}
