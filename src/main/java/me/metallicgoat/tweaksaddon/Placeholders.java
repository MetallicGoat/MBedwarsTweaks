package me.metallicgoat.tweaksaddon;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.GameAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.ArenaStatus;
import de.marcely.bedwars.api.arena.Team;
import de.marcely.bedwars.api.exception.ArenaConditionParseException;
import de.marcely.bedwars.api.message.Message;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.metallicgoat.tweaksaddon.config.ConfigValue;
import me.metallicgoat.tweaksaddon.tweaks.gentiers.GenTiers;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class Placeholders extends PlaceholderExpansion {

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
        return "2.0.0";
    }

    @Override
    public String onRequest(OfflinePlayer offlinePlayer, @NotNull String params) {

        final Player player1 = Bukkit.getPlayer(offlinePlayer.getUniqueId());
        final Arena arena = BedwarsAPI.getGameAPI().getSpectatingPlayers().contains(player1) ? BedwarsAPI.getGameAPI().getArenaBySpectator(player1) : BedwarsAPI.getGameAPI().getArenaByPlayer(player1);


        switch (params.toLowerCase()) {
            case "next-tier": {
                // GenTiers with countdown
                if (!ConfigValue.gen_tiers_enabled)
                    return "Gen-Tiers-Disabled";

                if(arena == null)
                    return "";

                switch (arena.getStatus()) {
                    case LOBBY:
                        int time = (int) arena.getLobbyTimeRemaining();
                        if (time > 0) {
                            //Waiting
                            return Message.build(ConfigValue.papi_next_tier_lobby_starting).placeholder("time", time).done();
                        } else {
                            //Starting
                            return Message.build(ConfigValue.papi_next_tier_lobby_waiting).done();
                        }
                    case END_LOBBY:
                        return Message.build(ConfigValue.papi_next_tier_lobby_end_lobby).done();
                    case STOPPED:
                        return Message.build(ConfigValue.papi_next_tier_lobby_stopped).done();
                    case RESETTING:
                        return Message.build(ConfigValue.papi_next_tier_lobby_resetting).done();
                    case RUNNING:
                        final String nextTierName = GenTiers.nextTierMap.get(arena);
                        final String[] nextTierTime = GenTiers.timeLeft(arena);
                        final String minutes = nextTierTime[0];
                        final String seconds = nextTierTime[1];
                        //Old format
                        final String fullTime = minutes + ":" + seconds;

                        return Message.build(ConfigValue.papi_next_tier_lobby_running)
                                .placeholder("next-tier", nextTierName)
                                .placeholder("time", fullTime)
                                .placeholder("min", minutes)
                                .placeholder("sec", seconds)
                                .done();
                }


                return "";
            }
            // Next tier name
            case "next-tier-name": {
                if (!ConfigValue.gen_tiers_enabled)
                    return "";

                if (arena != null && arena.getStatus() == ArenaStatus.RUNNING) {
                    final String nextTierName = GenTiers.nextTierMap.get(arena);
                    return Message.build(nextTierName).done();
                }

                return "";
            }

            // Arena mode placeholder (eg. Solo, Duos)
            case "arena-mode": {
                if (arena == null)
                    return "";

                for (Map.Entry<String, String> entries : ConfigValue.papi_arena_mode.entrySet()) {
                    try {
                        if (GameAPI.get().getArenasByPickerCondition(entries.getKey()).contains(arena)) {
                            return Message.build(entries.getValue()).done();
                        }
                    } catch (ArenaConditionParseException ignored) {

                    }
                }

                return "";
            }

            // Amount of players currently on team
            case "player-team-current-size": {
                if (arena == null)
                    return "";

                final Team team = arena.getPlayerTeam(player1);

                if (team == null)
                    return "";

                return Integer.toString(arena.getPlayersInTeam(team).size());
            }


            // Status of current team
            case "player-team-status": {
                if (arena == null)
                    return "";

                final Team team = arena.getPlayerTeam(player1);

                if (team == null)
                    return "";

                if (arena.isBedDestroyed(team))
                    return "Bed-Destroyed";
                else
                    return "Active";

            }

            case "igt": {
                if (arena == null || arena.getStatus() != ArenaStatus.RUNNING)
                    return "";

                final long millis = arena.getRunningTime();
                long minutes = (millis / 1000)  / 60;
                long seconds = ((millis / 1000) % 60);

                final String[] formatted = Util.formatMinSec((int) minutes, (int) seconds);

                return Message.build(ConfigValue.papi_player_arena_running_time)
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
                final Team playerTeam = arena.getPlayerTeam(player1);
                final Team scoreTeam = Team.getByName(teamName);

                if (scoreTeam == null)
                    return null;

                final int playerAmount = arena.getPlayersInTeam(scoreTeam).size();

                if (!arena.isBedDestroyed(scoreTeam) && !arena.getPlayersInTeam(scoreTeam).isEmpty())
                    output = ConfigValue.papi_team_status_has_bed;
                else if (arena.getPlayersInTeam(scoreTeam).isEmpty())
                    output = ConfigValue.papi_team_status_team_dead;
                else
                    output = ConfigValue.papi_team_status_no_bed;

                if (playerTeam != null && scoreTeam == playerTeam)
                    output += ConfigValue.papi_team_status_your_team_suffix;

                return Message.build(output).placeholder("player-amount", playerAmount).done();
            }
        }

        if (params.toLowerCase().startsWith("team-you-")) {
            final String teamName = params.replace("team-you-", "");

            if (arena != null && (arena.getStatus() == ArenaStatus.RUNNING || arena.getStatus() == ArenaStatus.END_LOBBY)) {
                final Team playerTeam = arena.getPlayerTeam(player1);
                final Team placeholderTeam = Team.getByName(teamName);

                if (placeholderTeam != null && playerTeam == placeholderTeam) {
                    return Message.build(ConfigValue.papi_team_you_placeholder).done();
                }
            }
            return "";
        }
        return "";
    }

    private String getPlayerAmount(ArenaStatus status) {
        int count = 0;

        // Iterate through every arena and check arena status
        for (Arena arena : GameAPI.get().getArenas()) {
            if (arena.getStatus() == status) {
                count += (ConfigValue.papi_count_spectators_as_players ? arena.getPlayers().size() + arena.getSpectators().size() : arena.getPlayers().size());
            }
        }

        return Integer.toString(count);
    }

    private String getPlayerAmount() {
        int count = 0;

        // Iterate through every arena
        for (Arena arena : GameAPI.get().getArenas()) {
            count += (ConfigValue.papi_count_spectators_as_players ? arena.getPlayers().size() + arena.getSpectators().size() : arena.getPlayers().size());
        }

        return Integer.toString(count);
    }
}
