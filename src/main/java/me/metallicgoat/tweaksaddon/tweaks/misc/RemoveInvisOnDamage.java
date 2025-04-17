package me.metallicgoat.tweaksaddon.tweaks.misc;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.GameAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.Team;
import java.util.UUID;
import me.metallicgoat.tweaksaddon.config.MainConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
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

      // Is the damage what we be looking for???
      if (arena != null && MainConfig.remove_invis_damge_causes.contains(event.getCause())) {
        boolean damagedCausedByTeam = false;

        // Is the damage from us or a team member???
        if (event instanceof EntityDamageByEntityEvent) {
          final EntityDamageByEntityEvent damageByEntityEvent = (EntityDamageByEntityEvent) event;
          final Entity damager = damageByEntityEvent.getDamager();

          // Get the owner of the fireball/tnt/gaurddog etc
          final UUID uuid = GameAPI.get().getCompanionOwner(damager);
          final Player damagerOwner = uuid != null ? Bukkit.getPlayer(uuid) : null;
          final Arena damagerArena = damagerOwner != null ? BedwarsAPI.getGameAPI().getArenaByPlayer(damagerOwner) : null;
          final Team damagerTeam = damagerArena != null ? damagerArena.getPlayerTeam(damagerOwner) : null;

          if (arena.getPlayerTeam(player) == damagerTeam) {
            damagedCausedByTeam = true;
          }
        }

        // Only remove if caused by non-team member
        if (!damagedCausedByTeam) {
          player.removePotionEffect(PotionEffectType.INVISIBILITY);
        }
      }
    }
  }
}
