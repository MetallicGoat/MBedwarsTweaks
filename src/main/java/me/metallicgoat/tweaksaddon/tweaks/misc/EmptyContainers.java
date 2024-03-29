package me.metallicgoat.tweaksaddon.tweaks.misc;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.Arena;
import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;
import me.metallicgoat.tweaksaddon.config.MainConfig;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

public class EmptyContainers implements Listener {

  // Keep in mind 1.8 does not have an off-hand
  @EventHandler
  public void onBucketEmpty(PlayerBucketEmptyEvent event) {
    if (!MainConfig.remove_empty_buckets)
      return;

    final Player player = event.getPlayer();
    final Arena arena = BedwarsAPI.getGameAPI().getArenaByPlayer(player);

    if (arena == null)
      return;

    event.getItemStack().setType(Material.AIR);
  }

  @EventHandler
  public void onConsume(PlayerItemConsumeEvent event) {
    if (!MainConfig.remove_empty_potions || event.getItem().getType() != Material.POTION)
      return;

    final Player player = event.getPlayer();
    final Arena arena = BedwarsAPI.getGameAPI().getArenaByPlayer(player);

    if (arena == null)
      return;

    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(MBedwarsTweaksPlugin.getInstance(), () ->
        event.getPlayer().getInventory().removeItem(new ItemStack(Material.GLASS_BOTTLE)), 1);
  }
}