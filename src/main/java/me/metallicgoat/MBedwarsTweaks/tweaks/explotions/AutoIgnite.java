package me.metallicgoat.MBedwarsTweaks.tweaks.explotions;

import me.metallicgoat.MBedwarsTweaks.Main;
import me.metallicgoat.MBedwarsTweaks.utils.ServerManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class AutoIgnite implements Listener {
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Bukkit.getServer().getScheduler().runTaskLater(plugin(), () -> {
            if (e.getBlock().getType() == Material.TNT) {
                if (ServerManager.getConfig().getBoolean("TNT.Auto-Ignite")) {
                    e.getBlockPlaced().setType(Material.AIR);
                    TNTPrimed tnt = e.getPlayer().getWorld().spawn(e.getBlockPlaced().getLocation().add(.5, 0, .5), TNTPrimed.class);
                    tnt.setFuseTicks(ServerManager.getConfig().getInt("TNT.Delay") * 20);
                }
            }
        }, 1L);
    }
    private static Main plugin(){
        return Main.getInstance();
    }
}
