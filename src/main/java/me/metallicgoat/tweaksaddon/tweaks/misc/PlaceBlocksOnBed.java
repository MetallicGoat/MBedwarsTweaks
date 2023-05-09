package me.metallicgoat.tweaksaddon.tweaks.misc;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.Arena;
import me.metallicgoat.tweaksaddon.config.MainConfig;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PlaceBlocksOnBed implements Listener {

  // TODO there might be issues with off hand
  @EventHandler
  public void onBlockPlace(PlayerInteractEvent event){
    if(!MainConfig.allow_block_place_on_bed || event.getAction() != Action.RIGHT_CLICK_BLOCK)
      return;

    final Player player = event.getPlayer();
    final Arena arena = BedwarsAPI.getGameAPI().getArenaByPlayer(player);
    final Block block = event.getClickedBlock();
    final BlockFace face = event.getBlockFace();

    if(arena == null || block == null || face == null || !block.getType().name().contains("BED"))
      return;

    final Block placeBlockLoc = block.getRelative(face);
    final ItemStack itemStack = player.getItemInHand();

    if(placeBlockLoc.getType() != Material.AIR || itemStack == null || !itemStack.getType().isBlock())
      return;

    placeBlockLoc.setType(itemStack.getType());
    itemStack.setAmount(itemStack.getAmount() - 1);
  }
}
