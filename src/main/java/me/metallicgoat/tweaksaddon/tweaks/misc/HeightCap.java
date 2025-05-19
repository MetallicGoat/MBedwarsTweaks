package me.metallicgoat.tweaksaddon.tweaks.misc;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.ArenaStatus;
import de.marcely.bedwars.api.message.Message;
import java.util.Map;
import me.metallicgoat.tweaksaddon.utils.CachedArenaIdentifier;
import me.metallicgoat.tweaksaddon.config.MainConfig;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;

public class HeightCap implements Listener {

  @EventHandler(priority = EventPriority.HIGH)
  public void onBuild(BlockPlaceEvent event) {
    if (!MainConfig.custom_height_cap_enabled)
      return;

    cancelIfOutOfBounds(event, event.getPlayer(), event.getBlockPlaced().getLocation());
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onBucketEmpty(PlayerBucketEmptyEvent event) {
    if (!MainConfig.custom_height_cap_enabled)
      return;

    cancelIfOutOfBounds(event, event.getPlayer(), event.getBlockClicked().getRelative(event.getBlockFace()).getLocation());
  }

  private void cancelIfOutOfBounds(Cancellable event, Player player, Location location) {
    if (event.isCancelled())
      return;

    final Arena arena = BedwarsAPI.getGameAPI().getArenaByPlayer(player);

    if (arena == null || arena.getStatus() != ArenaStatus.RUNNING)
      return;

    for (Map.Entry<CachedArenaIdentifier, Integer> arenaHeight : MainConfig.custom_height_cap_arenas.entrySet()) {
      if (arenaHeight.getKey().includes(arena) && arenaHeight.getValue() != null) {

        if (location.getY() > arenaHeight.getValue()) {
          player.sendMessage(Message.build(MainConfig.custom_height_cap_warn).done());
          event.setCancelled(true);
          return;
        }
      }
    }
  }
}
