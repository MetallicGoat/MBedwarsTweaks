package me.metallicgoat.MBedwarsTweaks.tweaks.misc;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.Arena;
import me.metallicgoat.MBedwarsTweaks.utils.ServerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;

import java.util.Collection;

public class WaterFlow implements Listener {

    @EventHandler
    public void onFlow(BlockFromToEvent e){
        Collection<Arena> toArena = BedwarsAPI.getGameAPI().getArenaByLocation(e.getToBlock().getLocation());
        Collection<Arena> fromArena = BedwarsAPI.getGameAPI().getArenaByLocation(e.getBlock().getLocation());

        if(toArena != null && fromArena != null &&
                ServerManager.getConfig().getBoolean("Prevent-Liquid-Build-Up")) {
            if (toArena.isEmpty() && !fromArena.isEmpty()) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onLiquidPlace(PlayerBucketEmptyEvent e){
        Player p = e.getPlayer();
        Arena a = BedwarsAPI.getGameAPI().getArenaByPlayer(p);
        if(a != null){
            Collection<Arena> placed = BedwarsAPI.getGameAPI().getArenaByLocation(e.getBlockClicked().getRelative(e.getBlockFace()).getLocation());
            if(placed != null && placed.isEmpty()){
                e.setCancelled(true);
            }
        }
    }
}
