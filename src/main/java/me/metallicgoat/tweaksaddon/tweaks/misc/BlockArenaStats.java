package me.metallicgoat.tweaksaddon.tweaks.misc;

import de.marcely.bedwars.api.GameAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.event.player.PlayerStatChangeEvent;
import me.metallicgoat.tweaksaddon.Util;
import me.metallicgoat.tweaksaddon.config.ConfigValue;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class BlockArenaStats implements Listener {

    @EventHandler
    public void onStatUpdate(PlayerStatChangeEvent event){
        if(!ConfigValue.block_stat_change_enabled || ConfigValue.block_stat_change_arenas.isEmpty())
            return;

        final Player player = Bukkit.getPlayer(event.getStats().getPlayerUUID());
        final Arena playerArena = player != null ? GameAPI.get().getArenaByPlayer(player) : null;

        if(playerArena == null)
            return;

        for(String arenaName : ConfigValue.block_stat_change_arenas){
            if(Util.parseArenas(arenaName).contains(playerArena)){
                event.setCancelled(true);
                return;
            }
        }
    }
}
