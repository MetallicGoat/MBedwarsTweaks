package me.metallicgoat.tweaksaddon.gentiers.handlers;

import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.Team;
import de.marcely.bedwars.tools.location.XYZ;
import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;
import me.metallicgoat.tweaksaddon.config.MainConfig;
import me.metallicgoat.tweaksaddon.gentiers.GenTierLevel;
import me.metallicgoat.tweaksaddon.gentiers.GenTiers;
import me.metallicgoat.tweaksaddon.gentiers.dragons.DragonFollowTask;
import me.metallicgoat.tweaksaddon.utils.Util;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

public class SuddenDeathHandler extends GenTierHandler {
  @Override
  public void run(GenTierLevel level, Arena arena) {
    final Location middle = getArenaMid(arena);

    if (!arena.isInside(middle))
      throw new RuntimeException("Failed to find the center of an Arena!");

    // Spawn Default Dragon
    if (MainConfig.default_sudden_death_dragon_enabled)
      DragonFollowTask.init(arena, null, middle).runTaskTimer(MBedwarsTweaksPlugin.getInstance(), 0, 1L);

    // Spawn Team Dragons
    for (Team team : arena.getEnabledTeams()) {
      if (GenTiers.getState(arena).hasDragon(team))
        DragonFollowTask.init(arena, team, middle).runTaskTimer(MBedwarsTweaksPlugin.getInstance(), 0, 1L);
    }
  }

  private Location getArenaMid(Arena arena) {
    final World world = arena.getGameWorld();

    if (world == null)
      throw new RuntimeException("Cannot spawn a dragon in an arena with no world!?!?!?!");

    final XYZ max = arena.getMaxRegionCorner();
    final XYZ min = arena.getMinRegionCorner();

    Location location = null;

    if (min != null && max != null) {
      location = new Location(
          world,
          (max.getX() + min.getX()) / 2,
          (max.getY() + min.getY()) / 2,
          (max.getZ() + min.getZ()) / 2
      );
    }

    if (location == null)
      location = findAverageLocation(Util.getAllTeamSpawns(arena, world, null), world);

    return location;
  }

  // Find the middle using a bunch of points in the arena
  public Location findAverageLocation(List<Location> locations, World world) {
    if (locations == null || locations.isEmpty()) {
      return null;
    }

    double totalX = 0;
    double totalY = 0;
    double totalZ = 0;

    for (Location loc : locations) {
      totalX += loc.getX();
      totalY += loc.getY();
      totalZ += loc.getZ();
    }

    int numberOfLocations = locations.size();

    return new Location(
        world,
        totalX / numberOfLocations,
        totalY / numberOfLocations,
        totalZ / numberOfLocations
    );
  }
}
