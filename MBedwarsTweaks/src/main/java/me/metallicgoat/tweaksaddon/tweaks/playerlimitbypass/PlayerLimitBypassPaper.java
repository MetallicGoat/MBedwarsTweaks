package me.metallicgoat.tweaksaddon.tweaks.playerlimitbypass;

import io.papermc.paper.event.player.PlayerServerFullCheckEvent;
import me.metallicgoat.tweaksaddon.config.MainConfig;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerLimitBypassPaper implements Listener {

  @EventHandler
  public void onServerFullCheck(PlayerServerFullCheckEvent event) {
    if (MainConfig.player_limit_bypass) {
      event.allow(true);
    }
  }
}
