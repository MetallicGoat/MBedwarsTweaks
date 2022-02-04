package me.metallicgoat.tweaks.tweaks.misc;

import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.Team;
import de.marcely.bedwars.api.event.arena.RoundStartEvent;
import de.marcely.bedwars.tools.Helper;
import me.metallicgoat.tweaks.utils.ServerManager;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class CustomTeamColors implements Listener {

    //TODO Do I need to do this every time the game starts?

    @EventHandler
    public void onRoundStart(RoundStartEvent e){
        final Arena arena = e.getArena();
        final boolean enabled = ServerManager.getConfig().getBoolean("Custom-Team-Chat-Color.Enabled");
        final List<String> teamColorList = ServerManager.getConfig().getStringList("Custom-Team-Chat-Color.Teams");

        if(!enabled || teamColorList.isEmpty())
            return;

        for(Team team : arena.getEnabledTeams()){
            for(String teamColorString : teamColorList){
                if(teamColorString.contains(":")){
                    final String[] teamColor = teamColorString.split(":");
                    final ChatColor chatColor = ChatColor.getByChar(teamColor[1]);

                    if(!teamColor[0].equalsIgnoreCase(team.name()) || chatColor == null)
                        continue;

                    team.setChatColor(chatColor);
                }
            }
        }
    }
}
