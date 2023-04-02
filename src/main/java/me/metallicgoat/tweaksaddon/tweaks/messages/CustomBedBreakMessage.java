package me.metallicgoat.tweaksaddon.tweaks.messages;

import de.marcely.bedwars.api.event.arena.ArenaBedBreakEvent;
import me.metallicgoat.tweaksaddon.config.MainConfig;
import me.metallicgoat.tweaksaddon.tweaks.spawners.BedBreakTier;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CustomBedBreakMessage implements Listener {

  @EventHandler
  public void onBedBreak(ArenaBedBreakEvent event) {
    if (!MainConfig.custom_bed_break_message_enabled)
      return;

    event.setBroadcasted(false);
    event.setPlayingSound(true);
    BedBreakTier.sendBedBreakMessage(event.getArena(), event.getTeam(), event.getPlayer());
  }
}
