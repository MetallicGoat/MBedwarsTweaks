package me.metallicgoat.tweaksaddon.tweaks.misc;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.Team;
import de.marcely.bedwars.tools.PersistentBlockData;
import me.metallicgoat.tweaksaddon.config.MainConfig;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PlaceBlocksOnBed implements Listener {

  @EventHandler
  public void onBlockPlace(PlayerInteractEvent event) {
    if (!MainConfig.allow_block_place_on_bed || event.getAction() != Action.RIGHT_CLICK_BLOCK || !event.isBlockInHand())
      return;

    final Player player = event.getPlayer();
    final ItemStack itemStack = event.getItem();
    final Arena arena = BedwarsAPI.getGameAPI().getArenaByPlayer(player);
    final Team team = arena != null ? arena.getPlayerTeam(player) : null;
    final Block block = event.getClickedBlock();
    final BlockFace face = event.getBlockFace();

    if (arena == null ||
        itemStack == null ||
        !itemStack.getType().isBlock() ||
        block == null ||
        face == null ||
        team == null ||
        !block.getType().name().contains("BED"))
      return;

    final Block placeBlockLoc = block.getRelative(face);

    if (placeBlockLoc.getType() != Material.AIR || !arena.canPlaceBlockAt(placeBlockLoc))
      return;

    // Dye the block (1.8 -> 1.12 support)
    final PersistentBlockData data = PersistentBlockData.fromMaterial(itemStack.getType());

    // Place it
    data.getDyedData(team.getDyeColor()).place(placeBlockLoc, true);
    arena.setBlockPlayerPlaced(placeBlockLoc, true);
    itemStack.setAmount(itemStack.getAmount() - 1); // take item, as it not an actual event

    // Calls the bukkit event (for MBedwars & other plugins/addons)
    final BlockPlaceEvent placeEvent = new BlockPlaceEvent(placeBlockLoc, placeBlockLoc.getState(), block, itemStack, player, true);
    Bukkit.getPluginManager().callEvent(placeEvent);
  }
}
