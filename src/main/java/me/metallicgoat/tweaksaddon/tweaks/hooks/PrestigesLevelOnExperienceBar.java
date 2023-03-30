package me.metallicgoat.tweaksaddon.tweaks.hooks;

import de.marcely.bedwars.api.event.arena.RoundEndEvent;
import de.marcely.bedwars.api.event.player.PlayerIngameRespawnEvent;
import me.harsh.prestigesaddon.storage.PlayerData;
import me.metallicgoat.tweaksaddon.DependType;
import me.metallicgoat.tweaksaddon.config.ConfigValue;
import me.metallicgoat.tweaksaddon.serverevents.DependManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PrestigesLevelOnExperienceBar implements Listener {

    @EventHandler
    public void onArenaStart(RoundEndEvent event) {
        // TODO: HARSH - Check if rejoinable players are also stored in #getPlayers
        for (Player player : event.getArena().getPlayers())
            setPlayerLevel(player);
    }

    // This will get called when the player rejoins too
    @EventHandler
    public void onPlayerRespawn(PlayerIngameRespawnEvent event) {
        setPlayerLevel(event.getPlayer());
    }

    private void setPlayerLevel(Player player) {
        // Check Enabled
        if(!ConfigValue.prestiges_level_on_exp_bar)
            return;

        // Check loaded
        if(!DependManager.isPresent(DependType.PRESTIGE_ADDON))
            return;

        final PlayerData data = PlayerData.from(player);

        player.setLevel(data.getStars());
        player.setExp((float) data.getProgressDouble());
    }
}