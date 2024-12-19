package me.metallicgoat.tweaksaddon.tweaks.explosives;

import de.marcely.bedwars.api.message.Message;
import java.text.DecimalFormat;
import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;
import me.metallicgoat.tweaksaddon.config.MainConfig;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class TNTIgniteCountdown implements Listener {

  private static final DecimalFormat decimalFormat = new DecimalFormat("0.00");

  // Place a countdown on TNT when it is ignited
  @EventHandler
  public void onEntitySpawnEvent(EntitySpawnEvent event) {
    if (!MainConfig.tnt_ignite_timer_enabled || event.getEntity().getType() != EntityType.PRIMED_TNT)
      return;

    final TNTPrimed entity = (TNTPrimed) event.getEntity();

    entity.setCustomNameVisible(true);

    final BukkitRunnable runnable = new BukkitRunnable() {
      @Override
      public void run() {
        if (!entity.isValid() || entity.getFuseTicks() <= 0) {
          cancel();
          return;
        }

        // Second to two decimal places
        final double seconds = ((double) entity.getFuseTicks()) / 20D;
        final String secondsString = decimalFormat.format(seconds);

        entity.setCustomName(Message.build(MainConfig.tnt_ignite_timer_title).placeholder("seconds", secondsString).done());
      }
    };

    runnable.runTaskTimer(MBedwarsTweaksPlugin.getInstance(), 0L, 1L);
  }
}
