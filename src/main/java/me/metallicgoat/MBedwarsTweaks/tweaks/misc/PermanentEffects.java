package me.metallicgoat.MBedwarsTweaks.tweaks.misc;

import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.event.arena.RoundStartEvent;
import de.marcely.bedwars.api.event.player.PlayerIngameRespawnEvent;
import me.metallicgoat.MBedwarsTweaks.utils.ServerManager;
import me.metallicgoat.MBedwarsTweaks.utils.XSeries.XPotion;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Optional;

public class PermanentEffects implements Listener {

    @EventHandler
    public void onRoundStart(RoundStartEvent e){
        e.getArena().getPlayers().forEach(player ->  giveEffects(player, e.getArena()));
    }

    @EventHandler
    public void onRespawn(PlayerIngameRespawnEvent e){
        giveEffects(e.getPlayer(), e.getArena());
    }

    private void giveEffects(Player player, Arena arena){
        String arenaName = arena.getName();

        for(String arenaPotion:ServerManager.getConfig().getStringList("Permanent-Effects")){
            String[] arenaPotionSplit = arenaPotion.split(":");
            if(arenaName.equalsIgnoreCase(arenaPotionSplit[0]) || arenaPotionSplit[0].equalsIgnoreCase("ALL-ARENAS")){
                Optional<XPotion> potion = XPotion.matchXPotion(arenaPotionSplit[1]);

                int amplifier = tryParseInt(arenaPotionSplit[2]);

                if(potion.isPresent() && potion.get().parsePotionEffectType() != null) {
                    PotionEffectType potionEffectType = potion.get().parsePotionEffectType();
                    if(potionEffectType != null) {
                        player.addPotionEffect((new PotionEffect(potionEffectType, Integer.MAX_VALUE, amplifier)));
                    }
                }
            }
        }
    }

    public int tryParseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 1;
        }
    }
}
