package me.metallicgoat.tweaksaddon.tweaks.playerlimitbypass;

import me.metallicgoat.tweaksaddon.config.MainConfig;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class PlayerLimitBypass implements Listener {

  @EventHandler
  public void onPlayerLoginEvent(PlayerLoginEvent event) {
    if (MainConfig.player_limit_bypass && event.getResult() == PlayerLoginEvent.Result.KICK_FULL) {
      event.allow();
    }
  }
}
