package me.metallicgoat.tweaksaddon.tweaks.spawners;

import de.marcely.bedwars.api.arena.Arena;
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
public class DisableEliminatedTeamGenerators implements Listener {


  @EventHandler
  public void onEliminate(TeamEliminateEvent event){
    if (!MainConfig.disable_eliminated_team_generators)
      return;

    final Arena arena = event.getArena();
    final XYZYP spawnXYZP = arena.getTeamSpawn(event.getTeam());
    if (spawnXYZP == null) return;
    final Location spawnLoc = spawnXYZP.toLocation(arena.getGameWorld());

    if (MainConfig.disable_eliminated_team_generators_delay == -1)
      disableGen(arena, spawnLoc);
    else
      Bukkit.getScheduler().runTaskLater(MBedwarsTweaksPlugin.getInstance(), () -> disableGen(arena, spawnLoc), 20L *MainConfig.disable_eliminated_team_generators_delay);
  }

  private void disableGen(Arena arena, Location spawnPoint) {
    for (Spawner spawner : arena.getSpawners()) {
      // TODO: ADD A WHITELIST TOO USING Spawner#getID

      final Location spawnerLoc = spawner.getLocation().toLocation(arena.getGameWorld());

      // Uses the same range from empty team's range.
      if (spawnerLoc.distance(spawnPoint) < MainConfig.disable_empty_generators_range) {
        spawner.addDropDurationModifier("TEAM_ELIMINATED_TIMEOUT", MBedwarsTweaksPlugin.getInstance(), SpawnerDurationModifier.Operation.SET, 999999);
      }
    }
  }
}