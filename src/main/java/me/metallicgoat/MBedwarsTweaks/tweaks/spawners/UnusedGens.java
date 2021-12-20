package me.metallicgoat.MBedwarsTweaks.tweaks.spawners;

import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.event.arena.RoundStartEvent;
import de.marcely.bedwars.api.game.spawner.Spawner;
import de.marcely.bedwars.api.game.spawner.SpawnerDurationModifier;
import me.metallicgoat.MBedwarsTweaks.MBedwarsTweaks;
import me.metallicgoat.MBedwarsTweaks.utils.ServerManager;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class UnusedGens implements Listener {

    @EventHandler
    public void onRoundStart(RoundStartEvent e){
        if (ServerManager.getConfig().getBoolean("Disable-Unused-Gens.Enabled")) {
            Arena arena = e.getArena();
            arena.getEnabledTeams().forEach(team -> {
                if (arena.getPlayersInTeam(team).size() == 0) {
                    Location spawnPoint = arena.getTeamSpawn(team).toLocation(arena.getGameWorld());
                    disableGens(arena, spawnPoint);
                }
            });
        }
    }

    private void disableGens(Arena arena, Location spawnPoint){
        arena.getSpawners().forEach(spawner -> {
            if(isDisableType(spawner)) {
                Location spawnerLoc = spawner.getLocation().toLocation(arena.getGameWorld());
                if (spawnerLoc.distance(spawnPoint) < ServerManager.getConfig().getDouble("Disable-Unused-Gens.Range")) {
                    spawner.addDropDurationModifier("EMPTY_TEAM_DISABLED", plugin(), SpawnerDurationModifier.Operation.SET, 999999);
                }
            }
        });
    }

    private boolean isDisableType(Spawner spawner){
        List<String> genTypes = ServerManager.getConfig().getStringList("Disable-Unused-Gens.Gen-Types");
        for(ItemStack itemStack:spawner.getDropType().getDroppingMaterials()){
            if(genTypes.contains(itemStack.getType().name())){
                return true;
            }
        }
        return false;
    }

    private static MBedwarsTweaks plugin(){
        return MBedwarsTweaks.getInstance();
    }
}
