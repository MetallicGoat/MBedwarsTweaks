package me.metallicgoat.tweaksaddon.tweaks.advancedswords;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.ArenaStatus;
import me.metallicgoat.tweaksaddon.config.ConfigValue;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class SwordDrop implements Listener {

    @EventHandler
    public void giveSwordOnDrop(PlayerDropItemEvent event) {

        if (!ConfigValue.sword_drop_enabled)
            return;

        final Player player = event.getPlayer();
        final Arena arena = BedwarsAPI.getGameAPI().getArenaByPlayer(player);
        final PlayerInventory pi = player.getInventory();


        if (arena == null || arena.getStatus() != ArenaStatus.RUNNING)
            return;


        if (event.getItemDrop().getItemStack().getType() == ToolSwordHelper.WOOD_SWORD) {
            event.setCancelled(true);
            return;
        }

        if (ToolSwordHelper.getSwordsAmount(event.getPlayer()) == 0) {

            final ItemStack is = ToolSwordHelper.getDefaultWoodSword(player, arena);

            // tries to put sword in slot player dropped sword from
            if (pi.getItem(pi.getHeldItemSlot()) == null)
                pi.setItem(pi.getHeldItemSlot(), is);
            else
                pi.addItem(is);
        }
    }

    @EventHandler
    public void replaceSwordOnCollect(PlayerPickupItemEvent event) {

        if (!ConfigValue.sword_drop_enabled)
            return;

        final Player player = event.getPlayer();
        final Arena arena = BedwarsAPI.getGameAPI().getArenaByPlayer(player);
        final ItemStack sword = event.getItem().getItemStack();

        if (arena != null &&
                ConfigValue.sword_drop_materials.contains(sword.getType()) &&
                ToolSwordHelper.isNotToIgnore(sword)) {

            final PlayerInventory pi = player.getInventory();

            if (pi.contains(ToolSwordHelper.WOOD_SWORD)) {
                pi.remove(ToolSwordHelper.WOOD_SWORD);
            }

            event.setCancelled(true);
            pi.addItem(sword);
            event.getItem().remove();
        }
    }
}
