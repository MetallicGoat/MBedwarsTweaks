package me.metallicgoat.tweaksaddon.tweaks.cosmetic;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.GameAPI;
import de.marcely.bedwars.api.event.arena.RoundEndEvent;
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
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FriendlyVillagers implements Listener {

  // 6+ hours has been spent on this. Rewritten like 6 times
  private final MBedwarsTweaksPlugin plugin = MBedwarsTweaksPlugin.getInstance();
  private final List<World> worlds = new ArrayList<>();
  private BukkitTask task;
  private boolean isRunning = false;

  @EventHandler
  public void onRoundStart(RoundStartEvent e) {
    if (!MainConfig.friendly_villagers_enabled)
      return;

    // Add arena to update list
    final World world = e.getArena().getGameWorld();
    if (world != null && !worlds.contains(world))
      worlds.add(world);

    // Start task if not running
    if (!isRunning && !worlds.isEmpty()) {
      startLooking();
      isRunning = true;
    }
  }

  @EventHandler
  public void onRoundEnd(RoundEndEvent e) {
    final World world = e.getArena().getGameWorld();
    if (world == null || !worlds.contains(world))
      return;

    final WorldStorage worldStorage = BedwarsAPI.getWorldStorage(world);

    if (worldStorage == null)
      return;

    // Reset Position
    for (HologramEntity hologramEntity : worldStorage.getHolograms()) {
      if (hologramEntity.getControllerType() == HologramControllerType.DEALER
          || hologramEntity.getControllerType() == HologramControllerType.UPGRADE_DEALER) {
        hologramEntity.teleport(hologramEntity.getSpawnLocation(), false);
      }
    }

    // Dont update it anymore
    worlds.remove(world);

    // Dont update at all if no arenas are running
    if (worlds.isEmpty() && task != null) {
      task.cancel();
      isRunning = false;
    }
  }


  private void startLooking() {
    // For each active world (Every Tick)
    task = Bukkit.getScheduler().runTaskTimer(plugin, () -> worlds.forEach(world -> {

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
          final List<Player> visiblePlayers = new ArrayList<>();

          for (Player player : hologramEntity.getSeeingPlayers()) {

            // Check for same world & within range
            if (player.getWorld() != hologramEntity.getWorld().asBukkit() ||
                GameAPI.get().getArenaByPlayer(player) == null ||
                GameAPI.get().getSpectatingPlayers().contains(player) ||
                player.getLocation().distance(hologramEntity.getLocation()) > MainConfig.friendly_villagers_range)
              continue;

            // Check if villager can even see player
            boolean canSee = true;

            if (MainConfig.friendly_villagers_check_visibility) {
              final Set<Material> transparentMaterials = Stream.of(Material.AIR, Material.BARRIER).collect(Collectors.toSet());
              final int distance = (int) player.getLocation().distance(hologramEntity.getLocation());

              // Blocks in the way
              final List<Block> blocks = player.getLineOfSight(transparentMaterials, distance);

              if (!blocks.isEmpty())
                canSee = false;
            }

            if (canSee)
              visiblePlayers.add(player);
          }

          if (!visiblePlayers.isEmpty()) {
            // Get the closest player
            final Player lookAtPlayer = visiblePlayers.stream().min(Comparator.comparingDouble(p -> p.getLocation().distance(hologramEntity.getLocation()))).get();

            // Final location
            final Location moveTo = hologramEntity.getLocation().setDirection(lookAtPlayer.getLocation().subtract(hologramEntity.getLocation()).toVector());

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
  }
}
