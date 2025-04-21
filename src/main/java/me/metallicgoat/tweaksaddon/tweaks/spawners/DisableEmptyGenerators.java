package me.metallicgoat.tweaksaddon.tweaks.spawners;


import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.Team;
import de.marcely.bedwars.api.event.arena.RoundStartEvent;
import de.marcely.bedwars.api.event.arena.TeamEliminateEvent;
import de.marcely.bedwars.api.game.spawner.Spawner;
import de.marcely.bedwars.api.game.spawner.SpawnerDurationModifier;
import de.marcely.bedwars.tools.location.XYZYP;
import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;
import me.metallicgoat.tweaksaddon.config.MainConfig;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class DisableEmptyGenerators implements Listener {

  @EventHandler
  public void onRoundStart(RoundStartEvent event) {
    if (!MainConfig.disable_empty_generators)
      return;

    final Arena arena = event.getArena();

    for (Team team : arena.getEnabledTeams()) {
      if (arena.getPlayersInTeam(team).isEmpty()) {
        final XYZYP spawnPoint = arena.getTeamSpawn(team);

        if (spawnPoint != null)
          disableGens(arena, spawnPoint.toLocation(arena.getGameWorld()));
      }
    }
  }
  @EventHandler
  public void onEliminate(TeamEliminateEvent event){
    if (!MainConfig.disable_eliminated_team_generators)
      return;

    final Arena arena = event.getArena();
    final XYZYP spawnXYZP = arena.getTeamSpawn(event.getTeam());
    if (spawnXYZP == null) return;
    final Location spawnLoc = spawnXYZP.toLocation(arena.getGameWorld());

    if (MainConfig.disable_eliminated_team_generators_delay == -1)
      disableGenTeam(arena, spawnLoc);
    else
      Bukkit.getScheduler().runTaskLater(MBedwarsTweaksPlugin.getInstance(), () -> disableGenTeam(arena, spawnLoc), 20L *MainConfig.disable_eliminated_team_generators_delay);
  }

  private void disableGenTeam(Arena arena, Location spawnPoint) {
    for (Spawner spawner : arena.getSpawners()) {

      // If the spawner is in the white list we DON'T disable it.
      if (MainConfig.disable_eliminated_team_generator_whitelist.contains(spawner.getDropType().getId()))
        continue;

      final Location spawnerLoc = spawner.getLocation().toLocation(arena.getGameWorld());

      // Uses the same range from empty team's range.
      if (spawnerLoc.distance(spawnPoint) < MainConfig.disable_empty_generators_range) {
        spawner.addDropDurationModifier("TEAM_ELIMINATED_TIMEOUT", MBedwarsTweaksPlugin.getInstance(), SpawnerDurationModifier.Operation.SET, 999999);
      }
    }
  }

  private void disableGens(Arena arena, Location spawnPoint) {
    for (Spawner spawner : arena.getSpawners()) {
      if (!MainConfig.disable_empty_generators_spawners.contains(spawner.getDropType()))
        continue;

      final Location spawnerLoc = spawner.getLocation().toLocation(arena.getGameWorld());

      if (spawnerLoc.distance(spawnPoint) < MainConfig.disable_empty_generators_range) {
        spawner.addDropDurationModifier("EMPTY_TEAM_DISABLED", MBedwarsTweaksPlugin.getInstance(), SpawnerDurationModifier.Operation.SET, 999999);
      }
    }
  }
}

