package me.metallicgoat.tweaks.old.advancedswords;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.ArenaStatus;
import me.metallicgoat.tweaks.utils.ServerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

public class AntiDrop implements Listener {
    @EventHandler
    public void onToolDrop(PlayerDropItemEvent e){
        final Player p = e.getPlayer();
        final Arena arena = BedwarsAPI.getGameAPI().getArenaByPlayer(p);
        // If player is trying to dop a tool he shouldn't, cancel event
        if(arena != null && arena.getStatus() == ArenaStatus.RUNNING) {
            if (ServerManager.getSwordsToolsConfig().getBoolean("Anti-Drop.Enabled") &&
                    ServerManager.getSwordsToolsConfig().getStringList("Anti-Drop.List").contains(e.getItemDrop().getItemStack().getType().name()) &&
                    ToolSwordHelper.isNotToIgnore(e.getItemDrop().getItemStack())) {
                e.setCancelled(true);
            }
        }
    }
}
