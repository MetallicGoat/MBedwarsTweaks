package me.metallicgoat.tweaksaddon.tweaks.misc;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.Arena;
import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;
import me.metallicgoat.tweaksaddon.config.MainConfig;
import me.metallicgoat.tweaksaddon.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

public class EmptyContainers implements Listener {

  @EventHandler
  public void onBucketEmpty(PlayerBucketEmptyEvent event) {
    if (!MainConfig.remove_empty_buckets)
      return;

    final Player player = event.getPlayer();
    final Arena arena = BedwarsAPI.getGameAPI().getArenaByPlayer(player);

    if (arena == null)
      return;

    // Uses isSimilar to remove ItemStacks (weird but works)
    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(MBedwarsTweaksPlugin.getInstance(), () ->
        Util.removePlayerItem(player, new ItemStack(Material.BUCKET)), 1);
  }

  @EventHandler
  public void onConsume(PlayerItemConsumeEvent event) {
    if (!MainConfig.remove_empty_potions || event.getItem().getType() != Material.POTION)
      return;

    final Player player = event.getPlayer();
    final Arena arena = BedwarsAPI.getGameAPI().getArenaByPlayer(player);

    if (arena == null)
      return;

    // Uses isSimilar to remove ItemStacks (weird but works)
    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(MBedwarsTweaksPlugin.getInstance(), () ->
        Util.removePlayerItem(player, new ItemStack(Material.GLASS_BOTTLE)), 1);
  }
}