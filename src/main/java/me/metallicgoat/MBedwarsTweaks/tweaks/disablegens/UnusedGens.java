package me.metallicgoat.MBedwarsTweaks.tweaks.disablegens;

import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.event.arena.RoundStartEvent;
import de.marcely.bedwars.api.game.spawner.Spawner;
import de.marcely.bedwars.api.game.spawner.SpawnerDurationModifier;
import me.metallicgoat.MBedwarsTweaks.Main;
import me.metallicgoat.MBedwarsTweaks.utils.ServerManager;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

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
        for(ItemStack itemStack:spawner.getDropType().getDroppingMaterials()){
            if(ServerManager.getConfig().getStringList("Disable-Unused-Gens.Gen-Types").contains(itemStack.getType().name())){
                return true;
            }
        }
        return false;
    }

    private static Main plugin(){
        return Main.getInstance();
    }
}
