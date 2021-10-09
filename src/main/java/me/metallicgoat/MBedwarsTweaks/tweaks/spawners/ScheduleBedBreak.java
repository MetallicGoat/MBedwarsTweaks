package me.metallicgoat.MBedwarsTweaks.tweaks.spawners;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.Team;
import de.marcely.bedwars.api.event.arena.ArenaBedBreakEvent;
import de.marcely.bedwars.api.message.Message;
import me.metallicgoat.MBedwarsTweaks.Main;
import me.metallicgoat.MBedwarsTweaks.utils.ServerManager;
import me.metallicgoat.MBedwarsTweaks.utils.XSeries.XSound;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

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
        for (Team team : arena.getEnabledTeams()) {
            if (!arena.isBedDestroyed(team)) {
                arena.destroyBedNaturally(team, "Auto-Break", false);
                Location bedLoc = arena.getBedLocation(team).toLocation(arena.getGameWorld());
                bedLoc.getBlock().setType(Material.AIR);
                sendBedBreakMessage(arena, team, null);
            }
        }
        for (String s : plugin().getConfig().getStringList("Auto-Destroy-Message")) {
            arena.broadcast(Message.build(s).done());
        }
    }

    //Message that gets sent when a bed gets destroyed
    private static void sendBedBreakMessage(Arena arena, Team team, Player destroyer){
        Main plugin = Main.getInstance();
        for(Player p : arena.getPlayersInTeam(team)){
            String sound = ServerManager.getConfig().getString("Bed-Destroy-Sound");
            String bigTitle = ServerManager.getConfig().getString("Notification.Big-Title");
            String smallTitle = ServerManager.getConfig().getString("Notification.Small-Title");

            assert bigTitle != null;
            assert smallTitle != null;
            BedwarsAPI.getNMSHelper().showTitle(p,
                    ChatColor.translateAlternateColorCodes('&', bigTitle),
                    ChatColor.translateAlternateColorCodes('&', smallTitle),
                    60, 15, 15);

            XSound.valueOf(sound).play(p);
        }
        if(destroyer != null) {
            for (String message : plugin.getConfig().getStringList("Player-Destroy-Message")) {
                String teamName = team.getDisplayName();
                String playerName = destroyer.getName();
                String messageFormatted = Message.build(message)
                        .placeholder("team-name", teamName)
                        .placeholder("destroyer-name", playerName)
                        .placeholder("team-color", "&" + team.getChatColor().getChar())
                        .placeholder("destroyer-color", "&" + arena.getPlayerTeam(destroyer).getChatColor().getChar())
                        .done();
                arena.broadcast(messageFormatted);
            }
        }
    }

    private static Main plugin(){
        return Main.getInstance();
    }
}
