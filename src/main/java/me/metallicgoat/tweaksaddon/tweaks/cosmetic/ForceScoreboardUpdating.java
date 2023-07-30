package me.metallicgoat.tweaksaddon.tweaks.cosmetic;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.ArenaStatus;
import de.marcely.bedwars.api.event.arena.RoundEndEvent;
import de.marcely.bedwars.api.event.player.PlayerJoinArenaEvent;
import de.marcely.bedwars.api.event.player.PlayerQuitArenaEvent;
import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;
import me.metallicgoat.tweaksaddon.config.MainConfig;
import me.metallicgoat.tweaksaddon.tweaks.spawners.GenTiers;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

public class ForceScoreboardUpdating implements Listener {

  private static BukkitTask scoreboardUpdatingTask = null;

  public static void checkIfUsed() {
    final boolean enabled = (MainConfig.scoreboard_updating_enabled_in_game || MainConfig.scoreboard_updating_enabled_in_lobby);

    if (!enabled)
      return;

    // Dont kill task if players are playing
    for (Arena arena : BedwarsAPI.getGameAPI().getArenas()) {
      if (!arena.getPlayers().isEmpty()) {
        return;
      }
    }

    // Kill task
    if (scoreboardUpdatingTask != null) {
      scoreboardUpdatingTask.cancel();
      scoreboardUpdatingTask = null;
    }
  }

  private static BukkitTask startUpdatingTime() {
    return Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(MBedwarsTweaksPlugin.getInstance(), () -> {
      for (Arena arena : BedwarsAPI.getGameAPI().getArenas()) {
        if ((arena.getStatus() == ArenaStatus.RUNNING && MainConfig.scoreboard_updating_enabled_in_game)) {
          final int integer = arena.getIngameTimeRemaining();

          if (integer % MainConfig.scoreboard_updating_interval == 0)
            arena.updateScoreboard();

        } else if (arena.getStatus() == ArenaStatus.LOBBY && MainConfig.scoreboard_updating_enabled_in_lobby) {
          final long integer = Math.round(arena.getLobbyTimeRemaining());

          if (integer % MainConfig.scoreboard_updating_interval == 0)
            arena.updateScoreboard();
        }
      }
    }, 0L, 20L);
  }

  @EventHandler
  public void onGameStart(PlayerJoinArenaEvent event) {
    final boolean enabled = (MainConfig.scoreboard_updating_enabled_in_game || MainConfig.scoreboard_updating_enabled_in_lobby);

    if (!enabled || scoreboardUpdatingTask != null)
      return;

    // Start updating scoreboard
    scoreboardUpdatingTask = startUpdatingTime();
  }

  // If someone leaves during lobby
  @EventHandler
  public void onPlayerLeave(PlayerQuitArenaEvent event) {
    checkIfUsed();
  }

  @EventHandler
  public void onGameEnd(RoundEndEvent event) {
    checkIfUsed();
  }
}
