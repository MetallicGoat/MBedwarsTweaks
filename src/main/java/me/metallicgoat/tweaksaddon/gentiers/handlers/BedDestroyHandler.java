package me.metallicgoat.tweaksaddon.gentiers.handlers;

import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.Team;
import de.marcely.bedwars.api.message.Message;
import de.marcely.bedwars.tools.location.XYZD;
import me.metallicgoat.tweaksaddon.config.MainConfig;
import me.metallicgoat.tweaksaddon.gentiers.GenTierLevel;
import org.bukkit.Material;

public class BedDestroyHandler extends GenTierHandler{
  @Override
  public void run(GenTierLevel level, Arena arena) {
    // Break all beds in an arena & run team upgrades
    for (Team team : arena.getEnabledTeams()) {
      final XYZD bedLoc = arena.getBedLocation(team);

      if (!arena.isBedDestroyed(team) && bedLoc != null) {
        arena.destroyBedNaturally(team, Message.build(level.getPapiName()).done());
        bedLoc.toLocation(arena.getGameWorld()).getBlock().setType(Material.AIR);
      }
    }

    // Broadcast Message
    if (!MainConfig.auto_bed_break_message_enabled)
      return;
      
    for (String s : MainConfig.auto_bed_break_message)
      arena.broadcast(Message.build(s).done());
  }
}
