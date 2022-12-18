package me.metallicgoat.tweaksaddon.tweaks.messages;

import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.QuitPlayerMemory;
import de.marcely.bedwars.api.event.arena.RoundEndEvent;
import de.marcely.bedwars.api.message.Message;
import de.marcely.bedwars.api.player.DefaultPlayerStatSet;
import de.marcely.bedwars.api.player.PlayerDataAPI;
import de.marcely.bedwars.api.player.PlayerStats;
import me.metallicgoat.tweaksaddon.config.ConfigValue;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.*;

public class TopKillerMessage implements Listener {

    @EventHandler
    public void onGameOver(RoundEndEvent event) {
        if (!ConfigValue.top_killer_message_enabled)
            return;

        final Arena arena = event.getArena();
        final HashMap<OfflinePlayer, Integer> nameIntMap = new HashMap<>();

        // Online Players
        for (OfflinePlayer player : arena.getPlayers())
            addStatsToMap(nameIntMap, player);

        // Offline Players
        for (QuitPlayerMemory memory : arena.getQuitPlayerMemories())
            addStatsToMap(nameIntMap, Bukkit.getOfflinePlayer(memory.getUniqueId()));

        printMessage(arena, sortHashMapByValue(nameIntMap));
    }

    // TODO this bad
    private void printMessage(Arena arena, HashMap<OfflinePlayer, Integer> playerIntegerMap) {
        final List<String> formattedList = new ArrayList<>();

        // There is Killers
        if (!playerIntegerMap.isEmpty()) {

            for (String line : ConfigValue.top_killer_pre_lines)
                formattedList.add(Message.build(line).done());

            int place = 1;
            for (OfflinePlayer player : playerIntegerMap.keySet()) {
                final String text = ConfigValue.top_killer_lines.get(place);

                if (text == null)
                    continue;

                formattedList.add(Message.build(text)
                        .placeholder("killer-name", player.getName())
                        .placeholder("kill-amount", playerIntegerMap.get(player))
                        //.placeholder("%Winner-Members%", !event.getWinners().isEmpty() ? event.getWinners().stream().map(Player::getName).collect(Collectors.joining(", ")) : "")
                        //.placeholder("%Winner-Members-Colored%", event.getWinnerTeam() != null ? event.getWinnerTeam().getChatColor()+event.getWinners().stream().map(Player::getName).collect(Collectors.joining(ChatColor.WHITE+", "+event.getWinnerTeam().getChatColor())) : "")
                        //.placeholder("%Winner-Team-Name%", event.getWinnerTeam() != null ? event.getWinnerTeam().getDisplayName() : "")
                        //.placeholder("%Winner-Team-Color%", !event.getWinners().isEmpty() ? event.getWinnerTeam().getChatColor().toString() : "")
                        .done());

                place++;
            }

            for (String line : ConfigValue.top_killer_sub_lines)
                formattedList.add(Message.build(line).done());

        // There are no killers
        } else {
            if (ConfigValue.no_top_killer_message_enabled)
                return;

            for (String line : ConfigValue.no_top_killer_message)
                formattedList.add(Message.build(line).done());
        }

        broadcast(arena, formattedList);
    }

    private void addStatsToMap(HashMap<OfflinePlayer, Integer> nameIntMap, OfflinePlayer player){
        if(player == null)
            return;

        final Optional<PlayerStats> stats =  PlayerDataAPI.get().getStatsNow(player);

        if(!stats.isPresent())
            return;

        final int kills = DefaultPlayerStatSet.KILLS.getValue(stats.get().getGameStats()).intValue();

        if (kills > 0)
            nameIntMap.put(player, kills);
    }

    private void broadcast(Arena arena, List<String> message) {
        if (message == null || (message.size() == 1 && message.get(0).equals("")))
            return;

        for (String line : message)
            arena.broadcast(line);
    }

    public static LinkedHashMap<OfflinePlayer, Integer> sortHashMapByValue(HashMap<OfflinePlayer, Integer> hm) {
        // creating list from elements of HashMap
        final List<Map.Entry<OfflinePlayer, Integer>> list = new LinkedList<>(hm.entrySet());

        // sorting list (Reverse order)
        list.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        final LinkedHashMap<OfflinePlayer, Integer> sortedMap = new LinkedHashMap<>();

        for (Map.Entry<OfflinePlayer, Integer> me : list)
            sortedMap.put(me.getKey(), me.getValue());

        return sortedMap;
    }
}
