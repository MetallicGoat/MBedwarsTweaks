package me.metallicgoat.tweaksaddon.tweaks.misc;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.Arena;
import me.metallicgoat.tweaksaddon.config.MainConfig;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffectType;

public class RemoveInvisOnDamage implements Listener {

  @EventHandler
  public void onEntityDamage(EntityDamageEvent e) {
    if (!MainConfig.remove_invis_ondamage_enabled)
      return;

    if (e.getEntity().getType() == EntityType.PLAYER) {
      final Player player = (Player) e.getEntity();
      final Arena arena = BedwarsAPI.getGameAPI().getArenaByPlayer(player);

      if (arena != null && MainConfig.remove_invis_damge_causes.contains(e.getCause()))
        player.removePotionEffect(PotionEffectType.INVISIBILITY);
    }
  }
}
