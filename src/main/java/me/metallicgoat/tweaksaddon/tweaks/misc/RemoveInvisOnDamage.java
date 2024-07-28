package me.metallicgoat.tweaksaddon.tweaks.misc;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.Arena;
import me.metallicgoat.tweaksaddon.config.MainConfig;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffectType;

public class RemoveInvisOnDamage implements Listener {

  @EventHandler(priority = EventPriority.HIGH)
  public void onEntityDamage(EntityDamageEvent event) {
    if (!MainConfig.remove_invis_ondamage_enabled || event.isCancelled() || event.getDamage() == 0)
      return;

    if (event.getEntity().getType() == EntityType.PLAYER) {
      final Player player = (Player) event.getEntity();
      final Arena arena = BedwarsAPI.getGameAPI().getArenaByPlayer(player);

      if (arena != null && MainConfig.remove_invis_damge_causes.contains(event.getCause()))
        player.removePotionEffect(PotionEffectType.INVISIBILITY);
    }
  }
}
