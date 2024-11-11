package me.metallicgoat.tweaksaddon.tweaks.misc;

import de.marcely.bedwars.api.GameAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.ArenaStatus;
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
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.Nullable;

public class LockTeamChest implements Listener {

  @EventHandler
  public void playerOpenArenaChest(PlayerOpenArenaChestEvent event) {
    if (!MainConfig.lock_team_chest_enabled)
      return;

    if (check(event.getPlayer(), event.getArena(), event.getTeam(), event.getChestBlock()))
      event.setCancelled(true);
  }

  @EventHandler
  public void playerInteract(PlayerInteractEvent event) {
    // in case the given material was configured to not be a personal chest,
    // but shall stil be locked
    if (!MainConfig.lock_team_chest_enabled)
      return;
    if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
      return;

    final Player player = event.getPlayer();
    final Arena arena = GameAPI.get().getArenaByPlayer(player);

    if (arena == null || arena.getStatus() != ArenaStatus.RUNNING)
      return;

    final Team playerTeam = arena.getPlayerTeam(player);

    if (check(player, arena, playerTeam, event.getClickedBlock()))
      event.setCancelled(true);
  }

  private boolean check(Player player, Arena arena, Team playerTeam, Block block) {
    if (!MainConfig.lock_team_chest_materials.contains(block.getType()))
      return false;

    final Team chestTeam = getChestTeam(arena, block);

    if (chestTeam != null && !arena.getPlayersInTeam(chestTeam).isEmpty() && chestTeam != playerTeam) {
      Message.build(MainConfig.lock_team_chest_fail_open)
          .placeholder("team-name", chestTeam.getDisplayName())
          .placeholder("team", chestTeam.getDisplayName())
          .send(player);
      return true;
    }

    return false;
  }

  @Nullable
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
