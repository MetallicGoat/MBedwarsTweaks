package me.metallicgoat.tweaksaddon.tweaks.advancedswords;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.ArenaStatus;
import me.metallicgoat.tweaksaddon.config.SwordsToolsConfig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

public class AntiDrop implements Listener {

  @EventHandler
  public void onToolDrop(PlayerDropItemEvent event) {
    if (!SwordsToolsConfig.anti_drop_enabled)
      return;

    final Player player = event.getPlayer();
    final Arena arena = BedwarsAPI.getGameAPI().getArenaByPlayer(player);

    // If player is trying to dop a tool he shouldn't, cancel event
    if (arena == null || arena.getStatus() != ArenaStatus.RUNNING)
      return;

    if (SwordsToolsConfig.anti_drop_materials.contains(event.getItemDrop().getItemStack().getType())
        && ToolSwordHelper.isNotToIgnore(event.getItemDrop().getItemStack())) {

      event.setCancelled(true);
    }
  }
}
