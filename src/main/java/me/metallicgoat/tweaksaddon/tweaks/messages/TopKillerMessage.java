package me.metallicgoat.tweaksaddon.tweaks.messages;

import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.event.arena.RoundEndEvent;
import de.marcely.bedwars.api.event.arena.RoundStartEvent;
import de.marcely.bedwars.api.event.player.PlayerKillPlayerEvent;
import de.marcely.bedwars.api.message.Message;
import me.metallicgoat.tweaksaddon.config.ConfigValue;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.*;

public class TopKillerMessage implements Listener {

    private final HashMap<Arena, Collection<Player>> arenaPlayerHashMap = new HashMap<>();
    private final HashMap<Player, Integer> scoreHashMap = new HashMap<>();

    @EventHandler
    public void onGameStart(RoundStartEvent e) {
        arenaPlayerHashMap.put(e.getArena(), e.getArena().getPlayers());
        e.getArena().getPlayers().forEach(player -> scoreHashMap.put(player, 0));
    }

    @EventHandler
    public void onPlayerKillPlayer(PlayerKillPlayerEvent e) {
        Player player = e.getPlayer().getKiller();
        if (scoreHashMap.containsKey(player)) {
            int oldScore = scoreHashMap.get(player);
            scoreHashMap.replace(player, oldScore, oldScore + 1);
        }
    }

    @EventHandler
    public void onGameOver(RoundEndEvent e) {

        final Arena arena = e.getArena();

        if (ConfigValue.top_killer_message_enabled
                && arenaPlayerHashMap.containsKey(arena)) {

            HashMap<Player, Integer> arenaScoreHashMap = new HashMap<>();

            for (Player player : arenaPlayerHashMap.get(arena)) {
                int playerKills = scoreHashMap.getOrDefault(player, 0);
                arenaScoreHashMap.put(player, playerKills);
                scoreHashMap.remove(player);
            }

            printMessage(e, sortHashMapByValue(arenaScoreHashMap));
            arenaPlayerHashMap.remove(arena);
        }
    }

    public static LinkedHashMap<Player, Integer> sortHashMapByValue(HashMap<Player, Integer> hm) {
        // creating list from elements of HashMap
        final List<Map.Entry<Player, Integer>> list = new LinkedList<>(hm.entrySet());
        // sorting list (Reverse order)
        list.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
        final LinkedHashMap<Player, Integer> ha = new LinkedHashMap<>();
        for (Map.Entry<Player, Integer> me : list) {
            ha.put(me.getKey(), me.getValue());
        }
        return ha;
    }

    private void printMessage(RoundEndEvent event, LinkedHashMap<Player, Integer> playerIntegerMap) {
        final List<String> formattedList = new ArrayList<>();
        final Object[] keys = playerIntegerMap.keySet().toArray();

        if (!playerIntegerMap.isEmpty() && playerIntegerMap.get((Player) keys[0]) != 0) {

            for (String line : ConfigValue.top_killer_pre_lines) {
                formattedList.add(Message.build(line).done());
            }

            for (Map.Entry<Integer, String> scoreText : ConfigValue.top_killer_lines.entrySet()) {

                if (keys.length < scoreText.getKey())
                    continue;

                final Player player = (Player) keys[scoreText.getKey() - 1];

                if (player == null)
                    continue;

                // TODO Placeholders
                final String buildPlaceholders = (scoreText.getValue())
                        .replace("{killer-name}", player.getDisplayName())
                        .replace("{kill-amount}", String.valueOf(playerIntegerMap.get(player)));

                formattedList.add(Message.build(buildPlaceholders)
                        //.placeholder("%Winner-Members%", !event.getWinners().isEmpty() ? event.getWinners().stream().map(Player::getName).collect(Collectors.joining(", ")) : "")
                        //.placeholder("%Winner-Members-Colored%", event.getWinnerTeam() != null ? event.getWinnerTeam().getChatColor()+event.getWinners().stream().map(Player::getName).collect(Collectors.joining(ChatColor.WHITE+", "+event.getWinnerTeam().getChatColor())) : "")
                        //.placeholder("%Winner-Team-Name%", event.getWinnerTeam() != null ? event.getWinnerTeam().getDisplayName() : "")
                        //.placeholder("%Winner-Team-Color%", !event.getWinners().isEmpty() ? event.getWinnerTeam().getChatColor().toString() : "")
                        .done());

            }

            for (String line : ConfigValue.top_killer_sub_lines) {
                formattedList.add(Message.build(line).done());
            }

        } else {
            if (ConfigValue.no_top_killer_message_enabled) {
                for (String line : ConfigValue.no_top_killer_message) {
                    formattedList.add(Message.build(line).done());
                }
            }
        }
        broadcast(event.getArena(), formattedList);
    }


    private void broadcast(Arena arena, List<String> message) {

        if (message == null)
            return;

        if (message.size() == 1 && message.get(0).equals(""))
            return;

        for (String line : message)
            arena.broadcast(line);
    }
}
