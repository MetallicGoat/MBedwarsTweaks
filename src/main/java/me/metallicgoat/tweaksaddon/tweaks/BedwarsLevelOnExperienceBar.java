package me.metallicgoat.tweaksaddon.tweaks;

import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.ArenaStatus;
import de.marcely.bedwars.api.event.arena.ArenaStatusChangeEvent;
import de.marcely.bedwars.api.event.player.PlayerIngameRespawnEvent;
import de.marcely.bedwars.api.event.player.PlayerRejoinArenaEvent;
import me.harsh.prestigesaddon.storage.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class BedwarsLevelOnExperienceBar implements Listener {

    @EventHandler
    public void onArenaStart(ArenaStatusChangeEvent event){
        // Arena has started
        if (event.getNewStatus() == ArenaStatus.RUNNING){
            for (Player player : event.getArena().getPlayers()) {
                // TODO: Check if rejoinable players are also stored in #getPlayers
                setPlayerLevel(player);
            }
        }
    }
    @EventHandler
    public void onPlayerRespawn(PlayerIngameRespawnEvent event){
        setPlayerLevel(event.getPlayer());
    }
    @EventHandler
    public void onPlayerRejoin(PlayerRejoinArenaEvent event){
        setPlayerLevel(event.getPlayer());
    }

    private void setPlayerLevel(Player player){
        final PlayerData data = PlayerData.from(player);
        player.setLevel(data.getStars());
        player.setExp((float) data.getProgressDouble());

    }
}
