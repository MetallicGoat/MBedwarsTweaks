package me.metallicgoat.tweaksaddon.integration.hooks;

import de.marcely.bedwars.api.event.arena.RoundEndEvent;
import de.marcely.bedwars.api.event.player.PlayerIngamePostRespawnEvent;
import de.marcely.bedwars.tools.Helper;
import java.util.Optional;
import java.util.function.Consumer;
import me.metallicgoat.tweaksaddon.config.MainConfig;
import me.tvhee.prestigesaddon.api.PrestigeAddonAPI;
import me.tvhee.prestigesaddon.api.PrestigePlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PrestigesLevelOnExperienceBar implements Listener {

  private final PrestigeAddonAPI prestigeAddonAPI;

  public PrestigesLevelOnExperienceBar() {
    prestigeAddonAPI = (PrestigeAddonAPI) Bukkit.getServer().getPluginManager().getPlugin("PrestigeAddon");
  }

  @EventHandler
  public void onArenaStart(RoundEndEvent event) {
    // TODO: HARSH - Check if rejoinable players are also stored in #getPlayers
    for (Player player : event.getArena().getPlayers())
      setPlayerLevel(player);
  }

  // This will get called when the player rejoins too
  @EventHandler
  public void onPlayerPostRespawn(PlayerIngamePostRespawnEvent event) {
    setPlayerLevel(event.getPlayer());
  }

  private void setPlayerLevel(Player player) {
    // Check Enabled
    if (!MainConfig.prestiges_level_on_exp_bar)
      return;

    getPrestigePlayer(player, data -> {
      player.setLevel(data.getStars());
      player.setExp(data.getProgress() / 100F); // It goes from 0 - 100
    });
  }

  private void getPrestigePlayer(Player player, Consumer<PrestigePlayer> callback) {
    {
      final Optional<PrestigePlayer> cached = prestigeAddonAPI.getPlayerDataSync(player);

      if (cached.isPresent()) {
        callback.accept(cached.get());
        return;
      }
    }

    prestigeAddonAPI.getPlayerData(
        player,
        data -> Helper.get().synchronize(() -> callback.accept(data))
    );
  }
}