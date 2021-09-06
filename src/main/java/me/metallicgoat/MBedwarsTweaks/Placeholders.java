package me.metallicgoat.MBedwarsTweaks;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.ArenaStatus;
import de.marcely.bedwars.api.message.Message;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.metallicgoat.MBedwarsTweaks.tweaks.genupdater.GenTiers;
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
                if (arena != null && arena.getStatus() == ArenaStatus.RUNNING) {

                    String nextTierName = GenTiers.nextTierMap.get(arena);
                    String nextTierTime = GenTiers.timeLeft(arena);

                    return Message.build(ServerManager.getConfig().getString("Next-Tier-Placeholder"))
                            .placeholder("next-tier", nextTierName)
                            .placeholder("time", nextTierTime)
                            .done();

                }
                return "---";
            }
        }
        return null;
    }


    private static Main plugin(){
        return Main.getInstance();
    }
}
