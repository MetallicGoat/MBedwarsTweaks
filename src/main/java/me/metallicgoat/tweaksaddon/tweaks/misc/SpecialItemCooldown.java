package me.metallicgoat.tweaksaddon.tweaks.misc;

import de.marcely.bedwars.api.GameAPI;
import de.marcely.bedwars.api.event.player.PlayerUseSpecialItemEvent;
import java.util.UUID;
import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;
import me.metallicgoat.tweaksaddon.config.MainConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.IdentityHashMap;
import java.util.Map;
import org.bukkit.event.player.PlayerJoinEvent;

public class SpecialItemCooldown implements Listener {

  private final Map<UUID, String> cooldownPlayers = new IdentityHashMap<>();

  @EventHandler(priority = EventPriority.LOW)
  public void onSpecialItemUse(PlayerUseSpecialItemEvent event) {
    final UUID uuid = event.getPlayer().getUniqueId();
    final String itemId = event.getSpecialItem().getId();
    final Map<String, Integer> customCooldownMap = MainConfig.special_items_custom_cooldowns;


    if (customCooldownMap.containsKey(itemId)) {
      for (String id : customCooldownMap.keySet()) {
        if (itemId.equalsIgnoreCase(id)) {

          if (cooldownPlayers.containsKey(uuid) && cooldownPlayers.get(uuid).equalsIgnoreCase(id)) {
            event.setCancelled(true);
            return;
          }

          cooldownPlayers.put(uuid, id);
          removeFromMapAfter(cooldownPlayers, uuid, customCooldownMap.get(id));
        }
      }
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

  // Helps admins to figure out special item id for the config.
  @EventHandler
  public void onJoin(PlayerJoinEvent event) {
    final Player player = event.getPlayer();
    final StringBuilder builder = new StringBuilder();

    if (!player.isOp() || !MainConfig.special_items_send_id_on_join)
      return;

    GameAPI.get().getSpecialItems().forEach(specialItem -> builder.append(specialItem.getId()).append(","));

    final String message = builder.toString();
    player.sendMessage(message);
  }

  private void removeFromMapAfter(Map<UUID, String> map, UUID uuid, double seconds) {
    Bukkit.getScheduler().runTaskLater(MBedwarsTweaksPlugin.getInstance(), () -> map.remove(uuid), (long) (20D * seconds));
  }
}
