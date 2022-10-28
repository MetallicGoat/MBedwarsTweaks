package me.metallicgoat.tweaksaddon.tweaks.cosmetic;

import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.ArenaStatus;
import de.marcely.bedwars.api.arena.Team;
import de.marcely.bedwars.api.event.player.PlayerBuyUpgradeEvent;
import de.marcely.bedwars.api.game.upgrade.UpgradeTriggerHandler;
import de.marcely.bedwars.api.game.upgrade.UpgradeTriggerHandlerType;
import de.marcely.bedwars.tools.location.XYZYP;
import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;
import me.metallicgoat.tweaksaddon.config.ConfigValue;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Random;

public class HealPoolParticles implements Listener {

    @EventHandler
    public void onUpgradeBuy(PlayerBuyUpgradeEvent event){
        if(!ConfigValue.heal_pool_particle_enabled)
            return;

        final UpgradeTriggerHandler handler = event.getUpgradeLevel().getTriggerHandler();

        if(handler == null || handler.getType() != UpgradeTriggerHandlerType.HEAL_POOL)
            return;

        final Team team = event.getTeam();
        final Arena arena = event.getArena();
        final XYZYP teamSpawn = arena.getTeamSpawn(team);

        if(teamSpawn != null)
            new HealPoolParticlesTask(arena, team, teamSpawn.toLocation(arena.getGameWorld()))
                    .runTaskTimerAsynchronously(MBedwarsTweaksPlugin.getInstance(), 20L, 20L);
    }
}

class HealPoolParticlesTask extends BukkitRunnable {

    private final Location teamSpawn;
    private final Team team;
    private final Arena arena;

    public HealPoolParticlesTask(Arena arena, Team team, Location teamSpawn){
        this.arena = arena;
        this.team = team;
        this.teamSpawn = teamSpawn;
    }

    @Override
    public void run() {
        if(arena.getStatus() != ArenaStatus.RUNNING || arena.getPlayers().isEmpty()){
            cancel();
            return;
        }

        for(Location location : getParticleLocations(teamSpawn, ConfigValue.heal_pool_particle_range)){
            if(ConfigValue.heal_pool_particle != null){
                if(ConfigValue.heal_pool_particle_team_view_only) {
                    for (Player player : arena.getPlayersInTeam(team))
                        ConfigValue.heal_pool_particle.play(location, player);
                } else
                    ConfigValue.heal_pool_particle.play(location);
            }
        }
    }

    public ArrayList<Location> getParticleLocations(Location start, int radius){
        final ArrayList<Location> locations = new ArrayList<>();

        for(double x = start.getX() - radius; x <= start.getX() + radius; x++){
            for(double y = start.getY() - radius; y <= start.getY() + radius; y++){
                for(double z = start.getZ() - radius; z <= start.getZ() + radius; z++){
                    final Location location = new Location(start.getWorld(), x, y, z);

                    if(location.getBlock().getType() == Material.AIR
                            && new Random().nextInt(200) == 0)
                        locations.add(location);
                }
            }
        }

        return locations;
    }
}
