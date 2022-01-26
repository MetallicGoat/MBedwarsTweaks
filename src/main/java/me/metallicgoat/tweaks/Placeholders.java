package me.metallicgoat.tweaks;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.GameAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.ArenaStatus;
import de.marcely.bedwars.api.arena.Team;
import de.marcely.bedwars.api.message.Message;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.metallicgoat.tweaks.tweaks.spawners.GenTiers;
import me.metallicgoat.tweaks.utils.ServerManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


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
        return "1.0.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {

        final Player player1 = Bukkit.getPlayer(player.getUniqueId());
        final Arena arena = BedwarsAPI.getGameAPI().getSpectatingPlayers().contains(player1) ? BedwarsAPI.getGameAPI().getArenaBySpectator(player1):BedwarsAPI.getGameAPI().getArenaByPlayer(player1);

        switch (params.toLowerCase()){
            case "next-tier":
                //Gen Tiers With countdown
                if(ServerManager.getConfig().getBoolean("Gen-Tiers-Enabled")) {
                    if (arena != null) {

                        switch (arena.getStatus()) {
                            case LOBBY:
                                int time = (int) arena.getLobbyTimeRemaining();
                                if(time > 0){
                                    //Waiting
                                    return Message.build(ServerManager.getConfig().getString("Next-Tier-PAPI-Placeholder.Lobby-Starting")).placeholder("time", time).done();
                                }else{
                                    //Starting
                                    return Message.build(ServerManager.getConfig().getString("Next-Tier-PAPI-Placeholder.Lobby-Waiting")).done();
                                }
                            case END_LOBBY:
                                return Message.build(ServerManager.getConfig().getString("Next-Tier-PAPI-Placeholder.End-Lobby")).done();
                            case STOPPED:
                                return Message.build(ServerManager.getConfig().getString("Next-Tier-PAPI-Placeholder.Stopped")).done();
                            case RESETTING:
                                return Message.build(ServerManager.getConfig().getString("Next-Tier-PAPI-Placeholder.Resetting")).done();
                            case RUNNING:
                                String nextTierName = GenTiers.nextTierMap.get(arena);
                                String[] nextTierTime = GenTiers.timeLeft(arena);
                                String minutes = nextTierTime[0];
                                String seconds = nextTierTime[1];
                                //Old format
                                String fullTime = minutes + ":" + seconds;

                                return Message.build(ServerManager.getConfig().getString("Next-Tier-PAPI-Placeholder.Running"))
                                        .placeholder("next-tier", nextTierName)
                                        .placeholder("time", fullTime)
                                        .placeholder("min", minutes)
                                        .placeholder("sec", seconds)
                                        .done();
                        }
                    }
                }
                return "---";
            //Next tier name
            case "next-tier-name":
                if(ServerManager.getConfig().getBoolean("Gen-Tiers-Enabled")) {
                    if (arena != null && arena.getStatus() == ArenaStatus.RUNNING) {
                        String nextTierName = GenTiers.nextTierMap.get(arena);
                        return Message.build(nextTierName).done();
                    }
                }
                return "---";
            //Arena mode placeholder (eg. Solo, Duos)
            case "arena-mode":
                if (arena != null) {
                    int teamsAmount = arena.getEnabledTeams().size();
                    int playersPerTeam = arena.getPlayersPerTeam();
                    for(String modeGroup : ServerManager.getConfig().getStringList("PAPI-Mode-Placeholder")){
                        String[] splitModeGroup = modeGroup.split(":");
                        try{
                            int groupTeams = Integer.parseInt(splitModeGroup[0]);
                            int groupPlayerPerTeam = Integer.parseInt(splitModeGroup[1]);
                            if(teamsAmount == groupTeams && playersPerTeam == groupPlayerPerTeam){
                                return splitModeGroup[2];
                            }
                        }catch (NumberFormatException ignored){

                        }
                    }
                }
                return "---";

            //Amount of players currently on team
            case "player-team-current-size":
                if(arena != null){
                    Team team = arena.getPlayerTeam(player1);
                    if(team != null){
                        return Integer.toString(arena.getPlayersInTeam(team).size());
                    }
                }

            //Status of current team
            case "player-team-status":
                if(arena != null){
                    Team team = arena.getPlayerTeam(player1);
                    if(team != null){
                        if(arena.isBedDestroyed(team)){
                            return "Bed-Destroyed";
                        }else{
                            return "Active";
                        }
                    }
                }

            //Player count placeholders
            case "allplayers": return getPlayerAmount();
            case "players-ingame": return getPlayerAmount(ArenaStatus.RUNNING);
            case "players-lobby": return getPlayerAmount(ArenaStatus.LOBBY);
            case "players-endlobby": return getPlayerAmount(ArenaStatus.END_LOBBY);
        }
        //Team status placeholder, to be used on scoreboard
        if(params.toLowerCase().startsWith("team-status-")){
            String output;
            String teamName = params.replace("team-status-", "");

            if(arena != null && (arena.getStatus() == ArenaStatus.RUNNING || arena.getStatus() == ArenaStatus.END_LOBBY)){
                Team playerTeam = arena.getPlayerTeam(player1);
                Team scoreTeam = Team.getByName(teamName);

                if(scoreTeam == null)
                    return null;

                int playerAmount = arena.getPlayersInTeam(scoreTeam).size();

                if(!arena.isBedDestroyed(scoreTeam)){
                    output = ServerManager.getConfig().getString("Team-Status-Placeholder.Has-Bed");
                }else if(arena.getPlayersInTeam(scoreTeam).isEmpty()){
                    output = ServerManager.getConfig().getString("Team-Status-Placeholder.Team-Dead");
                }else{
                    output = ServerManager.getConfig().getString("Team-Status-Placeholder.No-Bed");
                }

                if(playerTeam != null && scoreTeam == playerTeam){
                    output += ServerManager.getConfig().getString("Team-Status-Placeholder.Your-Team");
                }
                return Message.build(output).placeholder("player-amount", playerAmount).done();
            }
        }
        if(params.toLowerCase().startsWith("team-you-")){
            String teamName = params.replace("team-you-", "");

            if(arena != null && (arena.getStatus() == ArenaStatus.RUNNING || arena.getStatus() == ArenaStatus.END_LOBBY)) {
                final Team playerTeam = arena.getPlayerTeam(player1);
                final Team placeholderTeam = Team.getByName(teamName);

                if(placeholderTeam != null && playerTeam == placeholderTeam){
                    return Message.build(ServerManager.getConfig().getString("Team-You-Placeholder")).done();
                }
            }
            return "";
        }
        return null;
    }

    private String getPlayerAmount(ArenaStatus status) {
        int count = 0;

        //Iterate through every arena and check arena status
        for (Arena arena : GameAPI.get().getArenas()) {
            if(arena.getStatus() == status) {
                count += (ServerManager.getConfig().getBoolean("Player-Count-Placeholder-Count-Spectators") ? arena.getPlayers().size() + arena.getSpectators().size() : arena.getPlayers().size());
            }
        }

        return Integer.toString(count);
    }

    private String getPlayerAmount() {
        int count = 0;

        //Iterate through every arena
        for (Arena arena : GameAPI.get().getArenas()) {
            count += (ServerManager.getConfig().getBoolean("Player-Count-Placeholder-Count-Spectators") ? arena.getPlayers().size() + arena.getSpectators().size() : arena.getPlayers().size());
        }

        return Integer.toString(count);
    }
}
