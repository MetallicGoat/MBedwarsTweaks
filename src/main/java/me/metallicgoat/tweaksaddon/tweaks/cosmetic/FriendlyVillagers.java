package me.metallicgoat.tweaksaddon.tweaks.cosmetic;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.GameAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.ArenaStatus;
import de.marcely.bedwars.api.event.arena.ArenaDeleteEvent;
import de.marcely.bedwars.api.event.arena.ArenaStatusChangeEvent;
import de.marcely.bedwars.api.event.arena.RoundStartEvent;
import de.marcely.bedwars.api.world.WorldStorage;
import de.marcely.bedwars.api.world.hologram.HologramControllerType;
import de.marcely.bedwars.api.world.hologram.HologramEntity;
import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;
import me.metallicgoat.tweaksaddon.config.MainConfig;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BlockIterator;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class FriendlyVillagers implements Listener {

  private static final Set<Material> TRANSPARENT_MATERIALS = Arrays.stream(Material.values())
      .filter(material -> material.isTransparent() || material.name().contains("SLAB"))
      .collect(Collectors.toSet());

  // 6+ hours has been spent on this. Rewritten like 6 times
  // Harsh: I am proud of you!
  private final MBedwarsTweaksPlugin plugin = MBedwarsTweaksPlugin.getInstance();
  private final Map<World, WorldState> worlds = new ConcurrentHashMap<>();
  private final BukkitTask[] tasks = new BukkitTask[2];
  private boolean isRunning = false;

  @EventHandler
  public void onRoundStart(RoundStartEvent e) {
    if (!MainConfig.friendly_villagers_enabled)
      return;

    // Add arena to update list
    final World world = e.getArena().getGameWorld();

    if (world == null)
      return;

    worlds.compute(world, (g0, state) -> {
      if (state == null)
        state = new WorldState();

      if (!state.activeArenas.contains(e.getArena()))
        state.activeArenas.add(e.getArena());

      return state;
    });

    // Start task if not running
    if (!isRunning) {
      startLooking();
      isRunning = true;
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onArenaStatusChangeEvent(ArenaStatusChangeEvent e) {
    if (e.getNewStatus() == e.getOldStatus())
      return;
    if (e.getOldStatus() != ArenaStatus.RUNNING && e.getOldStatus() != ArenaStatus.END_LOBBY)
      return;
    if (e.getNewStatus() == ArenaStatus.RUNNING || e.getNewStatus() == ArenaStatus.END_LOBBY)
      return;

    removeArena(e.getArena());
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onArenaDeleteEvent(ArenaDeleteEvent e) {
    removeArena(e.getArena());
  }

  private void removeArena(Arena arena) {
    final World world = arena.getGameWorld();

    if (world == null)
      return;

    final WorldState newState = worlds.computeIfPresent(world, (g0, state) -> {
      state.activeArenas.remove(arena);

      return state.activeArenas.isEmpty() ? null : state;
    });

    if (newState != null)
      return;

    final WorldStorage worldStorage = BedwarsAPI.getWorldStorage(world);

    if (worldStorage != null)
      return;

    // Reset Position
    for (HologramEntity hologramEntity : worldStorage.getHolograms()) {
      if (hologramEntity.getControllerType() == HologramControllerType.DEALER
          || hologramEntity.getControllerType() == HologramControllerType.UPGRADE_DEALER) {
        hologramEntity.teleport(hologramEntity.getSpawnLocation(), false);
      }
    }

    // Dont update at all if no arenas are running
    if (isRunning && worlds.isEmpty()) {
      for (BukkitTask task : tasks) {
        if (task != null)
          task.cancel();
      }

      isRunning = false;
    }
  }


  private void startLooking() {
    // For each active world (Every Tick), async
    tasks[0] = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> worlds.forEach((world, state) -> {
      final WorldStorage worldStorage = BedwarsAPI.getWorldStorage(world);

      if (worldStorage == null)
        return;

      final Collection<HologramEntity> entities = worldStorage.getHolograms();

      // For each villager
      for (HologramEntity hologramEntity : entities) {

        // Only apply shops
        if (hologramEntity.getControllerType() == HologramControllerType.DEALER
            || hologramEntity.getControllerType() == HologramControllerType.UPGRADE_DEALER) {

          // Get players in range of villager
          final Player[] holoNearbyPlayers = hologramEntity.getSeeingPlayers();
          Collection<Player> visiblePlayers = new ArrayList<>(holoNearbyPlayers.length);

          for (Player player : holoNearbyPlayers) {
            // Check for same world & within range
            if (player.getWorld() != world ||
                GameAPI.get().getArenaByPlayer(player) == null ||
                GameAPI.get().getSpectatingPlayers().contains(player) ||
                player.getLocation().distanceSquared(hologramEntity.getLocation()) > MainConfig.friendly_villagers_range * MainConfig.friendly_villagers_range)
              continue;

            visiblePlayers.add(player);
          }

          // "Ask" main thread whether holo has blocks between player
          if (MainConfig.friendly_villagers_check_visibility) {
            final HoloState holoState = state.holoStates.computeIfAbsent(hologramEntity, g0 -> new HoloState());

            holoState.renderingPlayers.set(visiblePlayers);

            if (holoState.seenPlayers.get() != null)
              visiblePlayers = holoState.seenPlayers.get();
          }

          if (!visiblePlayers.isEmpty()) {
            // Get the closest player
            final Optional<Player> optionalLookAtPlayer = visiblePlayers.stream()
                .filter(p -> p.getWorld() == world)
                .min(Comparator.comparingDouble(p -> p.getLocation().distanceSquared(hologramEntity.getLocation())));

            // Rare case, where player has changed worlds or something
            if (!optionalLookAtPlayer.isPresent())
              continue;

            final Player lookAtPlayer = optionalLookAtPlayer.get(); // The player to look at
            final Location moveTo = hologramEntity.getLocation().setDirection(lookAtPlayer.getLocation().subtract(hologramEntity.getLocation()).toVector()); // final location
            final float currentYaw = hologramEntity.getLocation().getYaw(); // where the villager is currently facing
            final float targetYaw = moveTo.getYaw(); // Where we eventually want to end up
            final float difference = targetYaw - currentYaw; // How many degrees the npc needs to turn
            final int rotationDivisor = 3; // the larger this number, the slower the npc will turn
            float newYaw; // Where the player is going to be looking

            // Its not worth doing anything for a degree this small
            if (Math.abs(difference) < 5) {
              hologramEntity.teleport(moveTo, false);
              continue;
            }

            if (Math.abs(difference) <= 180)
              // Normal scenario (already the smaller difference) easy-peasy
              newYaw = currentYaw + (difference / rotationDivisor);
            else {
              // We need to find a shorter way with some mathies
              if (difference > 0)
                newYaw = currentYaw - ((360 - difference) / rotationDivisor);
              else
                newYaw = currentYaw + ((360 + difference) / rotationDivisor);
            }

            // Make corrections
            if (newYaw < 0)
              newYaw += 360;
            else if (newYaw > 360)
              newYaw -= 360;

            // Send adjustments
            moveTo.setYaw(newYaw);
            hologramEntity.teleport(moveTo, false);
          }
        }
      }
    }), 0L, 2L);

    // Optional sync task for checking player visibilities
    if (MainConfig.friendly_villagers_check_visibility) {
      tasks[1] = Bukkit.getScheduler().runTaskTimer(plugin, () -> worlds.values().forEach(state -> {
        final Iterator<Entry<HologramEntity, HoloState>> it = state.holoStates.entrySet().iterator();

        while (it.hasNext()) {
          final Entry<HologramEntity, HoloState> e = it.next();
          final HologramEntity holo = e.getKey();

          // Use this to also clean up old, removed holos
          if (!holo.exists()) {
            it.remove();
            continue;
          }

          // Has the other thread already given us any info?
          final Collection<Player> rendering = e.getValue().renderingPlayers.get();

          if (rendering == null)
            continue;

          // Find players that can be seen
          final List<Player> seen = new ArrayList<>(rendering.size());
          final Location loc = holo.getLocation().add(0, 1.5, 0);
          final World world = holo.getWorld().asBukkit();

          for (Player player : rendering) {
            if (player.getWorld() != world)
              continue;

            final int distance = (int) player.getEyeLocation().distance(loc);

            if (distance == 0) {
              seen.add(player);
              continue;
            }

            final BlockIterator blockIterator = new BlockIterator(
                world,
                loc.toVector(),
                player.getEyeLocation().toVector().clone().subtract(loc.toVector()),
                0,
                distance
            );

            boolean hasLineOfSight = true;

            while (blockIterator.hasNext()) {
              if (!TRANSPARENT_MATERIALS.contains(blockIterator.next().getType())) {
                hasLineOfSight = false;
                break;
              }
            }

            if (hasLineOfSight)
              seen.add(player);
          }

          // Ready for the other thread to pick up :)
          e.getValue().seenPlayers.set(seen);
        }
      }), 0, 10);
    }
  }

  private static class WorldState {

    final List<Arena> activeArenas = new ArrayList<>();
    final Map<HologramEntity, HoloState> holoStates = new ConcurrentHashMap<>();
  }

  private static class HoloState {

    final AtomicReference<Collection<Player>> renderingPlayers = new AtomicReference<>();
    final AtomicReference<Collection<Player>> seenPlayers = new AtomicReference<>();
  }
}
