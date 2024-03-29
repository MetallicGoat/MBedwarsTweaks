package me.metallicgoat.tweaksaddon.tweaks.explosives;

import de.marcely.bedwars.api.event.player.PlayerUseSpecialItemEvent;
import de.marcely.bedwars.api.game.specialitem.SpecialItemType;
import java.util.ArrayList;
import java.util.List;
import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;
import me.metallicgoat.tweaksaddon.config.MainConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class FireballUseCoolDown implements Listener {

  private final List<Player> coolDownPlayers = new ArrayList<>();

  @EventHandler
  public void onPlayerUseSpecialItem(PlayerUseSpecialItemEvent event) {
    if (!MainConfig.fireball_cooldown_enabled || event.getSpecialItem().getType() != SpecialItemType.FIREBALL)
      return;

    final Player player = event.getPlayer();

    // Remove restriction when in a vehicle to support dragon rider (from Cosmetics addon)
    if (player.isInsideVehicle() && player.getVehicle() instanceof LivingEntity)
      return;

    if (coolDownPlayers.contains(player)) {
      event.setCancelled(true);
      return;
    }

    coolDownPlayers.add(player);

    Bukkit.getServer().getScheduler().runTaskLater(MBedwarsTweaksPlugin.getInstance(), () ->
        coolDownPlayers.remove(player), MainConfig.fireball_cooldown_time);
  }
}
