package me.metallicgoat.tweaksaddon.gentiers.dragons;

import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.Team;
import de.marcely.bedwars.api.event.arena.ArenaStatusChangeEvent;
import de.marcely.bedwars.api.event.arena.ArenaUnloadEvent;
import de.marcely.bedwars.tools.Helper;
import de.marcely.bedwars.tools.NMSHelper;
import de.marcely.bedwars.tools.Validate;
import de.marcely.bedwars.tools.location.XYZ;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import lombok.Getter;
import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;
import me.metallicgoat.tweaksaddon.api.events.gentiers.SuddenDeathDragonTargetEvent;
import me.metallicgoat.tweaksaddon.api.gentiers.SuddenDeathDragon;
import me.metallicgoat.tweaksaddon.config.MainConfig;
import me.metallicgoat.tweaksaddon.utils.Console;
import me.metallicgoat.tweaksaddon.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCreatePortalEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;


public class SuddenDeathDragonImpl extends BukkitRunnable implements SuddenDeathDragon, Listener {

  private static final Random random = new Random();
  private static boolean fireballWarningPrinted = false;

  // Internal tuning, not exposed in the config
  private static final double ARRIVAL_RADIUS_SQUARED = 100;
  private static final double LEASH_MULTIPLIER = 1.2;
  private static final double ENTITY_LEASH_BONUS = 50;
  private static final double AIM_OFFSET_Y = 1.4;
  private static final int STATIC_TARGET_CHANCE = 75; // % base/generator, otherwise a random spot

  // Chase cycle: climb above the player, dive through them, circle them
  private static final int CHASE_CLIMB_TICKS = 40;
  private static final int CHASE_DIVE_TICKS = 30;
  private static final int CHASE_ORBIT_TICKS = 30;
  private static final int CHASE_CYCLE_TICKS = CHASE_CLIMB_TICKS + CHASE_DIVE_TICKS + CHASE_ORBIT_TICKS;
  private static final double CHASE_CLIMB_HEIGHT = 10;
  private static final double CHASE_ORBIT_RADIUS = 8;
  private static final double CHASE_ORBIT_STEP = 0.12; // radians/tick

  // Sky base detection (dragon_prefer_high_players)
  private static final double SKY_HEIGHT_THRESHOLD = 12; // blocks above the arena floor
  private static final double SKY_WEIGHT = 3.0;
  private static final int FLOATING_CHECK_DEPTH = 6; // blocks to look down for solid ground
  private static final double FLOATING_WEIGHT = 2.0;

  // Knockback
  private static final double KNOCKBACK_RADIUS_SQUARED = 16;
  private static final double KNOCKBACK_STRENGTH = 1.1;
  private static final double KNOCKBACK_UPWARD = 0.45;
  private static final int KNOCKBACK_COOLDOWN_TICKS = 30;

  private final Vector velocity = new Vector(0, 0, 0);
  private final List<Location> defaultTargets;
  private final double arenaFloorY;
  private final Map<UUID, Integer> knockbackCooldowns = new HashMap<>();

  @Getter
  final Arena arena;
  @Getter
  final World world;
  @Getter
  private final EnderDragon dragon;
  @Nullable
  @Getter
  private final Team team;

  private Listener portalListener;
  @Nullable
  private DragonFireballAttack fireballAttack;

  private Entity currEntityTarget = null;
  private Location currDefaultTarget = null;
  private double distanceToTarget = 0;
  private double distanceTraveled = 0;
  private int ticksOnTarget = 0;
  private boolean reachedTarget = false;
  private Location lastDestroyCenter;

  private SuddenDeathDragonImpl(EnderDragon dragon, List<Location> defaultTargets, double arenaFloorY, Arena arena, World world, @Nullable Team team) {
    this.dragon = dragon;
    this.defaultTargets = defaultTargets;
    this.arenaFloorY = arenaFloorY;
    this.arena = arena;
    this.world = world;
    this.team = team;
  }

  public static void createNewDragon(Arena arena, @Nullable Team team, Location arenaMiddle) {
    final World world = arena.getGameWorld();

    if (world == null)
      throw new RuntimeException("Sudden death dragon tried to spawn in an arena with no game world?!?!?!? WTF how did we get here in life?");

    Location location = DragonUtil.getDragonSpawn(arena, world, team);

    // Just spawn at the middle otherwise
    if (location == null)
      location = arenaMiddle;

    final EnderDragon dragon = (EnderDragon) world.spawnEntity(location, EntityType.ENDER_DRAGON);

    final SuddenDeathDragonImpl task = new SuddenDeathDragonImpl(
        dragon,
        DragonUtil.getRelevantStaticTargets(arena, team, world),
        findArenaFloorY(arena, world, location),
        arena,
        world,
        team
    );

    // Listeners to prevent dragons from creating portals (Changed after 1.8.8)
    if (NMSHelper.get().getVersion() >= 9)
      task.portalListener = new ModernPortalListener(task);
    else
      task.portalListener = new LegacyPortalListener(task);

    // DragonFireball only exists on 1.9+
    if (MainConfig.dragon_fireballs) {
      if (NMSHelper.get().getVersion() >= 9) {
        task.fireballAttack = new DragonFireballAttack(task);

      } else if (!fireballWarningPrinted) {
        fireballWarningPrinted = true;
        Console.printWarn("Dragon-Fireballs is enabled, but dragon fireballs only exist on Minecraft 1.9 and newer. Ignoring it.");
      }
    }

    // Register events for this dragon
    Bukkit.getPluginManager().registerEvents(task, MBedwarsTweaksPlugin.getInstance());
    Bukkit.getPluginManager().registerEvents(task.portalListener, MBedwarsTweaksPlugin.getInstance());

    task.runTaskTimer(MBedwarsTweaksPlugin.getInstance(), 0L, 1L);

    DragonUtil.runningDragons.add(task);
  }

  // Average team spawn height, the arena's ground level
  private static double findArenaFloorY(Arena arena, World world, Location fallback) {
    final List<Location> spawns = Util.getAllTeamSpawns(arena, world, null);

    if (spawns.isEmpty())
      return fallback.getY() - 30;

    double total = 0;

    for (Location spawn : spawns)
      total += spawn.getY();

    return total / spawns.size();
  }

  // We handle this ourselves allow the dragon to break the 'End' blocks
  @EventHandler(priority = EventPriority.LOWEST)
  public void onBlockBreak(EntityExplodeEvent event) {
    if (event.getEntity() != this.dragon)
      return;

    event.setCancelled(true);
  }

  // Dragons never hurt their own team
  @EventHandler(ignoreCancelled = true)
  public void onDamage(EntityDamageByEntityEvent event) {
    if (event.getDamager() != this.dragon || !(event.getEntity() instanceof Player))
      return;

    if (isOwnTeam((Player) event.getEntity()))
      event.setCancelled(true);
  }

  // Kill dragon on round end
  @EventHandler
  public void onArenaStatusChange(ArenaStatusChangeEvent event) {
    if (event.getArena() == this.arena)
      remove();
  }

  @EventHandler
  public void onArenaUnload(ArenaUnloadEvent event) {
    if (event.getArena() == this.arena)
      remove();
  }

  boolean isOwnTeam(Player player) {
    return this.team != null && this.arena.getPlayerTeam(player) == this.team;
  }

  private void updateTarget() {
    final List<Player> playerTargets = getCurrentPlayerTargets();

    final Entity previousEntityTarget = this.currEntityTarget;
    final Location previousLocation = previousEntityTarget == null ? this.currDefaultTarget : null;

    Entity newTargetEntity = null;
    final Location newTargetLocation;

    if (!playerTargets.isEmpty() && random.nextInt(100) < MainConfig.dragon_player_chance) {
      newTargetEntity = MainConfig.dragon_prefer_high_players
          ? pickHighPlayer(playerTargets)
          : playerTargets.get(random.nextInt(playerTargets.size()));
      newTargetLocation = newTargetEntity.getLocation();

    } else if (random.nextInt(100) < STATIC_TARGET_CHANCE) { // base or gen
      newTargetLocation = pickRandomTarget();

    } else { // random cord
      newTargetLocation = generateRandomLocation();
    }

    final SuddenDeathDragonTargetEvent event = new SuddenDeathDragonTargetEvent(
        this.arena,
        this,
        newTargetEntity,
        newTargetLocation,
        previousLocation,
        previousEntityTarget,
        previousEntityTarget != null
    );

    Bukkit.getPluginManager().callEvent(event);

    if (event.isCancelled() || event.getTargetLocation() == null)
      return;

    applyTarget(event.getTargetEntity(), event.getTargetLocation());
  }

  // Switch target and reset the leash
  private void applyTarget(@Nullable Entity entity, Location location) {
    this.currEntityTarget = entity;
    this.currDefaultTarget = location;
    this.distanceTraveled = 0;
    this.ticksOnTarget = 0;
    this.reachedTarget = false;
    this.distanceToTarget = location.distance(this.dragon.getLocation()) + (entity != null ? ENTITY_LEASH_BONUS : 0);
  }

  private List<Player> getCurrentPlayerTargets() {
    final List<Player> targets = new ArrayList<>();

    for (Player player : this.arena.getPlayers()) {
      final Team playerTeam = this.arena.getPlayerTeam(player);

      if (playerTeam != null && playerTeam != this.team && player.getWorld() == this.world)
        targets.add(player);
    }

    // Not the same player twice in a row
    if (targets.size() > 1)
      targets.remove(this.currEntityTarget);

    return targets;
  }

  // Weighted pick favoring high up or floating players
  private Player pickHighPlayer(List<Player> candidates) {
    final Location dragonLocation = this.dragon.getLocation();
    final double[] weights = new double[candidates.size()];
    double total = 0;

    for (int i = 0; i < candidates.size(); i++) {
      final Location location = candidates.get(i).getLocation();
      double weight = 1D / (1D + location.distance(dragonLocation) / 40D); // closer = slightly preferred

      if (location.getY() - this.arenaFloorY > SKY_HEIGHT_THRESHOLD)
        weight *= SKY_WEIGHT;

      if (!hasGroundBelow(location))
        weight *= FLOATING_WEIGHT;

      weights[i] = weight;
      total += weight;
    }

    double roll = random.nextDouble() * total;

    for (int i = 0; i < candidates.size(); i++) {
      roll -= weights[i];

      if (roll <= 0)
        return candidates.get(i);
    }

    return candidates.get(candidates.size() - 1);
  }

  private static boolean hasGroundBelow(Location location) {
    final Block block = location.getBlock();

    for (int i = 0; i <= FLOATING_CHECK_DEPTH; i++)
      if (block.getRelative(0, -i, 0).getType().isSolid())
        return true;

    return false;
  }

  private Location pickRandomTarget() {
    final int size = this.defaultTargets.size();
    int index = random.nextInt(size);

    // Not the same spot twice in a row
    if (size > 1 && this.defaultTargets.get(index).equals(this.currDefaultTarget))
      index = (index + 1 + random.nextInt(size - 1)) % size;

    return this.defaultTargets.get(index).clone();
  }

  // Find a random spot anywhere in the arena
  private Location generateRandomLocation() {
    final XYZ max = this.arena.getMaxRegionCorner();
    final XYZ min = this.arena.getMinRegionCorner();

    if (min != null && max != null) {
      final double xBound = Math.abs(max.getX() - min.getX());
      final double yBound = Math.abs(max.getY() - min.getY());
      final double zBound = Math.abs(max.getZ() - min.getZ());

      // Don't go all the way to the border
      final double x = random.nextInt((int) (xBound * 0.8)) + (xBound * 0.1);
      final double y = random.nextInt((int) (yBound * 0.6)) + (yBound * 0.3); // Shift upwards
      final double z = random.nextInt((int) (zBound * 0.8)) + (zBound * 0.1);

      return new Location(
          this.world,
          Math.min(max.getX(), min.getX()) + x,
          Math.min(max.getY(), min.getY()) + y,
          Math.min(max.getZ(), min.getZ()) + z
      );

    } else { // Find random spot based on arena locations
      final Location target = pickRandomTarget();

      target.add(random.nextInt(120) - 60, random.nextInt(50) - 10, random.nextInt(120) - 60);

      return target;
    }
  }

  // Current entity target, or null once it is gone or invalid
  @Nullable
  private Entity getValidEntityTarget() {
    final Entity entity = this.currEntityTarget;

    if (entity == null || !entity.isValid() || entity.getWorld() != this.world)
      return null;

    if (entity instanceof Player && this.arena.getPlayerTeam((Player) entity) == null)
      return null;

    return entity;
  }

  // Note to self: This works, don't fuck it up
  @Override
  public void run() {
    final Location dragonLocation = this.dragon.getLocation();
    final Entity entityTarget = getValidEntityTarget();
    Location targetLocation = entityTarget != null
        ? entityTarget.getLocation().add(0, AIM_OFFSET_Y, 0)
        : this.currDefaultTarget;

    if (targetLocation == null) {
      updateTarget();
      return;
    }

    this.ticksOnTarget++;

    if (dragonLocation.distanceSquared(targetLocation) < ARRIVAL_RADIUS_SQUARED)
      this.reachedTarget = true;

    if (entityTarget != null && this.ticksOnTarget <= MainConfig.dragon_chase_duration * 20) {
      // Chase timer still running
      targetLocation = getChaseWaypoint(targetLocation);

    } else if (this.reachedTarget || this.distanceTraveled > this.distanceToTarget * LEASH_MULTIPLIER) {
      // The dragon has reached its target + tricks so it does not get in an 'orbit' around the target
      updateTarget();
      return;
    }

    final Vector toTarget = targetLocation.toVector().subtract(dragonLocation.toVector());

    if (toTarget.lengthSquared() < 0.01)
      return;

    // simulate "gravity pull"
    this.velocity.add(toTarget.normalize().multiply(MainConfig.dragon_agility));

    // Do not let dragon infinitely accelerate towards target
    final double maxSpeed = MainConfig.dragon_speed;

    if (this.velocity.lengthSquared() > maxSpeed * maxSpeed)
      this.velocity.normalize().multiply(maxSpeed);

    this.distanceTraveled += this.velocity.length(); // Track how far it has been taking to get to the target

    // Move it! Move it! Move it! (dragons render facing away from their yaw, so face backwards)
    final Location teleportLocation = dragonLocation.add(this.velocity);
    teleportLocation.setDirection(this.velocity.clone().multiply(-1));

    // Only async on paper 1.14.4+
    Helper.get().teleportAsync(this.dragon, teleportLocation, null);

    // normally the dragon would not destroy 'End' blocks
    destroyNearbyBlocks(teleportLocation, MainConfig.dragon_block_destroy_radius);

    if (MainConfig.dragon_knockback)
      knockbackNearbyPlayers(teleportLocation);

    if (this.fireballAttack != null && entityTarget != null)
      this.fireballAttack.tick(entityTarget);
  }

  // Climb, dive, circle, repeat
  private Location getChaseWaypoint(Location chest) {
    final int cycle = this.ticksOnTarget % CHASE_CYCLE_TICKS;
    final double angle = this.ticksOnTarget * CHASE_ORBIT_STEP;
    final double offsetX = Math.cos(angle) * CHASE_ORBIT_RADIUS;
    final double offsetZ = Math.sin(angle) * CHASE_ORBIT_RADIUS;

    if (cycle < CHASE_CLIMB_TICKS)
      return chest.add(offsetX * 0.5, CHASE_CLIMB_HEIGHT, offsetZ * 0.5);

    if (cycle < CHASE_CLIMB_TICKS + CHASE_DIVE_TICKS)
      return chest;

    return chest.add(offsetX, 0, offsetZ);
  }

  // The dragon does not break end blocks by default
  private void destroyNearbyBlocks(Location location, double radius) {
    if (radius <= 0)
      return;

    final Block center = location.getBlock();
    final Location centerLocation = center.getLocation();

    // Same block as last tick, nothing new to clear
    if (centerLocation.equals(this.lastDestroyCenter))
      return;

    this.lastDestroyCenter = centerLocation;

    final int reach = (int) Math.ceil(radius);
    final double radiusSquared = (radius + 0.5) * (radius + 0.5); // padded sphere

    for (int x = -reach; x <= reach; x++) {
      for (int y = -reach; y <= reach; y++) {
        for (int z = -reach; z <= reach; z++) {
          if (x * x + y * y + z * z > radiusSquared)
            continue;

          final Block block = center.getRelative(x, y, z);

          if (block.getType() == Material.AIR || MainConfig.dragon_block_destroy_blacklist.contains(block.getType()))
            continue;

          if (MainConfig.dragon_block_destroy_only_player_placed && !this.arena.isBlockPlayerPlaced(block))
            continue;

          block.setType(Material.AIR, false);
        }
      }
    }
  }

  // Push enemy players near the head away and slightly up
  private void knockbackNearbyPlayers(Location head) {
    final int now = this.dragon.getTicksLived();

    for (Player player : this.arena.getPlayers()) {
      final Team playerTeam = this.arena.getPlayerTeam(player);

      if (playerTeam == null || playerTeam == this.team || player.getWorld() != this.world)
        continue;

      final Location location = player.getLocation();

      if (location.distanceSquared(head) > KNOCKBACK_RADIUS_SQUARED)
        continue;

      final Integer until = this.knockbackCooldowns.get(player.getUniqueId());

      if (until != null && until > now)
        continue;

      this.knockbackCooldowns.put(player.getUniqueId(), now + KNOCKBACK_COOLDOWN_TICKS);

      Vector push = location.toVector().subtract(head.toVector());
      push.setY(0);

      // On the head: push along the flight path
      if (push.lengthSquared() < 0.01)
        push = this.velocity.clone().setY(0);

      if (push.lengthSquared() < 0.01)
        push = new Vector(1, 0, 0);

      player.setVelocity(push.normalize().multiply(KNOCKBACK_STRENGTH).setY(KNOCKBACK_UPWARD));
    }
  }

  @Override
  public Location getDragonTargetLocation() {
    if (this.currEntityTarget != null && this.currEntityTarget.isValid())
      return this.currEntityTarget.getLocation();

    return this.currDefaultTarget;
  }

  @Override
  public void setDragonTarget(Location location) {
    Validate.notNull(location, "location");

    applyTarget(null, location);
  }

  @Override
  public void setDragonTarget(Entity entity) {
    Validate.notNull(entity, "entity");
    Validate.isTrue(entity.isValid(), "Entity must be valid");
    Validate.isTrue(entity.getWorld() == dragon.getWorld(), "Entity must be in the dragon's world");
    Validate.isTrue(this.arena.isInside(entity.getLocation()), "Entity must be inside the arena");

    applyTarget(entity, entity.getLocation());
  }

  @Override
  public void remove() {
    this.dragon.remove();

    // Unregister listeners
    HandlerList.unregisterAll(this.portalListener);
    HandlerList.unregisterAll(this);

    DragonUtil.runningDragons.remove(this);

    // Stop Scheduler
    super.cancel();
  }

  @Override
  public boolean exists() {
    return this.dragon.isValid();
  }


  private static class ModernPortalListener implements Listener {
    final SuddenDeathDragonImpl task;

    ModernPortalListener(SuddenDeathDragonImpl task) {
      this.task = task;
    }

    @EventHandler
    public void onDragonDeath(EntityDeathEvent event) {
      if (event.getEntity() != this.task.dragon)
        return;

      // TODO Find a better way... There might not be
      //  (Possibly remove and use packet to send death effect)
      // Hacky way to remove the dragon so the portal never gets created (gets created at tick 200)
      Bukkit.getScheduler().runTaskLater(MBedwarsTweaksPlugin.getInstance(), this.task::remove, 198L);
    }
  }

  private static class LegacyPortalListener implements Listener {
    final SuddenDeathDragonImpl task;

    LegacyPortalListener(SuddenDeathDragonImpl task) {
      this.task = task;
    }

    // This works for 1.8.8, but got broken with 1.9+
    @EventHandler
    public void onEntityCreatePortalEvent(EntityCreatePortalEvent event) {
      if (event.getEntity() != this.task.dragon)
        return;

      event.setCancelled(true);
    }
  }
}
