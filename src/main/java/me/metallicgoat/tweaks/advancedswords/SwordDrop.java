package me.metallicgoat.tweaks.advancedswords;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.ArenaStatus;
import me.metallicgoat.tweaks.utils.ServerManager;
import me.metallicgoat.tweaks.utils.XSeries.XMaterial;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Objects;

public class SwordDrop implements Listener {

    @EventHandler
    public void giveSwordOnDrop(PlayerDropItemEvent e){
        final Player p = e.getPlayer();
        final Arena arena = BedwarsAPI.getGameAPI().getArenaByPlayer(p);
        final PlayerInventory pi = p.getInventory();

        if(ServerManager.getSwordsToolsConfig().getBoolean("Advanced-Sword-Drop.Enabled")) {
            if (arena != null && arena.getStatus() == ArenaStatus.RUNNING) {
                final ItemStack is = new ItemStack(Objects.requireNonNull(XMaterial.WOODEN_SWORD.parseItem()));
                if (e.getItemDrop().getItemStack().getType() == XMaterial.WOODEN_SWORD.parseMaterial()) {
                    e.setCancelled(true);
                }
                if (getSwords(e.getPlayer()) == 0 &&
                        e.getItemDrop().getItemStack().getType() != XMaterial.WOODEN_SWORD.parseMaterial()) {
                    final ItemStack item = e.getItemDrop().getItemStack();
                    ItemMeta meta = item.getItemMeta();
                    assert meta != null;
                    meta.setDisplayName("Wooden Sword");
                    is.setItemMeta(meta);
                    //tries to put sword in slot player dropped sword from
                    if(pi.getItem(pi.getHeldItemSlot()) == null) {
                        pi.setItem(pi.getHeldItemSlot(), is);
                    }else{
                        pi.addItem(is);
                    }
                }
            }
        }
    }

    @EventHandler
    public void replaceSwordOnCollect(PlayerPickupItemEvent e){
        final Player p = e.getPlayer();
        final Arena arena = BedwarsAPI.getGameAPI().getArenaByPlayer(p);
        final PlayerInventory pi = p.getInventory();
        final ItemStack sword = e.getItem().getItemStack();

        if(ServerManager.getSwordsToolsConfig().getBoolean("Advanced-Sword-Drop.Enabled")) {
            if(arena != null &&
                    ServerManager.getSwordsToolsConfig().getStringList("Advanced-Sword-Drop.List").contains(sword.getType().name()) &&
                    ToolSwordHelper.isNotToIgnore(sword)) {
                assert XMaterial.WOODEN_SWORD.parseMaterial() != null;
                if(pi.contains(XMaterial.WOODEN_SWORD.parseMaterial())) {
                    pi.remove(XMaterial.WOODEN_SWORD.parseMaterial());
                }
                e.setCancelled(true);
                pi.addItem(sword);
                e.getItem().remove();
            }
        }
    }

    private int getSwords(Player player) {
        int count = 0;
        for(ItemStack item : player.getInventory().getContents()) {
            if(item != null)
                if(item.getType().name().endsWith("SWORD") &&
                        ToolSwordHelper.isNotToIgnore(item)){
                    count++;
                }
        }
        return count;
    }
}
