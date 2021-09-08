package me.metallicgoat.MBedwarsTweaks.tweaks.finalkill;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.Team;
import me.metallicgoat.MBedwarsTweaks.utils.ServerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class FinalStrike implements Listener {

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        Arena arena = BedwarsAPI.getGameAPI().getArenaByPlayer(p);
        boolean enabled = ServerManager.getConfig().getBoolean("Final-Kill-Strike.Enabled");
        if(enabled) {
            if (arena != null) {
                Team team = arena.getPlayerTeam(p);
                if(arena.isBedDestroyed(team)){
                    e.getEntity().getWorld().strikeLightningEffect(e.getEntity().getLocation());
                }
            }
        }
    }
}