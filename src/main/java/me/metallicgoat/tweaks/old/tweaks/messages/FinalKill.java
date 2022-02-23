package me.metallicgoat.tweaks.old.tweaks.messages;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.Team;
import de.marcely.bedwars.api.event.player.PlayerIngameDeathEvent;
import de.marcely.bedwars.api.message.Message;
import me.metallicgoat.tweaks.utils.ServerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class FinalKill implements Listener {
    @EventHandler
    public void onPlayerDeath(PlayerIngameDeathEvent e){
        final Player p = e.getPlayer();
        final Arena arena = BedwarsAPI.getGameAPI().getArenaByPlayer(p);
        final boolean enabled = ServerManager.getConfig().getBoolean("Final-Kill-Message");
        if(enabled) {
            if (arena != null) {
                final Team team = arena.getPlayerTeam(p);
                Message message = e.getDeathMessage();

                if(arena.isBedDestroyed(team) && message != null){
                    String finalMessage = (message.done(false) + " &b&lFINAL KILL!");
                    e.setDeathMessage(Message.build(finalMessage));
                }
            }
        }
    }
}
