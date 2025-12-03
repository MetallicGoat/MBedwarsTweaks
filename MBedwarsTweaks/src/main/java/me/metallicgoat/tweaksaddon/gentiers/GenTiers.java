package me.metallicgoat.tweaksaddon.gentiers;

import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.ArenaStatus;
import de.marcely.bedwars.api.arena.Team;
import de.marcely.bedwars.api.event.arena.ArenaDeleteEvent;
import de.marcely.bedwars.api.event.arena.ArenaStatusChangeEvent;
import de.marcely.bedwars.api.event.arena.RoundStartEvent;
import de.marcely.bedwars.api.game.spawner.Spawner;
import de.marcely.bedwars.api.message.Message;
import de.marcely.bedwars.tools.Validate;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;
import me.metallicgoat.tweaksaddon.api.events.gentiers.GenTiersScheduleEvent;
import me.metallicgoat.tweaksaddon.api.events.gentiers.GenTiersActionEvent;
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
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

public class GenTiers implements Listener {

  private static final Map<Arena, GenTierStateImpl> arenaStates = new IdentityHashMap<>();

  @EventHandler
  public void onRoundStartEvent(RoundStartEvent event) {
    if (!MainConfig.gen_tiers_enabled)
      return;

    final Arena arena = event.getArena();
    final GenTierLevel firstLevel = GenTiersConfig.gen_tier_levels.get(1);

    if (firstLevel == null)
      return;

    if (MainConfig.gen_tiers_custom_holo_enabled) {
      // Add custom Holo titles
      for (Spawner spawner : arena.getSpawners()) {
        if (MainConfig.gen_tiers_start_spawners.contains(spawner.getDropType())) {
          formatHoloTiles(MainConfig.gen_tiers_start_tier, spawner);
        }
      }
    }

    final GenTierStateImpl state = new GenTierStateImpl(arena);

    arenaStates.put(arena, state);
    scheduleNextTier(state, firstLevel);
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

  private static void removeArena(Arena arena) {
    final GenTierStateImpl state = arenaStates.remove(arena);

    if (state != null)
      cancelTask(state);
  }

  private static void cancelTask(GenTierStateImpl state) {
    if (state != null && state.genTierTask != null) {
      state.genTierTask.cancel();
      state.genTierTask = null;
    }
  }

  public static void scheduleNextTier(GenTierStateImpl state, GenTierLevel level) {
    scheduleNextTier(state, level, level.getTime());
  }

  public static void scheduleNextTier(GenTierStateImpl state, GenTierLevel level, Duration time) {
    // Cancel existing tasks
    cancelTask(state);

    // ask api
    final GenTiersScheduleEvent event = new GenTiersScheduleEvent(state, level, time);

    Bukkit.getPluginManager().callEvent(event);

    if (event.isCancelled())
      return;

    // ok apply
    level = event.getNextTier();
    time = event.getDelay();

    state.updateState(level, time);

    if (level.getHandler().getActionType() == GenTierActionType.GAME_OVER) {
      state.getArena().setIngameTimeRemaining((int) time.get(ChronoUnit.SECONDS));

    } else {
      // run periodically since TPS might not be constantly 20
      // otherwise, remaining time will print negative when server lags
      state.genTierTask = new BukkitRunnable() {
        public void run() {
          if (!state.getRemainingNextTier().isZero())
            return;

          cancel();
          state.genTierTask = null;

          runTier(state, event.getNextTier());
        }
      }.runTaskTimer(MBedwarsTweaksPlugin.getInstance(), 40, 40);
    }
  }

  private static void runTier(GenTierStateImpl state, GenTierLevel level) {
    state.currentTier = level;

    final GenTiersActionEvent event = new GenTiersActionEvent(state, level,true, true);

    Bukkit.getPluginManager().callEvent(event);

    if (event.isBroadcastingEarn())
      level.broadcastEarn(state.getArena());

    if (event.isExecutingHandlers())
      level.getHandler().run(level, state.getArena());

    // schedule next
    final GenTierLevel nextLevel = level.getNextLevel();

    if (nextLevel != null)
      scheduleNextTier(state, nextLevel);
    else
      state.updateState(null, null);
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



  @RequiredArgsConstructor
  public static class GenTierStateImpl implements GenTierState {

    @Getter
    private final Arena arena;
    @Getter
    private GenTierLevel nextTier, currentTier;
    @Getter
    private Instant nextTierTime;
    private final Set<Team> dragonTeams = EnumSet.noneOf(Team.class);
    private BukkitTask genTierTask = null;

    @Override
    public boolean isValid() {
      return GenTiers.getState(this.arena) == this;
    }

    @Override
    public boolean hasBoughtDragon(Team team) {
      Validate.notNull(team, "team");

      return this.dragonTeams.contains(team);
    }

    @Override
    public void setDragonBought(Team team, boolean state) {
      Validate.notNull(team, "team");

      if (state)
        this.dragonTeams.add(team);
      else
        this.dragonTeams.remove(team);
    }

    @Override
    public Duration getRemainingNextTier() {
      if (this.nextTierTime == null)
        return null;

      final Duration diff = Duration.between(Instant.now(), this.nextTierTime);

      if (diff.isNegative()) // avoid negative numbers because scheduler didn't catch up yet
        return Duration.ZERO;

      return diff;
    }

    @Override
    public void setRemainingNextTier(Duration duration) {
      Validate.notNull(duration, "duration");
      Validate.isTrue(this.nextTierTime != null, "There is no next gen tier level to set the remaining time for");

      this.nextTierTime = Instant.now().plus(duration);
    }

    @Override
    public void scheduleNextTier(GenTierLevel level) {
      Validate.notNull(level, "level");

      GenTiers.scheduleNextTier(this, level);
    }

    @Override
    public void scheduleNextTier(GenTierLevel level, Duration time) {
      Validate.notNull(level, "level");
      Validate.notNull(time, "time");

      GenTiers.scheduleNextTier(this, level, time);
    }

    @Override
    public void cancelTiers() {
      GenTiers.cancelTask(this);
      this.updateState(null, null);
    }

    public String getNextTierName() {
      return this.nextTier != null ? this.nextTier.getTierName() : "";
    }

    private void updateState(@Nullable GenTierLevel newNextLevel, @Nullable Duration time) {
      this.nextTier = newNextLevel;

      if (newNextLevel != null)
        this.nextTierTime = Instant.now().plus(time);
      else
        this.nextTierTime = null;
    }
  }
}