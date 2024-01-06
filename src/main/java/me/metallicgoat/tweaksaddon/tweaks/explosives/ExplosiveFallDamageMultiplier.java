package me.metallicgoat.tweaksaddon.tweaks.explosives;

import de.marcely.bedwars.api.GameAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.event.player.PlayerQuitArenaEvent;
import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;
import me.metallicgoat.tweaksaddon.config.MainConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Explosive;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

public class ExplosiveFallDamageMultiplier implements Listener {

  private final Map<Player, ExplodeInfo> explodedPlayers = new HashMap<>();

  @EventHandler
  public void onDamage(EntityDamageEvent event) {
    if (event.getCause() != EntityDamageEvent.DamageCause.FALL || !(event.getEntity() instanceof Player))
      return;

    final Player player = (Player) event.getEntity();

    if (!this.explodedPlayers.containsKey(player))
      return;

    final ExplodeInfo info = this.explodedPlayers.remove(player);
    final boolean isFireball = info.isFireball;

    info.removeTask.cancel();

    if (isFireball)
      event.setDamage(event.getDamage() * MainConfig.fireball_fall_damage_multiplier);
    else
      event.setDamage(event.getDamage() * MainConfig.tnt_fall_damage_multiplier);
  }

  @EventHandler
  public void onPlayerQuit(PlayerQuitArenaEvent event) {
    final Player player = event.getPlayer();

    if (this.explodedPlayers.containsKey(player))
      this.explodedPlayers.remove(player).removeTask.cancel();
  }

  @EventHandler
  public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
    if (!(event.getDamager() instanceof Explosive) || !(event.getEntity() instanceof Player))
      return;

    final Player player = (Player) event.getEntity();
    final Arena arena = GameAPI.get().getArenaByPlayer(player);
    final boolean isFireBall = event.getDamager().getType() == EntityType.FIREBALL;

    // Arena is null, or feature not enabled
    if (arena == null || (isFireBall && MainConfig.fireball_fall_damage_multiplier == 1) || (!isFireBall && MainConfig.tnt_fall_damage_multiplier == 1))
      return;

    // Reset the timer if its already going
    if (this.explodedPlayers.containsKey(player))
      this.explodedPlayers.remove(player).removeTask.cancel();

    final BukkitTask removeTask = Bukkit.getScheduler().runTaskLater(MBedwarsTweaksPlugin.getInstance(), () -> this.explodedPlayers.remove(player), 8 * 20);
    this.explodedPlayers.put(player, new ExplodeInfo(isFireBall, removeTask));
  }

  private static class ExplodeInfo {
    final boolean isFireball;
    final BukkitTask removeTask;

    ExplodeInfo(boolean isFireball, BukkitTask removeTask) {
      this.isFireball = isFireball;
      this.removeTask = removeTask;
    }
  }
}
