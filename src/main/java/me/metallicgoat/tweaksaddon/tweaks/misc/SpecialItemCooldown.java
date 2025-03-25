package me.metallicgoat.tweaksaddon.tweaks.misc;

import de.marcely.bedwars.api.GameAPI;
import de.marcely.bedwars.api.event.player.PlayerUseSpecialItemEvent;
import de.marcely.bedwars.tools.NMSHelper;
import java.util.IdentityHashMap;
import java.util.Map;
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
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

public class SpecialItemCooldown implements Listener {

  private final Map<UUID, String> cooldownPlayers = new IdentityHashMap<>();

  @EventHandler(priority = EventPriority.LOW)
  public void onSpecialItemUse(PlayerUseSpecialItemEvent event) {
    final Player player = event.getPlayer();
    final UUID uuid = player.getUniqueId();
    final String itemId = event.getSpecialItem().getId();
    final Map<String, Integer> customCooldownMap = MainConfig.special_items_custom_cooldowns;
    int cooldownSeconds;

    if (customCooldownMap.containsKey(itemId)) {
      cooldownSeconds = customCooldownMap.get(itemId);
      if (cooldownPlayers.containsKey(uuid) && cooldownPlayers.get(uuid).equalsIgnoreCase(itemId)) {
        event.setCancelled(true);
        return;
      }
      cooldownPlayers.put(uuid, itemId);
      removeFromMapAfter(uuid, cooldownSeconds);
      setVisualCooldown(player, event.getSpecialItem().getItemStack(), cooldownSeconds);
      return;
    }

    if (MainConfig.special_items_cooldown == 0)
      return;

    cooldownSeconds = (int) MainConfig.special_items_cooldown;
    if (cooldownPlayers.containsKey(uuid) && itemId.equalsIgnoreCase(cooldownPlayers.get(uuid))) {
      event.setCancelled(true);
      return;
    }

    cooldownPlayers.put(uuid, itemId);
    removeFromMapAfter(uuid, cooldownSeconds);
    setVisualCooldown(player, event.getSpecialItem().getItemStack(), cooldownSeconds);
  }

  private void removeFromMapAfter(UUID uuid, int seconds) {
    Bukkit.getScheduler().runTaskLater(MBedwarsTweaksPlugin.getInstance(),
            () -> cooldownPlayers.remove(uuid), (long) (20D * seconds));
  }

  private void setVisualCooldown(Player player, ItemStack itemStack, int cooldownSeconds) {
    if (NMSHelper.get().getVersion() >= 12) {
      try {
        int cooldownTicks = cooldownSeconds * 20; // 将秒数转换为 tick（20 tick = 1 秒）
        HumanEntity.class.getMethod("setCooldown", Material.class, int.class)
                .invoke(player, itemStack.getType(), cooldownTicks);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
