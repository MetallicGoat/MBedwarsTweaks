package me.metallicgoat.tweaksaddon.tweaks.cosmetic;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.GameAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.ArenaStatus;
import de.marcely.bedwars.api.arena.Team;
import de.marcely.bedwars.api.event.arena.RoundEndEvent;
import de.marcely.bedwars.api.event.player.PlayerJoinArenaEvent;
import de.marcely.bedwars.api.event.player.PlayerQuitArenaEvent;
import de.marcely.bedwars.api.message.Message;
import de.marcely.bedwars.tools.Helper;
import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;
import me.metallicgoat.tweaksaddon.config.ConfigValue;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;


public class ActionBar implements Listener {

    private static BukkitTask actionBarTask = null;

    @EventHandler
    public void onGameStart(PlayerJoinArenaEvent e) {
        final boolean enabled = (ConfigValue.custom_action_bar_in_game || ConfigValue.custom_action_bar_in_lobby);

        if (!enabled || actionBarTask != null)
            return;

        //Start updating ActionBar
        actionBarTask = startUpdatingTime();
    }

    @EventHandler
    public void onGameStop(RoundEndEvent event) {
        final boolean enabled = (ConfigValue.custom_action_bar_in_game || ConfigValue.custom_action_bar_in_lobby);

        if (!enabled)
            return;

        //Dont kill task if players are playing
        for (Arena arena : BedwarsAPI.getGameAPI().getArenas()) {
            if (!arena.getPlayers().isEmpty()) {
                return;
            }
        }

        //Kill task
        if (actionBarTask != null) {
            actionBarTask.cancel();
            actionBarTask = null;
        }
    }

    private static BukkitTask startUpdatingTime() {

        return Bukkit.getServer().getScheduler().runTaskTimer(MBedwarsTweaksPlugin.getInstance(), () -> {
            for (Arena arena : BedwarsAPI.getGameAPI().getArenas()) {

                if ((arena.getStatus() == ArenaStatus.RUNNING && ConfigValue.custom_action_bar_in_game) ||
                        (arena.getStatus() == ArenaStatus.LOBBY && ConfigValue.custom_action_bar_in_lobby)) {

                    for (Player player : arena.getPlayers())
                        BedwarsAPI.getNMSHelper().showActionbar(player, Message.build(Helper.get().replacePAPIPlaceholders(ConfigValue.custom_action_bar_message, player)).done());

                }
            }
        }, 0L, 20L);
    }

    @EventHandler
    public void onLeave(PlayerQuitArenaEvent event){
        final Arena arena = event.getArena();
        final Player player = event.getPlayer();
        final Team team = arena.getPlayerTeam(player);

        // The arena is over so it doesn't matter
        if(team == null || arena.getStatus() != ArenaStatus.RUNNING)
            return;

        Bukkit.getScheduler().runTaskLater(MBedwarsTweaksPlugin.getInstance(), () -> {
            // Maybe they left by now
            if(!player.isOnline())
                return;

            // Get the new arena they are in
            final Arena currPlayerArena = GameAPI.get().getArenaByPlayer(player);

            // The player has rejoined, so do nothing
            if(arena == currPlayerArena)
                return;

            // The bed is not already gone, and no other players are on the team
            if(!arena.isBedDestroyed(team) && arena.getPlayersInTeam(team).size() == 0)
                arena.destroyBed(team);

        }, 20 * 60 * 3);
    }
}
