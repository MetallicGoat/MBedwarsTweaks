package me.metallicgoat.MBedwarsTweaks.tweaks.misc;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.event.arena.RoundEndEvent;
import de.marcely.bedwars.api.event.arena.RoundStartEvent;
import de.marcely.bedwars.api.world.hologram.HologramEntity;
import me.metallicgoat.MBedwarsTweaks.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class FriendlyVillagers implements Listener {

    private BukkitTask task;
    private final List<World> worlds = new ArrayList<>();
    private boolean isRunning = false;

    @EventHandler
    public void onRoundStart(RoundStartEvent e){
        World world = e.getArena().getGameWorld();
        if(world != null && !worlds.contains(world)){
            worlds.add(world);
        }
        if(!isRunning && !worlds.isEmpty()){
            startLooking();
            isRunning = true;
        }
    }

    @EventHandler
    public void onRoundEnd(RoundEndEvent e){
        World world = e.getArena().getGameWorld();
        if(world != null){
            worlds.remove(world);
        }
        if (worlds.isEmpty()) {
            task.cancel();
            isRunning = false;
        }
    }


    private void startLooking(){

        //For each active world (Every Tick)
        task = Bukkit.getScheduler().runTaskTimer(plugin(), () -> worlds.forEach(world -> {

            //Get all villagers in the world
            Collection<HologramEntity> entity = BedwarsAPI.getWorldStorage(world).getHolograms();

            //For each villager
            entity.forEach(hologramEntity -> {
                //Get players in range of villager
                Collection<Player> players = Arrays.asList(hologramEntity.getSeeingPlayers());

                if(!players.isEmpty()){

                    //Get the closest player
                    Player lookAt = Collections.min(players, Comparator.comparingDouble(p -> p.getLocation().distanceSquared(hologramEntity.getLocation())));

                    //Final location
                    Location moveTo = hologramEntity.getLocation().setDirection(lookAt.getLocation().subtract(hologramEntity.getLocation()).toVector());

                    //Smooth Look (Interpolation)
                    float currentYaw = hologramEntity.getLocation().getYaw();
                    float targetYaw = moveTo.getYaw();
                    float newYaw = currentYaw + (targetYaw - currentYaw)/4;

                    //Actually move villager
                    Location location = new Location(moveTo.getWorld(), moveTo.getX(), moveTo.getY(), moveTo.getZ(), newYaw, moveTo.getPitch());
                    hologramEntity.teleport(location);


                }
            });
        }), 0L, 1);

    }
    private static Main plugin(){
        return Main.getInstance();
    }
}
