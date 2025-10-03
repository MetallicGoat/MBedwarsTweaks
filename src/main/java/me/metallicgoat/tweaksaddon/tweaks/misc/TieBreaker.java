//package me.metallicgoat.tweaksaddon.tweaks.misc;
//
//import de.marcely.bedwars.api.arena.Arena;
//import de.marcely.bedwars.api.arena.Team;
//import de.marcely.bedwars.api.event.arena.ArenaDeleteEvent;
//import de.marcely.bedwars.api.event.arena.RoundStartEvent;
//import de.marcely.bedwars.api.player.DefaultPlayerStatSet;
//import de.marcely.bedwars.api.player.PlayerDataAPI;
//import de.marcely.bedwars.api.player.PlayerStats;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//import java.util.UUID;
//import me.metallicgoat.tweaksaddon.config.MainConfig;
//import org.bukkit.entity.Player;
//import org.bukkit.event.EventHandler;
//import org.bukkit.event.EventPriority;
//import org.bukkit.event.Listener;
//import org.jetbrains.annotations.Nullable;
//
//public class TieBreaker implements Listener {
//
//  private final Map<Arena, Map<Team, UUID>> arenaPlayerTeamsOnStart = new HashMap<>();
//
//  @EventHandler(priority = EventPriority.MONITOR)
//  public void onArenaStart(RoundStartEvent event) {
//    if (!MainConfig.tie_breaker_enabled)
//      return;
//
//    final Arena arena = event.getArena();
//    final Map<Team, UUID> playerTeams = new HashMap<>();
//
//    for (Team team : arena.getEnabledTeams()) {
//      for (Player player : arena.getPlayersInTeam(team)) {
//        playerTeams.put(team, player.getUniqueId());
//      }
//    }
//
//    arenaPlayerTeamsOnStart.put(arena, playerTeams);
//  }
//
//  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
//  public void onArenaDeleteEvent(ArenaDeleteEvent event) {
//    if (!MainConfig.tie_breaker_enabled)
//      return;
//
//    arenaPlayerTeamsOnStart.remove(event.getArena());
//  }
//
//
//  // TODO
//  public void teamWinDetermine(Arena event) {
//    if (!MainConfig.tie_breaker_enabled)
//      return;
//
//    // TODO if event is not tie return
//
//    for (DecidingFactor factor : MainConfig.tie_breaker_priority) {
//      final Team winningTeam = getWinningTeam(event, factor);
//
//      if (winningTeam != null) {
//        // Print Win By X message
//
//
//        // Set winning team
//        return;
//      }
//    }
//
//    // No Winning Team
//  }
//
//
//  private @Nullable Team getWinningTeam(Arena arena, DecidingFactor factor) {
//    final Map<Team, UUID> playerTeams = arenaPlayerTeamsOnStart.get(arena);
//
//    if (factor == DecidingFactor.BED_ALIVE) {
//      final List<Team> teams = new ArrayList<>();
//
//      for (Team team : playerTeams.keySet()) {
//        if (arena.isBedDestroyed(team)) {
//          teams.add(team);
//        }
//      }
//
//      // Only one team has a bed remaining
//      if (teams.size() == 1)
//        return teams.get(0);
//
//      return null;
//
//    } else if (factor == DecidingFactor.PLAYERS_ALIVE_AMOUNT) {
//      Team winningTeam = null;
//      int highestPlayers = 0;
//
//      for (Team team : arena.getEnabledTeams()) {
//        final int players = arena.getPlayersInTeam(team).size();
//
//        if (players > highestPlayers) {
//          winningTeam = team;
//          highestPlayers = players;
//
//        } else if (players == highestPlayers) {
//          winningTeam = null; // Still Tie
//        }
//      }
//
//      return winningTeam;
//
//    } else {
//      // Loop though stats and add
//      Team winningTeam = null;
//      int highestPoints = 0;
//
//      for (Team team : playerTeams.keySet()) {
//        int points = 0;
//
//        for (UUID player : playerTeams.values()) {
//          final Optional<PlayerStats> stats = PlayerDataAPI.get().getStatsCached(player);
//
//          if (!stats.isPresent())
//            continue;
//
//          // Add stats
//          switch (factor) {
//            case KILLS_AMOUNT:
//              points += DefaultPlayerStatSet.KILLS.getValue(stats.get()).intValue();
//              break;
//
//            case BEDS_DESTROYED_AMOUNT:
//              points += DefaultPlayerStatSet.BEDS_DESTROYED.getValue(stats.get()).intValue();
//              break;
//
//            default:
//              throw new RuntimeException("Unknown factor: " + factor);
//
//          }
//
//          if (points > highestPoints) {
//            winningTeam = team;
//            highestPoints = points;
//
//          } else if (points == highestPoints) {
//            winningTeam = null; // Still Tie
//          }
//        }
//      }
//
//      return winningTeam;
//    }
//  }
//
//  public enum DecidingFactor {
//    KILLS_AMOUNT,
//    BEDS_DESTROYED_AMOUNT,
//    PLAYERS_ALIVE_AMOUNT,
//    BED_ALIVE
//  }
//
//  /*
//
//    // ===== TIE BREAKER
//  @SectionTitle(title = "TIE BREAKER")
//
//  @Config(
//      description = {
//          ""
//      }
//  )
//  public static boolean tie_breaker_enabled = false;
//
//  @Config(
//      description = {
//          ""
//      }
//  )
//  public static List<TieBreaker.DecidingFactor> tie_breaker_priority = Arrays.asList();
//
//
//
//
//   */
//}
