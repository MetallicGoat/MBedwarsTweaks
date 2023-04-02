package me.metallicgoat.tweaksaddon.tweaks.spawners;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.ArenaStatus;
import de.marcely.bedwars.api.event.arena.RoundEndEvent;
import de.marcely.bedwars.api.event.arena.RoundStartEvent;
import de.marcely.bedwars.api.game.spawner.Spawner;
import de.marcely.bedwars.api.game.spawner.SpawnerDurationModifier;
import de.marcely.bedwars.api.message.Message;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import me.metallicgoat.tweaksaddon.DependType;
import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;
import me.metallicgoat.tweaksaddon.Util;
import me.metallicgoat.tweaksaddon.config.GenTiersConfig;
import me.metallicgoat.tweaksaddon.config.MainConfig;
import me.metallicgoat.tweaksaddon.DependManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

public class GenTiers implements Listener {

  public static HashMap<Arena, String> nextTierMap = new HashMap<>();
  public static HashMap<Arena, Long> timeToNextUpdate = new HashMap<>();
  private final HashMap<Arena, BukkitTask> tasksToKill = new HashMap<>();
  private BukkitTask placeHolderTask = null;

  // Format time for placeholder
  public static String[] timeLeft(Arena arena) {
    final int timeoutSeconds = (timeToNextUpdate.getOrDefault(arena, 0L).intValue() / 20);
    final int minutes = (timeoutSeconds / 60) % 60;
    final int seconds = timeoutSeconds % 60;

    return Util.formatMinSec(minutes, seconds);
  }

  @EventHandler
  public void onGameStart(RoundStartEvent event) {
    if (!MainConfig.gen_tiers_enabled)
      return;

    // Start updating placeholders
    startUpdatingTime();

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
    final BukkitTask task = tasksToKill.get(event.getArena());

    if (task != null) {
      task.cancel();
      tasksToKill.remove(event.getArena());
    }

    // Dont kill task if
    for (Arena arena : BedwarsAPI.getGameAPI().getArenas()) {
      if (arena.getStatus() == ArenaStatus.RUNNING) {
        return;
      }
    }

    // Kill task
    if (placeHolderTask != null) {
      placeHolderTask.cancel();
      placeHolderTask = null;
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
    timeToNextUpdate.put(arena, currentLevel.getTime() * 20 * 60);

    // Kill previous task if running for some reason
    BukkitTask task = tasksToKill.get(arena);
    if (task != null)
      task.cancel();

    switch (currentLevel.getAction()) {
      case GAME_OVER: {
        playTierSound(arena, currentLevel);
        arena.setIngameTimeRemaining((int) (currentLevel.getTime() * 60));
        break;
      }

      case BED_DESTROY: {
        tasksToKill.put(arena, Bukkit.getServer().getScheduler().runTaskLater(MBedwarsTweaksPlugin.getInstance(), () -> {
          if (arena.getStatus() == ArenaStatus.RUNNING) {
            // Break beds, start next tier
            playTierSound(arena, currentLevel);
            scheduleTier(arena, nextTierLevel);
            BedBreakTier.breakArenaBeds(arena, currentLevel.getTierName());
          }
        }, currentLevel.getTime() * 20 * 60));
        break;
      }

      case GEN_UPGRADE: {
        tasksToKill.put(arena, Bukkit.getServer().getScheduler().runTaskLater(MBedwarsTweaksPlugin.getInstance(), () -> {
          if (arena.getStatus() == ArenaStatus.RUNNING) {

            scheduleTier(arena, nextTierLevel);
            arena.broadcast(Message.build(currentLevel.getEarnMessage()));
            playTierSound(arena, currentLevel);

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
        }, currentLevel.getTime() * 20 * 60));
        break;
      }
    }
  }

  // Custom format for hologram titles
  public void formatHoloTiles(String tierName, Spawner spawner) {
    final String spawnerName = spawner.getDropType().getConfigName();
    final String colorCode = spawnerName.substring(0, 2);
    final String strippedSpawnerName = ChatColor.stripColor(spawnerName);
    final List<String> formatted = new ArrayList<>();

    // Dont use placeholder, use REPLACE
    for (String string : MainConfig.gen_tiers_spawner_holo_titles) {
      final String formattedString = string
          .replace("{tier}", tierName)
          .replace("{spawner-color}", colorCode)
          .replace("{spawner}", strippedSpawnerName);

      formatted.add(ChatColor.translateAlternateColorCodes('&', formattedString));
    }

    spawner.setOverridingHologramLines(formatted.toArray(new String[0]));
  }

  // TODO improve (why is this a part of gen-tier?) (shit code)
  private void startUpdatingTime() {
    if (!DependManager.isPresent(DependType.PLACEHOLDER_API) || placeHolderTask != null)
      return;

    placeHolderTask = Bukkit.getServer().getScheduler().runTaskTimer(MBedwarsTweaksPlugin.getInstance(), () -> {
      if (timeToNextUpdate.isEmpty())
        return;

      timeToNextUpdate.forEach((arena, integer) -> {
        if (arena.getStatus() == ArenaStatus.RUNNING)
          timeToNextUpdate.replace(arena, integer, integer - 20);

      });
    }, 0L, 20L);
  }

  private void playTierSound(Arena arena, GenTierLevel level) {
    final Sound sound = level.getEarnSound();

    if (sound == null || arena == null)
      return;

    for (Player p : arena.getPlayers())
      p.playSound(p.getLocation(), sound, 1F, 1F);
  }
}