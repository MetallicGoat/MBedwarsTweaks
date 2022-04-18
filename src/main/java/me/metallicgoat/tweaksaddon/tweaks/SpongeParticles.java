package me.metallicgoat.tweaksaddon.tweaks;

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
class SpongeParticleTask extends BukkitRunnable{

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
        for(Location location:getParticles(block.getLocation(), radius)) {
            location.add(.5, .5, .5);
            VarParticle.PARTICLE_CLOUD.play(location);
            location.add(.15, .15, .15);
            VarParticle.PARTICLE_CLOUD.play(location);
        }
        radius++;
    }

    //Credit to MherZaqaryan from his open source sponge addon
    public List<Location> getParticles(Location loc, int radius) {
        final List<Location> result = new ArrayList<>();
        final Block start = loc.getWorld().getBlockAt(loc);
        final int iterations = (radius * 2) + 1;
        final List<Block> blocks = new ArrayList<>(iterations * iterations * iterations);

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    blocks.add(start.getRelative(x, y, z));
                }
            }
        }

        blocks.stream().filter(b -> b.getType().equals(Material.AIR)).forEach(b -> result.add(b.getLocation()));
        return result;
    }
}
