package me.metallicgoat.tweaksaddon.gentiers;

import de.marcely.bedwars.api.GameAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.ArenaStatus;
import de.marcely.bedwars.api.arena.Team;
import de.marcely.bedwars.api.event.arena.ArenaDeleteEvent;
import de.marcely.bedwars.api.event.arena.ArenaStatusChangeEvent;
import de.marcely.bedwars.api.event.arena.RoundStartEvent;
import de.marcely.bedwars.api.game.spawner.Spawner;
import de.marcely.bedwars.api.game.spawner.SpawnerDurationModifier;
import de.marcely.bedwars.api.game.upgrade.UpgradeLevel;
import de.marcely.bedwars.api.game.upgrade.UpgradeState;
import de.marcely.bedwars.api.game.upgrade.UpgradeTriggerHandler;
import de.marcely.bedwars.api.message.Message;
import de.marcely.bedwars.tools.location.XYZD;
import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;
import me.metallicgoat.tweaksaddon.config.GenTiersConfig;
import me.metallicgoat.tweaksaddon.config.MainConfig;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class GenTiers implements Listener {

  private static final Map<Arena, ArenaState> arenaStates = new IdentityHashMap<>();

  @EventHandler
  public void testDragon(AsyncPlayerChatEvent event){
    Bukkit.getScheduler().runTask(MBedwarsTweaksPlugin.getInstance(), () -> {
      final Player player = event.getPlayer();
      final Arena arena = GameAPI.get().getArenaByPlayer(player);

      if(arena == null)
        return;

      final Team team = arena.getPlayerTeam(player);

      final Location location = arena.getTeamSpawn(team).toLocation(arena.getGameWorld()).clone();

      location.add(0, 20, 0);

      final EnderDragon dragon = (EnderDragon) arena.getGameWorld().spawnEntity(location, EntityType.ENDER_DRAGON);
      new DragonFollowTask(dragon, arena, team).runTaskTimer(MBedwarsTweaksPlugin.getInstance(), 0, 1);
    });

  }

  @EventHandler
  public void onRoundStartEvent(RoundStartEvent event) {
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

    scheduleArena(arena, 1);
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onArenaStatusChangeEvent(ArenaStatusChangeEvent event) {
    if (event.getOldStatus() == ArenaStatus.RUNNING)
      removeArena(event.getArena());
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onArenaDeleteEvent(ArenaDeleteEvent event) {
    removeArena(event.getArena());
  }

  private void removeArena(Arena arena) {
    final ArenaState state = arenaStates.remove(arena);

    if (state != null && state.genTierTask != null)
      state.genTierTask.cancel();
  }

  private void scheduleArena(Arena arena, int tier) {
    final GenTierLevel currentLevel = GenTiersConfig.gen_tier_levels.get(tier);

    // Check if tier exists
    if (currentLevel == null)
      return;

    // Update Placeholder
    final ArenaState state = new ArenaState();

    removeArena(arena); // Cancel existing tasks
    arenaStates.put(arena, state);

    state.nextTierMap = currentLevel.getTierName();
    state.nextUpdateTime = System.currentTimeMillis() + ((long) currentLevel.getTime() * 60 * 1000);

    // Kill previous task if running for some reason
    switch (currentLevel.getAction()) {
      case GAME_OVER: {
        currentLevel.broadcastEarn(arena, false);
        arena.setIngameTimeRemaining((int) (currentLevel.getTime() * 60));
        break;
      }

      case BED_DESTROY: {
        state.genTierTask = Bukkit.getServer().getScheduler().runTaskLater(MBedwarsTweaksPlugin.getInstance(), () -> {
          // Break beds, start next tier
          currentLevel.broadcastEarn(arena, false);
          scheduleArena(arena, tier + 1);

          // Break all beds in an arena & run team upgrades
          for (Team team : arena.getEnabledTeams()) {
            final XYZD bedLoc = arena.getBedLocation(team);

            if (!arena.isBedDestroyed(team) && bedLoc != null) {
              arena.destroyBedNaturally(team, Message.build(currentLevel.getTierName()).done());
              bedLoc.toLocation(arena.getGameWorld()).getBlock().setType(Material.AIR);
            }

            // Spawn Dragon
            if (hasSuddenDeath(arena, team)) {
              final Location location = arena.getTeamSpawn(team).toLocation(arena.getGameWorld()).clone();

              location.add(0, 20, 0);

              final EnderDragon dragon = (EnderDragon) arena.getGameWorld().spawnEntity(location, EntityType.ENDER_DRAGON);
              new DragonFollowTask(dragon, arena, team).runTaskTimer(MBedwarsTweaksPlugin.getInstance(), 0, 1);
            }
          }

          // Broadcast Message
          if (MainConfig.auto_bed_break_message_enabled) {
            for (String s : MainConfig.auto_bed_break_message) {
              arena.broadcast(Message.build(s).done());
            }
          }

        }, (long) currentLevel.getTime() * 20 * 60);

        break;
      }

      case GEN_UPGRADE: {
        state.genTierTask = Bukkit.getServer().getScheduler().runTaskLater(MBedwarsTweaksPlugin.getInstance(), () -> {
          currentLevel.broadcastEarn(arena, true);
          scheduleArena(arena, tier + 1);

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
        }, (long) currentLevel.getTime() * 20 * 60);

        break;
      }
    }
  }

  private boolean hasSuddenDeath(Arena arena, Team team) {
    final UpgradeState upgradeState = arena.getUpgradeState(team);

    if (upgradeState == null)
      return false;

    for (UpgradeLevel level : upgradeState.getActiveUpgrades()) {
      final UpgradeTriggerHandler handler = level.getTriggerHandler();

      if (handler != null && handler.getId().equals("sudden-death"))
        return true;

    }

    return false;
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

  @Nullable
  public static String getNextTierName(Arena arena) {
    final ArenaState state = arenaStates.get(arena);

    if (state == null)
      return null;

    return state.nextTierMap;
  }

  // Format time for placeholder
  public static int getSecondsToNextUpdate(Arena arena) {
    final ArenaState state = arenaStates.get(arena);

    if (state == null)
      return 0;

    return (int) (state.nextUpdateTime - System.currentTimeMillis()) / 1000;
  }


  private static class ArenaState {

    String nextTierMap;
    long nextUpdateTime;
    BukkitTask genTierTask;
  }
}