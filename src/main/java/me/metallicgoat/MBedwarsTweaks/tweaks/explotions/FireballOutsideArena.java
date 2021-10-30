package me.metallicgoat.MBedwarsTweaks.tweaks.explotions;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.Arena;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.projectiles.ProjectileSource;

public class FireballOutsideArena implements Listener {

    @EventHandler
    public void onImpact(ProjectileHitEvent e){
        ProjectileSource shooter = e.getEntity().getShooter();
        if(shooter instanceof Player){
            Player player = (Player) shooter;
            Arena arena = BedwarsAPI.getGameAPI().getArenaByPlayer(player);

            if(arena != null && !arena.isInside(e.getEntity().getLocation())){
                e.getEntity().remove();
            }
        }
    }
}
