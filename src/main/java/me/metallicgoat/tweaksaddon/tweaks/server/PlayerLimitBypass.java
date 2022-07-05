package me.metallicgoat.tweaksaddon.tweaks.server;

import me.metallicgoat.tweaksaddon.config.ConfigValue;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class PlayerLimitBypass implements Listener {
    @EventHandler
    public void onPlayerLoginEvent(PlayerLoginEvent event){
        if (ConfigValue.player_limit_bypass && event.getResult() == PlayerLoginEvent.Result.KICK_FULL) {
            event.allow();
        }
    }
}
