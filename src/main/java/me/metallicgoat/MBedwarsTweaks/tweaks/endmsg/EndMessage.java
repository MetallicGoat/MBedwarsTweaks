package me.metallicgoat.MBedwarsTweaks.tweaks.endmsg;

import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.event.arena.RoundEndEvent;
import de.marcely.bedwars.api.player.DefaultPlayerStatSet;
import de.marcely.bedwars.api.player.PlayerDataAPI;
import me.metallicgoat.MBedwarsTweaks.utils.ServerManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class EndMessage implements Listener {

    private String firstKiller = null;
    private String secondKiller = null;
    private String thirdKiller = null;

    private int firstKillerInt = 0;
    private int secondKillerInt = 0;
    private int thirdKillerInt = 0;

    @EventHandler
    public void onGameOver(RoundEndEvent e) {

        if (ServerManager.getConfig().getBoolean("Top-Killer-FinalKillMessage-Enabled")) {
            HashMap<Player, Integer> scoreHashMap = new HashMap<>();

            e.getArena().getPlayers().forEach(p -> PlayerDataAPI.get().getStats(p, stats -> {
                Number i = DefaultPlayerStatSet.KILLS.getValue(stats.getGameStats());
                scoreHashMap.put(p, i.intValue());
            }));
            e.getArena().getSpectators().forEach(p -> PlayerDataAPI.get().getStats(p, stats -> {
                Number i = DefaultPlayerStatSet.KILLS.getValue(stats.getGameStats());
                scoreHashMap.put(p, i.intValue());
            }));

            List<Player> top3 = scoreHashMap.entrySet().stream().sorted(Map.Entry.<Player, Integer>comparingByValue().reversed()).limit(3).map(Map.Entry::getKey).collect(Collectors.toList());

            AtomicInteger i = new AtomicInteger(1);
            top3.forEach(p -> {
                if (i.get() == 1 && scoreHashMap.get(p) != 0) {
                    firstKiller = p.getDisplayName();
                    firstKillerInt = scoreHashMap.get(p);
                }else if(i.get() == 2 && scoreHashMap.get(p) != 0) {
                    secondKiller = p.getDisplayName();
                    secondKillerInt = scoreHashMap.get(p);
                }else if(i.get() == 3 && scoreHashMap.get(p) != 0) {
                    thirdKiller = p.getDisplayName();
                    thirdKillerInt = scoreHashMap.get(p);
                }
                i.getAndIncrement();
            });

            printMessage(e.getArena());
            scoreHashMap.clear();

            //scoreHashMap.forEach((player, integer) -> printMessage(player));

        }
    }

    private void printMessage(Arena arena) {
        List<String> formattedList = new ArrayList<>();

        if (firstKiller != null) {
            for (String s : ServerManager.getConfig().getStringList("Top-Killer-FinalKillMessage")) {

                if(s != null) {

                    String complete = null;
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
                    }


                    if (complete != null) {
                        formattedList.add(complete);
                    }
                }
            }
            broadcast(arena, formattedList);
            formattedList.clear();
        }else{
            broadcast(arena, ServerManager.getConfig().getStringList("No-Top-Killers-FinalKillMessage"));
        }
    }


    private void broadcast(Arena arena, List<String> message){
        message.forEach(s ->
                arena.broadcast(ChatColor.translateAlternateColorCodes('&', s)));
    }
}
