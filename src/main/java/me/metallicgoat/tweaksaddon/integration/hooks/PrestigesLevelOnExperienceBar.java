package me.metallicgoat.tweaksaddon.integration.hooks;

import de.marcely.bedwars.api.event.arena.RoundEndEvent;
import de.marcely.bedwars.api.event.player.PlayerIngameRespawnEvent;
import de.marcely.bedwars.tools.Helper;
import java.util.function.Consumer;
import me.harsh.prestigesaddon.PrestigeAddon;
import me.harsh.prestigesaddon.api.PrestigePlayer;
import me.metallicgoat.tweaksaddon.config.MainConfig;
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
    if (!MainConfig.prestiges_level_on_exp_bar)
      return;

    getPrestigePlayer(player, data -> {
      player.setLevel(data.getStars());
      player.setExp(data.getProgress() /* It goes from 0 - 100 */ / 100F);
    });
  }

  private static void getPrestigePlayer(Player player, Consumer<PrestigePlayer> callback) {
    {
      final PrestigePlayer cached = PrestigeAddon.getInstance().getPlayer(player);

      if (cached != null) {
        callback.accept(cached);
        return;
      }
    }

    PrestigeAddon.getInstance().getFromStorage(
        player.getUniqueId(),
        player.getName(),
        data -> Helper.get().synchronize(() -> callback.accept(data)));
  }
}