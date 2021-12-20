package me.metallicgoat.tweaks.tweaks.misc;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.Arena;
import me.metallicgoat.tweaks.MBedwarsTweaks;
import me.metallicgoat.tweaks.utils.ServerManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

public class EmptyPotion implements Listener {

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent e) {
        final Player p = e.getPlayer();
        Arena arena = BedwarsAPI.getGameAPI().getArenaByPlayer(p);
        boolean enabled = ServerManager.getConfig().getBoolean("Empty-Potions");
        if(enabled) {
            if (arena != null) {
                if (e.getItem().getType().equals(Material.POTION)) {
                    //1.8 does not have an off hand
                    Bukkit.getServer().getScheduler().runTaskLater(plugin(), () -> p.setItemInHand(new ItemStack(Material.AIR)), 1L);
                }
            }
        }
    }
    private static MBedwarsTweaks plugin(){
        return MBedwarsTweaks.getInstance();
    }
}