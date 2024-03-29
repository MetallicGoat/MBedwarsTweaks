package me.metallicgoat.tweaksaddon.gentiers.handlers;

import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.BedDestructionInfo;
import de.marcely.bedwars.api.arena.Team;
import de.marcely.bedwars.api.message.Message;
import me.metallicgoat.tweaksaddon.config.MainConfig;
import me.metallicgoat.tweaksaddon.gentiers.GenTierLevel;

public class BedDestroyHandler extends GenTierHandler {

  @Override
  public void run(GenTierLevel level, Arena arena) {
    // Break all beds in an arena & run team upgrades
    for (Team team : arena.getEnabledTeams()) {
      final BedDestructionInfo info = BedDestructionInfo.construct(team);

      info.setDestroyerName(Message.build(level.getTierName()).done());

      arena.destroyBedNaturally(info);
    }

    // Broadcast Message
    if (!MainConfig.auto_bed_break_message_enabled)
      return;
      
    for (String s : MainConfig.auto_bed_break_message)
      arena.broadcast(Message.build(s));
  }
}
