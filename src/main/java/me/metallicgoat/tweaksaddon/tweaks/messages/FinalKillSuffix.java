package me.metallicgoat.tweaksaddon.tweaks.messages;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.Team;
import de.marcely.bedwars.api.event.player.PlayerIngameDeathEvent;
import de.marcely.bedwars.api.message.Message;
import me.metallicgoat.tweaksaddon.config.ConfigValue;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class FinalKillSuffix implements Listener {
    @EventHandler
    public void onPlayerDeath(PlayerIngameDeathEvent e){

        if(!ConfigValue.final_kill_suffix_enabled)
            return;

        final Player p = e.getPlayer();
        final Arena arena = BedwarsAPI.getGameAPI().getArenaByPlayer(p);

        if (arena == null)
            return;

        final Team team = arena.getPlayerTeam(p);
        final Message message = e.getDeathMessage();

        if(arena.isBedDestroyed(team) && message != null) {
            final String finalMessage = (message.done(false) + ConfigValue.final_kill_suffix);
            e.setDeathMessage(Message.build(finalMessage));
        }
    }
}
