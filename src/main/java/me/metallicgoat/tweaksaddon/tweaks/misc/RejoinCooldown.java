package me.metallicgoat.tweaksaddon.tweaks.misc;

import de.marcely.bedwars.api.arena.RejoinPlayerIssue;
import de.marcely.bedwars.api.event.player.PlayerRejoinArenaEvent;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class RejoinCooldown implements Listener {

  // private final List<UUID> recentRejoin = new CopyOnWriteArrayList<>();

//  @EventHandler
//  public void onRejoinAttempt(PlayerRejoinArenaEvent event) {
//    // Failed attempt anyway, Ignore
//    if (event.hasIssues())
//      return;
//
//    if (this.recentRejoin.contains(event.getPlayer().getUniqueId())) {
//      event.addIssue(RejoinPlayerIssue.PLUGIN);
//      return;
//    }
//
//    final Player player = event.getPlayer();
//    final UUID uuid = player.getUniqueId();
//
//    final int currentCount = this.recentRejoinCount.getOrDefault(uuid, 0);
//
//
//  }

}
