package me.metallicgoat.tweaks.tweaks.misc;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.ArenaStatus;
import de.marcely.bedwars.api.event.arena.RoundEndEvent;
import de.marcely.bedwars.api.event.arena.RoundStartEvent;
import me.metallicgoat.tweaks.utils.ServerManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

import java.util.Collections;
import java.util.HashMap;

public class PersonalChests implements Listener {

    private static final HashMap<Inventory, Arena> inventoryArenaHashMap = new HashMap<>();
    private static final HashMap<Player, Block> openChests = new HashMap<>();

    @EventHandler
    public void onRoundStart(RoundStartEvent e){
        final Arena arena = e.getArena();
        for(Player player : arena.getPlayers()){
            Inventory inventory = Bukkit.createInventory(player, 27, "Ender Chest");
            inventoryArenaHashMap.put(inventory, arena);
        }
    }

    @EventHandler
    public void onRoundEnd(RoundEndEvent e){
        inventoryArenaHashMap.values().removeAll(Collections.singleton(e.getArena()));
    }


    @EventHandler(priority = EventPriority.HIGH)
    public void onChestOpen(PlayerInteractEvent e){
        Player player = e.getPlayer();
        Arena arena = BedwarsAPI.getGameAPI().getArenaByPlayer(player);
        Block block = e.getClickedBlock();

        if(ServerManager.getConfig().getBoolean("Personal-Ender-Chests")) {
            //Check if player is opening chest in an arena
            if (arena == null || block == null ||
                    arena.getStatus() != ArenaStatus.RUNNING ||
                    e.getAction() != Action.RIGHT_CLICK_BLOCK)
                return;

            if (block.getType() == Material.ENDER_CHEST) {
                inventoryArenaHashMap.forEach((inventory, arena1) -> {
                    if(inventory.getHolder() == player){
                        player.openInventory(inventory);
                        BedwarsAPI.getNMSHelper().simulateChestOpening(block);
                        openChests.put(player, block);
                    }
                });
            }
        }
    }
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e){
        final Player player = (Player) e.getPlayer();
        if(openChests.containsKey(player)){
            BedwarsAPI.getNMSHelper().simulateChestClosing(openChests.get(player));
            openChests.remove(player);
        }
    }
}
