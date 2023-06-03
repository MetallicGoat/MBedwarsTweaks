package me.metallicgoat.tweaksaddon.schedular;

import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.event.arena.RoundEndEvent;
import de.marcely.bedwars.api.event.arena.RoundStartEvent;
import java.util.ArrayList;
import java.util.List;
import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

public class ArenaScheduler implements Listener {

  private final MBedwarsTweaksPlugin plugin;
  private final List<ArenaSchedulerHandler> handlers = new ArrayList<>();
  private final List<Arena> arenas = new ArrayList<>();

  private long time = 0;
  private BukkitTask task;
  private boolean isRunning = false;

  public ArenaScheduler(MBedwarsTweaksPlugin plugin) {
    this.plugin = plugin;
  }

  public List<Arena> getArenas(){
    return arenas;
  }

  public void registerHandler(ArenaSchedulerHandler handler) {
    handlers.add(handler);
  }

  @EventHandler
  public void onRoundStart(RoundStartEvent event){
    if (!isRunning) {
      isRunning = true;
      task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
        time++;

        for (ArenaSchedulerHandler handler : handlers){
          if (time % handler.getUpdateInterval() == 0)
            handler.execute();
        }
      }, 0, 1L);
    }

    for (ArenaSchedulerHandler handler : handlers)
      handler.roundStart(event.getArena());
  }

  @EventHandler
  public void onRoundEnd(RoundEndEvent event){
    arenas.remove(event.getArena());
    if (arenas.isEmpty() && task != null) {
      task.cancel();
      isRunning = false;
      time = 0;
    }

    for (ArenaSchedulerHandler handler : handlers)
      handler.roundEnd(event.getArena());
  }
}
