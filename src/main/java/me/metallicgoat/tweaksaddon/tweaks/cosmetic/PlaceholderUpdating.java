package me.metallicgoat.tweaksaddon.tweaks.cosmetic;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.ArenaStatus;
import de.marcely.bedwars.api.event.arena.ArenaDeleteEvent;
import de.marcely.bedwars.api.event.arena.ArenaStatusChangeEvent;
import de.marcely.bedwars.api.event.player.PlayerJoinArenaEvent;
import de.marcely.bedwars.api.event.player.PlayerQuitArenaEvent;
import de.marcely.bedwars.api.message.Message;
import de.marcely.bedwars.tools.Helper;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;
import me.metallicgoat.tweaksaddon.config.MainConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

public class PlaceholderUpdating implements Listener {

  private final Map<Arena, BukkitTask> tickingArenas = new ConcurrentHashMap<>();

  private BukkitTask createScheduler(Arena arena) {
    long delay = 0;

    switch (arena.getStatus()) {
      case LOBBY:
      case END_LOBBY:
        delay = (int) Math.max(0, (arena.getLobbyTimeRemaining()%1)*20D);
        break;
      case RUNNING:
        delay = arena.getRunningTime().toMillis()/50;
        break;
    }

    return Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(MBedwarsTweaksPlugin.getInstance(), () -> {
        if (arena.getStatus() == ArenaStatus.RUNNING && MainConfig.scoreboard_updating_enabled_in_game)
          arena.updateScoreboard();
        else if (arena.getStatus() == ArenaStatus.LOBBY && MainConfig.scoreboard_updating_enabled_in_lobby)
          arena.updateScoreboard();

        if ((arena.getStatus() == ArenaStatus.RUNNING && MainConfig.custom_action_bar_in_game) ||
            (arena.getStatus() == ArenaStatus.LOBBY && MainConfig.custom_action_bar_in_lobby)) {

          for (Player player : arena.getPlayers())
            BedwarsAPI.getNMSHelper().showActionbar(player, Message.build(Helper.get().replacePAPIPlaceholders(MainConfig.custom_action_bar_message, player)).done());
        }
    }, delay, 20L);
  }

  private void startScheduler(Arena arena) {
    if (!arena.exists())
      return;

    this.tickingArenas.computeIfAbsent(arena, this::createScheduler);
  }

  private void stopScheduler(Arena arena) {
    final BukkitTask task = this.tickingArenas.remove(arena);

    if (task != null)
      task.cancel();
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinArenaEvent event) {
    final Arena arena = event.getArena();

    if (arena.getPlayers().size() >= 2) // already added
      return;

    startScheduler(arena);
  }

  @EventHandler
  public void onPlayerLeave(PlayerQuitArenaEvent event) {
    final Arena arena = event.getArena();

    if (!arena.getPlayers().isEmpty()) // not empty yet
      return;

    stopScheduler(arena);
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onArenaStatusChange(ArenaStatusChangeEvent event) {
    // resync scheduler
    final Arena arena = event.getArena();
    final ArenaStatus newStatus = event.getNewStatus();

    if (newStatus != ArenaStatus.RUNNING && newStatus != ArenaStatus.END_LOBBY)
      return;

    Bukkit.getScheduler().runTaskLaterAsynchronously(MBedwarsTweaksPlugin.getInstance(), () -> {
      stopScheduler(arena);
      startScheduler(arena);
    }, 1);
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onArenaDeleteEvent(ArenaDeleteEvent event) {
    stopScheduler(event.getArena());
  }
}
