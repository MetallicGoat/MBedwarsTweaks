package me.metallicgoat.tweaksaddon.tweaks.cosmetic;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.GameAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.ArenaStatus;
import de.marcely.bedwars.api.arena.Team;
import de.marcely.bedwars.api.event.arena.RoundEndEvent;
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


public class ActionBar implements Listener {

  private static BukkitTask actionBarTask = null;

  private static BukkitTask startUpdatingTime() {

    return Bukkit.getServer().getScheduler().runTaskTimer(MBedwarsTweaksPlugin.getInstance(), () -> {
      for (Arena arena : BedwarsAPI.getGameAPI().getArenas()) {

        if ((arena.getStatus() == ArenaStatus.RUNNING && MainConfig.custom_action_bar_in_game) ||
            (arena.getStatus() == ArenaStatus.LOBBY && MainConfig.custom_action_bar_in_lobby)) {

          for (Player player : arena.getPlayers())
            BedwarsAPI.getNMSHelper().showActionbar(player, Message.build(Helper.get().replacePAPIPlaceholders(MainConfig.custom_action_bar_message, player)).done());

        }
      }
    }, 0L, 20L);
  }

  @EventHandler
  public void onGameStart(PlayerJoinArenaEvent e) {
    final boolean enabled = (MainConfig.custom_action_bar_in_game || MainConfig.custom_action_bar_in_lobby);

    if (!enabled || actionBarTask != null)
      return;

    //Start updating ActionBar
    actionBarTask = startUpdatingTime();
  }

  @EventHandler
  public void onGameStop(RoundEndEvent event) {
    final boolean enabled = (MainConfig.custom_action_bar_in_game || MainConfig.custom_action_bar_in_lobby);

    if (!enabled)
      return;

    //Dont kill task if players are playing
    for (Arena arena : BedwarsAPI.getGameAPI().getArenas()) {
      if (!arena.getPlayers().isEmpty()) {
        return;
      }
    }

    //Kill task
    if (actionBarTask != null) {
      actionBarTask.cancel();
      actionBarTask = null;
    }
  }
}
