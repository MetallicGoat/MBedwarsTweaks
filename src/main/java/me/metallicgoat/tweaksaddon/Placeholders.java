package me.metallicgoat.tweaksaddon;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.GameAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.ArenaStatus;
import de.marcely.bedwars.api.arena.Team;
import de.marcely.bedwars.api.arena.picker.ArenaPickerAPI;
import de.marcely.bedwars.api.exception.ArenaConditionParseException;
import de.marcely.bedwars.api.message.Message;
import java.util.Map;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.metallicgoat.tweaksaddon.config.MainConfig;
import me.metallicgoat.tweaksaddon.tweaks.spawners.GenTiers;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Placeholders extends PlaceholderExpansion {

  private final MBedwarsTweaksPlugin plugin;

  public Placeholders(MBedwarsTweaksPlugin plugin) {
    this.plugin = plugin;
  }

  @Override
  public @NotNull String getIdentifier() {
    return "tweaks";
  }

  @Override
  public @NotNull String getAuthor() {
    return "MetallicGoat";
  }

  @Override
  public @NotNull String getVersion() {
    return plugin.getDescription().getVersion();
  }

  @Override
  public String onRequest(OfflinePlayer offlinePlayer, @NotNull String params) {
    if (offlinePlayer == null)
      return "Missing player info";
    if (!offlinePlayer.isOnline())
      return "Player not online";

    final Player player = (Player) offlinePlayer;
    Arena arena = BedwarsAPI.getGameAPI().getArenaByPlayer(player);

    if (arena == null)
      arena = BedwarsAPI.getGameAPI().getArenaBySpectator(player);

    switch (params.toLowerCase()) {
      case "next-tier": {
        // GenTiers with countdown
        if (!MainConfig.gen_tiers_enabled)
          return "Gen-Tiers-Disabled";

        if (arena == null)
          return "";

        switch (arena.getStatus()) {
          case LOBBY:
            int time = (int) arena.getLobbyTimeRemaining();
            if (time > 0) {
              //Waiting
              return Message.build(MainConfig.papi_next_tier_lobby_starting).placeholder("time", time).done();
            } else {
              //Starting
              return Message.build(MainConfig.papi_next_tier_lobby_waiting).done();
            }
          case END_LOBBY:
            return Message.build(MainConfig.papi_next_tier_lobby_end_lobby).done();
          case STOPPED:
            return Message.build(MainConfig.papi_next_tier_lobby_stopped).done();
          case RESETTING:
            return Message.build(MainConfig.papi_next_tier_lobby_resetting).done();
          case RUNNING:
            final String nextTierName = GenTiers.getNextTierName(arena);
            final int totalSeconds = GenTiers.getSecondsToNextUpdate(arena);
            final int min = totalSeconds / 60;
            String sec = String.valueOf(totalSeconds - (min * 60));

            if (sec.length() == 1)
              sec = "0" + sec;

            // Old format
            final String fullTime = min + ":" + sec;

            return Message.build(MainConfig.papi_next_tier_lobby_running)
                .placeholder("next-tier", nextTierName)
                .placeholder("time", fullTime)
                .placeholder("min", min)
                .placeholder("sec", sec)
                .done();
        }

        return "";
      }
      // Next tier name
      case "next-tier-name": {
        if (!MainConfig.gen_tiers_enabled)
          return "";

        if (arena != null && arena.getStatus() == ArenaStatus.RUNNING) {
          final String nextTierName = GenTiers.getNextTierName(arena);
          return Message.build(nextTierName).done();
        }

        return "";
      }

      // Arena mode placeholder (eg. Solo, Duos)
      case "arena-mode": {
        if (arena == null)
          return "";

        for (Map.Entry<String, String> entries : MainConfig.papi_arena_mode.entrySet()) {
          try {
            if (ArenaPickerAPI.get().getArenasByCondition(entries.getKey()).contains(arena)) {
              return Message.build(entries.getValue()).done();
            }
          } catch (ArenaConditionParseException ignored) { }
        }

        return "";
      }

      // Amount of players currently on team
      case "player-team-current-size": {
        if (arena == null)
          return "";

        final Team team = arena.getPlayerTeam(player);

        if (team == null)
          return "";

        return Integer.toString(arena.getPlayersInTeam(team).size());
      }

      // Status of current team
      case "player-team-status": {
        if (arena == null)
          return "";

        final Team team = arena.getPlayerTeam(player);

        if (team == null)
          return "";

        if (arena.isBedDestroyed(team))
          return "Bed-Destroyed";
        else
          return "Active";
      }

      case "player-arena-running-time":
      case "igt": {
        if (arena == null || arena.getStatus() != ArenaStatus.RUNNING)
          return "";

        final long millis = arena.getRunningTime();
        final long minutes = (millis / 1000) / 60;
        final long seconds = ((millis / 1000) % 60);

        final String[] formatted = Util.formatMinSec((int) minutes, (int) seconds);

        return Message.build(MainConfig.papi_player_arena_running_time)
            .placeholder("min", formatted[0])
            .placeholder("sec", formatted[1])
            .done();
      }

      // Player count placeholders
      case "all-players":
        return getPlayerAmount();
      case "players-ingame":
        return getPlayerAmount(ArenaStatus.RUNNING);
      case "players-lobby":
        return getPlayerAmount(ArenaStatus.LOBBY);
      case "players-endlobby":
        return getPlayerAmount(ArenaStatus.END_LOBBY);
    }

    // TODO Build into core MBedwars {Heart} placeholder
    // Team status placeholder, to be used on scoreboard
    if (params.toLowerCase().startsWith("team-status-")) {

      if (arena != null && (arena.getStatus() == ArenaStatus.RUNNING || arena.getStatus() == ArenaStatus.END_LOBBY)) {

        String output;
        final String teamName = params.replace("team-status-", "");
        final Team playerTeam = arena.getPlayerTeam(player);
        final Team scoreTeam = Team.getByName(teamName);

        if (scoreTeam == null)
          return null;

        final int playerAmount = arena.getPlayersInTeam(scoreTeam).size();

        if (!arena.isBedDestroyed(scoreTeam) && !arena.getPlayersInTeam(scoreTeam).isEmpty())
          output = MainConfig.papi_team_status_has_bed;
        else if (arena.getPlayersInTeam(scoreTeam).isEmpty())
          output = MainConfig.papi_team_status_team_dead;
        else
          output = MainConfig.papi_team_status_no_bed;

        if (playerTeam != null && scoreTeam == playerTeam)
          output += MainConfig.papi_team_status_your_team_suffix;

        return Message.build(output).placeholder("player-amount", playerAmount).done();
      }
    }

    if (params.toLowerCase().startsWith("team-you-")) {
      final String teamName = params.replace("team-you-", "");

      if (arena != null && (arena.getStatus() == ArenaStatus.RUNNING || arena.getStatus() == ArenaStatus.END_LOBBY)) {
        final Team playerTeam = arena.getPlayerTeam(player);
        final Team placeholderTeam = Team.getByName(teamName);

        if (placeholderTeam != null && playerTeam == placeholderTeam) {
          return Message.build(MainConfig.papi_team_you_placeholder).done();
        }
      }
      return "";
    }

    return null;
  }

  private String getPlayerAmount(ArenaStatus status) {
    int count = 0;

    // Iterate through every arena and check arena status
    for (Arena arena : GameAPI.get().getArenas())
      if (arena.getStatus() == status)
        count += (MainConfig.papi_count_spectators_as_players ? arena.getPlayers().size() + arena.getSpectators().size() : arena.getPlayers().size());

    return Integer.toString(count);
  }

  private String getPlayerAmount() {
    int count = 0;

    // Iterate through every arena
    for (Arena arena : GameAPI.get().getArenas())
      count += (MainConfig.papi_count_spectators_as_players ? arena.getPlayers().size() + arena.getSpectators().size() : arena.getPlayers().size());

    return Integer.toString(count);
  }
}
