package me.metallicgoat.MBedwarsTweaks.tweaks.endmsg;

import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.event.arena.RoundEndEvent;
import de.marcely.bedwars.api.event.arena.RoundStartEvent;
import de.marcely.bedwars.api.player.DefaultPlayerStatSet;
import de.marcely.bedwars.api.player.PlayerDataAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;

public class endmsg implements Listener {

    private final HashMap<Arena, Player> playerHashMap = new HashMap<>();
    private final HashMap<Player, Integer> scoreHashMap = new HashMap<>();

    @EventHandler
    public void onGameStart(RoundStartEvent e){
        Arena a = e.getArena();
        a.getPlayers().forEach(p -> playerHashMap.put(a, p));
    }

    @EventHandler
    public void onGameOver(RoundEndEvent e){

        //Add players to score list
        playerHashMap.forEach((arena, player) -> {
            if(arena == e.getArena()){

                PlayerDataAPI.get().getStats(player, stats -> {
                    Number i = DefaultPlayerStatSet.KILLS.getValue(stats.getGameStats());
                    scoreHashMap.put(player, i.intValue());
                });
            }
        });

        //empty join list
        scoreHashMap.forEach((player, atomicInteger) -> {
            if(playerHashMap.containsValue(player)){
                playerHashMap.remove(e.getArena(), player);
            }
        });

        {
            //NOTE: Empty list and make message
            for (int i = 0; i < 3; i++) {
                if (!scoreHashMap.isEmpty()) {
                    Map.Entry<Player, Integer> maxEntry = null;

                    for (Map.Entry<Player, Integer> entry : scoreHashMap.entrySet()) {
                        if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0) {
                            maxEntry = entry;
                        }
                    }

                    if(maxEntry.getValue() != 0) {
                        System.out.println(maxEntry.getKey().getDisplayName() + " - " + maxEntry.getValue());
                    }else{
                        System.out.println("------------");
                    }

                    scoreHashMap.remove(maxEntry.getKey());
                }else {
                    System.out.println("------------");
                }
            }
        }


        //empty score map
        scoreHashMap.clear();

    }
}
