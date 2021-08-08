package me.metallicgoat.MBedwarsTweaks.tweaks.useditems;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.Arena;
import me.metallicgoat.MBedwarsTweaks.Main;
import me.metallicgoat.MBedwarsTweaks.utils.ServerManager;
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
                if (e.getItem() == null) return;
                if (e.getItem().getType().equals(Material.POTION)) {
                    Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(plugin(), () -> p.setItemInHand(new ItemStack(Material.AIR)), 1L);
                }
            }
        }
    }
    private static Main plugin(){
        return Main.getInstance();
    }
}