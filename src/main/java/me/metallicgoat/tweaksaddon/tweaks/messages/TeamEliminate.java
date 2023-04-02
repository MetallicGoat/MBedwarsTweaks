package me.metallicgoat.tweaksaddon.tweaks.messages;

import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.Team;
import de.marcely.bedwars.api.event.arena.TeamEliminateEvent;
import de.marcely.bedwars.api.message.Message;
import me.metallicgoat.tweaksaddon.config.MainConfig;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class TeamEliminate implements Listener {

  @EventHandler
  public void onEliminate(TeamEliminateEvent event) {
    if (!MainConfig.team_eliminate_message_enabled)
      return;

    final Arena arena = event.getArena();
    final Team team = event.getTeam();
    final String teamName = ChatColor.stripColor(team.getDisplayName());
    final String teamColor = String.valueOf(team.getBungeeChatColor());

    for (String message : MainConfig.team_eliminate_message) {
      final String messageFormatted = Message.build(message)
          .placeholder("team-name", teamName)
          .placeholder("team-color", teamColor)
          .done();

      arena.broadcast(messageFormatted);
    }
  }
}
