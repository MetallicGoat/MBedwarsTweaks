package me.metallicgoat.tweaksaddon.AA_old.tweaks.misc;

import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.ArenaStatus;
import de.marcely.bedwars.api.event.player.PlayerJoinArenaEvent;
import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;
import me.metallicgoat.tweaksaddon.AA_old.utils.ServerManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ShortenCountdown implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinArenaEvent e){
        if(ServerManager.getConfig().getBoolean("Shorten-Countdown.Enabled")) {
            Arena arena = e.getArena();
            float max = arena.getMaxPlayers();
            float current = arena.getPlayers().size();

            double currentPercentFilled = (current / max) * 100;
            int minimumPercent = ServerManager.getConfig().getInt("Shorten-Countdown.Minimum-Percent-Filled");
            int newTime = ServerManager.getConfig().getInt("Shorten-Countdown.Shorten-Time-To");

            Bukkit.getServer().getScheduler().runTaskLater(plugin(), () -> {
                if (currentPercentFilled >= minimumPercent
                        && arena.getStatus() == ArenaStatus.LOBBY
                        && arena.getLobbyTimeRemaining() > newTime) {
                    arena.setLobbyTimeRemaining(newTime, false);
                }
            }, 1L);

        }
    }

    public static MBedwarsTweaksPlugin plugin(){
        return MBedwarsTweaksPlugin.getInstance();
    }
}
