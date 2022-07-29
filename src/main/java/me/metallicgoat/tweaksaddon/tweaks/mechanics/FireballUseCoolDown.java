package me.metallicgoat.tweaksaddon.tweaks.mechanics;

import de.marcely.bedwars.api.event.player.PlayerUseSpecialItemEvent;
import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;
import me.metallicgoat.tweaksaddon.config.ConfigValue;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class FireballUseCoolDown implements Listener {

    private final List<Player> coolDownPlayers = new ArrayList<>();

    @EventHandler
    public void onPlayerUseSpecialItem(PlayerUseSpecialItemEvent event) {

        if (!ConfigValue.fireball_cooldown_enabled || !event.getSpecialItem().getId().equalsIgnoreCase("Fireball"))
            return;

        final Player player = event.getPlayer();

        if (coolDownPlayers.contains(player))
            return;

        coolDownPlayers.add(player);

        Bukkit.getServer().getScheduler().runTaskLater(MBedwarsTweaksPlugin.getInstance(), () ->
                coolDownPlayers.remove(player), ConfigValue.fireball_cooldown_time);

    }
}
