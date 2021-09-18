package me.metallicgoat.MBedwarsTweaks.tweaks.misc;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.ArenaStatus;
import me.metallicgoat.MBedwarsTweaks.utils.ServerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class HeightCap implements Listener {

    @EventHandler
    public void onBuild(BlockPlaceEvent e){
        Player player = e.getPlayer();
        Arena arena = BedwarsAPI.getGameAPI().getArenaByPlayer(player);
        boolean enabled = ServerManager.getConfig().getBoolean("Height-Cap.Enabled");
        if(enabled && arena != null && arena.getStatus() == ArenaStatus.RUNNING){
            ServerManager.getConfig().getStringList("Height-Cap.Arenas").forEach(s -> {
                if(s.contains(":")) {
                    String[] token = s.split(":");
                    if (arena.getName().equalsIgnoreCase(token[0])) {
                        if (e.getBlockPlaced().getY() > Integer.parseInt(token[1])) {
                            e.setCancelled(true);
                        }
                    }
                }
            });
        }
    }
}
