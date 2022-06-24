package me.metallicgoat.tweaksaddon.tweaks.mechanics;

import me.metallicgoat.tweaksaddon.config.ConfigValue;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.ArrayList;
import java.util.List;

public class FireballBlockBreakWhitelist implements Listener {

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (!ConfigValue.fireball_whitelist_enabled || event.isCancelled() || event.getEntityType() != EntityType.FIREBALL)
            return;

        final List<Block> blockListCopy = new ArrayList<>(event.blockList());

        for (Block block : blockListCopy) {
            if (ConfigValue.fireball_whitelist_blocks.contains(block.getType()))
                event.blockList().remove(block);
        }
    }
}
