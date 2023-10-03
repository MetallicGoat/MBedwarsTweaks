package me.metallicgoat.tweaksaddon.tweaks.spawners;

import de.marcely.bedwars.api.GameAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.ArenaStatus;
import de.marcely.bedwars.api.arena.Team;
import de.marcely.bedwars.api.event.arena.RoundEndEvent;
import de.marcely.bedwars.api.event.player.PlayerBuyUpgradeEvent;
import de.marcely.bedwars.api.game.spawner.DropType;
import de.marcely.bedwars.api.game.spawner.Spawner;
import de.marcely.bedwars.api.game.upgrade.UpgradeTriggerHandler;
import de.marcely.bedwars.api.game.upgrade.UpgradeTriggerHandlerType;
import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;
import me.metallicgoat.tweaksaddon.config.MainConfig;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class SpawnerUpgrade implements Listener {

  public static Map<Arena, List<BukkitTask>> runningTasks = new ConcurrentHashMap<>();

  @EventHandler
  public void onPlayerBuyUpgrade(PlayerBuyUpgradeEvent event) {
    final Arena arena = event.getArena();
    final Team team = event.getTeam();
    final UpgradeTriggerHandler handler = event.getUpgradeLevel().getTriggerHandler();

    // Check enable
    if (!MainConfig.advanced_forge_enabled || arena == null || team == null || handler == null || !event.getProblems().isEmpty())
      return;

    // Check upgrade type
    if (handler.getType() != UpgradeTriggerHandlerType.SPAWNER_MULTIPLIER)
      return;

    // Start upgrade
    if (event.getUpgradeLevel().getLevel() == MainConfig.advanced_forge_level) {
      final BukkitTask task = (new SpawnerUpgradeTask(arena, team)).runTaskTimer(MBedwarsTweaksPlugin.getInstance(), 80, 20L * MainConfig.advanced_forge_drop_rate);
      final List<BukkitTask> arenaTasks = runningTasks.get(arena);

      if (arenaTasks != null)
        arenaTasks.add(task);
      else {
        final List<BukkitTask> list = new CopyOnWriteArrayList<>();
        list.add(task);
        runningTasks.put(arena, list);
      }
    }
  }

  @EventHandler
  public void onRoundEnd(RoundEndEvent event) {
    final Arena arena = event.getArena();
    final List<BukkitTask> tasks = runningTasks.get(arena);

    if (tasks == null)
      return;

    for (BukkitTask task : tasks)
      task.cancel();

    runningTasks.remove(arena);
  }



  private static class SpawnerUpgradeTask extends BukkitRunnable {
    private final Arena arena;
    private final Location baseLocation;
    private final DropType dropType = GameAPI.get().getDropTypeById(MainConfig.advanced_forge_new_drop);
    private final DropType affectingType = GameAPI.get().getDropTypeById(MainConfig.advanced_forge_effected_spawner);

    public SpawnerUpgradeTask(Arena arena, Team team) {
      this.arena = arena;
      this.baseLocation = arena.getTeamSpawn(team).toLocation(arena.getGameWorld());
    }

    @Override
    public void run() {
      if (arena.getStatus() != ArenaStatus.RUNNING) {
        cancel();
        return;
      }

      if (affectingType == null || dropType == null)
        return;

      for (Spawner spawner : getAffectingSpawners())
        spawner.drop(false, dropType.getDroppingMaterials());
    }

    private List<Spawner> getAffectingSpawners() {
      final List<Spawner> spawnerList = new ArrayList<>();

      for (Spawner spawner : this.arena.getSpawners()) {
        if (spawner.getDropType() != affectingType)
          continue;

        if (baseLocation.distance(spawner.getLocation().toLocation(this.arena.getGameWorld())) <= MainConfig.advanced_forge_range)
          spawnerList.add(spawner);
      }

      return spawnerList;
    }
  }
}
