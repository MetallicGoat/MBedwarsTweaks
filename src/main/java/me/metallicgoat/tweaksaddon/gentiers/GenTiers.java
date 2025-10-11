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
import me.metallicgoat.tweaksaddon.api.events.gentiers.GenTiersInitiateEvent;
import me.metallicgoat.tweaksaddon.api.events.gentiers.GenTiersPreformActionEvent;
import me.metallicgoat.tweaksaddon.api.gentiers.GenTierActionType;
import me.metallicgoat.tweaksaddon.api.gentiers.GenTierLevel;
import me.metallicgoat.tweaksaddon.api.gentiers.GenTierState;
import me.metallicgoat.tweaksaddon.config.GenTiersConfig;
import me.metallicgoat.tweaksaddon.config.MainConfig;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class GenTiers implements Listener {

  private static final Map<Arena, GenTierStateImpl> arenaStates = new IdentityHashMap<>();

  @EventHandler
  public void onRoundStartEvent(RoundStartEvent event) {
    if (!MainConfig.gen_tiers_enabled)
      return;

    final Arena arena = event.getArena();

    final GenTiersInitiateEvent initiateEvent = new GenTiersInitiateEvent(arena, 1);

    Bukkit.getPluginManager().callEvent(initiateEvent);

    if (initiateEvent.isCancelled())
      return;

    if (MainConfig.gen_tiers_custom_holo_enabled) {
      // Add custom Holo titles
      for (Spawner spawner : arena.getSpawners()) {
        if (MainConfig.gen_tiers_start_spawners.contains(spawner.getDropType())) {
          formatHoloTiles(MainConfig.gen_tiers_start_tier, spawner);
        }
      }
    }

    arenaStates.put(arena, new GenTierStateImpl());
    scheduleNextTier(arena, initiateEvent.getInitialTierLevel());
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

  public static @Nullable GenTierStateImpl getState(Arena arena) {
    return arenaStates.get(arena);
  }

  // Accessed by PrivateGamesAddon
  public static void removeArena(Arena arena) {
    final GenTierStateImpl state = arenaStates.remove(arena);

    if (state != null)
      cancelTask(state);
  }

  private static void cancelTask(GenTierStateImpl state) {
    if (state != null && state.genTierTask != null)
      state.genTierTask.cancel();
  }

  // Accessed by PrivateGamesAddon
  public static void scheduleNextTier(Arena arena, GenTierLevel level, double time) {
    final GenTierStateImpl state = getState(arena);

    if (state == null)
      return;

    state.updateState(level, time);

    cancelTask(state); // Cancel existing tasks

    if (level.getHandler().getActionType() == GenTierActionType.GAME_OVER) {
      arena.setIngameTimeRemaining((int) (time * 60));

    } else {
      state.genTierTask = Bukkit.getServer().getScheduler().runTaskLater(MBedwarsTweaksPlugin.getInstance(), () -> {
        final GenTiersPreformActionEvent event = new GenTiersPreformActionEvent(arena, true, true);

        Bukkit.getPluginManager().callEvent(event);

        if (event.isBroadcastingEarn())
          level.broadcastEarn(arena);

        if (event.isExecutingHandlers())
          level.getHandler().run(level, arena);

        scheduleNextTier(arena, level.getTier() + 1);

      }, (long) (time * 20 * 60));
    }
  }

  public static void scheduleNextTier(Arena arena, int tier) {
    final GenTierLevel level = GenTiersConfig.gen_tier_levels.get(tier);

    if (level == null)
      return;

    scheduleNextTier(arena, level, level.getTime());
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

  public static class GenTierStateImpl implements GenTierState  {

    @Getter
    private GenTierLevel nextGenTierLevel = null;
    @Getter
    private GenTierLevel currentGenTierLevel = null;
    private long nextUpdateTime = 0;
    private final List<Team> dragonTeams = new ArrayList<>();
    private BukkitTask genTierTask = null;

    public boolean hasDragon(Team team) {
      return this.dragonTeams.contains(team);
    }

    public String getNextTierName() {
      return this.nextGenTierLevel != null ? this.nextGenTierLevel.getTierName() : "";
    }

    public void addDragonTeam(Team team) {
      this.dragonTeams.add(team);
    }

    public int getSecondsToNextTier() {
      return (int) (this.nextUpdateTime - System.currentTimeMillis()) / 1000;
    }

    public long getNextTierTime() {
      return this.nextUpdateTime;
    }

    private void updateState(GenTierLevel newNextLevel, double seconds) {
      this.currentGenTierLevel = this.nextGenTierLevel;
      this.nextGenTierLevel = newNextLevel;
      this.nextUpdateTime = System.currentTimeMillis() + (long) (seconds * 60 * 1000);
    }
  }
}