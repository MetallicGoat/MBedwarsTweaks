package me.metallicgoat.tweaksaddon.AA_old.tweaks.explotions;

import me.metallicgoat.tweaksaddon.AA_old.utils.ServerManager;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.ArrayList;
import java.util.List;

public class FireballWhitelist implements Listener {

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent e) {
        if (e.isCancelled())
            return;
        //Check if explosion is a fireball
        if (e.getEntityType() == EntityType.FIREBALL && ServerManager.getConfig().getBoolean("FireballWhitelist.Enabled")) {
            List<Block> blockListCopy = new ArrayList<>(e.blockList());

            //Prevent blocks in config from being destroyed
            List<String> whitelistedBlocks = ServerManager.getConfig().getStringList("FireballWhitelist.Blocks");
            for (Block block : blockListCopy) {
                if (whitelistedBlocks.contains(block.getType().name()))
                    e.blockList().remove(block);
            }
        }
    }
}
