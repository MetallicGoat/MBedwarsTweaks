package me.metallicgoat.tweaksaddon.tweaks.misc;

import de.marcely.bedwars.api.event.player.PlayerUseSpecialItemEvent;
import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;
import me.metallicgoat.tweaksaddon.config.MainConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.IdentityHashMap;
import java.util.Map;

public class SpecialItemCooldown implements Listener {

  private final Map<Player, String> cooldownPlayers = new IdentityHashMap<>();

  @EventHandler (priority = EventPriority.LOW)
  public void onSpecialItemUse(PlayerUseSpecialItemEvent event) {
    if (MainConfig.special_items_cooldown == 0)
      return;

    final String cooldownItemId =  cooldownPlayers.get(event.getPlayer());
    final String itemId = event.getSpecialItem().getId();

    if (cooldownItemId != null && event.getSpecialItem().getId().equals(cooldownItemId)) {
      event.setCancelled(true);
      return;
    }

    cooldownPlayers.put(event.getPlayer(), itemId);

    Bukkit.getScheduler().runTaskLater(MBedwarsTweaksPlugin.getInstance(), () -> {
      cooldownPlayers.remove(event.getPlayer());
    }, (long) (20D * MainConfig.special_items_cooldown));
  }
}
