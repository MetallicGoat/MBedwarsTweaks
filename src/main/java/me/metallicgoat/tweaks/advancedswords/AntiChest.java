package me.metallicgoat.tweaks.advancedswords;

import de.marcely.bedwars.api.BedwarsAPI;
import me.metallicgoat.tweaks.utils.ServerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class AntiChest implements Listener {

    @EventHandler
    public void onShiftClick(InventoryClickEvent e) {
        if(e.getInventory().getSize() > 26) {
            if (e.getClick().isShiftClick() && inArena((Player) e.getWhoClicked())) {
                Inventory clicked = e.getClickedInventory();
                if (clicked == e.getWhoClicked().getInventory()) {
                    ItemStack clickedOn = e.getCurrentItem();
                    if (clickedOn != null && (getAntiChest().contains(clickedOn.getType().name())) && ToolSwordHelper.isNotToIgnore(clickedOn)) {
                        e.setCancelled(true);
                    }
                }
            }
        }
    }
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Inventory clicked = e.getClickedInventory();
        if(e.getInventory().getSize() > 26) {
            if (clicked != e.getWhoClicked().getInventory() && inArena((Player) e.getWhoClicked())) {
                // The cursor item is going into the top inventory
                ItemStack onCursor = e.getCursor();
                if (onCursor != null && (getAntiChest().contains(onCursor.getType().name())) && ToolSwordHelper.isNotToIgnore(onCursor)) {
                    e.setCancelled(true);
                }
            }
        }
    }
    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e) {
        ItemStack dragged = e.getOldCursor();
        if (getAntiChest().contains(dragged.getType().name()) && inArena((Player) e.getWhoClicked()) && ToolSwordHelper.isNotToIgnore(dragged)) {
            int inventorySize = e.getInventory().getSize(); // The size of the inventory, for reference
            // Now we go through all the slots and check if the slot is inside our inventory (using the inventory size as reference)
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

    private List<String> getAntiChest() {
        return ServerManager.getSwordsToolsConfig().getStringList("Anti-Chest");
    }
}
