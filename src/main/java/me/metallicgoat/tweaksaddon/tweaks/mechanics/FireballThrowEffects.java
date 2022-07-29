package me.metallicgoat.tweaksaddon.tweaks.mechanics;

import de.marcely.bedwars.api.event.player.PlayerUseSpecialItemEvent;
import me.metallicgoat.tweaksaddon.config.ConfigValue;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class FireballThrowEffects implements Listener {

    @EventHandler
    public void onPlayerUseSpecialItem(PlayerUseSpecialItemEvent event){

        if(!ConfigValue.fireball_throw_effects_enabled || !event.getSpecialItem().getId().equalsIgnoreCase("Fireball"))
            return;

        ConfigValue.fireball_throw_effects.forEach(potionEffect -> event.getPlayer().addPotionEffect(potionEffect));

    }
}
