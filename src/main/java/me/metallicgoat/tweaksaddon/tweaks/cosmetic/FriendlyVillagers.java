package me.metallicgoat.tweaksaddon.tweaks.cosmetic;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.event.arena.RoundEndEvent;
import de.marcely.bedwars.api.event.arena.RoundStartEvent;
import de.marcely.bedwars.api.world.WorldStorage;
import de.marcely.bedwars.api.world.hologram.HologramControllerType;
import de.marcely.bedwars.api.world.hologram.HologramEntity;
import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class FriendlyVillagers implements Listener {

    // TODO this shit

    /*

    private final MBedwarsTweaksPlugin plugin = MBedwarsTweaksPlugin.getInstance();
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

        for(HologramEntity hologramEntity : worldStorage.getHolograms()){
            if (hologramEntity.getControllerType() == HologramControllerType.DEALER
                    || hologramEntity.getControllerType() == HologramControllerType.UPGRADE_DEALER) {
                hologramEntity.teleport(hologramEntity.getSpawnLocation());
            }
        }

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

            final WorldStorage worldStorage = BedwarsAPI.getWorldStorage(world);

            if (worldStorage == null)
                return;

            //Get all villagers in the world
            final Collection<HologramEntity> entities = worldStorage.getHolograms();

            //For each villager
            for(HologramEntity hologramEntity : entities){

                if (hologramEntity.getControllerType() == HologramControllerType.DEALER
                        || hologramEntity.getControllerType() == HologramControllerType.UPGRADE_DEALER) {

                    //Get players in range of villager
                    final Player[] playersArray = hologramEntity.getSeeingPlayers();

                    if (playersArray.length > 0) {
                        //Get the closest player
                        final Player lookAtPlayer = Arrays.stream(playersArray).min(Comparator.comparingDouble(p -> p.getLocation().distanceSquared(hologramEntity.getLocation()))).get();

                        //Final location
                        Location moveTo = hologramEntity.getLocation().setDirection(lookAtPlayer.getLocation().subtract(hologramEntity.getLocation()).toVector());
                        hologramEntity.teleport(moveTo);
                    }
                }
            }
        }), 0L, 4);
    }

     */
}
