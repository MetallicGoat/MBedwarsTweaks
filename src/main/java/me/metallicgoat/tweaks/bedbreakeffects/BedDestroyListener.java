package me.metallicgoat.tweaks.bedbreakeffects;

import de.marcely.bedwars.api.event.arena.ArenaBedBreakEvent;
import me.metallicgoat.tweaks.utils.ServerManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.meta.FireworkMeta;

public class BedDestroyListener implements Listener {
    @EventHandler
    public void onBedBreak(ArenaBedBreakEvent event) {
        Location bedLocation = event.getBedLocation();
        Player player = event.getPlayer();

        if(player != null) {

            String effectMode = ServerManager.getConfig().getString("bed-destroy-effect");

            switch (effectMode != null ? effectMode : "") {
                case "NONE":
                    break;
                case "FIREWORK":
                    int red = ServerManager.getConfig().getInt("firework-color-red");
                    int green = ServerManager.getConfig().getInt("firework-color-green");
                    int blue = ServerManager.getConfig().getInt("firework-color-blue");

                    FireworkEffect.Builder effect = FireworkEffect.builder().withColor(Color.fromRGB(red, green, blue));
                    String type = ServerManager.getConfig().getString("firework-type");
                    switch (type != null ? type : "") {
                        case "CREEPER":
                            effect.with(FireworkEffect.Type.CREEPER);
                            break;
                        case "BALL":
                            effect.with(FireworkEffect.Type.BALL);
                            break;
                        case "BALL_LARGE":
                            effect.with(FireworkEffect.Type.BALL_LARGE);
                            break;
                        case "BURST":
                            effect.with(FireworkEffect.Type.BURST);
                            break;
                        case "STAR":
                            effect.with(FireworkEffect.Type.STAR);
                            break;
                        default:
                            effect.with(FireworkEffect.Type.BALL);
                            break;
                    }
                    Location spawnLocation = getAirAboveBed(bedLocation);
                    if(spawnLocation != null) {
                        Firework firework = player.getWorld().spawn(spawnLocation, Firework.class);
                        FireworkMeta meta = firework.getFireworkMeta();
                        meta.addEffect(effect.trail(ServerManager.getConfig().getBoolean("firework-trail")).build());
                        firework.setFireworkMeta(meta);
                        firework.setFireTicks(ServerManager.getConfig().getInt("firework-duration"));
                    }
                    break;
                default:
                    Bukkit.getServer().broadcastMessage("§cUNKNOWN BED EFFECT");
                    break;
            }
        }
    }
    private static Location getAirAboveBed(Location location){
        Block block = location.getBlock();
        int i = 0;

        do{
            if(block.getType() == Material.AIR)
                return block.getLocation();

            block = block.getRelative(0, 1, 0);
            i++;

        }while (block.getType() != Material.AIR && i < 7);

        return null;
    }
}