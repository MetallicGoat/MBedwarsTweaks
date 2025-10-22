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
        disableGens(arena, team, false);
      }
    }
  }

  @EventHandler
  public void onEliminate(TeamEliminateEvent event){
    if (!MainConfig.disable_eliminated_team_generators)
      return;

    final Arena arena = event.getArena();

    disableGens(arena, event.getTeam(), true);
  }

  private void disableGens(Arena arena, Team team, boolean teamElimination) {
    final XYZYP spawnXYZP = arena.getTeamSpawn(team);

    if (spawnXYZP == null)
      return;

    final Location spawnPoint = spawnXYZP.toLocation(arena.getGameWorld());

    for (Spawner spawner : arena.getSpawners()) {

      // If the spawner is in the whitelist, we do not disable it.
      if (!MainConfig.disable_empty_generators_spawners.contains(spawner.getDropType()))
        continue;

      final Location spawnerLoc = spawner.getLocation().toLocation(arena.getGameWorld());

      if (spawnerLoc.distanceSquared(spawnPoint) < MainConfig.disable_empty_generators_range * MainConfig.disable_empty_generators_range) {
        spawner.addDropDurationModifier(
            teamElimination ? "TEAM_ELIMINATED_DISABLED": "EMPTY_TEAM_DISABLED",
            MBedwarsTweaksPlugin.getInstance(), SpawnerDurationModifier.Operation.SET, 999999
        );
      }
    }
  }
}

