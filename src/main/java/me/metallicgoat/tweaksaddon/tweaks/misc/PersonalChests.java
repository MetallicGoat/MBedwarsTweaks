package me.metallicgoat.tweaksaddon.tweaks.misc;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.ArenaStatus;
import de.marcely.bedwars.api.arena.Team;
import de.marcely.bedwars.api.event.arena.RoundEndEvent;
import de.marcely.bedwars.api.event.arena.RoundStartEvent;
import de.marcely.bedwars.api.message.Message;
import me.metallicgoat.tweaksaddon.config.ConfigValue;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

import java.util.*;

public class PersonalChests implements Listener {

    private static final HashMap<Arena, List<Inventory>> inventoryArenaHashMap = new HashMap<>();
    private static final HashMap<Player, Block> openChests = new HashMap<>();

    @EventHandler
    public void onRoundStart(RoundStartEvent event) {
        if (!ConfigValue.personal_ender_chests_enabled)
            return;

        final Arena arena = event.getArena();
        final List<Inventory> inventories = new ArrayList<>();

        for (Player player : arena.getPlayers()) {
            final Team team = arena.getPlayerTeam(player);
            final String teamName = team != null ? ChatColor.stripColor(team.getDisplayName()) : "";
            final String teamColor = team != null ? "" + team.getBungeeChatColor() : "";
            final String chestName = Message.build(ConfigValue.personal_ender_chests_name)
                    .placeholder("team-name", teamName)
                    .placeholder("team-color", teamColor)
                    .done();

            inventories.add(Bukkit.createInventory(player, 27, chestName));
        }

        inventoryArenaHashMap.put(arena, inventories);
    }

    @EventHandler
    public void onRoundEnd(RoundEndEvent event) {
        inventoryArenaHashMap.remove(event.getArena());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onChestOpen(PlayerInteractEvent event) {
        if (!ConfigValue.personal_ender_chests_enabled)
            return;

        final Player player = event.getPlayer();
        final Arena arena = BedwarsAPI.getGameAPI().getArenaByPlayer(player);
        final Block block = event.getClickedBlock();

        // Check if player is opening chest in an arena
        if (arena == null || block == null ||
                block.getType() != Material.ENDER_CHEST ||
                arena.getStatus() != ArenaStatus.RUNNING ||
                event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        final List<Inventory> inventories = inventoryArenaHashMap.get(arena);

        if (inventories == null)
            return;

        for (Inventory inventory : inventories) {
            if (inventory.getHolder() == player) {
                BedwarsAPI.getNMSHelper().simulateChestOpening(block);
                player.openInventory(inventory);
                openChests.put(player, block);
                break;
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        final Player player = (Player) e.getPlayer();

        if (openChests.containsKey(player)) {
            BedwarsAPI.getNMSHelper().simulateChestClosing(openChests.get(player));
            openChests.remove(player);
        }
    }
}
