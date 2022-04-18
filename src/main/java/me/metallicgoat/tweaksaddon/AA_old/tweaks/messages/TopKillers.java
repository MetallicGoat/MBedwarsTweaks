package me.metallicgoat.tweaksaddon.AA_old.tweaks.messages;

import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.event.arena.RoundEndEvent;
import de.marcely.bedwars.api.event.arena.RoundStartEvent;
import de.marcely.bedwars.api.event.player.PlayerKillPlayerEvent;
import me.metallicgoat.tweaksaddon.AA_old.utils.ServerManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.*;
import java.util.stream.Collectors;

public class TopKillers implements Listener {

    //TODO: Showing 0

    private final HashMap<Arena, Collection<Player>> arenaPlayerHashMap = new HashMap<>();
    private final HashMap<Player, Integer> scoreHashMap = new HashMap<>();

    @EventHandler
    public void onGameStart(RoundStartEvent e){
        arenaPlayerHashMap.put(e.getArena(), e.getArena().getPlayers());
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

        final Arena arena = e.getArena();

        if (ServerManager.getConfig().getBoolean("Top-Killer-Message-Enabled")
                 && arenaPlayerHashMap.containsKey(arena)) {

            HashMap<Player, Integer> arenaScoreHashMap = new HashMap<>();

            for(Player player : arenaPlayerHashMap.get(arena)){
                int playerKills = scoreHashMap.getOrDefault(player, 0);
                arenaScoreHashMap.put(player, playerKills);
                scoreHashMap.remove(player);
            }

            printMessage(e, sortHashMapByValue(arenaScoreHashMap));
            arenaPlayerHashMap.remove(arena);
        }
    }
    public static HashMap<Player, Integer> sortHashMapByValue(HashMap<Player, Integer> hm) {
        // creating list from elements of HashMap
        List<Map.Entry<Player, Integer>> list = new LinkedList<>(hm.entrySet());
        // sorting list (Reverse order)
        list.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
        HashMap<Player, Integer> ha = new LinkedHashMap<>();
        for(Map.Entry<Player, Integer> me : list) {
            ha.put(me.getKey(), me.getValue());
        }
        return ha;
    }

    private void printMessage(RoundEndEvent event, Map<Player, Integer> playerIntegerMap) {

        String firstKiller = null, secondKiller = null, thirdKiller = null;
        int firstKillerInt = 0, secondKillerInt = 0, thirdKillerInt = 0;

        int i = 1;
        for(Map.Entry<Player, Integer> map : playerIntegerMap.entrySet()) {
            switch (i){
                case 1:
                    firstKiller = map.getKey().getDisplayName();
                    firstKillerInt = map.getValue();
                    break;
                case 2:
                    secondKiller = map.getKey().getDisplayName();
                    secondKillerInt = map.getValue();
                    break;
                case 3:
                    thirdKiller = map.getKey().getDisplayName();
                    thirdKillerInt = map.getValue();
                    break;
            }
            i++;
        }

        List<String> formattedList = new ArrayList<>();

        if (firstKiller != null && firstKillerInt != 0) {

            for (String s : ServerManager.getConfig().getStringList("Top-Killer-Message")) {

                if(s != null) {

                    String complete;

                    if (s.contains("%Killer-2-Name%")) {
                        if (secondKiller == null) {
                            continue;
                        }
                    }else if (s.contains("%Killer-3-Name%")) {
                        if (thirdKiller == null) {
                            continue;
                        }
                    }

                    complete = s
                            .replace("%Winner-Members%", !event.getWinners().isEmpty() ? event.getWinners().stream().map(Player::getName).collect(Collectors.joining(", ")) : "")
                            .replace("%Winner-Members-Colored%", event.getWinnerTeam() != null ? event.getWinnerTeam().getChatColor()+event.getWinners().stream().map(Player::getName).collect(Collectors.joining(ChatColor.WHITE+", "+event.getWinnerTeam().getChatColor())) : "")
                            .replace("%Winner-Team-Name%", event.getWinnerTeam() != null ? event.getWinnerTeam().getDisplayName() : "")
                            .replace("%Winner-Team-Color%", !event.getWinners().isEmpty() ? event.getWinnerTeam().getChatColor().toString() : "")
                            .replace("%Killer-1-Name%", firstKiller)
                            .replace("%Killer-2-Name%", secondKiller != null ? secondKiller:"")
                            .replace("%Killer-3-Name%", thirdKiller != null ? thirdKiller:"")
                            .replace("%Killer-1-Amount%", String.valueOf(firstKillerInt))
                            .replace("%Killer-2-Amount%", String.valueOf(secondKillerInt))
                            .replace("%Killer-3-Amount%", String.valueOf(thirdKillerInt));

                    formattedList.add(complete);

                }
            }
            broadcast(event.getArena(), formattedList);
            formattedList.clear();
        }else{
            List<String> noKillerMessage = ServerManager.getConfig().getStringList("No-Top-Killers-Message");
            broadcast(event.getArena(), noKillerMessage);
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
