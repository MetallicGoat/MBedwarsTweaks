package me.metallicgoat.tweaksaddon.tweaks.messages;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.Team;
import de.marcely.bedwars.api.event.arena.ArenaBedBreakEvent;
import de.marcely.bedwars.api.message.Message;
import me.metallicgoat.tweaksaddon.config.MainConfig;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CustomBedBreakMessage implements Listener {

  @EventHandler
  public void onBedBreak(ArenaBedBreakEvent event) {
    if (!MainConfig.custom_bed_break_message_enabled)
      return;

    final Arena arena = event.getArena();
    final Team team = event.getTeam();
    final Player destroyer = event.getPlayer();

    event.setPlayingSound(true);

    // Send our overriding custom title
    if (MainConfig.bed_destroy_title_enabled) {
      event.setSendingTitle(false);

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

    final String teamName = team.getDisplayName();
    final String playerName = BedwarsAPI.getHelper().getPlayerDisplayName(destroyer);
    final String destroyerColor = arena.getPlayerTeam(destroyer) != null ? String.valueOf(arena.getPlayerTeam(destroyer).getBungeeChatColor()) : "";
    final String destroyerTeam = arena.getPlayerTeam(destroyer) != null ? ChatColor.stripColor(arena.getPlayerTeam(destroyer).getDisplayName()) : "";

    final Message message = Message.build(MainConfig.custom_bed_break_message)
        .placeholder("team-name", teamName)
        .placeholder("team-color", String.valueOf(team.getBungeeChatColor()))
        .placeholder("destroyer-name", playerName)
        .placeholder("destroyer-team-name", destroyerTeam)
        .placeholder("destroyer-color", destroyerColor);

    event.setChatMessage(message);
  }
}
