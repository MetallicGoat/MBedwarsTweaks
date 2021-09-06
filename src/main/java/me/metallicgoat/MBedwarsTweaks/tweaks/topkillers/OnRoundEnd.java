package me.metallicgoat.MBedwarsTweaks.tweaks.topkillers;

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

public class OnRoundEnd implements Listener {

    private final HashMap<Player, Arena> playerArenaHashMap = new HashMap<>();
    private final HashMap<Player, Integer> scoreHashMap = new HashMap<>();

    private String firstKiller = null;
    private String secondKiller = null;
    private String thirdKiller = null;

    private int firstKillerInt = 0;
    private int secondKillerInt = 0;
    private int thirdKillerInt = 0;

    //TODO Plugin could think one player is in multiple arenas

    @EventHandler
    public void onGameStart(RoundStartEvent e){
        e.getArena().getPlayers().forEach(player -> {
            playerArenaHashMap.put(player, e.getArena());
            scoreHashMap.put(player, 0);
        });
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

            playerArenaHashMap.forEach((player, arena) -> {
                if(arena == e.getArena()){
                    arenaScoreHashMap.put(player, scoreHashMap.get(player));
                }
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

            arenaScoreHashMap.forEach((player, integer) -> {
                scoreHashMap.remove(player);
                playerArenaHashMap.remove(player);
            });

            arenaScoreHashMap.clear();


        }
    }

    private void printMessage(Arena arena) {
        List<String> formattedList = new ArrayList<>();

        if (firstKiller != null) {
            for (String s : ServerManager.getConfig().getStringList("Top-Killer-Message")) {

                if(s != null) {

                    String complete;

                    if (s.contains("%Killer-2-Name%")) {
                        if (secondKiller != null) {
                            continue;
                        }
                    }else if (s.contains("%Killer-3-Name%")) {
                        if (thirdKiller != null) {
                            continue;
                        }
                    }

                    if(secondKiller == null){
                        secondKiller = "";
                    }
                    if(thirdKiller == null){
                        thirdKiller = "";
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
            broadcast(arena, ServerManager.getConfig().getStringList("No-Top-Killers-FinalKillMessage"));
        }
    }


    private void broadcast(Arena arena, List<String> message){
        message.forEach(s -> {
            if(s != null && !s.equals(""))
                arena.broadcast(ChatColor.translateAlternateColorCodes('&', s));
        });
    }
}

//it's bad, but leaned a bunch from it, so ill keep it as a memorial

                    /*
                    int type = ServerManager.getConfig().getInt("No-Top-Killer-Display-Type");
                    if (type == 1) {
                        String noTopKiller = ServerManager.getConfig().getString("No-Top-Killer-FinalKillMessage");
                        if (s.contains("%Killer-1-Name%")) {
                            s = s.replace("%Killer-1-Name%", firstKiller);
                        }else if (s.contains("%Killer-2-Name%")) {
                            if (secondKiller != null) {
                                s = s.replace("%Killer-2-Name%", secondKiller);
                            } else {
                                s = noTopKiller;
                            }
                        } else if (s.contains("%Killer-3-Name%")) {
                            if (thirdKiller != null) {
                                s = s.replace("%Killer-3-Name%", thirdKiller);
                            } else {
                                s = noTopKiller;
                            }
                        }

                        assert s != null;
                        complete = s
                                .replace("%Killer-1-Amount%", String.valueOf(firstKillerInt))
                                .replace("%Killer-2-Amount%", String.valueOf(secondKillerInt))
                                .replace("%Killer-3-Amount%", String.valueOf(thirdKillerInt));

                    } else if (type == 2) {
                        String noTopKiller = ServerManager.getConfig().getString("No-Top-Killer-Name");

                        if (noTopKiller != null) {
                            if (firstKiller == null) {
                                firstKiller = noTopKiller;
                            }
                            if (secondKiller == null) {
                                secondKiller = noTopKiller;
                            }
                            if (thirdKiller == null) {
                                thirdKiller = noTopKiller;
                            }
                        }

                        complete = s
                                .replace("%Killer-1-Amount%", String.valueOf(firstKillerInt))
                                .replace("%Killer-2-Amount%", String.valueOf(secondKillerInt))
                                .replace("%Killer-3-Amount%", String.valueOf(thirdKillerInt))
                                .replace("%Killer-1-Name%", firstKiller)
                                .replace("%Killer-2-Name%", secondKiller)
                                .replace("%Killer-3-Name%", thirdKiller);

                    } else if (type == 3) {
                        //ONLY
                    }
# THE CONFIG PART
# 1 - we will use No-Top-Killer-FinalKillMessage to replace the line
# 2 - we will replace only the name to No-Top-Killer-Name
# 3 - we will remove the line completely
No-Top-Killer-Display-Type: 3

#Set No-Top-Killer-Display-Type to 1
No-Top-Killer-Message: 'This Spot Is Empty!'

#Set No-Top-Killer-Display-Type to 2
No-Top-Killer-Name: '----------'

                     */
