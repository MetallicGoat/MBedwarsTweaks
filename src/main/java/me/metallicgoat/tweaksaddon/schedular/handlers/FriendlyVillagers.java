package me.metallicgoat.tweaksaddon.schedular.handlers;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.GameAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.world.WorldStorage;
import de.marcely.bedwars.api.world.hologram.HologramControllerType;
import de.marcely.bedwars.api.world.hologram.HologramEntity;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import me.metallicgoat.tweaksaddon.config.MainConfig;
import me.metallicgoat.tweaksaddon.schedular.ArenaScheduler;
import me.metallicgoat.tweaksaddon.schedular.ArenaSchedulerHandler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

public class FriendlyVillagers extends ArenaSchedulerHandler {

  private final List<WorldStorage> worldStorageList = new ArrayList<>();

  public FriendlyVillagers(ArenaScheduler scheduler) {
    super(scheduler);
  }

  // 6+ hours has been spent on this. Rewritten like 6 times
  @Override
  public long getUpdateInterval() {
    return 2;
  }

  @Override
  public void execute() {

    for (WorldStorage worldStorage : worldStorageList) {
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
            boolean ok = true;
            if (MainConfig.friendly_villagers_check_visibility) {
              final BlockIterator iterator = new BlockIterator(player.getWorld(), hologramEntity.getLocation().toVector(),
                  player.getLocation().clone().subtract(hologramEntity.getLocation()).toVector(), 1, MainConfig.friendly_villagers_range);

              while (iterator.hasNext()) {
                final Material type = iterator.next().getType();

                if (type != Material.AIR && type != Material.BARRIER) {
                  ok = false;
                  break;
                }
              }
            }

            if (ok)
              visiblePlayers.add(player);
          }

          if (visiblePlayers.size() > 0) {
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
    }
  }

  @Override
  public void roundStart(Arena arena){
    updateStorageList();
  }

  @Override
  public void roundEnd(Arena arena) {
    final World world = arena.getGameWorld();
    if (world == null)
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

    updateStorageList();
  }

  private void updateStorageList() {
    worldStorageList.clear();

    for (Arena arena : scheduler.getArenas()) {
      final World world = arena.getGameWorld();

      if (world == null)
        continue;

      final WorldStorage worldStorage = BedwarsAPI.getWorldStorage(arena.getGameWorld());

      if (worldStorage != null && !worldStorageList.contains(worldStorage))
        worldStorageList.add(worldStorage);
    }
  }
}
