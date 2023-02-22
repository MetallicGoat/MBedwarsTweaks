package me.metallicgoat.tweaksaddon.tweaks.misc;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.Arena;
import me.metallicgoat.tweaksaddon.config.ConfigValue;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;

import java.util.Collection;

public class PreventLiquidBuildUp implements Listener {

    // TODO Possibly cause issues if region arenas intersect?
    @EventHandler
    public void onFlow(BlockFromToEvent event) {
        if (!ConfigValue.prevent_liquid_build_up)
            return;

        final Collection<Arena> toArena = BedwarsAPI.getGameAPI().getArenaByLocation(event.getToBlock().getLocation());
        final Collection<Arena> fromArena = BedwarsAPI.getGameAPI().getArenaByLocation(event.getBlock().getLocation());

        if (toArena != null && fromArena != null) {
            // Check if water is moving from a location in an arena to outside an arena
            if (toArena.isEmpty() && !fromArena.isEmpty())
                event.setCancelled(true);

            // Flowing into an arena
            if (fromArena.isEmpty())
                return;

            // For all possible arenas
            for (Arena arena : fromArena) {
                // Check if we can place block at position
                if (!arena.canPlaceBlockAt(event.getToBlock().getLocation())) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onLiquidPlace(PlayerBucketEmptyEvent event) {
        if (!ConfigValue.prevent_liquid_build_up)
            return;

        final Player p = event.getPlayer();
        final Arena arena = BedwarsAPI.getGameAPI().getArenaByPlayer(p);

        if (arena == null)
            return;

        final Location location = event.getBlockClicked().getRelative(event.getBlockFace()).getLocation();
        final Collection<Arena> placed = BedwarsAPI.getGameAPI().getArenaByLocation(location);

        // Check if block is inside arena
        if (placed != null && placed.isEmpty())
            event.setCancelled(true);

        // Check if block is placeable at location
        if (!arena.canPlaceBlockAt(location))
            event.setCancelled(true);
    }
}
