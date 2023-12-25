package me.metallicgoat.tweaksaddon.gentiers.dragons;

import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.Team;
import de.marcely.bedwars.api.event.arena.RoundEndEvent;
import de.marcely.bedwars.api.game.spawner.Spawner;
import de.marcely.bedwars.tools.location.XYZ;
import de.marcely.bedwars.tools.location.XYZYP;
import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;
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

  private DragonState state = DragonState.RETURNING_HOME;
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

  public static DragonFollowTask init(Arena arena, @Nullable Team team) {
    final World world = arena.getGameWorld();

    if (world == null)
      throw new RuntimeException("Sudden death dragon tried to spawn in an arena with no game world?!?!?! WTF how did we get here in life?");

    Location location = null;

    // Find optimal spot to spawn the dwwwagoon
    {
      if (team != null) {
        final XYZYP spawn = arena.getTeamSpawn(team);

        if (spawn != null)
          location = spawn.toLocation(world).add(0, 30, 0);

      } else {
        final XYZYP spawn = arena.getSpectatorSpawn();

        if (spawn != null && arena.isInside(spawn))
          location = spawn.toLocation(world);
      }

      if (location == null) {
        final XYZ max = arena.getMaxRegionCorner();
        final XYZ min = arena.getMinRegionCorner();

        if (min == null || max == null)
          throw new RuntimeException("Failed to find spot to spawn in a dragon");

        location = new Location(
            world,
            (max.getX() + min.getX()) / 2,
            (max.getY() + min.getY()) / 2,
            (max.getZ() + min.getZ()) / 2
        );
      }
    }

    final EnderDragon dragon = (EnderDragon) world.spawnEntity(location, EntityType.ENDER_DRAGON);
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

  private static List<Location> generateDefaultTargets(Arena arena, @Nullable Team team, World world) {
    final List<Location> targets = new ArrayList<>();
    final XYZYP teamSpawn = team != null ? arena.getTeamSpawn(team) : null;

    for (Team currTeam : arena.getEnabledTeams()) {
      if (currTeam == team)
        continue;

      final XYZYP spawn = arena.getTeamSpawn(currTeam);

      if (spawn != null)
        targets.add(spawn.toLocation(world));
    }

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
    // 20% chance the dragon will go to the middle to rest before its next move
    if (this.state != DragonState.RETURNING_HOME && random.nextInt(5) < 1) {
      final XYZYP spectatorSpawn = this.arena.getSpectatorSpawn();

      if (spectatorSpawn == null) {
        this.dragon.setHealth(0);
        cancel();
        return null;
      }

      this.state = DragonState.RETURNING_HOME;
      return this.arena.getSpectatorSpawn().toLocation(this.arena.getGameWorld());
    }

    this.state = DragonState.TARGET_LOCATION;

    final int chanceValue = random.nextInt(100);

    // Target a random player
    if (chanceValue < 10) {
      final List<Location> targets = getPlayerTargets();

      if (!targets.isEmpty())
        return targets.get(random.nextInt(targets.size()));
    }

    // Chose a random base or generator
    if (chanceValue < 50)
      return this.defaultTargets.get(random.nextInt(this.defaultTargets.size()));

    return generateRandomLocation();
  }

  // Find a random spot anywhere in the arena
  // TODO Check if this works on world type arenas
  private Location generateRandomLocation() {
    final XYZ max = this.arena.getMaxRegionCorner();
    final XYZ min = this.arena.getMinRegionCorner();

    if (min == null || max == null)
      throw new RuntimeException("Failed to find spot to spawn in a dragon");

    final int x = random.nextInt((int) Math.abs(max.getX() - min.getX()));
    final int y = random.nextInt((int) Math.abs(max.getY() - min.getY()));
    final int z = random.nextInt((int) Math.abs(max.getZ() - min.getZ()));

    return new Location(
        this.world,
        Math.min(max.getX(), min.getX()) + x,
        Math.min(max.getY(), min.getY()) + y,
        Math.min(max.getZ(), min.getZ()) + z
    );
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
    if (this.targetLocation == null || dragonLocation.distance(this.targetLocation) < 10 || this.distanceTraveled > this.distanceToTarget + (this.distanceToTarget * 0.1))
      updateTarget();

    final Vector toTarget = this.targetLocation.clone().subtract(dragonLocation).toVector().normalize();
    final Vector gravity = toTarget.multiply(0.05); // More big = More Gravity

    // simulate "gravity pull"
    this.velocity.add(gravity);

    // Dont let dragon infinitely accelerate towards target
    final double maxSpeed = 1.2;

    if (this.velocity.length() > maxSpeed)
      this.velocity.normalize().multiply(maxSpeed);

    final Location teleportLocation = dragonLocation.add(this.velocity);
    this.distanceTraveled += this.velocity.length(); // Track how far it has been taking to get to the target

    // Move it move it move it
    teleportLocation.setDirection(this.dragon.getLocation().clone().subtract(teleportLocation).toVector());
    this.dragon.teleport(teleportLocation);
  }

  private enum DragonState {
    RETURNING_HOME,
    TARGET_LOCATION
  }
}
