package me.metallicgoat.MBedwarsTweaks.tweaks.messages;

import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.Team;
import de.marcely.bedwars.api.event.arena.TeamEliminateEvent;
import de.marcely.bedwars.api.message.Message;
import me.metallicgoat.MBedwarsTweaks.utils.ServerManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class TeamEliminate implements Listener {

    @EventHandler
    public void onEliminate(TeamEliminateEvent e){
        Arena arena = e.getArena();
        Team team = e.getTeam();
        String teamName = team.name();
        String teamColor = "&" + team.getChatColor().getChar();
        if(ServerManager.getConfig().getBoolean("Team-Eliminate-Message-Enabled")) {
            for (String message : ServerManager.getConfig().getStringList("Team-Eliminate-Message")) {
                String messageFormatted = Message.build(message)
                        .placeholder("team-name", teamName)
                        .placeholder("team-color", teamColor)
                        .done();
                arena.broadcast(messageFormatted);
            }
        }
    }
}
