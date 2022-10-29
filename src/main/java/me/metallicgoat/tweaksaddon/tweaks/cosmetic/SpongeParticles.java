package me.metallicgoat.tweaksaddon.tweaks.cosmetic;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.tools.VarParticle;
import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;
import me.metallicgoat.tweaksaddon.config.ConfigValue;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class SpongeParticles implements Listener {

    @EventHandler
    public void onSpongePlace(BlockPlaceEvent e) {
        final Arena arena = BedwarsAPI.getGameAPI().getArenaByPlayer(e.getPlayer());
        final Block block = e.getBlock();

        if (!ConfigValue.sponge_particles_enabled ||
                e.isCancelled() ||
                arena == null ||
                arena.getGameWorld() == null ||
                !block.getType().equals(Material.SPONGE))
            return;

        new SpongeParticleTask(block).runTaskTimer(MBedwarsTweaksPlugin.getInstance(), 0L, 8L);
    }
}
class SpongeParticleTask extends BukkitRunnable {

    private final Block block;

    private int radius = 1;

    public SpongeParticleTask(Block block){
        this.block = block;
    }

    @Override
    public void run() {
        if (radius > 4) {
            cancel();
            return;
        }

        for(Location location : getParticles(block.getLocation(), radius)) {
            VarParticle.PARTICLE_CLOUD.play(location);
            VarParticle.PARTICLE_CLOUD.play(location.add(.15, .15, .15));
        }

        radius++;
    }

    public List<Location> getParticles(Location start, int radius) {
        final List<Location> locations = new ArrayList<>();

        for(double x = start.getX() - radius; x <= start.getX() + radius; x++){
            for(double y = start.getY() - radius; y <= start.getY() + radius; y++){
                for(double z = start.getZ() - radius; z <= start.getZ() + radius; z++){
                    final Location location = new Location(start.getWorld(), x + .5, y + .5, z + .5);

                    if(location.getBlock().getType() == Material.AIR)
                        locations.add(location);
                }
            }
        }

        return locations;
    }
}
