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
import java.util.List;
import java.util.Random;
import lombok.Getter;
import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;
import me.metallicgoat.tweaksaddon.api.events.gentiers.SuddenDeathDragonTargetEvent;
import me.metallicgoat.tweaksaddon.api.gentiers.SuddenDeathDragon;
import me.metallicgoat.tweaksaddon.config.MainConfig;
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
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;


public class SuddenDeathDragonImpl extends BukkitRunnable implements SuddenDeathDragon, Listener {

  private static final Random random = new Random();

  private final Vector velocity = new Vector(0, 0, 0);
  private final List<Location> defaultTargets;

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
  private boolean targetingEntity = false;
  private Entity currEntityTarget = null;
  private Location playerTargetLocation = null;
  private Location currDefaultTarget = null;
  private double distanceToTarget = 0;
  private double distanceTraveled = 0;

  private SuddenDeathDragonImpl(EnderDragon dragon, List<Location> defaultTargets, Arena arena, World world, @Nullable Team team) {
    this.dragon = dragon;
    this.defaultTargets = defaultTargets;
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
        arena,
        world,
        team
    );

    // Listeners to prevent dragons from creating portals (Changed after 1.8.8)
    if (NMSHelper.get().getVersion() >= 9)
      task.portalListener = new ModernPortalListener(task);
    else
      task.portalListener = new LegacyPortalListener(task);

    // Register events for this dragon
    Bukkit.getPluginManager().registerEvents(task, MBedwarsTweaksPlugin.getInstance());
    Bukkit.getPluginManager().registerEvents(task.portalListener, MBedwarsTweaksPlugin.getInstance());

    task.runTaskTimer(MBedwarsTweaksPlugin.getInstance(), 0L, 1L);

    DragonUtil.runningDragons.add(task);
  }

  // We handle this ourselves allow the dragon to break the 'End' blocks
  @EventHandler(priority = EventPriority.LOWEST)
  public void onBlockBreak(EntityExplodeEvent event) {
    if (event.getEntity() != this.dragon)
      return;

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

  private void updateTarget() {
    final int chanceValue = random.nextInt(100);
    final List<Player> playerTargets = getCurrentPlayerTargets();

    final boolean previouslyTargetingEntity = this.targetingEntity;
    final Entity previousEntityTarget = previouslyTargetingEntity ? this.currEntityTarget : null;
    final Location previousLocation = !previouslyTargetingEntity ? this.currDefaultTarget : null;

    final double distanceToTarget;
    final Entity newTargetEntity;
    final Location newTargetLocation;
    Location newEntityTargetLocation = null;


    // Try to target a random player
    if (!playerTargets.isEmpty() && chanceValue < Math.min(60, playerTargets.size() * 25)) {
      newTargetEntity = playerTargets.get(random.nextInt(playerTargets.size()));
      newEntityTargetLocation = newTargetEntity.getLocation();
      distanceToTarget = newEntityTargetLocation.distance(this.dragon.getLocation()) + 50;

    } else {
      if (chanceValue < 90) // base or gen
        newTargetLocation = pickRandomTarget();
      else // random cord
        newTargetLocation = generateRandomLocation();

      distanceToTarget = newTargetLocation.distance(this.dragon.getLocation());
    }

    final SuddenDeathDragonTargetEvent event = new SuddenDeathDragonTargetEvent(
        this.arena,
        this,
        this.currEntityTarget,
        this.currDefaultTarget,
        previousLocation,
        previousEntityTarget,
        previouslyTargetingEntity
    );

    Bukkit.getPluginManager().callEvent(event);

    if (!event.isCancelled()) {
      this.distanceTraveled = 0;
      this.distanceToTarget = distanceToTarget;
      this.targetingEntity = event.getTargetEntity() != null;
      this.currDefaultTarget = event.getTargetLocation();
      this.playerTargetLocation = newEntityTargetLocation;
    }
  }

  private List<Player> getCurrentPlayerTargets() {
    final List<Player> locations = new ArrayList<>();

    for (Player player : this.arena.getPlayers())
      if (player != this.currEntityTarget && this.arena.getPlayerTeam(player) != this.team && !this.arena.getSpectators().contains(player))
        locations.add(player);

    return locations;
  }

  private Location pickRandomTarget() {
    final List<Location> targets = new ArrayList<>(this.defaultTargets);

    if (currDefaultTarget != null)
      targets.remove(currDefaultTarget);

    final Location newTarget = targets.get(random.nextInt(targets.size())).clone();
    this.currDefaultTarget = newTarget;

    return newTarget;
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

  // Note to self: This works, don't fuck it up
  @Override
  public void run() {
    final Location dragonLocation = this.dragon.getLocation().clone();
    Location targetLocation;

    if (this.targetingEntity) {
      if (this.currEntityTarget.isValid() && this.currEntityTarget.getWorld() == this.world
          && (this.currEntityTarget.getType() != EntityType.PLAYER
          || (arena.getPlayers().contains((Player) this.currEntityTarget)
          && !arena.getSpectators().contains((Player) this.currEntityTarget))))

        targetLocation = this.currEntityTarget.getLocation();
      else
        targetLocation = this.playerTargetLocation;

    } else {
      targetLocation = this.currDefaultTarget;
    }

    // The dragon has reached its target + tricks so it does not get in an 'orbit' around the target
    if (targetLocation == null || dragonLocation.distanceSquared(targetLocation) < 100 || this.distanceTraveled > this.distanceToTarget * 1.2) {
      updateTarget();
      return;
    }

    targetLocation = targetLocation.clone();

    final Vector toTarget = targetLocation.subtract(dragonLocation).toVector().normalize();
    final Vector gravity = toTarget.multiply(0.05); // More big = More Gravity

    // simulate "gravity pull"
    this.velocity.add(gravity);

    // Do not let dragon infinitely accelerate towards target
    final double maxSpeed = MainConfig.dragon_speed;

    if (this.velocity.length() > maxSpeed)
      this.velocity.normalize().multiply(maxSpeed);

    final Location teleportLocation = dragonLocation.add(this.velocity);
    this.distanceTraveled += this.velocity.length(); // Track how far it has been taking to get to the target

    // Move it! Move it! Move it!
    teleportLocation.setDirection(this.dragon.getLocation().clone().subtract(teleportLocation).toVector());

    // Only async on paper 1.14.4+
    Helper.get().teleportAsync(this.dragon, teleportLocation, null);

    // normally the dragon would not destroy 'End' blocks
    destroyNearbyBlocks(this.dragon.getLocation(), MainConfig.dragon_block_destroy_radius);
  }

  // The dragon does not break end blocks by default
  private void destroyNearbyBlocks(Location location, double radius) {
    final double blockX = location.getBlockX() + 0.5;
    final double blockY = location.getBlockY() + 0.5;
    final double blockZ = location.getBlockZ() + 0.5;

    for (double x = blockX - radius; x <= blockX + radius; x++) {
      for (double y = blockY - radius; y <= blockY + radius; y++) {
        for (double z = blockZ - radius; z <= blockZ + radius; z++) {
          final Block block = new Location(location.getWorld(), x, y, z).getBlock();

          if (block.getType() != Material.AIR)
            block.setType(Material.AIR, false);
        }
      }
    }
  }

  @Override
  public Location getDragonTargetLocation() {
    return this.currDefaultTarget;
  }

  @Override
  public void setDragonTarget(Location location) {
    Validate.notNull(location, "location");

    this.targetingEntity = false;
    this.currDefaultTarget = location;
  }

  @Override
  public void setDragonTarget(Entity entity) {
    Validate.notNull(entity, "entity");
    Validate.isTrue(entity.isValid(), "Entity must be valid");
    Validate.isTrue(entity.getWorld() == dragon.getWorld(), "Entity must be in the dragon's world");
    Validate.isTrue(this.arena.isInside(entity.getLocation()), "Entity must be inside the arena");

    this.targetingEntity = true;
    this.currEntityTarget = entity;
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
