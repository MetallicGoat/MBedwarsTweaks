package me.metallicgoat.tweaksaddon.tweaks.gameplay;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.ArenaStatus;
import de.marcely.bedwars.api.message.Message;
import me.metallicgoat.tweaksaddon.Util;
import me.metallicgoat.tweaksaddon.config.ConfigValue;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.Map;

public class HeightCap implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onBuild(BlockPlaceEvent e) {
        final Player player = e.getPlayer();
        final Arena arena = BedwarsAPI.getGameAPI().getArenaByPlayer(player);

        if (!ConfigValue.custom_height_cap || arena == null || arena.getStatus() != ArenaStatus.RUNNING)
            return;

        for (Map.Entry<String, Integer> arenaHeight : ConfigValue.custom_height_cap_arenas.entrySet()) {
            if(Util.parseArenas(arenaHeight.getKey()).contains(arena)){
                if (e.getBlockPlaced().getY() > arenaHeight.getValue()) {
                    player.sendMessage(Message.build(ConfigValue.custom_height_cap_warn).done());
                    e.setCancelled(true);
                    return;
                }
            }
        }
    }
}
