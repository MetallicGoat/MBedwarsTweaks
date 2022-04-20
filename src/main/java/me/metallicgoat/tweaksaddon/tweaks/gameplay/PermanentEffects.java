package me.metallicgoat.tweaksaddon.tweaks.gameplay;

import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.event.arena.RoundStartEvent;
import de.marcely.bedwars.api.event.player.PlayerIngameRespawnEvent;
import me.metallicgoat.tweaksaddon.config.ConfigValue;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;

import java.util.Collection;

public class PermanentEffects implements Listener {

    @EventHandler
    public void onRoundStart(RoundStartEvent event){
        final Arena arena = event.getArena();
        final Collection<PotionEffect> effects = ConfigValue.permanent_effects_arenas.get(arena);

        if(!ConfigValue.permanent_effects_enabled || effects.isEmpty())
            return;

        for(Player player : arena.getPlayers())
            player.addPotionEffects(effects);
    }

    @EventHandler
    public void onRespawn(PlayerIngameRespawnEvent event){
        final Arena arena = event.getArena();
        final Collection<PotionEffect> effects = ConfigValue.permanent_effects_arenas.get(arena);


        if(!ConfigValue.permanent_effects_enabled || effects.isEmpty())
            return;

        final Player player = event.getPlayer();

        player.addPotionEffects(effects);
    }


    /*
    private void giveEffects(Player player, List<PotionEffectType> effectTypes){


        final String arenaName = arena.getName();

        for(String arenaPotion : ServerManager.getConfig().getStringList("Permanent-Effects")){
            String[] arenaPotionSplit = arenaPotion.split(":");
            if(arenaName.equalsIgnoreCase(arenaPotionSplit[0]) || arenaPotionSplit[0].equalsIgnoreCase("ALL-ARENAS")){
                Optional<XPotion> potion = XPotion.matchXPotion(arenaPotionSplit[1]);

                int amplifier = tryParseInt(arenaPotionSplit);

                if(potion.isPresent()) {
                    final PotionEffectType potionEffectType = potion.get().getPotionEffectType();
                    if(potionEffectType != null) {
                        player.addPotionEffect((new PotionEffect(potionEffectType, Integer.MAX_VALUE, amplifier)));
                    }
                }
            }
        }
    }

    public int tryParseInt(String[] value) {
        try {
            if(value.length > 2) {
                return Integer.parseInt(value[2]);
            }
        } catch (NumberFormatException e) {
            return 1;
        }
        return 1;
    }

     */

}
