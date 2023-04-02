package me.metallicgoat.tweaksaddon.tweaks.spawners;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.Team;
import de.marcely.bedwars.api.message.Message;
import de.marcely.bedwars.tools.location.XYZD;
import me.metallicgoat.tweaksaddon.config.MainConfig;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class BedBreakTier implements Listener {

  // Sets all beds to be destroyed at a specified time
  public static void breakArenaBeds(Arena arena, String tierName) {
    // Break all beds in an arena
    for (Team team : arena.getEnabledTeams()) {
      final XYZD bedLoc = arena.getBedLocation(team);
      if (!arena.isBedDestroyed(team) && bedLoc != null) {
        arena.destroyBedNaturally(team, Message.build(tierName).done(), !MainConfig.custom_bed_break_message_enabled);
        bedLoc.toLocation(arena.getGameWorld()).getBlock().setType(Material.AIR);
        if (MainConfig.custom_bed_break_message_enabled)
          sendBedBreakMessage(arena, team, null);
      }
    }
    // Broadcast Message
    if (MainConfig.auto_bed_break_message_enabled) {
      for (String s : MainConfig.auto_bed_break_message) {
        arena.broadcast(Message.build(s).done());
      }
    }
  }

  // Message that gets sent when a bed gets destroyed
  public static void sendBedBreakMessage(Arena arena, Team team, Player destroyer) {

    // Send title to victim team
    if (MainConfig.bed_destroy_title_enabled) {
      for (Player p : arena.getPlayersInTeam(team)) {
        BedwarsAPI.getNMSHelper().showTitle(p,
            Message.build(MainConfig.bed_destroy_title).done(),
            Message.build(MainConfig.bed_destroy_subtitle).done(),
            60, 15, 15);
      }
    }

    // Send public message
    if (destroyer == null)
      return;

    // Send public message
    for (String message : MainConfig.custom_bed_break_message) {
      final String teamName = team.getDisplayName();
      final String playerName = BedwarsAPI.getHelper().getPlayerDisplayName(destroyer);
      final String destroyerColor = arena.getPlayerTeam(destroyer) != null ? String.valueOf(arena.getPlayerTeam(destroyer).getBungeeChatColor()) : "";
      final String destroyerTeam = arena.getPlayerTeam(destroyer) != null ? ChatColor.stripColor(arena.getPlayerTeam(destroyer).getDisplayName()) : "";

      final String messageFormatted = Message.build(message)
          .placeholder("team-name", teamName)
          .placeholder("team-color", String.valueOf(team.getBungeeChatColor()))
          .placeholder("destroyer-name", playerName)
          .placeholder("destroyer-team-name", destroyerTeam)
          .placeholder("destroyer-color", destroyerColor)
          .done();
      arena.broadcast(messageFormatted);
    }
  }
}
