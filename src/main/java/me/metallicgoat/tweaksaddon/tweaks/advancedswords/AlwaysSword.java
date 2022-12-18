package me.metallicgoat.tweaksaddon.tweaks.advancedswords;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.ArenaStatus;
import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;
import me.metallicgoat.tweaksaddon.config.ConfigValue;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class AlwaysSword implements Listener {

    // TODO keep track of running instances
    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if(!ConfigValue.always_sword_chest_enabled)
            return;

        final Player player = (Player) e.getWhoClicked();
        final Arena arena = BedwarsAPI.getGameAPI().getArenaByPlayer(player);

        if (arena != null
                && arena.getStatus() == ArenaStatus.RUNNING
                && player.getGameMode() != GameMode.SPECTATOR) {

            Bukkit.getServer().getScheduler().runTaskLater((MBedwarsTweaksPlugin.getInstance()), () -> {
                final Inventory pi = player.getInventory();
                final int i = ToolSwordHelper.getSwordsAmount(player);

                if (i >= 2) {
                    for (ItemStack itemStack : pi.getContents()) {
                        if (itemStack != null
                                && itemStack.getType() == ToolSwordHelper.WOOD_SWORD
                                && ToolSwordHelper.isNotToIgnore(itemStack)) {

                            pi.remove(itemStack);

                            if(!ToolSwordHelper.hasBetterSword(player))
                                break;
                        }
                    }
                    // Give player a wood sword if they don't have one
                } else if (i == 0 &&
                        (e.getCurrentItem() == null || e.getCurrentItem() != null && !e.getCurrentItem().getType().name().endsWith("SWORD") && ToolSwordHelper.isNotToIgnore(e.getCurrentItem())) &&
                        (e.getCursor() == null || e.getCursor() != null && !e.getCursor().getType().name().endsWith("SWORD") && ToolSwordHelper.isNotToIgnore(e.getCursor()))) {

                    pi.addItem(ToolSwordHelper.getDefaultWoodSword(player, arena));

                }
            }, 25L);
        }
    }

    @EventHandler
    public void giveSwordOnDrop(PlayerDropItemEvent event) {
        if (!ConfigValue.always_sword_drop_enabled)
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
        if (!ConfigValue.always_sword_drop_enabled)
            return;

        final Player player = event.getPlayer();
        final Arena arena = BedwarsAPI.getGameAPI().getArenaByPlayer(player);
        final ItemStack sword = event.getItem().getItemStack();

        if (arena != null &&
                ToolSwordHelper.isSword(sword.getType()) &&
                ToolSwordHelper.isNotToIgnore(sword)) {

            final PlayerInventory pi = player.getInventory();
            if (pi.contains(ToolSwordHelper.WOOD_SWORD))
                pi.remove(ToolSwordHelper.WOOD_SWORD);

            pi.addItem(sword);
            event.getItem().remove();
            event.setCancelled(true);
        }
    }
}
