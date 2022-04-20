package me.metallicgoat.tweaksaddon.tweaks.gameplay;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.Arena;
import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;
import me.metallicgoat.tweaksaddon.config.ConfigValue;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public class EmptyContainers implements Listener {

    // Keep in mind 1.8 does not have an off-hand

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {

        if (!ConfigValue.remove_empty_buckets)
            return;

        final Player player = event.getPlayer();
        final Arena arena = BedwarsAPI.getGameAPI().getArenaByPlayer(player);

        if (arena == null)
            return;

        Bukkit.getServer().getScheduler().runTaskLater(MBedwarsTweaksPlugin.getInstance(), () -> {
            // Added isCancelled check because of WaterFlow or other plugin
            if (!event.isCancelled())
                event.getItemStack().setType(Material.AIR);
        }, 1L);
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {

        if (!ConfigValue.remove_empty_potions || event.getItem().getType() != Material.POTION)
            return;

        final Player player = event.getPlayer();
        final Arena arena = BedwarsAPI.getGameAPI().getArenaByPlayer(player);

        if (arena == null)
            return;

        Bukkit.getServer().getScheduler().runTaskLater(MBedwarsTweaksPlugin.getInstance(), () -> {
            if (!event.isCancelled())
                event.getItem().setType(Material.AIR);
        }, 1L);
    }
}