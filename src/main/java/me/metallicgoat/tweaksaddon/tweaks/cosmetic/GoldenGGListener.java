package me.metallicgoat.tweaksaddon.tweaks.cosmetic;

import de.marcely.bedwars.api.GameAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.ArenaStatus;
import de.marcely.bedwars.api.event.arena.RoundEndEvent;
import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;
import me.metallicgoat.tweaksaddon.config.ConfigValue;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.*;

public class GoldenGGListener implements Listener {

    // NOT THE BEST WAY
    private final Map<Arena, List<UUID>> winnerMap = new HashMap<>();

    @EventHandler
    public void onEnd(RoundEndEvent event){
        if (!ConfigValue.goldenGG) return;
        final Arena arena = event.getArena();
        winnerMap.put(arena, getUUIDList(event.getWinners()));
        Bukkit.getScheduler().runTaskLater(MBedwarsTweaksPlugin.getInstance(), () -> {
            winnerMap.remove(arena);
        }, 20* 10);//TODO: CHANGE THIS TIME TO END LOBBY REMAINING TIME
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent event){
        if (!ConfigValue.goldenGG) return;
        final Player player = event.getPlayer();
        final Arena arena = GameAPI.get().getArenaByPlayer(player);
        if (arena == null || arena.getStatus() != ArenaStatus.END_LOBBY) return;
        final String raw = event.getMessage();
        if (winnerMap.containsKey(arena) && isInList(player, winnerMap.get(arena))){
            event.setMessage(replaceGG(raw));
        }
    }

    private List<UUID> getUUIDList(Collection<Player> winners){
        final List<UUID> players = new ArrayList<>();
        for (Player winner : winners) {
            players.add(winner.getUniqueId());
        }
        return players;
    }
    private boolean isInList(Player player, List<UUID> winners){
        for (UUID winner : winners) {
            if (player.getUniqueId().equals(winner))
                return true;
        }
        return false;
    }
    private String replaceGG(String raw){
        return raw.toUpperCase().replace("GG", ChatColor.translateAlternateColorCodes('&', "&6&lGG"));
    }
}
