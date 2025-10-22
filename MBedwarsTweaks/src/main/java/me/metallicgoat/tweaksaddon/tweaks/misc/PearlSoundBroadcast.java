package me.metallicgoat.tweaksaddon.tweaks.misc;

import de.marcely.bedwars.api.GameAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.tools.Helper;
import de.marcely.bedwars.tools.VarSound;
import me.metallicgoat.tweaksaddon.config.MainConfig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PearlSoundBroadcast implements Listener {

  @SuppressWarnings("ConstantConditions") // null warning for the sound
  private final static VarSound PEARL_SOUND = VarSound.from(Helper.get().getSoundByName("ENTITY_ENDERMAN_TELEPORT"));

  @EventHandler
  public void onEntityTeleportEvent(PlayerTeleportEvent event) {
    if (!MainConfig.broadcast_ender_pearl_sound || event.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL)
      return;

    final Player teleporter = event.getPlayer();
    final Arena arena = GameAPI.get().getArenaByPlayer(teleporter);

    if (arena == null)
      return;

    for (Player player : arena.getPlayers()) {
      // "Most sounds can be heard 16 blocks away" (Try 12 it gets quieter)
      // https://minecraft.wiki/w/Sound
      if (player.getLocation().distance(event.getTo()) > 12) {
        PEARL_SOUND.play(player);
      }
    }
  }
}
