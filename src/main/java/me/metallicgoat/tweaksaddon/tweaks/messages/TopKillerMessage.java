package me.metallicgoat.tweaksaddon.tweaks.messages;

import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.event.arena.RoundEndEvent;
import de.marcely.bedwars.api.event.arena.RoundStartEvent;
import de.marcely.bedwars.api.message.Message;
import de.marcely.bedwars.api.player.DefaultPlayerStatSet;
import de.marcely.bedwars.api.player.PlayerDataAPI;
import de.marcely.bedwars.api.player.PlayerStats;
import de.marcely.bedwars.tools.Pair;
import me.metallicgoat.tweaksaddon.config.ConfigValue;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.*;

public class TopKillerMessage implements Listener {

    private final HashMap<Arena, List<Pair<UUID, String>>> arenaPlayerHashMap = new HashMap<>();

    @EventHandler
    public void onGameStart(RoundStartEvent event) {
        final List<Pair<UUID, String>> uuids = new ArrayList<>();

        for (Player player : event.getArena().getPlayers())
            uuids.add(new Pair<>(player.getUniqueId(), player.getDisplayName()));

        arenaPlayerHashMap.put(event.getArena(), uuids);
    }

    @EventHandler
    public void onGameOver(RoundEndEvent event) {
        if (!ConfigValue.top_killer_message_enabled || !arenaPlayerHashMap.containsKey(event.getArena()))
            return;

        final Arena arena = event.getArena();
        final HashMap<Pair<UUID, String>, Integer> nameIntMap = new HashMap<>();

        for (Pair<UUID, String> pair : arenaPlayerHashMap.get(arena)) {
            final Optional<PlayerStats> stats = PlayerDataAPI.get().getStatsNow(pair.getKey());

            if (!stats.isPresent())
                continue;

            final int kills = DefaultPlayerStatSet.KILLS.getValue(stats.get().getGameStats()).intValue();

            if (kills > 0)
                nameIntMap.put(pair, kills);
        }

        printMessage(arena, sortHashMapByValue(nameIntMap));
        arenaPlayerHashMap.remove(arena);
    }

    // TODO this bad
    private void printMessage(Arena arena, LinkedHashMap<Pair<UUID, String>, Integer> playerIntegerMap) {
        final List<String> formattedList = new ArrayList<>();

        // There is Killers
        if (!playerIntegerMap.isEmpty()) {

            for (String line : ConfigValue.top_killer_pre_lines) {
                formattedList.add(Message.build(line).done());
            }

            int place = 1;
            for (Pair<UUID, String> pair : playerIntegerMap.keySet()) {
                final String text = ConfigValue.top_killer_lines.get(place);

                if (text == null)
                    continue;

                formattedList.add(Message.build(text)
                        .placeholder("killer-name", pair.getValue())
                        .placeholder("kill-amount", playerIntegerMap.get(pair))
                        //.placeholder("%Winner-Members%", !event.getWinners().isEmpty() ? event.getWinners().stream().map(Player::getName).collect(Collectors.joining(", ")) : "")
                        //.placeholder("%Winner-Members-Colored%", event.getWinnerTeam() != null ? event.getWinnerTeam().getChatColor()+event.getWinners().stream().map(Player::getName).collect(Collectors.joining(ChatColor.WHITE+", "+event.getWinnerTeam().getChatColor())) : "")
                        //.placeholder("%Winner-Team-Name%", event.getWinnerTeam() != null ? event.getWinnerTeam().getDisplayName() : "")
                        //.placeholder("%Winner-Team-Color%", !event.getWinners().isEmpty() ? event.getWinnerTeam().getChatColor().toString() : "")
                        .done());

                place++;
            }

            for (String line : ConfigValue.top_killer_sub_lines) {
                formattedList.add(Message.build(line).done());
            }

        // There are no killers
        } else {
            if (ConfigValue.no_top_killer_message_enabled) {
                for (String line : ConfigValue.no_top_killer_message) {
                    formattedList.add(Message.build(line).done());
                }
            }
        }

        broadcast(arena, formattedList);
    }

    private void broadcast(Arena arena, List<String> message) {

        if (message == null)
            return;

        if (message.size() == 1 && message.get(0).equals(""))
            return;

        for (String line : message)
            arena.broadcast(line);
    }

    public static LinkedHashMap<Pair<UUID, String>, Integer> sortHashMapByValue(HashMap<Pair<UUID, String>, Integer> hm) {
        // creating list from elements of HashMap
        final List<Map.Entry<Pair<UUID, String>, Integer>> list = new LinkedList<>(hm.entrySet());

        // sorting list (Reverse order)
        list.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        final LinkedHashMap<Pair<UUID, String>, Integer> sortedMap = new LinkedHashMap<>();

        for (Map.Entry<Pair<UUID, String>, Integer> me : list) {
            sortedMap.put(me.getKey(), me.getValue());
        }

        return sortedMap;
    }
}
