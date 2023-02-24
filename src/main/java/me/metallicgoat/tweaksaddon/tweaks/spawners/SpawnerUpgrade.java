package me.metallicgoat.tweaksaddon.tweaks.spawners;

import de.marcely.bedwars.api.GameAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.ArenaStatus;
import de.marcely.bedwars.api.arena.Team;
import de.marcely.bedwars.api.event.player.PlayerBuyUpgradeEvent;
import de.marcely.bedwars.api.game.spawner.DropType;
import de.marcely.bedwars.api.game.spawner.Spawner;
import de.marcely.bedwars.api.game.upgrade.UpgradeTriggerHandlerType;
import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;
import me.metallicgoat.tweaksaddon.config.ConfigValue;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

public class SpawnerUpgrade implements Listener {

    @EventHandler
    public void onPlayerBuyUpgrade(PlayerBuyUpgradeEvent event){
        final Arena arena = event.getArena();
        final Team team = event.getTeam();

        if(!ConfigValue.advanced_forge_enabled || arena == null || team == null || !event.getProblems().isEmpty())
            return;

        if(event.getUpgradeLevel().getTriggerHandler().getType() != UpgradeTriggerHandlerType.SPAWNER_MULTIPLIER)
            return;

        final int level = event.getUpgradeLevel().getLevel();

        if(level == 3)
            (new SpawnerUpgradeTask(arena, team)).runTaskTimer(MBedwarsTweaksPlugin.getInstance(), 80, 120);
    }
}

class SpawnerUpgradeTask extends BukkitRunnable {
    private final Arena arena;
    private final Team team;
    private final DropType dropType = GameAPI.get().getDropTypeById(ConfigValue.advanced_forge_new_drop);
    private final DropType affectingType = GameAPI.get().getDropTypeById(ConfigValue.advanced_forge_effected_spawner);

    public SpawnerUpgradeTask(Arena arena, Team team){
        this.arena = arena;
        this.team = team;
    }

    @Override
    public void run() {
        if (arena.getStatus() != ArenaStatus.RUNNING) {
            cancel();
            return;
        }

        final Spawner spawner = getAffectingSpawner(arena, team, affectingType);

        if(spawner == null || dropType == null)
            return;

        spawner.drop(false, dropType.getDroppingMaterials());
    }

    private Spawner getAffectingSpawner(Arena arena, Team team, DropType affectingType){
        final Location baseLocation = arena.getTeamSpawn(team).toLocation(arena.getGameWorld());

        for(Spawner spawner : arena.getSpawners()){
            if(baseLocation.distance(spawner.getLocation().toLocation(arena.getGameWorld())) > 30)
                continue;

            if(spawner.getDropType() == affectingType)
                return spawner;
        }

        return null;
    }
}
