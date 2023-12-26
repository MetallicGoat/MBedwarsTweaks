package me.metallicgoat.tweaksaddon.gentiers.dragons;

import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.Team;
import de.marcely.bedwars.api.event.arena.RoundEndEvent;
import de.marcely.bedwars.api.game.spawner.Spawner;
import de.marcely.bedwars.tools.location.XYZ;
import de.marcely.bedwars.tools.location.XYZYP;
import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;
import me.metallicgoat.tweaksaddon.config.MainConfig;
import me.metallicgoat.tweaksaddon.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class DragonFollowTask extends BukkitRunnable implements Listener {

  private static final Random random = new Random();

  private final Vector velocity = new Vector(0, 0, 0);
  private final EnderDragon dragon;
  private final List<Location> defaultTargets;
  private final Arena arena;
  private final World world;
  @Nullable
  private final Team team;

  private Location lastDefaultTarget;
  private Location targetLocation = null;
  private double distanceToTarget = 0;
  private double distanceTraveled = 0;

  private DragonFollowTask(EnderDragon dragon, List<Location> defaultTargets, Arena arena, World world, @Nullable Team team) {
    this.dragon = dragon;
    this.defaultTargets = defaultTargets;
    this.arena = arena;
    this.world = world;
    this.team = team;
  }

  public static DragonFollowTask init(Arena arena, @Nullable Team team, Location arenaMiddle) {
    final World world = arena.getGameWorld();

    if (world == null)
      throw new RuntimeException("Sudden death dragon tried to spawn in an arena with no game world?!?!?! WTF how did we get here in life?");

    Location location = getDragonSpawn(arena, world, team);

    // Just spawn at the middle otherwise
    if (location == null)
      location = arenaMiddle;

    final EnderDragon dragon = (EnderDragon) world.spawnEntity(location, EntityType.ENDER_DRAGON);
    // TODO Use new MBedwars method to make silent

    final DragonFollowTask task = new DragonFollowTask(
        dragon,
        generateDefaultTargets(arena, team, world),
        arena,
        world,
        team
    );

    // Register events for this dragon
    Bukkit.getPluginManager().registerEvents(task, MBedwarsTweaksPlugin.getInstance());

    return task;
  }

  private static @Nullable Location getDragonSpawn(Arena arena, World world, Team team) {
    // Find optimal spot to spawn the dwwwagoon
    if (team != null) {
      final XYZYP spawn = arena.getTeamSpawn(team);

      if (spawn != null)
        return spawn.toLocation(world).add(0, 30, 0);

    } else {
      final XYZYP spawn = arena.getSpectatorSpawn();

      if (spawn != null && arena.isInside(spawn))
        return spawn.toLocation(world);
    }

    return null;
  }

  private static List<Location> generateDefaultTargets(Arena arena, @Nullable Team team, World world) {
    final XYZYP teamSpawn = team != null ? arena.getTeamSpawn(team) : null;
    final List<Location> targets = new ArrayList<>(Util.getAllTeamSpawns(arena, world, team));

    for (Spawner spawner : arena.getSpawners()) {
      final Location location = spawner.getLocation().toLocation(world);

      // ignore iron and gold spawners + spawners that are to close to the dragon's team's home base
      if (!spawner.getDropType().getId().equals("iron") &&
          !spawner.getDropType().getId().equals("gold") &&
          (teamSpawn == null || spawner.getLocation().toLocation(world).distance(teamSpawn.toLocation(world)) > 20))
        targets.add(location);
    }

    return Collections.unmodifiableList(targets);
  }

  // Allow dragon to break map blocks
  @EventHandler(priority = EventPriority.LOWEST)
  public void onBlockBreak(EntityExplodeEvent event) {
    if (event.getEntity() != this.dragon)
      return;

    // NOTE: We do not have to worry about beds being destroyed, since they are already gone
    for (Block block : event.blockList()) {
      if (this.arena.isInside(block.getLocation()) && this.arena.canPlaceBlockAt(block))
        block.setType(Material.AIR);
    }
  }

  // Kill dragon on round end
  @EventHandler
  public void onRoundEnd(RoundEndEvent event) {
    if (event.getArena() == this.arena) {

      if (this.dragon.isValid())
        this.dragon.remove();

      cancel();
    }
  }

  @EventHandler
  public void onDragonDeath(EntityDeathEvent event) {
    if (event.getEntity() != dragon)
      return;

    // TODO Find a better way... There might not be
    //  (Possibly remove and use packet to send death effect)
    // Hacky way to remove the dragon so the portal never gets created
    Bukkit.getScheduler().runTaskLater(MBedwarsTweaksPlugin.getInstance(), () -> {
      event.getEntity().remove();
    }, 20L * 6);
  }

  // Update where the dragon should go next
  private void updateTarget() {
    this.targetLocation = getNewTarget();

    if (this.targetLocation == null)
      throw new RuntimeException("MBedwars Tweaks: Bug detected! Dragon could not find a place to go to :(");

    // Add some location randomness, so it's not always flying to the exact same spot
    // TODO this.targetLocation.add(random.nextInt(20) - 10, random.nextInt(20) - 10, random.nextInt(20) - 10);

    // Reset distance tracking values
    this.distanceToTarget = this.targetLocation.distance(this.dragon.getLocation());
    this.distanceTraveled = 0;
  }

  private Location getNewTarget() {
    final int chanceValue = random.nextInt(100);

    // Target a random player
    if (chanceValue < 20) {
      final List<Location> targets = getPlayerTargets();

      if (!targets.isEmpty())
        return targets.get(random.nextInt(targets.size()));
    }

    // Chose a random base or generator
    if (chanceValue < 40)
      return pickRandomDefaultTarget();

    return generateRandomLocation();
  }

  private Location pickRandomDefaultTarget() {
    final List<Location> targets = new ArrayList<>(this.defaultTargets);

    if (lastDefaultTarget != null)
      targets.remove(lastDefaultTarget);

    final Location newTarget = targets.get(random.nextInt(targets.size())).clone();
    this.lastDefaultTarget = newTarget;

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

      // Dont go all the way to the border
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
      final Location target = pickRandomDefaultTarget();

      target.add(random.nextInt(120) - 60, random.nextInt(80) - 10, random.nextInt(120) - 60);

      return target;
    }
  }

  private List<Location> getPlayerTargets() {
    final List<Location> locations = new ArrayList<>();

    for (Player player : this.arena.getPlayers())
      if (this.arena.getPlayerTeam(player) != this.team && !this.arena.getSpectators().contains(player))
        locations.add(player.getLocation());

    return locations;
  }

  // Note to self: This works, don't fuck it up
  @Override
  public void run() {
    final Location dragonLocation = this.dragon.getLocation().clone();
    // The dragon has reached its target + tricks so it does not get in an 'orbit' around the target
    if (this.targetLocation == null || dragonLocation.distance(this.targetLocation) < 10 || this.distanceTraveled > this.distanceToTarget * 1.2)
      updateTarget();

    final Vector toTarget = this.targetLocation.clone().subtract(dragonLocation).toVector().normalize();
    final Vector gravity = toTarget.multiply(0.05); // More big = More Gravity

    // simulate "gravity pull"
    this.velocity.add(gravity);

    // Dont let dragon infinitely accelerate towards target
    final double maxSpeed = MainConfig.dragon_speed;

    if (this.velocity.length() > maxSpeed)
      this.velocity.normalize().multiply(maxSpeed);

    final Location teleportLocation = dragonLocation.add(this.velocity);
    this.distanceTraveled += this.velocity.length(); // Track how far it has been taking to get to the target

    // Move it move it move it
    teleportLocation.setDirection(this.dragon.getLocation().clone().subtract(teleportLocation).toVector());
    this.dragon.teleport(teleportLocation);
  }
}
