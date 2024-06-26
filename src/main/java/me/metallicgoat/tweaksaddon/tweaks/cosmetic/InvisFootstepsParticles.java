package me.metallicgoat.tweaksaddon.tweaks.cosmetic;

import de.marcely.bedwars.api.GameAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.event.player.PlayerQuitArenaEvent;
import de.marcely.bedwars.tools.NMSHelper;
import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;
import me.metallicgoat.tweaksaddon.config.MainConfig;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

import java.util.IdentityHashMap;
import java.util.Map;

// ONLY FOR 1.8.8 -> 1.12.2
public class InvisFootstepsParticles implements Listener {

  private final Map<Player, Long> playerMoveTimes = new IdentityHashMap<>();
  private BukkitTask task = null;

  @EventHandler
  public void onPlayerMove(PlayerMoveEvent event) {
    if (!MainConfig.play_footsteps_with_invis || NMSHelper.get().getVersion() > 12)
      return;

    final Player player = event.getPlayer();
    final Arena arena = GameAPI.get().getArenaByPlayer(player);

    if (arena == null || arena.getSpectators().contains(player) || !player.hasPotionEffect(PotionEffectType.INVISIBILITY))
      return;

    this.playerMoveTimes.put(player, System.currentTimeMillis());

    startTaskIfNeeded();
  }

  @EventHandler
  public void onPlayerQuit(PlayerQuitArenaEvent event) {
    if (!MainConfig.play_footsteps_with_invis || NMSHelper.get().getVersion() > 12)
      return;

    this.playerMoveTimes.remove(event.getPlayer());

    stopTaskIfEmpty();
  }

  @SuppressWarnings("deprecation") // Player#playEffect()
  private void startTaskIfNeeded() {
    if (this.task != null)
      return;

    this.task = Bukkit.getScheduler().runTaskTimer(MBedwarsTweaksPlugin.getInstance(), () -> {
      // Play particle if player moved within 2 seconds
      this.playerMoveTimes.entrySet().removeIf(entry -> {
        final Player player = entry.getKey();
        final Arena arena = GameAPI.get().getArenaByPlayer(player);

        if (!player.hasPotionEffect(PotionEffectType.INVISIBILITY) || arena == null || arena.getSpectators().contains(player))
          return true;

        if (System.currentTimeMillis() - entry.getValue() < 200) {
          final Location loc = getSuitableParticleLocation(player);

          if (loc != null)
            player.playEffect(loc, Effect.FOOTSTEP, 1);

        }

        return false;
      });

      stopTaskIfEmpty();

    }, 0, 7);
  }

  private void stopTaskIfEmpty() {
    if (!this.playerMoveTimes.isEmpty())
      return;

    if (this.task != null) {
      this.task.cancel();
      this.task = null;
    }
  }

  @Nullable
  private Location getSuitableParticleLocation(Player player) {
    final Location location = player.getLocation().clone();

    int i = 0;
    Material blockType = location.getBlock().getRelative(0, -1, 0).getType();

    while (blockType == Material.AIR && i < 2) {
      location.add(0, -1, 0);
      blockType = location.getBlock().getRelative(0, -1, 0).getType();
      i++;
    }

    if (blockType == Material.AIR)
      return null;

    location.setY(Math.floor(location.getY()) + 0.05);

    return location;
  }
}
