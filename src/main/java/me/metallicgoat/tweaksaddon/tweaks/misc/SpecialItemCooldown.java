package me.metallicgoat.tweaksaddon.tweaks.misc;

import de.marcely.bedwars.api.event.player.PlayerUseSpecialItemEvent;
import de.marcely.bedwars.tools.NMSHelper;
import java.util.UUID;
import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;
import me.metallicgoat.tweaksaddon.config.MainConfig;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.IdentityHashMap;
import java.util.Map;
import org.bukkit.inventory.ItemStack;

public class SpecialItemCooldown implements Listener {

  private final Map<UUID, String> cooldownPlayers = new IdentityHashMap<>();

  @EventHandler(priority = EventPriority.LOW)
  public void onSpecialItemUse(PlayerUseSpecialItemEvent event) {
    final Player player = event.getPlayer();
    final UUID uuid = player.getUniqueId();
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
          setVisualCooldown(player, event.getSpecialItem().getItemStack(), 20 * customCooldownMap.get(id));
        }
      }
      return;
    }

    if (MainConfig.special_items_cooldown == 0) return;

    if (cooldownPlayers.containsKey(uuid) && itemId.equals(cooldownPlayers.get(uuid))) {
      event.setCancelled(true);
      return;
    }

    cooldownPlayers.put(uuid, itemId);
    removeFromMapAfter(cooldownPlayers, uuid, MainConfig.special_items_cooldown);
    setVisualCooldown(player, event.getSpecialItem().getItemStack(), (int) (20 * MainConfig.special_items_cooldown));
  }


  private void removeFromMapAfter(Map<UUID, String> map, UUID uuid, double seconds) {
    Bukkit.getScheduler().runTaskLater(MBedwarsTweaksPlugin.getInstance(), () -> map.remove(uuid), (long) (20D * seconds));
  }

  private void setVisualCooldown(Player player, ItemStack itemStack, int cooldownSeconds) {
    if (NMSHelper.get().getVersion() >= 12) {
      try {
        int cooldownTicks = cooldownSeconds * 20;
        HumanEntity.class.getMethod("setCooldown", Material.class, int.class).invoke(player, itemStack.getType(), cooldownTicks);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
