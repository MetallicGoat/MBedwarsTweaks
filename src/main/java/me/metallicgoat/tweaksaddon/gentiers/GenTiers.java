package me.metallicgoat.tweaksaddon.gentiers;

import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.ArenaStatus;
import de.marcely.bedwars.api.arena.Team;
import de.marcely.bedwars.api.event.arena.ArenaDeleteEvent;
import de.marcely.bedwars.api.event.arena.ArenaStatusChangeEvent;
import de.marcely.bedwars.api.event.arena.RoundStartEvent;
import de.marcely.bedwars.api.game.spawner.Spawner;
import de.marcely.bedwars.api.message.Message;
import lombok.Getter;
import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;
import me.metallicgoat.tweaksaddon.config.GenTiersConfig;
import me.metallicgoat.tweaksaddon.config.MainConfig;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class GenTiers implements Listener {

  private static final Map<Arena, GenTierState> arenaStates = new IdentityHashMap<>();

  public static GenTierState getState(Arena arena) {
    final GenTierState state = arenaStates.get(arena);

    if (state == null)
      throw new RuntimeException("Failed to schedule next Gen Tier. This is a bug!");

    return state;
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

    arenaStates.put(arena, new GenTierState());
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
    final GenTierState state = arenaStates.remove(arena);

    cancelTask(state);
  }

  private void cancelTask(GenTierState state) {
    if (state != null && state.genTierTask != null)
      state.genTierTask.cancel();
  }

  private void scheduleArena(Arena arena, int tier) {
    final GenTierLevel currentLevel = GenTiersConfig.gen_tier_levels.get(tier);

    // Check if tier exists
    if (currentLevel == null)
      return;

    final GenTierState state = getState(arena);

    state.nextTierName = currentLevel.getTierName();
    state.nextUpdateTime = System.currentTimeMillis() + (long) (currentLevel.getTime() * 60 * 1000);

    cancelTask(state); // Cancel existing tasks

    if (currentLevel.getAction() == TierAction.GAME_OVER) {
      currentLevel.broadcastEarn(arena, false);
      currentLevel.getAction().getHandler().run(currentLevel, arena); // Currently does nothing

    } else {
      state.genTierTask = Bukkit.getServer().getScheduler().runTaskLater(MBedwarsTweaksPlugin.getInstance(), () -> {

        currentLevel.broadcastEarn(arena, true);
        currentLevel.getAction().getHandler().run(currentLevel, arena);

        scheduleArena(arena, tier + 1);

      }, (long) (currentLevel.getTime() * 20 * 60));
    }
  }

  // Custom format for hologram titles
  public static void formatHoloTiles(String tierName, Spawner spawner) {
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

  public static class GenTierState {
    @Getter
    private String nextTierName = null;
    private long nextUpdateTime = 0;
    private final List<Team> dragonTeams = new ArrayList<>();
    private BukkitTask genTierTask = null;

    public boolean hasDragon(Team team) {
      return dragonTeams.contains(team);
    }

    public void addDragonTeam(Team team) {
      dragonTeams.add(team);
    }

    public int getSecondsToNextTier() {
      return (int) (nextUpdateTime - System.currentTimeMillis()) / 1000;
    }
  }
}