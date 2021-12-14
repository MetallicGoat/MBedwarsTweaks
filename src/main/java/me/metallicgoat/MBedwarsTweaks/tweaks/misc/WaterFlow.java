package me.metallicgoat.MBedwarsTweaks.tweaks.misc;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.Arena;
import me.metallicgoat.MBedwarsTweaks.utils.ServerManager;
import org.bukkit.Location;
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
            //Check if water is moving from a location in an arena to outside an arena
            if (toArena.isEmpty() && !fromArena.isEmpty()) {
                e.setCancelled(true);
            }
            //Flowing into an arena
            if(!fromArena.isEmpty()){
                //For all possible arenas
                fromArena.forEach(arena -> {
                    //Check if we can place block at position
                    if(!arena.canPlaceBlockAt(e.getBlock().getLocation())){
                        e.setCancelled(true);
                    }
                });
            }
        }
    }

    @EventHandler
    public void onLiquidPlace(PlayerBucketEmptyEvent e){
        Player p = e.getPlayer();
        Arena arena = BedwarsAPI.getGameAPI().getArenaByPlayer(p);
        Location location = e.getBlockClicked().getRelative(e.getBlockFace()).getLocation();
        if(arena != null){
            Collection<Arena> placed = BedwarsAPI.getGameAPI().getArenaByLocation(location);
            //Check if block is inside arena
            if(placed != null && placed.isEmpty()){
                e.setCancelled(true);
            }
            //check if block is placeable at location
            if(!arena.canPlaceBlockAt(location)){
                e.setCancelled(true);
            }
        }
    }
}
