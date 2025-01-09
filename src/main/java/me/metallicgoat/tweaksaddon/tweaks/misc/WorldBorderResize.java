package me.metallicgoat.tweaksaddon.tweaks.misc;

import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.ArenaPersistentStorage;
import de.marcely.bedwars.api.arena.ArenaStatus;
import de.marcely.bedwars.api.arena.RegenerationType;
import de.marcely.bedwars.api.event.arena.ArenaStatusChangeEvent;
import de.marcely.bedwars.api.event.arena.ArenaUnloadEvent;
import de.marcely.bedwars.api.event.arena.RoundStartEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;
import me.metallicgoat.tweaksaddon.config.MainConfig;
import org.bukkit.Bukkit;
import org.bukkit.WorldBorder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

public class WorldBorderResize implements Listener {

  private static final String KEY_ORIGINAL_SIZE = "tweaks:world_border_original_size";

  private Map<Arena, BukkitTask> activeTasks = new HashMap<>();

  @EventHandler
  public void onRoundStart(RoundStartEvent event) {
    start(event.getArena());
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onRoundStop(ArenaStatusChangeEvent event) {
    if (event.getNewStatus() != ArenaStatus.RUNNING)
      cancel(event.getArena());
  }

  @EventHandler
  public void onArenaUnload(ArenaUnloadEvent event) {
    cancel(event.getArena());
  }

  private void start(Arena arena) {
    if (!MainConfig.world_border_resize_enabled || arena.getRegenerationType() != RegenerationType.WORLD)
      return;
    if (arena.getGameWorld().getWorldBorder().getSize() >= 100_000) // probably vanilla border (disabled)
      return;

    cancel(arena);

    final BukkitTask scheduler = Bukkit.getScheduler().runTaskLater(
        MBedwarsTweaksPlugin.getInstance(),
        () -> resize(arena),
        20 * MainConfig.world_border_resize_start_time);

    this.activeTasks.put(arena, scheduler);
  }

  private void resize(Arena arena) {
    final WorldBorder border = arena.getGameWorld().getWorldBorder();

    // store original size
    {
      final ArenaPersistentStorage storage = arena.getPersistentStorage();

      storage.setSynchronizedFlag(KEY_ORIGINAL_SIZE, false);
      storage.set(KEY_ORIGINAL_SIZE, border.getSize());
      storage.saveAsync();
    }

    // tell bukkit to resize
    border.setSize(
        border.getSize()*(MainConfig.world_border_resize_scale/100D),
        MainConfig.world_border_resize_duration);

    // clean up
    this.activeTasks.remove(arena);
  }

  private void cancel(Arena arena) {
    if (!MainConfig.world_border_resize_enabled)
      return;

    // cancel scheduler
    {
      final BukkitTask scheduler = this.activeTasks.remove(arena);

      if (scheduler != null)
        scheduler.cancel();
    }

    // restore border size
    {
      final ArenaPersistentStorage storage = arena.getPersistentStorage();
      final Optional<Double> originalSize = storage.getDouble(KEY_ORIGINAL_SIZE);

      if (originalSize.isPresent()) {
        final WorldBorder border = arena.getGameWorld().getWorldBorder();

        border.setSize(originalSize.get(), 1);
        storage.remove(KEY_ORIGINAL_SIZE);
      }
    }
  }
}
