package me.metallicgoat.MBedwarsTweaks.beddestoryeffects;

import de.marcely.bedwars.api.event.arena.ArenaBedBreakEvent;
import me.metallicgoat.MBedwarsTweaks.utils.ServerManager;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.meta.FireworkMeta;

public class BedDestoryListener implements Listener {
    @EventHandler
    public void onBedBreak(ArenaBedBreakEvent event) {
        Location bedLocation = event.getBedLocation();
        Player player = event.getPlayer();

        switch (ServerManager.getConfig().getString("bed-destroy-effect")) {
            case "NONE":
                break;
            case "FIREWORK":
                Integer red = ServerManager.getConfig().getInt("firework-color-red");
                Integer green = ServerManager.getConfig().getInt("firework-color-green");
                Integer blue = ServerManager.getConfig().getInt("firework-color-blue");

                FireworkEffect.Builder effect = FireworkEffect.builder().withColor(Color.fromRGB(red, green, blue));
                switch (ServerManager.getConfig().getString("firework-type")) {
                    case "CREEPER":
                        effect = effect.with(FireworkEffect.Type.CREEPER);
                        break;
                    case "BALL":
                        effect = effect.with(FireworkEffect.Type.BALL);
                        break;
                    case "BALL_LARGE":
                        effect = effect.with(FireworkEffect.Type.BALL_LARGE);
                        break;
                    case "BURST":
                        effect = effect.with(FireworkEffect.Type.BURST);
                        break;
                    case "STAR":
                        effect = effect.with(FireworkEffect.Type.STAR);
                        break;
                    default:
                        effect = effect.with(FireworkEffect.Type.BALL);
                        break;
                }

                Firework firework = player.getWorld().spawn(bedLocation, Firework.class);
                FireworkMeta meta = firework.getFireworkMeta();
                meta.addEffect(effect.trail(ServerManager.getConfig().getBoolean("firework-trail")).build());
                firework.setFireworkMeta(meta);
                firework.setFireTicks(ServerManager.getConfig().getInt("firework-duration"));
                break;
            default:
                Bukkit.getServer().broadcastMessage("§cUNKNOWN BED EFFECT");
                break;
        }
    }
}
