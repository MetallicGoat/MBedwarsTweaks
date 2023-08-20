package me.metallicgoat.tweaksaddon.tweaks.cosmetic;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.ArenaStatus;
import de.marcely.bedwars.api.event.player.PlayerJoinArenaEvent;
import de.marcely.bedwars.api.event.player.PlayerQuitArenaEvent;
import de.marcely.bedwars.api.message.Message;
import de.marcely.bedwars.tools.Helper;
import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;
import me.metallicgoat.tweaksaddon.config.MainConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

public class PlaceholderUpdating implements Listener {

  private BukkitTask asyncUpdatingTask = null;

  // TODO: NOTE: Updates every tick
  private void startUpdatingTime() {
    asyncUpdatingTask = Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(MBedwarsTweaksPlugin.getInstance(), () -> {
      for (Arena arena : BedwarsAPI.getGameAPI().getArenas()) {

        if ((arena.getStatus() == ArenaStatus.RUNNING && MainConfig.scoreboard_updating_enabled_in_game))
          arena.updateScoreboard();
        else if (arena.getStatus() == ArenaStatus.LOBBY && MainConfig.scoreboard_updating_enabled_in_lobby)
          arena.updateScoreboard();

        if ((arena.getStatus() == ArenaStatus.RUNNING && MainConfig.custom_action_bar_in_game) ||
            (arena.getStatus() == ArenaStatus.LOBBY && MainConfig.custom_action_bar_in_lobby)) {

          for (Player player : arena.getPlayers())
            BedwarsAPI.getNMSHelper().showActionbar(player, Message.build(Helper.get().replacePAPIPlaceholders(MainConfig.custom_action_bar_message, player)).done());
        }
      }
    }, 0L, 20L);
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinArenaEvent event) {
    if (asyncUpdatingTask == null)
      startUpdatingTime();
  }

  @EventHandler
  public void onPlayerLeave(PlayerQuitArenaEvent event) {
    if (notUsed() && asyncUpdatingTask != null){
      asyncUpdatingTask.cancel();
      asyncUpdatingTask = null;
    }
  }

  private boolean notUsed() {
    // Dont kill task if players are playing
    for (Arena arena : BedwarsAPI.getGameAPI().getArenas())
      if (!arena.getPlayers().isEmpty())
        return false;

    // Kill task
    return true;
  }
}
