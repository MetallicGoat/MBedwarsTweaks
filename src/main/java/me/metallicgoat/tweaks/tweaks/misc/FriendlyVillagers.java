package me.metallicgoat.tweaks.tweaks.misc;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.event.arena.RoundEndEvent;
import de.marcely.bedwars.api.event.arena.RoundStartEvent;
import de.marcely.bedwars.api.world.WorldStorage;
import de.marcely.bedwars.api.world.hologram.HologramControllerType;
import de.marcely.bedwars.api.world.hologram.HologramEntity;
import me.metallicgoat.tweaks.MBedwarsTweaks;
import me.metallicgoat.tweaks.utils.ServerManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class FriendlyVillagers implements Listener {

    private final MBedwarsTweaks plugin = MBedwarsTweaks.getInstance();
    private BukkitTask task;
    private final List<World> worlds = new ArrayList<>();
    private boolean isRunning = false;

    @EventHandler
    public void onRoundStart(RoundStartEvent e){
        if(!ServerManager.getConfig().getBoolean("Friendly-Villagers"))
            return;

        final World world = e.getArena().getGameWorld();

        if (world != null && !worlds.contains(world))
            worlds.add(world);

        if (!isRunning && !worlds.isEmpty()) {
            startLooking();
            isRunning = true;
        }
    }

    @EventHandler
    public void onRoundEnd(RoundEndEvent e){
        World world = e.getArena().getGameWorld();
        if(world == null)
            return;

        WorldStorage worldStorage = BedwarsAPI.getWorldStorage(world);

        if(worldStorage == null)
            return;

        worldStorage.getHolograms().forEach(hologramEntity -> {
            if (hologramEntity.getControllerType() == HologramControllerType.DEALER
                    || hologramEntity.getControllerType() == HologramControllerType.UPGRADE_DEALER) {
                hologramEntity.teleport(hologramEntity.getSpawnLocation());
            }
        });

        if (!ServerManager.getConfig().getBoolean("Friendly-Villagers"))
            return;

        worlds.remove(world);

        if (worlds.isEmpty() && task != null) {
            task.cancel();
            isRunning = false;
        }
    }


    private void startLooking() {

        //For each active world (Every Tick)
        task = Bukkit.getScheduler().runTaskTimer(plugin, () -> worlds.forEach(world -> {

            WorldStorage worldStorage = BedwarsAPI.getWorldStorage(world);

            if (worldStorage == null)
                return;

            //Get all villagers in the world
            Collection<HologramEntity> entity = worldStorage.getHolograms();

            //For each villager
            entity.forEach(hologramEntity -> {

                if (hologramEntity.getControllerType() == HologramControllerType.DEALER
                        || hologramEntity.getControllerType() == HologramControllerType.UPGRADE_DEALER) {

                    //Get players in range of villager
                    Player[] playersArray = hologramEntity.getSeeingPlayers();

                    if (playersArray.length > 0) {
                        //Get the closest player
                        Player lookAtPlayer = Arrays.stream(playersArray).min(Comparator.comparingDouble(p -> p.getLocation().distanceSquared(hologramEntity.getLocation()))).get();

                        //Final location
                        Location moveTo = hologramEntity.getLocation().setDirection(lookAtPlayer.getLocation().subtract(hologramEntity.getLocation()).toVector());

                        //Smooth Look (Interpolation)
                        //Time wasted so far: 4 fucking hours
                        float currentYaw = hologramEntity.getLocation().getYaw();
                        float targetYaw = moveTo.getYaw();

                        hologramEntity.teleport(calculateMove(moveTo, currentYaw, targetYaw));
                    }
                }
            });
        }), 0L, 1);
    }

    private static Location calculateMove(Location moveTo, float currentYaw, float targetYaw){
        while (targetYaw >= 180F) {
            targetYaw -= 360F;
        }
        while (targetYaw < -180F) {
            targetYaw += 360F;
        }
        double adjusted = targetYaw - 40;
        while (adjusted >= 180F) {
            adjusted -= 360F;
        }
        while (adjusted < -180F) {
            adjusted += 360F;
        }
        if (adjusted > currentYaw) {
            moveTo.setYaw((float) adjusted);
        }
        if (adjusted != currentYaw) {
            adjusted = targetYaw + 40;
            while (adjusted >= 180F) {
                adjusted -= 360F;
            }
            while (adjusted < -180F) {
                adjusted += 360F;
            }
            if (adjusted < currentYaw) {
                moveTo.setYaw((float) adjusted);
            }
        }
        moveTo.setYaw(targetYaw);
        return moveTo;
    }
}
