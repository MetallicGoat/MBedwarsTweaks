package me.metallicgoat.tweaksaddon.tweaks.cosmetic;

import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.Team;
import de.marcely.bedwars.api.event.arena.RoundStartEvent;
import me.metallicgoat.tweaksaddon.config.ConfigValue;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CustomTeamColors implements Listener {

    //TODO Do I need to do this every time the game starts?

    @EventHandler
    public void onRoundStart(RoundStartEvent event){
        final Arena arena = event.getArena();
        
        if(!ConfigValue.custom_team_colors_enabled || ConfigValue.custom_team_colors.isEmpty())
            return;

        for(Team team : arena.getEnabledTeams()){
            
            if(ConfigValue.custom_team_colors.containsKey(team)){
                team.setChatColor(ConfigValue.custom_team_colors.get(team));
            }
            
            /*
            for(String teamColorString : teamColorList){
                if(teamColorString.contains(":")){
                    final String[] teamColor = teamColorString.split(":");
                    final ChatColor chatColor = ChatColor.getByChar(teamColor[1]);

                    if(!teamColor[0].equalsIgnoreCase(team.name()) || chatColor == null)
                        continue;

                    team.setChatColor(chatColor);
                }
            }
             */
        }
    }
}
