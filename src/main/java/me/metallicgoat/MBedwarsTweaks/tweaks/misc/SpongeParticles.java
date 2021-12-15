package me.metallicgoat.MBedwarsTweaks.tweaks.misc;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.tools.VarParticle;
import me.metallicgoat.MBedwarsTweaks.Main;
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
        Arena arena = BedwarsAPI.getGameAPI().getArenaByPlayer(e.getPlayer());
        Block block = e.getBlock();

        if (e.isCancelled() || arena == null)
            return;

        World world = arena.getGameWorld();

        if (!block.getType().equals(Material.SPONGE) || world == null)
            return;

        new SpongeParticleTask(block).runTaskTimer(Main.getInstance(), 0L, 10L);
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
            VarParticle.PARTICLE_CLOUD.play(block.getWorld(), location, 1);
        }
        radius++;
    }

    //Credit to MherZaqaryan from his open source sponge addon
    public List<Location> getParticles(Location loc, int radius) {
        List<Location> result = new ArrayList<>();
        Block start = loc.getWorld().getBlockAt(loc);
        int iterations = (radius * 2) + 1;
        List<Block> blocks = new ArrayList<>(iterations * iterations * iterations);
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
