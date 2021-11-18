package me.metallicgoat.MBedwarsTweaks.tweaks.misc;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.ArenaStatus;
import de.marcely.bedwars.api.event.arena.RoundEndEvent;
import de.marcely.bedwars.api.event.arena.RoundStartEvent;
import me.clip.placeholderapi.PlaceholderAPI;
import me.metallicgoat.MBedwarsTweaks.Main;
import me.metallicgoat.MBedwarsTweaks.utils.ServerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;


public class ActionBar implements Listener {

    private BukkitTask actionBarTask = null;

    @EventHandler
    public void onGameStart(RoundStartEvent e){
        boolean enabled = ServerManager.getConfig().getBoolean("Action-Bar-Enabled");
        if(enabled && Main.papiEnabled) {
            //Start updating ActionBar
            if(actionBarTask == null){
                actionBarTask = startUpdatingTime();
            }
        }
    }

    @EventHandler
    public void onGameStop(RoundEndEvent event){
        boolean enabled = ServerManager.getConfig().getBoolean("Action-Bar-Enabled");
        if(enabled && Main.papiEnabled) {
            //Dont kill task if
            for (Arena arena : BedwarsAPI.getGameAPI().getArenas()) {
                if (arena.getStatus() == ArenaStatus.RUNNING) {
                    return;
                }
            }
            //Kill task
            if (actionBarTask != null) {
                actionBarTask.cancel();
                actionBarTask = null;
            }
        }
    }

    private static BukkitTask startUpdatingTime(){
        BukkitScheduler scheduler = plugin().getServer().getScheduler();

        return scheduler.runTaskTimer(plugin(),() -> {

            for(Arena arena:BedwarsAPI.getGameAPI().getArenas()){
                if(arena.getStatus() == ArenaStatus.RUNNING){
                    for(Player player:arena.getPlayers()){
                        String actionBarText = ServerManager.getConfig().getString("Action-Bar-Message");
                        if(actionBarText != null) {
                            actionBarText = PlaceholderAPI.setPlaceholders(player, actionBarText);
                            BedwarsAPI.getNMSHelper().showActionbar(player, actionBarText);
                        }
                    }
                }
            }
        }, 0L, 20L);
    }

    private static Main plugin(){
        return Main.getInstance();
    }
}
