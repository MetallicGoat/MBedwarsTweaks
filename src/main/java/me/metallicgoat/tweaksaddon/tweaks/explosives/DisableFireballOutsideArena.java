package me.metallicgoat.tweaksaddon.tweaks.explosives;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.Arena;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.projectiles.ProjectileSource;

public class DisableFireballOutsideArena implements Listener {

    //TODO config?

    @EventHandler
    public void onImpact(ProjectileHitEvent event){
        final ProjectileSource shooter = event.getEntity().getShooter();
        if(shooter instanceof Player){
            final Player player = (Player) shooter;
            final Arena arena = BedwarsAPI.getGameAPI().getArenaByPlayer(player);

            if(arena != null && !arena.isInside(event.getEntity().getLocation())){
                event.getEntity().remove();
            }
        }
    }
}
