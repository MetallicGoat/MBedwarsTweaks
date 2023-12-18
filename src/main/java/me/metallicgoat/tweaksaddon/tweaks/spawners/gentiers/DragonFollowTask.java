package me.metallicgoat.tweaksaddon.tweaks.spawners.gentiers;

import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.Team;
import de.marcely.bedwars.api.event.arena.RoundEndEvent;
import de.marcely.bedwars.api.game.spawner.Spawner;
import de.marcely.bedwars.tools.location.XYZYP;
import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DragonFollowTask extends BukkitRunnable implements Listener {

  private static final Random random = new Random();

  private DragonState state = DragonState.RETURNING_HOME;
  private int steps = 0;
  private final EnderDragon dragon;
  private final Arena arena;
  private final Team team;
  private Vector vec = null;

  public DragonFollowTask(EnderDragon dragon, Arena arena, Team team) {
    Bukkit.getPluginManager().registerEvents(this, MBedwarsTweaksPlugin.getInstance());

    this.dragon = dragon;
    this.arena = arena;
    this.team = team;
  }

  // Allow dragon to break map blocks
  @EventHandler(priority = EventPriority.LOWEST)
  public void onBlockBreak(EntityExplodeEvent event) {
    if (event.getEntity() != dragon)
      return;

    for (Block block : event.blockList()) {
      if (arena.canPlaceBlockAt(block))
        block.setType(Material.AIR);
    }
  }

  // Kill dragon on round end
  @EventHandler
  public void onRoundEnd(RoundEndEvent event) {
    if (event.getArena() == arena) {
      if (dragon.isValid())
        dragon.setHealth(0);

      cancel();
    }
  }

  // Update where the dragon should go next
  private void updateTarget() {
    final Location location = getNextTarget();

    if (location == null)
      throw new RuntimeException("MBedwars Tweaks: Bug detected! Dragon could not find a place to go to :(");

    final Vector direction = location.toVector().subtract(dragon.getLocation().toVector());
    final double speed = 0.8;

    steps = (int) (direction.length() * (2 - speed));
    vec = direction.clone().normalize().multiply(speed);
  }

  private Location getNextTarget() {
    if (state != DragonState.RETURNING_HOME) {
      final XYZYP spectatorSpawn = arena.getSpectatorSpawn();

      if (spectatorSpawn == null) {
        dragon.setHealth(0);
        cancel();
        return null;
      }

      state = DragonState.RETURNING_HOME;
      return arena.getSpectatorSpawn().toLocation(arena.getGameWorld());
    }

    final int randomInt = random.nextInt(100);
    Location location = null;

    if (randomInt < 45) { // 45% chance - random team spawn
      List<Team> teams = new ArrayList<>(arena.getEnabledTeams());
      teams.removeIf(currTeam -> currTeam == team);
      location = arena.getTeamSpawn(teams.get(random.nextInt(teams.size()))).toLocation(arena.getGameWorld());
      state = DragonState.TARGET_LOCATION;

    } else if (randomInt < 90) { // 45% chance - random generator
      List<Spawner> spawners = new ArrayList<>(arena.getSpawners());
      World world = arena.getGameWorld();
      spawners.removeIf(spawner -> spawner.getLocation().toLocation(world).distance(arena.getTeamSpawn(team).toLocation(world)) < 20);
      location = spawners.get(random.nextInt(spawners.size())).getLocation().toLocation(world);
      state = DragonState.TARGET_LOCATION;

    } else if (randomInt < 100) { // 10% chance - random player
      List<Player> players = new ArrayList<>(arena.getPlayers());
      players.removeIf(player -> arena.getPlayerTeam(player) == team);
      location = players.get(random.nextInt(players.size())).getLocation();
      state = DragonState.TARGET_PLAYER;
    }

    return location;
  }


  @Override
  public void run() {
    if (dragon == null || !dragon.isValid()) {
      cancel();
      return;
    }

    if (vec == null || steps-- <= 0) {
//      if (state == DragonState.RETURNING_HOME) {
//        // Rest for a little bit
//
//      } else
        updateTarget();
    }

    final Location location = dragon.getLocation().add(vec).setDirection(vec.clone().multiply(-1));
    dragon.teleport(location);
  }

  private enum DragonState {
    RETURNING_HOME,
    TARGET_PLAYER,
    TARGET_LOCATION
  }
}
