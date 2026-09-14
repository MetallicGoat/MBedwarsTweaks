package me.metallicgoat.tweaksaddon.gentiers.dragons;

import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.DragonFireball;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.AreaEffectCloudApplyEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

/**
 * Shoots dragon fireballs at players well above the dragon.
 * Uses 1.9+ types, never load this class on older servers.
 */
public class DragonFireballAttack {

  private static final int COOLDOWN_TICKS = 80;
  private static final double RANGE_SQUARED = 45 * 45;
  private static final double MIN_HEIGHT_ABOVE = 6; // only shoot at targets this far above the dragon
  private static final double LAUNCH_OFFSET = 8; // spawn clear of the dragon's hitboxes
  private static final int BREATH_DURATION_TICKS = 120;
  private static final float BREATH_RADIUS = 3F;

  private static boolean eventsRegistered = false;

  private final SuddenDeathDragonImpl owner;
  private int cooldown = 0;

  DragonFireballAttack(SuddenDeathDragonImpl owner) {
    this.owner = owner;

    if (!eventsRegistered) {
      eventsRegistered = true;
      Bukkit.getPluginManager().registerEvents(new Events(), MBedwarsTweaksPlugin.getInstance());
    }
  }

  void tick(Entity target) {
    if (this.cooldown > 0) {
      this.cooldown--;
      return;
    }

    final EnderDragon dragon = this.owner.getDragon();
    final Location from = dragon.getLocation();
    final Location to = target.getLocation().add(0, 1, 0);

    if (to.getY() - from.getY() < MIN_HEIGHT_ABOVE || from.distanceSquared(to) > RANGE_SQUARED)
      return;

    final Vector direction = to.toVector().subtract(from.toVector());

    if (direction.lengthSquared() < LAUNCH_OFFSET * LAUNCH_OFFSET)
      return; // too close

    direction.normalize();

    final Location spawnAt = from.add(direction.clone().multiply(LAUNCH_OFFSET));
    final DragonFireball fireball = spawnAt.getWorld().spawn(spawnAt, DragonFireball.class);

    fireball.setShooter(dragon);
    fireball.setDirection(direction); // fireballs accelerate along this and ignore setVelocity

    this.cooldown = COOLDOWN_TICKS;
  }

  // The running dragon that shot something, if any
  @Nullable
  private static SuddenDeathDragonImpl ownerOf(@Nullable ProjectileSource shooter) {
    if (!(shooter instanceof EnderDragon))
      return null;

    for (SuddenDeathDragonImpl dragon : DragonUtil.runningDragons)
      if (dragon.getDragon() == shooter)
        return dragon;

    return null;
  }

  // One listener shared by every dragon
  private static class Events implements Listener {

    // Vanilla breath clouds last 30s and grow, shrink them
    @EventHandler
    public void onFireballHit(ProjectileHitEvent event) {
      if (!(event.getEntity() instanceof DragonFireball))
        return;

      final ProjectileSource shooter = event.getEntity().getShooter();

      if (ownerOf(shooter) == null)
        return;

      final Location location = event.getEntity().getLocation();

      // The cloud is spawned right after this event
      Bukkit.getScheduler().runTask(MBedwarsTweaksPlugin.getInstance(), () -> {
        for (Entity entity : location.getWorld().getNearbyEntities(location, 3, 3, 3)) {
          if (!(entity instanceof AreaEffectCloud) || ((AreaEffectCloud) entity).getSource() != shooter)
            continue;

          final AreaEffectCloud cloud = (AreaEffectCloud) entity;

          cloud.setRadius(BREATH_RADIUS);
          cloud.setRadiusPerTick(0F);
          cloud.setDuration(BREATH_DURATION_TICKS);
        }
      });
    }

    // Keep the dragon's own team out of its breath clouds
    @EventHandler
    public void onCloudApply(AreaEffectCloudApplyEvent event) {
      final SuddenDeathDragonImpl owner = ownerOf(event.getEntity().getSource());

      if (owner == null)
        return;

      event.getAffectedEntities().removeIf(entity -> entity instanceof Player && owner.isOwnTeam((Player) entity));
    }
  }
}
