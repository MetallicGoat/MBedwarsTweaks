package me.metallicgoat.tweaks.advancedswords;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.ArenaStatus;
import me.metallicgoat.tweaks.utils.ServerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

import java.util.List;

public class AntiDrop implements Listener {
    @EventHandler
    public void onToolDrop(PlayerDropItemEvent e){
        Player p = e.getPlayer();
        Arena arena = BedwarsAPI.getGameAPI().getArenaByPlayer(p);
        // If player is trying to dop a tool he shouldn't, cancel event
        if(arena != null && arena.getStatus() == ArenaStatus.RUNNING) {
            if (antiDrop() &&
                    antiDropList().contains(e.getItemDrop().getItemStack().getType().name()) &&
                    ToolSwordHelper.isNotToIgnore(e.getItemDrop().getItemStack())) {
                e.setCancelled(true);
            }
        }
    }
    private boolean antiDrop() {
        return ServerManager.getSwordsToolsConfig().getBoolean("Anti-Drop.Enabled");
    }
    private List<String> antiDropList() {
        return ServerManager.getSwordsToolsConfig().getStringList("Anti-Drop.List");
    }
}
