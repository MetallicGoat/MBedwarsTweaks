package me.metallicgoat.tweaksaddon.tweaks.misc;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.ArenaStatus;
import de.marcely.bedwars.api.arena.Team;
import de.marcely.bedwars.api.event.arena.ArenaDeleteEvent;
import de.marcely.bedwars.api.event.arena.ArenaStatusChangeEvent;
import de.marcely.bedwars.api.event.arena.RoundEndEvent;
import de.marcely.bedwars.api.event.arena.RoundStartEvent;
import de.marcely.bedwars.api.message.Message;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import me.metallicgoat.tweaksaddon.config.MainConfig;
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

public class PersonalChests implements Listener {

  private static final Map<Arena, List<Inventory>> inventoryArenaHashMap = new IdentityHashMap<>();
  private static final Map<Player, Block> openChests = new IdentityHashMap<>();

  @EventHandler
  public void onRoundStart(RoundStartEvent event) {
    if (!MainConfig.personal_ender_chests_enabled)
      return;

    final Arena arena = event.getArena();
    final List<Inventory> inventories = new ArrayList<>();

    for (Player player : arena.getPlayers()) {
      final Team team = arena.getPlayerTeam(player);
      final String teamName = team != null ? ChatColor.stripColor(team.getDisplayName()) : "";
      final String teamColor = team != null ? String.valueOf(team.getBungeeChatColor()) : "";
      final String chestName = Message.build(MainConfig.personal_ender_chests_name)
          .placeholder("team-name", teamName)
          .placeholder("team-color", teamColor)
          .done();

      inventories.add(Bukkit.createInventory(player, 27, chestName));
    }

    inventoryArenaHashMap.put(arena, inventories);
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onArenaStatusChangeEvent(ArenaStatusChangeEvent event) {
    if (event.getOldStatus() == ArenaStatus.RUNNING)
      removeArena(event.getArena());
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onArenaDeleteEvent(ArenaDeleteEvent event) {
    removeArena(event.getArena());
  }

  private void removeArena(Arena arena) {
    inventoryArenaHashMap.remove(arena);
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onChestOpen(PlayerInteractEvent event) {
    if (!MainConfig.personal_ender_chests_enabled)
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
    final Block openBlock = openChests.remove(player);

    if (openBlock != null)
      BedwarsAPI.getNMSHelper().simulateChestClosing(openBlock);
  }
}
