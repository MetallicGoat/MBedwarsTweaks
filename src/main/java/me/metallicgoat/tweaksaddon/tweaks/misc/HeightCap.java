package me.metallicgoat.tweaksaddon.tweaks.misc;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.ArenaStatus;
import de.marcely.bedwars.api.message.Message;
import java.util.Map;
import me.metallicgoat.tweaksaddon.Util;
import me.metallicgoat.tweaksaddon.config.MainConfig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class HeightCap implements Listener {

  @EventHandler(priority = EventPriority.HIGH)
  public void onBuild(BlockPlaceEvent e) {
    final Player player = e.getPlayer();
    final Arena arena = BedwarsAPI.getGameAPI().getArenaByPlayer(player);

    if (!MainConfig.custom_height_cap_enabled || arena == null || arena.getStatus() != ArenaStatus.RUNNING)
      return;

    for (Map.Entry<String, Integer> arenaHeight : MainConfig.custom_height_cap_arenas.entrySet()) {
      if (Util.parseArenas(arenaHeight.getKey()).contains(arena)) {

        if (e.getBlockPlaced().getY() > arenaHeight.getValue()) {
          player.sendMessage(Message.build(MainConfig.custom_height_cap_warn).done());
          e.setCancelled(true);
          return;
        }
      }
    }
  }
}
