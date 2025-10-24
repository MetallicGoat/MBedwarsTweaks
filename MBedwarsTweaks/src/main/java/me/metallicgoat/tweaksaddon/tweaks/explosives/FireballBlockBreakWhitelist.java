package me.metallicgoat.tweaksaddon.tweaks.explosives;

import java.util.ArrayList;
import java.util.List;
import me.metallicgoat.tweaksaddon.config.MainConfig;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

public class FireballBlockBreakWhitelist implements Listener {

  @EventHandler
  public void onEntityExplode(EntityExplodeEvent event) {
    if (!MainConfig.fireball_whitelist_enabled || event.isCancelled() || event.getEntityType() != EntityType.FIREBALL)
      return;

    final List<Block> blockListCopy = new ArrayList<>(event.blockList());

    for (Block block : blockListCopy) {
      if (MainConfig.fireball_whitelist_blocks.contains(block.getType()))
        event.blockList().remove(block);
    }
  }
}
