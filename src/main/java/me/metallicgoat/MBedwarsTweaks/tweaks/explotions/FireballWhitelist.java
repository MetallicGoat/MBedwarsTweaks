package me.metallicgoat.MBedwarsTweaks.tweaks.explotions;

import me.metallicgoat.MBedwarsTweaks.utils.ServerManager;
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
            for (Block block : blockListCopy) {
                if (ServerManager.getConfig().getStringList("FireballWhitelist.Blocks").contains(block.getType().name()))
                    e.blockList().remove(block);
            }
        }
    }
}
