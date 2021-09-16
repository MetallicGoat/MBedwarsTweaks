package me.metallicgoat.MBedwarsTweaks.tweaks.spawners;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.Team;
import de.marcely.bedwars.api.event.arena.ArenaBedBreakEvent;
import de.marcely.bedwars.api.message.Message;
import me.metallicgoat.MBedwarsTweaks.Main;
import me.metallicgoat.MBedwarsTweaks.utils.ServerManager;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.Objects;

public class ScheduleBedBreak {

    @EventHandler
    public void onBedBreak(ArenaBedBreakEvent e){
        e.setBroadcasted(false);
        Arena arena = e.getArena();
        World w = e.getArena().getGameWorld();
        Player p = e.getPlayer();
        Team team = e.getTeam();
        Location bedLoc = Objects.requireNonNull(arena.getBedLocation(team)).toLocation(w);
        sendBedBreakMessage(arena, team, bedLoc, p);
    }

    //Sets all beds to be destroyed at a specified time
    public static void scheduleBreak(Long time, Arena arena){
        BukkitScheduler scheduler = plugin().getServer().getScheduler();
        scheduler.scheduleSyncDelayedTask(plugin(), () -> {
            for (Team team : arena.getEnabledTeams()) {
                if (!arena.isBedDestroyed(team)) {
                    arena.destroyBedNaturally(team, "Auto-Break", false);
                    Location bedLoc = arena.getBedLocation(team).toLocation(arena.getGameWorld());
                    bedLoc.getBlock().setType(Material.AIR);
                    sendBedBreakMessage(arena, team, bedLoc, null);
                }
            }
            for (String s : plugin().getConfig().getStringList("Auto-Destroy-Message")) {
                arena.broadcast(Message.build(s).done());
            }

        }, time);
    }

    //Message that gets sent when a bed gets destroyed
    private static void sendBedBreakMessage(Arena arena, Team team, Location bedLoc, Player destroyer){
        Main plugin = Main.getInstance();
        for(Player p : arena.getPlayersInTeam(team)){
            String bigTitle = ServerManager.getConfig().getString("Notification.Big-Title");
            String smallTitle = ServerManager.getConfig().getString("Notification.Small-Title");

            assert bigTitle != null;
            assert smallTitle != null;
            BedwarsAPI.getNMSHelper().showTitle(p,
                    ChatColor.translateAlternateColorCodes('&', bigTitle),
                    ChatColor.translateAlternateColorCodes('&', smallTitle),
                    60, 15, 15);

            if(plugin.getServer().getClass().getPackage().getName().contains("v1.8")){
                p.playSound(bedLoc, Sound.valueOf("ENDERDRAGON_GROWL"), 1.0F, 1.0F);
            }else{
                p.playSound(bedLoc, Sound.valueOf("ENTITY_ENDER_DRAGON_GROWL"), 1.0F, 1.0F);
            }
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
                Bukkit.getServer().broadcastMessage(messageFormatted);
            }
        }
    }

    private static Main plugin(){
        return Main.getInstance();
    }
}
