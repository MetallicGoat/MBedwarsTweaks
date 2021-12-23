package me.metallicgoat.tweaks.tweaks.spawners;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.Team;
import de.marcely.bedwars.api.event.arena.ArenaBedBreakEvent;
import de.marcely.bedwars.api.message.Message;
import me.metallicgoat.tweaks.MBedwarsTweaks;
import me.metallicgoat.tweaks.utils.ServerManager;
import me.metallicgoat.tweaks.utils.XSeries.XSound;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Optional;

public class ScheduleBedBreak implements Listener {

    @EventHandler
    public void onBedBreak(ArenaBedBreakEvent e){
        e.setBroadcasted(false);
        Arena arena = e.getArena();
        Player p = e.getPlayer();
        Team team = e.getTeam();
        sendBedBreakMessage(arena, team, p);
    }

    //Sets all beds to be destroyed at a specified time
    public static void breakArenaBeds(Arena arena){
        //Break all beds in an arena
        for (Team team : arena.getEnabledTeams()) {
            if (!arena.isBedDestroyed(team)) {
                arena.destroyBedNaturally(team, "Auto-Break", false);
                Location bedLoc = arena.getBedLocation(team).toLocation(arena.getGameWorld());
                bedLoc.getBlock().setType(Material.AIR);
                sendBedBreakMessage(arena, team, null);
            }
        }
        //Broadcast Message
        for (String s : plugin().getConfig().getStringList("Auto-Destroy-Message")) {
            arena.broadcast(Message.build(s).done());
        }
    }

    //Message that gets sent when a bed gets destroyed
    private static void sendBedBreakMessage(Arena arena, Team team, Player destroyer){
        String sound = ServerManager.getConfig().getString("Bed-Destroy-Sound");
        String bigTitle = ServerManager.getConfig().getString("Notification.Big-Title");
        String smallTitle = ServerManager.getConfig().getString("Notification.Small-Title");

        //Send sound/title to victim team
        for(Player p : arena.getPlayersInTeam(team)){
            assert bigTitle != null;
            assert smallTitle != null;
            BedwarsAPI.getNMSHelper().showTitle(p,
                    ChatColor.translateAlternateColorCodes('&', bigTitle),
                    ChatColor.translateAlternateColorCodes('&', smallTitle),
                    60, 15, 15);
            if(sound != null && !sound.equals("")) {
                Optional<XSound> xSound = XSound.matchXSound(sound);
                xSound.ifPresent(value -> value.play(destroyer.getLocation()));
            }
        }

        //Send public message
        if(destroyer != null) {

            //Send sound to destroyer
            if(sound != null && !sound.equals("")) {
                XSound.valueOf(sound).play(destroyer);
            }

            //Send public message
            for (String message : ServerManager.getConfig().getStringList("Player-Destroy-Message")) {
                String teamName = team.getDisplayName();
                String playerName = BedwarsAPI.getHelper().getPlayerDisplayName(destroyer);
                String destroyerColor = arena.getPlayerTeam(destroyer) != null ? "&" + arena.getPlayerTeam(destroyer).getChatColor().getChar() : "";

                String messageFormatted = Message.build(message)
                        .placeholder("team-name", teamName)
                        .placeholder("destroyer-name", playerName)
                        .placeholder("team-color", "&" + team.getChatColor().getChar())
                        .placeholder("destroyer-color", destroyerColor)
                        .done();
                arena.broadcast(messageFormatted);
            }
        }
    }

    private static MBedwarsTweaks plugin(){
        return MBedwarsTweaks.getInstance();
    }
}
