package me.metallicgoat.MBedwarsTweaks.tweaks.misc;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.Arena;
import me.metallicgoat.MBedwarsTweaks.utils.ServerManager;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffectType;

public class BreakInvis implements Listener {
    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        boolean enabled = ServerManager.getConfig().getBoolean("Break-Invis.Enabled");
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            Arena arena = BedwarsAPI.getGameAPI().getArenaByPlayer(p);
            if (enabled) {
                if (arena != null) {
                    if (e.getEntity() instanceof LivingEntity &&
                            ServerManager.getConfig().getStringList("Break-Invis.Causes").contains(e.getCause().name()))
                        ((LivingEntity) e.getEntity()).removePotionEffect(PotionEffectType.INVISIBILITY);
                }
            }
        }
    }
}