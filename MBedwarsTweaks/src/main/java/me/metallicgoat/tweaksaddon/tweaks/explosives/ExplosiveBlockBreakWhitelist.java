package me.metallicgoat.tweaksaddon.tweaks.explosives;

import de.marcely.bedwars.api.event.arena.ArenaExplosionEvent;
import de.marcely.bedwars.api.event.arena.ArenaExplosionEvent.BlockFilter;
import me.metallicgoat.tweaksaddon.config.MainConfig;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ExplosiveBlockBreakWhitelist implements Listener {

  private static final BlockFilter FILTER_FIREBALL = (event, block) ->
      MainConfig.fireball_whitelist_blocks.contains(block.getType());

  private static final BlockFilter FILTER_TNT = (event, block) ->
      MainConfig.tnt_whitelist_blocks.contains(block.getType());

  @EventHandler
  public void onArenaExplosion(ArenaExplosionEvent event) {
    checkFireball(event);
    checkTNT(event);
  }

  private void checkFireball(ArenaExplosionEvent event) {
    if (!MainConfig.fireball_whitelist_enabled || !event.isExplosiveFireball())
      return;

    event.addFilterAsFirst(FILTER_FIREBALL);
  }

  private void checkTNT(ArenaExplosionEvent event) {
    if (!MainConfig.tnt_whitelist_enabled || !event.isExplosiveTNT())
      return;

    event.addFilterAsFirst(FILTER_TNT);
  }
}
