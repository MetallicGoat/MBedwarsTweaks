package me.metallicgoat.tweaksaddon.tweaks.cosmetic;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.ArenaStatus;
import de.marcely.bedwars.api.event.arena.RoundEndEvent;
import de.marcely.bedwars.api.event.player.PlayerJoinArenaEvent;
import de.marcely.bedwars.api.event.player.PlayerQuitArenaEvent;
import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;
import me.metallicgoat.tweaksaddon.config.ConfigValue;
import me.metallicgoat.tweaksaddon.tweaks.spawners.GenTiers;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

public class ForceScoreboardUpdating implements Listener {

    private static BukkitTask scoreboardUpdatingTask = null;

    @EventHandler
    public void onGameStart(PlayerJoinArenaEvent e) {
        final boolean enabled = (ConfigValue.gen_tiers_scoreboard_updating_enabled_in_game || ConfigValue.gen_tiers_scoreboard_updating_enabled_in_lobby);

        if (!enabled || scoreboardUpdatingTask != null)
            return;

        // Start updating scoreboard
        scoreboardUpdatingTask = startUpdatingTime();
    }

    // If someone leaves during lobby
    @EventHandler
    public void onPlayerLeave(PlayerQuitArenaEvent event) {
        checkIfUsed();
    }

    @EventHandler
    public void onGameEnd(RoundEndEvent event) {
        checkIfUsed();
    }

    public static void checkIfUsed(){
        final boolean enabled = (ConfigValue.gen_tiers_scoreboard_updating_enabled_in_game || ConfigValue.gen_tiers_scoreboard_updating_enabled_in_lobby);

        if (!enabled)
            return;

        // Dont kill task if players are playing
        for (Arena arena : BedwarsAPI.getGameAPI().getArenas()) {
            if (!arena.getPlayers().isEmpty()) {
                return;
            }
        }

        // Kill task
        if (scoreboardUpdatingTask != null) {
            scoreboardUpdatingTask.cancel();
            scoreboardUpdatingTask = null;
        }
    }

    private static BukkitTask startUpdatingTime() {
        return Bukkit.getServer().getScheduler().runTaskTimer(MBedwarsTweaksPlugin.getInstance(), () -> {
            for (Arena arena : BedwarsAPI.getGameAPI().getArenas()) {
                if ((arena.getStatus() == ArenaStatus.RUNNING && ConfigValue.gen_tiers_scoreboard_updating_enabled_in_game)) {

                    if (GenTiers.timeToNextUpdate.containsKey(arena)){
                        final long integer = GenTiers.timeToNextUpdate.get(arena);

                        if (((integer - 20) / 20) % ConfigValue.gen_tiers_scoreboard_updating_interval == 0)
                            arena.updateScoreboard();

                        continue;
                    }

                    final int integer = arena.getIngameTimeRemaining();
                    if (integer % ConfigValue.gen_tiers_scoreboard_updating_interval == 0)
                        arena.updateScoreboard();

                } else if (arena.getStatus() == ArenaStatus.LOBBY && ConfigValue.gen_tiers_scoreboard_updating_enabled_in_lobby){
                    final long integer = Math.round(arena.getLobbyTimeRemaining());

                    if (integer % ConfigValue.gen_tiers_scoreboard_updating_interval == 0)
                        arena.updateScoreboard();
                }
            }
        }, 0L, 20L);
    }
}
