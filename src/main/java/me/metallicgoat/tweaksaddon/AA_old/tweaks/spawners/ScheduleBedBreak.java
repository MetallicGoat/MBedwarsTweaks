package me.metallicgoat.tweaksaddon.AA_old.tweaks.spawners;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.Team;
import de.marcely.bedwars.api.event.arena.ArenaBedBreakEvent;
import de.marcely.bedwars.api.message.Message;
import de.marcely.bedwars.tools.location.XYZD;
import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;
import me.metallicgoat.tweaksaddon.AA_old.utils.ServerManager;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ScheduleBedBreak implements Listener {

    @EventHandler
    public void onBedBreak(ArenaBedBreakEvent e){
        e.setBroadcasted(false);
        e.setPlayingSound(true);
        Arena arena = e.getArena();
        Player p = e.getPlayer();
        Team team = e.getTeam();
        sendBedBreakMessage(arena, team, p);
    }

    //Sets all beds to be destroyed at a specified time
    public static void breakArenaBeds(Arena arena){
        //Break all beds in an arena
        for (Team team : arena.getEnabledTeams()) {
            final XYZD bedLoc = arena.getBedLocation(team);
            if (!arena.isBedDestroyed(team) && bedLoc != null) {
                arena.destroyBedNaturally(team, "Auto-Break", false);
                bedLoc.toLocation(arena.getGameWorld()).getBlock().setType(Material.AIR);
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
        final String bigTitle = ServerManager.getConfig().getString("Notification.Big-Title");
        final String smallTitle = ServerManager.getConfig().getString("Notification.Small-Title");

        //Send title to victim team
        for(Player p : arena.getPlayersInTeam(team)){
            assert bigTitle != null;
            assert smallTitle != null;
            BedwarsAPI.getNMSHelper().showTitle(p,
                    ChatColor.translateAlternateColorCodes('&', bigTitle),
                    ChatColor.translateAlternateColorCodes('&', smallTitle),
                    60, 15, 15);
        }

        //Send public message
        if(destroyer != null) {

            //Send public message
            for (String message : ServerManager.getConfig().getStringList("Player-Destroy-Message")) {
                final String teamName = team.getDisplayName();
                final String playerName = BedwarsAPI.getHelper().getPlayerDisplayName(destroyer);
                final String destroyerColor = arena.getPlayerTeam(destroyer) != null ? "&" + arena.getPlayerTeam(destroyer).getChatColor().getChar() : "";
                final String destroyerTeam = arena.getPlayerTeam(destroyer) != null ? ChatColor.stripColor(arena.getPlayerTeam(destroyer).getDisplayName()) : "";

                final String messageFormatted = Message.build(message)
                        .placeholder("team-name", teamName)
                        .placeholder("team-color", "&" + team.getChatColor().getChar())
                        .placeholder("destroyer-name", playerName)
                        .placeholder("destroyer-team-name", destroyerTeam)
                        .placeholder("destroyer-color", destroyerColor)
                        .done();
                arena.broadcast(messageFormatted);
            }
        }
    }

    private static MBedwarsTweaksPlugin plugin(){
        return MBedwarsTweaksPlugin.getInstance();
    }
}