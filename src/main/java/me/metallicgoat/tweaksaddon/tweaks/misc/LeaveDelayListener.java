package me.metallicgoat.tweaksaddon.tweaks.misc;

import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.KickReason;
import de.marcely.bedwars.api.event.player.PlayerUseLobbyItemEvent;
import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;
import me.metallicgoat.tweaksaddon.config.ConfigValue;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class LeaveDelayListener implements Listener {

    private final List<UUID> cancelablePLayers = new ArrayList<>();
    private final Map<UUID, BukkitRunnable> playerTask = new HashMap<>();

    @EventHandler
    public void onUse(PlayerUseLobbyItemEvent event){
        if (!ConfigValue.arenaLeaveDelayEnabled) return;
        final Player player = event.getPlayer();
        final UUID playerUUID = player.getUniqueId();
        final Arena arena = event.getArena();
        if (event.getLobbyItem().getHandler().getId().equalsIgnoreCase("bedwars:leave")){
            event.setCancelled(true);
            if (cancelablePLayers.contains(playerUUID)){
                stopTask(playerUUID);
                cancelablePLayers.remove(playerUUID);
            }
            cancelablePLayers.add(playerUUID);
            sendMessage(player, "&a&lTeleporting you to lobby in " + ConfigValue.arenaLeaveDelay + " seconds..." +
                    "&a&lRight click again to cancel the teleport!");
            runTimer(player, arena);
        }
    }

    private void runTimer(Player player, Arena arena){
        final BukkitRunnable run = new BukkitRunnable() {
            int time = 0;
            @Override
            public void run() {
                if (time == ConfigValue.arenaLeaveDelay){
                    arena.kickPlayer(player, KickReason.LEAVE);
                    cancelablePLayers.remove(player.getUniqueId());
                    cancel();
                }
                time++;
            }
        };
        playerTask.put(player.getUniqueId(), run);
        Bukkit.getScheduler().runTaskTimer(MBedwarsTweaksPlugin.getInstance(), run , 0, 20L);
    }
    private void stopTask(UUID player){
        if (!playerTask.containsKey(player)) return;
        playerTask.get(player).cancel();
    }
    private void sendMessage(Player player, String message){
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }
}
