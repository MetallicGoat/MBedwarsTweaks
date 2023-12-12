package me.metallicgoat.tweaksaddon.tweaks.cosmetic;

import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.ArenaStatus;
import de.marcely.bedwars.api.arena.Team;
import de.marcely.bedwars.api.event.player.PlayerBuyUpgradeEvent;
import de.marcely.bedwars.api.game.upgrade.UpgradeTriggerHandler;
import de.marcely.bedwars.api.game.upgrade.UpgradeTriggerHandlerType;
import de.marcely.bedwars.tools.VarParticle;
import de.marcely.bedwars.tools.location.XYZYP;
import java.util.Collection;
import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;
import me.metallicgoat.tweaksaddon.config.MainConfig;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

public class HealPoolParticles implements Listener {

  @EventHandler(priority = EventPriority.MONITOR)
  public void onUpgradeBuy(PlayerBuyUpgradeEvent event) {
    if (!MainConfig.heal_pool_particle_enabled || !event.getProblems().isEmpty() || MainConfig.heal_pool_particle_type == null)
      return;

    final UpgradeTriggerHandler handler = event.getUpgradeLevel().getTriggerHandler();

    if (handler == null || handler.getType() != UpgradeTriggerHandlerType.HEAL_POOL)
      return;

    final Team team = event.getTeam();
    final Arena arena = event.getArena();
    final XYZYP teamSpawn = arena.getTeamSpawn(team);

    if (teamSpawn == null)
      return;

    final VarParticle particle = MainConfig.heal_pool_particle_type.clone();
    final int volume = MainConfig.heal_pool_particle_range*3*2;
    final int count = Math.max(1, volume/4);

    particle.setOffset(
        MainConfig.heal_pool_particle_range,
        MainConfig.heal_pool_particle_range,
        MainConfig.heal_pool_particle_range
    );
    particle.setCount(count);

    new HealPoolParticlesTask(arena, team, teamSpawn.toLocation(arena.getGameWorld()), particle).start();
  }



  private static class HealPoolParticlesTask extends BukkitRunnable {

    private final Arena arena;
    private final Team team;
    private final Location teamSpawn;
    private final VarParticle particle;

    public HealPoolParticlesTask(Arena arena, Team team, Location teamSpawn, VarParticle particle) {
      this.arena = arena;
      this.team = team;
      this.teamSpawn = teamSpawn;
      this.particle = particle;
    }

    public void start() {
      if (this.arena.getStatus() == ArenaStatus.RUNNING)
        runTaskTimerAsynchronously(MBedwarsTweaksPlugin.getInstance(), 0L, 20L);
    }

    @Override
    public void run() {
      if (this.arena.getStatus() != ArenaStatus.RUNNING) {
        cancel();
        return;
      }

      if (MainConfig.heal_pool_particle_team_view_only) {
        final Collection<Player> members = this.arena.getPlayersInTeam(this.team);

        for (Player member : members)
          this.particle.play(this.teamSpawn, member);

      } else {
        this.particle.play(this.teamSpawn);
      }
    }
  }
}
