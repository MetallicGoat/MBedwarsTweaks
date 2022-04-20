package me.metallicgoat.tweaksaddon.tweaks.cosmetic;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.Arena;
import me.metallicgoat.tweaksaddon.config.ConfigValue;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class FinalStrike implements Listener {

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        if (!ConfigValue.final_strike_enabled)
            return;

        final Player player = e.getEntity();
        final Arena arena = BedwarsAPI.getGameAPI().getArenaByPlayer(player);

        if (arena == null)
            return;

        if (arena.isBedDestroyed(arena.getPlayerTeam(player)))
            e.getEntity().getWorld().strikeLightningEffect(e.getEntity().getLocation());
    }
}