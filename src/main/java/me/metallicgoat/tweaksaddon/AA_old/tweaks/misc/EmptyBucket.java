package me.metallicgoat.tweaksaddon.AA_old.tweaks.misc;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.Arena;
import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;
import me.metallicgoat.tweaksaddon.AA_old.utils.ServerManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.inventory.ItemStack;

public class EmptyBucket implements Listener {

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent e) {
        final Player p = e.getPlayer();
        Arena arena = BedwarsAPI.getGameAPI().getArenaByPlayer(p);
        boolean enabled = ServerManager.getConfig().getBoolean("Empty-Buckets");
        if(enabled) {
            if (arena != null) {
                Bukkit.getServer().getScheduler().runTaskLater(plugin(), () -> {
                    //Added isCancelled Check because of WaterFlow
                    if(!e.isCancelled()){
                        p.setItemInHand(new ItemStack(Material.AIR));
                    }
                }, 1L);
            }
        }
    }
    private static MBedwarsTweaksPlugin plugin(){
        return MBedwarsTweaksPlugin.getInstance();
    }
}
