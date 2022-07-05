package me.metallicgoat.tweaksaddon.tweaks.advancedswords;

import de.marcely.bedwars.api.BedwarsAPI;
import me.metallicgoat.tweaksaddon.config.ConfigValue;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class AntiChest implements Listener {

    @EventHandler
    public void onShiftClick(InventoryClickEvent e) {
        if(e.getInventory().getSize() > 26) {
            if (e.getClick().isShiftClick() && inArena((Player) e.getWhoClicked())) {
                Inventory clicked = e.getClickedInventory();
                if (clicked == e.getWhoClicked().getInventory()) {
                    ItemStack clickedOn = e.getCurrentItem();
                    if (clickedOn != null
                            && ConfigValue.anti_chest_materials.contains(clickedOn.getType())
                            && ToolSwordHelper.isNotToIgnore(clickedOn)) {
                        e.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {

        if(e.getAction() == InventoryAction.HOTBAR_SWAP || e.getAction() == InventoryAction.HOTBAR_MOVE_AND_READD){
            final HumanEntity player = e.getWhoClicked();
            final Inventory inventory = player != null ? player.getInventory() : null;

            if(inventory != null){
                final int swapSlot = e.getHotbarButton();
                final ItemStack itemStack = inventory.getItem(swapSlot);

                if(itemStack != null
                        && ConfigValue.anti_chest_materials.contains(itemStack.getType())
                        && ToolSwordHelper.isNotToIgnore(itemStack)){
                    e.setCancelled(true);
                }
            }
        }

        final Inventory clicked = e.getClickedInventory();
        if(e.getInventory().getSize() > 26) {
            if (clicked != e.getWhoClicked().getInventory() && inArena((Player) e.getWhoClicked())) {
                // The cursor item is going into the top inventory
                final ItemStack onCursor = e.getCursor();
                if (onCursor != null
                        && ConfigValue.anti_chest_materials.contains(onCursor.getType())
                        && ToolSwordHelper.isNotToIgnore(onCursor)) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e) {
        final ItemStack dragged = e.getOldCursor();
        if (ConfigValue.anti_chest_materials.contains(dragged.getType())
                && inArena((Player) e.getWhoClicked())
                && ToolSwordHelper.isNotToIgnore(dragged)) {

            final int inventorySize = e.getInventory().getSize();

            for (int i : e.getRawSlots()) {
                if (i < inventorySize) {
                    e.setCancelled(true);
                    break;
                }
            }
        }
    }

    private boolean inArena(Player p){
        return BedwarsAPI.getGameAPI().getArenaByPlayer(p) != null;
    }
}
