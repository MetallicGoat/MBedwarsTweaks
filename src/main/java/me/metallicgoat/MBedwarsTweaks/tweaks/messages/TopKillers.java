package me.metallicgoat.MBedwarsTweaks.tweaks.messages;

import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.event.arena.RoundEndEvent;
import de.marcely.bedwars.api.event.arena.RoundStartEvent;
import de.marcely.bedwars.api.event.player.PlayerKillPlayerEvent;
import me.metallicgoat.MBedwarsTweaks.utils.ServerManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class TopKillers implements Listener {

    private final HashMap<Arena, Collection<Player>> playerArenaHashMap = new HashMap<>();
    private final HashMap<Player, Integer> scoreHashMap = new HashMap<>();

    private String firstKiller = null;
    private String secondKiller = null;
    private String thirdKiller = null;

    private int firstKillerInt = 0;
    private int secondKillerInt = 0;
    private int thirdKillerInt = 0;

    @EventHandler
    public void onGameStart(RoundStartEvent e){
        playerArenaHashMap.put(e.getArena(), e.getArena().getPlayers());
        e.getArena().getPlayers().forEach(player -> scoreHashMap.put(player, 0));
    }

    @EventHandler
    public void onPlayerKillPlayer(PlayerKillPlayerEvent e){
        Player player = e.getPlayer().getKiller();
        if(scoreHashMap.containsKey(player)){
            int oldScore = scoreHashMap.get(player);
            scoreHashMap.replace(player, oldScore, oldScore + 1);
        }
    }

    @EventHandler
    public void onGameOver(RoundEndEvent e) {

        if (ServerManager.getConfig().getBoolean("Top-Killer-Message-Enabled")) {

            HashMap<Player, Integer> arenaScoreHashMap = new HashMap<>();

            playerArenaHashMap.get(e.getArena()).forEach(player -> {
                int playerKills = scoreHashMap.get(player);
                arenaScoreHashMap.put(player, playerKills);
                scoreHashMap.remove(player);
            });

            List<Player> top3 = arenaScoreHashMap.entrySet().stream().sorted(Map.Entry.<Player, Integer>comparingByValue().reversed()).limit(3).map(Map.Entry::getKey).collect(Collectors.toList());

            AtomicInteger i = new AtomicInteger(1);
            top3.forEach(p -> {
                if (i.get() == 1 && arenaScoreHashMap.get(p) != 0) {
                    firstKiller = p.getDisplayName();
                    firstKillerInt = arenaScoreHashMap.get(p);
                }else if(i.get() == 2 && arenaScoreHashMap.get(p) != 0) {
                    secondKiller = p.getDisplayName();
                    secondKillerInt = arenaScoreHashMap.get(p);
                }else if(i.get() == 3 && arenaScoreHashMap.get(p) != 0) {
                    thirdKiller = p.getDisplayName();
                    thirdKillerInt = arenaScoreHashMap.get(p);
                }
                i.getAndIncrement();
            });

            printMessage(e.getArena());

            playerArenaHashMap.remove(e.getArena());

            arenaScoreHashMap.clear();


        }
    }

    private void printMessage(Arena arena) {
        List<String> formattedList = new ArrayList<>();

        if (firstKiller != null) {

            if(secondKiller == null){
                secondKiller = "";
            }
            if(thirdKiller == null){
                thirdKiller = "";
            }

            for (String s : ServerManager.getConfig().getStringList("Top-Killer-Message")) {

                if(s != null) {

                    String complete;

                    if (s.contains("%Killer-2-Name%")) {
                        if (secondKiller.equals("")) {
                            continue;
                        }
                    }else if (s.contains("%Killer-3-Name%")) {
                        if (thirdKiller.equals("")) {
                            continue;
                        }
                    }

                    complete = s
                            .replace("%Killer-1-Name%", firstKiller)
                            .replace("%Killer-2-Name%", secondKiller)
                            .replace("%Killer-3-Name%", thirdKiller)
                            .replace("%Killer-1-Amount%", String.valueOf(firstKillerInt))
                            .replace("%Killer-2-Amount%", String.valueOf(secondKillerInt))
                            .replace("%Killer-3-Amount%", String.valueOf(thirdKillerInt));

                    formattedList.add(complete);

                }
            }
            broadcast(arena, formattedList);
            formattedList.clear();
        }else{
            List<String> noKillerMessage = ServerManager.getConfig().getStringList("No-Top-Killers-Message");
            broadcast(arena, noKillerMessage);
        }
    }


    private void broadcast(Arena arena, List<String> message){
        if(message != null) {
            if(message.size() == 1){
                if (message.get(0).equals("")) {
                    message.remove(0);
                }
            }
            message.forEach(s -> {
                if (s != null)
                    arena.broadcast(ChatColor.translateAlternateColorCodes('&', s));
            });
        }
    }
}
