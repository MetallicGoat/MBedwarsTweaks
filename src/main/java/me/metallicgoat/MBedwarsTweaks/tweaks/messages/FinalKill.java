package me.metallicgoat.MBedwarsTweaks.tweaks.messages;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.Team;
import de.marcely.bedwars.api.event.player.PlayerIngameDeathEvent;
import de.marcely.bedwars.api.message.Message;
import me.metallicgoat.MBedwarsTweaks.utils.ServerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class FinalKill implements Listener {
    @EventHandler
    public void onPlayerDeath(PlayerIngameDeathEvent e){
        Player p = e.getPlayer();
        Arena arena = BedwarsAPI.getGameAPI().getArenaByPlayer(p);
        boolean enabled = ServerManager.getConfig().getBoolean("Final-Kill-Message");
        if(enabled) {
            if (arena != null) {
                Team team = arena.getPlayerTeam(p);
                String message = e.getDeathMessage().done(false);
                if(arena.isBedDestroyed(team) && !message.isEmpty()){
                    message += " &b&lFINAL KILL!";
                    e.setDeathMessage(Message.build(message));
                }
            }
        }
    }
}
