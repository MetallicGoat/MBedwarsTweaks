package me.metallicgoat.tweaksaddon.tweaks.misc;

import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.event.arena.RoundStartEvent;
import de.marcely.bedwars.api.event.player.PlayerIngameRespawnEvent;
import java.util.Collection;
import java.util.Map;
import me.metallicgoat.tweaksaddon.Util;
import me.metallicgoat.tweaksaddon.config.MainConfig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.Nullable;

public class PermanentEffects implements Listener {

  @EventHandler
  public void onRoundStart(RoundStartEvent event) {
    final Arena arena = event.getArena();
    final PotionEffect effect = getArenaEffects(arena);

    if (!MainConfig.permanent_effects_enabled || effect == null)
      return;

    for (Player player : arena.getPlayers())
      player.addPotionEffect(effect);
  }

  @EventHandler
  public void onRespawn(PlayerIngameRespawnEvent event) {
    final Arena arena = event.getArena();
    final PotionEffect effect = getArenaEffects(arena);

    if (!MainConfig.permanent_effects_enabled || effect == null)
      return;

    event.getPlayer().addPotionEffect(effect);
  }

  public @Nullable PotionEffect getArenaEffects(Arena arena) {
    for (Map.Entry<String, PotionEffect> entry : MainConfig.permanent_effects_arenas.entrySet()) {
      final Collection<Arena> arenas = Util.parseArenas(entry.getKey());

      if (arenas.contains(arena))
        return entry.getValue();

    }

    return null;
  }
}
