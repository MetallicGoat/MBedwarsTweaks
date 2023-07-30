package me.metallicgoat.tweaksaddon.tweaks.cosmetic;

import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.ArenaStatus;
import de.marcely.bedwars.api.arena.Team;
import de.marcely.bedwars.api.event.arena.RoundEndEvent;
import de.marcely.bedwars.api.event.player.PlayerBuyUpgradeEvent;
import de.marcely.bedwars.api.game.upgrade.UpgradeTriggerHandler;
import de.marcely.bedwars.api.game.upgrade.UpgradeTriggerHandlerType;
import de.marcely.bedwars.tools.location.XYZYP;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;
import me.metallicgoat.tweaksaddon.config.MainConfig;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

public class HealPoolParticles implements Listener {

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onUpgradeBuy(PlayerBuyUpgradeEvent event) {
    if (!MainConfig.heal_pool_particle_enabled || !event.getProblems().isEmpty())
      return;

    final UpgradeTriggerHandler handler = event.getUpgradeLevel().getTriggerHandler();

    if (handler == null || handler.getType() != UpgradeTriggerHandlerType.HEAL_POOL)
      return;

    final Team team = event.getTeam();
    final Arena arena = event.getArena();
    final XYZYP teamSpawn = arena.getTeamSpawn(team);

    if (teamSpawn != null)
      new HealPoolParticlesTask(arena, team, teamSpawn.toLocation(arena.getGameWorld())).start();
  }



  private static class HealPoolParticlesTask extends BukkitRunnable implements Listener {
    private final ArrayList<Location> locs;
    private final Team team;
    private final Arena arena;

    public HealPoolParticlesTask(Arena arena, Team team, Location teamSpawn) {
      this.locs = getParticleLocations(teamSpawn, MainConfig.heal_pool_particle_range);
      this.arena = arena;
      this.team = team;
    }

    public void start(){
      if (this.arena.getStatus() == ArenaStatus.RUNNING)
        runTaskTimer(MBedwarsTweaksPlugin.getInstance(), 0L, 20L);
    }

    @EventHandler
    public void onRoundEnd(RoundEndEvent event){
      cancel();
    }

    @Override
    public void run() {
      Collections.shuffle(locs);
      final List<Location> locationsRandomized = locs.subList(0, locs.size() / 4);

      for (Location location : locationsRandomized) {
        if (MainConfig.heal_pool_particle_type != null) {
          if (MainConfig.heal_pool_particle_team_view_only) {
            for (Player player : arena.getPlayersInTeam(team))
              MainConfig.heal_pool_particle_type.play(location, player);
          } else
            MainConfig.heal_pool_particle_type.play(location);
        }
      }
    }

    public ArrayList<Location> getParticleLocations(Location start, int radius) {
      final ArrayList<Location> locations = new ArrayList<>();

      for (double x = start.getX() - radius; x <= start.getX() + radius; x++) {
        for (double y = start.getY() - radius; y <= start.getY() + radius; y++) {
          for (double z = start.getZ() - radius; z <= start.getZ() + radius; z++) {
            final Location location = new Location(start.getWorld(), x, y, z);

            if (location.getBlock().getType() == Material.AIR && new Random().nextInt(200) == 0)
              locations.add(location);
          }
        }
      }

      return locations;
    }
  }
}
