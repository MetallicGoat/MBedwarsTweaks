package me.metallicgoat.tweaksaddon.tweaks.cosmetic;

import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.Team;
import de.marcely.bedwars.api.event.arena.RoundStartEvent;
import me.metallicgoat.tweaksaddon.config.MainConfig;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CustomTeamColors implements Listener {

  //TODO Do I need to do this every time the game starts?
  @EventHandler
  public void onRoundStart(RoundStartEvent event) {
    final Arena arena = event.getArena();

    if (!MainConfig.custom_team_colors_enabled || MainConfig.custom_team_colors.isEmpty())
      return;

    for (Team team : arena.getEnabledTeams()) {

      if (MainConfig.custom_team_colors.containsKey(team)) {
        team.setBungeeChatColor(MainConfig.custom_team_colors.get(team));
      }
    }
  }
}
