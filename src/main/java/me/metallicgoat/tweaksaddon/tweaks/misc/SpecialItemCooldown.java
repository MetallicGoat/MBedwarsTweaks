package me.metallicgoat.tweaksaddon.tweaks.misc;

import de.marcely.bedwars.api.event.player.PlayerUseSpecialItemEvent;
import java.util.UUID;
import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;
import me.metallicgoat.tweaksaddon.config.MainConfig;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.IdentityHashMap;
import java.util.Map;

public class SpecialItemCooldown implements Listener {

  // Faster to store UUID instead of player
  private final Map<UUID, String> cooldownPlayers = new IdentityHashMap<>();
  private final Map<UUID, String> customCooldownPlayers = new IdentityHashMap<>();

  @EventHandler(priority = EventPriority.LOW)
  public void onSpecialItemUse(PlayerUseSpecialItemEvent event) {
    final UUID uuid = event.getPlayer().getUniqueId();
    final String itemId = event.getSpecialItem().getId();
    final Map<String, Integer> customCooldownMap = MainConfig.special_items_custom_cooldowns;

    // LOGIC: All 'id', 'itemId', customCooldownPlayers.get() should be equal in case of
    // found in cooldown. in that case we just cancel the event
    // if itemId and id are equal but customCooldownPlayers does not contain key/value then we just
    // add them to the custom cooldown map to check from next time.
    if (customCooldownMap.containsKey(itemId)) {
      for (String id : customCooldownMap.keySet()) {
        System.out.println(id);
        // Custom item setting matched
        if (itemId.equalsIgnoreCase(id)) {

          if (customCooldownPlayers.containsKey(uuid) && customCooldownPlayers.get(uuid).equalsIgnoreCase(id)) {
            event.setCancelled(true);
            return;
          }
          customCooldownPlayers.put(uuid, id);
          removeFromMapAfter(customCooldownPlayers, uuid, customCooldownMap.get(id));
        }
      }
      // Don't wanna mess with the default cooldowns if there is a custom one.
      return;
    }

    if (MainConfig.special_items_cooldown == 0)
      return;

    if (cooldownPlayers.containsKey(uuid) && itemId.equals(cooldownPlayers.get(uuid))) {
      event.setCancelled(true);
      return;
    }

    cooldownPlayers.put(uuid, itemId);
    removeFromMapAfter(cooldownPlayers, uuid, MainConfig.special_items_cooldown);
  }

  // Easier to call twice
  private void removeFromMapAfter(Map<UUID, String> map, UUID uuid, double seconds) {
    Bukkit.getScheduler().runTaskLater(MBedwarsTweaksPlugin.getInstance(), () -> map.remove(uuid), (long) (20D * seconds));
  }
}
