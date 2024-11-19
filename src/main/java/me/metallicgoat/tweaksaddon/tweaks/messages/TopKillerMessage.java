package me.metallicgoat.tweaksaddon.tweaks.messages;

import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.QuitPlayerMemory;
import de.marcely.bedwars.api.event.arena.ArenaDeleteEvent;
import de.marcely.bedwars.api.event.arena.RoundEndEvent;
import de.marcely.bedwars.api.event.arena.RoundStartEvent;
import de.marcely.bedwars.api.message.Message;
import de.marcely.bedwars.api.player.DefaultPlayerStatSet;
import de.marcely.bedwars.api.player.PlayerDataAPI;
import de.marcely.bedwars.api.player.PlayerStats;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import me.metallicgoat.tweaksaddon.config.MainConfig;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class TopKillerMessage implements Listener {

  public Map<Arena, Collection<UUID>> arenaPlayers = new IdentityHashMap<>();

  @EventHandler
  public void onRoundStart(RoundStartEvent event) {
    if (!MainConfig.top_killer_message_enabled)
      return;

    final Arena arena = event.getArena();
    final List<UUID> players = new ArrayList<>();

    for (Player player : arena.getPlayers())
      players.add(player.getUniqueId());

    arenaPlayers.put(arena, players);
  }

  @EventHandler
  public void onGameOver(RoundEndEvent event) {
    if (!MainConfig.top_killer_message_enabled)
      return;

    final Arena arena = event.getArena();
    final Map<UUID, Integer> nameIntMap = new HashMap<>();

    // Online Players
    for (UUID player : arenaPlayers.get(arena))
      addStatsToMap(nameIntMap, player);

    // Offline Players
    for (QuitPlayerMemory memory : arena.getQuitPlayerMemories())
      addStatsToMap(nameIntMap, memory.getUniqueId());

    printMessage(arena, sortHashMapByValue(nameIntMap));
    arenaPlayers.remove(arena);
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onArenaDeleteEvent(ArenaDeleteEvent event) {
    arenaPlayers.remove(event.getArena());
  }

  // TODO this bad
  private void printMessage(Arena arena, Map<UUID, Integer> playerIntegerMap) {
    final List<Message> formattedList = new ArrayList<>();

    // There is Killers
    if (!playerIntegerMap.isEmpty()) {
      for (String line : MainConfig.top_killer_pre_lines)
        formattedList.add(Message.build(line));

      int place = 1;
      for (UUID uuid : playerIntegerMap.keySet()) {
        final String text = MainConfig.top_killer_lines.get(place);

        if (text == null)
          continue;

        final OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);

        formattedList.add(Message.build(text)
            .placeholder("killer-name", player.getName())
            .placeholder("kill-amount", playerIntegerMap.get(uuid))
        );

        place++;
      }

      for (String line : MainConfig.top_killer_sub_lines)
        formattedList.add(Message.build(line));

      // There are no killers
    } else {
      if (MainConfig.no_top_killer_message_enabled)
        return;

      for (String line : MainConfig.no_top_killer_message)
        formattedList.add(Message.build(line));
    }

    broadcast(arena, formattedList);
  }

  public LinkedHashMap<UUID, Integer> sortHashMapByValue(Map<UUID, Integer> hm) {
    return hm.entrySet()
        .stream()
        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
        .collect(Collectors.toMap(
            Map.Entry::getKey,
            Map.Entry::getValue,
            (e1, e2) -> e1,
            LinkedHashMap::new
        ));
  }

  private void addStatsToMap(Map<UUID, Integer> nameIntMap, UUID player) {
    if (player == null)
      return;

    final Optional<PlayerStats> stats = PlayerDataAPI.get().getStatsCached(player);

    if (!stats.isPresent())
      return;

    final int kills = DefaultPlayerStatSet.KILLS.getValue(stats.get().getGameStats()).intValue();

    if (kills > 0)
      nameIntMap.put(player, kills);
  }

  private void broadcast(Arena arena, List<Message> message) {
    if (message == null || (message.size() == 1 && message.get(0).done(false).isEmpty()))
      return;

    for (Message line : message)
      arena.broadcast(line);
  }
}
