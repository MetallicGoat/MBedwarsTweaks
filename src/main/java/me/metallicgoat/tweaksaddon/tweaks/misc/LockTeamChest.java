package me.metallicgoat.tweaksaddon.tweaks.misc;

import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.Team;
import de.marcely.bedwars.api.event.player.PlayerOpenArenaChestEvent;
import de.marcely.bedwars.api.message.Message;
import de.marcely.bedwars.tools.location.XYZYP;
import me.metallicgoat.tweaksaddon.config.MainConfig;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class LockTeamChest implements Listener {

  // Many people will want team chests disabled, use this with only regular chests
  @EventHandler
  public void playerOpenArenaChest(PlayerOpenArenaChestEvent event) {
    if (!MainConfig.lock_team_chest_enabled)
      return;

    final Block block = event.getChestBlock();

    if (!MainConfig.lock_team_chest_materials.contains(block.getType()))
      return;

    final Arena arena = event.getArena();
    final Player player = event.getPlayer();
    final Team playerTeam = event.getTeam();
    final Team chestTeam = getChestTeam(arena, block);

    if (chestTeam != null && !arena.getPlayersInTeam(chestTeam).isEmpty() && chestTeam != playerTeam) {
      Message.build(MainConfig.lock_team_chest_fail_open)
          .placeholder("team-name", chestTeam.getDisplayName())
          .placeholder("team", chestTeam.getDisplayName())
          .send(player);

      event.setCancelled(true);
    }
  }

  private Team getChestTeam(Arena arena, Block chest) {
    if (arena.getGameWorld() == chest.getWorld()) {
      for (Team team : arena.getEnabledTeams()) {
        final XYZYP spawn = arena.getTeamSpawn(team);

        if (spawn != null) {
          final Location bukkitSpawn = spawn.toLocation(arena.getGameWorld());

          if (MainConfig.lock_team_chest_range >= bukkitSpawn.distance(chest.getLocation())) {
            return team;
          }
        }
      }
    }

    return null;
  }
}
