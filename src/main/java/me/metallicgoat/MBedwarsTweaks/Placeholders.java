package me.metallicgoat.MBedwarsTweaks;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.GameAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.ArenaStatus;
import de.marcely.bedwars.api.message.Message;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.metallicgoat.MBedwarsTweaks.tweaks.spawners.GenTiers;
import me.metallicgoat.MBedwarsTweaks.utils.ServerManager;
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

        //Gen Tiers
        if(ServerManager.getConfig().getBoolean("Gen-Tiers-Enabled")) {
            if (params.equalsIgnoreCase("next-tier")) {
                Player player1 = Bukkit.getPlayer(player.getUniqueId());

                Arena arena = BedwarsAPI.getGameAPI().getArenaByPlayer(player1);
                if (arena != null) {

                    String nextTierName = GenTiers.nextTierMap.get(arena);
                    String nextTierTime = GenTiers.timeLeft(arena);

                    switch (arena.getStatus()) {
                        case LOBBY:
                            return "Game is still in Lobby";
                        case END_LOBBY:
                            return "Game Ended";
                        case STOPPED:
                            return "Game Stopped";
                        case RESETTING:
                            return "Game Resetting";
                        case RUNNING:
                            return Message.build(ServerManager.getConfig().getString("Next-Tier-Placeholder"))
                                    .placeholder("next-tier", nextTierName)
                                    .placeholder("time", nextTierTime)
                                    .done();
                    }
                }
                return "---";
            }
        }

        //Player Count of All Arenas
        if(params.equalsIgnoreCase("allplayers")) {
            return getPlayerAmount();
        }

        //Player Count of All Arenas in Lobby
        if(params.equalsIgnoreCase("players-lobby")) {
            return getPlayerAmount(ArenaStatus.LOBBY);
        }

        //Player Count of All Arenas Running
        if(params.equalsIgnoreCase("players-ingame")) {
            return getPlayerAmount(ArenaStatus.RUNNING);
        }

        //Player Count of All Arenas in EndLobby
        if(params.equalsIgnoreCase("players-endlobby")) {
            return getPlayerAmount(ArenaStatus.END_LOBBY);
        }

        return null;
    }

    private String getPlayerAmount(ArenaStatus status) {
        Integer count = 0;

        //Iterate through every arena
        for (Arena arena : GameAPI.get().getArenas()) {
            if(arena.getStatus().equals(status)) {
                count += arena.getPlayers().size();
            }
        }

        return count.toString();
    }

    private String getPlayerAmount() {
        Integer count = 0;

        //Iterate through every arena
        for (Arena arena : GameAPI.get().getArenas()) {
            count += arena.getPlayers().size();
        }

        return count.toString();
    }

    private static Main plugin(){
        return Main.getInstance();
    }
}
