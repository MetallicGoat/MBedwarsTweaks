package me.metallicgoat.tweaksaddon.gentiers.dragons;

import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.Team;
import de.marcely.bedwars.api.event.arena.RoundEndEvent;
import de.marcely.bedwars.api.game.spawner.Spawner;
import de.marcely.bedwars.tools.Helper;
import de.marcely.bedwars.tools.NMSHelper;
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
import org.bukkit.event.entity.EntityCreatePortalEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class DragonFollowTask extends BukkitRunnable implements Listener {

  private static final List<DragonFollowTask> runningDragons = new ArrayList<>();
  private static final Random random = new Random();

  private final Vector velocity = new Vector(0, 0, 0);
  private final EnderDragon dragon;
  private final List<Location> defaultTargets;
  private final Arena arena;
  private final World world;
  @Nullable
  private final Team team;

  private boolean targetingPlayer = false;
  private Player currPlayerTarget = null;
  private Location playerTargetLocation = null;
  private Location currDefaultTarget = null;
  private double distanceToTarget = 0;
  private double distanceTraveled = 0;

  private DragonFollowTask(EnderDragon dragon, List<Location> defaultTargets, Arena arena, World world, @Nullable Team team) {
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

    Location location = getDragonSpawn(arena, world, team);

    // Just spawn at the middle otherwise
    if (location == null)
      location = arenaMiddle;

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

    task.runTaskTimer(MBedwarsTweaksPlugin.getInstance(), 0L, 1L);

    runningDragons.add(task);
  }

  public static void killAll() {
    final Iterator<DragonFollowTask> it = runningDragons.iterator();

    // DO NOT REPLACE (Like intellij says... It lies)
    while (it.hasNext()) {
      it.next().remove(false);
      it.remove();
    }
  }

  // Find optimal spot to spawn the dwwwagoon
  private static @Nullable Location getDragonSpawn(Arena arena, World world, Team team) {
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
    if (event.getArena() == this.arena)
      remove();
  }

  @EventHandler
  public void onDragonDeath(EntityDeathEvent event) {
    if (event.getEntity() != dragon)
      return;

    // TODO Find a better way... There might not be
    //  (Possibly remove and use packet to send death effect)
    // Hacky way to remove the dragon so the portal never gets created (gets created at tick 200)
    if (NMSHelper.get().getVersion() >= 9)
      Bukkit.getScheduler().runTaskLater(MBedwarsTweaksPlugin.getInstance(), this::remove, 198L);
  }

  // This works for 1.8.8, but got broken with 1.9+
  @EventHandler
  public void onEntityCreatePortalEvent(EntityCreatePortalEvent event) {
    if (event.getEntity() != dragon)
      return;

    event.setCancelled(true);
  }

  private void updateTarget() {
    final int chanceValue = random.nextInt(100);
    final List<Player> playerTargets = getPlayerTargets();

    // Try to target a random player
    if (!playerTargets.isEmpty() && chanceValue < Math.min(60, playerTargets.size() * 25)) {
      this.currPlayerTarget = playerTargets.get(random.nextInt(playerTargets.size()));
      this.playerTargetLocation = currPlayerTarget.getLocation();
      this.distanceToTarget = this.playerTargetLocation.distance(this.dragon.getLocation()) + 50;
      this.targetingPlayer = true;

    } else {
      if (chanceValue < 90) // base or gen
        this.currDefaultTarget = pickRandomDefaultTarget();
      else // random cord
        this.currDefaultTarget = generateRandomLocation();

      this.distanceToTarget = this.currDefaultTarget.distance(this.dragon.getLocation());
      this.targetingPlayer = false;
    }

    this.distanceTraveled = 0;
  }

  private List<Player> getPlayerTargets() {
    final List<Player> locations = new ArrayList<>();

    for (Player player : this.arena.getPlayers())
      if (player != this.currPlayerTarget && this.arena.getPlayerTeam(player) != this.team && !this.arena.getSpectators().contains(player))
        locations.add(player);

    return locations;
  }

  private Location pickRandomDefaultTarget() {
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

      target.add(random.nextInt(120) - 60, random.nextInt(50) - 10, random.nextInt(120) - 60);

      return target;
    }
  }

  // Note to self: This works, don't fuck it up
  @Override
  public void run() {
    final Location dragonLocation = this.dragon.getLocation().clone();
    Location targetLocation;

    if (this.targetingPlayer) {
      if (this.currPlayerTarget.isOnline() && arena.getPlayers().contains(this.currPlayerTarget) && !arena.getSpectators().contains(this.currPlayerTarget))
        targetLocation = this.currPlayerTarget.getLocation();
      else
        targetLocation = this.playerTargetLocation;

    } else {
      targetLocation = this.currDefaultTarget;
    }

    // The dragon has reached its target + tricks so it does not get in an 'orbit' around the target
    if (targetLocation == null || dragonLocation.distance(targetLocation) < 10 || this.distanceTraveled > this.distanceToTarget * 1.2) {
      updateTarget();
      return;
    }

    targetLocation = targetLocation.clone();

    final Vector toTarget = targetLocation.subtract(dragonLocation).toVector().normalize();
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

    // Only async on paper 1.14.4+
    Helper.get().teleportAsync(this.dragon, teleportLocation, null);
  }

  private void remove() {
    remove(true);
  }

  private void remove(boolean removeFromList) {
    if (this.dragon.isValid())
      this.dragon.remove();

    if (removeFromList)
      runningDragons.remove(this);

    cancel();
  }
}
