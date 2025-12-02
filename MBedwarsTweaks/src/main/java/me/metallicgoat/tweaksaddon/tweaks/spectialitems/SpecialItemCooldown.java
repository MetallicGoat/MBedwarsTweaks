package me.metallicgoat.tweaksaddon.tweaks.spectialitems;

import de.marcely.bedwars.api.event.player.PlayerUseSpecialItemEvent;
import de.marcely.bedwars.tools.NMSHelper;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
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

  private final Map<CooldownEntry, Instant> cooldowns = new HashMap<>();

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
    if (customCooldown <= 0D)
      return;

    final CooldownEntry entry = new CooldownEntry(uuid, itemId);
    final Instant now = Instant.now();

    // Check existing cooldown
    // Use instants to avoid issues with low TPS
    {
      final Instant elapsesTime = this.cooldowns.computeIfPresent(entry, (g0, time) -> {
        return now.isAfter(time) ? null : time;
      });

      if (elapsesTime != null) {
        event.setCancelled(true);
        return;
      }
    }

    // Apply coolown
    final Instant elapsesTime = now.plusMillis((long) (customCooldown * 1000D));

    this.cooldowns.put(entry, elapsesTime);
    setVisualCooldown(player, event.getSpecialItem().getItemStack(), customCooldown);

    Bukkit.getScheduler().runTaskLater(MBedwarsTweaksPlugin.getInstance(), () ->
        this.cooldowns.remove(entry, elapsesTime /* important */),
      (long) (20D * customCooldown));
  }

  private static void setVisualCooldown(Player player, ItemStack itemStack, double cooldownSeconds) {
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


  @AllArgsConstructor
  @EqualsAndHashCode
  private static class CooldownEntry {

    private final UUID playerUUID;
    private final String specialItemId;
  }
}
