package me.metallicgoat.tweaksaddon.tweaks.spawners;


import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.Team;
import de.marcely.bedwars.api.event.arena.RoundStartEvent;
import de.marcely.bedwars.api.game.spawner.Spawner;
import de.marcely.bedwars.api.game.spawner.SpawnerDurationModifier;
import de.marcely.bedwars.tools.location.XYZYP;
import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;
import me.metallicgoat.tweaksaddon.config.ConfigValue;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class DisableEmptyGenerators implements Listener {

    @EventHandler
    public void onRoundStart(RoundStartEvent event){
        if (!ConfigValue.disable_empty_generators)
            return;

        final Arena arena = event.getArena();

        for(Team team : arena.getEnabledTeams()){
            if (arena.getPlayersInTeam(team).size() == 0) {
                final XYZYP spawnPoint = arena.getTeamSpawn(team);

                if(spawnPoint != null)
                    disableGens(arena, spawnPoint.toLocation(arena.getGameWorld()));
            }
        }
    }

    private void disableGens(Arena arena, Location spawnPoint){
        for(Spawner spawner : arena.getSpawners()){
            if(!ConfigValue.disable_empty_generators_spawners.contains(spawner.getDropType()))
                continue;

            final Location spawnerLoc = spawner.getLocation().toLocation(arena.getGameWorld());

            if (spawnerLoc.distance(spawnPoint) < ConfigValue.disable_empty_generators_range) {
                spawner.addDropDurationModifier("EMPTY_TEAM_DISABLED", MBedwarsTweaksPlugin.getInstance(), SpawnerDurationModifier.Operation.SET, 999999);
            }
        }
    }
}

