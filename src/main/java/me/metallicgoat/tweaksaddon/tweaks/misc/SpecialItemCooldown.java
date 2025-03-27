package me.metallicgoat.tweaksaddon.tweaks.misc;

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
import org.bukkit.inventory.ItemStack;

public class SpecialItemCooldown implements Listener {

  private final Map<UUID, String> cooldownPlayers = new IdentityHashMap<>();

  @EventHandler(priority = EventPriority.LOW)
  public void onSpecialItemUse(PlayerUseSpecialItemEvent event) {
    final Player player = event.getPlayer();
    final UUID uuid = player.getUniqueId();
    final String itemId = event.getSpecialItem().getId();

    // Try item specific value
    Double customCooldown = MainConfig.special_items_custom_cooldowns.get(itemId);

    // Fall back on default value
    if (customCooldown == null)
      customCooldown = MainConfig.special_items_cooldown;

    // No cooldown
    if (MainConfig.special_items_cooldown == 0)
      return;

    // Cooldown active - Stop event
    if (this.cooldownPlayers.containsKey(uuid) && itemId.equals(this.cooldownPlayers.get(uuid))) {
      event.setCancelled(true);
      return;
    }

    // Apply cooldown
    this.cooldownPlayers.put(uuid, itemId);
    setVisualCooldown(player, event.getSpecialItem().getItemStack(), customCooldown);

    Bukkit.getScheduler().runTaskLater(MBedwarsTweaksPlugin.getInstance(), () ->
        this.cooldownPlayers.remove(uuid), (long) (20D * customCooldown)
    );
  }

  private void setVisualCooldown(Player player, ItemStack itemStack, double cooldownSeconds) {
    if (NMSHelper.get().getVersion() >= 12) {
      try {
        final int cooldownTicks = (int) (cooldownSeconds * 20);

        HumanEntity.class.getMethod("setCooldown", Material.class, int.class).invoke(
            player,
            itemStack.getType(),
            cooldownTicks
        );

      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
