package me.metallicgoat.tweaksaddon.tweaks.spawners.gentiers;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.ArenaStatus;
import de.marcely.bedwars.api.arena.Team;
import de.marcely.bedwars.api.event.arena.RoundEndEvent;
import de.marcely.bedwars.api.event.arena.RoundStartEvent;
import de.marcely.bedwars.api.game.spawner.Spawner;
import de.marcely.bedwars.api.game.spawner.SpawnerDurationModifier;
import de.marcely.bedwars.api.message.Message;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.marcely.bedwars.tools.location.XYZD;
import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;
import me.metallicgoat.tweaksaddon.config.GenTiersConfig;
import me.metallicgoat.tweaksaddon.config.MainConfig;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

public class GenTiers implements Listener {

  private static final HashMap<Arena, String> nextTierMap = new HashMap<>();
  private static final HashMap<Arena, Long> nextUpdateTime = new HashMap<>();
  private final HashMap<Arena, BukkitTask> genTierTasks = new HashMap<>();

  @EventHandler
  public void onGameStart(RoundStartEvent event) {
    if (!MainConfig.gen_tiers_enabled)
      return;

    final Arena arena = event.getArena();

    if (MainConfig.gen_tiers_custom_holo_enabled) {
      // Add custom Holo titles
      for (Spawner spawner : arena.getSpawners()) {
        if (MainConfig.gen_tiers_start_spawners.contains(spawner.getDropType())) {
          formatHoloTiles(MainConfig.gen_tiers_start_tier, spawner);
        }
      }
    }

    scheduleTier(arena, 1);
  }

  @EventHandler
  public void onGameStop(RoundEndEvent event) {
    // Kill Gen Tiers on round end
    final BukkitTask task = genTierTasks.get(event.getArena());

    if (task != null) {
      task.cancel();
      genTierTasks.remove(event.getArena());
    }
  }

  private void scheduleTier(Arena arena, int key) {
    // Check if tier exists
    if (GenTiersConfig.gen_tier_levels.get(key) == null)
      return;

    final GenTierLevel currentLevel = GenTiersConfig.gen_tier_levels.get(key);
    final int nextTierLevel = key + 1;

    // Update Placeholder
    nextTierMap.put(arena, currentLevel.getTierName());
    nextUpdateTime.put(arena, System.currentTimeMillis() + ((long) currentLevel.getTime() * 60 * 1000));

    // Kill previous task if running for some reason
    final BukkitTask task = genTierTasks.get(arena);
    if (task != null)
      task.cancel();

    switch (currentLevel.getAction()) {
      case GAME_OVER: {
        currentLevel.broadcastEarn(arena, false);
        arena.setIngameTimeRemaining((int) (currentLevel.getTime() * 60));
        break;
      }

      case BED_DESTROY: {
        genTierTasks.put(arena, Bukkit.getServer().getScheduler().runTaskLater(MBedwarsTweaksPlugin.getInstance(), () -> {
          if (arena.getStatus() == ArenaStatus.RUNNING) {
            // Break beds, start next tier
            currentLevel.broadcastEarn(arena, false);
            scheduleTier(arena, nextTierLevel);

            // Break all beds in an arena
            for (Team team : arena.getEnabledTeams()) {
              final XYZD bedLoc = arena.getBedLocation(team);
              if (!arena.isBedDestroyed(team) && bedLoc != null) {
                arena.destroyBedNaturally(team, Message.build(currentLevel.getTierName()).done());
                bedLoc.toLocation(arena.getGameWorld()).getBlock().setType(Material.AIR);
              }
            }

            // Broadcast Message
            if (MainConfig.auto_bed_break_message_enabled) {
              for (String s : MainConfig.auto_bed_break_message) {
                arena.broadcast(Message.build(s).done());
              }
            }
          }
        }, (long) currentLevel.getTime() * 20 * 60));
        break;
      }

      case GEN_UPGRADE: {
        genTierTasks.put(arena, Bukkit.getServer().getScheduler().runTaskLater(MBedwarsTweaksPlugin.getInstance(), () -> {
          if (arena.getStatus() == ArenaStatus.RUNNING) {
            currentLevel.broadcastEarn(arena, true);
            scheduleTier(arena, nextTierLevel);

            // For all spawners
            for (Spawner spawner : arena.getSpawners()) {
              if (currentLevel.getType() != null && spawner.getDropType() == currentLevel.getType()) {
                // Set drop time
                if (currentLevel.getSpeed() != null)
                  spawner.addDropDurationModifier("GEN_UPGRADE", MBedwarsTweaksPlugin.getInstance(), SpawnerDurationModifier.Operation.SET, currentLevel.getSpeed());

                // Set new limit
                if (currentLevel.getLimit() != null)
                  spawner.setMaxNearbyItems(currentLevel.getLimit());

                // Add custom Holo tiles
                if (MainConfig.gen_tiers_custom_holo_enabled)
                  formatHoloTiles(currentLevel.getTierLevel(), spawner);

              }
            }
          } else {
            nextTierMap.remove(arena);
          }
        }, (long) currentLevel.getTime() * 20 * 60));
        break;
      }
    }
  }

  // Custom format for hologram titles
  private void formatHoloTiles(String tierName, Spawner spawner) {
    final String spawnerName = spawner.getDropType().getConfigName();
    final String colorCode = spawnerName.substring(0, 2);
    final String strippedSpawnerName = ChatColor.stripColor(spawnerName);
    final List<String> formatted = new ArrayList<>();

    // Dont use placeholder, use REPLACE
    for (String string : MainConfig.gen_tiers_custom_holo_titles) {
      final String formattedString = Message.build(string)
          .placeholder("tier", tierName)
          .placeholder("spawner-color", colorCode)
          .placeholder("spawner", strippedSpawnerName)
          .done(true);

      formatted.add(formattedString);
    }

    spawner.setOverridingHologramLines(formatted.toArray(new String[0]));
  }

  public static String getNextTierName(Arena arena){
    return nextTierMap.get(arena);
  }

  // Format time for placeholder
  public static int getSecondsToNextUpdate(Arena arena) {
    return Math.max((int) (nextUpdateTime.getOrDefault(arena, 0L) - System.currentTimeMillis()) / 1000, 0);
  }
}