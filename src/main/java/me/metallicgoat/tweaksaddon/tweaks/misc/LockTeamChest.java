package me.metallicgoat.tweaksaddon.tweaks.misc;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.ArenaStatus;
import de.marcely.bedwars.api.arena.Team;
import de.marcely.bedwars.api.message.Message;
import de.marcely.bedwars.tools.location.XYZYP;
import me.metallicgoat.tweaksaddon.config.ConfigValue;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class LockTeamChest implements Listener {

    @EventHandler
    public void onChestOpen(PlayerInteractEvent e) {
        if (!ConfigValue.lock_team_chest_enabled)
            return;

        final Player player = e.getPlayer();
        final Arena arena = BedwarsAPI.getGameAPI().getArenaByPlayer(player);
        final Block block = e.getClickedBlock();

        // Check if player is opening chest in an arena
        if (arena == null ||
                arena.getStatus() != ArenaStatus.RUNNING ||
                block == null ||
                e.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        if (block.getType() == Material.CHEST) {
            final Team playerTeam = arena.getPlayerTeam(player);
            final Team chestTeam = getChestTeam(arena, block);

            if (chestTeam != null && !arena.getPlayersInTeam(chestTeam).isEmpty() && chestTeam != playerTeam) {

                final String failOpen = Message.build(ConfigValue.lock_team_chest_fail_open)
                        .placeholder("team-name", chestTeam.getDisplayName())
                        .placeholder("team", chestTeam.getDisplayName())
                        .done();

                player.sendMessage(failOpen);
                e.setCancelled(true);
            }
        }
    }

    private static Team getChestTeam(Arena arena, Block chest) {
        if (arena.getGameWorld() == chest.getWorld()) {
            for (Team team : arena.getEnabledTeams()) {
                final XYZYP spawn = arena.getTeamSpawn(team);

                if (spawn != null) {
                    final Location bukkitSpawn = spawn.toLocation(arena.getGameWorld());

                    if (ConfigValue.lock_team_chest_range >= bukkitSpawn.distance(chest.getLocation())) {
                        return team;
                    }
                }
            }
        }
        return null;
    }
}
