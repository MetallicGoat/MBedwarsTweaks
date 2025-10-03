package me.metallicgoat.tweaksaddon.gentiers.dragons;

import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.Team;
import de.marcely.bedwars.api.game.spawner.Spawner;
import de.marcely.bedwars.tools.location.XYZYP;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import me.metallicgoat.tweaksaddon.api.gentiers.SuddenDeathDragon;
import me.metallicgoat.tweaksaddon.utils.Util;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

public class DragonUtil {

  public static final List<SuddenDeathDragonImpl> runningDragons = new CopyOnWriteArrayList<>();

  // Find optimal spot to spawn the dwwwagoon
  public static @Nullable Location getDragonSpawn(Arena arena, World world, Team team) {
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

  public static List<Location> getRelevantStaticTargets(Arena arena, @Nullable Team team, World world) {
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

  public static List<SuddenDeathDragon> getDragons(Arena arena, Team team) {
    if (arena == null)
      return Collections.unmodifiableList(runningDragons);

    return Collections.unmodifiableList(runningDragons.stream().filter((dragon -> team == null || dragon.getTeam() == team)).collect(Collectors.toList()));
  }

  public static void killAllDragons() {
    for (SuddenDeathDragonImpl task : new ArrayList<>(runningDragons))
      task.remove();

    runningDragons.clear();
  }
}
